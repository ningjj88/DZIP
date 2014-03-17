package csii.base.bean;

import csii.base.action.util.AbstractEnumProperties;

/**
 * 交易状态
 * 
 * @author shengzt
 *
 */
public class RtxnCat extends AbstractEnumProperties{
	private static final long serialVersionUID = 1L;
	
	public static final String RtxnCat_00 = "0";
	public static final String RtxnCat_02 = "02";
	public static final String RtxnCat_03 = "03";
	
	private static final String[][] initValue = {
		{RtxnCat_00,  "本代本"},
		{RtxnCat_02,   "本代他"},
		{RtxnCat_03,   "他代本"}
	};

	public RtxnCat() {	
		super();
		super.init(RtxnCat.initValue);		
	}
	
	public static void main(String[] args) {
		RtxnCat c = new RtxnCat();
		System.out.println(c.get(RtxnCat_00).getValue());
		System.out.println(c.get(RtxnCat_00).getDescription());
	}

}
