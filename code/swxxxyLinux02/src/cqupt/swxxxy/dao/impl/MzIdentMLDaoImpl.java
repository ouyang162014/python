package cqupt.swxxxy.dao.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cqupt.swxxxy.utils.Tools;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;

import psidev.psi.tools.xxindex.index.IndexElement;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import uk.ac.ebi.jmzidml.model.mzidml.IonType;
import uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationItem;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.MzIdentMLControllerImpl;
import uk.ac.ebi.pride.utilities.data.core.Peptide;
import uk.ac.ebi.pride.utilities.data.core.Protein;
import uk.ac.ebi.pride.utilities.data.core.SpectrumIdentification;
import uk.ac.ebi.pride.utilities.data.io.file.MzIdentMLUnmarshallerAdaptor;
import cqupt.swxxxy.dao.BaseDao;
import cqupt.swxxxy.dao.Intf.MzIdentMLDaoInt;
import cqupt.swxxxy.mzIdentML.impl.ParseProteinDetectionListImpl;
import cqupt.swxxxy.mzIdentML.impl.ParseSequenceCollectionImpl;
import cqupt.swxxxy.mzIdentML.impl.ParseSpectrumIdentificationImpl;
import cqupt.swxxxy.mzIdentML.intf.ParseProteinDetectionListIntf;
import cqupt.swxxxy.mzIdentML.intf.ParseSequenceCollectionIntf;
import cqupt.swxxxy.mzIdentML.intf.ParseSpectrumIdentificationIntf;

public class MzIdentMLDaoImpl extends BaseDao implements MzIdentMLDaoInt {

	// 创建ParseProteinDetectionList模块解析对象
	ParseProteinDetectionListIntf parseProteinDetectionListIntf;
	// 创建ParseSequenceCollectionIntf解析对象
	ParseSequenceCollectionIntf parseSequenceCollectionIntf;
	// 创建ParseSpectrumIdentification模块解析对象
	ParseSpectrumIdentificationIntf parseSpectrumIdentificationIntf;
	// 创建对象MzIdentMLControllerImpl实现对mzIdentMl文件解析
	MzIdentMLControllerImpl mzIdentMLControllerImpl; 
	// 创建数据库链接对象
	// datafile表连接对象
	Connection dataFileCon;
	// t_protein表连接对象
	Connection proteinCon;
	// t_peptide表连接对象
	Connection peptideCon;
	// t-spectrum表连接对象
	Connection spectrumCon;
	// t_pep_pro_psm_sub表连接对象
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
	// 输出日志
	Logger log = Logger.getLogger(Logger.class);

	// 文件全路径名
	String filePathName;
	// 文件名
	String fileName;

	// 存蛋白质id与主键对应关系
	Map<String, String> proteinIdToKey = new HashMap<String, String>();
	// 保存肽段id与主键对应关系
	Map<String, String> peptideIdToKey = new HashMap<String, String>();
	// 保存谱图id与主键对应关系
	Map<String, String> spectrumIdToKey = new HashMap<String, String>();
	// 保存蛋白质与谱图对应关系
	Map<String, String> proteinToSpectrum = new HashMap<String, String>();
	// 保存光谱 顺序序号与id对应关系
	List<String> spectrumNumToId = new ArrayList<String>();
	// 保存蛋白质 顺序序号与id对应关系
	List<String> proteinNumToId = new ArrayList<String>();

	public MzIdentMLDaoImpl(String inputFilePathName) {
		// 创建读取数据文件对象
		File inputFile = new File(inputFilePathName);
		log.info(inputFile);
		mzIdentMLControllerImpl= new MzIdentMLControllerImpl(
				inputFile);
		//判断文件是否正确
		if(!mzIdentMLControllerImpl.isValidFormat(inputFile)){
			log.info("文件不符合要求");
			//return;
		}
		// 初始化各模块解析对象
		// 初始化ParseProteinDetectionList模块解析对象
		parseProteinDetectionListIntf = new ParseProteinDetectionListImpl(
				mzIdentMLControllerImpl);
		// 初始化ParseSequenceCollectionIntf解析对象
		parseSequenceCollectionIntf = new ParseSequenceCollectionImpl(
				mzIdentMLControllerImpl);
		// 初始化ParseSpectrumIdentification模块解析对象
		parseSpectrumIdentificationIntf = new ParseSpectrumIdentificationImpl(
				mzIdentMLControllerImpl);

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
		this.filePathName = filePathName;
		// windows
		// subName=fileName.substring(fileName.lastIndexOf("\\")+1,
		// fileName.indexOf("."));
		// linux
		this.fileName = inputFilePathName.substring(
				inputFilePathName.lastIndexOf("/") + 1, inputFilePathName.indexOf("."));
	}

	@Override
	public int insertProtein(Collection<Comparable> proteinIds)
			throws Exception {
		// 创建sql语句
		String sql = "insert into t_protein(protein,threshold,peptides,distinct_peptides,ptms) values(?,?,?,?,?)";
		// try {
		proteinPs = (PreparedStatement) proteinCon.prepareStatement(sql,
				Statement.RETURN_GENERATED_KEYS);
		for (Comparable proteinId : proteinIds) {
			proteinNumToId.add(proteinId.toString());
			// 根据ID获得相应蛋白
			Protein protein = parseProteinDetectionListIntf
					.getProteinById(proteinId);
			// coverage 暂时留空
			double coverage = protein.getSequenceCoverage();
			// threshold
			float threshold = (float) protein.getThreshold();
			// peptides
			int peptides = protein.getPeptides().size();
			// distinctPeptides
			int distinctPeptides = parseProteinDetectionListIntf
					.getDistinctPeptides(proteinId);
			// ptms
			int ptms = parseProteinDetectionListIntf.getNumberOfPTMs(proteinId);
			proteinPs.setString(1, proteinId.toString());
			proteinPs.setFloat(2, threshold);
			proteinPs.setInt(3, peptides);
			proteinPs.setInt(4, distinctPeptides);
			proteinPs.setInt(5, ptms);
			// 设置批量插入
			proteinPs.addBatch();
			// 输出日志
			log.info("**********" + proteinPs + "***********");

		}
		proteinPs.executeBatch();
		// 获取自动生成的key值
		proteinRs = proteinPs.getGeneratedKeys();
		// 计数器
		Integer numOfProteinId = 0;
		while (proteinRs.next()) {
			// 将自动生成的主键存入集合
			proteinIdToKey.put(proteinNumToId.get(numOfProteinId++),
					proteinRs.getLong(1) + "");
		}
		log.info(proteinPs);

		return 0;
	}

	@Override
	public int insertPeptide(Comparable proteinId) throws Exception {
		// sql语句
		String sql = "insert into t_peptide(peptide,charge,precursor_m_z,modifications,lons,mascot_score,length,start,stop,psm,identification_id) values(?,?,?,?,?,?,?,?,?,?,?)";
		peptidePs = (PreparedStatement) peptideCon.prepareStatement(sql,
				Statement.RETURN_GENERATED_KEYS);
		// 根据proteinId获取protein
		Protein protein = parseProteinDetectionListIntf
				.getProteinById(proteinId);
		// 获取peptide集合
		List<Peptide> peptideList = protein.getPeptides();
		// 计数器
		int num = 0;
		for (Peptide peptide : peptideList) {
			// 获取SpectrumIdentification并写入文件
			SpectrumIdentification spectrumIdentification = peptide
					.getSpectrumIdentification();
			// sequence
			String sequence = peptide.getSequence();
			// fit 留空
			String fit;
			// deltaMZ 留空
			float deltaMZ;
			// charge
			int charge = spectrumIdentification.getChargeState();
			// precusorMZ
			double precusorMZ = spectrumIdentification
					.getExperimentalMassToCharge();
			// precusorm 留空
			float precusorm;
			// readablemod 留空
			String readablemod = null;
			// modifications 需修改关系表
			String modifications = parseSequenceCollectionIntf
					.getModifications(peptide);
			// missedcleave 留空
			int missedcleave = 0;
			// fragmentIon 的数量
			int lons = peptide.getFragmentation().size();
			// mascotScore
			String cvParams = parseSpectrumIdentificationIntf.getCvParams(
					spectrumIdentification).get("mascot:score");
			//获取mascotScore
			float mascotScore = (cvParams != null ? Float.parseFloat(cvParams)
					: 0);
			// 开始点
			int start = peptide.getPeptideEvidence().getStartPosition();
			// 结束点
			int stop = peptide.getPeptideEvidence().getEndPosition();
			// 长度
			int length = stop - start + 1;
			// 获取序列相同的肽段数
			int pms = parseSequenceCollectionIntf
					.getNumberOfQuantPTMs(proteinId);
			// identificationId
			int identificationId = Integer.parseInt(proteinIdToKey
					.get(proteinId));
			// cuttingSize 留空
			int cuttingSize = 0;
			peptidePs.setString(1, sequence);
			peptidePs.setInt(2, charge);
			peptidePs.setDouble(3, precusorMZ);
			peptidePs.setString(4, modifications);
			peptidePs.setInt(5, lons);
			peptidePs.setFloat(6, mascotScore);
			peptidePs.setInt(7, length);
			peptidePs.setInt(8, start);
			peptidePs.setInt(9, stop);
			peptidePs.setInt(10, pms);
			peptidePs.setInt(11, identificationId);
			peptidePs.addBatch();
			//存储蛋白质与谱图对应关系
			proteinToSpectrum.put(proteinId.toString() + (num++), peptide
					.getSpectrumIdentification().getId().toString());
			// 输出日志
			log.info("**********" + peptidePs + "***********");
		}
		//执行批量插入
		peptidePs.executeBatch();
		// 获得自动生成的主键
		peptideRs = peptidePs.getGeneratedKeys();
		// 计数器
		int numOfpeptide = 0;
		while (peptideRs.next()) {
			// 保存所生成的主键 key为：proteinId+序列号
			peptideIdToKey.put(proteinId.toString() + (numOfpeptide++),
					peptideRs.getLong(1) + "");
		}
		log.info(peptidePs);

		return 0;
	}

	@Override
	public int insertSpectrum(
			MzIdentMLUnmarshallerAdaptor mzIdentMLUnmarshallerAdaptor)
			throws Exception {
		// 创建sql语句
		String sql = "insert into t_spectrum(identified,charge,precursor_m_z,sum_of_intensity,peaks,path) values(?,?,?,?,?,?)";
		// 自动获取插入主键
		spectrumPs = (PreparedStatement) spectrumCon.prepareStatement(sql,
				Statement.RETURN_GENERATED_KEYS);
		// 获取所有spectrumIdentification id
		Map<String, Map<String, List<IndexElement>>> spectrumIdentificationItemIds = mzIdentMLUnmarshallerAdaptor
				.getScannedIdMappings();
		// 迭代SpectrumIdentificationResult id
		Iterator spectrumIdentificationResultId = spectrumIdentificationItemIds
				.keySet().iterator();
		while (spectrumIdentificationResultId.hasNext()) {
			// 获取SpectrumIdentificationItemIds
			String key = spectrumIdentificationResultId.next().toString();
			// 获取一个spectrumIdentificationResultId对应的所有spectrumIdentificationItemId
			Map<String, List<IndexElement>> spectrumIdentificationItemIdsMap = spectrumIdentificationItemIds
					.get(key);
			// 迭代获取每个spectrumIdentificationItemId
			Iterator spectrumIdentificationItemId = spectrumIdentificationItemIdsMap
					.keySet().iterator();
			while (spectrumIdentificationItemId.hasNext()) {
				String siid = spectrumIdentificationItemId.next().toString();
				// 保存spectrum id
				spectrumNumToId.add(siid);
				// 根据id获取SpectrumIdentificationItem对象
				SpectrumIdentificationItem spectrumIdentificationItem = mzIdentMLUnmarshallerAdaptor
						.getSpectrumIdentificationsById(siid);
				String path = writeSpectrum(spectrumIdentificationItem,
						getConf().get("linuxMspFilePath") + fileName);
				// msLevel
				// int msLevel = parseSpectrumIdentificationIntf
				// .getSpectrumMsLevel(spectrumId);
				// identified
				int identified = (parseSpectrumIdentificationIntf
						.isIdentifiedSpectrum(siid)) ? 1 : 0;
				// charge
				int charge = spectrumIdentificationItem.getChargeState();
				// precusorMZ
				double precusorMZ = spectrumIdentificationItem
						.getExperimentalMassToCharge();
				// sumOfIntensity 所有电子强度之和
				double sumOfIntensity = parseSpectrumIdentificationIntf
						.getSumOfIntensity(spectrumIdentificationItem);
				// peaks 获得波峰的个数
				int peaks = parseSpectrumIdentificationIntf
						.getNumberOfSpectrumPeaks(spectrumIdentificationItem);
				// spectrumPs.setInt(1, msLevel);
				spectrumPs.setInt(1, identified);
				spectrumPs.setInt(2, charge);
				spectrumPs.setDouble(3, precusorMZ);
				spectrumPs.setDouble(4, sumOfIntensity);
				spectrumPs.setInt(5, peaks);
				spectrumPs.setString(6, path);
				spectrumPs.addBatch();
				// 输出日志
				log.info("**********" + spectrumPs + "***********");
			}
		}
		spectrumPs.executeBatch();
		// 获得自动生成的主键
		spectrumRs = spectrumPs.getGeneratedKeys();
		// 计数器，统计改蛋白质的第几个谱图
		int numOfSpectrum = 0;
		while (spectrumRs.next()) {
			// 保存所生成的主键
			spectrumIdToKey.put(spectrumNumToId.get(numOfSpectrum++),
					spectrumRs.getLong(1) + "");
		}
		return 0;
	}

	@Override
	public int insertPepProPsmSub() throws Exception {
		int subid = 2;
		// 查询dataFile表
		String dataFileSql="select subid from datafile df where df.filename like '%？%'";
		 dataFileSql=dataFileSql.replace("?", fileName); //获取数据库连接
		 dataFilePs=(PreparedStatement)
		 dataFileCon.prepareStatement(dataFileSql); //查询
		 dataFileRs=dataFilePs.executeQuery(); while(dataFileRs.next()){
			 subid= dataFileRs.getInt("subid"); 
		 }
		String sql = "insert into t_pep_pro_psm_sub(protein_id,peptide_id,spectrum_id,subid) values(?,?,?,?)";
		// log.info(sql);
		pepProPsmSubPs = (PreparedStatement) pepProPsmSubCon
				.prepareStatement(sql);
		Protein protein;
		// 迭代proteinKey集合
		Iterator proteinIdToKeyIterator = proteinIdToKey.keySet().iterator();
		while (proteinIdToKeyIterator.hasNext()) {
			// 获取蛋白质在prideXML中的ID
			Comparable proteinId = proteinIdToKeyIterator.next().toString();
			// 获取蛋白质主键
			String proteinPrimaryKey = proteinIdToKey.get(proteinId);
			// 获取当前蛋白质信息
			protein = parseProteinDetectionListIntf.getProteinById(proteinId);
			// 获取当前蛋白质下的所有肽段集合大小
			int size = parseProteinDetectionListIntf.getPeptideList(protein)
					.size();
			for (int i = 0; i < size; i++) {
				// 获取肽段主键
				String peptidePrimaryKey = peptideIdToKey.get(proteinId
						.toString() + i);
				// 根据光谱ID
				String spectrumId = proteinToSpectrum.get(proteinId.toString()
						+ i);
				// 获取光谱主键
				String spectrumPrimaryKey = spectrumIdToKey.get(spectrumId);
				pepProPsmSubPs.setInt(1, Integer.parseInt(proteinPrimaryKey));
				pepProPsmSubPs.setInt(2, Integer.parseInt(peptidePrimaryKey));
				pepProPsmSubPs.setInt(3, Integer.parseInt(spectrumPrimaryKey));
				pepProPsmSubPs.setInt(4, subid);
				pepProPsmSubPs.addBatch();
				// 输出日志
				log.info("**************" + pepProPsmSubPs
						+ "******************");
			}
		}
		pepProPsmSubPs.executeBatch();
		return 0;
	}

	@Override
	public String writeSpectrum(
			SpectrumIdentificationItem spectrumIdentificationItem,
			String fileName) throws FileNotFoundException,
			TransformerException, ParserConfigurationException, IOException {
		// 获取spectrum ID
		String id = spectrumIdentificationItem.getId();
		// 获取原文件文件名
		StringBuilder spectrumName = new StringBuilder(fileName);
		// spectrum文件
		spectrumName.append("spectrum").append(id).append(".msp");
		// 创建文件
		File file = new File(spectrumName.toString());
		// 判断文件内容是否为空
		/*
		 * if (file.exists() && file.length() > 0) { return
		 * spectrumName.toString(); }
		 */
		// 写入中文字符时解决中文乱码问题
		FileOutputStream fos = new FileOutputStream(file);
		// 设置文件流的编码格式
		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
		BufferedWriter bw = new BufferedWriter(osw);
		// 写入文件名
		bw.write("NAME: "
				+ (spectrumIdentificationItem.getPeptide().getPeptideSequence()
						.toString()).toUpperCase() + "/2" + "\t\n");
		// 写入波峰个数
		bw.write("Num peaks: "
				+ parseSpectrumIdentificationIntf
						.getNumberOfSpectrumPeaks(spectrumIdentificationItem)
				+ "\t\n");
		List<IonType> ionTypeList = spectrumIdentificationItem
				.getFragmentation().getIonType();
		if (ionTypeList != null && ionTypeList.size() > 0) {
			// 写入sequence
			for (IonType ionType : ionTypeList) {
				// 获取m_mz
				List<Float> mz = ionType.getFragmentArray().get(0).getValues();
				// 获取m_intensity
				List<Float> intensity = ionType.getFragmentArray().get(1)
						.getValues();
				// 获取m_error
				List<Float> error = ionType.getFragmentArray().get(2)
						.getValues();
				// 获取ion值
				String ion = ionType.getCvParam().getValue();
				// 获取charge
				int charge = ionType.getCharge();
				// 写入文件
				for (int i = 0; i < mz.size(); i++)
					bw.write(mz.get(i) + " " + intensity.get(i) + " \"" + "y"
							+ (ion != null ? ion : "") + new Tools().getAddNum(charge)
							+ "/" + error.get(i) + "\"" + "\t\n");
			}
		}
		log.info("--------数据写入文件成功----------");
		//输出流
		bw.flush();
		// 关闭流
		bw.close();
		osw.close();
		fos.close();
		return spectrumName.toString();
	}

	

	@Override
	public int insertAll() {
		// 获取所有蛋白质ID
		Collection<Comparable> proteinIds = parseProteinDetectionListIntf
				.getProteinIds();
		try {
			// 项蛋白质表中插入数据
			insertProtein(proteinIds);
			log.info("########### 蛋白质插入成功 #########");
			// 获取MzIdentMLUnmarshallerAdaptor对象
			MzIdentMLUnmarshallerAdaptor mzIdentMLUnmarshallerAdaptor = parseSpectrumIdentificationIntf
					.getMzIdentMLUnmarshallerAdaptor();
			// 向谱图表中插入数据
			insertSpectrum(mzIdentMLUnmarshallerAdaptor);
			log.info("########### 谱图插入成功 #########");
			// insertSpectrum(spectrumIds);
			for (Comparable proteinId : proteinIds) {
				// 向肽段表中插入数据
				insertPeptide(proteinId);
				log.info("########### 肽段插入成功 #########");
			}

			// 提交事物
			log.info("********************提交事物************************");
			proteinCon.commit();
			spectrumCon.commit();
			peptideCon.commit();
			// 向PepProPsmSub表中插入数据
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

}
