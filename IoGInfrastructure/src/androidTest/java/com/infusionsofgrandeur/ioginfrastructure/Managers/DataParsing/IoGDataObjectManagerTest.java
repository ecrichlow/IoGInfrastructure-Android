package com.infusionsofgrandeur.ioginfrastructure.Managers.DataParsing;

import com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager;

import com.google.gson.Gson;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class IoGDataObjectManagerTest
{

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void testParseSingleObject()
    {
        TestComputerObject computer = IoGDataObjectManager.getSharedManager().parseObject(IoGTestConfigurationManager.parsingObjectData, TestComputerObject.class);
        assertNotNull(computer);
        assertEquals(computer.model, "TRS-80 Color Computer 2");
        assertEquals(computer.processor, "6809");
    }

    @Test
    public void testFailedObjectParse()
    {
        TestComputerObject computer = IoGDataObjectManager.getSharedManager().parseObject("", TestComputerObject.class);
        assertNull(computer);
    }

    @Test
    public void testParseObjectArray()
    {
        ArrayList<TestComputerObject> computerArray = (ArrayList<TestComputerObject>)IoGDataObjectManager.getSharedManager().parseArray(IoGTestConfigurationManager.parsingObjectArrayData, TestComputerObject.class);
        assertEquals(computerArray.size(), 3);
        int index = 0;
        for (TestComputerObject nextComputer : computerArray)
            {
            switch (index)
                {
                case 0:
                    assertNotNull(nextComputer);
                    assertEquals(nextComputer.model, "TRS-80 Color Computer 2");
                    assertEquals(nextComputer.processor, "6809");
                    break;
                case 1:
                    assertNotNull(nextComputer);
                    assertEquals(nextComputer.model, "TRS-80 Color Computer 3");
                    assertEquals(nextComputer.processor, "68B09E");
                    break;
                case 2:
                    assertNotNull(nextComputer);
                    assertEquals(nextComputer.model, "MM/1");
                    assertEquals(nextComputer.processor, "68070");
                    break;
                default:
                    break;
                }
            index++;
            }
    }

    @Test
    public void testFailedArrayParse()
    {
        ArrayList<TestComputerObject> computerArray = (ArrayList<TestComputerObject>)IoGDataObjectManager.getSharedManager().parseArray("", TestComputerObject.class);
        assertEquals(computerArray.size(), 0);
    }

    @Test
    public void testRetrieveUnlabeledProperty()
    {
        TestComputerObject computer = IoGDataObjectManager.getSharedManager().parseObject(IoGTestConfigurationManager.parsingObjectData, TestComputerObject.class);
        assertNotNull(computer);
        assertEquals(computer.getValue("year"), "1980");
    }

    public static class TestComputerObject extends IoGDataObject
    {

        public String model;
        public String processor;

        TestComputerObject()
        {
        }

        @Override
        public void parseSourceData(String source)
        {
            sourceData = source;

            Gson gson = new Gson();
            objectDictionary = gson.fromJson(source, HashMap.class);

            // Known fields
            if (objectDictionary.containsKey("model"))
                {
                model = (String)objectDictionary.get("model");
                }
            if (objectDictionary.containsKey("processor"))
                {
                processor = (String)objectDictionary.get("processor");
                }
        }
    }
}