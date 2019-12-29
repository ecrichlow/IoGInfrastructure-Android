package com.infusionsofgrandeur.ioginfrastructure.Managers.DataParsing;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class IoGDataObjectManager
{

	private static IoGDataObjectManager singletonInstance = null;

	public static IoGDataObjectManager getSharedManager()
	{
		if (singletonInstance == null)
			{
			singletonInstance = new IoGDataObjectManager();
			}
		return (singletonInstance);
	}

	public <T> T parseObject(String objectString, Class<T> classType)
	{
		IoGDataObject object;
		try
			{
			object = (IoGDataObject)classType.newInstance();
			object.parseSourceData(objectString);
			return ((T)object);
			}
		catch (Exception ex)
			{
			return (null);
			}
	}

	public ArrayList<? extends IoGDataObject> parseArray(String arrayString, Class<?> classType)
	{
		ArrayList objectList = new ArrayList<IoGDataObject>();
		try
			{
			JSONArray array = new JSONArray(arrayString);
			for (int index=0;index<array.length();index++)
				{
				String nextObject = array.getString(index);
				IoGDataObject newObject = (IoGDataObject)classType.newInstance();
				newObject.parseSourceData(nextObject);
				objectList.add(newObject);
				}
			}
		catch (Exception ex)
			{
			}

		return (objectList);
	}
}
