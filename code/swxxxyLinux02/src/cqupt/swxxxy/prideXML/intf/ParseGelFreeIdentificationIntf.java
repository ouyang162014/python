package cqupt.swxxxy.prideXML.intf;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.pride.utilities.data.core.FragmentIon;
import uk.ac.ebi.pride.utilities.data.core.Peptide;
import uk.ac.ebi.pride.utilities.data.core.Protein;

/**
 * GelFreeIdentificationģ��
 * ʵ��ģ�������ݵĶ�ȡ
 * @author chao ouayang
 *
 */
public interface ParseGelFreeIdentificationIntf {
	/**
	 * ��ȡ����proteinIds
	 * @return pride.xml�ļ�������proteinId
	 * proteinIdΪprotein���ļ��е��������
	 */
	public Collection<Comparable> getProteinIds();
	
	/**
	 * ���ݻ�ȡProteinId��ȡprotein�����Ϣ
	 * @param proteinId 
	 * @return protein
	 */
	public Protein getProteinById(Comparable proteinId);
	
	/**
	 * ��ȡ���׵ĵǼ����
	 * @param proteinId
	 * @return protein
	 */
	public String getProteinAccession(Comparable proteinId);
	
	/**
	 * ��ȡ���������ݿ�����
	 * @param proteinId
	 * @return proteinName
	 */
	public String getDatabase(Comparable proteinId);
	
	/**
	 * ��ȡ���ݿ�汾
	 * @param proteinId
	 * @return DatabaseVersion
	 */
	public String getDatabaseVersion(Comparable proteinId);
	
	/**
	 * ���ݵ���ID��ȡ�Ķ�ID
	 * @param proteinId
	 * @return
	 */
	public Collection<Comparable> getPeptideIds(Comparable proteinId);
	
	/**
	 * ��ȡ�����ʷ���
	 * @param proteinId
	 * @return
	 */
	public double getProteinScore(Comparable proteinId);
	
	/**
	 * ��ȡ�����ʷ�ֵ
	 * @param proteinId
	 * @return
	 */
	public double getProteinThreshold(Comparable proteinId);
	
	/**
	 * ��ȡ���׵ļ�������
	 * @param proteinId
	 * @return
	 */
	public String getProteinSearchEngine(Comparable proteinId);
	
	/**
	 * ����ID���±��ȡ�Ķ�
	 * @param proteinId
	 * @param index
	 * @return
	 */
	public Peptide getPeptideByIndex(Comparable proteinId,Comparable index);
	
	/**
	 * ��ȡ�Ķ�����
	 * @param peptide
	 * @return
	 */
	public String getPeptideSequence(Peptide peptide);
	
	/**
	 * ��ȡ�Ķο�ʼ��
	 * @param peptide
	 * @return
	 */
	public int getPeptideSequenceStart(Peptide peptide);
	
	/**
	 * ��ȡ�Ķν�����
	 * @param peptide
	 * @return
	 */
	public int getPeptideSequenceEnd(Peptide peptide);
	
	/**
	 * ��ȡ�Ķι�������ͼID
	 * @param peptide
	 * @return
	 */
	public Comparable getPeptideSpectrumId(Peptide peptide);
	
	/**
	 * �����Ƭ���Ӽ�
	 * @param peptide
	 * @return
	 */
	public List<FragmentIon> getFragmentIonList(Peptide peptide);
	
	/**
	 * ��ȡ��������
	 * @param fragmentIon
	 * @return
	 */
	public Map<String,Double> getCVParam(FragmentIon fragmentIon);
	
	/**
	 * ��ȡ�Ķ�������Ϣ
	 * @param peptide
	 * @return
	 */
	public Map<String,String> getPeptideAdditional(Peptide peptide);
	
	/**
	 * ��ȡ�����ʵ�������Ϣ
	 * @param protein
	 * @return
	 */
	public Map<String,String> getProteinAdditional(Protein protein);
	
	/**
	 * ��ȡ�����������Ķ���Ŀ
	 * @param proteinId
	 * @return
	 */
	public int getNumberOfPeptides(Comparable proteinId);
	
	/**
	 * ��ȡ�����ʲ�ͬ���Ķ���
	 * @param proteinId
	 * @return
	 */
	public int getNumberOfUniquePeptides(Comparable proteinId);
	
	/**
	 * ��ȡ���������е�ptm����
	 * @param proteinId
	 * @return
	 */
	public int getNumberOfPTMs(Comparable proteinId);
	
	/**
	 * ��ȡ������ͬ���Ķ���
	 * @param proteinId
	 * @param peptideId
	 * @return
	 */
	public int getNumberOfQuantPeptides(Comparable proteinId,Comparable peptideId);
	
	/**
	 * ��ȡpeptide��ص�spectrumid
	 * @param peptide
	 * @return
	 */
	public Comparable getSpectrumReference(Peptide peptide);
	
	/**
	 * ��peptide��ȡmodificationsϵ������ֵ
	 * @param peptide
	 * @return
	 */
	public String getModifications(Peptide peptide);
}
