package com.infusionsofgrandeur.ioginfrastructure.Managers.RemoteData;

import android.os.AsyncTask;

import com.infusionsofgrandeur.ioginfrastructure.Managers.Configuration.IoGConfigurationManager;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;

public class IoGLiveDataRequestResponse extends IoGDataRequestResponse
{

	private Timer timeoutTimer;
	private boolean connectionConfigured = false;

	IoGLiveDataRequestResponse(int reqID, IoGDataManager.IoGDataRequestType type, HttpURLConnection connection, HashMap<String, Object> bodyFields, IoGDataRequestResponseCallbackReceiver callback)
	{
		super(reqID, type, connection, bodyFields, callback);
	}

	@Override
	public void processRequest()
	{

		HashMap<String, Object> bodyFields = (HashMap<String, Object>)requestInfo.get(IoGConfigurationManager.requestResponseKeyRequestBody);
		final HttpURLConnection connection = (HttpURLConnection)requestInfo.get(IoGConfigurationManager.requestResponseKeyRequest);
		HashMap<String, Object> requestData = new HashMap<>();
		if (bodyFields != null)
			{
			try
				{
				JSONObject bodyData = new JSONObject(bodyFields);
				String body = bodyData.toString();
				byte[] bytes = body.getBytes("UTF-8");
				if (!connectionConfigured)
					{
					String contentType = connection.getRequestProperty("Content-Type");
					if (contentType.equalsIgnoreCase("application/json"))
						{
						connection.setFixedLengthStreamingMode(bytes.length);
						}
					}
				requestData.put("requestID", new Integer(requestID));
				requestData.put("start", new Date());
				requestData.put("body", body);
				}
			catch (Exception ex)
				{
				ex.printStackTrace();
				}
			}
		timeoutTimer = new Timer();
		timeoutTimer.schedule(new TimerTask()
			{
			@Override public void run()
				{
				retryNumber++;
				if (IoGConfigurationManager.defaultRequestNumRetries > 0 && retryNumber <= IoGConfigurationManager.defaultRequestNumRetries)
					{
					connection.disconnect();
					processRequest();
					}
				else
					{
					IoGDataRequestResponseCallbackReceiver responseReceiver = (IoGDataRequestResponseCallbackReceiver) callbackInfo.get(IoGConfigurationManager.requestResponseKeyCallback);
					responseInfo.put(IoGConfigurationManager.requestResponseKeyError, IoGConfigurationManager.requestResponseTimeoutErrorDescription);
					connection.disconnect();
					responseReceiver.dataRequestResponse(IoGLiveDataRequestResponse.this);
					}
				}
			}, IoGConfigurationManager.defaultRequestTimeoutDelay, IoGConfigurationManager.defaultRequestTimeoutDelay);
		requestData.put("start", new Date());
		new dataRequestAsyncTask(connection, requestData).execute();
	}

	@Override
	public void continueMultiPartRequest()
	{
		final HttpURLConnection connection = (HttpURLConnection)requestInfo.get(IoGConfigurationManager.requestResponseKeyRequest);
		HashMap<String, Object> requestData = new HashMap<>();

		requestData.put("requestID", new Integer(requestID));
		requestData.put("start", new Date());
		timeoutTimer.cancel();
		timeoutTimer.schedule(new TimerTask()
			{
			@Override public void run()
				{
				retryNumber++;
				if (IoGConfigurationManager.defaultRequestNumRetries > 0 && retryNumber <= IoGConfigurationManager.defaultRequestNumRetries)
					{
					connection.disconnect();
					continueMultiPartRequest();
					}
				else
					{
					IoGDataRequestResponseCallbackReceiver responseReceiver = (IoGDataRequestResponseCallbackReceiver) callbackInfo.get(IoGConfigurationManager.requestResponseKeyCallback);
					responseInfo.put(IoGConfigurationManager.requestResponseKeyError, IoGConfigurationManager.requestResponseTimeoutErrorDescription);
					responseReceiver.dataRequestResponse(IoGLiveDataRequestResponse.this);
					}
				}
			}, IoGConfigurationManager.defaultRequestTimeoutDelay, IoGConfigurationManager.defaultRequestTimeoutDelay);
		new dataRequestAsyncTask(connection, requestData).execute();
	}

	private class dataRequestAsyncTask extends AsyncTask<Void, Void, Void>
	{

		HttpURLConnection connection;
		HashMap<String, Object> request;
		Integer reqID;
		int responseCode;
		HashMap<String, Object> resp;
		String errorMsg = "";
		String contentType = null;
		String requestMethod = null;

		public dataRequestAsyncTask(HttpURLConnection conn, HashMap<String, Object> req)
		{
			connection = conn;
			request = req;
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			BufferedReader reader = null;
			HashMap<String, Object> tempConnectionInfo = new HashMap<String, Object>();
			if (!connectionConfigured)
				{
				contentType = connection.getRequestProperty("Content-Type");
				tempConnectionInfo.put("headers", connection.getRequestProperties());
				requestMethod = connection.getRequestMethod();
				}
			BufferedOutputStream outputStream = null;
			byte[] postData = null;
			String body = null;
			reqID = (Integer)request.get("requestID");

			resp = new HashMap<>();
			try
				{
				String bodyString = "";
				if (request.containsKey("body"))
					{
					body = (String) request.get("body");
					JSONObject jsonDict = new JSONObject(body);
					if (contentType.equalsIgnoreCase("application/x-www-form-urlencoded"))
						{
						if (jsonDict.opt(IoGConfigurationManager.webRequestRawPostBodyString) != null)
							{
							bodyString = (String) jsonDict.get(IoGConfigurationManager.webRequestRawPostBodyString);
							postData = bodyString.getBytes("UTF-8");
							int postDataLength = postData.length;
							if (!connectionConfigured)
								{
								connection.setRequestProperty("charset", "utf-8");
								connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
								}
							}
						}
					else
						{
						bodyString = body;
						postData = bodyString.getBytes("UTF-8");
						int postDataLength = postData.length;
						if (!connectionConfigured)
							{
							connection.setRequestProperty("charset", "utf-8");
							connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
							}
						}
					}
				double responseTime = 0;
				if ((requestMethod.equalsIgnoreCase("post")  || requestMethod.equalsIgnoreCase("patch")) && body != null)
					{
					connection.setDoOutput(true);
					connection.connect();
					outputStream = new BufferedOutputStream(connection.getOutputStream());
					outputStream.write(postData);
					outputStream.close();
					}
				else
					{
					connection.connect();
					}
				String response;
				HashMap<String, Object> connectionInfo = new HashMap<String, Object>();
				responseCode = connection.getResponseCode();
				timeoutTimer.cancel();
				timeoutTimer = null;
				Date endTime = new Date();
				Date startTime = (Date)request.get("start");
				responseTime = (endTime.getTime() - startTime.getTime());
				connectionInfo.put("url", connection.getURL());
				if (!connectionConfigured)
					{
					connectionInfo.put("headers", connection.getHeaderFields());
					}
				connectionConfigured = true;
				if (responseCode < 200 || responseCode > 299)
					{
					reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
					response = reader.readLine();
					resp.put ("result", "FAILURE");
					if (body != null)
						{
						errorMsg = "Service request failed with status: " + responseCode + " for endpoint: " + connection.getURL().toString() + " with body: " + body;
						}
					else
						{
						errorMsg = "Service request failed with status: " + responseCode + " for endpoint: " + connection.getURL().toString();
						}
					}
				else
					{
					reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					response = reader.readLine();
					String status = "";
					try
						{
						JSONObject responseFields = new JSONObject(response);
						if (responseFields.length() > 0)
							{
							status = "SUCCESS";
							}
						}
					catch (Exception ex)
						{
						ex.printStackTrace();
						}
					if (status.equalsIgnoreCase("success"))
						{
						resp.put("result", "SUCCESS");
						}
					else
						{
						resp.put("result", "FAILURE");
						}
					}
				resp.put ("response", response);
				resp.put ("connection", connectionInfo);
				resp.put ("duration", responseTime);
				IoGLiveDataRequestResponse.this.responseInfo = resp;
				IoGLiveDataRequestResponse.this.responseData = response.getBytes();
				IoGLiveDataRequestResponse.this.statusCode = responseCode;
				connection.disconnect();
				}
			catch (Exception ex)
				{
				resp.put("result", "FAILURE");
				responseCode = 400;
				connection.disconnect();
				}
			finally
				{
				if (reader != null)
					{
					try
						{
						reader.close();
						}
					catch (Exception ex)
						{
						if (outputStream != null)
							{
							try
								{
								outputStream.close();
								}
							catch (Exception oex)
								{
								oex.printStackTrace();
								}
							}
						}
					}
				if (outputStream != null)
					{
					try
						{
						outputStream.close();
						}
					catch (Exception ex)
						{
						ex.printStackTrace();
						}
					}

				}
			return null;
		}

		protected void onPostExecute(Void result)
		{
			try
				{
				IoGDataRequestResponseCallbackReceiver responseReceiver = (IoGDataRequestResponseCallbackReceiver)callbackInfo.get(IoGConfigurationManager.requestResponseKeyCallback);
				responseReceiver.dataRequestResponse(IoGLiveDataRequestResponse.this);
				}
			catch (Exception ex)
				{
				ex.printStackTrace();
				}
		}
	}
}
