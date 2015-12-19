package cqupt.swxxxy.test.mzIdentML;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import psidev.psi.tools.xxindex.index.IndexElement;
import cqupt.swxxxy.mzIdentML.impl.ParseSpectrumIdentificationImpl;
import cqupt.swxxxy.mzIdentML.intf.ParseSpectrumIdentificationIntf;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.CachedDataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.MzIdentMLControllerImpl;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.ReferencedIdentificationController;
import uk.ac.ebi.pride.utilities.data.core.IdentificationMetaData;
import uk.ac.ebi.jmzidml.model.mzidml.IonType;
import uk.ac.ebi.jmzidml.model.mzidml.Sample;
import uk.ac.ebi.jmzidml.model.mzidml.SpectraData;
import uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationItem;
import uk.ac.ebi.pride.utilities.data.core.Spectrum;
import uk.ac.ebi.pride.utilities.data.core.SpectrumIdentificationProtocol;
import uk.ac.ebi.pride.utilities.data.io.file.MzIdentMLUnmarshallerAdaptor;

/**
 * mzIdentML文件SpectrumIdentificationResult模块解析测试
 * @author Administrator
 *
 */
public class ParseSpectrumIdentificationTest {

	public static void main(String[] args) throws JAXBException {
		// 文件路径
		String filePath = "D:\\mzIdentML\\mzidentml-example.mzid";
		// 创建文件
		File file = new File(filePath);
		// 创建CachedDataAccessController对象
		MzIdentMLControllerImpl mzIdentMLControllerImpl = new MzIdentMLControllerImpl(file, true);
		//ReferencedIdentificationController referencedIdentificationController=new ReferencedIdentificationController(file,);
		ParseSpectrumIdentificationIntf ParseSpectrumIdentificationIntf=new ParseSpectrumIdentificationImpl(mzIdentMLControllerImpl);
		MzIdentMLUnmarshallerAdaptor mzIdentMLUnmarshallerAdaptor=ParseSpectrumIdentificationIntf.getMzIdentMLUnmarshallerAdaptor();
		Map<String, Map<String, List<IndexElement>>> map=mzIdentMLUnmarshallerAdaptor.getScannedIdMappings();
		SpectrumIdentificationItem spectrumIdentificationItem=mzIdentMLUnmarshallerAdaptor.getSpectrumIdentificationsById("SII_564_1");
		List<IonType> ionTypeList = spectrumIdentificationItem.getFragmentation().getIonType();
		if (ionTypeList != null && ionTypeList.size() > 0) {
			// 写入sequence
			for (IonType ionType : ionTypeList) {
				//获取m_mz
				List<Float> mz=ionType.getFragmentArray().get(0).getValues();
				//获取m_intensity
				List<Float> intensity=ionType.getFragmentArray().get(1).getValues();
				//获取m_error
				List<Float> error=ionType.getFragmentArray().get(2).getValues();
				String ion=ionType.getCvParam().getValue();
				int charge=ionType.getCharge();
				for(int i=0;i<mz.size();i++){
					String s=mz.get(i) + " " + intensity.get(i) + " \"" + "y" + (ion!=null?ion:"")
						 + "/" + error.get(i) + "\"" + "\t\n";
					int j=1;
				}
			}
		}
		int i=1;
	}

}
