package com.other;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogThread implements Runnable {
	private static final Logger logger = LogManager.getLogger(LogThread.class);

	@Override
	public void run() {
		while(true) {
			logger.info("INFO OTHER");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
