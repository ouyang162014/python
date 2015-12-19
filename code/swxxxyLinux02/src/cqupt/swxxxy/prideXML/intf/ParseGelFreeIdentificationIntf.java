package cqupt.swxxxy.prideXML.intf;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.pride.utilities.data.core.FragmentIon;
import uk.ac.ebi.pride.utilities.data.core.Peptide;
import uk.ac.ebi.pride.utilities.data.core.Protein;

/**
 * GelFreeIdentification模块
 * 实现模块内数据的读取
 * @author chao ouayang
 *
 */
public interface ParseGelFreeIdentificationIntf {
	/**
	 * 获取所有proteinIds
	 * @return pride.xml文件中所有proteinId
	 * proteinId为protein在文件中的排序序号
	 */
	public Collection<Comparable> getProteinIds();
	
	/**
	 * 根据获取ProteinId获取protein相关信息
	 * @param proteinId 
	 * @return protein
	 */
	public Protein getProteinById(Comparable proteinId);
	
	/**
	 * 获取蛋白的登记入册
	 * @param proteinId
	 * @return protein
	 */
	public String getProteinAccession(Comparable proteinId);
	
	/**
	 * 获取搜索的数据库名称
	 * @param proteinId
	 * @return proteinName
	 */
	public String getDatabase(Comparable proteinId);
	
	/**
	 * 获取数据库版本
	 * @param proteinId
	 * @return DatabaseVersion
	 */
	public String getDatabaseVersion(Comparable proteinId);
	
	/**
	 * 根据蛋白ID获取肽段ID
	 * @param proteinId
	 * @return
	 */
	public Collection<Comparable> getPeptideIds(Comparable proteinId);
	
	/**
	 * 获取蛋白质分数
	 * @param proteinId
	 * @return
	 */
	public double getProteinScore(Comparable proteinId);
	
	/**
	 * 获取蛋白质阀值
	 * @param proteinId
	 * @return
	 */
	public double getProteinThreshold(Comparable proteinId);
	
	/**
	 * 获取蛋白的检索机构
	 * @param proteinId
	 * @return
	 */
	public String getProteinSearchEngine(Comparable proteinId);
	
	/**
	 * 根据ID和下标获取肽段
	 * @param proteinId
	 * @param index
	 * @return
	 */
	public Peptide getPeptideByIndex(Comparable proteinId,Comparable index);
	
	/**
	 * 获取肽段序列
	 * @param peptide
	 * @return
	 */
	public String getPeptideSequence(Peptide peptide);
	
	/**
	 * 获取肽段开始点
	 * @param peptide
	 * @return
	 */
	public int getPeptideSequenceStart(Peptide peptide);
	
	/**
	 * 获取肽段结束点
	 * @param peptide
	 * @return
	 */
	public int getPeptideSequenceEnd(Peptide peptide);
	
	/**
	 * 获取肽段关联的谱图ID
	 * @param peptide
	 * @return
	 */
	public Comparable getPeptideSpectrumId(Peptide peptide);
	
	/**
	 * 获得碎片离子集
	 * @param peptide
	 * @return
	 */
	public List<FragmentIon> getFragmentIonList(Peptide peptide);
	
	/**
	 * 获取参数集合
	 * @param fragmentIon
	 * @return
	 */
	public Map<String,Double> getCVParam(FragmentIon fragmentIon);
	
	/**
	 * 获取肽段其它信息
	 * @param peptide
	 * @return
	 */
	public Map<String,String> getPeptideAdditional(Peptide peptide);
	
	/**
	 * 获取蛋白质的其它信息
	 * @param protein
	 * @return
	 */
	public Map<String,String> getProteinAdditional(Protein protein);
	
	/**
	 * 获取蛋白质所有肽段数目
	 * @param proteinId
	 * @return
	 */
	public int getNumberOfPeptides(Comparable proteinId);
	
	/**
	 * 获取蛋白质不同的肽段数
	 * @param proteinId
	 * @return
	 */
	public int getNumberOfUniquePeptides(Comparable proteinId);
	
	/**
	 * 获取蛋白质所有的ptm数量
	 * @param proteinId
	 * @return
	 */
	public int getNumberOfPTMs(Comparable proteinId);
	
	/**
	 * 获取序列相同的肽段数
	 * @param proteinId
	 * @param peptideId
	 * @return
	 */
	public int getNumberOfQuantPeptides(Comparable proteinId,Comparable peptideId);
	
	/**
	 * 获取peptide相关的spectrumid
	 * @param peptide
	 * @return
	 */
	public Comparable getSpectrumReference(Peptide peptide);
	
	/**
	 * 由peptide获取modifications系列属性值
	 * @param peptide
	 * @return
	 */
	public String getModifications(Peptide peptide);
}
