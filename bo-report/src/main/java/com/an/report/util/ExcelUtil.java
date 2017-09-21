package com.an.report.util;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class ExcelUtil {
	static Logger log = Logger.getLogger(ExcelUtil.class);

	/**
	 * Create Header row
	 * @param sheet
	 * @param headers
	 */
	 static void createHeader(Sheet sheet, List<String> headers) {
		Row header = sheet.createRow(0);
		for (int i = 0; i < headers.size(); i++) {
			Cell cell = header.createCell(i);
			cell.setCellValue(headers.get(i));
		}
	}

	/**
	 * Generate xls for input collections
	 * @param dtos
	 * @param sheetName
	 * @param outputPath
	 * @throws Exception
	 */
	 public static void generateXlsForCollection(Collection<?> dtos, String sheetName, String outputPath)
			throws Exception {

		if (dtos == null || dtos.isEmpty()) {
			log.warn("No records available");
			generateEmptyXls(sheetName,outputPath);
		}else{
			generateXls(dtos,sheetName,outputPath);
		}
	}

	/**
	 * Generate non-empty xls for specified collection
	 * @param dtos
	 * @param sheetName
	 * @param outputPath
	 * @throws Exception
	 */
	private static void generateXls(Collection<?> dtos, String sheetName, String outputPath) throws Exception{
		OutputStream stream = null;
		Workbook wb = new HSSFWorkbook();

		Class<?> elementClass = dtos.iterator().next().getClass();
		Field[] fields = elementClass.getDeclaredFields();
		Method[] methods = elementClass.getDeclaredMethods();
		Map<String, Method> methodMap = new HashMap<>();

		for (Method method : methods) {
			String methodName = method.getName();
			if (methodName.startsWith("get") || methodName.startsWith("is")) {
				methodMap.put(methodName.toUpperCase(), method);
			}
		}

		Map<Integer, String> headerMap = new TreeMap<>();

		for (Field field : fields) {
			ExcelAttribute excelAttribute = field.getDeclaredAnnotation(ExcelAttribute.class);
			if (excelAttribute != null) {
				if (excelAttribute.isExport()) {
					int index = excelAttribute.index();
					String headerName = excelAttribute.name();
					headerMap.put(index, headerName);
				}
			}
		}

		if (headerMap.isEmpty()) {
			throw new Exception("The element of the input collection need to apply the ExcelAttribute annotation!");
		}

		try {
			Sheet sheet = wb.createSheet(sheetName);
			List<String> headers = new ArrayList<>(headerMap.values());
            CellStyle wrapContentStyle = wb.createCellStyle();
            wrapContentStyle.setWrapText(true);

			createHeader(sheet, headers);

			int currentRowNumber = 1;
            boolean firstTime = true;
			for (Object object : dtos) {
				Row row = sheet.createRow(currentRowNumber++);
				for (Field field : fields) {

					ExcelAttribute excelAttribute = field.getDeclaredAnnotation(ExcelAttribute.class);
					if (excelAttribute != null && excelAttribute.isExport()) {
					    int columnIndex = excelAttribute.index();
                        if(firstTime){
                            sheet.setColumnWidth(columnIndex, excelAttribute.width() * 256);
                        }
						Cell cell = row.createCell(columnIndex);
						String prefix = field.getType().getName().toUpperCase().contains("BOOLEAN")?"is":"get";
						Method method = methodMap.get((prefix + field.getName()).toUpperCase());
						if (method != null) {
							String value = (String) method.invoke(object);
							cell.setCellValue(value);

							if(excelAttribute.wrapContent()){
							    cell.setCellStyle(wrapContentStyle);
                            }
						}
					}
				}

                firstTime = false;
			}

			stream = new FileOutputStream(outputPath);
			wb.write(stream);

		} catch (Exception e) {
			throw e;
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

	/**
	 * Generate Empty xls
	 * @param sheetName
	 * @param outputPath
	 * @throws Exception
	 */
	private static void generateEmptyXls(String sheetName, String outputPath) throws Exception{

		Workbook wb = new HSSFWorkbook();

		Sheet sheet  = wb.createSheet(sheetName);
	 	Row row = sheet.createRow(0);
	 	Cell cell = row.createCell(0);
	 	cell.setCellValue("No data available.");

		OutputStream stream = null;

		try{
			stream = new FileOutputStream(outputPath);
			wb.write(stream);
		}catch (Exception e){
			throw e;
		}finally {
			if (stream != null) {
				stream.close();
			}
		}

	}

}
