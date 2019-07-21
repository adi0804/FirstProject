package com.optum.adt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class QaAutomationAdtApplication {

	public static void main(String[] args) {

		ConfigurableApplicationContext cat = SpringApplication.run(QaAutomationAdtApplication.class, args);

		AttributeCompare attributeCompare = (AttributeCompare) cat.getBean("attributeCompare");

		String testername = "";
		for (int i = 0; i < args.length; i++) {

			testername += args[i] + " ";
		}

		attributeCompare.compareAttribute(testername);

	}
}
