package com.infusionsofgrandeur.ioginfrastructure.Managers.DataParsing;

import com.google.gson.Gson;

import java.util.HashMap;

public class IoGDataObject
{

	protected String sourceData;
	protected HashMap<String, Object> objectDictionary = new HashMap<String, Object>();

	public IoGDataObject()
	{
	}

	public void parseSourceData(String source)
	{
		sourceData = source;

		Gson gson = new Gson();
		objectDictionary = gson.fromJson(source, HashMap.class);
	}

	public Object getValue(String key)
	{
		if (objectDictionary.containsKey(key))
			{
			return (objectDictionary.get(key));
			}
		else
			{
			return (new String());
			}
	}
}
