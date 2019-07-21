package com.optum.adt.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.optum.adt.dao.QAAADTDao;
import com.optum.adt.models.TransactionObjectModel;
import com.optum.adt.models.TransactionResponseModel;

@Component
public class ExcelFileWriteUtil {
	@Autowired
	QAAADTDao qAAADTDao;
	@Autowired
	CheckNoOfPassedOrFailedTransactions checkNoOfPassedOrFailedTransactions;
	@Autowired
	private Environment environment;

	public void writeToExcel(String hl7filename, String flatfilename, String filepath,
			List<TransactionResponseModel> transactionResponseModelList, int hl7transactions, int flatfiletransactions,
			int total_transactions, String qAResourceName) {

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("ADT Automation Report");
		CellStyle cellStyle = workbook.createCellStyle();

		int succesfulTransactions = checkNoOfPassedOrFailedTransactions
				.checkNoOfSuccessfulTransactions(transactionResponseModelList);

		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);

		int rowCount = writeHeaderOne(hl7filename, flatfilename, sheet, hl7transactions, flatfiletransactions,
				succesfulTransactions, total_transactions, cellStyle, qAResourceName);

		rowCount = writeHeaderTwo(rowCount, sheet, workbook, cellStyle);

		writeContent(transactionResponseModelList, rowCount, sheet, workbook, cellStyle);

		writeFinalReportToOneFile(hl7filename + "_vs_" + flatfilename + "_report", filepath, workbook);

	}

	private void writeContent(List<TransactionResponseModel> transactionResponseModelList, int rowCount,
			XSSFSheet sheet, XSSFWorkbook workbook, CellStyle cellStyle) {

		for (TransactionResponseModel transactionResponseModel : transactionResponseModelList) {

			List<TransactionObjectModel> transactionObjectModelList = transactionResponseModel
					.getTransactionObjectModelList();

			int cellCount = 0;
			XSSFRow row5 = sheet.createRow(rowCount);

			XSSFCell cell = row5.createCell(cellCount);
			cell.setCellValue(transactionResponseModel.getTransaction());
			cell.setCellStyle(cellStyle);
			cellCount++;

			XSSFCell cellA = row5.createCell(cellCount);
			cellA.setCellValue(transactionResponseModel.getMessageType());
			cellA.setCellStyle(cellStyle);
			cellCount++;

			for (TransactionObjectModel transactionObjectModel : transactionObjectModelList) {

				if (transactionObjectModel != null) {
					XSSFCell cell5 = row5.createCell(cellCount);

					cell5.setCellValue(transactionObjectModel.getHl7fieldValue());
					cell5.setCellStyle(cellStyle);

					cellCount++;
				}
			}
			rowCount++;

			cellCount = 0;
			XSSFRow row6 = sheet.createRow(rowCount);

			XSSFCell cella = row6.createCell(cellCount);

			cella.setCellValue(transactionResponseModel.getTransaction());
			cella.setCellStyle(cellStyle);

			cellCount++;

			cella = row6.createCell(cellCount);

			cella.setCellValue("");
			cella.setCellStyle(cellStyle);

			cellCount++;

			for (TransactionObjectModel transactionObjectModel : transactionObjectModelList) {
				if (transactionObjectModel != null) {

					XSSFCell cell6 = row6.createCell(cellCount);
					cell6.setCellValue(transactionObjectModel.getFlatFileFiledValue());
					cell6.setCellStyle(cellStyle);

					cellCount++;
				}
			}
			rowCount++;

			cellCount = 0;
			XSSFRow row7 = sheet.createRow(rowCount);

			XSSFCell cellb = row7.createCell(cellCount);

			cellb.setCellValue(transactionResponseModel.getTransaction());
			cellb.setCellStyle(cellStyle);

			cellCount++;

			XSSFCell cellx = row7.createCell(cellCount);

			cellx.setCellValue("");
			cellx.setCellStyle(cellStyle);

			cellCount++;

			for (TransactionObjectModel transactionObjectModel : transactionObjectModelList) {
				if (transactionObjectModel != null) {

					XSSFCell cell7 = row7.createCell(cellCount);
					if (transactionObjectModel.getStatus().equals("P")) {
						cell7.setCellValue("NO");
					} else if (transactionObjectModel.getStatus() != null
							&& transactionObjectModel.getStatus().trim().equals("N/A")) {
						cell7.setCellValue("N/A");

					} else if (transactionObjectModel.getStatus().trim().isEmpty()) {
						cell7.setCellValue("NO");

					} else {
						cell7.setCellValue("YES");

					}
					cell7.setCellStyle(cellStyle);

					cellCount++;
				}
			}

			try {
				sheet.addMergedRegion(new CellRangeAddress(rowCount - 2, rowCount, 0, 0));
			} catch (Exception e) {

			}

			try {
				sheet.addMergedRegion(new CellRangeAddress(rowCount - 2, rowCount, 1, 1));
			} catch (Exception e) {

			}

			rowCount++;

		}

		rowCount++;

	}

	public int writeHeaderOne(String hl7filename, String flatfilename, XSSFSheet sheet, int hl7transactions,
			int flatfiletransactions, int succesfulTransactions, int total_transactions, CellStyle cellStyle,
			String qAResourceName) {

		int rowCount = 0;
		int cellCount = 0;

		XSSFRow row0 = sheet.createRow(rowCount);
		XSSFCell cell00 = row0.createCell(cellCount);
		cell00.setCellValue("Date and Time:");
		cellCount++;

		XSSFCell cell01 = row0.createCell(cellCount);

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String date = sdf.format(new Date());

		cell01.setCellValue(date);
		cellCount++;

		XSSFCell cell02 = row0.createCell(cellCount);
		cell02.setCellValue("Transactions count");
		cell02.setCellStyle(cellStyle);
		cellCount++;

		XSSFCell cell03 = row0.createCell(cellCount);
		cell03.setCellValue("");
		cellCount++;

		XSSFCell cell04 = row0.createCell(cellCount);
		cell04.setCellValue("Number record with Fails:");
		cellCount++;
		XSSFCell cell05 = row0.createCell(cellCount);
		cell05.setCellValue(total_transactions - succesfulTransactions);

		try {
			sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 2, 3));
		} catch (Exception e) {

		}
		// ************************************************************
		// 2nd row
		rowCount++;

		cellCount = 0;
		XSSFRow row1 = sheet.createRow(rowCount);
		XSSFCell cell10 = row1.createCell(cellCount);
		cell10.setCellValue("HL7 Raw File Name:");
		cellCount++;

		XSSFCell cell11 = row1.createCell(cellCount);
		cell11.setCellValue(hl7filename);
		cellCount++;

		XSSFCell cell12 = row1.createCell(cellCount);
		cell12.setCellValue(hl7transactions);
		cell12.setCellStyle(cellStyle);

		cellCount++;

		XSSFCell cell13 = row1.createCell(cellCount);
		cell13.setCellValue("");
		cellCount++;

		XSSFCell cell14 = row1.createCell(cellCount);
		cell14.setCellValue("Number record without fail:");
		cellCount++;
		XSSFCell cell15 = row1.createCell(cellCount);
		cell15.setCellValue(succesfulTransactions);

		try {
			sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 2, 3));
		} catch (Exception e) {

		}
		// ************************************************************
		// 3rd row
		rowCount++;
		cellCount = 0;
		XSSFRow row2 = sheet.createRow(rowCount);
		XSSFCell cell20 = row2.createCell(cellCount);
		cell20.setCellValue("SDR FF File Name:");
		cellCount++;

		XSSFCell cell21 = row2.createCell(cellCount);
		cell21.setCellValue(flatfilename);
		cellCount++;

		XSSFCell cell22 = row2.createCell(cellCount);
		cell22.setCellValue(flatfiletransactions);
		cell22.setCellStyle(cellStyle);

		cellCount++;

		XSSFCell cell23 = row2.createCell(cellCount);
		cell23.setCellValue("");
		cellCount++;

		XSSFCell cell24 = row2.createCell(cellCount);
		cell24.setCellValue("Failure Rate:");
		cellCount++;
		XSSFCell cell25 = row2.createCell(cellCount);
		cell25.setCellValue((((total_transactions - succesfulTransactions) * 100f) / total_transactions) + "%");

		try {
			sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 2, 3));
		} catch (Exception e) {

		}

		// ************************************************************
		// 4th row
		rowCount++;
		cellCount = 0;
		XSSFRow row3 = sheet.createRow(rowCount);
		XSSFCell cell30 = row3.createCell(cellCount);
		cell30.setCellValue("QA Resource Name:");
		cellCount++;

		XSSFCell cell31 = row3.createCell(cellCount);
		cell31.setCellValue(qAResourceName);
		cellCount++;

		XSSFCell cell32 = row3.createCell(cellCount);

		cell32.setCellValue("Diff:");
		cell32.setCellStyle(cellStyle);
		cellCount++;

		XSSFCell cell33 = row3.createCell(cellCount);
		cell33.setCellValue(Math.abs(flatfiletransactions - hl7transactions));
		cellCount++;

		XSSFCell cell34 = row3.createCell(cellCount);
		cell34.setCellValue("Success Rate:");
		cellCount++;
		XSSFCell cell35 = row3.createCell(cellCount);
		cell35.setCellValue(((succesfulTransactions * 100f) / total_transactions) + "%");

		return rowCount;

	}

	public static void main(String[] args) {

		ExcelFileWriteUtil efu = new ExcelFileWriteUtil();

	}

	private int writeHeaderTwo(int rowCount, XSSFSheet sheet, XSSFWorkbook workbook, CellStyle cellStyle) {

		List<Map<String, Object>> mappingValues = qAAADTDao.getADTSDRMappingsFromDB();

		int cellCount = 0;
		rowCount++;

		// set cell style
		XSSFCellStyle myStyle = workbook.createCellStyle();
		myStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		myStyle.setAlignment(HorizontalAlignment.CENTER);
		myStyle.setRotation((short) 90);

		XSSFRow row4 = sheet.createRow(rowCount);
		XSSFCell cellA = row4.createCell(cellCount);
		cellA.setCellValue("HL7 RAW Field");
		cellA.setCellStyle(cellStyle);

		cellCount++;

		XSSFCell cellAB = row4.createCell(cellCount);
		cellAB.setCellValue("MSH-9");
		cellAB.setCellStyle(myStyle);

		cellCount++;

		for (Map<String, Object> map : mappingValues) {

			cellA = row4.createCell(cellCount);
			cellA.setCellValue(map.get("HL7_File_Raw_Component") + "");
			cellA.setCellStyle(myStyle);

			// cellA.setCellStyle(cellStyle);

			cellCount++;
		}

		rowCount = rowCount + 1;
		cellCount = 0;
		XSSFRow row5 = sheet.createRow(rowCount);
		XSSFCell cellB = row5.createCell(cellCount);
		cellB.setCellValue("FF Field Name");
		cellB.setCellStyle(cellStyle);
		cellCount++;
		XSSFCell cellBB = row5.createCell(cellCount);
		cellBB.setCellValue("ADT Type");
		cellBB.setCellStyle(myStyle);
		cellCount++;
		for (Map<String, Object> map : mappingValues) {

			cellB = row5.createCell(cellCount);
			cellB.setCellValue(map.get("SDR_File_Component") + "");
			cellB.setCellStyle(myStyle);

			cellCount++;

		}
		rowCount++;

		return rowCount;
	}

	public void writeFinalReportToOneFile(String FileName, String filepath, XSSFWorkbook workbook) {
		FileOutputStream outputStream = null;
		try {
			/*
			 * outputStream = new FileOutpuStStream( environment.getProperty("filespath") +
			 * FileName + ".xlsx");
			 */

			outputStream = new FileOutputStream(filepath + FileName + ".xlsx");

			workbook.write(outputStream);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				// workbook.close();
				if (outputStream != null) {
					outputStream.flush();
					outputStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
