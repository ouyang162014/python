package cqupt.swxxxy.mzIdentML.intf;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.pride.utilities.data.core.Peptide;
import uk.ac.ebi.pride.utilities.data.core.Protein;

/**
 * ʵ�ֶ�mzIdentMl��ʽ�ļ�ProteinDetectionListģ��Ľ���
 * @author Administrator
 *
 */
public interface ParseProteinDetectionListIntf {
	
	/**
	 * ��ȡmzIdentMl�ļ������е�����ID
	 * @return
	 */
	public Collection<Comparable> getProteinIds();
	
	/**
	 * ��ȡmzIdentML�ļ��еĵ�������Ϣ
	 * @param proteinId
	 * @return
	 */
	public Protein getProteinById(Comparable proteinId);
	
	/**
	 * ��ȡmascot:scorֵ����ͬ���Ķ���
	 * @param protein
	 * @return
	 */
	public Map<String,String> getMascotScorAndDistinctPeptides(Protein protein);
	
	/**
	 * ��ȡ�õ����ʵ�����peptide
	 * @param protein
	 * @return
	 */
	public List<Peptide> getPeptideList(Protein protein);
	
	/**
	 * ��ȡ�뵰������ص�PeptideEvidenceId
	 * @param peptide
	 * @return
	 */
	public Comparable getPeptideEvidenceId(Peptide peptide);
	
	/**
	 * ��ȡ��õ������Ķ���ص���ͼId
	 * @param peptide
	 * @return
	 */
	public Comparable getSpectrumIdentificationItem(Peptide peptide);
	
	/**
	 * ��ȡdinstinctPeptides
	 * @param proteinId
	 * @return
	 */
	public int getDistinctPeptides(Comparable proteinId);
	
	/**
	 * ��ȡptms������modification���εĸ���
	 * @param proteinId
	 * @return
	 */
	public int getNumberOfPTMs(Comparable proteinId);
		
}
