package cqupt.swxxxy.prideXML.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.pride.utilities.data.core.BinaryDataArray;
import uk.ac.ebi.pride.utilities.data.core.CvParam;
import uk.ac.ebi.pride.utilities.data.core.Modification;
import uk.ac.ebi.pride.utilities.data.core.ParamGroup;
import uk.ac.ebi.pride.utilities.data.core.Peptide;
import uk.ac.ebi.pride.utilities.data.core.Precursor;
import uk.ac.ebi.pride.utilities.data.core.Scan;
import uk.ac.ebi.pride.utilities.data.core.ScanList;
import uk.ac.ebi.pride.utilities.data.core.Spectrum;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.CachedDataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.PrideXmlControllerImpl;

import cqupt.swxxxy.prideXML.intf.ParseSpectrumIntf;

public class ParseSpectrumImpl implements ParseSpectrumIntf {
	CachedDataAccessController cachedDataAccessController;
	
	public ParseSpectrumImpl(CachedDataAccessController prideXmlControllerImpl){
		this.cachedDataAccessController = prideXmlControllerImpl;
	}
	
	public List<Spectrum> getSpectrums() {
		Collection<Comparable> comparable=getSpectrumIds();
		List<Spectrum> spectrumList=new ArrayList<Spectrum>();
		for(Comparable c:comparable){
			Spectrum spectrum=cachedDataAccessController.getSpectrumById(c);
			spectrumList.add(spectrum);
		}
		return spectrumList;
	}

	public Collection<Comparable> getSpectrumIds() {
		//获取所有Spectrum的ID
		return cachedDataAccessController.getSpectrumIds();
	}

	public double[][] getMZArrayBinary(Spectrum spectrum) {
		int i=0;
		double[][] parseArray=new double[2][];
		List<BinaryDataArray> binaryDataArrays=spectrum.getBinaryDataArrays();
		for(BinaryDataArray binaryDataArray:binaryDataArrays){
			parseArray[i]=binaryDataArray.getDoubleArray();
			i++;
		}
		return parseArray;
	}

	public Map<String,String> getPeakMZTimeChargeState(Spectrum spectrum) {
		Map<String,String> cvParams=new HashMap<String,String>();
		int i=0;
		List<Precursor> precursorList=spectrum.getPrecursors();
		for(Precursor precursor:precursorList){
			List<ParamGroup> paramGroupList=precursor.getSelectedIons();
			for(ParamGroup paramGroup:paramGroupList){
				List<CvParam> cvParamList=paramGroup.getCvParams();
				for(CvParam cvParam:cvParamList){
					cvParams.put(cvParam.getName(),cvParam.getValue());
					i++;
				}
			}
		}
		return cvParams;
	}

	public int getSpectrumMsLevel(Comparable SpectrumID) {
		int lev=cachedDataAccessController.getSpectrumMsLevel(SpectrumID);
		return lev;
	}
	
	public Map<String,String> getMzRange(Spectrum spectrum) {
		Map<String,String> range=new HashMap<String,String>();
		ScanList scanList=spectrum.getScanList();
		List<Scan> scans=scanList.getScans();
		List<ParamGroup> paramGroups=scans.get(0).getScanWindows();
		ParamGroup paramGroup=paramGroups.get(0);
		List<CvParam> cvParamList=paramGroup.getCvParams();
		for(CvParam cvParam:cvParamList){
			range.put(cvParam.getName(), cvParam.getValue());
		}
		return range;
	}

	public int getCount(Spectrum spectrum) {
		List<Precursor> precursorList=spectrum.getPrecursors();
		int count=precursorList.size();
		return count;
	}

	public Spectrum getSpectrum(Comparable spectrumId) {
		return cachedDataAccessController.getSpectrumById(spectrumId);
	}

	public double getSumOfIntensity(Comparable specId) {
		return cachedDataAccessController.getSumOfIntensity(specId);
	}

	public int getNumberOfSpectrumPeaks(Comparable specId) {
		return cachedDataAccessController.getNumberOfSpectrumPeaks(specId);
	}

	public boolean isIdentifiedSpectrum(Comparable specId) {
		return cachedDataAccessController.isIdentifiedSpectrum(specId);
	}

}
