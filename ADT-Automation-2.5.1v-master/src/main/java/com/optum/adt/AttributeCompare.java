package com.optum.adt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.optum.adt.dao.QAAADTDao;
import com.optum.adt.models.ADTFlatFileModel;
import com.optum.adt.models.ADTHL7FileModel;
import com.optum.adt.models.TransactionObjectModel;
import com.optum.adt.models.TransactionResponseModel;
import com.optum.adt.utils.CSVFileReadUtil;
import com.optum.adt.utils.CheckNoOfPassedOrFailedTransactions;
import com.optum.adt.utils.ExcelFileWriteUtil;
import com.optum.adt.utils.HL7FileReadUtil;
import com.optum.adt.utils.QARulesUtil;

@Component
public class AttributeCompare {

	@Autowired
	CSVFileReadUtil cSVFileReadUtil;
	@Autowired
	HL7FileReadUtil hL7FileReadUtil;

	@Autowired
	ExcelFileWriteUtil excelFileWriteUtil;
	@Autowired
	CheckNoOfPassedOrFailedTransactions checkNoOfPassedOrFailedTransactions;

	@Autowired
	QAAADTDao qAAADTDao;

	@Autowired
	QARulesUtil qARulesUtil;

	private final Logger logger = LoggerFactory.getLogger(AttributeCompare.class);

	public void compareAttribute(String qAResourceName) {

		logger.info("QA Resource name: {}", qAResourceName);
		List<Map<String, Object>> filesTobeProcesseList = qAAADTDao.getFilesToBeProcessed(qAResourceName);
		logger.info("           ");
		logger.info("Files Processing started ");

		if (filesTobeProcesseList != null && !filesTobeProcesseList.isEmpty()) {

			for (Map<String, Object> filesTobeProcess : filesTobeProcesseList) {

				String flatfilepath = (String) filesTobeProcess.get("Flat_File_path");
				String hl7filepath = (String) filesTobeProcess.get("HL7_File_path");

				String hl7filename = (String) filesTobeProcess.get("HL7_file_name");
				String flatfilename = (String) filesTobeProcess.get("Flat_file_name");

				String reportpath = (String) filesTobeProcess.get("report_path");

				logger.info("HL7 File :{}", hl7filename);
				logger.info("HL7 File Path: {}", hl7filepath);
				logger.info("Flat File :{}", flatfilename);
				logger.info("Flat File Path :{}", flatfilepath);

				List<ADTFlatFileModel> aDTFlatFileModelList = cSVFileReadUtil
						.readFlatCSVFile(flatfilepath.trim() + flatfilename.trim());

				List<ADTHL7FileModel> aDTHL7FileModelList = hL7FileReadUtil
						.readHL7FileasList(hl7filepath.trim() + hl7filename.trim());

				int hl7transactions = 0;
				int flatfiletransactions = 0;

				if (aDTHL7FileModelList != null && !aDTHL7FileModelList.isEmpty()) {
					hl7transactions = aDTHL7FileModelList.size();

					if (aDTFlatFileModelList != null && !aDTFlatFileModelList.isEmpty()) {

						flatfiletransactions = aDTFlatFileModelList.size();
						List<Map<String, Object>> mappingValues = qAAADTDao.getADTSDRMappingsFromDB();

						int i = 0;

						List<TransactionResponseModel> transactionResponseModelList = new ArrayList<>();
						for (ADTHL7FileModel aDTHL7FileModel : aDTHL7FileModelList) {

							ADTFlatFileModel aDTFlatFileModel = null;

							if (aDTFlatFileModelList.size() > i) {
								aDTFlatFileModel = aDTFlatFileModelList.get(i);
							}

							if (aDTFlatFileModel != null) {

								i += 1;
								TransactionResponseModel transactionResponseModel = new TransactionResponseModel();

								transactionResponseModel.setTransaction("Transaction " + i);
								transactionResponseModel.setMessageType(aDTHL7FileModel.getMessageType());

								List<TransactionObjectModel> transactionObjectModelList = new ArrayList<>();

								if (mappingValues != null && !mappingValues.isEmpty()) {
									for (Map<String, Object> mapping : mappingValues) {

										String hl7FileField = (String) mapping.get("HL7_File_Component");
										String sdrFileField = (String) mapping.get("SDR_File_Component");

										String event = aDTHL7FileModel.getMessageType().split("\\^")[1];

										String eventValue = (String) mapping.get(event);

										TransactionObjectModel transactionObjectModel = null;

										if ("R".equals(eventValue)) {

											transactionObjectModel = qARulesUtil.checkConditionForRequired(hl7FileField,
													sdrFileField, aDTFlatFileModel, aDTHL7FileModel,
													transactionResponseModel.getTransaction());
										} else if ("O".equals(eventValue)) {

											transactionObjectModel = qARulesUtil.checkConditionForOptional(hl7FileField,
													sdrFileField, aDTFlatFileModel, aDTHL7FileModel,
													transactionResponseModel.getTransaction());
										}

										else if ("".equals(eventValue.trim())) {

											transactionObjectModel = qARulesUtil.noCondition(hl7FileField, sdrFileField,
													aDTFlatFileModel, aDTHL7FileModel);

										} else if ("N/A".equals(eventValue)) {

											transactionObjectModel = new TransactionObjectModel();

											transactionObjectModel.setFlatFileFiledValue("N/A");
											transactionObjectModel.setHl7fieldValue("N/A");
											transactionObjectModel.setStatus("N/A");
										}

										transactionObjectModelList.add(transactionObjectModel);

									}

								} else {

									logger.info("No mapping values found from Database");

								}

								transactionResponseModel.setTransactionObjectModelList(transactionObjectModelList);

								transactionResponseModelList.add(transactionResponseModel);

							} else {

								logger.info("No mapping data found in SDT file ");
							}

						}

						int successfulTransactions = checkNoOfPassedOrFailedTransactions
								.checkNoOfSuccessfulTransactions(transactionResponseModelList);

						int total_transactions = Math.max(hl7transactions, flatfiletransactions);

						qAAADTDao.updateProcessConfig(hl7transactions, flatfiletransactions, successfulTransactions,
								Math.abs(hl7transactions - flatfiletransactions),
								(total_transactions - successfulTransactions),
								Integer.parseInt(filesTobeProcess.get("FileId") + ""));

						excelFileWriteUtil.writeToExcel(hl7filename, flatfilename, reportpath,
								transactionResponseModelList, hl7transactions, flatfiletransactions, total_transactions,
								qAResourceName);
					}

					else {
						logger.info("No data found in Flat(SDT) Files");
					}

				} else {

					logger.info("No data found in HL7(ADT) Files");
				}
			}

			logger.info("*************Files Process Completed************");
		}

		else {

			logger.info("No files found for processing");
		}
	}

}
