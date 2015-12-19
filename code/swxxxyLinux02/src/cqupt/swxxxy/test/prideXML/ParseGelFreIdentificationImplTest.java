package cqupt.swxxxy.test.prideXML;



import java.io.File;

import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.CachedDataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.PrideXmlControllerImpl;
import uk.ac.ebi.pride.utilities.data.core.Peptide;
import uk.ac.ebi.pride.utilities.data.core.Protein;

import cqupt.swxxxy.prideXML.impl.ParseGelFreIdentificationImpl;

public class ParseGelFreIdentificationImplTest {

	public static void main(String[] args) {
		String filePath="G://swxxxy//xml//CHPP_97H_RP10_1.dat-pride.xml";
		File inputFile=new File(filePath);
		CachedDataAccessController cachedDataAccessController=new PrideXmlControllerImpl(inputFile);
		ParseGelFreIdentificationImpl pgfi=new ParseGelFreIdentificationImpl(cachedDataAccessController);
		Peptide peptide=pgfi.getPeptideByIndex("1",0);
		int i=1;
	}

}
