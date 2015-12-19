package cqupt.swxxxy.test.mzIdentML;

import java.io.File;
import java.util.Collection;
import java.util.List;

import cqupt.swxxxy.mzIdentML.impl.ParseProteinDetectionListImpl;
import cqupt.swxxxy.mzIdentML.intf.ParseProteinDetectionListIntf;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.CachedDataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.MzIdentMLControllerImpl;
import uk.ac.ebi.pride.utilities.data.core.Peptide;
import uk.ac.ebi.pride.utilities.data.core.Protein;

public class ParseProteinDetectionListTest {

	public static void main(String[] args) {
		//�ļ�·��
		String filePath="D:\\mzIdentML\\mzidentml-example.mzid";
		//�����ļ�
		File file=new File(filePath);
		//����CachedDataAccessController����
		CachedDataAccessController cachedDataAccessController=new MzIdentMLControllerImpl(file,true);
		//����ParseProteinDetectionListģ���������
		ParseProteinDetectionListIntf parseProteinDetectionListIntf=new ParseProteinDetectionListImpl(cachedDataAccessController);
		//��ȡ���е�����ID
		Collection<Comparable> proteinList=parseProteinDetectionListIntf.getProteinIds();
		for(Comparable proteinId:proteinList){
			Protein protein=parseProteinDetectionListIntf.getProteinById(proteinId);
			List<Peptide> peptideList=protein.getPeptides();
			for(Peptide peptide:peptideList){
				Comparable spectrumId=peptide.getSpectrumIdentification().getId();
				//int j=1;
			}
			//int i=1;
		}
	}

}
