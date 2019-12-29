package com.infusionsofgrandeur.ioginfrastructure.Managers.Retry;

import android.os.Handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.infusionsofgrandeur.ioginfrastructure.Managers.Configuration.IoGConfigurationManager;

public class IoGRetryManager
{

	public interface IoGRetryManagerCallbackReceiver
	{
		void retrySessionCompleted(int requestID, RetryResult result);
	}

	public interface DispositionAttempt
	{
		void dispositionAttempt(int requestNumber, Disposition result);
	}

	public interface RetryRoutine
	{
		void retry(DispositionAttempt retry);
	}

	public enum RetryLifespan
	{
		Infinite,
		CountLimited,
		TimeLimited,
		ExpirationLimited
	}

	public enum RetryResult
	{
		Success,
		CountExceeded,
		TimeLimitExceeded,
		Expired
	}

	public enum Disposition
	{
		Success,
		Failure
	}

	static String retryItemFieldLifespan = "Lifespan";
	static String retryItemFieldRetryMaxCount = "Retries";
	static String retryItemFieldRetryCurrentCount = "RetryNumber";
	static String retryItemFieldExpiration = "Expiration";
	static String retryItemFieldTimeLimit = "TimeLimit";
	static String retryItemFieldRoutine = "Routine";
	static String retryItemFieldIdentifier = "Identifier";

	private static IoGRetryManager singletonInstance = null;

	private ArrayList<IoGRetryManagerCallbackReceiver> callbackReceivers = new ArrayList<IoGRetryManagerCallbackReceiver>();
	private HashMap<Integer, HashMap<String, Object>> retryStore = new HashMap<Integer, HashMap<String, Object>>();

	private int requestID = 0;

	public static IoGRetryManager getSharedManager()
	{
		if (singletonInstance == null)
			{
			singletonInstance = new IoGRetryManager();
			}
		return (singletonInstance);
	}

	IoGRetryManager()
	{
	}

	public void registerCallbackListener(IoGRetryManagerCallbackReceiver callbackReceiver)
	{
		if (!callbackReceivers.contains(callbackReceiver))
			{
			callbackReceivers.add(callbackReceiver);
			}
	}

	public void unregisterCallbackListener(IoGRetryManagerCallbackReceiver callbackReceiver)
	{
		if (callbackReceivers.contains(callbackReceiver))
			{
			callbackReceivers.remove(callbackReceiver);
			}
	}

	public int startRetries(int interval, RetryLifespan lifespan, int maxCount, int timeSpan, Date expiration, RetryRoutine routine)
	{
		if ((lifespan == RetryLifespan.ExpirationLimited && expiration == null) || (lifespan == RetryLifespan.TimeLimited && timeSpan <= 0) || (lifespan == RetryLifespan.CountLimited && maxCount <= 0))
			{
			return -1;
			}
		HashMap<String, Object> newRetryEntry = new HashMap<String, Object>();
		int request = requestID;
		requestID++;
		newRetryEntry.put(IoGConfigurationManager.retryItemFieldLifespan, lifespan);
		if (maxCount > 0)
			{
			newRetryEntry.put(IoGConfigurationManager.retryItemFieldRetryMaxCount, maxCount);
			}
		// If both expiration and timespan values are set, expiration will override timespan
		if (expiration != null)
			{
			newRetryEntry.put(IoGConfigurationManager.retryItemFieldExpiration, expiration);
			}
		else if (timeSpan > 0)
			{
			Date currentTime = new Date();
			Date exp = new Date(currentTime.getTime() + timeSpan);
			newRetryEntry.put(IoGConfigurationManager.retryItemFieldTimeLimit, exp);
			}
		newRetryEntry.put(IoGConfigurationManager.retryItemFieldRoutine, routine);
		if (maxCount > 0)
			{
			newRetryEntry.put(IoGConfigurationManager.retryItemFieldRetryCurrentCount, 0);
			if (maxCount > 1)
				{
				Timer retryTimer = new Timer();
				retryTimer.schedule(new TimerTask() {
					@Override
					public void run()
						{
						makeRetryAttempt(retryTimer, request);
						}
					}, interval, interval);
				}
			else
				{
				Timer retryTimer = new Timer();
				retryTimer.schedule(new TimerTask() {
					@Override
					public void run()
						{
						makeRetryAttempt(retryTimer, request);
						}
					}, interval);
				}
			}
		else
			{
			Timer retryTimer = new Timer();
			retryTimer.schedule(new TimerTask() {
				@Override
				public void run()
					{
					makeRetryAttempt(retryTimer, request);
					}
				}, interval, interval);
			}
		retryStore.put(request, newRetryEntry);
		return (request);
	}

	public int startRetries(int interval, RetryRoutine routine)
	{
		return (startRetries(interval, RetryLifespan.Infinite, 0, 0, null, routine));
	}

	public int startCountRetries(int interval, int maxCount, RetryRoutine routine)
	{
		return (startRetries(interval, RetryLifespan.CountLimited, maxCount, 0, null, routine));
	}

	public int startTimespanRetries(int interval, int timeSpan, RetryRoutine routine)
	{
		return (startRetries(interval, RetryLifespan.TimeLimited, 0, timeSpan, null, routine));
	}

	public int startRetries(int interval, Date expiration, RetryRoutine routine)
	{
		return (startRetries(interval, RetryLifespan.TimeLimited, 0, 0, expiration, routine));
	}

	public void cancelRetries(int identifier)
	{
		retryStore.remove(identifier);
	}

	private void makeRetryAttempt(Timer timer, int requestNum)
	{
		if (retryStore.containsKey(requestNum))
			{
			HashMap<String, Object> retryEntry = retryStore.get(requestNum);
			if (retryEntry.containsKey(IoGConfigurationManager.retryItemFieldExpiration))				// Expiration limited
				{
				Date exp = (Date) retryEntry.get(IoGConfigurationManager.retryItemFieldExpiration);
				if (exp.after(new Date()))
					{
					RetryRoutine routine = (RetryRoutine) retryEntry.get(IoGConfigurationManager.retryItemFieldRoutine);
					routine.retry(disposition);
					}
				else
					{
					if (callbackReceivers.size() > 0)
						{
						ArrayList<IoGRetryManagerCallbackReceiver> tempReceiverList = new ArrayList<IoGRetryManagerCallbackReceiver>(callbackReceivers);
						for (final IoGRetryManagerCallbackReceiver nextReceiver : tempReceiverList)
							{
							try
								{
								Handler mainHandler = new Handler(IoGConfigurationManager.getSharedManager().getApplicationContext().getMainLooper());
								mainHandler.post(new Runnable() {
									@Override
									public void run ()
										{
										nextReceiver.retrySessionCompleted(requestNum, RetryResult.Expired);
										}
									});
								}
							catch (Exception ex)
								{
								callbackReceivers.remove(nextReceiver);
								}
							}
						}
					timer.cancel();
					retryStore.remove(requestNum);
					}
				}
			else if (retryEntry.containsKey(IoGConfigurationManager.retryItemFieldTimeLimit))			// Time limited
				{
				Date exp = (Date) retryEntry.get(IoGConfigurationManager.retryItemFieldTimeLimit);
				if (exp.after(new Date()))
					{
					RetryRoutine routine = (RetryRoutine) retryEntry.get(IoGConfigurationManager.retryItemFieldRoutine);
					routine.retry(disposition);
					}
				else
					{
					if (callbackReceivers.size() > 0)
						{
						ArrayList<IoGRetryManagerCallbackReceiver> tempReceiverList = new ArrayList<IoGRetryManagerCallbackReceiver>(callbackReceivers);
						for (final IoGRetryManagerCallbackReceiver nextReceiver : tempReceiverList)
							{
							try
								{
								Handler mainHandler = new Handler(IoGConfigurationManager.getSharedManager().getApplicationContext().getMainLooper());
								mainHandler.post(new Runnable() {
									@Override
									public void run ()
										{
										nextReceiver.retrySessionCompleted(requestNum, RetryResult.TimeLimitExceeded);
										}
									});
								}
							catch (Exception ex)
								{
								callbackReceivers.remove(nextReceiver);
								}
							}
						}
					timer.cancel();
					retryStore.remove(requestNum);
					}
				}
			else if (retryEntry.containsKey(IoGConfigurationManager.retryItemFieldRetryMaxCount))			// Count limited
				{
				int count = (int) retryEntry.get(IoGConfigurationManager.retryItemFieldRetryMaxCount);
				int lastRetry = (int) retryEntry.get(IoGConfigurationManager.retryItemFieldRetryCurrentCount);
				if (lastRetry < count)
					{
					RetryRoutine routine = (RetryRoutine) retryEntry.get(IoGConfigurationManager.retryItemFieldRoutine);
					retryEntry.put(IoGConfigurationManager.retryItemFieldRetryCurrentCount, lastRetry + 1);
					routine.retry(disposition);
					}
				else
					{
					if (callbackReceivers.size() > 0)
						{
						ArrayList<IoGRetryManagerCallbackReceiver> tempReceiverList = new ArrayList<IoGRetryManagerCallbackReceiver>(callbackReceivers);
						for (final IoGRetryManagerCallbackReceiver nextReceiver : tempReceiverList)
							{
							try
								{
								Handler mainHandler = new Handler(IoGConfigurationManager.getSharedManager().getApplicationContext().getMainLooper());
								mainHandler.post(new Runnable() {
									@Override
									public void run ()
										{
										nextReceiver.retrySessionCompleted(requestNum, RetryResult.CountExceeded);
										}
									});
								}
							catch (Exception ex)
								{
								callbackReceivers.remove(nextReceiver);
								}
							}
						}
					timer.cancel();
					retryStore.remove(requestNum);
					}
				}
			else																						// Infinite
				{
				RetryRoutine routine = (RetryRoutine) retryEntry.get(IoGConfigurationManager.retryItemFieldRoutine);
				routine.retry(disposition);
				}
			}
		else
			{
			timer.cancel();
			}
	}

	DispositionAttempt disposition = (int requestNumber, Disposition result) -> {
		Integer requestNum = new Integer(requestNumber);
		if (retryStore.containsKey(requestNum))
			{
			if (result == Disposition.Success)
				{
				if (callbackReceivers.size() > 0)
					{
					ArrayList<IoGRetryManagerCallbackReceiver> tempReceiverList = new ArrayList<IoGRetryManagerCallbackReceiver>(callbackReceivers);
					for (final IoGRetryManagerCallbackReceiver nextReceiver : tempReceiverList)
						{
						try
							{
							Handler mainHandler = new Handler(IoGConfigurationManager.getSharedManager().getApplicationContext().getMainLooper());
							mainHandler.post(new Runnable() {
								@Override
								public void run ()
									{
									nextReceiver.retrySessionCompleted(requestNumber, RetryResult.Success);
									}
								});
							}
						catch (Exception ex)
							{
							callbackReceivers.remove(nextReceiver);
							}
						}
					}
				retryStore.remove(requestNum);
				}
			}
		};
}
