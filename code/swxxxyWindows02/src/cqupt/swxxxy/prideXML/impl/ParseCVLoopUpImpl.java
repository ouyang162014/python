package cqupt.swxxxy.prideXML.impl;

import java.io.File;
import java.util.List;

import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.PrideXmlControllerImpl;
import uk.ac.ebi.pride.utilities.data.core.CVLookup;

import cqupt.swxxxy.prideXML.intf.ParseCVLoopUpIntf;

public class ParseCVLoopUpImpl implements ParseCVLoopUpIntf{
	PrideXmlControllerImpl cachedDataAccessController;
	
	public ParseCVLoopUpImpl(PrideXmlControllerImpl cachedDataAccessController){
		this.cachedDataAccessController=cachedDataAccessController;
	}
	
	public List<CVLookup> getCvLookups() {
		List<CVLookup> lookUpList=cachedDataAccessController.getCvLookups();
		return lookUpList;
	}

	public String getCvLookupLable(CVLookup cVLookup) {
		return cVLookup.getCvLabel();
	}

	public String getCvLookupFullName(CVLookup cVLookup) {
		return cVLookup.getFullName();
	}

	public String getCvLookVersion(CVLookup cVLookup) {
		return cVLookup.getVersion();
	}

	public String getCvLookupAddress(CVLookup cVLookup) {
		return cVLookup.getAddress();
	}

}
