package com.infusionsofgrandeur.ioginfrastructure.Managers.Encryption;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager;
import com.infusionsofgrandeur.ioginfrastructure.Managers.Configuration.IoGConfigurationManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import static org.junit.Assert.*;

public class IoGEncryptionKeyManagerTest
{

	@Before
	public void setUp() throws Exception
	{
		Context appContext = InstrumentationRegistry.getTargetContext();
		IoGConfigurationManager.getSharedManager().setApplicationContext(appContext);
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testEncryptDecryptWithDefaultKey()
	{
		String encodedString = EncryptionKeyManager.getSharedManager().encryptAndEncodeString(IoGTestConfigurationManager.stringToEncrypt);
		assertNotNull(encodedString);
		assertNotEquals(encodedString, IoGTestConfigurationManager.stringToEncrypt);
		String decodedString = EncryptionKeyManager.getSharedManager().decodeAndDecryptString(encodedString);
		assertNotNull(decodedString);
		assertEquals(decodedString, IoGTestConfigurationManager.stringToEncrypt);
	}

	@Test
	public void testEncryptDecryptWithCustomKey()
	{
		try
			{
			SecureRandom secureRandom = new SecureRandom();
			KeyGenerator keyGenerator = KeyGenerator.getInstance(IoGConfigurationManager.ALGORITHM_AES);
			keyGenerator.init(IoGConfigurationManager.AES_KEY_SIZE, secureRandom);
			SecretKey key = keyGenerator.generateKey();
			String encodedString = EncryptionKeyManager.getSharedManager().encryptAndEncodeString(IoGTestConfigurationManager.stringToEncrypt, key);
			assertNotNull(encodedString);
			assertNotEquals(encodedString, IoGTestConfigurationManager.stringToEncrypt);
			String decodedString = EncryptionKeyManager.getSharedManager().decodeAndDecryptString(encodedString, key);
			assertNotNull(decodedString);
			assertEquals(decodedString, IoGTestConfigurationManager.stringToEncrypt);
			}
		catch (Exception ex)
			{
			Assert.fail();
			}
	}
}
