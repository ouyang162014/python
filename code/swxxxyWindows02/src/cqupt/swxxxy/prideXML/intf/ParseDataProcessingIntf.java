package cqupt.swxxxy.prideXML.intf;

import java.util.List;
import java.util.Map;

import uk.ac.ebi.pride.utilities.data.core.CvParam;
import uk.ac.ebi.pride.utilities.data.core.DataProcessing;
import uk.ac.ebi.pride.utilities.data.core.ProcessingMethod;
import uk.ac.ebi.pride.utilities.data.core.Software;

/**
 * DataProcessing模块解析
 * @author Administrator
 *
 */
public interface ParseDataProcessingIntf {
	/**
	 * 获取软件
	 * @return
	 */
	public List<Software> getSoftwares();
	
	/**
	 * 获取Processings
	 * @return
	 */
	public List<DataProcessing> getDataProcessings();
	
	/**
	 * 获取所有ProcessingMethod
	 * @return
	 */
	public List<ProcessingMethod> getProcessingMethod(DataProcessing dataProcessing);
	
	/**
	 * 获取software的所有参数
	 * @param software
	 * @return
	 */
	public Map<String,String> getSoftwareParam(Software software);
	
	/**
	 * 获取CvParam所有参数
	 * @param CvParam
	 * @return
	 */
	public Map<String,String> getProcessingMethodCvParam(CvParam cvParam);
	
}
