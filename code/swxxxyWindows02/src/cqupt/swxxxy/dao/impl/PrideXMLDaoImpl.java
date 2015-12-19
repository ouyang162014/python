package cqupt.swxxxy.dao.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import org.apache.log4j.Logger;

import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.CachedDataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.PrideXmlControllerImpl;
import uk.ac.ebi.pride.utilities.data.core.FragmentIon;
import uk.ac.ebi.pride.utilities.data.core.Peptide;
import uk.ac.ebi.pride.utilities.data.core.Protein;
import uk.ac.ebi.pride.utilities.data.core.Spectrum;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;
import cqupt.swxxxy.dao.BaseDao;
import cqupt.swxxxy.dao.Intf.PrideXMLDaoIntf;
import cqupt.swxxxy.prideXML.impl.ParseAdminImpl;
import cqupt.swxxxy.prideXML.impl.ParseCVLoopUpImpl;
import cqupt.swxxxy.prideXML.impl.ParseDataProcessingImpl;
import cqupt.swxxxy.prideXML.impl.ParseGelFreIdentificationImpl;
import cqupt.swxxxy.prideXML.impl.ParseProtocalImpl;
import cqupt.swxxxy.prideXML.impl.ParseSpectrumImpl;
import cqupt.swxxxy.prideXML.intf.ParseAdminIntf;
import cqupt.swxxxy.prideXML.intf.ParseCVLoopUpIntf;
import cqupt.swxxxy.prideXML.intf.ParseDataProcessingIntf;
import cqupt.swxxxy.prideXML.intf.ParseGelFreeIdentificationIntf;
import cqupt.swxxxy.prideXML.intf.ParseProtocalIntf;
import cqupt.swxxxy.prideXML.intf.ParseSpectrumIntf;

/**
 * 实现读取prideXML文件中的数据存储到数据库和相应文件中
 * 
 * @author chao ouyang
 * 
 */
public class PrideXMLDaoImpl extends BaseDao implements PrideXMLDaoIntf {
	// cvLookUp模块解析对象
	// ParseCVLoopUpIntf parseCVLoopUpIntf;
	// DataProcessing模块解析对象
	// ParseDataProcessingIntf parseDataProcessingIntf;
	// GelFreIdentification模块解析对象
	ParseGelFreeIdentificationIntf parseGelFreeIdentificationIntf;
	// Protocal模块解析对象
	// ParseProtocalIntf parseProtocalIntf;
	// Spectrum模块解析对象
	ParseSpectrumIntf parseSpectrumIntf;
	// Admin模块的解析
	// ParseAdminIntf parseAdminIntf;
	// 创建数据库连接对象
	Connection dataFileCon;
	Connection proteinCon;
	Connection peptideCon;
	Connection spectrumCon;
	Connection pepProPsmSubCon;
	PreparedStatement dataFilePs;
	PreparedStatement proteinPs;
	PreparedStatement peptidePs;
	PreparedStatement spectrumPs;
	PreparedStatement pepProPsmSubPs;
	ResultSet dataFileRs;
	ResultSet proteinRs;
	ResultSet peptideRs;
	ResultSet spectrumRs;
	ResultSet pepProPsmSubRs;
	// 读写的文件全路径名
	String fileName;
	// 文件名
	String subName;
	// 输出日志
	Logger log = Logger.getLogger(Logger.class);
	// 存蛋白质表主键
	Map<String, String> proteinKey = new HashMap<String, String>();
	// 保存肽段表主键
	Map<String, String> peptideKey = new HashMap<String, String>();
	// 保存光谱表主键
	Map<String, String> spectrumKey = new HashMap<String, String>();

	/**
	 * 
	 * @param fileName
	 *            上传文件名
	 */
	public PrideXMLDaoImpl(String fileName)  {
		File inputFile = new File(fileName);
		PrideXmlControllerImpl prideXmlControllerImpl = new PrideXmlControllerImpl(
				inputFile);
		if(prideXmlControllerImpl.isValidFormat(inputFile)){
			log.info("上传文件不合法");
			//return;
		}
		// parseCVLoopUpIntf = new
		// ParseCVLoopUpImpl(prideXmlControllerImpl);
		// parseDataProcessingIntf = new
		// ParseDataProcessingImpl(prideXmlControllerImpl);
		parseGelFreeIdentificationIntf = new ParseGelFreIdentificationImpl(
				prideXmlControllerImpl);
		// parseProtocalIntf = new
		// ParseProtocalImpl(prideXmlControllerImpl);
		parseSpectrumIntf = new ParseSpectrumImpl(prideXmlControllerImpl);
		// parseAdminIntf = new ParseAdminImpl(prideXmlControllerImpl);
		// 创建数据库连接对象
		dataFileCon = getConnection();
		proteinCon = getConnection();
		peptideCon = getConnection();
		spectrumCon = getConnection();
		pepProPsmSubCon = getConnection();
		// 使用事务，必须设置setAutoCommit false，表示手动提交
		try {
			proteinCon.setAutoCommit(false);
			peptideCon.setAutoCommit(false);
			spectrumCon.setAutoCommit(false);
			pepProPsmSubCon.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.fileName = fileName;
		// windows
		 subName=fileName.substring(fileName.lastIndexOf("\\")+1,
		 fileName.indexOf("."));
		// linux
		//subName = fileName.substring(fileName.lastIndexOf("/") + 1,
			//	fileName.indexOf("."));
	}

	public int insertProtein(Collection<Comparable> proteinIds)
			throws Exception {
		// 创建sql语句
		String sql = "insert into t_protein(protein,threshold,peptides,distinct_peptides,ptms) values(?,?,?,?,?)";
		// try {
		proteinPs = (PreparedStatement) proteinCon.prepareStatement(sql,
				Statement.RETURN_GENERATED_KEYS);
		for (Comparable proteinId : proteinIds) {
			// 根据ID获得相应蛋白
			Protein protein = parseGelFreeIdentificationIntf
					.getProteinById(proteinId);
			// coverage 暂时留空
			double coverage = protein.getSequenceCoverage();
			// accession
			String accession = parseGelFreeIdentificationIntf
					.getProteinAccession(proteinId);
			// threshold
			float threshold = (float) parseGelFreeIdentificationIntf
					.getProteinThreshold(proteinId);
			// peptides
			int peptides = protein.getPeptides().size();
			// distinctPeptides
			int distinctPeptides = parseGelFreeIdentificationIntf
					.getNumberOfUniquePeptides(proteinId);
			// ptms
			int ptms = parseGelFreeIdentificationIntf
					.getNumberOfPTMs(proteinId);

			// } catch (SQLException e) {
			// e.printStackTrace();
			// }
			// try {
			proteinPs.setString(1, accession);
			proteinPs.setFloat(2, threshold);
			proteinPs.setInt(3, peptides);
			proteinPs.setInt(4, distinctPeptides);
			proteinPs.setInt(5, ptms);
			// 设置批量插入
			proteinPs.addBatch();
		}
		proteinPs.executeBatch();
		log.info(proteinPs);

		// 提交
		// con.commit();
		// 获取自动生成的key值
		proteinRs = proteinPs.getGeneratedKeys();
		try {
			int i = 0;
			while (proteinRs.next()) {
				proteinKey.put(i + "", proteinRs.getLong(1) + "");
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int insertPeptide(Collection<Comparable> peptideIds,
			Comparable proteinId) throws Exception {
		// sql语句
		String sql = "insert into t_peptide(peptide,charge,precursor_m_z,modifications,lons,mascot_score,length,start,stop,psm,identification_id) values(?,?,?,?,?,?,?,?,?,?,?)";
		// log.info(sql);
		// try {
		peptidePs = (PreparedStatement) peptideCon.prepareStatement(sql,
				Statement.RETURN_GENERATED_KEYS);
		// 计数器
		int index = 0;
		for (Comparable peptideId : peptideIds) {
			Peptide peptide = parseGelFreeIdentificationIntf.getPeptideByIndex(
					proteinId, index);
			Comparable spectrumId = parseGelFreeIdentificationIntf
					.getPeptideSpectrumId(peptide);
			Spectrum spectrum = parseSpectrumIntf.getSpectrum(spectrumId);
			List<FragmentIon> fragmentIonList = parseGelFreeIdentificationIntf
					.getFragmentIonList(peptide);
			writeSpectrum(spectrum, getConf().get("windowsMspFilePath") + subName,
					fragmentIonList, peptide);
			// sequence
			String sequence = peptide.getSequence();
			// fit 留空
			String fit;
			// deltaMZ 留空
			float deltaMZ;
			// charge
			Map<String, String> additional = parseGelFreeIdentificationIntf
					.getPeptideAdditional(peptide);
			int charge = additional.get("charge state") != null ? Integer
					.parseInt(additional.get("charge state")) : 0;
			// precusorMZ
			int precusorMZ = Integer.parseInt(parseGelFreeIdentificationIntf
					.getPeptideSpectrumId(peptide).toString());
			// precusorm 留空
			float precusorm;
			// readablemod 留空
			String readablemod = null;
			// modifications 需修改关系表
			String modifications = parseGelFreeIdentificationIntf
					.getModifications(peptide);
			// missedcleave 留空
			int missedcleave = 0;
			// fragmentIon 的数量
			int lons = peptide.getFragmentation().size();
			// mascotScore
			float mascotScore = additional.get("Mascot Score") != null ? Float
					.parseFloat(additional.get("Mascot Score")) : 0;
			int start = parseGelFreeIdentificationIntf
					.getPeptideSequenceStart(peptide);
			int stop = parseGelFreeIdentificationIntf
					.getPeptideSequenceEnd(peptide);
			int length = stop - start + 1;
			// 获取序列相同的肽段数
			int pms = parseGelFreeIdentificationIntf.getNumberOfQuantPeptides(
					proteinId, index);
			// identificationId
			int identificationId = Integer.parseInt(proteinKey.get(
					proteinId.toString()).toString());
			// cuttingSize 留空
			int cuttingSize = 0;
			peptidePs.setString(1, sequence);
			peptidePs.setInt(2, charge);
			peptidePs.setInt(3, precusorMZ);
			peptidePs.setString(4, modifications);
			peptidePs.setInt(5, lons);
			peptidePs.setFloat(6, mascotScore);
			peptidePs.setInt(7, length);
			peptidePs.setInt(8, start);
			peptidePs.setInt(9, stop);
			peptidePs.setInt(10, pms);
			peptidePs.setInt(11, identificationId);
			peptidePs.addBatch();
			index++;
		}
		peptidePs.executeBatch();
		log.info(peptidePs);
		// 获得自动生成的主键
		peptideRs = peptidePs.getGeneratedKeys();
		int i = 0;
		while (peptideRs.next()) {
			// 保存所生成的主键
			peptideKey.put(proteinId + "" + i, peptideRs.getLong(1) + "");
			i++;
		}
		return 0;
	}

	public int insertSpectrum(Collection<Comparable> spectrumIds)
			throws Exception {
		String sql = "insert into t_spectrum(ms_level,identified,charge,precursor_m_z,sum_of_intensity,peaks,path) values(?,?,?,?,?,?,?)";
		// log.info(sql);
		spectrumPs = (PreparedStatement) spectrumCon.prepareStatement(sql,
				Statement.RETURN_GENERATED_KEYS);
		for (Comparable spectrumId : spectrumIds) {
			Spectrum spectrum = parseSpectrumIntf.getSpectrum(spectrumId);
			String path = writeSpectrum(spectrum, getConf().get("windowsMspFilePath")
					+ subName, null, null);
			// msLevel
			int msLevel = parseSpectrumIntf.getSpectrumMsLevel(spectrumId);
			// identified
			int identified = (parseSpectrumIntf
					.isIdentifiedSpectrum(spectrumId)) ? 1 : 0;
			Map<String, String> cvParams = parseSpectrumIntf
					.getPeakMZTimeChargeState(spectrum);
			// charge
			int charge = (cvParams.get("charge state") != null) ? Integer
					.parseInt(cvParams.get("charge state")) : 0;
			// precusorMZ
			double precusorMZ = (cvParams.get("selected ion m/z") != null) ? Double
					.parseDouble(cvParams.get("selected ion m/z")) : 0;
			// sumOfIntensity 所有电子强度之和
			double sumOfIntensity = parseSpectrumIntf
					.getSumOfIntensity(spectrumId);
			// peaks 获得波峰的个数
			int peaks = parseSpectrumIntf.getNumberOfSpectrumPeaks(spectrumId);
			spectrumPs.setInt(1, msLevel);
			spectrumPs.setInt(2, identified);
			spectrumPs.setInt(3, charge);
			spectrumPs.setDouble(4, precusorMZ);
			spectrumPs.setDouble(5, sumOfIntensity);
			spectrumPs.setInt(6, peaks);
			spectrumPs.setString(7, path);
			spectrumPs.addBatch();
			// log.info(spectrumPs);
		}
		spectrumPs.executeBatch();
		// 获得自动生成的主键
		spectrumRs = spectrumPs.getGeneratedKeys();
		int i = 1;
		while (spectrumRs.next()) {
			// 保存所生成的主键
			spectrumKey.put("" + i, spectrumRs.getLong(1) + "");
			i++;
		}
		return 0;
	}

	public int insertPepProPsmSub() throws Exception {
		int subid = 2;
		// 查询dataFile表
		
		 String dataFileSql="select subid from datafile df where df.filename like '%？%'";
		 dataFileSql=dataFileSql.replace("?", subName); //获取数据库连接
		 dataFilePs=(PreparedStatement)
		 dataFileCon.prepareStatement(dataFileSql); //查询
		 dataFileRs=dataFilePs.executeQuery(); while(dataFileRs.next()){
			 subid= dataFileRs.getInt("subid"); 
		 }
		 
		String sql = "insert into t_pep_pro_psm_sub(protein_id,peptide_id,spectrum_id,subid) values(?,?,?,?)";
		// log.info(sql);
		pepProPsmSubPs = (PreparedStatement) pepProPsmSubCon
				.prepareStatement(sql);
		// 迭代proteinKey集合
		Iterator proteinKeyIterator = proteinKey.keySet().iterator();
		while (proteinKeyIterator.hasNext()) {
			// 获取蛋白质在prideXML中的ID
			String proteinId = proteinKeyIterator.next().toString();
			// 获取蛋白质关系表的ID
			String proteinPrimaryKey = proteinKey.get(proteinId);
			// 获取当前蛋白质下的所有肽段序列
			int peptides = parseGelFreeIdentificationIntf
					.getProteinById(proteinId).getPeptides().size();
			for (int i = 0; i < peptides; i++) {
				// 获取肽段关系表中的ID
				String peptidePrimaryKey = peptideKey.get(proteinId + i);
				// 获取肽段
				//log.info(proteinId + "*************" + i);
				Peptide peptide = parseGelFreeIdentificationIntf
						.getPeptideByIndex(proteinId, i);
				// 获取肽段对应的光谱ID
				String SpectrunId = parseGelFreeIdentificationIntf
						.getSpectrumReference(peptide).toString();
				// 根据光谱ID获取光谱关系表中对应的ID
				String spectrumPrimaryKey = spectrumKey.get(SpectrunId);
				pepProPsmSubPs.setInt(1, Integer.parseInt(proteinPrimaryKey));
				pepProPsmSubPs.setInt(2, Integer.parseInt(peptidePrimaryKey));
				pepProPsmSubPs.setInt(3, Integer.parseInt(spectrumPrimaryKey));
				pepProPsmSubPs.setInt(4, subid);
				pepProPsmSubPs.addBatch();
				log.info(pepProPsmSubPs);
			}
		}
		pepProPsmSubPs.executeBatch();
		//log.info("#################################################");
		return 0;
	}

	public int insertAll() {
		// 获取所有蛋白质ID
		Collection<Comparable> proteinIds = parseGelFreeIdentificationIntf
				.getProteinIds();
		Collection<Comparable> spectrumIds = parseSpectrumIntf.getSpectrumIds();
		/*
		 * log.info("*****************" + proteinIds.size() +
		 * "**********************");
		 */
		try {
			insertProtein(proteinIds);
			// insertSpectrum(spectrumIds);
			for (Comparable proteinId : proteinIds) {
				// 获取蛋白质对应是所有肽段的ID
				Collection<Comparable> peptideIds = parseGelFreeIdentificationIntf
						.getPeptideIds(proteinId);
				//log.info("*****************" + peptideIds.size()
					//	+ "**********************");
				insertPeptide(peptideIds, proteinId);

			}
			insertSpectrum(spectrumIds);

			// 提交事物
			log.info("********************提交事物************************");
			proteinCon.commit();
			spectrumCon.commit();
			peptideCon.commit();

			insertPepProPsmSub();
			pepProPsmSubCon.commit();
			log.info("********************事物提交成功************************");
		} catch (Exception e) {
			try {
				log.info("捕获异常，插入数据回滚");
				// 数据回滚
				pepProPsmSubCon.rollback();
				spectrumCon.rollback();
				proteinCon.rollback();
				peptideCon.rollback();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			closeAll(proteinRs, proteinPs, proteinCon);
			closeAll(peptideRs, peptidePs, peptideCon);
			closeAll(spectrumRs, spectrumPs, spectrumCon);
			closeAll(pepProPsmSubRs, pepProPsmSubPs, pepProPsmSubCon);
		}
		return 0;
	}

	/**
	 * 向文件中写入spectrum信息
	 * 
	 * @param spectrum
	 *            传入的spectrum
	 * @param 文件名
	 * @param fragmentIonList
	 */
	public String writeSpectrum(Spectrum spectrum, String fileName,
			List<FragmentIon> fragmentIonList, Peptide peptide)
			throws TransformerException, ParserConfigurationException,
			IOException {
		// 获取spectrum ID
		String id = spectrum.getId() + "";
		// 获取原文件文件名
		StringBuilder spectrumName = new StringBuilder(fileName);
		// spectrum文件
		spectrumName.append("spectrum").append(id).append(".msp");
		// 创建文件
		File file = new File(spectrumName.toString());
		// 判断文件内容是否为空
		if (file.exists() && file.length() > 0) {
			return spectrumName.toString();
		}
		// 写入中文字符时解决中文乱码问题
		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
		BufferedWriter bw = new BufferedWriter(osw);
		// 获取解密后的mz数组
		double[] mz = parseSpectrumIntf.getMZArrayBinary(spectrum)[0];
		// 解密后的intensity数组
		double[] intensity = parseSpectrumIntf.getMZArrayBinary(spectrum)[1];
		// 判断fragmentIonList集合是否为空,不为空则将FragmentIon信息写入文件中
		if (fragmentIonList != null && fragmentIonList.size() > 0
				&& peptide != null) {
			// 创建map简历索引 key为mz value为fragmentIonList的index值
			Map<String, Integer> fragmentIonMap = new HashMap<String, Integer>();
			// 记录在fragmentIonList中的位置
			int theNumOfFragmentIon = 0;
			for (FragmentIon fragmentIon : fragmentIonList) {
				fragmentIonMap.put(fragmentIon.getMz() + "",
						theNumOfFragmentIon);
				theNumOfFragmentIon++;
			}
			// 写入sequence
			bw.write("NAME: " + (peptide.getSequence()).toUpperCase() + "/2"
					+ "\t\n");
			// 写入波峰个数
			bw.write("Num peaks: " + intensity.length + "\t\n");
			// 循环写入mz intensity 电子数和理论误差
			for (int i = 0; i < mz.length; i++) {
				if (fragmentIonMap.get(mz[i] + "") != null) {
					int index = fragmentIonMap.get(mz[i] + "");
					int yIon = fragmentIonList.get(index).getLocation();
					int charge = fragmentIonList.get(index).getCharge();
					double error = fragmentIonList.get(index).getMassError();
					bw.write(mz[i] + " " + intensity[i] + " \"" + "y" + yIon
							+ getAddNum(charge) + "/" + error + "\"" + "\t\n");
				} else {
					bw.write(mz[i] + " " + intensity[i] + "\t\n");
				}
			}
		} else {
			// 写入sequence
			bw.write("NAME: " + "\t\n");
			// 写入波峰个数
			bw.write("Num peaks: " + intensity.length + "\t\n");
			for (int i = 0; i < mz.length; i++) {
				bw.write(mz[i] + " " + intensity[i] + "\t\n");
			}
		}
		// 关闭流
		bw.close();
		osw.close();
		fos.close();
		return spectrumName.toString();
	}

	/**
	 * 获取正电荷数目
	 * 
	 * @param num
	 * @return
	 */
	public String getAddNum(int num) {
		StringBuilder sb = new StringBuilder();
		if (num > 0) {
			for (int i = 0; i < num; i++) {
				sb.append("+");
			}
			return sb.toString();
		} else {
			return "";
		}
	}

}