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

	// ����ParseProteinDetectionListģ���������
	ParseProteinDetectionListIntf parseProteinDetectionListIntf;
	// ����ParseSequenceCollectionIntf��������
	ParseSequenceCollectionIntf parseSequenceCollectionIntf;
	// ����ParseSpectrumIdentificationģ���������
	ParseSpectrumIdentificationIntf parseSpectrumIdentificationIntf;
	// ��������MzIdentMLControllerImplʵ�ֶ�mzIdentMl�ļ�����
	MzIdentMLControllerImpl mzIdentMLControllerImpl; 
	// �������ݿ����Ӷ���
	// datafile�����Ӷ���
	Connection dataFileCon;
	// t_protein�����Ӷ���
	Connection proteinCon;
	// t_peptide�����Ӷ���
	Connection peptideCon;
	// t-spectrum�����Ӷ���
	Connection spectrumCon;
	// t_pep_pro_psm_sub�����Ӷ���
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
	// �����־
	Logger log = Logger.getLogger(Logger.class);

	// �ļ�ȫ·����
	String filePathName;
	// �ļ���
	String fileName;

	// �浰����id��������Ӧ��ϵ
	Map<String, String> proteinIdToKey = new HashMap<String, String>();
	// �����Ķ�id��������Ӧ��ϵ
	Map<String, String> peptideIdToKey = new HashMap<String, String>();
	// ������ͼid��������Ӧ��ϵ
	Map<String, String> spectrumIdToKey = new HashMap<String, String>();
	// ���浰��������ͼ��Ӧ��ϵ
	Map<String, String> proteinToSpectrum = new HashMap<String, String>();
	// ������� ˳�������id��Ӧ��ϵ
	List<String> spectrumNumToId = new ArrayList<String>();
	// ���浰���� ˳�������id��Ӧ��ϵ
	List<String> proteinNumToId = new ArrayList<String>();

	public MzIdentMLDaoImpl(String inputFilePathName) {
		// ������ȡ�����ļ�����
		File inputFile = new File(inputFilePathName);
		log.info(inputFile);
		mzIdentMLControllerImpl= new MzIdentMLControllerImpl(
				inputFile);
		//�ж��ļ��Ƿ���ȷ
		if(!mzIdentMLControllerImpl.isValidFormat(inputFile)){
			log.info("�ļ�������Ҫ��");
			//return;
		}
		// ��ʼ����ģ���������
		// ��ʼ��ParseProteinDetectionListģ���������
		parseProteinDetectionListIntf = new ParseProteinDetectionListImpl(
				mzIdentMLControllerImpl);
		// ��ʼ��ParseSequenceCollectionIntf��������
		parseSequenceCollectionIntf = new ParseSequenceCollectionImpl(
				mzIdentMLControllerImpl);
		// ��ʼ��ParseSpectrumIdentificationģ���������
		parseSpectrumIdentificationIntf = new ParseSpectrumIdentificationImpl(
				mzIdentMLControllerImpl);

		dataFileCon = getConnection();
		proteinCon = getConnection();
		peptideCon = getConnection();
		spectrumCon = getConnection();
		pepProPsmSubCon = getConnection();
		// ʹ�����񣬱�������setAutoCommit false����ʾ�ֶ��ύ
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
		// ����sql���
		String sql = "insert into t_protein(protein,threshold,peptides,distinct_peptides,ptms) values(?,?,?,?,?)";
		// try {
		proteinPs = (PreparedStatement) proteinCon.prepareStatement(sql,
				Statement.RETURN_GENERATED_KEYS);
		for (Comparable proteinId : proteinIds) {
			proteinNumToId.add(proteinId.toString());
			// ����ID�����Ӧ����
			Protein protein = parseProteinDetectionListIntf
					.getProteinById(proteinId);
			// coverage ��ʱ����
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
			// ������������
			proteinPs.addBatch();
			// �����־
			log.info("**********" + proteinPs + "***********");

		}
		proteinPs.executeBatch();
		// ��ȡ�Զ����ɵ�keyֵ
		proteinRs = proteinPs.getGeneratedKeys();
		// ������
		Integer numOfProteinId = 0;
		while (proteinRs.next()) {
			// ���Զ����ɵ��������뼯��
			proteinIdToKey.put(proteinNumToId.get(numOfProteinId++),
					proteinRs.getLong(1) + "");
		}
		log.info(proteinPs);

		return 0;
	}

	@Override
	public int insertPeptide(Comparable proteinId) throws Exception {
		// sql���
		String sql = "insert into t_peptide(peptide,charge,precursor_m_z,modifications,lons,mascot_score,length,start,stop,psm,identification_id) values(?,?,?,?,?,?,?,?,?,?,?)";
		peptidePs = (PreparedStatement) peptideCon.prepareStatement(sql,
				Statement.RETURN_GENERATED_KEYS);
		// ����proteinId��ȡprotein
		Protein protein = parseProteinDetectionListIntf
				.getProteinById(proteinId);
		// ��ȡpeptide����
		List<Peptide> peptideList = protein.getPeptides();
		// ������
		int num = 0;
		for (Peptide peptide : peptideList) {
			// ��ȡSpectrumIdentification��д���ļ�
			SpectrumIdentification spectrumIdentification = peptide
					.getSpectrumIdentification();
			// sequence
			String sequence = peptide.getSequence();
			// fit ����
			String fit;
			// deltaMZ ����
			float deltaMZ;
			// charge
			int charge = spectrumIdentification.getChargeState();
			// precusorMZ
			double precusorMZ = spectrumIdentification
					.getExperimentalMassToCharge();
			// precusorm ����
			float precusorm;
			// readablemod ����
			String readablemod = null;
			// modifications ���޸Ĺ�ϵ��
			String modifications = parseSequenceCollectionIntf
					.getModifications(peptide);
			// missedcleave ����
			int missedcleave = 0;
			// fragmentIon ������
			int lons = peptide.getFragmentation().size();
			// mascotScore
			String cvParams = parseSpectrumIdentificationIntf.getCvParams(
					spectrumIdentification).get("mascot:score");
			//��ȡmascotScore
			float mascotScore = (cvParams != null ? Float.parseFloat(cvParams)
					: 0);
			// ��ʼ��
			int start = peptide.getPeptideEvidence().getStartPosition();
			// ������
			int stop = peptide.getPeptideEvidence().getEndPosition();
			// ����
			int length = stop - start + 1;
			// ��ȡ������ͬ���Ķ���
			int pms = parseSequenceCollectionIntf
					.getNumberOfQuantPTMs(proteinId);
			// identificationId
			int identificationId = Integer.parseInt(proteinIdToKey
					.get(proteinId));
			// cuttingSize ����
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
			//�洢����������ͼ��Ӧ��ϵ
			proteinToSpectrum.put(proteinId.toString() + (num++), peptide
					.getSpectrumIdentification().getId().toString());
			// �����־
			log.info("**********" + peptidePs + "***********");
		}
		//ִ����������
		peptidePs.executeBatch();
		// ����Զ����ɵ�����
		peptideRs = peptidePs.getGeneratedKeys();
		// ������
		int numOfpeptide = 0;
		while (peptideRs.next()) {
			// ���������ɵ����� keyΪ��proteinId+���к�
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
		// ����sql���
		String sql = "insert into t_spectrum(identified,charge,precursor_m_z,sum_of_intensity,peaks,path) values(?,?,?,?,?,?)";
		// �Զ���ȡ��������
		spectrumPs = (PreparedStatement) spectrumCon.prepareStatement(sql,
				Statement.RETURN_GENERATED_KEYS);
		// ��ȡ����spectrumIdentification id
		Map<String, Map<String, List<IndexElement>>> spectrumIdentificationItemIds = mzIdentMLUnmarshallerAdaptor
				.getScannedIdMappings();
		// ����SpectrumIdentificationResult id
		Iterator spectrumIdentificationResultId = spectrumIdentificationItemIds
				.keySet().iterator();
		while (spectrumIdentificationResultId.hasNext()) {
			// ��ȡSpectrumIdentificationItemIds
			String key = spectrumIdentificationResultId.next().toString();
			// ��ȡһ��spectrumIdentificationResultId��Ӧ������spectrumIdentificationItemId
			Map<String, List<IndexElement>> spectrumIdentificationItemIdsMap = spectrumIdentificationItemIds
					.get(key);
			// ������ȡÿ��spectrumIdentificationItemId
			Iterator spectrumIdentificationItemId = spectrumIdentificationItemIdsMap
					.keySet().iterator();
			while (spectrumIdentificationItemId.hasNext()) {
				String siid = spectrumIdentificationItemId.next().toString();
				// ����spectrum id
				spectrumNumToId.add(siid);
				// ����id��ȡSpectrumIdentificationItem����
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
				// sumOfIntensity ���е���ǿ��֮��
				double sumOfIntensity = parseSpectrumIdentificationIntf
						.getSumOfIntensity(spectrumIdentificationItem);
				// peaks ��ò���ĸ���
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
				// �����־
				log.info("**********" + spectrumPs + "***********");
			}
		}
		spectrumPs.executeBatch();
		// ����Զ����ɵ�����
		spectrumRs = spectrumPs.getGeneratedKeys();
		// ��������ͳ�Ƹĵ����ʵĵڼ�����ͼ
		int numOfSpectrum = 0;
		while (spectrumRs.next()) {
			// ���������ɵ�����
			spectrumIdToKey.put(spectrumNumToId.get(numOfSpectrum++),
					spectrumRs.getLong(1) + "");
		}
		return 0;
	}

	@Override
	public int insertPepProPsmSub() throws Exception {
		int subid = 2;
		// ��ѯdataFile��
		String dataFileSql="select subid from datafile df where df.filename like '%��%'";
		 dataFileSql=dataFileSql.replace("?", fileName); //��ȡ���ݿ�����
		 dataFilePs=(PreparedStatement)
		 dataFileCon.prepareStatement(dataFileSql); //��ѯ
		 dataFileRs=dataFilePs.executeQuery(); while(dataFileRs.next()){
			 subid= dataFileRs.getInt("subid"); 
		 }
		String sql = "insert into t_pep_pro_psm_sub(protein_id,peptide_id,spectrum_id,subid) values(?,?,?,?)";
		// log.info(sql);
		pepProPsmSubPs = (PreparedStatement) pepProPsmSubCon
				.prepareStatement(sql);
		Protein protein;
		// ����proteinKey����
		Iterator proteinIdToKeyIterator = proteinIdToKey.keySet().iterator();
		while (proteinIdToKeyIterator.hasNext()) {
			// ��ȡ��������prideXML�е�ID
			Comparable proteinId = proteinIdToKeyIterator.next().toString();
			// ��ȡ����������
			String proteinPrimaryKey = proteinIdToKey.get(proteinId);
			// ��ȡ��ǰ��������Ϣ
			protein = parseProteinDetectionListIntf.getProteinById(proteinId);
			// ��ȡ��ǰ�������µ������Ķμ��ϴ�С
			int size = parseProteinDetectionListIntf.getPeptideList(protein)
					.size();
			for (int i = 0; i < size; i++) {
				// ��ȡ�Ķ�����
				String peptidePrimaryKey = peptideIdToKey.get(proteinId
						.toString() + i);
				// ���ݹ���ID
				String spectrumId = proteinToSpectrum.get(proteinId.toString()
						+ i);
				// ��ȡ��������
				String spectrumPrimaryKey = spectrumIdToKey.get(spectrumId);
				pepProPsmSubPs.setInt(1, Integer.parseInt(proteinPrimaryKey));
				pepProPsmSubPs.setInt(2, Integer.parseInt(peptidePrimaryKey));
				pepProPsmSubPs.setInt(3, Integer.parseInt(spectrumPrimaryKey));
				pepProPsmSubPs.setInt(4, subid);
				pepProPsmSubPs.addBatch();
				// �����־
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
		// ��ȡspectrum ID
		String id = spectrumIdentificationItem.getId();
		// ��ȡԭ�ļ��ļ���
		StringBuilder spectrumName = new StringBuilder(fileName);
		// spectrum�ļ�
		spectrumName.append("spectrum").append(id).append(".msp");
		// �����ļ�
		File file = new File(spectrumName.toString());
		// �ж��ļ������Ƿ�Ϊ��
		/*
		 * if (file.exists() && file.length() > 0) { return
		 * spectrumName.toString(); }
		 */
		// д�������ַ�ʱ���������������
		FileOutputStream fos = new FileOutputStream(file);
		// �����ļ����ı����ʽ
		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
		BufferedWriter bw = new BufferedWriter(osw);
		// д���ļ���
		bw.write("NAME: "
				+ (spectrumIdentificationItem.getPeptide().getPeptideSequence()
						.toString()).toUpperCase() + "/2" + "\t\n");
		// д�벨�����
		bw.write("Num peaks: "
				+ parseSpectrumIdentificationIntf
						.getNumberOfSpectrumPeaks(spectrumIdentificationItem)
				+ "\t\n");
		List<IonType> ionTypeList = spectrumIdentificationItem
				.getFragmentation().getIonType();
		if (ionTypeList != null && ionTypeList.size() > 0) {
			// д��sequence
			for (IonType ionType : ionTypeList) {
				// ��ȡm_mz
				List<Float> mz = ionType.getFragmentArray().get(0).getValues();
				// ��ȡm_intensity
				List<Float> intensity = ionType.getFragmentArray().get(1)
						.getValues();
				// ��ȡm_error
				List<Float> error = ionType.getFragmentArray().get(2)
						.getValues();
				// ��ȡionֵ
				String ion = ionType.getCvParam().getValue();
				// ��ȡcharge
				int charge = ionType.getCharge();
				// д���ļ�
				for (int i = 0; i < mz.size(); i++)
					bw.write(mz.get(i) + " " + intensity.get(i) + " \"" + "y"
							+ (ion != null ? ion : "") + new Tools().getAddNum(charge)
							+ "/" + error.get(i) + "\"" + "\t\n");
			}
		}
		log.info("--------����д���ļ��ɹ�----------");
		//�����
		bw.flush();
		// �ر���
		bw.close();
		osw.close();
		fos.close();
		return spectrumName.toString();
	}

	

	@Override
	public int insertAll() {
		// ��ȡ���е�����ID
		Collection<Comparable> proteinIds = parseProteinDetectionListIntf
				.getProteinIds();
		try {
			// ����ʱ��в�������
			insertProtein(proteinIds);
			log.info("########### �����ʲ���ɹ� #########");
			// ��ȡMzIdentMLUnmarshallerAdaptor����
			MzIdentMLUnmarshallerAdaptor mzIdentMLUnmarshallerAdaptor = parseSpectrumIdentificationIntf
					.getMzIdentMLUnmarshallerAdaptor();
			// ����ͼ���в�������
			insertSpectrum(mzIdentMLUnmarshallerAdaptor);
			log.info("########### ��ͼ����ɹ� #########");
			// insertSpectrum(spectrumIds);
			for (Comparable proteinId : proteinIds) {
				// ���Ķα��в�������
				insertPeptide(proteinId);
				log.info("########### �Ķβ���ɹ� #########");
			}

			// �ύ����
			log.info("********************�ύ����************************");
			proteinCon.commit();
			spectrumCon.commit();
			peptideCon.commit();
			// ��PepProPsmSub���в�������
			insertPepProPsmSub();
			pepProPsmSubCon.commit();
			log.info("********************�����ύ�ɹ�************************");
		} catch (Exception e) {
			try {
				log.info("�����쳣���������ݻع�");
				// ���ݻع�
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
