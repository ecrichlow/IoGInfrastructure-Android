package com.infusionsofgrandeur.ioginfrastructure.Managers.RemoteData;

import android.os.Handler;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import com.infusionsofgrandeur.ioginfrastructure.Managers.Configuration.IoGConfigurationManager;

public abstract class IoGDataManager
{

	public interface IoGDataManagerCallbackReceiver
	{
		void dataRequestResponseReceived(int requestID, IoGDataRequestType requestType, byte[] responseData, String error, IoGDataRequestResponse response);
	}

	public enum IoGDataManagerType
	{
		IoGDataManagerTypeLive,
		IoGDataManagerTypeMock
	}

	public enum IoGDataRequestType
	{
		Register((byte)0x00),
		Login((byte)0x01),
		Logout((byte)0x02),
		ResetPassword((byte)0x03),
		UserInfo((byte)0x04),
		UpdateUserInfo((byte)0x05),
		Features((byte)0x06),
		Version((byte)0x07);

		private final byte id;
		IoGDataRequestType(byte id) {this.id = id;}
		public byte getValue(){return (id);}
	}

	private static IoGDataManager singletonInstance = null;

	private ArrayList<IoGDataManagerCallbackReceiver> callbackReceivers = new ArrayList<IoGDataManagerCallbackReceiver>();
	HashMap<Integer, IoGLiveDataRequestResponse> outstandingRequests = new HashMap<Integer, IoGLiveDataRequestResponse>();
	int requestID = 0;

	public static IoGDataManager dataManagerOfType(IoGDataManagerType type)
	{
		switch (type)
			{
			case IoGDataManagerTypeLive:
				if (singletonInstance == null || singletonInstance.getClass().getName().equalsIgnoreCase("IoGMockDataManager"))
					{
					singletonInstance = new IoGLiveDataManager();
					}
				break;
			case IoGDataManagerTypeMock:
				if (singletonInstance == null || singletonInstance.getClass().getName().equalsIgnoreCase("IoGLiveDataManager"))
					{
					singletonInstance = new IoGMockDataManager();
					}
				break;
			}
		return (singletonInstance);
	}

	public static IoGDataManager dataManagerOfDefaultType()
	{
		return (IoGDataManager.dataManagerOfType(IoGConfigurationManager.defaultDataManagerType));
	}

	IoGDataManager()
	{
	}

	public void registerCallbackListener(IoGDataManagerCallbackReceiver callbackReceiver)
	{
		if (!callbackReceivers.contains(callbackReceiver))
			{
			callbackReceivers.add(callbackReceiver);
			}
	}

	public void unregisterCallbackListener(IoGDataManagerCallbackReceiver callbackReceiver)
	{
		if (callbackReceivers.contains(callbackReceiver))
			{
			callbackReceivers.remove(callbackReceiver);
			}
	}

	public void continueMultiPartRequest(IoGDataRequestResponse multiPartResponse)
	{
		multiPartResponse.continueMultiPartRequest();
	}

	abstract public int transmitRequest(HttpURLConnection urlConnection, HashMap<String, Object> bodyFields, IoGDataRequestType type);

	IoGDataRequestResponse.IoGDataRequestResponseCallbackReceiver callbackReceiver = new IoGDataRequestResponse.IoGDataRequestResponseCallbackReceiver()
	{
		public void dataRequestResponse(final IoGDataRequestResponse response)
		{
			if (callbackReceivers.size() > 0)
				{
				ArrayList<IoGDataManagerCallbackReceiver> tempReceiverList = new ArrayList<IoGDataManagerCallbackReceiver>(callbackReceivers);
				for (final IoGDataManagerCallbackReceiver nextReceiver : tempReceiverList)
					{
					try
						{
						Handler mainHandler = new Handler(IoGConfigurationManager.getSharedManager().getApplicationContext().getMainLooper());
						mainHandler.post(new Runnable() {
							@Override
							public void run ()
								{
								nextReceiver.dataRequestResponseReceived(response.getRequestID(), (IoGDataRequestType)response.getRequestInfo().get(IoGConfigurationManager.requestResponseKeyRequestType), response.responseData, (String)response.getResponseInfo().get(IoGConfigurationManager.requestResponseKeyError), response);
								}
							});
						}
					catch (Exception ex)
						{
						callbackReceivers.remove(nextReceiver);
						}
					}
				}
		}
	};
}
