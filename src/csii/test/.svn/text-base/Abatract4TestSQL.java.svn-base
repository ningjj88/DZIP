/**
 * 
 */
package csii.test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import csii.base.constant.Constants;

/**
 * @author 张永庆
 * @version 1.0.0
 * @since 2010-6-6
 */
public abstract class Abatract4TestSQL {
	 
//	private static final String CONFIGLOCATION = "/config/svc_localdb.xml";
	private static final String CONFIGLOCATION = "/config/svc_corebankdb.xml";
	private static final String DRIVERCLASSNAME = "oracle.jdbc.OracleDriver";
	private static final String URL = "jdbc:oracle:thin:@192.168.1.9:1521:test";
	private static final String USERNAME = "osibank";
	private static final String PASSWORD = "osibank";
//	private static final String DRIVERCLASSNAME = "com.mysql.jdbc.Driver";
//	private static final String URL = "jdbc:mysql://localhost:3306/test";
//	private static final String USERNAME = "root";
//	private static final String PASSWORD = "admin";
	 
	public static void main(String[] args) throws SQLException {
		 Map sqlMap = new HashMap();
//		 resultMap = (Map)corebankAccess.getSqlMap().queryForObject("common.getNtwkNodeInfo",resultMap);
//		 map.put("coreRespCD", "");
		 sqlMap.put("in_TerminalCd", "06070001");
//		 sqlMap.put("RtxnCatCd", "03");
//		 sqlMap.put("TranCd", "042000");
//		 map.put("trancd", "002002");
//		List list =  queryForList("common.queryHostStat", hostLogStat);
//	      update("common.updateHostStat", hostLogStat);
//		for(Iterator iterator = list.iterator();iterator.hasNext();){
//			Map map  = (Map) iterator.next();
		 queryForObject("common.getNtwkNodeInfo",sqlMap);
			 System.out.println(sqlMap);
//		}
}
		

	public static Object queryForObject(String statementName, Object parameterObject){
		return getSqlMapClientTemplate().queryForObject(statementName, parameterObject);
	}
	
	public static List queryForList(String statementName, Object parameterObject){
		return getSqlMapClientTemplate().queryForList(statementName, parameterObject);
	}
	public static void  update(String statementName, Object parameterObject){
		getSqlMapClientTemplate().update(statementName, parameterObject);
	}

	public static SqlMapClientTemplate getSqlMapClientTemplate() {
		ApplicationContext factory = new ClassPathXmlApplicationContext(CONFIGLOCATION);
//		SqlMapClientTemplate sqlMapExecutor = (SqlMapClientTemplate) factory.getBean("localdb.ibsdbSqlMapExecutor");
		SqlMapClientTemplate sqlMapExecutor = (SqlMapClientTemplate) factory.getBean("corebankdb.ibsdbSqlMapExecutor");
		sqlMapExecutor.setDataSource(getDataSource());
		return sqlMapExecutor;
	}

	public static DriverManagerDataSource getDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(DRIVERCLASSNAME);
		dataSource.setUrl(URL);
		dataSource.setUsername(USERNAME);
		dataSource.setPassword(PASSWORD);
		return dataSource;
	}
}
