package com.optum.adt.example;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.optum.adt.utils.CSVFileReadUtil;
import com.optum.adt.utils.HL7FileReadUtil;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v251.group.ADT_A01_INSURANCE;
import ca.uhn.hl7v2.model.v251.group.ADT_A03_INSURANCE;
import ca.uhn.hl7v2.model.v251.group.ADT_A06_INSURANCE;
import ca.uhn.hl7v2.model.v251.message.ADT_A01;
import ca.uhn.hl7v2.model.v251.message.ADT_A03;
import ca.uhn.hl7v2.model.v251.message.ADT_A06;
import ca.uhn.hl7v2.model.v251.segment.IN1;
import ca.uhn.hl7v2.model.v251.segment.MSH;
import ca.uhn.hl7v2.util.Hl7InputStreamMessageIterator;

@Component
public class Example {

	@Autowired
	private Environment environment;

	@Autowired
	CSVFileReadUtil cSVFileReadUtil;
	@Autowired
	HL7FileReadUtil hL7FileReadUtil;

	public void example() {

		File file = new File(
				"C:/Users/nkanuri/Documents/MyJabberFiles/nkanuri@corpimsvcs.com/ADT/NEHII_HL7_RAW.20180814.txt");

		InputStream is = null;

		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}

		// It's generally a good idea to buffer file IO
		is = new BufferedInputStream(is);

		// The following class is a HAPI utility that will iterate over
		// the messages which appear over an InputStream
		Hl7InputStreamMessageIterator iter = new Hl7InputStreamMessageIterator(is);

		int i = 1;
		while (iter.hasNext()) {
			Message message = iter.next();

			try {

				MSH msh = (MSH) message.get("MSH");

				if (msh.getMsh9_MessageType().getMsg3_MessageStructure().getValue().equals("ADT_A01")) {

					ADT_A01 adtMsg = (ADT_A01) message;

					List<ADT_A01_INSURANCE> in = adtMsg.getINSURANCEAll();

					in.forEach(a -> {

						IN1 in1 = a.getIN1();

					});

				}
				i++;

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public IN1 getInsurance(Message message, String messageType) {

		if ("ADT_A01".equals(messageType)) {
			ADT_A01 adtMsg = (ADT_A01) message;

			List<ADT_A01_INSURANCE> in;
			try {
				in = adtMsg.getINSURANCEAll();
			} catch (HL7Exception e) {
				e.printStackTrace();
			}

		}

		if ("ADT_A03".equals(messageType)) {
			ADT_A03 adtMsg = (ADT_A03) message;

			List<ADT_A03_INSURANCE> in;
			try {
				in = adtMsg.getINSURANCEAll();
			} catch (HL7Exception e) {
				e.printStackTrace();
			}

		}

		if ("ADT_A06".equals(messageType)) {
			ADT_A06 adtMsg = (ADT_A06) message;

			List<ADT_A06_INSURANCE> in;
			try {
				in = adtMsg.getINSURANCEAll();
			} catch (HL7Exception e) {
				e.printStackTrace();
			}

		}

		return null;

	}

}
