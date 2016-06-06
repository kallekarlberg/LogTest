package com.kkarlberg.log;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

public class Log4j2PropertiesConfigurator {

	private static final File DEFAULT_DRWP_LOGCONF_FILE=new File("logging.properties");
	private static final Logger logger = LogManager.getLogger(Log4j2PropertiesConfigurator.class);

	private static boolean startupInConsole=true;

	public static void reconfigure(Configuration appConfig, boolean startup) {
		//startup is special since we want need to set custom log config location
		LoggingProperties drwpLogProps = new LoggingProperties(appConfig);
		writeToDisk(translateToLog4j2Properties(drwpLogProps));
		if ( startup ) {
			reconfigureFileLocation(DEFAULT_DRWP_LOGCONF_FILE);
		} else {
			LoggingProperties currentProps = getCurrentConfig();
		}
	}

	private static LoggingProperties getCurrentConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	private static void reconfigureFileLocation(File newLocation) {
		LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
		// this will force a reconfiguration
		context.setConfigLocation(newLocation.toURI());
		context.getConfiguration();
	}

	private static void writeToDisk(Properties log4jJulConf) {
		try (FileWriter fw = new FileWriter(DEFAULT_DRWP_LOGCONF_FILE)){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			log4jJulConf.store(fw, sdf.format(new Date()));
		} catch (Exception e) {
			logger.error("unable to save log congfg",e);
		}
	}

	private static Properties translateToLog4j2Properties(LoggingProperties currentConfig) {
		Properties p = new Properties();
		p.setProperty("status", "error");
		p.setProperty("name", "PropertiesConfig");
		p.setProperty("monitorInterval","20");

		p.setProperty("appender.syslog.type", "Syslog");
		p.setProperty("appender.syslog.name","Syslog");
		p.setProperty("appender.syslog.facility",currentConfig.getSyslogFacility());
		p.setProperty("appender.syslog.host",currentConfig.getSyslogHost());
		p.setProperty("appender.syslog.port","514");
		p.setProperty("appender.syslog.protocol","UDP");

		p.setProperty("appender.console.type","Console");
		p.setProperty("appender.console.name","STDOUT");
		p.setProperty("appender.console.layout.type","PatternLayout");

		//FIXME
		p.setProperty("appender.console.layout.pattern","%m%n");

		p.setProperty("rootLogger.level",currentConfig.getRootLogger());

		//		rootLogger.appenderRef.stdout.ref = STDOUT
		//		rootLogger.appenderRef.rolling.ref = RollingFile

		for ( Entry<String, String> e : currentConfig.getClassBlocking().entrySet()) {
			p.put("logger."+e.getKey().replace(".", "")+".name", e.getKey());
			p.put("logger."+e.getKey().replace(".", "")+".level", e.getValue());
		}
		return p;
	}
}
