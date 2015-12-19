package cqupt.swxxxy.prideXML.intf;

import java.util.List;

import uk.ac.ebi.pride.utilities.data.core.Contact;
import uk.ac.ebi.pride.utilities.data.core.Organization;
import uk.ac.ebi.pride.utilities.data.core.Person;
import uk.ac.ebi.pride.utilities.data.core.Sample;
import uk.ac.ebi.pride.utilities.data.core.SourceFile;

public interface ParseAdminIntf {

	/**
	 * ��ȡ����sample
	 * @return
	 */
	public List<Sample> getSamples();
	
	/**
	 * ��ȡ����sourceFile
	 * @return
	 */
	public List<SourceFile> getSourceFiles();
	
	/**
	 * ��ȡ����person��Ϣ
	 * @return
	 */
	public List<Person> getPersonContacts();
	
	/**
	 * ��ȡSample�����Ϣ
	 * @param sample
	 * @return
	 */
	public String[] getSampleinfos(Sample sample);
	
	/**
	 * ��ȡsourceFile�����Ϣ
	 * @param sourceFile
	 * @return
	 */
	public String[] getSourFileInfo(SourceFile sourceFile);
	
	/**
	 * ��ȡcontact�����Ϣ
	 * @param contact
	 * @return
	 */
	public String[] getPersonInfos(Person person);
	
	/**
	 * ��ȡ������֯
	 * @return
	 */
	public List<Organization> getOrganizationContacts();
	
	/**
	 * ��ȡ��֯��Ϣ
	 * @param organization
	 * @return
	 */
	public String getOrganizationInfos(Organization organization);
	
}
