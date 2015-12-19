package cqupt.swxxxy.prideXML.intf;
import java.util.List;

import uk.ac.ebi.pride.utilities.data.core.CVLookup;

/**
 * cvLoopUp模块解析接口
 * @author Administrator
 *
 */
public interface ParseCVLoopUpIntf {
	/**
	 * 获取cvlookup集合
	 * @return 
	 */
	public List<CVLookup> getCvLookups();
	
	/**
	 * 获取标签
	 * @param cVLookup
	 * @return
	 */
	public String getCvLookupLable(CVLookup cVLookup);
	
	/**
	 * 获取全名
	 * @param cVLookup
	 * @return
	 */
	public String getCvLookupFullName(CVLookup cVLookup);
	
	/**
	 * 获取版本
	 * @param cVLookup
	 * @return
	 */
	public String getCvLookVersion(CVLookup cVLookup);
	
	/**
	 * 获取地址
	 * @param cVLookup
	 * @return
	 */
	public String getCvLookupAddress(CVLookup cVLookup);
}
