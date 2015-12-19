package cqupt.swxxxy.prideXML.intf;

import java.util.List;
import java.util.Map;

import uk.ac.ebi.pride.utilities.data.core.ParamGroup;

/**
 * 协议模块解析接口
 * @author 欧阳超
 *
 */
public interface ParseProtocalIntf {
	/**
	 * 获取协议名称
	 * @return protocolName
	 */
	public String getProtocolName();
	
	/**
	 * 获取协议步骤
	 * @return
	 */
	public List<ParamGroup> getProtocolSteps();
	
	/**
	 * 获取paramGroup的名称和值
	 * @param paramGroup
	 * @return
	 */
	public Map<String,String> getProtocolStepCvParam(ParamGroup paramGroup);
}
