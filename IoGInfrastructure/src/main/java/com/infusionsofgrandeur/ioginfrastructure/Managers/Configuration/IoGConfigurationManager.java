package com.infusionsofgrandeur.ioginfrastructure.Managers.Configuration;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.preference.PreferenceManager;

import java.net.URL;
import java.util.ArrayList;

import com.infusionsofgrandeur.ioginfrastructure.Managers.Persistence.IoGPersistenceManager;
import com.infusionsofgrandeur.ioginfrastructure.Managers.RemoteData.IoGDataManager.IoGDataManagerType;

public class IoGConfigurationManager
{

	// Persistence Manager
	public static final String persistenceFolderPath = "/Documents/IoGSettings";
	public static final String persistenceElementValue = "Value";
	public static final String persistenceElementType = "Type";
	public static final String persistenceElementSource = "Source";
	public static final String persistenceElementProtection = "Protection";
	public static final String persistenceElementLifespan = "Lifespan";
	public static final String persistenceElementExpiration = "Expiration";
	public static final String persistenceManagementExpiringItems = "ExpiringItems";
	public static final String persistenceManagementSessionItems = "SessionItems";
	public static final String persistenceExpirationItemName = "ExpiringItemName";
	public static final String persistenceExpirationItemSource = "ExpiringItemSource";
	public static final String persistenceExpirationItemExpirationDate = "ExpiringItemExpirationDate";
	public static final long timerPeriodPersistenceExpirationCheck = 60 * 1000;
	public static final String persistenceReadResultCode = "persistenceReadResultCode";
	public static final String persistenceReadResultValue = "persistenceReadResultValue";

	// Data Manager
	public static IoGDataManagerType defaultDataManagerType = IoGDataManagerType.IoGDataManagerTypeLive;
	public static final long defaultRequestTimeoutDelay = 10 * 1000;
	public static final int defaultRequestNumRetries = 0;
	public static final String requestResponseKeyRequest = "Request";
	public static final String requestResponseKeyRequestType = "RequestType";
	public static final String requestResponseKeyRequestBody = "RequestBody";
	public static final String requestResponseKeyCallback = "Callback";
	public static final String requestResponseKeyError = "Error";
	public static final String requestResponseKeyResponse = "Response";
	public static final String requestResponseTimeoutErrorDescription = "HTTP Timeout Error";
	public static final int requestResponseTimeoutErrorCode = 408;
	public static final String requestResponseGeneralErrorDescription = "HTTP Request Returned Error";
	public static final long mockFastDataRequestResponseTime = 100;
	public static final long mockSlowDataRequestResponseTime = 5 * 1000;
	public static final String mockResponseIndicator1 = "/1";
	public static final String mockSlowResponseIndicator = "/3";
	public static final String mockSuccessfulCallIndicator = "www.success.com";
	public static final String mockFailedCallIndicator = "www.failure.com";
	public static final String mockDataResponse1 = "{\"Generation\":\"1\", \"Computers\":[\"Color Computer 2\", \"Color Computer 3\", \"MM/1\"], \"Manufacturer\":null, \"Conventions\":\"Rainbowfest\"}";
	public static final String mockDataResponse2 = "{\"Generation\":\"2\", \"Computers\":[\"Mac Performa 6400\", \"Powerbook G4\", \"Power Mac G4\", \"iMac\", \"Macbook Pro\"], \"Manufacturer\":\"Apple\"}";
	public static final String webRequestRawPostBodyString = "rawPostBodyString";

	// Retry Manager
	public static final String retryItemFieldLifespan = "Lifespan";
	public static final String retryItemFieldRetryMaxCount = "Retries";
	public static final String retryItemFieldRetryCurrentCount = "RetryNumber";
	public static final String retryItemFieldExpiration = "Expiration";
	public static final String retryItemFieldTimeLimit = "TimeLimit";
	public static final String retryItemFieldRoutine = "Routine";
	public static final String retryItemFieldIdentifier = "Identifier";

	private static IoGConfigurationManager singletonInstance = null;

	private Context applicationContext;
	private SharedPreferences prefs;
	private boolean sessionActive = false;

// 02-23-22 - EGC - Deprecated single API URL in favor of an array of supported URLs
//	private String currentAPIURL;
	ArrayList<String> APIURLs = new ArrayList<>();

	public static IoGConfigurationManager getSharedManager()
	{
		if (singletonInstance == null)
			{
			singletonInstance = new IoGConfigurationManager();
			}
		return (singletonInstance);
	}

	IoGConfigurationManager()
	{
// 02-23-22 - EGC - Deprecated single API URL in favor of an array of supported URLs
//		currentAPIURL = "http://";
	}

	public void setApplicationContext(Context context)
	{
		applicationContext = context;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public Context getApplicationContext()
	{
		return (applicationContext);
	}

	public SharedPreferences getSharedPreferences()
	{
		return (prefs);
	}

	public String getVersion()
	{
		if (applicationContext == null)
			{
			return ("");
			}
		try
			{
			PackageInfo pInfo = applicationContext.getPackageManager().getPackageInfo(applicationContext.getPackageName(), 0);
			String version = pInfo.versionName;
			int versionCode = pInfo.versionCode;
			String code = Integer.toString(versionCode);
			return (version + "." + code);
			}
		catch (Exception ex)
			{
			return ("");
			}
	}

	public void setSessionActive(boolean state)
	{
		sessionActive = state;
		if (state == false)
			{
			IoGPersistenceManager.getSharedManager().removeSessionItems();
			}
	}

	public boolean getSessionActive()
	{
		return (sessionActive);
	}

// 02-23-22 - EGC - Deprecated single API URL in favor of an array of supported URLs
/*
	public void setAPIURL(String address)
	{
		currentAPIURL = address;
	}

	public String getAPIURLString()
	{
		return (currentAPIURL);
	}

	public URL getAPIURL()
	{
		try
			{
			URL url = new URL(currentAPIURL);
			return (url);
			}
		catch (Exception ex)
			{
			return (null);
			}
	}
*/

	public void addAPIURL(String address)
	{
		APIURLs.add(address);
	}

	public ArrayList<String> getAPIURLStrings()
	{
		return (APIURLs);
	}

	public ArrayList<URL> getAPIURLs()
	{
		try
			{
			ArrayList<URL> urlList = new ArrayList<>();
			for (String nextURLString : APIURLs)
				{
				URL url = new URL(nextURLString);
				urlList.add(url);
				}
			return (urlList);
			}
		catch (Exception ex)
			{
			return (null);
			}
	}
}
