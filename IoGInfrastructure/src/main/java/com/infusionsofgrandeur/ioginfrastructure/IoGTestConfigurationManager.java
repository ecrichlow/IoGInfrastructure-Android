package com.infusionsofgrandeur.ioginfrastructure;

import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

public class IoGTestConfigurationManager
{

	// Persistence Manager Test Data
	static public String persistenceFolderPath = "/Documents/IoGPersistence";
	static public String persistenceTestSaveName = "TestIdentifier";
	static public String persistenceTestInvalidSaveName = "/Documents/IoGPersistence";
	static public int persistenceTestNumericValue = 42;
	static public String persistenceTestStringValue = "Forty Two";
	static public String persistenceTestSecondaryStringValue = "Nineteen Eighty Nine";
	static public List<String> persistenceTestArrayValue = Arrays.asList("Infusions", "Of", "Grandeur");
	static public Map<String, Object> persistenceTestHashMapValue = new HashMap<String, Object>() {{put("NumericValue", new Integer(42)); put("StringValue", "IoG");}};
	static public int persistenceTestExpiration = 2 * 60 * 1000;
	static public int persistenceTestExpirationCheck = 3 * 60 * 1000;
	static public int persistenceTestExpirationCheckTimeout = 4 * 60 * 1000;

	// Retry Manager Test Data
	static public int retryTestExpiration = 8 * 1000;
	static public int preExpiration = retryTestExpiration - (2 * 1000);
	static public int retryTestExpirationCheckTimeout = 30 * 1000;
	static public int infiniteRetryTestExpirationCheckTimeout = 120 * 1000;
	static public int retryDelay = 1 * 1000;
	static public int decoyRetryDelay = 500;
	static public int retryCallbackDelay = 1 * 1000;
	static public int decoyMaxCount = 3;
	static public int decoyRetrySuccessIteration = 4;
	static public int maxCount = 12;
	static public int retrySuccessIteration = 6;
	static public int infiniteRetrySuccessIteration = 99;

	// Data Manager Test Data
	static public String successURL1 = "http://www.success.com/1";
	static public String successURL2 = "http://www.success.com/2";
	static public String successURL1Slow = "http://www.success.com/3";
	static public String failureURL1 = "http://www.failure.com/1";
	static public String failureURLSlow = "http://www.failure.com/2";
	static public int dataRequestFastResponseCheck = 500;
	static public int dataRequestSlowResponseCheck = 7 * 1000;
	static public int dataTestExpirationCheckTimeout = 10 * 1000;
	static public String dataRequestCustomType = "CustomTypeTestX";

	// Data Parsing
	static public String parsingObjectData = "{\"model\":\"TRS-80 Color Computer 2\", \"processor\":\"6809\", \"year\":\"1980\"}";
	static public String parsingObjectArrayData = "[{\"model\":\"TRS-80 Color Computer 2\", \"processor\":\"6809\", \"year\":\"1980\"}, {\"model\":\"TRS-80 Color Computer 3\", \"processor\":\"68B09E\", \"year\":\"1986\"}, {\"model\":\"MM/1\", \"processor\":\"68070\", \"year\":\"1990\"}]";
}
