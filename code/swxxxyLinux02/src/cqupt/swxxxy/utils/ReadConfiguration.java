package cqupt.swxxxy.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ReadConfiguration {
	public Map<String, String> map;

	public ReadConfiguration() {
		map = getProperties();
	}

	public Map<String, String> getProperties() {
		Map<String, String> map = new HashMap<String, String>();
		String str;
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			String filePath = "/home/iprox/conf/conf.properties";
			// 文件输入流
			fis = new FileInputStream(filePath);
			// 读取文件
			isr = new InputStreamReader(fis);
			// 缓冲流
			br = new BufferedReader(isr);
			while ((str = br.readLine()) != null) {
				//获取key
				String key = str.substring(0, str.indexOf("="));
				//获取value
				String value = str
						.substring(str.indexOf("=") + 1, str.length());
				map.put(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				//关闭缓冲流
				if (br != null) {
					br.close();
				}
				//关闭文件读入流
				if (isr != null) {
					isr.close();
				}
				//关闭文件流
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	public Map<String, String> getMap() {
		return map;
	}

}
