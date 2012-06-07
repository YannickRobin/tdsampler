package com.ebizance.tdsampler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 
 * @author Yannick Robin
 *
 */
public class TDSamplerConfig 
{
	private static final Logger logger = Logger.getLogger(TDSamplerConfig.class);
	
    private static final String CONFIGURATION_FILE_DEFAULT = "../conf/conf.properties"; 
	
    public static boolean displayThreadStateReport_;
    public static boolean countDuplicateMethods_;
    public static String includeListThreadName_;    
    public static String includeListThreadState_;    
    public static String includeListMethod_;
    public static String excludeListMethod_;
    public static String includeListThread_;
    public static String excludeListThread_;
    public static String includeListIOWait_;
      
    static {
    	Properties properties = new Properties();
    	try {
    		
    		String configurationFile;
    		if (System.getenv("tdsampler.configuration") != null)
    			configurationFile = System.getenv("tdsampler.configuration");
    		else
    			configurationFile = CONFIGURATION_FILE_DEFAULT;
    		   			
			properties.load(new FileInputStream(configurationFile));
			displayThreadStateReport_ = Boolean.parseBoolean(properties.getProperty("displayThreadStateReport", "false"));
			countDuplicateMethods_ = Boolean.parseBoolean(properties.getProperty("countDuplicateMethods", "false"));
			includeListThreadName_ = properties.getProperty("includeListThreadName", "");
			includeListThreadState_ = properties.getProperty("includeListThreadState", "");
			includeListMethod_ = properties.getProperty("includeListMethod", "");
			excludeListMethod_ = properties.getProperty("excludeListMethod", "");
			includeListThread_ = properties.getProperty("includeListThread", "");
			excludeListThread_ = properties.getProperty("excludeListThread", "");
			includeListIOWait_ = properties.getProperty("includeListIOWait", "");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
}
