
package csii.pe.service.schedule;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.csii.pe.service.schedule.Alarm;

public class BeanAlarmLoader extends AlarmLoader implements InitializingBean,
		ApplicationContextAware {

	public BeanAlarmLoader() {
	}

	public void setEntries(List list) {
		entries = list;
	}

	public void setApplicationContext(ApplicationContext applicationcontext)
			throws BeansException {
		appcontext = applicationcontext;
	}

	public void afterPropertiesSet() throws Exception {
		Map map = appcontext
				.getBeansOfType(com.csii.pe.service.schedule.AlarmRecord.class);
		AlarmRecord alarmrecord;
		for (Iterator iterator = map.values().iterator(); iterator.hasNext(); entries.add(alarmrecord))
			alarmrecord = (AlarmRecord) iterator.next();

	}

	public void load(Alarm alarm, Alarm.Trigger trigger) {
		if (entries != null) {
			AlarmRecord alarmrecord;
			for (Iterator iterator = entries.iterator(); iterator.hasNext(); 
			loadEntry(
					alarm, alarmrecord, trigger))
				alarmrecord = (AlarmRecord) iterator.next();

		}
	}

	private ApplicationContext appcontext;
	private List entries;
}
