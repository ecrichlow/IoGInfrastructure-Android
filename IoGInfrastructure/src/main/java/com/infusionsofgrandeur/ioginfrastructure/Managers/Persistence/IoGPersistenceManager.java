package com.infusionsofgrandeur.ioginfrastructure.Managers.Persistence;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import com.infusionsofgrandeur.ioginfrastructure.Managers.Configuration.IoGConfigurationManager;

public class IoGPersistenceManager
{

	public enum PersistenceSource
	{
		Memory,
		SharedPreferences,
		FileStorage
	}

	public enum PersistenceProtectionLevel
	{
		Unsecured,
		Secured
	}

	public enum PersistenceLifespan
	{
		Immortal,
		Session,
		Expiration
	}

	public enum PersistenceDataType
	{
		Boolean,
		Number,
		String,
		Array,
		HashMap,
		Data
	}

	public enum PersistenceReadResultCode
	{
		Success,
		NotFound,
		NoContext,
		Expired
	}

	private static IoGPersistenceManager singletonInstance = null;

	private HashMap<String, HashMap<String, Object>> memoryStore = new HashMap<>();

	public static IoGPersistenceManager getSharedManager()
	{
		if (singletonInstance == null)
			{
			singletonInstance = new IoGPersistenceManager();
			}
		return (singletonInstance);
	}

	IoGPersistenceManager()
	{
		new Timer().schedule(new TimerTask(){@Override public void run(){checkForExpiredItems();}}, IoGConfigurationManager.timerPeriodPersistenceExpirationCheck, IoGConfigurationManager.timerPeriodPersistenceExpirationCheck);
	}

	public boolean saveValue(String name, Object value, PersistenceDataType type, PersistenceSource destination, PersistenceProtectionLevel protection, PersistenceLifespan lifespan, Date expiration, boolean overwrite)
	{
		Context context = IoGConfigurationManager.getSharedManager().getApplicationContext();
		if (context == null)
			{
			return (false);
			}
		SharedPreferences prefs = IoGConfigurationManager.getSharedManager().getSharedPreferences();
		HashMap<String, Object> savedDataElement = new HashMap<>();
		savedDataElement.put(IoGConfigurationManager.persistenceElementValue, value);
		savedDataElement.put(IoGConfigurationManager.persistenceElementType, type);
		savedDataElement.put(IoGConfigurationManager.persistenceElementSource, destination);
		savedDataElement.put(IoGConfigurationManager.persistenceElementProtection, protection);
		savedDataElement.put(IoGConfigurationManager.persistenceElementLifespan, lifespan);
		savedDataElement.put(IoGConfigurationManager.persistenceElementExpiration, expiration);
		if (destination == PersistenceSource.Memory)
			{
			if (!memoryStore.containsKey(name) || overwrite)
				{
				memoryStore.put(name, savedDataElement);
				}
			else
				{
				return (false);
				}
			}
		else if (destination == PersistenceSource.SharedPreferences)
			{
			SharedPreferences.Editor editor = prefs.edit();
			Gson gson = new Gson();
			if (!prefs.contains(name) || overwrite)
				{
				String hashMapString = gson.toJson(savedDataElement);
				editor.putString(name, hashMapString);
				editor.commit();
				}
			else
				{
				return (false);
				}
			}
		else if (destination == PersistenceSource.FileStorage)
			{
			Gson gson = new Gson();
			String hashMapString = gson.toJson(savedDataElement);
			File filesDir = context.getFilesDir();
			String filesDirPath = filesDir.getAbsolutePath();
			String persistenceFilePath = filesDirPath + IoGConfigurationManager.persistenceFolderPath.replace('/', File.separatorChar);
			File file = new File(persistenceFilePath);
			File destinationFile = new File(persistenceFilePath, name);
			if (file.exists() == false)
				{
				file.mkdirs();
				}
			if (destinationFile.exists() == true && !overwrite)
				{
				return (false);
				}
			else if (destinationFile.exists() == true && overwrite)
				{
				destinationFile.delete();
				}
			try
				{
				FileOutputStream outputStream = new FileOutputStream(destinationFile);
				outputStream.write(hashMapString.getBytes());
				outputStream.close();
				}
			catch (Exception ex)
				{
				return (false);
				}
			}
		if (lifespan == PersistenceLifespan.Session)
			{
			if (prefs.contains(IoGConfigurationManager.persistenceManagementSessionItems) == false)
				{
				SharedPreferences.Editor editor = prefs.edit();
				Gson gson = new Gson();
				HashMap<String, Object> sessionItemEntry = new HashMap<String, Object>();
				ArrayList<HashMap<String, Object>> sessionItemEntries = new ArrayList<HashMap<String, Object>>();
				sessionItemEntry.put(IoGConfigurationManager.persistenceExpirationItemName, name);
				sessionItemEntry.put(IoGConfigurationManager.persistenceExpirationItemSource, destination);
				sessionItemEntries.add(sessionItemEntry);
				String hashMapString = gson.toJson(sessionItemEntries);
				editor.putString(IoGConfigurationManager.persistenceManagementSessionItems, hashMapString);
				editor.commit();
				}
			else
				{
				SharedPreferences.Editor editor = prefs.edit();
				String hashMapArrayString = prefs.getString(IoGConfigurationManager.persistenceManagementSessionItems, null);
				Gson gson = new Gson();
				ArrayList<HashMap<String, Object>> sessionItemEntries = gson.fromJson(hashMapArrayString, ArrayList.class);
				HashMap<String, Object> sessionItemEntry = new HashMap<String, Object>();
				sessionItemEntry.put(IoGConfigurationManager.persistenceExpirationItemName, name);
				sessionItemEntry.put(IoGConfigurationManager.persistenceExpirationItemSource, destination);
				sessionItemEntries.add(sessionItemEntry);
				String hashMapString = gson.toJson(sessionItemEntries);
				editor.putString(IoGConfigurationManager.persistenceManagementSessionItems, hashMapString);
				editor.commit();
				}
			}
		if (expiration != null)
			{
			if (!prefs.contains(IoGConfigurationManager.persistenceManagementExpiringItems))
				{
				SharedPreferences.Editor editor = prefs.edit();
				Gson gson = new Gson();
				DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG);
				ArrayList<HashMap<String, Object>> expirationDateItemEntries = new ArrayList<HashMap<String, Object>>();
				HashMap<String, ArrayList<HashMap<String, Object>>> expiringItemEntries = new HashMap<String, ArrayList<HashMap<String, Object>>>();
				HashMap<String, Object> expiringItemEntry = new HashMap<String, Object>();
				String expirationDateString = dateFormat.format(expiration);
				expiringItemEntry.put(IoGConfigurationManager.persistenceExpirationItemName, name);
				expiringItemEntry.put(IoGConfigurationManager.persistenceExpirationItemSource, destination);
				expirationDateItemEntries.add(expiringItemEntry);
				expiringItemEntries.put(expirationDateString, expirationDateItemEntries);
				String hashMapString = gson.toJson(expiringItemEntries);
				editor.putString(IoGConfigurationManager.persistenceManagementExpiringItems, hashMapString);
				editor.commit();
				}
			else
				{
				Gson gson = new Gson();
				SharedPreferences.Editor editor = prefs.edit();
				String expiringItemEntriesString = prefs.getString(IoGConfigurationManager.persistenceManagementExpiringItems, null);
				HashMap<Date, String[]>expiringItemEntries = gson.fromJson(expiringItemEntriesString, HashMap.class);
				if (expiringItemEntries.containsKey(expiration))
					{
					String[] entries = expiringItemEntries.get(expiration);
					List<String> entryList = Arrays.asList(entries);
					entryList.add(name);
					String[] newEntryArray = entryList.toArray(new String[0]);
					expiringItemEntries.put(expiration, newEntryArray);
					String hashMapString = gson.toJson(value);
					editor.putString(IoGConfigurationManager.persistenceManagementExpiringItems, hashMapString);
					editor.commit();
					}
				else
					{
					String[] expiringItems = {name};
					expiringItemEntries.put(expiration, expiringItems);
					String hashMapString = gson.toJson(value);
					editor.putString(IoGConfigurationManager.persistenceManagementExpiringItems, hashMapString);
					editor.commit();
					}
				}
			}
		return (true);
	}

	public HashMap<String, Object> readValue(String name, PersistenceSource from)
	{
		Context context = IoGConfigurationManager.getSharedManager().getApplicationContext();
		HashMap<String, Object> returnValue = new HashMap<String, Object>();
		if (from == PersistenceSource.Memory)
			{
			if (memoryStore.containsKey(name))
				{
				HashMap<String, Object> itemEntry = memoryStore.get(name);
				returnValue.put(IoGConfigurationManager.persistenceReadResultCode, PersistenceReadResultCode.Success);
				returnValue.put(IoGConfigurationManager.persistenceReadResultValue, itemEntry.get(IoGConfigurationManager.persistenceElementValue));
				}
			else
				{
				returnValue.put(IoGConfigurationManager.persistenceReadResultCode, PersistenceReadResultCode.NotFound);
				returnValue.put(IoGConfigurationManager.persistenceReadResultValue, null);
				}
			}
		else if (from == PersistenceSource.SharedPreferences)
			{
			if (context == null)
				{
				returnValue.put(IoGConfigurationManager.persistenceReadResultCode, PersistenceReadResultCode.NoContext);
				returnValue.put(IoGConfigurationManager.persistenceReadResultValue, null);
				return (returnValue);
				}
			SharedPreferences prefs = IoGConfigurationManager.getSharedManager().getSharedPreferences();
			Gson gson = new Gson();
			if (prefs.contains(name))
				{
				String hashMapString = prefs.getString(name, null);
				Type hashmapType = new TypeToken<HashMap<String, String>>() {}.getType();
				HashMap<String, Object> entry = gson.fromJson(hashMapString, HashMap.class);
				returnValue.put(IoGConfigurationManager.persistenceReadResultCode, PersistenceReadResultCode.Success);
				returnValue.put(IoGConfigurationManager.persistenceReadResultValue, entry.get(IoGConfigurationManager.persistenceElementValue));
				}
			else
				{
				returnValue.put(IoGConfigurationManager.persistenceReadResultCode, PersistenceReadResultCode.NotFound);
				returnValue.put(IoGConfigurationManager.persistenceReadResultValue, null);
				}
			}
		else if (from == PersistenceSource.FileStorage)
			{
			File filesDir = context.getFilesDir();
			String filesDirPath = filesDir.getAbsolutePath();
			String persistenceFilePath = filesDirPath + IoGConfigurationManager.persistenceFolderPath.replace('/', File.separatorChar);
			File sourceFile = new File(persistenceFilePath, name);
			if (!sourceFile.exists())
				{
				returnValue.put(IoGConfigurationManager.persistenceReadResultCode, PersistenceReadResultCode.NotFound);
				returnValue.put(IoGConfigurationManager.persistenceReadResultValue, null);
				}
			else
				{
				int fileLength = (int) sourceFile.length();
				byte[] fileBytes = new byte[fileLength];
				try
					{
					Gson gson = new Gson();
					FileInputStream inputStream = new FileInputStream(sourceFile);
					inputStream.read(fileBytes);
					String hashMapString = new String(fileBytes);
					HashMap<String, Object> entry = gson.fromJson(hashMapString, HashMap.class);
					returnValue.put(IoGConfigurationManager.persistenceReadResultCode, PersistenceReadResultCode.Success);
					returnValue.put(IoGConfigurationManager.persistenceReadResultValue, entry.get(IoGConfigurationManager.persistenceElementValue));
					inputStream.close();
					}
				catch (Exception ex)
					{
					returnValue.put(IoGConfigurationManager.persistenceReadResultCode, PersistenceReadResultCode.NotFound);
					returnValue.put(IoGConfigurationManager.persistenceReadResultValue, null);
					}
				}
			}
		return (returnValue);
	}

	public Boolean checkForValue(String name, PersistenceSource from)
	{
		if (from == PersistenceSource.Memory)
			{
			if (memoryStore.containsKey(name))
				{
				return (true);
				}
			}
		else if (from == PersistenceSource.SharedPreferences)
			{
			Context context = IoGConfigurationManager.getSharedManager().getApplicationContext();
			if (context == null)
				{
				return (false);
				}
			SharedPreferences prefs = IoGConfigurationManager.getSharedManager().getSharedPreferences();
			if (prefs.contains(name))
				{
				return (true);
				}
			}
		else if (from == PersistenceSource.FileStorage)
			{
			Context context = IoGConfigurationManager.getSharedManager().getApplicationContext();
			if (context == null)
				{
				return (false);
				}
			File filesDir = context.getFilesDir();
			String filesDirPath = filesDir.getAbsolutePath();
			String persistenceFilePath = filesDirPath + IoGConfigurationManager.persistenceFolderPath.replace('/', File.separatorChar);
			File sourceFile = new File(persistenceFilePath, name);
			return (sourceFile.exists());
			}
		return (false);
	}

	public boolean clearValue(String name, PersistenceSource from)
	{
		if (checkForValue(name, from))
			{
			if (from == PersistenceSource.Memory)
				{
				memoryStore.remove(name);
				return (true);
				}
			else if (from == PersistenceSource.SharedPreferences)
				{
				Context context = IoGConfigurationManager.getSharedManager().getApplicationContext();
				if (context == null)
					{
					return (false);
					}
				SharedPreferences prefs = IoGConfigurationManager.getSharedManager().getSharedPreferences();
				SharedPreferences.Editor editor = prefs.edit();
				editor.remove(name);
				editor.commit();
				return (true);
				}
			else if (from == PersistenceSource.FileStorage)
				{
				Context context = IoGConfigurationManager.getSharedManager().getApplicationContext();
				if (context == null)
					{
					return (false);
					}
				File filesDir = context.getFilesDir();
				String filesDirPath = filesDir.getAbsolutePath();
				String persistenceFilePath = filesDirPath + IoGConfigurationManager.persistenceFolderPath.replace('/', File.separatorChar);
				File sourceFile = new File(persistenceFilePath, name);
				if (sourceFile.exists())
					{
					sourceFile.delete();
					return (true);
					}
				}
			}
		return (false);
	}

	public void checkForExpiredItems()
	{
		Context context = IoGConfigurationManager.getSharedManager().getApplicationContext();
		if (context == null)
			{
			return;
			}
		SharedPreferences prefs = IoGConfigurationManager.getSharedManager().getSharedPreferences();
		Gson gson = new Gson();
		if (prefs.contains(IoGConfigurationManager.persistenceManagementExpiringItems))
			{
			SharedPreferences.Editor editor = prefs.edit();
			String expiringItemEntriesString = prefs.getString(IoGConfigurationManager.persistenceManagementExpiringItems, null);
			DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG);
			HashMap<String, ArrayList<LinkedTreeMap<String, Object>>>expiringItemEntries = gson.fromJson(expiringItemEntriesString, HashMap.class);
			Set<String> dateSet = expiringItemEntries.keySet();
			ArrayList<String> expiredDates = new ArrayList<>();
			for (String nextDate : dateSet)
				{
				try
					{
					Date date = dateFormat.parse(nextDate);
					if (date.before(new Date()))
						{
						ArrayList<LinkedTreeMap<String, Object>> expiredItems = expiringItemEntries.get(nextDate);
						expiredDates.add(nextDate);
						for (LinkedTreeMap<String, Object> nextItem : expiredItems)
							{
							String name = (String) nextItem.get(IoGConfigurationManager.persistenceExpirationItemName);
							String persistenceSourceString = (String) nextItem.get(IoGConfigurationManager.persistenceExpirationItemSource);
							PersistenceSource source;
							switch (persistenceSourceString)
								{
								case "Memory":
									source = PersistenceSource.Memory;
									break;
								case "SharedPreferences":
									source = PersistenceSource.SharedPreferences;
									break;
								case "FileStorage":
									source = PersistenceSource.FileStorage;
									break;
								default:
									source = PersistenceSource.Memory;
									break;
								}
							clearValue(name, source);
							}
						}
					}
				catch (Exception ex)
					{
					}
				}
			for (String dateToRemove : expiredDates)
				{
				expiringItemEntries.remove(dateToRemove);
				}
			if (expiringItemEntries.size() > expiredDates.size())
				{
				String hashMapString = gson.toJson(expiringItemEntries);
				editor.putString(IoGConfigurationManager.persistenceManagementExpiringItems, hashMapString);
				}
			else
				{
				editor.remove(IoGConfigurationManager.persistenceManagementExpiringItems);
				}
			editor.commit();
			}
	}

	public void removeSessionItems()
	{
		Context context = IoGConfigurationManager.getSharedManager().getApplicationContext();
		if (context == null)
			{
			return;
			}
		SharedPreferences prefs = IoGConfigurationManager.getSharedManager().getSharedPreferences();
		SharedPreferences.Editor editor = prefs.edit();
		if (prefs.contains(IoGConfigurationManager.persistenceManagementSessionItems))
			{
			String hashMapArrayString = prefs.getString(IoGConfigurationManager.persistenceManagementSessionItems, null);
			Gson gson = new Gson();
			ArrayList<LinkedTreeMap<String, Object>> sessionItemEntries = gson.fromJson(hashMapArrayString, ArrayList.class);
			for (LinkedTreeMap<String, Object> nextEntry : sessionItemEntries)
				{
				String name = (String)nextEntry.get(IoGConfigurationManager.persistenceExpirationItemName);
				String persistenceSourceString = (String)nextEntry.get(IoGConfigurationManager.persistenceExpirationItemSource);
				PersistenceSource source;
				switch (persistenceSourceString)
					{
					case "Memory":
						source = PersistenceSource.Memory;
						break;
					case "SharedPreferences":
						source = PersistenceSource.SharedPreferences;
						break;
					case "FileStorage":
						source = PersistenceSource.FileStorage;
						break;
					default:
						source = PersistenceSource.Memory;
						break;
					}
				if (source != null)
					{
					clearValue(name, source);
					}
				}
			editor.remove(IoGConfigurationManager.persistenceManagementSessionItems);
			editor.commit();
			}
	}
}
