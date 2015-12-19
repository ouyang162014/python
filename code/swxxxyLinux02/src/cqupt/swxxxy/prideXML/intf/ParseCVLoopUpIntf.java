package cqupt.swxxxy.prideXML.intf;
import java.util.List;

import uk.ac.ebi.pride.utilities.data.core.CVLookup;

/**
 * cvLoopUpģ������ӿ�
 * @author Administrator
 *
 */
public interface ParseCVLoopUpIntf {
	/**
	 * ��ȡcvlookup����
	 * @return 
	 */
	public List<CVLookup> getCvLookups();
	
	/**
	 * ��ȡ��ǩ
	 * @param cVLookup
	 * @return
	 */
	public String getCvLookupLable(CVLookup cVLookup);
	
	/**
	 * ��ȡȫ��
	 * @param cVLookup
	 * @return
	 */
	public String getCvLookupFullName(CVLookup cVLookup);
	
	/**
	 * ��ȡ�汾
	 * @param cVLookup
	 * @return
	 */
	public String getCvLookVersion(CVLookup cVLookup);
	
	/**
	 * ��ȡ��ַ
	 * @param cVLookup
	 * @return
	 */
	public String getCvLookupAddress(CVLookup cVLookup);
}
