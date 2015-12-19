package cqupt.swxxxy.prideXML.intf;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.pride.utilities.data.core.Peptide;
import uk.ac.ebi.pride.utilities.data.core.Spectrum;

/**
 * prideXML文件解析接口
 * @author chao ouyang
 *
 */
public interface ParseSpectrumIntf {
	
	/**
	 * 读取Spectrum信息
	 * @return List<Spectrum>
	 */
	public List<Spectrum> getSpectrums();
	
	
	/**
	 * 获取SpectrumIds
	 * @return Collection<Comparable> 
	 */
	public Collection<Comparable> getSpectrumIds();

	/**
	 * 解析mzArrayBinary加密序列 
	 * @param Spectrum
	 * @return 解析后序列
	 */
	public double[][] getMZArrayBinary(Spectrum spectrum);
	
	/**
	 * 获取波峰，m/z，time，电荷数
	 * @param Spectrum
	 * @return  波峰，m/z，time，电荷数
	 */
	public Map<String,String> getPeakMZTimeChargeState(Spectrum spectrum);

	/**
	 * 获取lev
	 * @param comparable
	 * @return lev
	 */
	public int getSpectrumMsLevel(Comparable comparable);
	
	/**
	 * 获取质核比范围
	 * @param spectrum
	 * @return
	 */
	public Map<String,String> getMzRange(Spectrum spectrum);
	
	/**
	 * 获取数量
	 * @param spectrum
	 * @return
	 */
	public int getCount(Spectrum spectrum);
	
	/**
	 * 根据spectrumId获取Spectrum
	 * @param spectrumId
	 * @return
	 */
	public Spectrum getSpectrum(Comparable spectrumId);
	
	/**
	 * 获得所有电子强度之和
	 * @param specId
	 * @return
	 */
	public double getSumOfIntensity(Comparable specId);
	
	/**
	 * 获得波峰的个数
	 * @param specId
	 * @return
	 */
	public int getNumberOfSpectrumPeaks(Comparable specId);
	
	/**
	 * 是否为已定义的光谱
	 * @param specId
	 * @return
	 */
	public boolean isIdentifiedSpectrum(Comparable specId);
	
}
