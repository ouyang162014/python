package cqupt.swxxxy.test.prideXML;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.CachedDataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.PrideXmlControllerImpl;
import uk.ac.ebi.pride.utilities.data.core.Peptide;
import uk.ac.ebi.pride.utilities.data.core.Protein;
import uk.ac.ebi.pride.utilities.data.core.Spectrum;
import cqupt.swxxxy.dao.Intf.PrideXMLDaoIntf;
import cqupt.swxxxy.dao.impl.PrideXMLDaoImpl;
import cqupt.swxxxy.prideXML.impl.ParseGelFreIdentificationImpl;
import cqupt.swxxxy.prideXML.intf.ParseGelFreeIdentificationIntf;
import cqupt.swxxxy.utils.Constants;
import cqupt.swxxxy.utils.ReadConfiguration;

public class PrideXMlDaoTest {

	public static void main(String[] args) {
		ReadConfiguration rc=new ReadConfiguration();
		Map<String,String> conf=rc.getProperties();
		//String url=conf.get("url");
		String inputFilePath=conf.get("inputFilePath");
		//String filePath=Constants.prideXmlFilePath;
		
		//File inputFile=new File(filePath);
		//CachedDataAccessController cachedDataAccessController=new PrideXmlControllerImpl(inputFile);
		PrideXMLDaoIntf prideXMLDaoIntf=new PrideXMLDaoImpl(inputFilePath);
		//ParseGelFreeIdentificationIntf parseGelFreeIdentificationIntf=new ParseGelFreIdentificationImpl(cachedDataAccessController);
		//Protein protein=cachedDataAccessController.getProteinById("0");
		//Collection<Comparable> peptideIds=parseGelFreeIdentificationIntf.getPeptideIds("0");
		//Spectrum spectrum=cachedDataAccessController.getSpectrumById("1");
		
		/*try {
			prideXMLDaoIntf.insertPeptide(peptideIds, "0");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//prideXMLDaoIntf.insertProtein("0");
		//prideXMLDaoIntf.insertSpectrum("1");
		prideXMLDaoIntf.insertAll();
		/*try {
			prideXMLDaoIntf.writeSpectrum(spectrum, filePath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		/*ParseGelFreeIdentificationIntf ParseGelFreeIdentificationIntf=new ParseGelFreIdentificationImpl(cachedDataAccessController);
		Peptide peptide=ParseGelFreeIdentificationIntf.getPeptideByIndex(0, 0);
		int i=1;*/
	}

}
