package com.infusionsofgrandeur.ioginfrastructure.Managers.RemoteData;

import java.net.HttpURLConnection;
import java.util.HashMap;

public class IoGLiveDataManager extends IoGDataManager
{

	public int transmitRequest(HttpURLConnection urlConnection, HashMap<String, Object> bodyFields, IoGDataRequestType type)
	{
		IoGLiveDataRequestResponse requestResponse = new IoGLiveDataRequestResponse(requestID, type, urlConnection, bodyFields, callbackReceiver);
		outstandingRequests.put(requestID, requestResponse);
		requestResponse.processRequest();
		return requestID++;
	}

}
