package com.optum.adt.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v251.datatype.CE;
import ca.uhn.hl7v2.model.v251.datatype.ST;
import ca.uhn.hl7v2.model.v251.segment.DG1;
import ca.uhn.hl7v2.model.v251.segment.PV2;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = HL7ToCustomModelConversion.class)
public class HL7ToCustomModelConversionTest {

	@Autowired
	HL7ToCustomModelConversion hL7ToCustomModelConversion;

	@Test
	public void testGetChiefComplaint() throws HL7Exception {
		/**
		 * If PV2-3 is populated, use it
		 */

		PV2 pv2 = Mockito.mock(PV2.class);

		DG1 dg1 = Mockito.mock(DG1.class);

		DG1[] dg1Array = new DG1[] { dg1 };
		CE ce = Mockito.mock(CE.class);
		Mockito.when(pv2.getPv23_AdmitReason()).thenReturn(ce);
		Mockito.when(ce.encode()).thenReturn("cheifcomplaint");
		Assert.assertEquals("cheifcomplaint", hL7ToCustomModelConversion.getChiefComplaint(dg1Array, pv2));
	}

	@Test
	public void testGetChiefComplaintForElse() throws HL7Exception {
		/**
		 * if PV2-3 is blank, use DG1-3.1 & "^" & DG1-4 If DG1-4 is blank, use DG1-3.2
		 * Repeat for each DG1 segment, separate iterations with tilde
		 */
		PV2 pv2 = Mockito.mock(PV2.class);
		DG1 dg1 = Mockito.mock(DG1.class);
		DG1[] dg1Array = new DG1[] { dg1 };
		CE ce = Mockito.mock(CE.class);
		ST st = Mockito.mock(ST.class);
		Mockito.when(pv2.getPv23_AdmitReason()).thenReturn(ce);
		Mockito.when(ce.encode()).thenReturn("");
		CE ce2 = Mockito.mock(CE.class);
		Mockito.when(dg1.getDg13_DiagnosisCodeDG1()).thenReturn(ce2);
		Mockito.when(st.encode()).thenReturn("Anal fissure, unspecified");
		Mockito.when(dg1.getDg14_DiagnosisDescription()).thenReturn(st);

		ST st2 = Mockito.mock(ST.class);
		Mockito.when(ce2.getCe1_Identifier()).thenReturn(st2);
		Mockito.when(st2.encode()).thenReturn("K60.2");

		Assert.assertEquals("K60.2^Anal fissure, unspecified",
				hL7ToCustomModelConversion.getChiefComplaint(dg1Array, pv2));
	}

	@Test
	public void fetchChiefComplaintValueForDG131ElseBlock() throws HL7Exception {

		/**
		 * If PV2-3 is populated, use it. Else if PV2-3 is blank, use DG1-3.1 & "^" &
		 * DG1-4 If DG1-4 is blank, use DG1-3.2 Repeat for each DG1 segment, separate
		 * iterations with tilde
		 */

		PV2 pv2 = Mockito.mock(PV2.class);
		DG1 dg1 = Mockito.mock(DG1.class);
		DG1[] dg1Array = new DG1[] { dg1 };
		CE ce = Mockito.mock(CE.class);
		ST st = Mockito.mock(ST.class);
		Mockito.when(pv2.getPv23_AdmitReason()).thenReturn(ce);
		Mockito.when(ce.encode()).thenReturn("");
		CE ce2 = Mockito.mock(CE.class);
		Mockito.when(dg1.getDg13_DiagnosisCodeDG1()).thenReturn(ce2);
		Mockito.when(st.encode()).thenReturn("");
		Mockito.when(dg1.getDg14_DiagnosisDescription()).thenReturn(st);

		ST st2 = Mockito.mock(ST.class);
		Mockito.when(ce2.getCe1_Identifier()).thenReturn(st2);
		Mockito.when(st2.encode()).thenReturn("K60.2");

		ST st3 = Mockito.mock(ST.class);
		Mockito.when(dg1.getDg13_DiagnosisCodeDG1().getCe2_Text()).thenReturn(st3);
		Mockito.when(st3.encode()).thenReturn("Anal fissure, unspecified");

		Assert.assertEquals("K60.2^Anal fissure, unspecified",
				hL7ToCustomModelConversion.getChiefComplaint(dg1Array, pv2));

	}

	@Test
	public void testGetChiefComplaintForElseAndRecursive() throws HL7Exception {
		/**
		 * If PV2-3 is populated, use it. Else if PV2-3 is blank, use DG1-3.1 & "^" &
		 * DG1-4 If DG1-4 is blank, use DG1-3.2 Repeat for each DG1 segment, separate
		 * iterations with tilde
		 */

		PV2 pv2 = Mockito.mock(PV2.class);
		DG1 dg1 = Mockito.mock(DG1.class);

		DG1[] dg1Array = new DG1[] { dg1, dg1 };
		CE ce = Mockito.mock(CE.class);
		ST st = Mockito.mock(ST.class);
		Mockito.when(pv2.getPv23_AdmitReason()).thenReturn(ce);
		Mockito.when(ce.encode()).thenReturn("");
		CE ce2 = Mockito.mock(CE.class);
		Mockito.when(dg1.getDg13_DiagnosisCodeDG1()).thenReturn(ce2);
		Mockito.when(st.encode()).thenReturn("Anal fissure, unspecified");
		Mockito.when(dg1.getDg14_DiagnosisDescription()).thenReturn(st);

		ST st2 = Mockito.mock(ST.class);
		Mockito.when(ce2.getCe1_Identifier()).thenReturn(st2);
		Mockito.when(st2.encode()).thenReturn("K60.2");

		Assert.assertEquals("K60.2^Anal fissure, unspecified~K60.2^Anal fissure, unspecified",
				hL7ToCustomModelConversion.getChiefComplaint(dg1Array, pv2));
	}

	@Test
	public void fetchChiefComplaintValueForDG13ElseBlockAllEmpty() throws HL7Exception {

		/**
		 * If PV2-3 is populated, use it. Else if PV2-3 is blank, use DG1-3.1 & "^" &
		 * DG1-4 If DG1-4 is blank, use DG1-3.2 Repeat for each DG1 segment, separate
		 * iterations with tilde
		 */
		PV2 pv2 = Mockito.mock(PV2.class);
		DG1 dg1 = Mockito.mock(DG1.class);
		DG1[] dg1Array = new DG1[] { dg1, dg1 };
		CE ce = Mockito.mock(CE.class);
		ST st = Mockito.mock(ST.class);
		Mockito.when(pv2.getPv23_AdmitReason()).thenReturn(ce);
		Mockito.when(ce.encode()).thenReturn("");
		CE ce2 = Mockito.mock(CE.class);
		Mockito.when(dg1.getDg13_DiagnosisCodeDG1()).thenReturn(ce2);
		Mockito.when(st.encode()).thenReturn("");
		Mockito.when(dg1.getDg14_DiagnosisDescription()).thenReturn(st);

		ST st2 = Mockito.mock(ST.class);
		Mockito.when(ce2.getCe1_Identifier()).thenReturn(st2);
		Mockito.when(st2.encode()).thenReturn("");

		ST st3 = Mockito.mock(ST.class);
		Mockito.when(dg1.getDg13_DiagnosisCodeDG1().getCe2_Text()).thenReturn(st3);
		Mockito.when(st3.encode()).thenReturn("");

		Assert.assertEquals("", hL7ToCustomModelConversion.getChiefComplaint(dg1Array, pv2));

	}

	@Test
	public void testFetchDg13() {
	}

	@Test
	public void testFetchPV2Value() {
	}

	@Test
	public void testSetDG1Values() {
	}

}
