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
	     * 监听所添加的文件
	     * 调用接口进行文件解析
	     */
	    @Override  
	    public void onFileCreate(File file) { 
	    	System.out.println("onFileCreate:" + file.getName()); 
			//读取文件
	    	readFile(file);
	         
	    }  
	    
	    /**
	     * 读取文件
	     * @param file
	     */
	    public void readFile(File file){
	    	Logger log=Logger.getLogger(Logger.class);
	    	//判断文件是否被占用
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
	    	//判断文件是否被占用
	    	if(f1==f2){
	    		log.info("********文件上传完成*************");
	    		//文件没有被占用
	    		//获取上传文件路径
				String inputFileName=getMap().get("windowsInputFilePath")+file.getName();
	    		if(file.getName().toLowerCase().contains("pride")){
					//prideXML文件
					PrideXMLDaoIntf prideXMLDaoIntf=new PrideXMLDaoImpl(inputFileName);
					prideXMLDaoIntf.insertAll();
				}else if(file.getName().toLowerCase().contains("mzid")){
					//mzIdentML文件
					MzIdentMLDaoInt mzIdentMLDaoInt=new MzIdentMLDaoImpl(inputFileName);
					mzIdentMLDaoInt.insertAll();
				}
	    	}else{
	    		//文件被占用
	    		log.info("********文件正在上传，请等待5秒*************");
	    			//等待10秒
	    		//判断文件是否被占用
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
