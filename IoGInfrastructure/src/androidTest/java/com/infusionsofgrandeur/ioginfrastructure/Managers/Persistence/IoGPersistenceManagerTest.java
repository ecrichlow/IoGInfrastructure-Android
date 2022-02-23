package com.infusionsofgrandeur.ioginfrastructure.Managers.Persistence;

import android.content.SharedPreferences;
import android.os.Environment;
import android.Manifest;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.GrantPermissionRule;

import com.infusionsofgrandeur.ioginfrastructure.Managers.Configuration.IoGConfigurationManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.ArrayList;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class IoGPersistenceManagerTest
{

	public IoGConfigurationManager configurartionManager;
	public IoGPersistenceManager persistenceManager;
	public IoGPersistenceManager.PersistenceSource persistenceSource;

	private CompletableFuture<String> future;

	@Rule
	public GrantPermissionRule runtimePermissionRuleRead = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);

	@Rule
	public GrantPermissionRule runtimePermissionRuleWrite = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE);

	@Before
	public void setUp()
	{
		configurartionManager = IoGConfigurationManager.getSharedManager();
		persistenceManager = IoGPersistenceManager.getSharedManager();
		configurartionManager.setApplicationContext(InstrumentationRegistry.getContext());
		SharedPreferences prefs = IoGConfigurationManager.getSharedManager().getSharedPreferences();
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(IoGConfigurationManager.persistenceManagementSessionItems);
		editor.remove(IoGConfigurationManager.persistenceManagementExpiringItems);
		editor.commit();
	}

	@After
	public void tearDown()
	{
		String homePathString = Environment.getExternalStorageDirectory().getAbsolutePath();
		String persistencePathString = homePathString + com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceFolderPath;
		File filesDir = new File(persistencePathString);
		File file = new File(filesDir, persistencePathString);
		if (file.exists())
			{
			file.delete();
			}
		persistenceManager.clearValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		persistenceManager.removeSessionItems();
		configurartionManager = null;
		persistenceManager = null;
		persistenceSource = null;
	}

	@Test
	public void testSaveBoolean()
	{
		persistenceSource = IoGPersistenceManager.PersistenceSource.Memory;
		Boolean saveBoolean = new Boolean(true);
		boolean saveResult = persistenceManager.saveValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, saveBoolean, IoGPersistenceManager.PersistenceDataType.Boolean, persistenceSource, IoGPersistenceManager.PersistenceProtectionLevel.Unsecured, IoGPersistenceManager.PersistenceLifespan.Immortal, null, false);
		assertTrue(saveResult);
		HashMap<String, Object> readResponse = persistenceManager.readValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		IoGPersistenceManager.PersistenceReadResultCode readResult = (IoGPersistenceManager.PersistenceReadResultCode) readResponse.get(IoGConfigurationManager.persistenceReadResultCode);
		Boolean readValue = (Boolean) readResponse.get(IoGConfigurationManager.persistenceReadResultValue);
		assertEquals(readResult, IoGPersistenceManager.PersistenceReadResultCode.Success);
		assertEquals(readValue, saveBoolean);
	}

	@Test
	public void testSaveNumber()
	{
		persistenceSource = IoGPersistenceManager.PersistenceSource.Memory;
		Integer saveNumber = new Integer(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestNumericValue);
		boolean saveResult = persistenceManager.saveValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, saveNumber, IoGPersistenceManager.PersistenceDataType.Number, persistenceSource, IoGPersistenceManager.PersistenceProtectionLevel.Unsecured, IoGPersistenceManager.PersistenceLifespan.Immortal, null, false);
		assertTrue(saveResult);
		HashMap<String, Object> readResponse = persistenceManager.readValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		IoGPersistenceManager.PersistenceReadResultCode readResult = (IoGPersistenceManager.PersistenceReadResultCode) readResponse.get(IoGConfigurationManager.persistenceReadResultCode);
		Integer readValue = (Integer) readResponse.get(IoGConfigurationManager.persistenceReadResultValue);
		assertEquals(readResult, IoGPersistenceManager.PersistenceReadResultCode.Success);
		assertEquals(readValue, saveNumber);
	}

	@Test
	public void testSaveString()
	{
		persistenceSource = IoGPersistenceManager.PersistenceSource.SharedPreferences;
		String saveString = com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestStringValue;
		boolean saveResult = persistenceManager.saveValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, saveString, IoGPersistenceManager.PersistenceDataType.String, persistenceSource, IoGPersistenceManager.PersistenceProtectionLevel.Unsecured, IoGPersistenceManager.PersistenceLifespan.Session, null, true);
		assertTrue(saveResult);
		HashMap<String, Object> readResponse = persistenceManager.readValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		IoGPersistenceManager.PersistenceReadResultCode readResult = (IoGPersistenceManager.PersistenceReadResultCode) readResponse.get(IoGConfigurationManager.persistenceReadResultCode);
		String readValue = (String) readResponse.get(IoGConfigurationManager.persistenceReadResultValue);
		assertEquals(readResult, IoGPersistenceManager.PersistenceReadResultCode.Success);
		assertEquals(readValue, saveString);
	}

	@Test
	public void testSaveArray()
	{
		persistenceSource = IoGPersistenceManager.PersistenceSource.FileStorage;
		List<String> saveArray = com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestArrayValue;
		boolean saveResult = persistenceManager.saveValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, saveArray, IoGPersistenceManager.PersistenceDataType.Array, persistenceSource, IoGPersistenceManager.PersistenceProtectionLevel.Unsecured, IoGPersistenceManager.PersistenceLifespan.Immortal, null, true);
		assertTrue(saveResult);
		HashMap<String, Object> readResponse = persistenceManager.readValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		IoGPersistenceManager.PersistenceReadResultCode readResult = (IoGPersistenceManager.PersistenceReadResultCode) readResponse.get(IoGConfigurationManager.persistenceReadResultCode);
		ArrayList<String> readValue = (ArrayList<String>) readResponse.get(IoGConfigurationManager.persistenceReadResultValue);
		assertEquals(readResult, IoGPersistenceManager.PersistenceReadResultCode.Success);
		int index = 0;
		for (String nextValue : saveArray)
			{
			assertEquals(nextValue, readValue.get(index++));
			}
	}

	@Test
	public void testSaveMap()
	{
		persistenceSource = IoGPersistenceManager.PersistenceSource.Memory;
		Map<String, Object> saveMap = com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestHashMapValue;
		boolean saveResult = persistenceManager.saveValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, saveMap, IoGPersistenceManager.PersistenceDataType.HashMap, persistenceSource, IoGPersistenceManager.PersistenceProtectionLevel.Unsecured, IoGPersistenceManager.PersistenceLifespan.Session, null, false);
		assertTrue(saveResult);
		HashMap<String, Object> readResponse = persistenceManager.readValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		IoGPersistenceManager.PersistenceReadResultCode readResult = (IoGPersistenceManager.PersistenceReadResultCode) readResponse.get(IoGConfigurationManager.persistenceReadResultCode);
		HashMap<String, Object> readValue = (HashMap<String, Object>) readResponse.get(IoGConfigurationManager.persistenceReadResultValue);
		assertEquals(readResult, IoGPersistenceManager.PersistenceReadResultCode.Success);
		assertEquals(saveMap.size(), readValue.size());
		for (String nextKey : saveMap.keySet())
			{
			assertEquals(saveMap.get(nextKey), readValue.get(nextKey));
			}
	}

	@Test
	public void testSaveDataToSharedPreferences()
	{
		persistenceSource = IoGPersistenceManager.PersistenceSource.SharedPreferences;
		Map<String, Object> saveMap = com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestHashMapValue;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ObjectOutput objectOut = null;
		byte[] byteArray = new byte[]{};
		try
			{
			objectOut = new ObjectOutputStream(outputStream);
			objectOut.writeObject(saveMap);
			objectOut.flush();
			byteArray = outputStream.toByteArray();
			}
		catch (Exception ex)
			{
			Assert.fail();
			}
		finally
			{
			try
				{
				outputStream.close();
				}
			catch (IOException ex)
				{
				Assert.fail();
				}
			}
		boolean saveResult = persistenceManager.saveValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, byteArray, IoGPersistenceManager.PersistenceDataType.Data, persistenceSource, IoGPersistenceManager.PersistenceProtectionLevel.Unsecured, IoGPersistenceManager.PersistenceLifespan.Immortal, null, true);
		assertTrue(saveResult);
		HashMap<String, Object> readResponse = persistenceManager.readValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		IoGPersistenceManager.PersistenceReadResultCode readResult = (IoGPersistenceManager.PersistenceReadResultCode) readResponse.get(IoGConfigurationManager.persistenceReadResultCode);
		ArrayList<Double> readValue = (ArrayList) readResponse.get(IoGConfigurationManager.persistenceReadResultValue);
		byte[] readBytes = new byte[readValue.size()];
		for(int i = 0; i < readValue.size(); i++)
			{
			int intValue = (int)readValue.get(i).doubleValue();
			byte nextByte = (byte) intValue;
			readBytes[i] = nextByte;
			}
		assertEquals(readResult, IoGPersistenceManager.PersistenceReadResultCode.Success);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(readBytes);
		ObjectInput objectIn = null;
		HashMap<String, Object> readMap = new HashMap<>();
		try
			{
			objectIn = new ObjectInputStream(inputStream);
			Object object = objectIn.readObject();
			readMap = (HashMap<String, Object>) object;
			}
		catch (Exception ex)
			{
			Assert.fail();
			}
		finally
			{
			try
				{
				if (inputStream != null)
					{
					inputStream.close();
					}
				}
			catch (IOException ex)
				{
				Assert.fail();
				}
			}
		assertEquals(saveMap.size(), readMap.size());
		for (String nextKey : saveMap.keySet())
			{
			assertEquals(saveMap.get(nextKey), readMap.get(nextKey));
			}
	}

	@Test
	public void testSaveDataToMemory()
	{
		persistenceSource = IoGPersistenceManager.PersistenceSource.Memory;
		Map<String, Object> saveMap = com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestHashMapValue;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ObjectOutput objectOut = null;
		byte[] byteArray = new byte[]{};
		try
			{
			objectOut = new ObjectOutputStream(outputStream);
			objectOut.writeObject(saveMap);
			objectOut.flush();
			byteArray = outputStream.toByteArray();
			}
		catch (Exception ex)
			{
			Assert.fail();
			}
		finally
			{
			try
				{
				outputStream.close();
				}
			catch (IOException ex)
				{
				Assert.fail();
				}
			}
		boolean saveResult = persistenceManager.saveValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, byteArray, IoGPersistenceManager.PersistenceDataType.Data, persistenceSource, IoGPersistenceManager.PersistenceProtectionLevel.Unsecured, IoGPersistenceManager.PersistenceLifespan.Immortal, null, true);
		assertTrue(saveResult);
		HashMap<String, Object> readResponse = persistenceManager.readValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		IoGPersistenceManager.PersistenceReadResultCode readResult = (IoGPersistenceManager.PersistenceReadResultCode) readResponse.get(IoGConfigurationManager.persistenceReadResultCode);
		byte[] readValue = (byte[]) readResponse.get(IoGConfigurationManager.persistenceReadResultValue);
		assertEquals(readResult, IoGPersistenceManager.PersistenceReadResultCode.Success);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(readValue);
		ObjectInput objectIn = null;
		HashMap<String, Object> readMap = new HashMap<>();
		try
			{
			objectIn = new ObjectInputStream(inputStream);
			Object object = objectIn.readObject();
			readMap = (HashMap<String, Object>) object;
			}
		catch (Exception ex)
			{
			Assert.fail();
			}
		finally
			{
			try
				{
				if (inputStream != null)
					{
					inputStream.close();
					}
				}
			catch (IOException ex)
				{
				Assert.fail();
				}
			}
		assertEquals(saveMap.size(), readMap.size());
		for (String nextKey : saveMap.keySet())
			{
			assertEquals(saveMap.get(nextKey), readMap.get(nextKey));
			}
	}

	@Test
	public void testSaveDataToFile()
	{
		persistenceSource = IoGPersistenceManager.PersistenceSource.FileStorage;
		Map<String, Object> saveMap = com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestHashMapValue;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ObjectOutput objectOut = null;
		byte[] byteArray = new byte[]{};
		try
			{
			objectOut = new ObjectOutputStream(outputStream);
			objectOut.writeObject(saveMap);
			objectOut.flush();
			byteArray = outputStream.toByteArray();
			}
		catch (Exception ex)
			{
			Assert.fail();
			}
		finally
			{
			try
				{
				outputStream.close();
				}
			catch (IOException ex)
				{
				Assert.fail();
				}
			}
		boolean saveResult = persistenceManager.saveValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, byteArray, IoGPersistenceManager.PersistenceDataType.Data, persistenceSource, IoGPersistenceManager.PersistenceProtectionLevel.Unsecured, IoGPersistenceManager.PersistenceLifespan.Immortal, null, true);
		assertTrue(saveResult);
		HashMap<String, Object> readResponse = persistenceManager.readValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		IoGPersistenceManager.PersistenceReadResultCode readResult = (IoGPersistenceManager.PersistenceReadResultCode) readResponse.get(IoGConfigurationManager.persistenceReadResultCode);
		ArrayList<Double> readValue = (ArrayList) readResponse.get(IoGConfigurationManager.persistenceReadResultValue);
		byte[] readBytes = new byte[readValue.size()];
		for(int i = 0; i < readValue.size(); i++)
			{
			int intValue = (int)readValue.get(i).doubleValue();
			byte nextByte = (byte) intValue;
			readBytes[i] = nextByte;
			}
		assertEquals(readResult, IoGPersistenceManager.PersistenceReadResultCode.Success);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(readBytes);
		ObjectInput objectIn = null;
		HashMap<String, Object> readMap = new HashMap<>();
		try
			{
			objectIn = new ObjectInputStream(inputStream);
			Object object = objectIn.readObject();
			readMap = (HashMap<String, Object>) object;
			}
		catch (Exception ex)
			{
			Assert.fail();
			}
		finally
			{
			try
				{
				if (inputStream != null)
					{
					inputStream.close();
					}
				}
			catch (IOException ex)
				{
				Assert.fail();
				}
			}
		assertEquals(saveMap.size(), readMap.size());
		for (String nextKey : saveMap.keySet())
			{
			assertEquals(saveMap.get(nextKey), readMap.get(nextKey));
			}
	}

	@Test
	public void testSaveToMemory()
	{
		persistenceSource = IoGPersistenceManager.PersistenceSource.Memory;
		String saveString = com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestStringValue;
		boolean saveResult = persistenceManager.saveValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, saveString, IoGPersistenceManager.PersistenceDataType.String, persistenceSource, IoGPersistenceManager.PersistenceProtectionLevel.Unsecured, IoGPersistenceManager.PersistenceLifespan.Immortal, null, true);
		assertTrue(saveResult);
		HashMap<String, Object> readResponse = persistenceManager.readValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		IoGPersistenceManager.PersistenceReadResultCode readResult = (IoGPersistenceManager.PersistenceReadResultCode) readResponse.get(IoGConfigurationManager.persistenceReadResultCode);
		String readValue = (String) readResponse.get(IoGConfigurationManager.persistenceReadResultValue);
		assertEquals(readResult, IoGPersistenceManager.PersistenceReadResultCode.Success);
		assertEquals(readValue, saveString);
	}

	@Test
	public void testSaveToSharedPreferences()
	{
		persistenceSource = IoGPersistenceManager.PersistenceSource.SharedPreferences;
		String saveString = com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestStringValue;
		boolean saveResult = persistenceManager.saveValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, saveString, IoGPersistenceManager.PersistenceDataType.String, persistenceSource, IoGPersistenceManager.PersistenceProtectionLevel.Unsecured, IoGPersistenceManager.PersistenceLifespan.Immortal, null, true);
		assertTrue(saveResult);
		HashMap<String, Object> readResponse = persistenceManager.readValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		IoGPersistenceManager.PersistenceReadResultCode readResult = (IoGPersistenceManager.PersistenceReadResultCode) readResponse.get(IoGConfigurationManager.persistenceReadResultCode);
		String readValue = (String) readResponse.get(IoGConfigurationManager.persistenceReadResultValue);
		assertEquals(readResult, IoGPersistenceManager.PersistenceReadResultCode.Success);
		assertEquals(readValue, saveString);
	}

	@Test
	public void testSaveToFile()
	{
		persistenceSource = IoGPersistenceManager.PersistenceSource.FileStorage;
		String saveString = com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestStringValue;
		boolean saveResult = persistenceManager.saveValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, saveString, IoGPersistenceManager.PersistenceDataType.String, persistenceSource, IoGPersistenceManager.PersistenceProtectionLevel.Unsecured, IoGPersistenceManager.PersistenceLifespan.Immortal, null, true);
		assertTrue(saveResult);
		HashMap<String, Object> readResponse = persistenceManager.readValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		IoGPersistenceManager.PersistenceReadResultCode readResult = (IoGPersistenceManager.PersistenceReadResultCode) readResponse.get(IoGConfigurationManager.persistenceReadResultCode);
		String readValue = (String) readResponse.get(IoGConfigurationManager.persistenceReadResultValue);
		assertEquals(readResult, IoGPersistenceManager.PersistenceReadResultCode.Success);
		assertEquals(readValue, saveString);
	}

	@Test
	public void testFailRead()
	{
		persistenceSource = IoGPersistenceManager.PersistenceSource.SharedPreferences;
		HashMap<String, Object> readResponse = persistenceManager.readValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		IoGPersistenceManager.PersistenceReadResultCode readResult = (IoGPersistenceManager.PersistenceReadResultCode) readResponse.get(IoGConfigurationManager.persistenceReadResultCode);
		String readValue = (String) readResponse.get(IoGConfigurationManager.persistenceReadResultValue);
		assertEquals(readResult, IoGPersistenceManager.PersistenceReadResultCode.NotFound);
		assertNull(readValue);
	}

	@Test
	public void testOverwriteSave()
	{
		persistenceSource = IoGPersistenceManager.PersistenceSource.SharedPreferences;
		String firstSaveString = com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestStringValue;
		String secondSaveString = com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSecondaryStringValue;
		boolean saveResult = persistenceManager.saveValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, firstSaveString, IoGPersistenceManager.PersistenceDataType.String, persistenceSource, IoGPersistenceManager.PersistenceProtectionLevel.Unsecured, IoGPersistenceManager.PersistenceLifespan.Immortal, null, true);
		assertTrue(saveResult);
		saveResult = persistenceManager.saveValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, secondSaveString, IoGPersistenceManager.PersistenceDataType.String, persistenceSource, IoGPersistenceManager.PersistenceProtectionLevel.Unsecured, IoGPersistenceManager.PersistenceLifespan.Immortal, null, true);
		assertTrue(saveResult);
		HashMap<String, Object> readResponse = persistenceManager.readValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		IoGPersistenceManager.PersistenceReadResultCode readResult = (IoGPersistenceManager.PersistenceReadResultCode) readResponse.get(IoGConfigurationManager.persistenceReadResultCode);
		String readValue = (String) readResponse.get(IoGConfigurationManager.persistenceReadResultValue);
		assertEquals(readResult, IoGPersistenceManager.PersistenceReadResultCode.Success);
		assertEquals(readValue, secondSaveString);
	}

	@Test
	public void testFailOverwrite()
	{
		persistenceSource = IoGPersistenceManager.PersistenceSource.SharedPreferences;
		String firstSaveString = com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestStringValue;
		String secondSaveString = com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSecondaryStringValue;
		boolean saveResult = persistenceManager.saveValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, firstSaveString, IoGPersistenceManager.PersistenceDataType.String, persistenceSource, IoGPersistenceManager.PersistenceProtectionLevel.Unsecured, IoGPersistenceManager.PersistenceLifespan.Immortal, null, true);
		assertTrue(saveResult);
		saveResult = persistenceManager.saveValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, secondSaveString, IoGPersistenceManager.PersistenceDataType.String, persistenceSource, IoGPersistenceManager.PersistenceProtectionLevel.Unsecured, IoGPersistenceManager.PersistenceLifespan.Immortal, null, false);
		assertFalse(saveResult);
		HashMap<String, Object> readResponse = persistenceManager.readValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		IoGPersistenceManager.PersistenceReadResultCode readResult = (IoGPersistenceManager.PersistenceReadResultCode) readResponse.get(IoGConfigurationManager.persistenceReadResultCode);
		String readValue = (String) readResponse.get(IoGConfigurationManager.persistenceReadResultValue);
		assertEquals(readResult, IoGPersistenceManager.PersistenceReadResultCode.Success);
		assertEquals(readValue, firstSaveString);
	}

	@Test
	public void testClearValueSucceed()
	{
		persistenceSource = IoGPersistenceManager.PersistenceSource.SharedPreferences;
		String saveString = com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestStringValue;
		boolean saveResult = persistenceManager.saveValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, saveString, IoGPersistenceManager.PersistenceDataType.String, persistenceSource, IoGPersistenceManager.PersistenceProtectionLevel.Unsecured, IoGPersistenceManager.PersistenceLifespan.Immortal, null, true);
		assertTrue(saveResult);
		HashMap<String, Object> readResponse = persistenceManager.readValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		IoGPersistenceManager.PersistenceReadResultCode readResult = (IoGPersistenceManager.PersistenceReadResultCode) readResponse.get(IoGConfigurationManager.persistenceReadResultCode);
		String readValue = (String) readResponse.get(IoGConfigurationManager.persistenceReadResultValue);
		assertEquals(readResult, IoGPersistenceManager.PersistenceReadResultCode.Success);
		assertEquals(readValue, saveString);
		boolean clearResult = persistenceManager.clearValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		assertTrue(clearResult);
		readResponse = persistenceManager.readValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		readResult = (IoGPersistenceManager.PersistenceReadResultCode) readResponse.get(IoGConfigurationManager.persistenceReadResultCode);
		String readClearedValue = (String) readResponse.get(IoGConfigurationManager.persistenceReadResultValue);
		assertEquals(readResult, IoGPersistenceManager.PersistenceReadResultCode.NotFound);
		assertNull(readClearedValue);
	}

	@Test
	public void testClearValueFail()
	{
		persistenceSource = IoGPersistenceManager.PersistenceSource.SharedPreferences;
		boolean clearResult = persistenceManager.clearValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		assertFalse(clearResult);
	}

	@Test
	public void testCheckForValuePresent()
	{
		persistenceSource = IoGPersistenceManager.PersistenceSource.SharedPreferences;
		String saveString = com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestStringValue;
		boolean saveResult = persistenceManager.saveValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, saveString, IoGPersistenceManager.PersistenceDataType.String, persistenceSource, IoGPersistenceManager.PersistenceProtectionLevel.Unsecured, IoGPersistenceManager.PersistenceLifespan.Immortal, null, true);
		assertTrue(saveResult);
		HashMap<String, Object> readResponse = persistenceManager.readValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		IoGPersistenceManager.PersistenceReadResultCode readResult = (IoGPersistenceManager.PersistenceReadResultCode) readResponse.get(IoGConfigurationManager.persistenceReadResultCode);
		String readValue = (String) readResponse.get(IoGConfigurationManager.persistenceReadResultValue);
		assertEquals(readResult, IoGPersistenceManager.PersistenceReadResultCode.Success);
		assertEquals(readValue, saveString);
		boolean checkResult = persistenceManager.checkForValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		assertTrue(checkResult);
	}

	@Test
	public void testCheckForValueMissing()
	{
		persistenceSource = IoGPersistenceManager.PersistenceSource.SharedPreferences;
		boolean checkResult = persistenceManager.checkForValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		assertFalse(checkResult);
	}

	@Test
	public void testImmortalSave()
	{
		configurartionManager.setSessionActive(true);
		persistenceSource = IoGPersistenceManager.PersistenceSource.Memory;
		String saveString = com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestStringValue;
		boolean saveResult = persistenceManager.saveValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, saveString, IoGPersistenceManager.PersistenceDataType.String, persistenceSource, IoGPersistenceManager.PersistenceProtectionLevel.Unsecured, IoGPersistenceManager.PersistenceLifespan.Immortal, null, true);
		assertTrue(saveResult);
		configurartionManager.setSessionActive(false);
		HashMap<String, Object> readResponse = persistenceManager.readValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		IoGPersistenceManager.PersistenceReadResultCode readResult = (IoGPersistenceManager.PersistenceReadResultCode) readResponse.get(IoGConfigurationManager.persistenceReadResultCode);
		String readValue = (String) readResponse.get(IoGConfigurationManager.persistenceReadResultValue);
		assertEquals(readResult, IoGPersistenceManager.PersistenceReadResultCode.Success);
		assertEquals(readValue, saveString);
	}

	@Test
	public void testSessionSave()
	{
		configurartionManager.setSessionActive(true);
		persistenceSource = IoGPersistenceManager.PersistenceSource.Memory;
		String saveString = com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestStringValue;
		boolean saveResult = persistenceManager.saveValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, saveString, IoGPersistenceManager.PersistenceDataType.String, persistenceSource, IoGPersistenceManager.PersistenceProtectionLevel.Unsecured, IoGPersistenceManager.PersistenceLifespan.Session, null, true);
		assertTrue(saveResult);
		configurartionManager.setSessionActive(false);
		boolean checkResult = persistenceManager.checkForValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		assertFalse(checkResult);
	}

	@Test
	public void testExpiringSave()
	{
		persistenceSource = IoGPersistenceManager.PersistenceSource.SharedPreferences;
		String saveString = com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestStringValue;
		Date expirationTime = new Date();
		Long currentMilliseconds = expirationTime.getTime();
		expirationTime.setTime(currentMilliseconds + com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestExpiration);
		boolean saveResult = persistenceManager.saveValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, saveString, IoGPersistenceManager.PersistenceDataType.String, persistenceSource, IoGPersistenceManager.PersistenceProtectionLevel.Unsecured, IoGPersistenceManager.PersistenceLifespan.Expiration, expirationTime, true);
		assertTrue(saveResult);
		try
			{
			Thread.sleep(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestExpirationCheck);
			}
		catch(Exception ex)
			{
			}
		boolean checkResult = persistenceManager.checkForValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		assertFalse(checkResult);
	}

	@Test
	public void testExpiringSaveAndReuseIdentifier()
	{
		persistenceSource = IoGPersistenceManager.PersistenceSource.SharedPreferences;
		String saveString = com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestStringValue;
		Date expirationTime = new Date();
		Long currentMilliseconds = expirationTime.getTime();
		expirationTime.setTime(currentMilliseconds + com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestExpiration);
		boolean saveResult = persistenceManager.saveValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, saveString, IoGPersistenceManager.PersistenceDataType.String, persistenceSource, IoGPersistenceManager.PersistenceProtectionLevel.Unsecured, IoGPersistenceManager.PersistenceLifespan.Expiration, expirationTime, true);
		assertTrue(saveResult);
		try
			{
			Thread.sleep(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestExpirationCheck);
			}
		catch(Exception ex)
			{
			}
		boolean checkResult = persistenceManager.checkForValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		assertFalse(checkResult);
		saveString = com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSecondaryStringValue;
		currentMilliseconds = expirationTime.getTime();
		expirationTime.setTime(currentMilliseconds + com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestExpiration);
		saveResult = persistenceManager.saveValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, saveString, IoGPersistenceManager.PersistenceDataType.String, persistenceSource, IoGPersistenceManager.PersistenceProtectionLevel.Unsecured, IoGPersistenceManager.PersistenceLifespan.Expiration, expirationTime, true);
		assertTrue(saveResult);
		checkResult = persistenceManager.checkForValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		assertTrue(checkResult);
		HashMap<String, Object> readResponse = persistenceManager.readValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		IoGPersistenceManager.PersistenceReadResultCode readResult = (IoGPersistenceManager.PersistenceReadResultCode) readResponse.get(IoGConfigurationManager.persistenceReadResultCode);
		String readValue = (String) readResponse.get(IoGConfigurationManager.persistenceReadResultValue);
		assertEquals(readResult, IoGPersistenceManager.PersistenceReadResultCode.Success);
		assertEquals(readValue, saveString);
		try
			{
			Thread.sleep(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestExpirationCheck);
			}
		catch(Exception ex)
			{
			}
		checkResult = persistenceManager.checkForValue(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.persistenceTestSaveName, persistenceSource);
		assertFalse(checkResult);
	}
}
