package csii.pe.service.schedule;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.springframework.orm.ibatis.SqlMapClientOperations;

import com.csii.pe.core.PeRuntimeException;
import com.csii.pe.service.schedule.Alarm;
import com.csii.pe.service.schedule.Alarm.Entry;
import com.csii.pe.transform.stream.extern.ExternUtil;

public class DefaultAlarmListener// implements AlarmServer.Listener 
{
	public static interface HolidayChecker {

		public abstract boolean isHoliday(Timestamp timestamp, Map map);
	}

	public DefaultAlarmListener() {
		externFlag = true;
	}

	public void setSqlMap(SqlMapClientOperations sqlmapclientoperations) {
		_fldif = sqlmapclientoperations;
	}

	public void setExternFlag(boolean flag) {
		externFlag = flag;
	}

	public void setStopWhenError(boolean flag) {
		stopWhenErrorFlag = flag;
	}

	public void setQueryAlarmStateSql(String s) {
		queryAlarmStateSql = s;
	}

	public void setQueryAlarmDetailSql(String s) {
		queryAlarmDetailSql = s;
	}

	public void setUpdateAlarmSql(String s) {
		updateAlarmSql = s;
	}

	public void setHolidayChecker(HolidayChecker holidaychecker) {
		this.holidaychecker = holidaychecker;
	}

	public Map beforeHandle(Alarm.Entry entry, String s) {
		boolean flag = false;
		if (queryAlarmStateSql != null) {
			String s1 = (String) _fldif.queryForObject(queryAlarmStateSql, entry
					.attachment());
			flag = !"0".equals(s1);
		}
		if (flag) {
			entry.stop();
			return null;
		}
		if (holidaychecker != null
				&& holidaychecker.isHoliday(new Timestamp(entry.getLastAlarmTime()),
						(Map) entry.attachment()))
			return null;
		Object obj = null;
		if (queryAlarmDetailSql != null) {
			obj = (Map) _fldif.queryForObject(queryAlarmDetailSql, entry.attachment());
			if (externFlag) {
				Object obj1 = ((Map) (obj)).get("_JnlData");
				byte abyte0[] = (byte[]) obj1;
				try {
					Map map = (Map) ExternUtil.deExtern(abyte0);
					((Map) (obj)).putAll(map);
				} catch (Exception exception) {
					throw new PeRuntimeException("ibserror.read_extern_failed",
							exception);
				}
			}
		} else {
			obj = new HashMap();
			if (entry.attachment() != null
					&& (entry.attachment() instanceof Map))
				((Map) (obj)).putAll((Map) entry.attachment());
			if (((Map) (obj)).get(s) == null)
				((Map) (obj)).put(s, entry.getKey());
		}
		((Map) (obj)).put("TimerCallback", new Boolean(true));
		return ((Map) (obj));
	}

	public void afterHandle(Alarm.Entry entry, Throwable throwable) {
		String s = entry.isRepeating() && !entry.isStopped() ? "0" : "1";
		if (throwable != null && stopWhenErrorFlag) {
			entry.stop();
			s = "9";
		}
		if (updateAlarmSql != null
				&& (entry.attachment() != null || (entry.attachment() instanceof Map))) {
			HashMap hashmap = new HashMap((Map) entry.attachment());
			hashmap.put("AlarmState", s);
			_fldif.update(updateAlarmSql, hashmap);
		}
	}

	public static final String STATE_OK = "0";
	public static final String STATE_COMPLETE = "1";
	public static final String STATE_ERROR = "9";
	public static final String FIELD_ALARM_CALLBACK = "TimerCallback";
	public static final String FIELD_ALARM_STATE = "AlarmState";
	public static final String FIELD_JNL_DATE = "_JnlData";
	private boolean externFlag;
	private boolean stopWhenErrorFlag;
	private SqlMapClientOperations _fldif;
	private String queryAlarmStateSql;
	private String queryAlarmDetailSql;
	private String updateAlarmSql;
	private HolidayChecker holidaychecker;
	public Map beforeHandle(Entry entry) {
		Map res=new HashMap();
		res.put("transCode", entry.getKey());
		return res;
	}
}
