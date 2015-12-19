package cqupt.swxxxy.prideXML.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.PrideXmlControllerImpl;
import uk.ac.ebi.pride.utilities.data.core.CvParam;
import uk.ac.ebi.pride.utilities.data.core.DataProcessing;
import uk.ac.ebi.pride.utilities.data.core.ProcessingMethod;
import uk.ac.ebi.pride.utilities.data.core.Software;

import cqupt.swxxxy.prideXML.intf.ParseDataProcessingIntf;

public class ParseDataProcessingImpl implements ParseDataProcessingIntf{
	PrideXmlControllerImpl cachedDataAccessController;
	
	public ParseDataProcessingImpl(PrideXmlControllerImpl cachedDataAccessController){
		this.cachedDataAccessController=cachedDataAccessController;
	}
	
	public List<Software> getSoftwares() {
		List<Software> softwareList=cachedDataAccessController.getSoftwares();
		return softwareList;
	}

	public List<DataProcessing> getDataProcessings() {
		List<DataProcessing> dataProcessingList=cachedDataAccessController.getDataProcessings();
		return dataProcessingList;
	}

	public List<ProcessingMethod> getProcessingMethod(DataProcessing dataProcessing) {
		return dataProcessing.getProcessingMethods();
	}

	public Map<String, String> getSoftwareParam(Software software) {
		Map<String,String> softwareParam=new HashMap<String,String>();
		softwareParam.put("name", software.getName());
		softwareParam.put("version", software.getVersion());
		return softwareParam;
	}

	public Map<String, String> getProcessingMethodCvParam(
			CvParam cvParam) {
		Map<String,String> processingMethodCvParam=new HashMap<String,String>();
		processingMethodCvParam.put("accession", cvParam.getAccession());
		processingMethodCvParam.put("name", cvParam.getName());
		processingMethodCvParam.put("value", cvParam.getValue());
		return processingMethodCvParam;
	}

}
