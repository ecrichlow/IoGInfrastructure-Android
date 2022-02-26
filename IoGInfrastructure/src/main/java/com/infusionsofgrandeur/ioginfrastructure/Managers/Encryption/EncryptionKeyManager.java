package com.infusionsofgrandeur.ioginfrastructure.Managers.Encryption;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.util.Base64;
import java.util.HashMap;
import java.nio.charset.StandardCharsets;

import com.infusionsofgrandeur.ioginfrastructure.Managers.Configuration.IoGConfigurationManager;
import com.infusionsofgrandeur.ioginfrastructure.Managers.Persistence.IoGPersistenceManager;

public class EncryptionKeyManager
{

	private static EncryptionKeyManager singletonInstance = null;

	private SecretKey AESKey = null;

	private EncryptionKeyManager()
	{
	}

	public static EncryptionKeyManager getSharedManager()
	{
		if (singletonInstance == null)
			{
			singletonInstance = new EncryptionKeyManager();
			}
		return singletonInstance;
	}

	private static SecretKey createSymmetricKey()
	{
		try
			{
			SecureRandom secureRandom = new SecureRandom();
			KeyGenerator keyGenerator = KeyGenerator.getInstance(IoGConfigurationManager.ALGORITHM_AES);
			keyGenerator.init(IoGConfigurationManager.AES_KEY_SIZE, secureRandom);
			SecretKey key = keyGenerator.generateKey();
			byte[] encodedKey = key.getEncoded();
			String encodedKeyString = Base64.getEncoder().encodeToString(encodedKey);
			IoGPersistenceManager.getSharedManager().saveValue(IoGConfigurationManager.symmetricKeyIdentifier, encodedKeyString, IoGPersistenceManager.PersistenceDataType.String, IoGPersistenceManager.PersistenceSource.SharedPreferences, IoGPersistenceManager.PersistenceProtectionLevel.Unsecured, IoGPersistenceManager.PersistenceLifespan.Immortal, null, true);
			return (key);
			}
		catch (Exception ex)
			{
			return (null);
			}
	}

	private static SecretKey getKey()
	{
		if (IoGPersistenceManager.getSharedManager().checkForValue(IoGConfigurationManager.symmetricKeyIdentifier, IoGPersistenceManager.PersistenceSource.SharedPreferences))
			{
			HashMap<String, Object> response = IoGPersistenceManager.getSharedManager().readValue(IoGConfigurationManager.symmetricKeyIdentifier, IoGPersistenceManager.PersistenceSource.SharedPreferences);
			IoGPersistenceManager.PersistenceReadResultCode result = (IoGPersistenceManager.PersistenceReadResultCode) response.get(IoGConfigurationManager.persistenceReadResultCode);
			if (result == IoGPersistenceManager.PersistenceReadResultCode.Success)
				{
				try
					{
					String encodeKeyString = (String) response.get(IoGConfigurationManager.persistenceReadResultValue);
					byte[] encodedKey = Base64.getDecoder().decode(encodeKeyString);
					SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, IoGConfigurationManager.ALGORITHM_AES);
					return (key);
					}
				catch (Exception ex)
					{
					return (createSymmetricKey());
					}
				}
			else
				{
				return (createSymmetricKey());
				}
			}
		else
			{
			return (createSymmetricKey());
			}
	}

	public String encryptAndEncodeString(String string)
	{
		try
			{
			Cipher cipher = Cipher.getInstance(IoGConfigurationManager.aesEncryptionParameters);
			IvParameterSpec iv = new IvParameterSpec(IoGConfigurationManager.aesEncryptionIV.getBytes(StandardCharsets.UTF_8));
			cipher.init(Cipher.ENCRYPT_MODE, EncryptionKeyManager.getKey(), iv);
			byte[] encryptedData = cipher.doFinal(string.getBytes(StandardCharsets.UTF_8));
			String encodedEncryptedData = Base64.getEncoder().encodeToString(encryptedData);
			return (encodedEncryptedData);
			}
		catch (Exception ex)
			{
			return (null);
			}
	}

	public String encryptAndEncodeString(String string, SecretKey key)
	{
		try
			{
			Cipher cipher = Cipher.getInstance(IoGConfigurationManager.aesEncryptionParameters);
			IvParameterSpec iv = new IvParameterSpec(IoGConfigurationManager.aesEncryptionIV.getBytes(StandardCharsets.UTF_8));
			cipher.init(Cipher.ENCRYPT_MODE, key, iv);
			byte[] encryptedData = cipher.doFinal(string.getBytes(StandardCharsets.UTF_8));
			String encodedEncryptedData = Base64.getEncoder().encodeToString(encryptedData);
			return (encodedEncryptedData);
			}
		catch (Exception ex)
			{
			return (null);
			}
	}

	public String decodeAndDecryptString(String encodedString)
	{
		try
			{
			Cipher cipher = Cipher.getInstance(IoGConfigurationManager.aesEncryptionParameters);
			IvParameterSpec iv = new IvParameterSpec(IoGConfigurationManager.aesEncryptionIV.getBytes(StandardCharsets.UTF_8));
			cipher.init(Cipher.DECRYPT_MODE, EncryptionKeyManager.getKey(), iv);
			byte[] encryptedData = Base64.getDecoder().decode(encodedString);
			byte[] decryptedStringData = cipher.doFinal(encryptedData);
			String decryptedString = new String(decryptedStringData);
			return (decryptedString);
			}
		catch (Exception ex)
			{
			return (null);
			}
	}

	public String decodeAndDecryptString(String encodedString, SecretKey key)
	{
		try
			{
			Cipher cipher = Cipher.getInstance(IoGConfigurationManager.aesEncryptionParameters);
			IvParameterSpec iv = new IvParameterSpec(IoGConfigurationManager.aesEncryptionIV.getBytes(StandardCharsets.UTF_8));
			cipher.init(Cipher.DECRYPT_MODE, key, iv);
			byte[] encryptedData = Base64.getDecoder().decode(encodedString);
			byte[] decryptedStringData = cipher.doFinal(encryptedData);
			String decryptedString = new String(decryptedStringData);
			return (decryptedString);
			}
		catch (Exception ex)
			{
			return (null);
			}
	}
}
