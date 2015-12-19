package cqupt.swxxxy.mzIdentML.intf;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.pride.utilities.data.core.Peptide;
import uk.ac.ebi.pride.utilities.data.core.Protein;

/**
 * 实现对mzIdentMl格式文件ProteinDetectionList模块的解析
 * @author Administrator
 *
 */
public interface ParseProteinDetectionListIntf {
	
	/**
	 * 获取mzIdentMl文件中所有蛋白质ID
	 * @return
	 */
	public Collection<Comparable> getProteinIds();
	
	/**
	 * 获取mzIdentML文件中的蛋白质信息
	 * @param proteinId
	 * @return
	 */
	public Protein getProteinById(Comparable proteinId);
	
	/**
	 * 获取mascot:scor值及不同的肽段数
	 * @param protein
	 * @return
	 */
	public Map<String,String> getMascotScorAndDistinctPeptides(Protein protein);
	
	/**
	 * 获取该蛋白质的所有peptide
	 * @param protein
	 * @return
	 */
	public List<Peptide> getPeptideList(Protein protein);
	
	/**
	 * 获取与蛋白质相关的PeptideEvidenceId
	 * @param peptide
	 * @return
	 */
	public Comparable getPeptideEvidenceId(Peptide peptide);
	
	/**
	 * 获取与该蛋白质肽段相关的谱图Id
	 * @param peptide
	 * @return
	 */
	public Comparable getSpectrumIdentificationItem(Peptide peptide);
	
	/**
	 * 获取dinstinctPeptides
	 * @param proteinId
	 * @return
	 */
	public int getDistinctPeptides(Comparable proteinId);
	
	/**
	 * 获取ptms数：有modification修饰的个数
	 * @param proteinId
	 * @return
	 */
	public int getNumberOfPTMs(Comparable proteinId);
		
}
