package com.kkarlberg.log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

public class LoggingProperties {
	private final String rootLogger;
	private final String appender;
	private final String syslogFacility;
	//FIXME
	private final String pattern ="FIXME[%n%m]";
	private final String syslogHost;
	private final Map<String,String> classBlocking;

	public LoggingProperties(Configuration config) {
		syslogFacility=maybeDefaultFacility(config.getString("log4j.facility"));
		rootLogger=maybeDefaultLevel(config.getString("log4j.rootLogger"));
		appender=config.getString("log4j.appender");
		syslogHost=config.getString("log4j.loghost");
		classBlocking=new HashMap<>();
		Iterator<String> classBlockKeys = config.getKeys("log4j.logger");
		for (String k : iterable(classBlockKeys)) {
			classBlocking.put(k, config.getString(k));
		}
	}

	private static String maybeDefaultLevel(String l) {
		if ( l!=null && l.matches("(?i)[DEBUG|INFO]") ) {
			return l;
		}
		System.err.println("Illegal loglevel detected: "+l+" defaluting to INFO");
		return "INFO";
	}

	private static String maybeDefaultFacility(String f) {
		if ( f!=null && f.matches("(?i)local[0-7]")) {
			return f;
		}
		System.err.println("Illegal facility: "+f+" defaluting to local0");
		return "local0";
	}

	public Map<String, String> getClassBlocking() {
		return classBlocking;
	}

	public String getRootLogger() {
		return rootLogger;
	}

	public String getAppender() {
		return appender;
	}

	public String getSyslogFacility() {
		return syslogFacility;
	}

	public String getPattern() {
		return pattern;
	}

	public String getSyslogHost() {
		return syslogHost;
	}

	@Override
	public boolean equals(Object rhs) {
		if ( rhs != null && rhs instanceof LoggingProperties ) {
			LoggingProperties right = (LoggingProperties)rhs;

			if ( rootLogger != null && !rootLogger.equals(right.rootLogger)) {
				return false;
			} else if (rootLogger==null && right.rootLogger!=null) {
				return false;
			}

			if ( syslogFacility != null && !syslogFacility.equals(right.syslogFacility)) {
				return false;
			} else if (syslogFacility==null && right.syslogFacility!=null) {
				return false;
			}

			if ( syslogHost != null && !syslogHost.equals(right.syslogHost)) {
				return false;
			} else if (syslogHost==null && right.syslogHost!=null) {
				return false;
			}

			if ( appender != null && !appender.equals(right.appender)) {
				return false;
			} else if (appender==null && right.appender!=null) {
				return false;
			}

			if ( pattern != null && !pattern.equals(right.pattern)) {
				return false;
			} else if (pattern==null && right.pattern!=null) {
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("rootLogger: ").append(rootLogger);
		sb.append(" appender: ").append(appender);
		sb.append(" pattern: ").append(pattern);
		sb.append(" syslogFacility: ").append(syslogFacility);
		sb.append(" syslogHost: ").append(syslogHost);
		return sb.toString();
	}

	private static <T> Iterable<T> iterable(final Iterator<T> it){
		return new Iterable<T>(){ public Iterator<T> iterator(){ return it; } };
	}
}
