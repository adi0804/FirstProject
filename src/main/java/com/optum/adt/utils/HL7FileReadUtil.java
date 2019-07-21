package com.optum.adt.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v251.segment.PID;
import ca.uhn.hl7v2.util.Hl7InputStreamMessageIterator;

import com.optum.adt.models.ADTHL7FileModel;

@Component
public class HL7FileReadUtil {

	@Autowired
	private Environment environment;
	private final Logger logger = LoggerFactory.getLogger(HL7FileReadUtil.class);
	@Autowired
	HL7ToCustomModelConversion hL7ToCustomModelConversion;

	public List<ADTHL7FileModel> readHL7FileasList(String filepath) {

		List<ADTHL7FileModel> aDTHL7FileModelList = new ArrayList<>();

		File file = new File(filepath);

		InputStream is = null;

		try {
			is = new FileInputStream(file);

			// It's generally a good idea to buffer file IO
			is = new BufferedInputStream(is);

			// The following class is a HAPI utility that will iterate over
			// the messages which appear over an InputStream
			Hl7InputStreamMessageIterator iter = new Hl7InputStreamMessageIterator(is);

			while (iter.hasNext()) {

				Message message = iter.next();

				ADTHL7FileModel adthl7FileModel = hL7ToCustomModelConversion.getHL7ObjectFromFile(message);

				aDTHL7FileModelList.add(adthl7FileModel);
			}
		} catch (FileNotFoundException e) {

			logger.info("File Not Found {} ", e);
		} catch (Exception e) {
			logger.info("Execption while reading file ", e);
		}
		return aDTHL7FileModelList;

	}
}
