package csii.pe.ibsExtend;
import java.util.Iterator;
import java.util.Map;

import com.csii.ibs.workflow.AbstractTrsWorkflow;
import com.csii.pe.action.Action;
import com.csii.pe.action.Executable;
import com.csii.pe.action.PlaceholderAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

/*
 */
@SuppressWarnings("unchecked")
public class PayExecuteTemplate extends AbstractTrsWorkflow {

	public void execute(Context context) throws PeException {

		Map templateActions = getActions();

		Iterator it = templateActions.values().iterator();
		while (it.hasNext()) {

			Action action = (Action) it.next();

			if (action instanceof PlaceholderAction) {
				try {
					exeConfigAction(context);
				} catch (PeException e) {
					Executable aftAction =
						(Executable) getAction("aftAction", context);		  
//					aftAction.execute(context);
//					e.printStackTrace();
					throw e;
				}
			} else {
				if (log.isDebugEnabled()) {
					log.debug(
						action.getClass().getName() + "============>start");
				}
				doInternal(context, action);

				if (log.isDebugEnabled()) {
					log.debug(action.getClass().getName() + "============>end");
				}
			}

		}
	}

	private void exeConfigAction(Context context) throws PeException {
		Map transActions = context.getTransactionConfig().getActions();
		Iterator it = transActions.values().iterator();
		while (it.hasNext()) {
			Action action = (Action) it.next();

			if (log.isDebugEnabled()) {
				log.debug(action.getClass().getName() + "============>start");
			}
			doInternal(context, action);

			if (log.isDebugEnabled()) {
				log.debug(action.getClass().getName() + "============>end");
			}
		}
	}

	protected void doInternal(Context context, Action action)
		throws PeException {
		if (action instanceof Executable)
			 ((Executable) action).execute(context);
		else
			throw new PeException(
				"system.executable_mismatch",
				new Object[] {
					action.getClass().getName(),
					context.getTransactionId()});
	}
}
