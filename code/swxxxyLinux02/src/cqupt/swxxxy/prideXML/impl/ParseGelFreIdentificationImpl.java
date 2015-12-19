package cqupt.swxxxy.prideXML.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cqupt.swxxxy.prideXML.intf.ParseGelFreeIdentificationIntf;

import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.CachedDataAccessController;
import uk.ac.ebi.pride.utilities.data.core.CvParam;
import uk.ac.ebi.pride.utilities.data.core.FragmentIon;
import uk.ac.ebi.pride.utilities.data.core.Modification;
import uk.ac.ebi.pride.utilities.data.core.Peptide;
import uk.ac.ebi.pride.utilities.data.core.Protein;
import uk.ac.ebi.pride.utilities.data.core.SearchDataBase;
import uk.ac.ebi.pride.utilities.data.core.PeptideEvidence;
import uk.ac.ebi.pride.utilities.data.core.Spectrum;
import uk.ac.ebi.pride.utilities.data.core.SpectrumIdentification;
import uk.ac.ebi.pride.utilities.term.SearchEngineCvTermReference;

public class ParseGelFreIdentificationImpl implements ParseGelFreeIdentificationIntf{
	CachedDataAccessController cachedDataAccessController;
	
	public ParseGelFreIdentificationImpl(CachedDataAccessController cachedDataAccessController){
		this.cachedDataAccessController=cachedDataAccessController;
	}

	@Override
	public String getProteinAccession(Comparable proteinId) {
		return cachedDataAccessController.getProteinAccession(proteinId);
	}

	public String getDatabase(Comparable proteinId) {
		SearchDataBase searchDatabase=cachedDataAccessController.getSearchDatabase(proteinId);
		return searchDatabase.getName();
	}

	public String getDatabaseVersion(Comparable proteinId) {
		return cachedDataAccessController.getProteinAccessionVersion(proteinId);
	}

	public Collection<Comparable> getPeptideIds(Comparable proteinId) {
		return cachedDataAccessController.getPeptideIds(proteinId);
	}

	public double getProteinScore(Comparable proteinId) {
		return cachedDataAccessController.getProteinScore(proteinId);
	}

	public double getProteinThreshold(Comparable proteinId) {
		return cachedDataAccessController.getProteinThreshold(proteinId);
	}

	public String getProteinSearchEngine(Comparable proteinId) {
		List<SearchEngineCvTermReference> searchEngine=cachedDataAccessController.getSearchEngineCvTermReferences();
		return null;
	}

	public Peptide getPeptideByIndex(Comparable proteinId, Comparable index) {
		return cachedDataAccessController.getPeptideByIndex(proteinId, index);
	}

	public String getPeptideSequence(Peptide peptide) {
		return peptide.getSequence();
	}

	public int getPeptideSequenceStart(Peptide peptide) {
		PeptideEvidence peptideEvidence=peptide.getPeptideEvidence();
		return peptideEvidence.getStartPosition();
	}

	public int getPeptideSequenceEnd(Peptide peptide) {
		PeptideEvidence peptideEvidence=peptide.getPeptideEvidence();
		return peptideEvidence.getEndPosition();
	}

	public Comparable getPeptideSpectrumId(Peptide peptide) {
		SpectrumIdentification spectrumIdentification=peptide.getSpectrumIdentification();
		Spectrum spectrum=spectrumIdentification.getSpectrum();
		return spectrum.getId();
	}

	public List<FragmentIon> getFragmentIonList(Peptide peptide) {
		return peptide.getFragmentation();
	}

	public Map<String,Double> getCVParam(FragmentIon fragmentIon) {
		Map<String,Double> cvParams=new HashMap<String,Double>();
		cvParams.put("product ion charge",(double)fragmentIon.getCharge());
		cvParams.put("product ion intensity", fragmentIon.getIntensity());
		cvParams.put("product ion m/z",fragmentIon.getMz());
		cvParams.put("product ion mass error",fragmentIon.getMassError());
		cvParams.put("y ion",(double)fragmentIon.getLocation());
		return cvParams;
	}

	public Protein getProteinById(Comparable proteinId) {
		return cachedDataAccessController.getProteinById(proteinId);
	}

	public Map<String,String> getPeptideAdditional(Peptide peptide) {
		Map<String,String> additional=new HashMap<String,String>();
		SpectrumIdentification spectrumIdentification=peptide.getSpectrumIdentification();
		List<CvParam> cvParamList=spectrumIdentification.getCvParams();
		for(CvParam cvParam:cvParamList){
			additional.put(cvParam.getName(), cvParam.getValue());
		}
		return additional;
	}

	public Map<String,String> getProteinAdditional(Protein protein) {
		List<CvParam> cvParamList=protein.getCvParams();
		Map<String,String> additional=new HashMap<String,String>();
		for(CvParam cvParam:cvParamList){
			additional.put(cvParam.getName(), cvParam.getValue());
		}
		return additional;
	}

	public Collection<Comparable> getProteinIds() {
		return cachedDataAccessController.getProteinIds();
	}

	public int getNumberOfPeptides(Comparable proteinId) {
		return cachedDataAccessController.getNumberOfPeptides();
	}

	public int getNumberOfUniquePeptides(Comparable proteinId) {
		return cachedDataAccessController.getNumberOfUniquePeptides(proteinId);
	}

	public int getNumberOfPTMs(Comparable proteinId) {
		return cachedDataAccessController.getNumberOfPTMs(proteinId);
	}

	public int getNumberOfQuantPeptides(Comparable proteinId,Comparable peptideId) {
		Protein protein=cachedDataAccessController.getProteinById(proteinId);
		List<Peptide> peptideList=protein.getPeptides();
		String sequence=cachedDataAccessController.getPeptideSequence(proteinId, peptideId);
		int num=0;
		for(Peptide peptide:peptideList){
			if(sequence.equals(peptide.getSequence())){
				num++;
			}
		}
		return --num;
		//return cachedDataAccessController.getNumberOfQuantPTMs(proteinId,peptideId);
	}

	public Comparable getSpectrumReference(Peptide peptide) {
		return peptide.getSpectrumIdentification().getSpectrum().getId();
	}
	
	public String getModifications(Peptide peptide) {
		//获取Modification对象集合
		List<Modification> modificationList=peptide.getModifications();
		StringBuilder sb=new StringBuilder();
		if(modificationList!=null && modificationList.size()>0){
			//sb.append("ModificationItem:").append(modificationList.size()).append("{");
			for(Modification modification:modificationList){
				sb.append("name:").append(modification.getName()).append(",");
				//sb.append("ModAccession:").append(modification.getId().toString()).append(",");
				//List<Double> modMonoDeltaList=modification.getMonoisotopicMassDelta();
				//if(modMonoDeltaList!=null && modMonoDeltaList.size()>0){
					//for(double modMonoDelta:modMonoDeltaList){
						//sb.append("modMonoDelta:").append(modMonoDelta).append(",");
					//}
				//}
				sb.delete(sb.length()-1, sb.length());
				//sb.append(";");
			}
			//sb.append("}");
			return sb.toString();
		}else{
			return "";
		}
		
	}
	
}
