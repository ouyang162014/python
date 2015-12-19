package cqupt.swxxxy.utils;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

public class WindowsFileMonitor {
	FileAlterationMonitor monitor = null;  
    public WindowsFileMonitor(long interval) throws Exception {  
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
        WindowsFileMonitor m = new WindowsFileMonitor(5000);  
        m.monitor(new ReadConfiguration().getMap().get("windowsInputFilePath"),new FileListener());  
        m.start();  
    }  

}
