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
		// 文件路径
		String filePath = "D:\\mzIdentML\\mzidentml-example.mzid";
		// 创建文件
		File file = new File(filePath);
		// 创建CachedDataAccessController对象
		CachedDataAccessController cachedDataAccessController = new MzIdentMLControllerImpl(
				file, true);
		// 创建ParseProteinDetectionList模块解析对象
		ParseProteinDetectionListIntf parseProteinDetectionListIntf = new ParseProteinDetectionListImpl(
				cachedDataAccessController);
		ParseSequenceCollectionIntf parseSequenceCollectionIntf=new ParseSequenceCollectionImpl(cachedDataAccessController);
		// 获取所有蛋白质ID
		Collection<Comparable> proteinList = parseProteinDetectionListIntf
				.getProteinIds();
		for (Comparable proteinId : proteinList) {
			//根据ID获取蛋白质
			Protein protein = parseProteinDetectionListIntf.getProteinById(proteinId);
			//根据蛋白质获取所有的肽段
			List<Peptide> peptideList = protein.getPeptides();
			for (Peptide peptide : peptideList) {
				//获取peptideEvidenceId
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
