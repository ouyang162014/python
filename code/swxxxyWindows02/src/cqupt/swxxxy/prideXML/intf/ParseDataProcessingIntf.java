package cqupt.swxxxy.prideXML.intf;

import java.util.List;
import java.util.Map;

import uk.ac.ebi.pride.utilities.data.core.CvParam;
import uk.ac.ebi.pride.utilities.data.core.DataProcessing;
import uk.ac.ebi.pride.utilities.data.core.ProcessingMethod;
import uk.ac.ebi.pride.utilities.data.core.Software;

/**
 * DataProcessingģ�����
 * @author Administrator
 *
 */
public interface ParseDataProcessingIntf {
	/**
	 * ��ȡ���
	 * @return
	 */
	public List<Software> getSoftwares();
	
	/**
	 * ��ȡProcessings
	 * @return
	 */
	public List<DataProcessing> getDataProcessings();
	
	/**
	 * ��ȡ����ProcessingMethod
	 * @return
	 */
	public List<ProcessingMethod> getProcessingMethod(DataProcessing dataProcessing);
	
	/**
	 * ��ȡsoftware�����в���
	 * @param software
	 * @return
	 */
	public Map<String,String> getSoftwareParam(Software software);
	
	/**
	 * ��ȡCvParam���в���
	 * @param CvParam
	 * @return
	 */
	public Map<String,String> getProcessingMethodCvParam(CvParam cvParam);
	
}
