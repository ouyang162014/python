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
	 * 插入蛋白质信息
	 * @param proteinId 蛋白质ID
	 * @return
	 * @throws Exception 
	 */
	public int insertProtein(Collection<Comparable> proteinIds) throws Exception;

	/**
	 * 插入肽段信息
	 * @param peptides
	 * @param proteinId
	 * @return
	 * @throws Exception 
	 */
	public int insertPeptide(Collection<Comparable> peptides,Comparable proteinIds) throws Exception;
	
	/**
	 * 插入光谱信息
	 * @param spectrumIds
	 * @return
	 * @throws Exception 
	 */
	public int insertSpectrum(Collection<Comparable> spectrumIds) throws Exception;
	
	/**
	 * 插入用户信息
	 * @return
	 */
	public int insertAll();
	
	/**
	 * 向PepProPsmSub关系表中插入数据
	 * @return
	 * @throws Exception 
	 */
	public int insertPepProPsmSub() throws Exception;
	
	/**
	 * 向文件中写入spectrum信息
	 * @param spectrum
	 * @param fileName
	 * @return 文件存储路径
	 * @throws TransformerException 
	 * @throws FileNotFoundException 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 */
	public String writeSpectrum(Spectrum spectrum,String fileName,List<FragmentIon> fragmentIonList,Peptide peptide) throws FileNotFoundException, TransformerException, ParserConfigurationException, IOException;
}
