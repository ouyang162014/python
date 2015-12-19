package cqupt.swxxxy.prideXML.impl;

import java.util.List;

import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.PrideXmlControllerImpl;
import uk.ac.ebi.pride.utilities.data.core.CvParam;
import uk.ac.ebi.pride.utilities.data.core.Organization;
import uk.ac.ebi.pride.utilities.data.core.Person;
import uk.ac.ebi.pride.utilities.data.core.Sample;
import uk.ac.ebi.pride.utilities.data.core.SourceFile;

import cqupt.swxxxy.prideXML.intf.ParseAdminIntf;

public class ParseAdminImpl implements ParseAdminIntf {
	PrideXmlControllerImpl cachedDataAccessController;
	
	public ParseAdminImpl(){}
	
	public ParseAdminImpl(PrideXmlControllerImpl cachedDataAccessController){
		this.cachedDataAccessController=cachedDataAccessController;
	}
	
	public List<Sample> getSamples() {
		List<Sample> sampleList=cachedDataAccessController.getSamples();
		return sampleList;
	}

	public List<SourceFile> getSourceFiles() {
		List<SourceFile> sourceFileList=cachedDataAccessController.getSourceFiles();
		return sourceFileList;
	}

	public List<Person> getPersonContacts() {
		List<Person> personList=cachedDataAccessController.getPersonContacts();
		return personList;
	}

	public String[] getSampleinfos(Sample sample) {
		int i=1;
		String[] sampleInfos=new String[4];
		sampleInfos[0]=sample.getName();
		List<CvParam> cvParamList=sample.getCvParams();
		for(CvParam cvParam:cvParamList){
			sampleInfos[i]=cvParam.getName();
		}
		return sampleInfos;
	}

	public String[] getSourFileInfo(SourceFile sourceFile) {
		String[] sourceFileInfos=new String[3];
		sourceFileInfos[0]=sourceFile.getName();
		sourceFileInfos[1]=sourceFile.getPath();
		sourceFileInfos[2]=sourceFile.getExternalFormatDocumentationURI();
		return sourceFileInfos;
	}


	public List<Organization> getOrganizationContacts() {
		List<Organization> organizationList=cachedDataAccessController.getOrganizationContacts();
		return organizationList;
	}

	public String getOrganizationInfos(Organization organization) {
		return organization.getName();
	}

	public String[] getPersonInfos(Person person) {
		String[] contactInfos=new String[2];
		contactInfos[0]=person.getName();
		contactInfos[1]=person.getContactInfo();
		return contactInfos;
	}

}
