/*
 * 创建日期 2006-3-23
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package csii.base.action.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;


import com.csii.pe.core.PeException;
import com.csii.pe.service.jdbc.SqlExecutor;


/**
 * @author db2admin
 *
 * TODO 要更改此生成的类型注释的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */

public final class BatchSqlConstants {
	//数据库常用保留子
	private final static String SQL_SELECT="SELECT ";
	private final static String SQL_INSERT="INSERT INTO ";
	private final static String SQL_VALUES=" VALUES";
	private final static String SQL_UPDATE="UPDATE ";
	private final static String SQL_SET=" SET ";
	private final static String SQL_DELETE="DELETE ";
	private final static String SQL_WHERE=" WHERE ";
	private final static String SQL_GROUP=" GROUP BY ";
	private final static String SQL_ORDER=" ORDER BY ";
	private final static String SQL_FROM=" FROM ";
	private final static String ORDER_BY_ASC=" ASC ";
	private final static String ORDER_BY_DESC=" DESC ";
	public final static String  FETCH_TOP_RECORD=" fetch first 1000 rows only";
	public final static String  FETCH_FIRST_RECORD=" fetch first 1 rows only";
	private final static String LEFT_PARENTHESIS=" ( ";
	private final static String RIGHT_PARENTHESIS=" ) ";
	private final static String COMMA=",";
	private final static String QUESTION="? ";
	private final static String EQUALS=" = ";
	private final static String AND=" AND ";
	private final static String OR=" OR ";
	public final static int BATCH_UNIT=1000;
	
	
	public final static String getInsertSql(String table,String[] fields,String[] addFields){
		StringBuffer insertSql = new StringBuffer();
		insertSql.append(SQL_INSERT).append(table);
		insertSql.append(LEFT_PARENTHESIS).append(getSqlFieldString(fields));
		if((addFields!=null) && addFields.length>0){
			insertSql.append(COMMA).append(getSqlFieldString(addFields));
		}
		insertSql.append(RIGHT_PARENTHESIS);
		insertSql.append(SQL_VALUES);
		insertSql.append(LEFT_PARENTHESIS).append(geSqltParamString(fields));
		if((addFields!=null) && addFields.length>0){
			insertSql.append(COMMA).append(geSqltParamString(addFields));
		}
		insertSql.append(RIGHT_PARENTHESIS);
		return insertSql.toString();
	}
	
	
	public final  static String[] clearString(String[] object,String[] clearObj  ){
		String[] result = new String[object.length-clearObj.length];
		if((object!=null) && (object.length >0)){
			int j=0;
			for(int i=0;i<object.length ;i++){
				String  obj = object[i];
				if(!isContain(clearObj,obj)){
					result[j]=obj;
					j++;
				}
			}
		}
		return result ;
	}
	public final  static Object[] clearObject(Object[] object,Object[] clearObj  ){
		Object[] result = new Object[object.length-clearObj.length];
		if((object!=null) && (object.length >0)){
			int j=0;
			for(int i=0;i<object.length ;i++){
				Object  obj =  object[i];
				if(!isContain(clearObj,obj)){
					result[j]=obj;
					j++;
				}
				
			}
		}
		return result ;
	}
	public final static boolean isContain(Object[]  object,Object obj){
		if((object!=null) && (object.length>0)){
			for(int i=0;i<object.length  ;i++){
				if(obj.equals(object[i])){
					return true;
				}
			}
		}
		return false;
	}

	private final static String getSqlFieldString(String[] fields){
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<fields.length;i++){
			if (i == 0) {
				sb.append(fields[i]);
			} else {
				sb.append(COMMA);
				sb.append(fields[i]);
			}
		}
		return sb.toString() ;
	}
	private final static String getUpdateFieldString(String[] fields){
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<fields.length;i++){
			if (i == 0) {
				sb.append(fields[i]).append(EQUALS).append(QUESTION);
			} else {
				sb.append(COMMA);
				sb.append(fields[i]).append(EQUALS).append(QUESTION);
			}
		}
		return sb.toString() ;
	}
	public final static String getOrderByCondition(String[] fields,boolean isUp){
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<fields.length;i++){
			if ((i>0))  {
				    sb.append(COMMA);
			}	
			sb.append(fields[i]);
			if(isUp){
					sb.append(ORDER_BY_ASC);
			}else {
					sb.append(ORDER_BY_DESC);
			}	
 
		}
		return sb.toString() ;
	}
	public final static String getSqlCondition(String[] fields,boolean isAndconjunctive){
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<fields.length;i++){
			if (i == 0) {
                if(fields.length >1){
                	sb.append(LEFT_PARENTHESIS).append(fields[i]).append(EQUALS).append(QUESTION).append(RIGHT_PARENTHESIS);
                }else{
                	sb.append(fields[i]).append(EQUALS).append(QUESTION);
                }
				
			} else {
				if(isAndconjunctive){
					sb.append(AND);
				}else {
					sb.append(OR);
				}
				sb.append(LEFT_PARENTHESIS).append(fields[i]).append(EQUALS).append(QUESTION).append(RIGHT_PARENTHESIS);
			}
		}
		return sb.toString() ;
	}
	public final static  String geSqltParamString(String[] fields) {
		return getSqlParamString(fields.length);
	}
	public final static  String getSqlParamString(int length) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				buf.append(QUESTION);
			} else {
				buf.append(COMMA);
				buf.append(QUESTION);
			}
		}
		return buf.toString();
	}
	
	public static Object[] getValueByKey(Object[] key,Map data){
		Object[] object = new Object[key.length];
		for(int i=0;i<key.length ;i++){
			object[i] = data.get(key[i]);
		}
		return object;
	}

	public static Object[] merageArrays(Object[] object1,Object[] object2){
		Object[] object = new Object[object1.length+object2.length];
		for(int j=0;j<object1.length;j++){
			object[j] = object1[j];
		}
		for(int i=0;i<object2.length;i++){
			object[object1.length+i]=object2[i];
		}
		return object;
	}
	
	public static String[] merageArrays(String[] object1,String[] object2){
		String[] object = new String[object1.length+object2.length];
		for(int j=0;j<object1.length;j++){
			object[j] = object1[j];
		}
		for(int i=0;i<object2.length;i++){
			object[object1.length+i]=object2[i];
		}
		return object;
	}
	
	public static void main(String[] args) {}}