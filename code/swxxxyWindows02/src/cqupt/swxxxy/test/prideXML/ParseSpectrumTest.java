package cqupt.swxxxy.test.prideXML;

import java.io.File;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.CachedDataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.PrideXmlControllerImpl;
import uk.ac.ebi.pride.utilities.data.core.Spectrum;
import cqupt.swxxxy.prideXML.impl.ParseSpectrumImpl;
import cqupt.swxxxy.prideXML.intf.ParseSpectrumIntf;
import cqupt.swxxxy.utils.Constants;

public class ParseSpectrumTest {

	public static void main(String[] args) {
		String filePath=Constants.prideXmlFilePath;
		File inputFile=new File(filePath);
		CachedDataAccessController cachedDataAccessController=new PrideXmlControllerImpl(inputFile);
		ParseSpectrumIntf ppi=new ParseSpectrumImpl(cachedDataAccessController);
		List<Spectrum> spectrumList=ppi.getSpectrums();
		Spectrum s=spectrumList.get(0);
		Map<String,String> cvParams=ppi.getPeakMZTimeChargeState(s);
	}

}
