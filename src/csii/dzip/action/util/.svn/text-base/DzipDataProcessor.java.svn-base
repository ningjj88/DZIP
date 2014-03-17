/**
 * 
 */
package csii.dzip.action.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import csii.base.constant.Constants;

/**
 * @author 张永庆
 * @version 1.0.0
 * @since 2010-6-7
 */
public class DzipDataProcessor {

	public static final String INVALID_PARAMSMAP = "invalidParamsMap";

	/**
	 * 检查交易处理时所需参数是否合法的基本校验
	 * @author 张永庆
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String validateParams(Map paramsMap) {
		if (paramsMap instanceof HashMap) {
			Iterator<Entry<String, String>> it = paramsMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> entry = it.next();
				String key = String.valueOf(entry.getKey());
				String value = String.valueOf(entry.getValue());
				if (value.trim().equals(Constants.PE_NULL)) {
					return key;
				}
			}
			return "";
		} else {
			return INVALID_PARAMSMAP;
		}
	}

	/**
	 * 检查交易处理时所需参数是否合法的基本校验
	 * 
	 * @author 张永庆
	 * @param params
	 * @return
	 */
	public String validateParams(HashMap<String, String> paramsMap, String... params4Check) {
		if (paramsMap instanceof HashMap) {
			int length = params4Check.length;
			if (length < 1) {
				validateParams(paramsMap);
			}
			for (int i = 0; i < length; i++) {
				if (params4Check[i].trim().equals(Constants.PE_NULL)) {
					return params4Check[i];
				}
			}
			return Constants.PE_NULL;
		} else {
			return INVALID_PARAMSMAP;
		}
	}

	/**
	 * 检查交易处理时所需参数是否合法的基本校验
	 * 
	 * @author 张永庆
	 * @param params
	 * @return
	 */
	public String validateParams(HashMap<String, String> paramsMap, List<String> paramsList4Check) {
		if (paramsMap instanceof HashMap) {
			if (paramsList4Check == null || paramsList4Check.isEmpty()) {
				validateParams(paramsMap);
			}
			Iterator<String> it = paramsList4Check.iterator();
			while (it.hasNext()) {
				String param = it.next();
				String temp = paramsMap.get(param);
				if (temp == null || temp.trim().equals(Constants.PE_NULL)) {
					return param;
				}
			}
			return Constants.PE_NULL;
		} else {
			return INVALID_PARAMSMAP;
		}
	}
}
