/**
 * 
 */
package csii.base.action.util;

import java.io.Serializable;

/**
 * 只读BEAN，保存枚举值及值描述
 * 
 * @author shengzt
 */
public class EnumValue implements Serializable {

	private static final long serialVersionUID = -4530982059905745465L;

	private String value;
	private String description;
	
	public EnumValue(String value, String description) {
		super();
		
		this.value = value;
		this.description = description;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

}
