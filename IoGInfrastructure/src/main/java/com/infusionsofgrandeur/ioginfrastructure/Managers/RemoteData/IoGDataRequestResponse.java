package com.infusionsofgrandeur.ioginfrastructure.Managers.RemoteData;

import java.util.HashMap;
import java.net.HttpURLConnection;

import com.infusionsofgrandeur.ioginfrastructure.Managers.Configuration.IoGConfigurationManager;
import com.infusionsofgrandeur.ioginfrastructure.Managers.RemoteData.IoGDataManager.IoGDataRequestType;

public abstract class IoGDataRequestResponse
{

	public interface IoGDataRequestResponseCallbackReceiver
	{
		void dataRequestResponse(IoGDataRequestResponse response);
	}

	int requestID;
	HashMap<String, Object> callbackInfo = new HashMap<String, Object>();
	HashMap<String, Object> requestInfo = new HashMap<String, Object>();
	int retryNumber;
	int statusCode;
	HashMap<String, Object> responseInfo = new HashMap<String, Object>();
	byte[] responseData;

	IoGDataRequestResponse(int reqID, IoGDataRequestType type, HttpURLConnection connection, HashMap<String, Object> bodyFields, IoGDataRequestResponseCallbackReceiver callback)
	{
		requestID = reqID;
		retryNumber = 0;
		callbackInfo.put(IoGConfigurationManager.requestResponseKeyCallback, callback);
		requestInfo.put(IoGConfigurationManager.requestResponseKeyRequest, connection);
		requestInfo.put(IoGConfigurationManager.requestResponseKeyRequestType, type);
		requestInfo.put(IoGConfigurationManager.requestResponseKeyRequestBody, bodyFields);
	}

	abstract public void processRequest();
	abstract public void continueMultiPartRequest();

	public boolean didRequestSucceed()
	{
		if (statusCode == 0)
			{
			return (false);
			}
		if (statusCode >= 200 && statusCode < 300)
			{
			return (true);
			}
		else
			{
			return (false);
			}
	}

	public int getRequestID()
	{
		return (requestID);
	}

	public HashMap<String, Object> getRequestInfo()
	{
		return (requestInfo);
	}

	public HashMap<String, Object> getResponseInfo()
	{
		return (responseInfo);
	}

	public int getStatusCode()
	{
		return (statusCode);
	}
}
