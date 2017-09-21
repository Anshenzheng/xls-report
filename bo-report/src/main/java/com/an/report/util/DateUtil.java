package com.an.report.util;

import com.mysql.jdbc.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DateUtil {

    /**
     * Generate YYYY-MM-DD format dates between given date range.
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<String>  generateDatesWithinRange(String startDate, String endDate){

        List<String> dates = new ArrayList<>();

        if(StringUtils.isNullOrEmpty(startDate)){
            dates.add(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }else{
            LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate endLocalDate;
            if(!StringUtils.isNullOrEmpty(endDate)){
                endLocalDate = LocalDate.parse(endDate,DateTimeFormatter.ISO_LOCAL_DATE);
            }else{
                endLocalDate = LocalDate.now();
            }

            while (startLocalDate.isBefore(endLocalDate) || startLocalDate.isEqual(endLocalDate)){
                dates.add(startLocalDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
                startLocalDate = startLocalDate.plusDays(1);
            }
        }

        return dates;
    }


}
