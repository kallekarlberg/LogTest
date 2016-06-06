package com.kkarlberg.log;

import java.io.File;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LoggerContext;

import com.other.LogThread;

public class Main {

	private static final Logger logger = LogManager.getLogger(LogTester.class);

	public static void main2(String[] args) throws ConfigurationException {

		ThreadContext.put("appName", "MyCoolApp");
		ThreadContext.put("logTraceId", String.valueOf(System.currentTimeMillis()));

		logger.info("Setup pending...");

		Configuration appConfig = new PropertiesConfiguration("drwp.properties");
		Log4j2PropertiesConfigurator.reconfigure(appConfig , true);
	}

	public static void main(String[] args) throws InterruptedException, ConfigurationException {

		ThreadContext.put("appName", "MyCoolApp");
		ThreadContext.put("logTraceId", String.valueOf(System.currentTimeMillis()));

		logger.info("Setup pending...");
		//		Configuration cfg = new PropertiesConfiguration(
		//				Main.class.getResource("/logger.properties")
		//				);

		LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
		File file = new File(Main.class.getResource("/logger.properties").getFile());

		// this will force a reconfiguration
		//context.setConfigLocation(file.toURI());
		org.apache.logging.log4j.core.config.Configuration current = context.getConfiguration();
		//		Thread t = new Thread(new LogTester(cfg,"MyApp"));
		//		t.start();
		Thread t = new Thread(new LogThread());
		t.start();
		int i=0;
		while(true) {
			logger.info("INFO "+i);
			logger.debug("DEBUG "+i);
			logger.warn("WARN "+i);
			Thread.sleep(1000);
			i++;
		}
	}
}
