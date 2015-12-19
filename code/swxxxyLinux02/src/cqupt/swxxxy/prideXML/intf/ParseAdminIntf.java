package cqupt.swxxxy.prideXML.intf;

import java.util.List;

import uk.ac.ebi.pride.utilities.data.core.Contact;
import uk.ac.ebi.pride.utilities.data.core.Organization;
import uk.ac.ebi.pride.utilities.data.core.Person;
import uk.ac.ebi.pride.utilities.data.core.Sample;
import uk.ac.ebi.pride.utilities.data.core.SourceFile;

public interface ParseAdminIntf {

	/**
	 * 获取所有sample
	 * @return
	 */
	public List<Sample> getSamples();
	
	/**
	 * 获取所有sourceFile
	 * @return
	 */
	public List<SourceFile> getSourceFiles();
	
	/**
	 * 获取所有person信息
	 * @return
	 */
	public List<Person> getPersonContacts();
	
	/**
	 * 获取Sample相关信息
	 * @param sample
	 * @return
	 */
	public String[] getSampleinfos(Sample sample);
	
	/**
	 * 获取sourceFile相关信息
	 * @param sourceFile
	 * @return
	 */
	public String[] getSourFileInfo(SourceFile sourceFile);
	
	/**
	 * 获取contact相关信息
	 * @param contact
	 * @return
	 */
	public String[] getPersonInfos(Person person);
	
	/**
	 * 获取所有组织
	 * @return
	 */
	public List<Organization> getOrganizationContacts();
	
	/**
	 * 获取组织信息
	 * @param organization
	 * @return
	 */
	public String getOrganizationInfos(Organization organization);
	
}
