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
	public int insertPeptide(Comparable proteinIds) throws Exception;
	
	/**
	 * 插入光谱信息
	 * @param mzIdentMLUnmarshallerAdaptor
	 * @return
	 * @throws Exception 
	 */
	public int insertSpectrum(MzIdentMLUnmarshallerAdaptor mzIdentMLUnmarshallerAdaptor) throws Exception;
	
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
	 * @param peptide
	 * @param fileName
	 * @return 文件存储路径
	 * @throws TransformerException 
	 * @throws FileNotFoundException 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 */
	public String writeSpectrum(SpectrumIdentificationItem spectrumIdentificationItem,String fileName) throws FileNotFoundException, TransformerException, ParserConfigurationException, IOException;
}
