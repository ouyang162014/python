package cqupt.swxxxy.mzIdentML.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import psidev.psi.tools.xxindex.index.IndexElement;
import uk.ac.ebi.jmzidml.model.mzidml.FragmentArray;
import uk.ac.ebi.jmzidml.model.mzidml.IonType;
import uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationItem;
import uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationResult;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.CachedDataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.MzIdentMLControllerImpl;
import uk.ac.ebi.pride.utilities.data.core.CvParam;
import uk.ac.ebi.pride.utilities.data.core.FragmentIon;
import uk.ac.ebi.pride.utilities.data.core.Peptide;
import uk.ac.ebi.pride.utilities.data.core.SpectrumIdentification;
import uk.ac.ebi.pride.utilities.data.io.file.MzIdentMLUnmarshallerAdaptor;
import cqupt.swxxxy.mzIdentML.intf.ParseSpectrumIdentificationIntf;

public class ParseSpectrumIdentificationImpl implements ParseSpectrumIdentificationIntf{

	MzIdentMLControllerImpl mzIdentMLControllerImpl;
	
	public ParseSpectrumIdentificationImpl(){}
	
	public ParseSpectrumIdentificationImpl(MzIdentMLControllerImpl mzIdentMLControllerImpl){
		this.mzIdentMLControllerImpl=mzIdentMLControllerImpl;
	}

	@Override
	public Map<String, String> getCvParams(
			SpectrumIdentification spectrumIdentificationm) {
		//创建集合存储对象
		Map<String,String> map=new HashMap<String,String>();
		List<CvParam> cvParamList=spectrumIdentificationm.getCvParams();
		for(CvParam cvParam:cvParamList){
			//获取集合属性值
			map.put(cvParam.getName(), cvParam.getValue());
		}
		return map;
	}

	@Override
	public List<FragmentIon> getFragmentationList(
			SpectrumIdentification spectrumIdentification) {
		return spectrumIdentification.getFragmentation();
	}
	
	public int getSpectrumMsLevel(Comparable SpectrumID) {
		int lev=mzIdentMLControllerImpl.getSpectrumMsLevel(SpectrumID);
		return lev;
	}
	
	public boolean isIdentifiedSpectrum(Comparable specId) {
		return mzIdentMLControllerImpl.isIdentifiedSpectrum(specId);
	}

	@Override
	public double getSumOfIntensity(SpectrumIdentificationItem spectrumIdentificationItem) {
		//获取IonType集合对象
		List<IonType> ionTypeList=spectrumIdentificationItem.getFragmentation().getIonType();
		double sum=0;
		for(IonType ionType:ionTypeList){
			List<FragmentArray> fragmentArrayList=ionType.getFragmentArray();
			List<Float> intensitys=fragmentArrayList.get(1).getValues();
			sum+=sumOfList(intensitys);
		}
		return sum;
	}

	@Override
	public int getNumberOfSpectrumPeaks(SpectrumIdentificationItem spectrumIdentificationItem) {
		List<IonType> ionTypeList=spectrumIdentificationItem.getFragmentation().getIonType();
		int sum=0;
		for(IonType ionType:ionTypeList){
			List<FragmentArray> fragmentArrayList=ionType.getFragmentArray();
			List<Float> intensitys=fragmentArrayList.get(1).getValues();
			sum+=intensitys.size();
		}
		return sum;
	}

	@Override
	public MzIdentMLUnmarshallerAdaptor getMzIdentMLUnmarshallerAdaptor() {
		return mzIdentMLControllerImpl.getUnmarshaller();
	}

	/**
	 * 获取double集合的和
	 * @param intensitys
	 * @return
	 */
	public float sumOfList(List<Float> intensitys){
		float sum=0;
		for(Float f:intensitys){
			sum+=f;
		}
		return sum;
	}
	
}
