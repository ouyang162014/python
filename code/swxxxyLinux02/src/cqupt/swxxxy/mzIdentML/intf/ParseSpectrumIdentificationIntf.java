package cqupt.swxxxy.mzIdentML.intf;

import java.util.List;
import java.util.Map;

import psidev.psi.tools.xxindex.index.IndexElement;
import uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationItem;
import uk.ac.ebi.pride.utilities.data.core.FragmentIon;
import uk.ac.ebi.pride.utilities.data.core.Peptide;
import uk.ac.ebi.pride.utilities.data.core.SpectrumIdentification;
import uk.ac.ebi.pride.utilities.data.io.file.MzIdentMLUnmarshallerAdaptor;

/**
 * 实现对mzIdentMl文件
 * SpectrumIdentificationResult模块解析方法的定义
 * @author chao ouyang
 *
 */
public interface ParseSpectrumIdentificationIntf {
	
	/**
	 * 获取spectrum中cvParam相关参数
	 * @param spectrumIdentificationm
	 * @return
	 */
	public Map<String, String> getCvParams(SpectrumIdentification spectrumIdentificationm);
	
	/**
	 * 获取FragmentIon集合对象
	 * @param spectrumIdentification
	 * @return
	 */
	public List<FragmentIon> getFragmentationList(SpectrumIdentification spectrumIdentification);
	
	/**
	 * 获取spectrum mslevel
	 * @param SpectrumID
	 * @return
	 */
	public int getSpectrumMsLevel(Comparable SpectrumID);
	
	/**
	 * 判断是否为已验证的spectrum
	 * @param specId
	 * @return
	 */
	public boolean isIdentifiedSpectrum(Comparable specId);
	
	/**
	 * 获取所有intensity之和
	 * @param specId
	 * @return
	 */
	public double getSumOfIntensity(SpectrumIdentificationItem spectrumIdentificationItem);
	
	/**
	 * 获取波峰数
	 * @param specId
	 * @return
	 */
	public int getNumberOfSpectrumPeaks(SpectrumIdentificationItem spectrumIdentificationItem);
	
	/**
	 * 获取MzIdentMLUnmarshallerAdaptor
	 * @return
	 */
	public MzIdentMLUnmarshallerAdaptor getMzIdentMLUnmarshallerAdaptor();
	
	
}
