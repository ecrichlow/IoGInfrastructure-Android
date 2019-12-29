package com.infusionsofgrandeur.ioginfrastructure.Managers.RemoteData;

import com.infusionsofgrandeur.ioginfrastructure.Managers.Configuration.IoGConfigurationManager;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class IoGMockDataRequestResponse extends IoGDataRequestResponse
{

	IoGMockDataRequestResponse(int reqID, IoGDataManager.IoGDataRequestType type, HttpURLConnection connection, HashMap<String, Object> bodyFields, IoGDataRequestResponseCallbackReceiver callback)
	{
		super(reqID, type, connection, bodyFields, callback);
	}

	public void processRequest()
	{
		sendResponse();
	}

	public void continueMultiPartRequest()
	{
		responseData = null;
		sendResponse();
	}

	private void sendResponse()
	{
		HttpURLConnection connection = (HttpURLConnection)requestInfo.get(IoGConfigurationManager.requestResponseKeyRequest);
		final String requestString = connection.getURL().toString();
		long responseDelay = requestString.endsWith(IoGConfigurationManager.mockSlowResponseIndicator) == true ? IoGConfigurationManager.mockSlowDataRequestResponseTime : IoGConfigurationManager.mockFastDataRequestResponseTime;
		new Timer().schedule(new TimerTask()
			{
			@Override
			public void run()
				{
				if (requestString.contains(IoGConfigurationManager.mockFailedCallIndicator))
					{
					responseInfo.put(IoGConfigurationManager.requestResponseKeyError, IoGConfigurationManager.requestResponseGeneralErrorDescription);
					}
				else if (requestString.contains(IoGConfigurationManager.mockSuccessfulCallIndicator))
					{
					String respString = (requestString.endsWith(IoGConfigurationManager.mockResponseIndicator1) == true || requestString.endsWith(IoGConfigurationManager.mockSlowResponseIndicator) == true) ? IoGConfigurationManager.mockDataResponse1 : IoGConfigurationManager.mockDataResponse2;
					byte[] resp = respString.getBytes();
					responseInfo.put(IoGConfigurationManager.requestResponseKeyResponse, resp);
					responseData = resp;
					}
				try
					{
					IoGDataRequestResponseCallbackReceiver responseReceiver = (IoGDataRequestResponseCallbackReceiver)callbackInfo.get(IoGConfigurationManager.requestResponseKeyCallback);
					responseReceiver.dataRequestResponse(com.infusionsofgrandeur.ioginfrastructure.Managers.RemoteData.IoGMockDataRequestResponse.this);
					}
				catch (Exception ex)
					{
					ex.printStackTrace();
					}
				}
			}, responseDelay);
	}
}
