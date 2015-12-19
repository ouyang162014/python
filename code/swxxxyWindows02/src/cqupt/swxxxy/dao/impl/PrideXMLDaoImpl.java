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
 * ʵ�ֶ�ȡprideXML�ļ��е����ݴ洢�����ݿ����Ӧ�ļ���
 * 
 * @author chao ouyang
 * 
 */
public class PrideXMLDaoImpl extends BaseDao implements PrideXMLDaoIntf {
	// cvLookUpģ���������
	// ParseCVLoopUpIntf parseCVLoopUpIntf;
	// DataProcessingģ���������
	// ParseDataProcessingIntf parseDataProcessingIntf;
	// GelFreIdentificationģ���������
	ParseGelFreeIdentificationIntf parseGelFreeIdentificationIntf;
	// Protocalģ���������
	// ParseProtocalIntf parseProtocalIntf;
	// Spectrumģ���������
	ParseSpectrumIntf parseSpectrumIntf;
	// Adminģ��Ľ���
	// ParseAdminIntf parseAdminIntf;
	// �������ݿ����Ӷ���
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
	// ��д���ļ�ȫ·����
	String fileName;
	// �ļ���
	String subName;
	// �����־
	Logger log = Logger.getLogger(Logger.class);
	// �浰���ʱ�����
	Map<String, String> proteinKey = new HashMap<String, String>();
	// �����Ķα�����
	Map<String, String> peptideKey = new HashMap<String, String>();
	// ������ױ�����
	Map<String, String> spectrumKey = new HashMap<String, String>();

	/**
	 * 
	 * @param fileName
	 *            �ϴ��ļ���
	 */
	public PrideXMLDaoImpl(String fileName)  {
		File inputFile = new File(fileName);
		PrideXmlControllerImpl prideXmlControllerImpl = new PrideXmlControllerImpl(
				inputFile);
		if(prideXmlControllerImpl.isValidFormat(inputFile)){
			log.info("�ϴ��ļ����Ϸ�");
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
		// �������ݿ����Ӷ���
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
		// ����sql���
		String sql = "insert into t_protein(protein,threshold,peptides,distinct_peptides,ptms) values(?,?,?,?,?)";
		// try {
		proteinPs = (PreparedStatement) proteinCon.prepareStatement(sql,
				Statement.RETURN_GENERATED_KEYS);
		for (Comparable proteinId : proteinIds) {
			// ����ID�����Ӧ����
			Protein protein = parseGelFreeIdentificationIntf
					.getProteinById(proteinId);
			// coverage ��ʱ����
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
			// ������������
			proteinPs.addBatch();
		}
		proteinPs.executeBatch();
		log.info(proteinPs);

		// �ύ
		// con.commit();
		// ��ȡ�Զ����ɵ�keyֵ
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
		// sql���
		String sql = "insert into t_peptide(peptide,charge,precursor_m_z,modifications,lons,mascot_score,length,start,stop,psm,identification_id) values(?,?,?,?,?,?,?,?,?,?,?)";
		// log.info(sql);
		// try {
		peptidePs = (PreparedStatement) peptideCon.prepareStatement(sql,
				Statement.RETURN_GENERATED_KEYS);
		// ������
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
			// fit ����
			String fit;
			// deltaMZ ����
			float deltaMZ;
			// charge
			Map<String, String> additional = parseGelFreeIdentificationIntf
					.getPeptideAdditional(peptide);
			int charge = additional.get("charge state") != null ? Integer
					.parseInt(additional.get("charge state")) : 0;
			// precusorMZ
			int precusorMZ = Integer.parseInt(parseGelFreeIdentificationIntf
					.getPeptideSpectrumId(peptide).toString());
			// precusorm ����
			float precusorm;
			// readablemod ����
			String readablemod = null;
			// modifications ���޸Ĺ�ϵ��
			String modifications = parseGelFreeIdentificationIntf
					.getModifications(peptide);
			// missedcleave ����
			int missedcleave = 0;
			// fragmentIon ������
			int lons = peptide.getFragmentation().size();
			// mascotScore
			float mascotScore = additional.get("Mascot Score") != null ? Float
					.parseFloat(additional.get("Mascot Score")) : 0;
			int start = parseGelFreeIdentificationIntf
					.getPeptideSequenceStart(peptide);
			int stop = parseGelFreeIdentificationIntf
					.getPeptideSequenceEnd(peptide);
			int length = stop - start + 1;
			// ��ȡ������ͬ���Ķ���
			int pms = parseGelFreeIdentificationIntf.getNumberOfQuantPeptides(
					proteinId, index);
			// identificationId
			int identificationId = Integer.parseInt(proteinKey.get(
					proteinId.toString()).toString());
			// cuttingSize ����
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
		// ����Զ����ɵ�����
		peptideRs = peptidePs.getGeneratedKeys();
		int i = 0;
		while (peptideRs.next()) {
			// ���������ɵ�����
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
			// sumOfIntensity ���е���ǿ��֮��
			double sumOfIntensity = parseSpectrumIntf
					.getSumOfIntensity(spectrumId);
			// peaks ��ò���ĸ���
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
		// ����Զ����ɵ�����
		spectrumRs = spectrumPs.getGeneratedKeys();
		int i = 1;
		while (spectrumRs.next()) {
			// ���������ɵ�����
			spectrumKey.put("" + i, spectrumRs.getLong(1) + "");
			i++;
		}
		return 0;
	}

	public int insertPepProPsmSub() throws Exception {
		int subid = 2;
		// ��ѯdataFile��
		
		 String dataFileSql="select subid from datafile df where df.filename like '%��%'";
		 dataFileSql=dataFileSql.replace("?", subName); //��ȡ���ݿ�����
		 dataFilePs=(PreparedStatement)
		 dataFileCon.prepareStatement(dataFileSql); //��ѯ
		 dataFileRs=dataFilePs.executeQuery(); while(dataFileRs.next()){
			 subid= dataFileRs.getInt("subid"); 
		 }
		 
		String sql = "insert into t_pep_pro_psm_sub(protein_id,peptide_id,spectrum_id,subid) values(?,?,?,?)";
		// log.info(sql);
		pepProPsmSubPs = (PreparedStatement) pepProPsmSubCon
				.prepareStatement(sql);
		// ����proteinKey����
		Iterator proteinKeyIterator = proteinKey.keySet().iterator();
		while (proteinKeyIterator.hasNext()) {
			// ��ȡ��������prideXML�е�ID
			String proteinId = proteinKeyIterator.next().toString();
			// ��ȡ�����ʹ�ϵ���ID
			String proteinPrimaryKey = proteinKey.get(proteinId);
			// ��ȡ��ǰ�������µ������Ķ�����
			int peptides = parseGelFreeIdentificationIntf
					.getProteinById(proteinId).getPeptides().size();
			for (int i = 0; i < peptides; i++) {
				// ��ȡ�Ķι�ϵ���е�ID
				String peptidePrimaryKey = peptideKey.get(proteinId + i);
				// ��ȡ�Ķ�
				//log.info(proteinId + "*************" + i);
				Peptide peptide = parseGelFreeIdentificationIntf
						.getPeptideByIndex(proteinId, i);
				// ��ȡ�Ķζ�Ӧ�Ĺ���ID
				String SpectrunId = parseGelFreeIdentificationIntf
						.getSpectrumReference(peptide).toString();
				// ���ݹ���ID��ȡ���׹�ϵ���ж�Ӧ��ID
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
		// ��ȡ���е�����ID
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
				// ��ȡ�����ʶ�Ӧ�������Ķε�ID
				Collection<Comparable> peptideIds = parseGelFreeIdentificationIntf
						.getPeptideIds(proteinId);
				//log.info("*****************" + peptideIds.size()
					//	+ "**********************");
				insertPeptide(peptideIds, proteinId);

			}
			insertSpectrum(spectrumIds);

			// �ύ����
			log.info("********************�ύ����************************");
			proteinCon.commit();
			spectrumCon.commit();
			peptideCon.commit();

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

	/**
	 * ���ļ���д��spectrum��Ϣ
	 * 
	 * @param spectrum
	 *            �����spectrum
	 * @param �ļ���
	 * @param fragmentIonList
	 */
	public String writeSpectrum(Spectrum spectrum, String fileName,
			List<FragmentIon> fragmentIonList, Peptide peptide)
			throws TransformerException, ParserConfigurationException,
			IOException {
		// ��ȡspectrum ID
		String id = spectrum.getId() + "";
		// ��ȡԭ�ļ��ļ���
		StringBuilder spectrumName = new StringBuilder(fileName);
		// spectrum�ļ�
		spectrumName.append("spectrum").append(id).append(".msp");
		// �����ļ�
		File file = new File(spectrumName.toString());
		// �ж��ļ������Ƿ�Ϊ��
		if (file.exists() && file.length() > 0) {
			return spectrumName.toString();
		}
		// д�������ַ�ʱ���������������
		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
		BufferedWriter bw = new BufferedWriter(osw);
		// ��ȡ���ܺ��mz����
		double[] mz = parseSpectrumIntf.getMZArrayBinary(spectrum)[0];
		// ���ܺ��intensity����
		double[] intensity = parseSpectrumIntf.getMZArrayBinary(spectrum)[1];
		// �ж�fragmentIonList�����Ƿ�Ϊ��,��Ϊ����FragmentIon��Ϣд���ļ���
		if (fragmentIonList != null && fragmentIonList.size() > 0
				&& peptide != null) {
			// ����map�������� keyΪmz valueΪfragmentIonList��indexֵ
			Map<String, Integer> fragmentIonMap = new HashMap<String, Integer>();
			// ��¼��fragmentIonList�е�λ��
			int theNumOfFragmentIon = 0;
			for (FragmentIon fragmentIon : fragmentIonList) {
				fragmentIonMap.put(fragmentIon.getMz() + "",
						theNumOfFragmentIon);
				theNumOfFragmentIon++;
			}
			// д��sequence
			bw.write("NAME: " + (peptide.getSequence()).toUpperCase() + "/2"
					+ "\t\n");
			// д�벨�����
			bw.write("Num peaks: " + intensity.length + "\t\n");
			// ѭ��д��mz intensity ���������������
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
			// д��sequence
			bw.write("NAME: " + "\t\n");
			// д�벨�����
			bw.write("Num peaks: " + intensity.length + "\t\n");
			for (int i = 0; i < mz.length; i++) {
				bw.write(mz[i] + " " + intensity[i] + "\t\n");
			}
		}
		// �ر���
		bw.close();
		osw.close();
		fos.close();
		return spectrumName.toString();
	}

	/**
	 * ��ȡ�������Ŀ
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