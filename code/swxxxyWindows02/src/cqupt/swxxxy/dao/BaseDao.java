package cqupt.swxxxy.dao;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import cqupt.swxxxy.utils.Constants;
import cqupt.swxxxy.utils.ReadConfiguration;

/**
 * ���ݿ�������
 * @author Administrator
 *
 */
public class BaseDao {
	//��ȡ�����ļ�
	private static Map<String,String> conf=new ReadConfiguration().getMap();
	public static Map<String, String> getConf() {
		return conf;
	}

	//�������ݿ����ӵ�ַ
	private static String url=conf.get("url");
	
	static Connection conn;
	
	static{
		try{
			Class.forName("com.mysql.jdbc.Driver");
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * ��ȡ����
	 * @return
	 */
	public static Connection getConnection(){
		try {
			conn=(Connection) DriverManager.getConnection(url);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	/**
	 * �ر�����
	 * @param rs
	 * @param ps
	 * @param con
	 */
	public static void closeAll(ResultSet rs,PreparedStatement ps,Connection con){
		try{
			if(rs!=null){
				rs.close();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		try{
			if(ps!=null){
				ps.close();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		try{
			if(con!=null){
				con.close();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
}
