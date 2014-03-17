
package csii.dzip.action.cups.PreAuthorization;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.UpdateJoural;

/**
 *@ClassName:&{type_name}
 *@Description：POS预授权撤销冲正
 *@author :chenshq
 *@date：2010-08-16
 */
public class PreAuthCancelRevAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor utilProcessor;

	public void execute(final Context ctx) throws PeException {
		logger.info("CUPS PreAuthCancelRevAction ==================>Start");
	}


	public UpdateJoural getUpdateJoural() {
		return updateJoural;
	}

	public void setUpdateJoural(UpdateJoural updateJoural) {
		this.updateJoural = updateJoural;
	}

	public ActionUtilProcessor getUtilProcessor() {
		return utilProcessor;
	}

	public void setUtilProcessor(ActionUtilProcessor utilProcessor) {
		this.utilProcessor = utilProcessor;
	}

	}
