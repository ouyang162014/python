package cqupt.swxxxy.mzIdentML.intf;

import java.util.List;
import java.util.Map;

import psidev.psi.tools.xxindex.index.IndexElement;
import uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationItem;
import uk.ac.ebi.pride.utilities.data.core.FragmentIon;
import uk.ac.ebi.pride.utilities.data.core.Peptide;
import uk.ac.ebi.pride.utilities.data.core.SpectrumIdentification;
import uk.ac.ebi.pride.utilities.data.io.file.MzIdentMLUnmarshallerAdaptor;

/**
 * ʵ�ֶ�mzIdentMl�ļ�
 * SpectrumIdentificationResultģ����������Ķ���
 * @author chao ouyang
 *
 */
public interface ParseSpectrumIdentificationIntf {
	
	/**
	 * ��ȡspectrum��cvParam��ز���
	 * @param spectrumIdentificationm
	 * @return
	 */
	public Map<String, String> getCvParams(SpectrumIdentification spectrumIdentificationm);
	
	/**
	 * ��ȡFragmentIon���϶���
	 * @param spectrumIdentification
	 * @return
	 */
	public List<FragmentIon> getFragmentationList(SpectrumIdentification spectrumIdentification);
	
	/**
	 * ��ȡspectrum mslevel
	 * @param SpectrumID
	 * @return
	 */
	public int getSpectrumMsLevel(Comparable SpectrumID);
	
	/**
	 * �ж��Ƿ�Ϊ����֤��spectrum
	 * @param specId
	 * @return
	 */
	public boolean isIdentifiedSpectrum(Comparable specId);
	
	/**
	 * ��ȡ����intensity֮��
	 * @param specId
	 * @return
	 */
	public double getSumOfIntensity(SpectrumIdentificationItem spectrumIdentificationItem);
	
	/**
	 * ��ȡ������
	 * @param specId
	 * @return
	 */
	public int getNumberOfSpectrumPeaks(SpectrumIdentificationItem spectrumIdentificationItem);
	
	/**
	 * ��ȡMzIdentMLUnmarshallerAdaptor
	 * @return
	 */
	public MzIdentMLUnmarshallerAdaptor getMzIdentMLUnmarshallerAdaptor();
	
	
}
