package cqupt.swxxxy.test.prideXML;

import java.io.File;
import java.util.List;

import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.PrideXmlControllerImpl;
import uk.ac.ebi.pride.utilities.data.core.Organization;
import uk.ac.ebi.pride.utilities.data.core.Person;

import cqupt.swxxxy.prideXML.impl.ParseAdminImpl;
import cqupt.swxxxy.prideXML.intf.ParseAdminIntf;

public class ParseAdminTest {
	
	public static void main(String[] args) {
		String filePath="G://swxxxy//xml//CHPP_97H_RP10_1.dat-pride.xml";
		File inputFile=new File(filePath);
		PrideXmlControllerImpl cachedDataAccessController=new PrideXmlControllerImpl(inputFile);
		ParseAdminImpl parseAdmin=new ParseAdminImpl(cachedDataAccessController);
		List<Organization> organizationList=parseAdmin.getOrganizationContacts();
		List<Person> personList=parseAdmin.getPersonContacts();
	}
	
}
