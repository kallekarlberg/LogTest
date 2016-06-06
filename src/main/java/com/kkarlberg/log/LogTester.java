package com.kkarlberg.log;

import java.io.Serializable;
import java.nio.charset.Charset;

import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.SyslogAppender;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.layout.LoggerFields;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.layout.Rfc5424Layout;
import org.apache.logging.log4j.core.net.Facility;

public class LogTester implements Runnable {

	private static final Logger logger = LogManager.getLogger(LogTester.class);

	private LoggingProperties currentLogProperties=null;

	private final String appName;

	private final Configuration config;

	public LogTester(Configuration config, String appName) {
		this.config=config;
		this.appName=appName;
	}

	@Override
	public void run() {
		while(true) {
			LoggingProperties newLogProps = new LoggingProperties(config);
			if ( shouldReconfigure(currentLogProperties, newLogProps) ) {
				configureLogging(newLogProps);
			}
			currentLogProperties = newLogProps;
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public boolean shouldReconfigure(LoggingProperties oldLogProps, LoggingProperties newLogProps) {
		//return newLogProps!=null && !newLogProps.equals(oldLogProps);
		return true;
	}

	public void configureLogging(LoggingProperties logProperties) {
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		AbstractConfiguration config = (AbstractConfiguration) ctx.getConfiguration();

		removeDrwpAppenders(config);

		config.getRootLogger().addAppender( createDrwpAppender(logProperties), getLogLevel(logProperties.getRootLogger()), null);

		config.getRootLogger().setLevel(getLogLevel(logProperties.getRootLogger()));

		ctx.updateLoggers();

	}

	private Appender createDrwpAppender(LoggingProperties logProperties) {
		if ( seemsToBeConsoleAppender(logProperties.getAppender() ) ) {
			return createDrwpConsoleAppender(logProperties);
		}
		return createDrwpSyslogAppender(logProperties, appName);
	}

	private Appender createDrwpSyslogAppender(LoggingProperties logProperties, String appName) {
		SyslogAppender a = SyslogAppender.createAppender(logProperties.getSyslogHost(), 514,
				"UDP", null, 100, 0, true, "Syslog", true, true,
				getFacility(logProperties.getSyslogFacility()), "id", Rfc5424Layout.DEFAULT_ENTERPRISE_NUMBER,
				true, "mdc","_mdc","", false, "", appName, "", "", "",
				"", "", null, null, Charset.forName("UTF-8"),
				"", getLoggerFields(), false);
		a.start();
		return a;
	}


	private LoggerFields[] getLoggerFields() {
		return null;
	}

	private static boolean seemsToBeConsoleAppender(String appender) {
		if ( appender == null ) {
			return false;
		}
		return appender.endsWith("ConsoleAppender");
	}

	private Appender createDrwpConsoleAppender(LoggingProperties logProperties) {
		Layout<? extends Serializable> layout = createPatternLayout(logProperties);
		Appender a= ConsoleAppender.createDefaultAppenderForLayout( layout );
		a.start();
		return a;
	}

	private PatternLayout createPatternLayout(LoggingProperties logProperties) {
		return PatternLayout.createLayout(logProperties.getPattern(), null, null,
				Charset.forName("UTF-8"), true, false, null, null);
	}

	private static void removeDrwpAppenders(AbstractConfiguration logConifg) {
		logConifg.removeAppender("Console");
		logConifg.removeAppender("Syslog");
	}

	private static Level getLogLevel(String rootLogger) {
		switch (rootLogger.toLowerCase()) {
		case "off":
			return Level.OFF;
		case "fatal":
			return Level.FATAL;
		case "error":
			return Level.ERROR;
		case "warn":
			return Level.WARN;
		case "info":
			return Level.INFO;
		case "debug":
			return Level.DEBUG;
		case "trace":
			return Level.TRACE;
		case "all":
			return Level.ALL;
		default:
			return Level.INFO;
		}
	}

	private Facility getFacility(String syslogFacility) {
		switch (syslogFacility.toLowerCase()) {
		case "local0":
			return Facility.LOCAL0;
		case "local1":
			return Facility.LOCAL1;
		case "local2":
			return Facility.LOCAL2;
		case "local3":
			return Facility.LOCAL3;
		case "local4":
			return Facility.LOCAL4;
		case "local5":
			return Facility.LOCAL5;
		case "local6":
			return Facility.LOCAL6;
		case "local7":
			return Facility.LOCAL7;
		}
		return Facility.LOCAL0;
	}

}
