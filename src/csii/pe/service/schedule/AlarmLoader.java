
package csii.pe.service.schedule;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csii.pe.service.schedule.Alarm;

public abstract class AlarmLoader //implements AlarmServer.Loader 
{
	public static interface EntryResolver {

		public abstract AlarmRecord resolve(Map map);
	}

	public AlarmLoader() {
	}

	public void setKeepRunningWhenException(boolean flag) {
		keepRunningWhenExceptionFlg = flag;
	}

	public void setEntryResolver(EntryResolver entryresolver) {
		this.entryresolver = entryresolver;
	}

	protected Alarm.Entry loadEntry(Alarm alarm, AlarmRecord alarmrecord,
			Alarm.Trigger trigger) {
		String s = alarmrecord.getKey();
		if (s == null)
			if (keepRunningWhenExceptionFlg) {
				_fldif.error("key of entry is null.");
				return null;
			} else {
				throw new IllegalArgumentException("key of entry is null.");
			}
		Object obj = alarmrecord.getRule();
		if (obj == null)
			if (keepRunningWhenExceptionFlg) {
				_fldif.error("rule of entry is null.");
				return null;
			} else {
				throw new IllegalArgumentException("rule of entry is null.");
			}
		int i = alarmrecord.getMode();
		Date date = alarmrecord.getBeginTime();
		Date date1 = alarmrecord.getEndTime();
		int j = alarmrecord.getExecTimes();
		Timestamp timestamp = alarmrecord.getTimestamp();
		if (timestamp != null && (_timestamp == null || timestamp.after(_timestamp)))
			_timestamp = timestamp;
		try {
			Alarm.Entry entry = null;
			switch (i) {
			case 0: // '\0'
				String s1 = (String) obj;
				if (j > 0)
					entry = alarm.addCron(s, s1, trigger, date, j);
				else
					entry = alarm.addCron(s, s1, trigger, date, date1);
				break;

			case 1: // '\001'
				Object obj1 = null;
				if (obj instanceof Date)
					obj1 = (Date) obj;
				else
					obj1 = getTimestamp((String) obj);
				entry = alarm.addOnce(s, ((Date) (obj1)), trigger);
				break;

			case 2: // '\002'
				int k = -1;
				if (obj instanceof Number)
					k = ((Number) obj).intValue();
				else
					k = Integer.parseInt((String) obj);
				if (j > 0)
					entry = alarm.addRelative(s, k, true, trigger, date, j);
				else
					entry = alarm.addRelative(s, k, true, trigger, date, date1);
				break;

			default:
				throw new IllegalArgumentException(
						"alarm mode just support 0-cron, 1-once, 2-relative.");
			}
			return entry;
		} catch (RuntimeException runtimeexception) {
			if (keepRunningWhenExceptionFlg) {
				_fldif.error("add entry error.", runtimeexception);
				return null;
			} else {
				throw runtimeexception;
			}
		}
	}

	protected void load(Alarm alarm, Map map, Alarm.Trigger trigger) {
		if (entryresolver == null)
			throw new IllegalArgumentException(
					"alarm entry resolver is undefined");
		AlarmRecord alarmrecord = entryresolver.resolve(map);
		Alarm.Entry entry = loadEntry(alarm, alarmrecord, trigger);
		if (entry != null)
			entry.attach(map);
	}

	static Timestamp getTimestamp(String s) {
		return Timestamp.valueOf(s);
	}

	final Log _fldif = LogFactory.getLog(getClass());
	private boolean keepRunningWhenExceptionFlg;
	protected Timestamp _timestamp;
	private EntryResolver entryresolver;
}

