package cqupt.swxxxy.dao.Intf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import uk.ac.ebi.pride.utilities.data.core.FragmentIon;
import uk.ac.ebi.pride.utilities.data.core.Peptide;
import uk.ac.ebi.pride.utilities.data.core.Spectrum;

public interface PrideXMLDaoIntf {
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
	public int insertPeptide(Collection<Comparable> peptides,Comparable proteinIds) throws Exception;
	
	/**
	 * ���������Ϣ
	 * @param spectrumIds
	 * @return
	 * @throws Exception 
	 */
	public int insertSpectrum(Collection<Comparable> spectrumIds) throws Exception;
	
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
	 * @param spectrum
	 * @param fileName
	 * @return �ļ��洢·��
	 * @throws TransformerException 
	 * @throws FileNotFoundException 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 */
	public String writeSpectrum(Spectrum spectrum,String fileName,List<FragmentIon> fragmentIonList,Peptide peptide) throws FileNotFoundException, TransformerException, ParserConfigurationException, IOException;
}
