package com.amalw.utils;


import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class LogManagerUtil {
	
	public static void configureFrameworkLogLevel(String level) {

		Logger logger = (Logger) LoggerFactory.getLogger("com.amalw");

        logger.setLevel(Level.toLevel(level));
    }

}
