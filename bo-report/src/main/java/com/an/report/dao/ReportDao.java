package com.an.report.dao;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.an.report.pojo.BOFastPassCountsDTO;
import com.an.report.pojo.FastpassReportDTO;
import com.an.report.util.DBColumn;
import com.an.report.util.DbUtil;

import org.apache.log4j.Logger;

public class ReportDao {

    Logger log = Logger.getLogger(this.getClass());
	DbUtil dbUtil;

	public ReportDao(DbUtil.ENV env){
        dbUtil = new DbUtil(env);
    }

    /**
     * SQL for querying fast-pass counts based on FP type & status
     */
	private final String FASTPASS_COUNTS_REPORTS_SQL = "select " + 
			"	'DPA' as 'Type', " + 
			"    	xe.XPASS_ENTTL_CURR_STS_CD as 'Status', " + 
			"    count(1) as 'Count' " + 
			"from " + 
			"	xpass_enttl xe," + 
			"	xpass_enttl_actvy xea, " + 
			"	xpass_enttl_actvy_rsn xear " + 
			"where Cast(xe.CREATE_DTS as DATE) = ? and " +
			"	xear.XPASS_ENTTL_RSN_CD in " + 
			"	(select distinct(REASON_CODE) from shdr_product_mapping where LOGICAL_DEL_IN = 'N') and " + 
			"    xe.XPASS_ENTTL_ID = xea.XPASS_ENTTL_ID and " + 
			"    xea.XPASS_ENTTL_ACTVY_RSN_ID = xear.XPASS_ENTTL_ACTVY_RSN_ID and " + 
			"	xe.LOGICAL_DEL_IN = 'N' and " + 
			"    xea.LOGICAL_DEL_IN = 'N' and " + 
			"    xear.LOGICAL_DEL_IN = 'N' " + 
			"group by XPASS_ENTTL_TYP_CD,XPASS_ENTTL_CURR_STS_CD " + 
			"union " + 
			"select  " + 
			" Type,Status,count(1) from (select " +
			"	xe.XPASS_ENTTL_CURR_STS_CD as 'Status', " + 
			"    case " +
			"		when xear.XPASS_ENTTL_RSN_CD = 'MDP' then 'MDP' " + 
			"        else xe.XPASS_ENTTL_TYP_CD " + 
			"        end as 'Type' " + 
			"from " + 
			"	xpass_enttl xe, " + 
			"	xpass_enttl_actvy xea, " + 
			"	xpass_enttl_actvy_rsn xear " + 
			"where Cast(xe.CREATE_DTS as DATE) = ? and " + 
			"    xe.XPASS_ENTTL_ID = xea.XPASS_ENTTL_ID and" + 
			"    xea.XPASS_ENTTL_ACTVY_RSN_ID = xear.XPASS_ENTTL_ACTVY_RSN_ID and " + 
			"	xe.LOGICAL_DEL_IN = 'N' and " + 
			"    xea.LOGICAL_DEL_IN = 'N' and " + 
			"    xear.LOGICAL_DEL_IN = 'N' " + 
			"    ) as temp " + 
			"group by Type,Status";

    /**
     * SQL for generating fast pass details based on given date
     */
	private final String FASTPASS_REPORT_SQL = "select DISTINCT" +
			" temp.GXP_LNK_ID as 'Guest ID', " +
			" temp.ENTRTN_ID as 'Attraction ID'," +
			" temp.XPASS_ENTTL_CURR_STS_CD  as 'DFP Status Code'," +
			" shr.PRODUCT_CODE as 'DFP PLU'," +
			" case" +
			"   when temp.XPASS_ENTTL_RSN_CD in (select distinct(REASON_CODE) from shdr_product_mapping where LOGICAL_DEL_IN = 'N') then 'DPA' " +
			"   when temp.XPASS_ENTTL_RSN_CD = 'MDP' then 'MDP' " +
			"        else temp.XPASS_ENTTL_TYP_CD " +
			"  end as 'Fastpass Type'," +
            " temp.XPASS_ENTTL_RSN_CD as 'Reason Code'," +
			" shr.bundle as 'Bundle'," +
			" date_format(temp.CREATE_DTS , '%Y-%m-%d %H:%i:%s')  as 'DFP Issue Date', " +
            " temp.XPASS_ENTTL_ID as 'Fasspass ID'," +
            "  temp.CREATE_USR_ID as 'Issued By'," +
            "  CASE " +
            "    when temp.XPASS_ENTTL_CURR_STS_CD = 'RED' then date_format( temp.UPDT_DTS , '%Y-%m-%d %H:%i:%s') " +
            "    else '' " +
            "    end " +
            "    as 'Use Time' " +
			" from " +
			" (select distinct " +
			" xe.GXP_LNK_ID," +
			" ge.ENTRTN_ID," +
			" xe.XPASS_ENTTL_CURR_STS_CD," +
			" xear.XPASS_ENTTL_RSN_CD," +
			" xe.XPASS_ENTTL_TYP_CD," +
			" xe.CREATE_DTS, " +
            " xe.XPASS_ENTTL_ID," +
            " xe.CREATE_USR_ID," +
            " xe.UPDT_DTS"+
			"  from " +
			"   xpass_enttl xe, " +
			"   xpass_enttl_entrtn_loc xeel, " +
			"   gxp_entrtn ge, " +
			"   xpass_enttl_actvy xea, " +
			"   xpass_enttl_actvy_rsn xear " +
			" where " +
			"   cast(xe.CREATE_DTS as DATE) = ? and " +
			"   xe.XPASS_ENTTL_ID = xeel.XPASS_ENTTL_ID and " +
			"   ge.GXP_ENTRTN_ID = xeel.GXP_ENTRTN_ID and " +
			"   xe.XPASS_ENTTL_ID = xea.XPASS_ENTTL_ID and " +
			"   xea.XPASS_ENTTL_ACTVY_RSN_ID = xear.XPASS_ENTTL_ACTVY_RSN_ID and " +
			"   xe.LOGICAL_DEL_IN = 'N' and " +
			"   xea.LOGICAL_DEL_IN = 'N' and " +
			"   xear.LOGICAL_DEL_IN = 'N' and " +
			"   ge.LOGICAL_DEL_IN = 'N' ) as temp " +
			" left  join " +
			"  (select distinct " +
			"   PRODUCT_CODE, " +
			"        REASON_CODE, " +
			"   case " +
			"     when reason_code='BND' then 'YES' " +
			"     else 'NO' " +
			"     end as 'bundle' " +
			" from shdr_product_mapping " +
			"    where " +
			"     LOGICAL_DEL_IN = 'N') as shr " +
			" on temp.XPASS_ENTTL_RSN_CD = shr.REASON_CODE";


    /**
     * SQL for querying Cast notes & Ticket VID
     */
	private final String CASTNOTE_VID_AND_CONTENT_SQL =
            "select " +
            "    gl.value as 'VID'," +
            "    nt.content as 'Content' " +
            "from " +
            "  guest_locator gl," +
            "  note nt," +
            "  note_guest_association nga " +
            "where " +
            " cast(nt.CREATE_DATE as DATE) = ? and " +
            " gl.TYPE = 'ticket-visual-id' and " +
            " gl.GUEST_LOCATOR_ID = nga.GUEST_LOCATOR_ID and " +
            " nt.NOTE_ID = nga.NOTE_ID";


	/**
	 * Fetch Fasspass counts Report
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public List<BOFastPassCountsDTO> fetchBoFastPassCountsReport(String date) throws Exception{

        Map<Integer,Object> parameters = new HashMap<>();
        parameters.put(1,date);
        parameters.put(2,date);

        List<Object> objects = executeQuery(FASTPASS_COUNTS_REPORTS_SQL, BOFastPassCountsDTO.class,parameters);

        return objects.stream().flatMap(object-> Stream.of((BOFastPassCountsDTO)object)).collect(Collectors.toList());
	}

	/**
	 * Fetch Fastpass report
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public List<FastpassReportDTO> fetchFastPassReport(String date) throws Exception{

	    Map<Integer,Object> parameters = new HashMap<>();
	    parameters.put(1,date);
	    List<Object> objects = executeQuery(FASTPASS_REPORT_SQL, FastpassReportDTO.class,parameters);

	    return objects.stream().flatMap(object-> Stream.of((FastpassReportDTO)object)).collect(Collectors.toList());
	}

    /**
     * Execute SQL Query
     * @param sql
     * @param objectClass
     * @param parameters
     * @return
     * @throws Exception
     */
    private List<Object> executeQuery(String sql, Class objectClass, Map<Integer, Object> parameters) throws Exception{
        Connection conn;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        List<Object> objects = new ArrayList<>();
        try {
            conn = dbUtil.getFastPassConnection();
            psmt = conn.prepareStatement(sql);
            log.info("***************************************** Start Executing SQL ************************************");
            log.info(sql);

            if(parameters != null && !parameters.isEmpty()){
                Set<Map.Entry<Integer,Object>> entries = parameters.entrySet();
                for(Map.Entry<Integer,Object> entry:entries){
                    psmt.setObject(entry.getKey(),entry.getValue());
                }
            }

            rs = psmt.executeQuery();
            log.info("***************************************** End Executing SQL **************************************");

            Field[] fields = objectClass.getDeclaredFields();
            Method[] methods = objectClass.getDeclaredMethods();
            Map<String,Method> methodMap = new HashMap<>();
            for(Method method : methods){
                methodMap.put(method.getName().toUpperCase(),method);
            }
            Map<String,Method> feedValueMethods = new HashMap<>();

            List<String> columns = new ArrayList<>();
            for(Field field:fields){
                DBColumn dbColumn = field.getAnnotation(DBColumn.class);
                if(dbColumn != null){
                    String columnName = dbColumn.name();
                    columns.add(columnName);
                    String methodName = "set" + field.getName();
                    feedValueMethods.put(columnName,methodMap.get(methodName.toUpperCase()));
                }

            }

            if(columns.isEmpty()){
                throw new Exception("Class need to apply the DBColumn annotation!");
            }

            while(rs.next()){

                Object object = objectClass.newInstance();
                for(String column:columns){
                    Method method = feedValueMethods.get(column);
                    String value = rs.getString(column);
                    method.invoke(object,value);
                }

                objects.add(object);
            }
        }catch(Exception e) {
            log.error(e.toString(),e);
        }finally {
            if(rs != null) {
                rs.close();
            }
            if(psmt != null) {
                psmt.close();
            }
        }

        return objects;
    }

    /**
     * Fetch mapped link IDs
     * @param minGuestID
     * @param maxGuestID
     * @return
     * @throws Exception
     */
    public Map<String, String> getMappedLinkIDs(Long minGuestID, Long maxGuestID) throws Exception{
        Connection conn;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        Map<String,String> mappedLinkIDs = new HashMap<>();
        String sql = "select GXP_LNK_ID,LINK_ID from gxp_lnk_assc where GXP_LNK_ID >= ? and GXP_LNK_ID <= ?";

        try {
            conn = dbUtil.getLinkMdConnection();
            psmt = conn.prepareStatement(sql);
            psmt.setLong(1,minGuestID);
            psmt.setLong(2,maxGuestID);
            rs = psmt.executeQuery();
            while(rs.next()){
                String gxpLnkID = rs.getString(1);
                String linkID = rs.getString(2);

                mappedLinkIDs.put(gxpLnkID, linkID);
            }
        }catch(Exception e) {
            log.error(e.toString(),e);
        }finally {
            if(rs != null) {
                rs.close();
            }
            if(psmt != null) {
                psmt.close();
            }
        }

        return mappedLinkIDs;
    }

    /**
     * Fetch Cast Notes
     * @return
     * @throws Exception
     */
    public Map<String, String> getCastNotes(String currentDate) throws Exception{
        Connection conn;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        Map<String,String> castNotesMap = new HashMap<>();
        try {
            conn = dbUtil.getCastNoteConnection();
            psmt = conn.prepareStatement(CASTNOTE_VID_AND_CONTENT_SQL);
            psmt.setString(1,currentDate);
            rs = psmt.executeQuery();
            while(rs.next()){
                String vid = rs.getString(1);
                String content = rs.getString(2);


                if(castNotesMap.containsKey(vid)){
                    String existContent = castNotesMap.get(vid);
                    content = existContent + "\r\n" + content;
                }

                castNotesMap.put(vid,content);
            }
        }catch(Exception e) {
            log.error(e.toString(),e);
        }finally {
            if(rs != null) {
                rs.close();
            }
            if(psmt != null) {
                psmt.close();
            }
        }

        return castNotesMap;
    }
}
