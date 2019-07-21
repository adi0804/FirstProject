package com.optum.adt.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.optum.adt.models.ADTFlatFileModel;

@Component
public class CSVFileReadUtil {

	@Autowired
	private Environment environment;

	

	private static final Logger logger = LoggerFactory.getLogger(CSVFileReadUtil.class);
	DateConversionUtil dateConversionUtil = new DateConversionUtil();;

	public List<ADTFlatFileModel> readFlatCSVFile(String filepath) {
		List<ADTFlatFileModel> aDTFlatFileModelList = new ArrayList<>();
		try {
			// Create an object of file reader class with CSV file as a
			// parameter.
			FileReader filereader = new FileReader(filepath);

			// create csvParser object with
			// custom seperator semi-colon
			CSVParser parser = new CSVParserBuilder().withSeparator('|').build();

			// create csvReader object with parameter
			// filereader and parser
			CSVReader csvReader = new CSVReaderBuilder(filereader).withCSVParser(parser).build();

			// Read all data at once
			csvReader.readNext();
			List<String[]> allData = csvReader.readAll();

			// Print Data.

			for (String[] row : allData) {
				ADTFlatFileModel aDTFlatFileModel = new ADTFlatFileModel();

				if (row[0] != null) {
					aDTFlatFileModel.setAccount_Number(row[0].trim());
				}
				if (row[1] != null) {
					aDTFlatFileModel.setMedical_Record_Number(row[1].trim());
				}
				if (row[2] != null) {
					aDTFlatFileModel.setPatient_Last_Name(row[2].trim());
				}
				if (row[3] != null) {
					aDTFlatFileModel.setPatient_First_Name(row[3].trim());
				}
				if (row[4] != null) {
					aDTFlatFileModel.setPatient_Middle_Name(row[4].trim());
				}
				if (row[5] != null) {
					aDTFlatFileModel.setPatient_Address_1(row[5].trim());
				}
				if (row[6] != null) {
					aDTFlatFileModel.setPatient_Address_2(row[6].trim());
				}
				if (row[7] != null) {
					aDTFlatFileModel.setCity(row[7].trim());
				}
				if (row[8] != null) {
					aDTFlatFileModel.setState(row[8].trim());
				}
				if (row[9] != null) {
					aDTFlatFileModel.setZip(row[9].trim());
				}
				if (row[10] != null) {
					aDTFlatFileModel.setPhone(row[10].trim());
				}
				if (row[11] != null) {
					aDTFlatFileModel.setDOB(row[11].trim());
				}
				if (row[12] != null) {

					if (!row[12].trim().isEmpty()) {

						aDTFlatFileModel.setGender(row[12].trim().split("\\^")[0]);

					} else {

						aDTFlatFileModel.setGender("");

					}
				}

				if (row[13] != null) {
					aDTFlatFileModel.setAdmit_Date(row[13].trim());
				}

				String date = null;
				if (aDTFlatFileModel.getAdmit_Date() != null && !aDTFlatFileModel.getAdmit_Date().trim().isEmpty()) {

					date = row[13];
					aDTFlatFileModel.setAdmit_Date(dateConversionUtil.getDate(aDTFlatFileModel.getAdmit_Date()));

				}

				if (row[14] != null && !row[14].isEmpty()) {

					if (aDTFlatFileModel.getAdmit_Date() != null && !aDTFlatFileModel.getAdmit_Date().isEmpty()) {

						aDTFlatFileModel.setAdmit_Time(row[14].trim());
						if (aDTFlatFileModel.getAdmit_Time().equals("0000")) {
							aDTFlatFileModel.setAdmit_Time("");

						}
					} else {

						aDTFlatFileModel.setAdmit_Time("");
					}
				}

				if (row[15] != null) {
					aDTFlatFileModel.setAdmit_Hospital(row[15].trim());
				}
				if (row[16] != null) {
					aDTFlatFileModel.setChief_Complaint(row[16].trim());
				}
				if (row[17] != null) {
					aDTFlatFileModel.setCarrier_1_Code(row[17].trim());
				}
				if (row[18] != null) {
					aDTFlatFileModel.setCarrier_1_Name(row[18].trim());
				}
				if (row[19] != null) {
					aDTFlatFileModel.setInsured_ID_1(row[19].trim());
				}
				if (row[20] != null) {
					aDTFlatFileModel.setCarrier_2_Code(row[20].trim());
				}
				if (row[21] != null) {
					aDTFlatFileModel.setCarrier_2_Name(row[21].trim());
				}
				if (row[22] != null) {
					aDTFlatFileModel.setInsured_ID_2(row[22].trim());
				}
				if (row[23] != null) {
					aDTFlatFileModel.setCarrier_3_Code(row[23].trim());
				}
				if (row[24] != null) {
					aDTFlatFileModel.setCarrier_3_Name(row[24].trim());
				}
				if (row[25] != null) {
					aDTFlatFileModel.setInsured_ID_3(row[25].trim());
				}

				if (row[26] != null) {
					aDTFlatFileModel.setAdmitting_Physician(row[26].trim());
				}
				if (row[27] != null) {
					aDTFlatFileModel.setAttending_Physician(row[27].trim());
				}
				if (row[28] != null) {
					aDTFlatFileModel.setReferring_Physician(row[28].trim());
				}
				if (row[29] != null) {
					aDTFlatFileModel.setConsulting_Physician(row[29].trim());
				}
				if (row[30] != null) {
					aDTFlatFileModel.setComments(row[30].trim());
				}
				if (row[31] != null) {
					aDTFlatFileModel.setNursing_Station(row[31].trim());
				}
				if (row[32] != null) {
					aDTFlatFileModel.setPatient_Room(row[32].trim());
				}
				if (row[33] != null) {
					aDTFlatFileModel.setPatient_Bed(row[33].trim());
				}
				if (row[34] != null) {
					aDTFlatFileModel.setPatient_Service_Code(row[34].trim());
				}
				if (row[35] != null) {
					aDTFlatFileModel.setPatient_Type(row[35].trim());
				}

				if (row[36] != null) {
					aDTFlatFileModel.setDischarge_Date(row[36]);
				}
				if (row[37] != null) {
					aDTFlatFileModel.setDischarge_Time(row[37]);
				}

				String date2 = null;
				if (aDTFlatFileModel.getDischarge_Date() != null
						&& !aDTFlatFileModel.getDischarge_Date().trim().isEmpty()) {

					date2 = row[36];
					aDTFlatFileModel
							.setDischarge_Date(dateConversionUtil.getDate(aDTFlatFileModel.getDischarge_Date()));

				}

				if (row[37] != null && !row[37].isEmpty()) {

					if (aDTFlatFileModel.getDischarge_Date() != null
							&& !aDTFlatFileModel.getDischarge_Date().trim().isEmpty()) {

						aDTFlatFileModel.setDischarge_Time(row[37].trim());

						if (aDTFlatFileModel.getDischarge_Time().equals("0000")) {
							aDTFlatFileModel.setDischarge_Time("");

						}
					} else {

						aDTFlatFileModel.setDischarge_Time("");
					}
				}

				aDTFlatFileModel.setDischarge_Disposition(row[38]);
				aDTFlatFileModel.setSubscriber_ID(row[39]);
				if (row[40] != null) {
					aDTFlatFileModel.setMember_ID((row[40].replace(",", "")).trim());
				} else {
					aDTFlatFileModel.setMember_ID(row[40]);

				}

				aDTFlatFileModelList.add(aDTFlatFileModel);
			}

		} catch (FileNotFoundException e) {

			logger.info("File Not Found {}", filepath);
		} catch (Exception e) {
			logger.info("Execption while reading file {}", e);
		}
		return aDTFlatFileModelList;

	}

}
