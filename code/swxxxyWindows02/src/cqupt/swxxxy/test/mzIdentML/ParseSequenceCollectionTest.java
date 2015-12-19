package cqupt.swxxxy.test.mzIdentML;

import java.io.File;
import java.util.Collection;
import java.util.List;

import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.CachedDataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.MzIdentMLControllerImpl;
import uk.ac.ebi.pride.utilities.data.core.Peptide;
import uk.ac.ebi.pride.utilities.data.core.PeptideEvidence;
import uk.ac.ebi.pride.utilities.data.core.Protein;
import cqupt.swxxxy.mzIdentML.impl.ParseProteinDetectionListImpl;
import cqupt.swxxxy.mzIdentML.impl.ParseSequenceCollectionImpl;
import cqupt.swxxxy.mzIdentML.intf.ParseProteinDetectionListIntf;
import cqupt.swxxxy.mzIdentML.intf.ParseSequenceCollectionIntf;

public class ParseSequenceCollectionTest {
	
	
	public static void main(String[] args) {
		// �ļ�·��
		String filePath = "D:\\mzIdentML\\mzidentml-example.mzid";
		// �����ļ�
		File file = new File(filePath);
		// ����CachedDataAccessController����
		CachedDataAccessController cachedDataAccessController = new MzIdentMLControllerImpl(
				file, true);
		// ����ParseProteinDetectionListģ���������
		ParseProteinDetectionListIntf parseProteinDetectionListIntf = new ParseProteinDetectionListImpl(
				cachedDataAccessController);
		ParseSequenceCollectionIntf parseSequenceCollectionIntf=new ParseSequenceCollectionImpl(cachedDataAccessController);
		// ��ȡ���е�����ID
		Collection<Comparable> proteinList = parseProteinDetectionListIntf
				.getProteinIds();
		for (Comparable proteinId : proteinList) {
			//����ID��ȡ������
			Protein protein = parseProteinDetectionListIntf.getProteinById(proteinId);
			//���ݵ����ʻ�ȡ���е��Ķ�
			List<Peptide> peptideList = protein.getPeptides();
			for (Peptide peptide : peptideList) {
				//��ȡpeptideEvidenceId
				List<PeptideEvidence> peptideEvidenceList=peptide.getPeptideEvidenceList();
				for(PeptideEvidence peptideEvidence:peptideEvidenceList){
					Comparable evidenceId=peptideEvidence.getId();
					/*PeptideEvidence evidence=parseSequenceCollectionIntf.getPeptideEvidences(proteinId, evidenceId);
					for(PeptideEvidence evidence:EvidenceList){
						int start=evidence.getStartPosition();
						int stop=evidence.getEndPosition();
					}*/
					int i=1;
				}
			}
		}
	}
}
