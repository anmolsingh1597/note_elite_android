package com.lambton.note_elite_android.utils;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

public class TimeUtils{
	public static final PrettyTime prettyTime;

	static{
		prettyTime = new PrettyTime();
	}

	public static String getHumanReadableTimeDiff(Date lastTime){
		if (lastTime == null) return "";
		prettyTime.setReference(new Date());
		return prettyTime.format(lastTime);
	}
}
