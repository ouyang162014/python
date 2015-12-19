package cqupt.swxxxy.utils;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

import com.sun.istack.logging.Logger;

import cqupt.swxxxy.dao.Intf.MzIdentMLDaoInt;
import cqupt.swxxxy.dao.Intf.PrideXMLDaoIntf;
import cqupt.swxxxy.dao.impl.MzIdentMLDaoImpl;
import cqupt.swxxxy.dao.impl.PrideXMLDaoImpl;

public class FileListener extends ReadConfiguration implements FileAlterationListener{
	 	//FileMonitor monitor = null;  
	    @Override  
	    public void onStart(FileAlterationObserver observer) {  
	        System.out.println("onStart");  
	    }  
	    @Override  
	    public void onDirectoryCreate(File directory) {  
	        System.out.println("onDirectoryCreate:" +  directory.getName());  
	    }  
	  
	    @Override  
	    public void onDirectoryChange(File directory) {  
	        System.out.println("onDirectoryChange:" + directory.getName());  
	    }  
	  
	    @Override  
	    public void onDirectoryDelete(File directory) {  
	        System.out.println("onDirectoryDelete:" + directory.getName());  
	    }  
	  
	    /**
	     * ��������ӵ��ļ�
	     * ���ýӿڽ����ļ�����
	     */
	    @Override  
	    public void onFileCreate(File file) { 
	    	System.out.println("onFileCreate:" + file.getName()); 
			//��ȡ�ļ�
	    	readFile(file);
	         
	    }  
	    
	    /**
	     * ��ȡ�ļ�
	     * @param file
	     */
	    public void readFile(File file){
	    	Logger log=Logger.getLogger(Logger.class);
	    	//�ж��ļ��Ƿ�ռ��
	    	float f1=file.length();
	    	//log.info("------------"+f1+"----------------");
	    	float f2=0;
	    	try{
	    		Thread.sleep(5000);
	    		f2=file.length();
	    		//log.info("------------"+f2+"----------------");
	    	}catch(Exception e){
	    		//e.printStackTrace();
	    	}
	    	//�ж��ļ��Ƿ�ռ��
	    	if(f1==f2){
	    		log.info("********�ļ��ϴ����*************");
	    		//�ļ�û�б�ռ��
	    		//��ȡ�ϴ��ļ�·��
				String inputFileName=getMap().get("windowsInputFilePath")+file.getName();
	    		if(file.getName().toLowerCase().contains("pride")){
					//prideXML�ļ�
					PrideXMLDaoIntf prideXMLDaoIntf=new PrideXMLDaoImpl(inputFileName);
					prideXMLDaoIntf.insertAll();
				}else if(file.getName().toLowerCase().contains("mzid")){
					//mzIdentML�ļ�
					MzIdentMLDaoInt mzIdentMLDaoInt=new MzIdentMLDaoImpl(inputFileName);
					mzIdentMLDaoInt.insertAll();
				}
	    	}else{
	    		//�ļ���ռ��
	    		log.info("********�ļ������ϴ�����ȴ�5��*************");
	    			//�ȴ�10��
	    		//�ж��ļ��Ƿ�ռ��
	    		readFile(file);
	    	}
	    }
	  
	    @Override  
	    public void onFileChange(File file) {  
	        System.out.println("onFileCreate : " + file.getName());  
	    }  
	  
	    @Override  
	    public void onFileDelete(File file) {  
	        System.out.println("onFileDelete :" + file.getName());  
	    }  
	  
	    @Override  
	    public void onStop(FileAlterationObserver observer) {  
	        //System.out.println("onStop");  
	    }  
}
