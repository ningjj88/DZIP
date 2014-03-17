/**
 *
 */
package csii.dzip.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.csii.pe.transform.TransformException;
import com.csii.pe.transform.TransformerElement;
import com.csii.pe.transform.object.ObjectTransformer;
import com.csii.pe.transform.stream.VirtualSegment;
import com.csii.pe.util.config.Element;
import com.csii.pe.util.config.Include;
import com.csii.pe.validation.rule.OgnlExpr;

import csii.base.constant.Constants;
import csii.dzip.action.util.DzipProcessTemplate;

/**
 * @author 张永庆
 * @version 1.0.0
 * @since 2010-7-20
 */
public class DzipVirtualSegment extends VirtualSegment{

	public VirtualSegment vs = null;

	private DzipProcessTemplate dzipProcessTemplate = null;

	public DzipVirtualSegment(){
	}

	public DzipVirtualSegment(VirtualSegment vs){
		this.vs = vs;
	}

	@SuppressWarnings("unchecked")
	public Object parse(Object paramArrayOfByte, Map map) throws TransformException {
		Object arrayOfByte;
		if (paramArrayOfByte instanceof InputStream)
			arrayOfByte = (InputStream) paramArrayOfByte;
		else
			arrayOfByte = new ByteArrayInputStream((byte[]) paramArrayOfByte);
		String transactionId=null;//交易ID
		Object object = new LinkedHashMap();
		Object children[] = vs.getChildren();
		for (int i = 0; i < children.length; i++) {
			Element element = (Element) children[i];
			if (element instanceof Include) {
				Include include = (Include) element;
				String condition = include.getCondition();  //获得KeyName的条件
				if (condition != null) {     				//如果条件不为空
					OgnlExpr ognlexpr = new OgnlExpr();
					boolean flag = ((Boolean) ognlexpr.getValue(condition, null, object)).booleanValue();
					if (!flag)
						continue;
				}
				StringBuffer subKeyValue = new StringBuffer();
				String prefixName = null;
				HashMap hashmap = new HashMap(3);
				boolean b = true;
				if (include.getKeyName() != null) {
					String keyName = include.getKeyName();
					String keyValue = (String) ((HashMap) (object)).get(keyName); 									//获得主键值
					String serviceConditionCode = (String) ((HashMap) (object)).get(Constants.ISO8583_SERCONDCODE); //服务点条件码
					if(keyValue != null){
						subKeyValue  =subKeyValue.append(keyValue.substring(0,2));
						 //06:预授权; 60：追加预授权;28:代收, 91:现金充值撤销,现金充值撤销冲正
						if(Constants.PE_06.equals(serviceConditionCode)
							|| Constants.PE_60.equals(serviceConditionCode)
							|| Constants.PE_28.equals(serviceConditionCode)
							|| (Constants.PE_91.equals(serviceConditionCode) && Constants.PE_17.equals(subKeyValue.toString()))){
							subKeyValue = subKeyValue.append(serviceConditionCode);
						}
					}
					hashmap.put(keyName, subKeyValue);  				//截取主键的前两位
				}else{
						hashmap.put(Constants.ISO8583_PRO_CODE, Constants.PE_NULL);  	//若没有主键，补空
				}
				if(b){
					if (include.getPrefixName() != null) {
						prefixName = (String) ((HashMap) (object)).get(include.getPrefixName());
						if (prefixName == null)
							prefixName = (String) map.get(include.getPrefixName());
						hashmap.put(include.getPrefixName(), prefixName);
					}
					//获得交易ID
					if(Constants.MSG_TYPE_0620.equals(prefixName)||Constants.MSG_TYPE_0630.equals(prefixName)){
						//脚本处理结果通知交易不需要交易码
						transactionId=prefixName;
						hashmap.put(Constants.ISO8583_PRO_CODE,"");
					}else{
						transactionId=prefixName+subKeyValue.toString();
					}
					TransformerElement transformerelement = (TransformerElement) ((Include) element).getElement("p", hashmap);
					if (transformerelement instanceof ObjectTransformer) {
						object = (HashMap) transformerelement.parse(object, map);
					} else {
						Object obj4 = transformerelement.parse(arrayOfByte, map);
						((HashMap) (object)).putAll((Map) obj4);
					}
				}
			} else {
				parse(element, arrayOfByte, object, map);
			}

		}
		//获得交易ID
		((HashMap) (object)).put(Constants.TransactionId,transactionId);

		return object;
	}

	@SuppressWarnings("unchecked")
	public void parse(Element element, Object arrayOfByte, Object object, Map map) throws TransformException {
		Object obj3 = ((TransformerElement) element).parse(arrayOfByte, ((Map) (map != null ? map : ((Map) (object)))));
		((HashMap) (object)).putAll((Map) obj3);
	}

	public void setVirtualSegment(VirtualSegment vs){
		this.vs = vs;
	}

	public VirtualSegment getVirtualSegment(){
		return vs;
	}

	public DzipProcessTemplate getDzipProcessTemplate() {
		return dzipProcessTemplate;
	}

	public void setDzipProcessTemplate(DzipProcessTemplate dzipProcessTemplate) {
		this.dzipProcessTemplate = dzipProcessTemplate;
	}

}
