
package csii.pe.service.schedule;

import java.sql.Timestamp;
import java.util.Date;

public class AlarmRecord {

	public AlarmRecord() {
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int i) {
		mode = i;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String s) {
		key = s;
	}

	public Object getRule() {
		return rule;
	}

	public void setRule(Object obj) {
		rule = obj;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date date) {
		beginTime = date;
	}

	public void setBeginTimeString(String s) {
		beginTime = Timestamp.valueOf(s);
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date date) {
		endTime = date;
	}

	public void setEndTimeString(String s) {
		endTime = Timestamp.valueOf(s);
	}

	public int getExecTimes() {
		return execTimes;
	}

	public void setExecTimes(int i) {
		execTimes = i;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public void setTimestampString(String s) {
		this.timestamp = Timestamp.valueOf(s);
	}

	public static final int MODE_CRON = 0;
	public static final int MODE_ONCE = 1;
	public static final int MODE_RELATIVE = 2;
	private int mode;
	private String key;
	private Object rule;
	private Date beginTime;
	private Date endTime;
	private int execTimes;
	private Timestamp timestamp;
}
