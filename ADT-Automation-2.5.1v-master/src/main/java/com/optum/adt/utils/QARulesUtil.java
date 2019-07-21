package com.optum.adt.utils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.optum.adt.models.ADTFlatFileModel;
import com.optum.adt.models.ADTHL7FileModel;
import com.optum.adt.models.TransactionObjectModel;

@Component
public class QARulesUtil {
	private final Logger logger = LoggerFactory.getLogger(QARulesUtil.class);
	@Autowired
	private Environment environment;

	public TransactionObjectModel checkConditionForRequired(String hl7FileField, String sdrFileField,
			ADTFlatFileModel aDTFlatFileModel, ADTHL7FileModel aDTHL7FileModel, String transaction) {

		String status = "F";
		TransactionObjectModel transactionObjectModel = new TransactionObjectModel();
		String sdrFileValue = (String) getValueByFieldNameForADTFlatFileModel(aDTFlatFileModel, sdrFileField);

		String hl7FileValue = (String) getValueByFieldNameForADTHL7FileModel(aDTHL7FileModel, hl7FileField);

		String[] medicalRecordNumberArray = null;
		String[] admitHospitalarray = null;

		if (hl7FileField.trim().equals("AdmitHospital")) {

			admitHospitalarray = hl7FileValue.split("<>");

		}

		if (sdrFileValue != null && hl7FileValue != null) {

			if (hl7FileField.trim().equals("AdmitHospital")) {

				if (admitHospitalarray != null) {

					for (int i = 0; i < admitHospitalarray.length; i++) {

						if (sdrFileValue.equals(admitHospitalarray[i].trim())) {

							hl7FileValue = admitHospitalarray[i].trim();
							break;

						}

						else {

							hl7FileValue = admitHospitalarray[0];
						}

					}
				}

			}

		}

		if (hl7FileField.trim().equals("MedicalRecordNumber")) {

			medicalRecordNumberArray = hl7FileValue.split("<>");

		}

		if (sdrFileValue != null && hl7FileValue != null) {

			if (hl7FileField.trim().equals("MedicalRecordNumber")) {

				if (medicalRecordNumberArray != null) {

					for (int i = 0; i < medicalRecordNumberArray.length; i++) {

						if (sdrFileValue.equals(medicalRecordNumberArray[i].trim())) {

							hl7FileValue = medicalRecordNumberArray[i].trim();
							break;

						}

						else {

							hl7FileValue = medicalRecordNumberArray[0];
						}

					}
				}

			}

		}

		if (sdrFileValue == null) {

			sdrFileValue = "";
		}
		if (hl7FileValue == null) {

			hl7FileValue = "";
		}

		hl7FileValue = hl7FileValue.replace("\"", "");

		if (sdrFileValue != null && hl7FileValue != null) {

			if (sdrFileValue.trim().equals(hl7FileValue.trim())) {

				status = "P";

			} else {
				logger.info("                                 ");
				logger.info(transaction + ":  Content not matched for the Field :{}", hl7FileField);
				logger.info("hl7FileValue:{}", hl7FileValue);
				logger.info("Flat File Value{} ", sdrFileValue);
				logger.info("                                 ");

			}

		}

		transactionObjectModel.setFlatFileFiledValue(sdrFileValue);
		transactionObjectModel.setHl7fieldValue(hl7FileValue);
		transactionObjectModel.setStatus(status);

		if (sdrFileField.equals("Patient_Type")) {
			if (Arrays.asList(environment.getProperty("pv1values").split("<>")).contains(sdrFileValue)) {

				transactionObjectModel.setStatus("P");
			} else {

				transactionObjectModel.setStatus("F");

			}
		}

		return transactionObjectModel;

	}

	public TransactionObjectModel checkConditionForOptional(String hl7FileField, String sdrFileField,
			ADTFlatFileModel aDTFlatFileModel, ADTHL7FileModel aDTHL7FileModel, String transaction) {
		TransactionObjectModel transactionObjectModel = new TransactionObjectModel();

		String sdrFileValue = (String) getValueByFieldNameForADTFlatFileModel(aDTFlatFileModel, sdrFileField);

		String hl7FileValue = (String) getValueByFieldNameForADTHL7FileModel(aDTHL7FileModel, hl7FileField);
		String status = "F";

		// System.out.println(sdrFileValue + "*******" + hl7FileValue);

		if (sdrFileValue == null) {

			sdrFileValue = "";
		}
		if (hl7FileValue == null) {
			hl7FileValue = "";
		}

		/*
		 * if (sdrFileValue.isEmpty() && !hl7FileValue.isEmpty() &&
		 * sdrFileField.equals("Chief_Complaint") &&
		 * hl7FileField.equals("ChiefComplaint")) {
		 * 
		 * System.out .println(hl7FileValue + "&&&&&&&&&&&&&&& " + sdrFileValue); }
		 */
		hl7FileValue = hl7FileValue.replace("\"", "");

		if (sdrFileValue.trim().isEmpty() && hl7FileValue.trim().isEmpty()) {

			status = "P";

		} else if (sdrFileValue.trim().equals(hl7FileValue.trim())) {

			status = "P";

		} else {

			status = "F";
			logger.info("                                 ");
			logger.info(transaction + ":  Content not matched for the Field :" + hl7FileField);
			logger.info("hl7FileValue:" + hl7FileValue);
			logger.info("Flat File Value " + sdrFileValue);
			logger.info("                                 ");

		}

		transactionObjectModel.setFlatFileFiledValue(sdrFileValue);
		transactionObjectModel.setHl7fieldValue(hl7FileValue);
		transactionObjectModel.setStatus(status);

		if (sdrFileField.equals("Patient_Type")) {
			if (Arrays.asList(environment.getProperty("pv1values").split("<>")).contains(sdrFileValue)) {

				transactionObjectModel.setStatus("P");
			} else {

				transactionObjectModel.setStatus("F");

			}
		}
		return transactionObjectModel;

	}

	public TransactionObjectModel noCondition(String hl7FileField, String sdrFileField,
			ADTFlatFileModel aDTFlatFileModel, ADTHL7FileModel aDTHL7FileModel) {

		TransactionObjectModel transactionObjectModel = new TransactionObjectModel();

		String sdrFileValue = (String) getValueByFieldNameForADTFlatFileModel(aDTFlatFileModel, sdrFileField);

		String hl7FileValue = (String) getValueByFieldNameForADTHL7FileModel(aDTHL7FileModel, hl7FileField);
		String status = "";

		if (sdrFileValue == null) {

			sdrFileValue = "";
		}
		if (hl7FileValue == null) {
			hl7FileValue = "";
		}
		hl7FileValue = hl7FileValue.replace("\"", "");

		if (sdrFileValue == null || !sdrFileValue.trim().isEmpty() && hl7FileValue == null
				|| !hl7FileValue.trim().isEmpty()) {

			status = "";

		} else if (sdrFileValue != null && hl7FileValue != null) {

			if (sdrFileValue.trim().equals(hl7FileValue.trim())) {

				status = "";

			}

		} else {

			System.out.println(hl7FileValue + "Failed " + sdrFileValue);
		}

		transactionObjectModel.setFlatFileFiledValue(sdrFileValue);
		transactionObjectModel.setHl7fieldValue(hl7FileValue);
		transactionObjectModel.setStatus(status);

		if (sdrFileField.equals("Patient_Type")) {
			if (Arrays.asList(environment.getProperty("pv1values").split("<>")).contains(sdrFileValue)) {

				transactionObjectModel.setStatus("P");
			} else {

				transactionObjectModel.setStatus("F");

			}
		}
		return transactionObjectModel;

	}

	public Object getValueByFieldNameForADTFlatFileModel(ADTFlatFileModel aDTFlatFileModel, String fieldName) {
		Object value = null;

		try {

			value = new PropertyDescriptor(fieldName.trim(), ADTFlatFileModel.class).getReadMethod()
					.invoke(aDTFlatFileModel);

		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}

		return value;

	}

	public Object getValueByFieldNameForADTHL7FileModel(ADTHL7FileModel aDTHL7FileModel, String fieldName) {
		Object value = null;
		try {

			value = new PropertyDescriptor(fieldName.trim(), ADTHL7FileModel.class).getReadMethod()
					.invoke(aDTHL7FileModel);

		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}

		return value;

	}

}
