package cqupt.swxxxy.test.prideXML;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class IteratorTest {

	public String getAddNum(int num){
		StringBuilder sb=new StringBuilder();
		if(num>0){
			for(int i=0;i<num;i++){
				sb.append("+");
			}
			System.out.println(sb.toString());
			return sb.toString();
		}else{
			return "";
		}
	}
	
	public static void main(String[] args) {
		/*Map<String,String> map=new HashMap<String,String>();
		map.put("11", "ouyang");
		map.put("22", "chao");
		Iterator it=map.keySet().iterator();
		while(it.hasNext()){
			String key=it.next().toString();
			System.out.println(key);
		}
		Comparable cmp=1;
		String cmpString=cmp.toString();
		System.out.println(cmpString);*/
		IteratorTest it=new IteratorTest();
		it.getAddNum(5);
	}
}
