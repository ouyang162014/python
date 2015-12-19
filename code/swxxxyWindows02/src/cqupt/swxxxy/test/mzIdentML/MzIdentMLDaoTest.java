package cqupt.swxxxy.test.mzIdentML;

import java.sql.SQLException;
import java.util.Map;

import cqupt.swxxxy.dao.Intf.MzIdentMLDaoInt;
import cqupt.swxxxy.dao.impl.MzIdentMLDaoImpl;
import cqupt.swxxxy.utils.ReadConfiguration;

public class MzIdentMLDaoTest {

	public static void main(String[] args) {
		ReadConfiguration rc=new ReadConfiguration();
		Map<String,String> conf=rc.getProperties();
		//String url=conf.get("url");
		String inputFilePath=conf.get("windowsInputFilePath");
		try {
			MzIdentMLDaoInt mzIdentMLDaoIntf=new MzIdentMLDaoImpl(inputFilePath);
			mzIdentMLDaoIntf.insertAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
