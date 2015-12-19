package cqupt.swxxxy.utils;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

public class LinuxFileMonitor {
	FileAlterationMonitor monitor = null;  
    public LinuxFileMonitor(long interval) throws Exception {  
        monitor = new FileAlterationMonitor(interval);  
    }  
    
    public void monitor(String path, FileAlterationListener listener) {  
        FileAlterationObserver observer = new FileAlterationObserver(new File(path));  
        monitor.addObserver(observer);  
        observer.addListener(listener);  
    }  
    //Í£Ö¹¼àÌý
    public void stop() throws Exception{  
        monitor.stop();  
    }  
    //Æô¶¯¼àÌý
    public void start() throws Exception {  
        monitor.start();  
    }  
    public static void main(String[] args) throws Exception {  
        LinuxFileMonitor m = new LinuxFileMonitor(5000);  
        m.monitor(new ReadConfiguration().getMap().get("linuxInputFilePath"),new FileListener());  
        m.start();  
    }  

}
