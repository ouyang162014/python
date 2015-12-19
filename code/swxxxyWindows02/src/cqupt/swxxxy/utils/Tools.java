package cqupt.swxxxy.utils;

/**
 * 工具类
 * @author Administrator
 *
 */
public class Tools {
	
	/**
	 * 获取正电荷数目
	 * 
	 * @param num
	 * @return
	 */
	public String getAddNum(int num) {
		StringBuilder sb = new StringBuilder();
		if (num > 0) {
			for (int i = 0; i < num; i++) {
				sb.append("+");
			}
			return sb.toString();
		} else {
			return "";
		}
	}
}
