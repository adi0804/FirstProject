package com.optum.adt.utils;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.optum.adt.models.ADTHL7FileModel;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v251.datatype.CX;
import ca.uhn.hl7v2.model.v251.group.ADT_A01_INSURANCE;
import ca.uhn.hl7v2.model.v251.group.ADT_A03_INSURANCE;
import ca.uhn.hl7v2.model.v251.group.ADT_A06_INSURANCE;
import ca.uhn.hl7v2.model.v251.message.ADT_A01;
import ca.uhn.hl7v2.model.v251.message.ADT_A03;
import ca.uhn.hl7v2.model.v251.message.ADT_A06;
import ca.uhn.hl7v2.model.v251.segment.DG1;
import ca.uhn.hl7v2.model.v251.segment.IN1;
import ca.uhn.hl7v2.model.v251.segment.MSH;
import ca.uhn.hl7v2.model.v251.segment.NK1;
import ca.uhn.hl7v2.model.v251.segment.PID;
import ca.uhn.hl7v2.model.v251.segment.PV1;
import ca.uhn.hl7v2.model.v251.segment.PV2;

@Component
public class HL7ToCustomModelConversion {

	private static final Logger LOGGER = LoggerFactory.getLogger(HL7ToCustomModelConversion.class);

	public ADTHL7FileModel getHL7ObjectFromFile(Message message) {

		/*
		 * 
		 * •1: MSH (MESSAGE HEADER) •2: EVN (EVENT TYPE) •3: PID (PATIENT
		 * IDENTIFICATION) •4: NK1 (NEXTOF KIN) •5: PV1 (PATIENT VISIT) •6: DG1
		 * (DIAGNOSIS) optional
		 */

		ADTHL7FileModel aDTHL7FileModel = new ADTHL7FileModel();

		try {

			MSH msh = null;

			PID pid = null;
			NK1 nk1 = null;
			PV1 pv1 = null;
			DG1[] dg1 = null;
			IN1 in1 = null;
			PV2 pv2 = null;
			try {
				msh = (MSH) message.get("MSH");
			} catch (Exception e) {

			}

			try {
				nk1 = (NK1) message.get("NK1");
			} catch (Exception e) {

			}
			try {
				pv1 = (PV1) message.get("PV1");
			} catch (Exception e) {

			}

			try {
				pv2 = (PV2) message.get("PV2");
			} catch (Exception e) {

			}
			try {
				// dg1 = (DG1) message.get("DG1");

				dg1 = new DG1[message.getAll("DG1").length];

				for (int i = 0; i < dg1.length; i++) {

					dg1[i] = (DG1) message.getAll("DG1")[i];
				}

			} catch (Exception e) {

				LOGGER.error("Exception {}", e);
			}
			try {
				pid = (PID) message.get("PID");
			} catch (Exception e) {
				LOGGER.error("Exception {}", e);

			}

			try {
				in1 = (IN1) message.get("IN1");

			} catch (Exception e) {
				LOGGER.error("Exception {}", e);

			}

			if (pid != null && !pid.isEmpty()) {
				aDTHL7FileModel = setPIDValues(pid, aDTHL7FileModel);
			} else {

				LOGGER.info("No Patient Idenitifcation details(PID) found ");
			}

			if (pv1 != null && !pv1.isEmpty()) {

				aDTHL7FileModel = setPV1Values(pv1, aDTHL7FileModel);
			} else {

				LOGGER.info("No Patient Visit details(PV1) found ");
			}

			if (msh != null && !msh.isEmpty()) {

				aDTHL7FileModel = setMSHValues(msh, pv1, aDTHL7FileModel);

			} else {

				LOGGER.info("No Messae Header(MSH) found ");
			}

			aDTHL7FileModel.setChiefComplaint(getChiefComplaint(dg1, pv2));

			if (null != msh) {
				aDTHL7FileModel = getInsurance(message, aDTHL7FileModel,
						msh.getMsh9_MessageType().getMsg3_MessageStructure().getValue());
			}

		} catch (HL7Exception e) {
			LOGGER.error("Exception ", e);
		}

		return aDTHL7FileModel;

	}

	public ADTHL7FileModel setPIDValues(PID pid, ADTHL7FileModel aDTHL7FileModel) {

		// PID-18
		try {
			if (null != pid.getPid18_PatientAccountNumber()) {
				aDTHL7FileModel.setAccountNumber(pid.getPid18_PatientAccountNumber().getCx1_IDNumber().encode());
			} else {
				LOGGER.info("Getting null patient account number");

			}
		} catch (HL7Exception e) {
			LOGGER.error("Exception {}", e);

		}

		String medical_record_number = "";
		if (pid.getPid3_PatientIdentifierList().length != 0) {

			for (int i = 0; i < pid.getPid3_PatientIdentifierList().length; i++) {

				if ("MR".equals(pid.getPid3_PatientIdentifierList()[i].getIdentifierTypeCode().getValue())) {

					if (medical_record_number.trim().isEmpty()) {
						medical_record_number = pid.getPid3_PatientIdentifierList()[i].getCx1_IDNumber().getValue();
					} else {

						medical_record_number = medical_record_number + "<>"
								+ pid.getPid3_PatientIdentifierList()[i].getCx1_IDNumber().getValue();
					}

				}

			}
			aDTHL7FileModel.setMedicalRecordNumber(medical_record_number);

		}

		// PID-5.1
		aDTHL7FileModel
				.setPatientLastName(pid.getPid5_PatientName()[0].getXpn1_FamilyName().getFn1_Surname().getValue());

		// PID-5.2
		aDTHL7FileModel.setPatientFirstName(pid.getPid5_PatientName()[0].getXpn2_GivenName().getValue());

		// PID-5.3
		aDTHL7FileModel.setPatientMiddleName(
				pid.getPid5_PatientName()[0].getXpn3_SecondAndFurtherGivenNamesOrInitialsThereof().getValue());

		if (pid.getPid11_PatientAddress().length != 0) {

			// PID-11.1
			aDTHL7FileModel.setPatientAddress1(pid.getPid11_PatientAddress()[0].getXad1_StreetAddress()
					.getSad1_StreetOrMailingAddress().getValue());

			// PID-11.2
			aDTHL7FileModel.setPatientAddress2(pid.getPid11_PatientAddress()[0].getXad2_OtherDesignation().getValue());

			// PID-11.3
			aDTHL7FileModel.setCity(pid.getPid11_PatientAddress()[0].getXad3_City().getValue());

			// PID-11.4
			aDTHL7FileModel.setState(pid.getPid11_PatientAddress()[0].getXad4_StateOrProvince().getValue());

			// PID-11.5
			aDTHL7FileModel.setZip(pid.getPid11_PatientAddress()[0].getXad5_ZipOrPostalCode().getValue());
		}
		// PID-13
		if (pid.getPid13_PhoneNumberHome().length != 0) {

			String phonenumb = "";
			if (pid.getPid13_PhoneNumberHome()[0].getXtn6_AreaCityCode().getValue() != null) {

				phonenumb = phonenumb + pid.getPid13_PhoneNumberHome()[0].getXtn6_AreaCityCode().getValue();

			}
			if (pid.getPid13_PhoneNumberHome()[0].getXtn7_LocalNumber().getValue() != null) {

				phonenumb = phonenumb + pid.getPid13_PhoneNumberHome()[0].getXtn7_LocalNumber().getValue();
			}
			aDTHL7FileModel.setPhone(phonenumb);

		}

		// PID-7
		aDTHL7FileModel.setDOB(pid.getPid7_DateTimeOfBirth().getTime().getValue());

		// PID-8
		aDTHL7FileModel.setGender(pid.getPid8_AdministrativeSex().getValue());

		return aDTHL7FileModel;

	}

	public ADTHL7FileModel setPV1Values(PV1 pv1, ADTHL7FileModel aDTHL7FileModel) {

		try {
			String year = "", month = "", day = "", hour = "", minute = "";

			if (pv1.getPv144_AdmitDateTime().getTime().getYear() != 0) {

				year = pv1.getPv144_AdmitDateTime().getTime().getYear() + "";
			}
			if (pv1.getPv144_AdmitDateTime().getTime().getMonth() != 0) {
				month = pv1.getPv144_AdmitDateTime().getTime().getMonth() + "";
			}

			if (pv1.getPv144_AdmitDateTime().getTime().getDay() != 0) {
				day = pv1.getPv144_AdmitDateTime().getTime().getDay() + "";
			}

			if (pv1.getPv144_AdmitDateTime().getTime().getHour() < 10) {
				hour = "0" + pv1.getPv144_AdmitDateTime().getTime().getHour() + "";

			} else {
				hour = pv1.getPv144_AdmitDateTime().getTime().getHour() + "";

			}

			if (pv1.getPv144_AdmitDateTime().getTime().getMinute() < 10) {
				minute = "0" + pv1.getPv144_AdmitDateTime().getTime().getMinute() + "";
			} else {
				minute = pv1.getPv144_AdmitDateTime().getTime().getMinute() + "";

			}

			// PV1-44
			aDTHL7FileModel.setAdmitDate(year + month + day);
			// PV1-44
			aDTHL7FileModel.setAdmitTime(hour + minute);

			if ("0000".equals(aDTHL7FileModel.getAdmitTime())) {

				aDTHL7FileModel.setAdmitTime("");

			}

			if (pv1.getPv145_DischargeDateTime().length != 0) {

				String year1 = "", month1 = "", day1 = "", hour1 = "", minute1 = "";

				if (pv1.getPv145_DischargeDateTime()[0].getTime().getYear() != 0) {
					year1 = pv1.getPv145_DischargeDateTime()[0].getTime().getYear() + "";
				}
				if (pv1.getPv145_DischargeDateTime()[0].getTime().getMonth() != 0) {
					month1 = pv1.getPv145_DischargeDateTime()[0].getTime().getMonth() + "";
				}
				if (pv1.getPv145_DischargeDateTime()[0].getTime().getDay() != 0) {
					day1 = pv1.getPv145_DischargeDateTime()[0].getTime().getDay() + "";
				}

				if (pv1.getPv145_DischargeDateTime()[0].getTime().getHour() < 10) {
					hour1 = "0" + pv1.getPv145_DischargeDateTime()[0].getTime().getHour() + "";
				} else {
					hour1 = pv1.getPv145_DischargeDateTime()[0].getTime().getHour() + "";
				}

				if (pv1.getPv145_DischargeDateTime()[0].getTime().getMinute() < 10) {

					minute1 = "0" + pv1.getPv145_DischargeDateTime()[0].getTime().getMinute() + "";
				} else {
					minute1 = pv1.getPv145_DischargeDateTime()[0].getTime().getMinute() + "";

				}
				// PV1-45
				aDTHL7FileModel.setDischargeDate(year1 + month1 + day1);

				// PV1-45
				aDTHL7FileModel.setDischargeTime(hour1 + minute1);

				if ("0000".equals(aDTHL7FileModel.getDischargeTime())) {

					aDTHL7FileModel.setDischargeTime("");

				}

			}

		} catch (DataTypeException e) {

		}

		// PV1-17
		if (pv1.getPv117_AdmittingDoctor().length != 0) {

			String admit_doc_fname = "", admit_doc_givenname = "";

			if (pv1.getPv117_AdmittingDoctor()[0].getXcn2_FamilyName().getSurname().getValue() != null) {
				admit_doc_fname = pv1.getPv117_AdmittingDoctor()[0].getXcn2_FamilyName().getSurname().getValue();
			}
			if (pv1.getPv117_AdmittingDoctor()[0].getGivenName().getValue() != null) {
				admit_doc_givenname = pv1.getPv117_AdmittingDoctor()[0].getGivenName().getValue();
			}

			aDTHL7FileModel.setAdmittingPhysician((admit_doc_fname + " " + admit_doc_givenname).trim());
		}
		// PV1-7
		if (pv1.getPv17_AttendingDoctor().length != 0) {

			String attending_doc_fname = "", attending_doc_givenname = "";

			if (pv1.getPv17_AttendingDoctor()[0].getXcn2_FamilyName().getSurname().getValue() != null) {
				attending_doc_fname = pv1.getPv17_AttendingDoctor()[0].getXcn2_FamilyName().getSurname().getValue();

			}

			if (pv1.getPv17_AttendingDoctor()[0].getGivenName().getValue() != null) {
				attending_doc_givenname = pv1.getPv17_AttendingDoctor()[0].getGivenName().getValue();
			}

			aDTHL7FileModel.setAttendingPhysician((attending_doc_fname + " " + attending_doc_givenname).trim());
		}
		// PV1-8
		if (pv1.getPv18_ReferringDoctor().length != 0) {

			String reffering_doc_fname = "", reffering_doc_givenname = "";

			if (pv1.getPv18_ReferringDoctor()[0].getXcn2_FamilyName().getSurname().getValue() != null) {

				reffering_doc_fname = pv1.getPv18_ReferringDoctor()[0].getXcn2_FamilyName().getSurname().getValue();
			}
			if (pv1.getPv18_ReferringDoctor()[0].getGivenName().getValue() != null) {
				reffering_doc_givenname = pv1.getPv18_ReferringDoctor()[0].getGivenName().getValue();
			}
			aDTHL7FileModel.setReferringPhysician((reffering_doc_fname + " " + reffering_doc_givenname).trim());
		}

		// PV1-9
		if (pv1.getPv19_ConsultingDoctor().length != 0) {

			String consulting_doc_fname = "", consulting_doc_givenname = "";

			if (pv1.getPv19_ConsultingDoctor()[0].getXcn2_FamilyName().getSurname().getValue() != null) {
				consulting_doc_fname = pv1.getPv19_ConsultingDoctor()[0].getXcn2_FamilyName().getSurname().getValue();
			}
			if (pv1.getPv19_ConsultingDoctor()[0].getGivenName().getValue() != null) {
				consulting_doc_givenname = pv1.getPv19_ConsultingDoctor()[0].getGivenName().getValue();
			}

			aDTHL7FileModel.setConsultingPhysician((consulting_doc_fname + " " + consulting_doc_givenname).trim());
		}

		// PV1-3.4
		try {
			aDTHL7FileModel.setNursingStation(
					pv1.getPv13_AssignedPatientLocation().getPl4_Facility().encode().replace("^", "&"));
		} catch (HL7Exception e2) {

			e2.printStackTrace();
		}

		// PV1-3.2
		try {

			aDTHL7FileModel.setPatientRoom(pv1.getPv13_AssignedPatientLocation().getRoom().encode().replace("^", "&"));
		} catch (HL7Exception e1) {

			e1.printStackTrace();
		}

		// PV1-3.3
		try {
			aDTHL7FileModel.setPatientBed(pv1.getPv13_AssignedPatientLocation().getBed().encode().replace("^", "&"));
		} catch (HL7Exception e) {

			LOGGER.error("Exception ", e);
		}

		// PV1-2
		try {
			aDTHL7FileModel.setPatientType(pv1.getPv12_PatientClass().encode());
		} catch (HL7Exception e) {

			LOGGER.error("Exception ", e);
		}

		// PV1-36
		if (pv1.getPv136_DischargeDisposition().getValue() != null) {
			pv1.getPv136_DischargeDisposition().getExtraComponents();
		}

		int num_comp = pv1.getPv136_DischargeDisposition().getExtraComponents().numComponents();

		String sub_comp = "";

		for (int i = 0; i < num_comp; i++) {

			if (pv1.getPv136_DischargeDisposition().getExtraComponents().getComponent(i).getData().toString() != null
					&& !pv1.getPv136_DischargeDisposition().getExtraComponents().getComponent(i).getData().toString()
							.trim().isEmpty()) {
				sub_comp += pv1.getPv136_DischargeDisposition().getExtraComponents().getComponent(i).getData()
						.toString() + "^";
			}

		}

		if (pv1.getPv136_DischargeDisposition().getValue() != null) {

			String discharge_dispo = pv1.getPv136_DischargeDisposition().getValue() + "^" + sub_comp;

			while (discharge_dispo.trim().endsWith("^")) {
				discharge_dispo = discharge_dispo.trim().substring(0, discharge_dispo.trim().length() - 1).concat("");
			}
			aDTHL7FileModel.setDischargeDisposition(discharge_dispo);
		} else {

			while (sub_comp.trim().endsWith("^")) {
				sub_comp = sub_comp.trim().substring(0, sub_comp.trim().length() - 1).concat("");
			}
			aDTHL7FileModel.setDischargeDisposition(sub_comp);
		}

		//

		return aDTHL7FileModel;

	}

	public ADTHL7FileModel setMSHValues(MSH msh, PV1 pv1, ADTHL7FileModel aDTHL7FileModel) throws HL7Exception {

		String admitHospital = "";
		try {
			if (msh.getMsh4_SendingFacility().encode() != null
					&& msh.getMsh4_SendingFacility().getHd1_NamespaceID().encode() != null) {

				admitHospital = msh.getMsh4_SendingFacility().getHd1_NamespaceID().encode();

			}
		} catch (HL7Exception e) {

			LOGGER.error("Exception ", e);
		}

		String pv1Value = "";

		if (pv1.getPv13_AssignedPatientLocation().getPl1_PointOfCare().getValue() != null) {

			pv1Value = pv1.getPv13_AssignedPatientLocation().getPl1_PointOfCare().getValue();
		}

		aDTHL7FileModel.setAdmitHospital(admitHospital + "<>" + pv1Value);
		// aDTHL7FileModel.setMessageType(msh.getMsh9_MessageType().getMsg1_MessageCode().getValue()
		// + "^"
		// + msh.getMsh9_MessageType().getMsg2_TriggerEvent().getValue() + "^"
		// + msh.getMsh9_MessageType().getMsg3_MessageStructure().getValue());
		aDTHL7FileModel.setMessageType(msh.getMsh9_MessageType().encode());
		return aDTHL7FileModel;

	}

	public String getChiefComplaint(DG1[] dg1Array, PV2 pv2) {

		/**
		 * If PV2-3 is populated, use it. Else if PV2-3 is blank, use DG1-3.1 & "^" &
		 * DG1-4 If DG1-4 is blank, use DG1-3.2 Repeat for each DG1 segment, separate
		 * iterations with tilde
		 */

		String fetchedPV2Value = fetchPV2Value(pv2);

		if (!StringUtils.isEmpty(fetchedPV2Value)) {
			return fetchedPV2Value;
		} else {

			return fetchChiefComplaintValueForDG13(dg1Array);

		}

	}

	private String fetchChiefComplaintValueForDG13(DG1[] dg1Array) {

		StringBuilder chiefComplaint = new StringBuilder();

		for (int i = 0; i < dg1Array.length; i++) {

			DG1 dg1 = dg1Array[i];

			chiefComplaint.append(fetchDg131(dg1));

			String fetchedDg14 = fetchDg14(dg1);

			if (StringUtils.isEmpty(fetchedDg14)) {

				String fetchedDg132 = fetchDg132(dg1);
				if (!fetchedDg132.isEmpty()) {
					chiefComplaint.append("^");
					chiefComplaint.append(fetchedDg132);
				}

			} else {

				chiefComplaint.append("^");
				chiefComplaint.append(fetchedDg14);

			}
			if (dg1Array.length - i != 1 && !StringUtils.isEmpty(chiefComplaint)) {

				chiefComplaint.append("~");
			}

		}

		return chiefComplaint.toString();
	}

	private String fetchDg132(DG1 dg1) {
		String dg132 = "";

		try {
			dg132 = dg1.getDg13_DiagnosisCodeDG1().getCe2_Text().encode();
		} catch (HL7Exception e) {
			LOGGER.error("Exception while fetching Diagnose code text (DG1-3-2): {}", e);

		}
		return dg132;

	}

	private String fetchDg14(DG1 dg1) {

		String dg14 = "";
		try {
			dg14 = dg1.getDg14_DiagnosisDescription().encode();
		} catch (HL7Exception e) {
			LOGGER.error("Exception while fetching Diagnose description (DG1-4): {}", e);

		}

		return dg14;

	}

	public String fetchDg131(DG1 dg1) {
		String dg131 = "";
		try {
			dg131 = dg1.getDg13_DiagnosisCodeDG1().getCe1_Identifier().encode();
		} catch (HL7Exception e) {
			LOGGER.error("Exception while fetching Diagnose Code (DG1-3): {}", e);

		}
		return dg131;
	}

	public String fetchPV2Value(PV2 pv2) {
		String pv2Value = "";
		if (null != pv2) {

			try {
				pv2Value = pv2.getPv23_AdmitReason().encode();
			} catch (HL7Exception e) {

				LOGGER.error("Exception while fetching admit reason(PV2-3): {}", e);
			}

		}
		return pv2Value;
	}

	public ADTHL7FileModel setDG1Values(DG1[] dg1, ADTHL7FileModel aDTHL7FileModel, PV2 pv2) {

		String diag_string = "";

		for (int i = 0; i < dg1.length; i++) {

			DG1 dg = dg1[i];

			String diag3 = "";
			String diag4 = "";

			try {
				diag3 = dg.getDg13_DiagnosisCodeDG1().encode();
				diag4 = dg.getDg14_DiagnosisDescription().encode();
			} catch (Exception e) {

			}

			String diag = diag3.trim() + "^" + diag4.trim();

			String diag_string_value = diag + "~";

			diag_string = diag_string + diag_string_value;

		}

		if (diag_string.trim().endsWith("~")) {
			diag_string = diag_string.trim().substring(0, diag_string.trim().length() - 1).concat("");
		}

		aDTHL7FileModel.setChiefComplaint(diag_string);

		return aDTHL7FileModel;

	}

	public ADTHL7FileModel getInsurance(Message message, ADTHL7FileModel aDTHL7FileModel, String messageType) {

		if ("ADT_A01".equals(messageType)) {
			aDTHL7FileModel = setINValuesForADT_A01(message, aDTHL7FileModel);

		}

		if ("ADT_A03".equals(messageType)) {
			aDTHL7FileModel = setINValuesForADT_A03(message, aDTHL7FileModel);

		}

		if ("ADT_A06".equals(messageType)) {
			aDTHL7FileModel = setINValuesForADT_A06(message, aDTHL7FileModel);

		}

		return aDTHL7FileModel;

	}

	private ADTHL7FileModel setINValuesForADT_A06(Message message, ADTHL7FileModel aDTHL7FileModel) {
		ADT_A06 adtMsg = (ADT_A06) message;

		List<ADT_A06_INSURANCE> inList;
		try {
			inList = adtMsg.getINSURANCEAll();

			int i = 0;
			for (ADT_A06_INSURANCE adta06 : inList) {

				aDTHL7FileModel = setIN1_1values(adta06.getIN1(), i, aDTHL7FileModel);
				i++;
			}

		} catch (HL7Exception e) {

			LOGGER.error("Exception ", e);
		}
		return aDTHL7FileModel;
	}

	private ADTHL7FileModel setINValuesForADT_A03(Message message, ADTHL7FileModel aDTHL7FileModel) {
		ADT_A03 adtMsg = (ADT_A03) message;

		List<ADT_A03_INSURANCE> inList;
		try {
			inList = adtMsg.getINSURANCEAll();

			int i = 0;
			for (ADT_A03_INSURANCE adta03 : inList) {

				aDTHL7FileModel = setIN1_1values(adta03.getIN1(), i, aDTHL7FileModel);
				i++;
			}

		} catch (HL7Exception e) {

			LOGGER.error("Exception ", e);
		}
		return aDTHL7FileModel;
	}

	private ADTHL7FileModel setINValuesForADT_A01(Message message, ADTHL7FileModel aDTHL7FileModel) {
		ADT_A01 adtMsg = (ADT_A01) message;

		List<ADT_A01_INSURANCE> inList;
		try {
			inList = adtMsg.getINSURANCEAll();

			int i = 0;

			System.out.println("INsurance size " + inList.size());
			for (ADT_A01_INSURANCE adta01 : inList) {

				aDTHL7FileModel = setIN1_1values(adta01.getIN1(), i, aDTHL7FileModel);
				i++;
			}

		} catch (HL7Exception e) {

			LOGGER.error("Exception ", e);
		}
		return aDTHL7FileModel;
	}

	public ADTHL7FileModel setIN1_1values(IN1 in1, int index, ADTHL7FileModel aDTHL7FileModel) {

		if (index == 0) {

			aDTHL7FileModel.setSubscriberID(getIN49ArrayString(in1.getIn149_InsuredSIDNumber()));
			aDTHL7FileModel.setMemberID(getIN49ArrayString(in1.getIn149_InsuredSIDNumber()));

			aDTHL7FileModel.setCarrier1Code(getIn1CarrierCode(in1));

			if (in1.getIn14_InsuranceCompanyName().length != 0) {

				try {

					aDTHL7FileModel.setCarrier2Name(in1.getIn14_InsuranceCompanyName()[0].encode());

				} catch (HL7Exception e) {
					LOGGER.error("Exception ", e);
				}
			}

			try {
				aDTHL7FileModel.setInsuredID1(in1.getIn136_PolicyNumber().encode());
			} catch (HL7Exception e) {

				LOGGER.error("Exception ", e);
			}
		}
		if (index == 1) {

			aDTHL7FileModel.setCarrier2Code(getIn1CarrierCode(in1));
			if (in1.getIn14_InsuranceCompanyName().length != 0) {

				int num_comp = in1.getIn14_InsuranceCompanyName()[0].getXon1_OrganizationName().getExtraComponents()
						.numComponents();

				String sub_comp = "";

				for (int i = 0; i < num_comp; i++) {

					if (in1.getIn14_InsuranceCompanyName()[0].getXon1_OrganizationName().getExtraComponents()
							.getComponent(i).getData().toString() != null
							&& !in1.getIn14_InsuranceCompanyName()[0].getXon1_OrganizationName().getExtraComponents()
									.getComponent(i).getData().toString().trim().isEmpty()) {
						sub_comp += in1.getIn14_InsuranceCompanyName()[0].getXon1_OrganizationName()
								.getExtraComponents().getComponent(i).getData().toString() + "&";
					}

				}

				try {
					aDTHL7FileModel.setCarrier2Name(in1.getIn14_InsuranceCompanyName()[0].encode());
				} catch (HL7Exception e) {

					LOGGER.error("Exception ", e);
				}

			}

			try {
				aDTHL7FileModel.setInsuredID2(in1.getIn136_PolicyNumber().encode());
			} catch (HL7Exception e) {

				LOGGER.error("Exception ", e);
			}
		}
		if (index == 2) {

			aDTHL7FileModel.setCarrier3Code(getIn1CarrierCode(in1));
			if (in1.getIn14_InsuranceCompanyName().length != 0) {

				int num_comp = in1.getIn14_InsuranceCompanyName()[0].getXon1_OrganizationName().getExtraComponents()
						.numComponents();

				String sub_comp = "";

				for (int i = 0; i < num_comp; i++) {

					if (in1.getIn14_InsuranceCompanyName()[0].getXon1_OrganizationName().getExtraComponents()
							.getComponent(i).getData().toString() != null
							&& !in1.getIn14_InsuranceCompanyName()[0].getXon1_OrganizationName().getExtraComponents()
									.getComponent(i).getData().toString().trim().isEmpty()) {
						sub_comp += in1.getIn14_InsuranceCompanyName()[0].getXon1_OrganizationName()
								.getExtraComponents().getComponent(i).getData().toString() + "&";
					}

				}

				try {
					aDTHL7FileModel.setCarrier3Name(in1.getIn14_InsuranceCompanyName()[0].encode());
				} catch (HL7Exception e) {
					LOGGER.error("Exception :{}", e);
				}

			}

			try {
				aDTHL7FileModel.setInsuredID3(in1.getIn136_PolicyNumber().encode());
			} catch (HL7Exception e) {

				LOGGER.error("Exception :{}", e);
			}

		}

		return aDTHL7FileModel;

	}

	public String getIn1CarrierCode(IN1 in1) {

		try {
			return in1.getIn12_InsurancePlanID().encode();
		} catch (HL7Exception e) {
			LOGGER.error("Exception ", e);
			return "";
		}

	}

	public String getIN49ArrayString(CX[] array) {

		String value = "";
		if (array.length != 0) {

			for (int i = 0; i < array.length; i++) {

				if (value.isEmpty()) {
					try {
						value = array[i].encode();
					} catch (HL7Exception e) {
						LOGGER.error("Exception :{}", e);
					}

				} else {

					try {
						value = value + "~" + array[i].encode();
					} catch (HL7Exception e) {
						LOGGER.error("Exception :{}", e);
					}

				}

			}

			if (value.trim().endsWith("~")) {
				value = value.trim().substring(0, value.trim().length() - 1).concat("");
			}

		} else {

			return "";
		}
		return value;

	}

}
