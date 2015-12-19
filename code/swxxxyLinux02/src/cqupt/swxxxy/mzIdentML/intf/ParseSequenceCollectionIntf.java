package cqupt.swxxxy.mzIdentML.intf;

import java.util.Collection;

import uk.ac.ebi.pride.utilities.data.core.Peptide;
import uk.ac.ebi.pride.utilities.data.core.PeptideEvidence;

/**
 * ʵ�ֶ�mzIdentMl�ļ�SequenceCollectionģ��Ľ���
 * @author Administrator
 *
 */
public interface ParseSequenceCollectionIntf {
	
	/**
	 * ��ȡ�Ķ���ʼ��
	 * @param peptide
	 * @return
	 */
	public int getStart(Peptide peptide);
	
	/**
	 * ��ȡ�Ķν�����
	 * @param peptide
	 * @return
	 */
	public int getEnd(Peptide peptide);
	
	/**
	 * ��ȡdBSequence_ref
	 * @param peptide
	 * @return
	 */
	public Comparable getDBSequence_ref(Peptide peptide);
	
	/**
	 * ��ȡ�Ķ�����
	 * @param peptide
	 * @return
	 */
	public String getPeptideSequence(Peptide peptide);
	
	/**
	 * ��ȡ�Ķ�������Ϣ
	 * @param peptide
	 * @return
	 */
	public String getModifications(Peptide peptide);
	
	/**
	 * ��ȡ��ͬ���Ķ���
	 * @param proteinId
	 * @return
	 */
	public int getNumberOfQuantPTMs(Comparable proteinId);
}
