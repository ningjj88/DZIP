package csii.dzip.action.platformAdmin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.csii.ibs.action.IbsQueryAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.service.id.IdFactory;

import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.dzip.core.Dict;
import csii.dzip.core.InitData4Dzip;
import csii.pe.service.comm.StringChange;

public class ValidateICDataAction extends IbsQueryAction {
	
	private IdFactory peJournalNOIdFactory;

	public IdFactory getPeJournalNOIdFactory() {
		return peJournalNOIdFactory;
	}

	public void setPeJournalNOIdFactory(IdFactory peJournalNOIdFactory) {
		this.peJournalNOIdFactory = peJournalNOIdFactory;
	}

	@Override
	public void execute(Context ctx) throws PeException {
		ctx.setData(Dict.TRXTYPE, Constants.IC_ICDATAVALIDATE);
		ctx.setData(Dict.RETCODE, Constants.IC_NULLRETCODE);
		ctx.setData(Dict.INSTNO, Util.fill(InitData4Dzip.getForwOrgCd4Ic(), 10, Constants.RIGHT, Constants.PE_SPACE));
		ctx.setData(Dict.TRANSEQ, peJournalNOIdFactory.generate());
		DateFormat df = new SimpleDateFormat("MMddHHmmss");
		ctx.setData(Dict.TRANTIME, df.format(ctx.getData("IBSTrsTimestamp")));
		ctx.setData(Dict.RSPNO, Constants.IC_NULLRETCODE);
		String icData = StringChange.HexToString((String) ctx.getData(Dict.ICDATA));
		ctx.setData(Dict.ICDATALENGTH, icData.length());
		ctx.setData(Dict.ICDATA, icData);
		super.execute(ctx);
		String icDataRes = (String) ctx.getData(Dict.ICDATA);
		int icDataLenRes = Integer.parseInt(ctx.getData(Dict.ICDATALENGTH).toString());
		ctx.setData(Dict.ICDATA, StringChange.StringToHex(icDataRes.substring(0, icDataLenRes)));
	}

}
