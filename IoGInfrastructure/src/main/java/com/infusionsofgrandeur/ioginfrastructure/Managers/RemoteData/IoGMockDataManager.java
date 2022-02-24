package com.infusionsofgrandeur.ioginfrastructure.Managers.RemoteData;

import java.net.HttpURLConnection;
import java.util.HashMap;

public class IoGMockDataManager extends IoGDataManager
{

	public int transmitRequest(HttpURLConnection urlConnection, HashMap<String, Object> bodyFields, IoGDataRequestType type)
	{
		IoGMockDataRequestResponse requestResponse = new IoGMockDataRequestResponse(requestID, type, urlConnection, bodyFields, callbackReceiver);
		requestResponse.processRequest();
		return requestID++;
	}

	public int transmitRequest(HttpURLConnection urlConnection, HashMap<String, Object> bodyFields, IoGDataRequestType type, String customTypeIdentifier)
	{
		IoGMockDataRequestResponse requestResponse = new IoGMockDataRequestResponse(requestID, type, urlConnection, bodyFields, callbackReceiver);
		requestResponse.setCustomRequestType(customTypeIdentifier);
		requestResponse.processRequest();
		return requestID++;
	}

}
