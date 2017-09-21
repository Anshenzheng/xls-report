package com.an.report.action;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.an.report.dao.ReportDao;
import com.an.report.pojo.*;
import com.an.report.util.DateUtil;
import com.an.report.util.DbUtil;
import com.an.report.util.ExcelUtil;
import com.mysql.jdbc.StringUtils;

import org.apache.log4j.Logger;

/**
 * @author aan
 */
public class MainApplication {
    private static Logger log = Logger.getLogger(MainApplication.class);
    private static String BO_REPORT_PATH;
    private static ReportDao dao = new ReportDao(DbUtil.ENV.PROD);

    public static void main(String[] args) {
//        if (args == null || args.length == 0) {
//            args = new String[]{"path=/Users/aan/Documents","start=2017-09-06","end=2017-09-06"};
//        }
        if (args == null || args.length == 0) {
            log.error("Not output path specified for Bo-Report file!");
        } else {
            Map<String,String> parameters = new HashMap<>();
            for(String arg:args){
                if(arg != null){
                    String[] kvParam= arg.split("=");
                    if(kvParam != null && kvParam.length >1){
                        parameters.put(kvParam[0].toUpperCase(),kvParam[1]);
                    }
                }
            }
            // Set the report output path
            String reportPath = parameters.get("PATH");
            if(StringUtils.isNullOrEmpty(reportPath)){
                log.error("Not output path specified for Bo-Report file!");
            }else{
                log.info("Specified report path is: " + reportPath);
                BO_REPORT_PATH = reportPath;

                // Get the report Date
                String startDate = parameters.get("START");
                String endDate = parameters.get("END");

                try{
                    List<String> dates = DateUtil.generateDatesWithinRange(startDate,endDate);
                    for(String currentDate:dates){
                        log.info("report date: " + currentDate);

                        // Generate the fast-pass count report
                        generateFastPassCountsReport(currentDate);

                        // Generate the fast-pass report
                        generateFastpassReport(currentDate);
                    }
                }catch (Exception e){
                    log.error(e.toString(),e);
                }finally {
                    DbUtil.closeConnections();
                }

            }

        }

    }


    /**
     * Generate report that reflects the total fast passes counts
     * @param currentDate
     */
    private static void generateFastPassCountsReport(String currentDate) {

        log.info("Start generating Fastpass counts Report...");

        Map<String, BoFastpassCountsFormattedDTO> typeMap = new HashMap<>();

        try {

            List<BOFastPassCountsDTO> dtos = dao.fetchBoFastPassCountsReport(currentDate);

            dtos.forEach(dto -> {
                BoFastpassCountsFormattedDTO fdto;
                String type = dto.getType();
                if (typeMap.containsKey(type)) {
                    fdto = typeMap.get(type);
                } else {
                    fdto = new BoFastpassCountsFormattedDTO();
                }

                fdto.setType(type);

                if ("BKD".equalsIgnoreCase(dto.getStatus())) {
                    fdto.setBkdCount(dto.getCount());
                } else if ("CAN".equalsIgnoreCase(dto.getStatus())) {
                    fdto.setCanCount(dto.getCount());
                } else if ("EXP".equalsIgnoreCase(dto.getStatus())) {
                    fdto.setExpCount(dto.getCount());
                } else if ("INQ".equalsIgnoreCase(dto.getStatus())) {
                    fdto.setInqCount(dto.getCount());
                } else if ("RED".equalsIgnoreCase(dto.getStatus())) {
                    fdto.setRedCount(dto.getCount());
                } else {
                    log.error("UnHandledStatusCode:" + dto.getStatus());
                }
                typeMap.put(type, fdto);
            });


            String fileName = BO_REPORT_PATH + "/BoReport_Fastpass_Counts_" + currentDate + ".xls";

            ExcelUtil.generateXlsForCollection(typeMap.values(), "BoReport_Fastpass_Counts", fileName);

            log.info("BoReport of fastpass counts Successfully generated to " + fileName);

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }


    /**
     * Generate the Fastpass Report
     * @param currentDate
     */
    private static void generateFastpassReport(String currentDate) {

        log.info("Start generating Fastpass Report...");

        try {
            List<FastpassReportDTO> dtos = dao.fetchFastPassReport(currentDate);
            if (dtos != null && !dtos.isEmpty()) {
                List<Long> guestIDs = dtos.stream()
                        .filter(fastpassReportDTO -> !StringUtils.isNullOrEmpty(fastpassReportDTO.getGuestID()))
                        .flatMap(dto-> Stream.of(Long.parseLong(dto.getGuestID().trim())))
                        .collect(Collectors.toList());
                Long minGuestID = guestIDs.stream().min(Comparator.comparing(Long::longValue)).get();
                Long maxGuestID = guestIDs.stream().max(Comparator.comparing(Long::longValue)).get();
                log.info("Fetching Ticket VID by guest IDs between " + minGuestID + " and " + maxGuestID);

                final Map<String, String> mappedLnkIDs = dao.getMappedLinkIDs(minGuestID,maxGuestID);
                final Map<String, String> castNotes = dao.getCastNotes(currentDate);
                dtos.forEach(dto -> {
                    String vid = mappedLnkIDs.get(dto.getGuestID());
                    dto.setPartTicketVID(vid);
                    dto.setCastNotes(castNotes.get(vid));
                });
            }

            String fileName = BO_REPORT_PATH + "/Fastpass_Report_" + currentDate + ".xls";

            ExcelUtil.generateXlsForCollection(dtos, "Fastpass Report", fileName);
            log.info("Fastpass Report Successfully generated to " + fileName);

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

}
