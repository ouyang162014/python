package cqupt.swxxxy.utils;

/**
 * ������
 * @author Administrator
 *
 */
public class Tools {
	
	/**
	 * ��ȡ�������Ŀ
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
