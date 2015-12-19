package cqupt.swxxxy.mzIdentML.intf;

import java.util.Collection;

import uk.ac.ebi.pride.utilities.data.core.Peptide;
import uk.ac.ebi.pride.utilities.data.core.PeptideEvidence;

/**
 * 实现对mzIdentMl文件SequenceCollection模块的解析
 * @author Administrator
 *
 */
public interface ParseSequenceCollectionIntf {
	
	/**
	 * 获取肽段起始点
	 * @param peptide
	 * @return
	 */
	public int getStart(Peptide peptide);
	
	/**
	 * 获取肽段结束点
	 * @param peptide
	 * @return
	 */
	public int getEnd(Peptide peptide);
	
	/**
	 * 获取dBSequence_ref
	 * @param peptide
	 * @return
	 */
	public Comparable getDBSequence_ref(Peptide peptide);
	
	/**
	 * 获取肽段序列
	 * @param peptide
	 * @return
	 */
	public String getPeptideSequence(Peptide peptide);
	
	/**
	 * 获取肽段修饰信息
	 * @param peptide
	 * @return
	 */
	public String getModifications(Peptide peptide);
	
	/**
	 * 获取相同的肽段数
	 * @param proteinId
	 * @return
	 */
	public int getNumberOfQuantPTMs(Comparable proteinId);
}
