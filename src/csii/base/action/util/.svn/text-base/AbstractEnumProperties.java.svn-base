/**
 * 
 */
package csii.base.action.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 枚举量基类，帮助将枚举量实现map接口访问
 * 
 * @author shengzt
 */
public abstract class AbstractEnumProperties implements Serializable, Map<String, EnumValue> {

	private static final long serialVersionUID = -8919240892323246712L;

	private Map<String, EnumValue> map = new HashMap<String, EnumValue>();
		
	/**
	 * 初始化枚举数据
	 * 
	 * @param orig 枚举数据，格式{ {枚举值1，描述1}, {枚举值1，描述1}, {...}, ...}
	 */
	public void init(String[][] orig) {
		for (int i = 0; i < orig.length; i++) {
			map.put(orig[i][0], new EnumValue(orig[i][0],orig[i][1]));
		}
	}

	/**
	 * 检查值合法性
	 * 
	 * @param value 值
	 */
	public void verify(String value) {
		if (!map.containsKey(value)) {
			
			StringBuilder sb = new StringBuilder();
			sb.append("invalid value: ");
			sb.append(value);
			
			throw new IllegalArgumentException(sb.toString());
		}
	}
	
	/**
	 * 禁止此方法
	 */
	public void clear() { }

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public Set<java.util.Map.Entry<String, EnumValue>> entrySet() {
		return map.entrySet();
	}

	public EnumValue get(Object key) {
		return map.get(key);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set<String> keySet() {
		return map.keySet();
	}

	/**
	 * 禁止此方法
	 */
	public EnumValue put(String key, EnumValue value) {
		return null;
	}

	/**
	 * 禁止此方法
	 */
	public void putAll(Map<? extends String, ? extends EnumValue> t) { }

	/**
	 * 禁止此方法
	 */
	public EnumValue remove(Object key) {
		return null;
	}

	public int size() {
		return map.size();
	}

	public Collection<EnumValue> values() {
		return map.values();
	}

}
