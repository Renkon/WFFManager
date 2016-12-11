package ar.com.renkon.utils;

import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerFactory extends LogManager
{
	public static ArrayList<Logger> loggers = new ArrayList<Logger>();
	
	static LoggerFactory instance;
	static FileHandler file;
    public LoggerFactory() { instance = this; }
    @Override public void reset() { /* don't reset yet. */ }
    private void reset0() { super.reset(); }
    public static void resetFinally() { instance.reset0(); }
    
	public static <T> Logger getClassLogger(String className)
	{
		try
		{
			Logger logger = Logger.getLogger(className);
			if (file == null)
			{
				file = new FileHandler("wffmgr.log", true);
				file.setFormatter(new SimpleFormatter());
				file.setLevel(Level.FINER);
			}
			logger.setLevel(Level.FINER);
			logger.addHandler(file);
			loggers.add(logger);
			return logger;
		}
		catch (Exception e)
		{
			return null;
		}
	}
		
	public static void killLoggers()
	{
		loggers.removeAll(loggers);
		instance.reset0();
	}
}