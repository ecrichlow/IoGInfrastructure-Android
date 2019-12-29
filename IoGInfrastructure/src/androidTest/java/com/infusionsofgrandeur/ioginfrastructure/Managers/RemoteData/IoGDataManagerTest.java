package com.infusionsofgrandeur.ioginfrastructure.Managers.RemoteData;

import android.support.test.InstrumentationRegistry;

import com.infusionsofgrandeur.ioginfrastructure.Managers.Configuration.IoGConfigurationManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import static org.junit.Assert.*;

public class IoGDataManagerTest
{

    public IoGConfigurationManager configurartionManager;
    public boolean callbackInvoked;
    public IoGDataRequestResponse callbackResponse;
    public byte[] returnedData;

    @Before
    public void setUp() throws Exception
    {
        configurartionManager = IoGConfigurationManager.getSharedManager();
        configurartionManager.setApplicationContext(InstrumentationRegistry.getContext());
        callbackInvoked = false;
        callbackResponse = null;
        returnedData = null;
        IoGDataManager.dataManagerOfType(IoGDataManager.IoGDataManagerType.IoGDataManagerTypeMock).registerCallbackListener(callbackReceiver);
    }

    @After
    public void tearDown() throws Exception
    {
        IoGDataManager.dataManagerOfType(IoGDataManager.IoGDataManagerType.IoGDataManagerTypeMock).unregisterCallbackListener(callbackReceiver);
        configurartionManager = null;
    }

    @Test
    public void testSuccessfulFastDataRetrieval()
    {
        try
            {
            URL mockURL = new URL(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.successURL1);
            HttpURLConnection connection = new HttpURLConnection(mockURL)
                {
                @Override
                public void disconnect()
                    {
                    }

                @Override
                public boolean usingProxy()
                    {
                    return false;
                    }

                @Override
                public void connect() throws IOException
                    {
                    }
                };
            IoGDataManager.dataManagerOfType(IoGDataManager.IoGDataManagerType.IoGDataManagerTypeMock).transmitRequest(connection, null, IoGDataManager.IoGDataRequestType.Login);
            Thread.sleep(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.dataRequestFastResponseCheck);
            assertTrue(callbackInvoked);
            assertNotNull(callbackResponse);
            assertNotNull(returnedData);
            String returnedString = new String(returnedData);
            assertEquals(returnedString, IoGConfigurationManager.mockDataResponse1);
            }
        catch (Exception ex)
            {
            Assert.fail();
            }
    }

    @Test
    public void testFailedFastDataRetrieval()
    {
        try
            {
            URL mockURL = new URL(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.failureURL1);
            HttpURLConnection connection = new HttpURLConnection(mockURL)
                {
                @Override
                public void disconnect()
                    {
                    }

                @Override
                public boolean usingProxy()
                    {
                    return false;
                    }

                @Override
                public void connect() throws IOException
                    {
                    }
                };
            IoGDataManager.dataManagerOfType(IoGDataManager.IoGDataManagerType.IoGDataManagerTypeMock).transmitRequest(connection, null, IoGDataManager.IoGDataRequestType.Login);
            Thread.sleep(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.dataRequestFastResponseCheck);
            assertTrue(callbackInvoked);
            assertNotNull(callbackResponse);
            assertNull(returnedData);
            HashMap<String, Object> responseInfo = callbackResponse.getResponseInfo();
            String errorString = (String)responseInfo.get(IoGConfigurationManager.requestResponseKeyError);
            assertEquals(errorString, IoGConfigurationManager.requestResponseGeneralErrorDescription);
            }
        catch (Exception ex)
            {
            Assert.fail();
            }
    }

    @Test
    public void testSuccessfulSlowDataRetrieval()
    {
        try
            {
            URL mockURL = new URL(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.successURL1Slow);
            HttpURLConnection connection = new HttpURLConnection(mockURL)
                {
                @Override
                public void disconnect()
                    {
                    }

                @Override
                public boolean usingProxy()
                    {
                    return false;
                    }

                @Override
                public void connect() throws IOException
                    {
                    }
                };
            IoGDataManager.dataManagerOfType(IoGDataManager.IoGDataManagerType.IoGDataManagerTypeMock).transmitRequest(connection, null, IoGDataManager.IoGDataRequestType.Login);
            Thread.sleep(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.dataRequestSlowResponseCheck);
            assertTrue(callbackInvoked);
            assertNotNull(callbackResponse);
            assertNotNull(returnedData);
            String returnedString = new String(returnedData);
            assertEquals(returnedString, IoGConfigurationManager.mockDataResponse1);
            }
        catch (Exception ex)
            {
            Assert.fail();
            }
    }

    @Test
    public void testFailedSlowDataRetrieval()
    {
        try
        {
            URL mockURL = new URL(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.failureURLSlow);
            HttpURLConnection connection = new HttpURLConnection(mockURL)
                {
                @Override
                public void disconnect()
                    {
                    }

                @Override
                public boolean usingProxy()
                    {
                    return false;
                    }

                @Override
                public void connect() throws IOException
                    {
                    }
                };
            IoGDataManager.dataManagerOfType(IoGDataManager.IoGDataManagerType.IoGDataManagerTypeMock).transmitRequest(connection, null, IoGDataManager.IoGDataRequestType.Login);
            Thread.sleep(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.dataRequestSlowResponseCheck);
            assertTrue(callbackInvoked);
            assertNotNull(callbackResponse);
            assertNull(returnedData);
            HashMap<String, Object> responseInfo = callbackResponse.getResponseInfo();
            String errorString = (String)responseInfo.get(IoGConfigurationManager.requestResponseKeyError);
            assertEquals(errorString, IoGConfigurationManager.requestResponseGeneralErrorDescription);
            }
        catch (Exception ex)
            {
            Assert.fail();
            }
    }

    @Test
    public void testSuccessfulMultiPageDataRetrieval()
    {
        try
            {
            URL mockURL = new URL(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.successURL1);
            HttpURLConnection connection = new HttpURLConnection(mockURL)
                {
                @Override
                public void disconnect()
                    {
                    }

                @Override
                public boolean usingProxy()
                    {
                    return false;
                    }

                @Override
                public void connect() throws IOException
                    {
                    }
                };
            IoGDataManager.dataManagerOfType(IoGDataManager.IoGDataManagerType.IoGDataManagerTypeMock).transmitRequest(connection, null, IoGDataManager.IoGDataRequestType.Login);
            Thread.sleep(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.dataRequestFastResponseCheck);
            assertTrue(callbackInvoked);
            assertNotNull(callbackResponse);
            assertNotNull(returnedData);
            String returnedString = new String(returnedData);
            assertEquals(returnedString, IoGConfigurationManager.mockDataResponse1);
            HashMap<String, Object> requestInfo = callbackResponse.getRequestInfo();
            callbackInvoked = false;
            returnedData = null;
            mockURL = new URL(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.successURL2);
            connection = new HttpURLConnection(mockURL)
                {
                @Override
                public void disconnect()
                    {
                    }

                @Override
                public boolean usingProxy()
                    {
                    return false;
                    }

                @Override
                public void connect() throws IOException
                    {
                    }
                };
            requestInfo.put(IoGConfigurationManager.requestResponseKeyRequest, connection);
            IoGDataManager.dataManagerOfType(IoGDataManager.IoGDataManagerType.IoGDataManagerTypeMock).continueMultiPartRequest(callbackResponse);
            Thread.sleep(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.dataRequestFastResponseCheck);
            assertTrue(callbackInvoked);
            assertNotNull(callbackResponse);
            assertNotNull(returnedData);
            returnedString = new String(returnedData);
            assertEquals(returnedString, IoGConfigurationManager.mockDataResponse2);
            }
        catch (Exception ex)
            {
            Assert.fail();
            }
    }

    @Test
    public void testFailedMultiPageDataRetrieval()
    {
        try
            {
            URL mockURL = new URL(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.successURL1);
            HttpURLConnection connection = new HttpURLConnection(mockURL)
                {
                @Override
                public void disconnect()
                    {
                    }

                @Override
                public boolean usingProxy()
                    {
                    return false;
                    }

                @Override
                public void connect() throws IOException
                    {
                    }
                };
            IoGDataManager.dataManagerOfType(IoGDataManager.IoGDataManagerType.IoGDataManagerTypeMock).transmitRequest(connection, null, IoGDataManager.IoGDataRequestType.Login);
            Thread.sleep(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.dataRequestFastResponseCheck);
            assertTrue(callbackInvoked);
            assertNotNull(callbackResponse);
            assertNotNull(returnedData);
            String returnedString = new String(returnedData);
            assertEquals(returnedString, IoGConfigurationManager.mockDataResponse1);
            HashMap<String, Object> requestInfo = callbackResponse.getRequestInfo();
            callbackInvoked = false;
            returnedData = null;
            mockURL = new URL(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.failureURL1);
            connection = new HttpURLConnection(mockURL)
                {
                @Override
                public void disconnect()
                    {
                    }

                @Override
                public boolean usingProxy()
                    {
                    return false;
                    }

                @Override
                public void connect() throws IOException
                    {
                    }
                };
            requestInfo.put(IoGConfigurationManager.requestResponseKeyRequest, connection);
            IoGDataManager.dataManagerOfType(IoGDataManager.IoGDataManagerType.IoGDataManagerTypeMock).continueMultiPartRequest(callbackResponse);
            Thread.sleep(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.dataRequestFastResponseCheck);
            assertTrue(callbackInvoked);
            assertNotNull(callbackResponse);
            assertNull(returnedData);
            HashMap<String, Object> responseInfo = callbackResponse.getResponseInfo();
            String errorString = (String)responseInfo.get(IoGConfigurationManager.requestResponseKeyError);
            assertEquals(errorString, IoGConfigurationManager.requestResponseGeneralErrorDescription);
            }
        catch (Exception ex)
            {
            Assert.fail();
            }
    }

    IoGDataManager.IoGDataManagerCallbackReceiver callbackReceiver = new IoGDataManager.IoGDataManagerCallbackReceiver()
    {
        public void dataRequestResponseReceived(int requestID, IoGDataManager.IoGDataRequestType requestType, byte[] responseData, String error, IoGDataRequestResponse response)
        {
            callbackInvoked = true;
            callbackResponse = response;
            returnedData = responseData;
        }
    };
}