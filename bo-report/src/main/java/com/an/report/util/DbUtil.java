package com.an.report.util;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbUtil {
	private  static String FASTPASS_DB_URL;
	private  static String FASTPASS_DB_USER_NAME;
	private  static String FASTPASS_DB_PASSWD;

	private  static String LINKMD_DB_URL;
	private  static String LINKMD_DB_USER_NAME;
	private  static String LINKMD_DB_PASSWD;

	private  static String CASTNOTE_DB_URL;
	private  static String CASTNOTE_DB_USER_NAME;
	private  static String CASTNOTE_DB_PASSWD;

	private final static String LATEST_DB_CONFIGURATION = "db-latest.properties";
	private final static String STAGE_DB_CONFIGURATION = "db-stage.properties";
	private final static String PROD_DB_CONFIGURATION = "db-prod.properties";

	private static Logger log = Logger.getLogger(DbUtil.class);

	public static Connection FAST_PASS_CONNECTION;
	public static Connection LINK_MD_CONNECTION;
	public static Connection CAST_NOTE_CONNECTION;

	public enum ENV{
		LATEST,STAGE,PROD
	}

	public DbUtil(ENV env) {
		InputStream input = null;
		Properties prop = new Properties();
		String dbConfiguration;
		try {
			if(ENV.PROD.equals(env)){
				dbConfiguration = PROD_DB_CONFIGURATION;
			}else if(ENV.STAGE.equals(env)){
				dbConfiguration = STAGE_DB_CONFIGURATION;
			}else{
				dbConfiguration = LATEST_DB_CONFIGURATION;
			}
			log.info("db file: " + dbConfiguration);
			ClassLoader classLoader = DbUtil.class.getClassLoader();
			input = classLoader.getResourceAsStream(dbConfiguration);
			prop.load(input);

			FASTPASS_DB_URL = prop.getProperty("fastpass.db.url");
			FASTPASS_DB_USER_NAME = prop.getProperty("fastpass.db.username");
			FASTPASS_DB_PASSWD = prop.getProperty("fastpass.db.password");
			
			LINKMD_DB_URL = prop.getProperty("linkmd.db.url");
			LINKMD_DB_USER_NAME = prop.getProperty("linkmd.db.username");
			LINKMD_DB_PASSWD = prop.getProperty("linkmd.db.password");

			CASTNOTE_DB_URL = prop.getProperty("castnote.db.url");
			CASTNOTE_DB_USER_NAME = prop.getProperty("castnote.db.username");
			CASTNOTE_DB_PASSWD = prop.getProperty("castnote.db.password");
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally {
			if(input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public  Connection getFastPassConnection() throws Exception {
		if(FAST_PASS_CONNECTION == null){
			FAST_PASS_CONNECTION = DriverManager.getConnection(FASTPASS_DB_URL, FASTPASS_DB_USER_NAME, FASTPASS_DB_PASSWD);
		}

		return FAST_PASS_CONNECTION;
	}

	public  Connection getLinkMdConnection() throws Exception {
		if(LINK_MD_CONNECTION == null){
			LINK_MD_CONNECTION = DriverManager.getConnection(LINKMD_DB_URL, LINKMD_DB_USER_NAME, LINKMD_DB_PASSWD);
		}

		return LINK_MD_CONNECTION;
	}

	public  Connection getCastNoteConnection() throws Exception {

		if(CAST_NOTE_CONNECTION == null){
			CAST_NOTE_CONNECTION = DriverManager.getConnection(CASTNOTE_DB_URL, CASTNOTE_DB_USER_NAME, CASTNOTE_DB_PASSWD);
		}

		return CAST_NOTE_CONNECTION;
	}

	/**
	 * Close all the DB connections
	 */
	public static void closeConnections(){
		if(FAST_PASS_CONNECTION != null){
			try {
				FAST_PASS_CONNECTION.close();
				log.info("Fastpass DB connection closed successfully.");
			} catch (SQLException e) {
				log.error("Fastpass DB connection close failed with:" + e.getMessage(),e);
			}
		}

		if(LINK_MD_CONNECTION != null){
			try {
				LINK_MD_CONNECTION.close();
				log.info("LinkMD DB connection closed successfully.");
			} catch (SQLException e) {
				log.error("LinkMD DB connection close failed with:" + e.getMessage(),e);
			}
		}

		if(CAST_NOTE_CONNECTION != null){
			try {
				CAST_NOTE_CONNECTION.close();
				log.info("Castnote DB connection closed successfully.");
			} catch (SQLException e) {
				log.error("Castnote DB connection close failed with:" + e.getMessage(),e);
			}
		}
	}
}
