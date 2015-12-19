package cqupt.swxxxy.dao.Intf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import psidev.psi.tools.xxindex.index.IndexElement;
import uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationItem;
import uk.ac.ebi.pride.utilities.data.core.FragmentIon;
import uk.ac.ebi.pride.utilities.data.core.Peptide;
import uk.ac.ebi.pride.utilities.data.core.Spectrum;
import uk.ac.ebi.pride.utilities.data.core.SpectrumIdentification;
import uk.ac.ebi.pride.utilities.data.io.file.MzIdentMLUnmarshallerAdaptor;

public interface MzIdentMLDaoInt {
	/**
	 * ���뵰������Ϣ
	 * @param proteinId ������ID
	 * @return
	 * @throws Exception 
	 */
	public int insertProtein(Collection<Comparable> proteinIds) throws Exception;

	/**
	 * �����Ķ���Ϣ
	 * @param peptides
	 * @param proteinId
	 * @return
	 * @throws Exception 
	 */
	public int insertPeptide(Comparable proteinIds) throws Exception;
	
	/**
	 * ���������Ϣ
	 * @param mzIdentMLUnmarshallerAdaptor
	 * @return
	 * @throws Exception 
	 */
	public int insertSpectrum(MzIdentMLUnmarshallerAdaptor mzIdentMLUnmarshallerAdaptor) throws Exception;
	
	/**
	 * �����û���Ϣ
	 * @return
	 */
	public int insertAll();
	
	/**
	 * ��PepProPsmSub��ϵ���в�������
	 * @return
	 * @throws Exception 
	 */
	public int insertPepProPsmSub() throws Exception;
	
	/**
	 * ���ļ���д��spectrum��Ϣ
	 * @param peptide
	 * @param fileName
	 * @return �ļ��洢·��
	 * @throws TransformerException 
	 * @throws FileNotFoundException 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 */
	public String writeSpectrum(SpectrumIdentificationItem spectrumIdentificationItem,String fileName) throws FileNotFoundException, TransformerException, ParserConfigurationException, IOException;
}
