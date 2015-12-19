package cqupt.swxxxy.prideXML.intf;

import java.util.List;
import java.util.Map;

import uk.ac.ebi.pride.utilities.data.core.ParamGroup;

/**
 * Э��ģ������ӿ�
 * @author ŷ����
 *
 */
public interface ParseProtocalIntf {
	/**
	 * ��ȡЭ������
	 * @return protocolName
	 */
	public String getProtocolName();
	
	/**
	 * ��ȡЭ�鲽��
	 * @return
	 */
	public List<ParamGroup> getProtocolSteps();
	
	/**
	 * ��ȡparamGroup�����ƺ�ֵ
	 * @param paramGroup
	 * @return
	 */
	public Map<String,String> getProtocolStepCvParam(ParamGroup paramGroup);
}
