package com.infusionsofgrandeur.ioginfrastructure.Managers.Retry;

import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Timer;
import java.util.Date;

import com.infusionsofgrandeur.ioginfrastructure.Managers.Configuration.IoGConfigurationManager;
import com.infusionsofgrandeur.ioginfrastructure.Managers.Retry.IoGRetryManager.IoGRetryManagerCallbackReceiver;
import com.infusionsofgrandeur.ioginfrastructure.Managers.Retry.IoGRetryManager.RetryRoutine;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class IoGRetryManagerTest implements IoGRetryManagerCallbackReceiver
{

    public IoGConfigurationManager configurartionManager;
    int retryIdentifier;
    int decoyRetryIdentifier;
    boolean retrySucceeded;
    boolean callbackInvoked;
    Timer callbackTimer;
    IoGRetryManager.RetryResult retryResult;
    int retryCount = 0;
    int decoyRetryCount = 0;

    @Before
    public void setUp()
    {
        configurartionManager = IoGConfigurationManager.getSharedManager();
        configurartionManager.setApplicationContext(InstrumentationRegistry.getContext());
        retryIdentifier = 0;
        decoyRetryIdentifier = 0;
        retrySucceeded = false;
        callbackInvoked = false;
        callbackTimer = null;
        retryResult = null;
        retryCount = 0;
        decoyRetryCount = 0;
        IoGRetryManager.getSharedManager().registerCallbackListener(this);
    }

    @After
    public void tearDown()
    {
        if (callbackTimer != null)
            {
            callbackTimer.cancel();
            }
        IoGRetryManager.getSharedManager().unregisterCallbackListener(this);
    }

    @Test
    public void testExpiringRetrySuccess()
    {
        Date currentTime = new Date();
        Date expiration = new Date(currentTime.getTime() + com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.retryTestExpiration);
        Date preExpiration = new Date(currentTime.getTime() + com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.preExpiration);
        RetryRoutine retryRoutine = (disposition) ->
            {
            if (new Date().before(preExpiration))
                {
                disposition.dispositionAttempt(retryIdentifier, IoGRetryManager.Disposition.Failure);
                }
            else
                {
                disposition.dispositionAttempt(retryIdentifier, IoGRetryManager.Disposition.Success);
                }
            };
        RetryRoutine decoyRetryRoutine = (disposition) ->
            {
            if (decoyRetryCount < com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyRetrySuccessIteration)
                {
                disposition.dispositionAttempt(decoyRetryIdentifier, IoGRetryManager.Disposition.Failure);
                }
            else if (decoyRetryCount == com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyRetrySuccessIteration)
                {
                disposition.dispositionAttempt(decoyRetryIdentifier, IoGRetryManager.Disposition.Success);
                }
            else
                {
                Assert.fail();
                }
            };
        try
            {
            retryIdentifier = IoGRetryManager.getSharedManager().startRetries(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.retryDelay, IoGRetryManager.RetryLifespan.ExpirationLimited, 0, 0, expiration, retryRoutine);
            decoyRetryIdentifier = IoGRetryManager.getSharedManager().startRetries(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyRetryDelay, IoGRetryManager.RetryLifespan.CountLimited, com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyMaxCount, 0, null, decoyRetryRoutine);
            assertEquals(decoyRetryIdentifier, retryIdentifier + 1);
            Thread.sleep(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.retryTestExpirationCheckTimeout);
            assertTrue(callbackInvoked);
            assertTrue(retrySucceeded);
            }
        catch (Exception ex)
            {
            Assert.fail();
            }
    }

    @Test
    public void testExpiringRetryFail()
    {
        Date currentTime = new Date();
        Date expiration = new Date(currentTime.getTime() + com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.retryTestExpiration);
        RetryRoutine retryRoutine = (disposition) ->
            {
            disposition.dispositionAttempt(retryIdentifier, IoGRetryManager.Disposition.Failure);
            };
        RetryRoutine decoyRetryRoutine = (disposition) ->
            {
            if (decoyRetryCount < com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyRetrySuccessIteration)
                {
                disposition.dispositionAttempt(decoyRetryIdentifier, IoGRetryManager.Disposition.Failure);
                }
            else if (decoyRetryCount == com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyRetrySuccessIteration)
                {
                disposition.dispositionAttempt(decoyRetryIdentifier, IoGRetryManager.Disposition.Success);
                }
            else
                {
                Assert.fail();
                }
            };
        try
            {
            retryIdentifier = IoGRetryManager.getSharedManager().startRetries(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.retryDelay, IoGRetryManager.RetryLifespan.ExpirationLimited, 0, 0, expiration, retryRoutine);
            decoyRetryIdentifier = IoGRetryManager.getSharedManager().startRetries(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyRetryDelay, IoGRetryManager.RetryLifespan.CountLimited, com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyMaxCount, 0, null, decoyRetryRoutine);
            assertEquals(decoyRetryIdentifier, retryIdentifier + 1);
            Thread.sleep(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.retryTestExpirationCheckTimeout);
            assertTrue(callbackInvoked);
            assertFalse(retrySucceeded);
            }
        catch (Exception ex)
            {
            Assert.fail();
            }
    }

    @Test
    public void testTimeLimitedRetrySuccess()
    {
        Date currentTime = new Date();
        Date preExpiration = new Date(currentTime.getTime() + com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.preExpiration);
        RetryRoutine retryRoutine = (disposition) ->
            {
            if (new Date().before(preExpiration))
                {
                disposition.dispositionAttempt(retryIdentifier, IoGRetryManager.Disposition.Failure);
                }
            else
                {
                disposition.dispositionAttempt(retryIdentifier, IoGRetryManager.Disposition.Success);
                }
            };
        RetryRoutine decoyRetryRoutine = (disposition) ->
            {
            if (decoyRetryCount < com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyRetrySuccessIteration)
                {
                disposition.dispositionAttempt(decoyRetryIdentifier, IoGRetryManager.Disposition.Failure);
                }
            else if (decoyRetryCount == com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyRetrySuccessIteration)
                {
                disposition.dispositionAttempt(decoyRetryIdentifier, IoGRetryManager.Disposition.Success);
                }
            else
                {
                Assert.fail();
                }
            };
        try
            {
            retryIdentifier = IoGRetryManager.getSharedManager().startRetries(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.retryDelay, IoGRetryManager.RetryLifespan.TimeLimited, 0, com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.retryTestExpiration, null, retryRoutine);
            decoyRetryIdentifier = IoGRetryManager.getSharedManager().startRetries(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyRetryDelay, IoGRetryManager.RetryLifespan.CountLimited, com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyMaxCount, 0, null, decoyRetryRoutine);
            assertEquals(decoyRetryIdentifier, retryIdentifier + 1);
            Thread.sleep(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.retryTestExpirationCheckTimeout);
            assertTrue(callbackInvoked);
            assertTrue(retrySucceeded);
            }
        catch (Exception ex)
            {
            Assert.fail();
            }
    }

    @Test
    public void testTimeLimitedRetryFail()
    {
        RetryRoutine retryRoutine = (disposition) ->
            {
            disposition.dispositionAttempt(retryIdentifier, IoGRetryManager.Disposition.Failure);
            };
        RetryRoutine decoyRetryRoutine = (disposition) ->
            {
            if (decoyRetryCount < com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyRetrySuccessIteration)
                {
                disposition.dispositionAttempt(decoyRetryIdentifier, IoGRetryManager.Disposition.Failure);
                }
            else if (decoyRetryCount == com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyRetrySuccessIteration)
                {
                disposition.dispositionAttempt(decoyRetryIdentifier, IoGRetryManager.Disposition.Success);
                }
            else
                {
                Assert.fail();
                }
            };
        try
            {
            retryIdentifier = IoGRetryManager.getSharedManager().startRetries(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.retryDelay, IoGRetryManager.RetryLifespan.TimeLimited, 0, com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.retryTestExpiration, null, retryRoutine);
            decoyRetryIdentifier = IoGRetryManager.getSharedManager().startRetries(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyRetryDelay, IoGRetryManager.RetryLifespan.CountLimited, com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyMaxCount, 0, null, decoyRetryRoutine);
            assertEquals(decoyRetryIdentifier, retryIdentifier + 1);
            Thread.sleep(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.retryTestExpirationCheckTimeout);
            assertTrue(callbackInvoked);
            assertFalse(retrySucceeded);
            assertEquals(retryResult, IoGRetryManager.RetryResult.TimeLimitExceeded);
            }
        catch (Exception ex)
            {
            Assert.fail();
            }
    }

    @Test
    public void testCountLimitedRetrySuccess()
    {
        RetryRoutine retryRoutine = (disposition) ->
            {
            if (retryCount < com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.retrySuccessIteration)
                {
                disposition.dispositionAttempt(retryIdentifier, IoGRetryManager.Disposition.Failure);
                }
            else
                {
                disposition.dispositionAttempt(retryIdentifier, IoGRetryManager.Disposition.Success);
                }
            retryCount++;
            };
        RetryRoutine decoyRetryRoutine = (disposition) ->
            {
            if (decoyRetryCount < com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyRetrySuccessIteration)
                {
                disposition.dispositionAttempt(decoyRetryIdentifier, IoGRetryManager.Disposition.Failure);
                }
            else if (decoyRetryCount == com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyRetrySuccessIteration)
                {
                disposition.dispositionAttempt(decoyRetryIdentifier, IoGRetryManager.Disposition.Success);
                }
            else
                {
                Assert.fail();
                }
            decoyRetryCount++;
            };
        try
            {
            retryIdentifier = IoGRetryManager.getSharedManager().startRetries(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.retryDelay, IoGRetryManager.RetryLifespan.TimeLimited, 0, com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.retryTestExpiration, null, retryRoutine);
            decoyRetryIdentifier = IoGRetryManager.getSharedManager().startRetries(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyRetryDelay, IoGRetryManager.RetryLifespan.CountLimited, com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyMaxCount, 0, null, decoyRetryRoutine);
            assertEquals(decoyRetryIdentifier, retryIdentifier + 1);
            Thread.sleep(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.retryTestExpirationCheckTimeout);
            assertTrue(callbackInvoked);
            assertTrue(retrySucceeded);
            }
        catch (Exception ex)
            {
            Assert.fail();
            }
    }

    @Test
    public void testCountLimitedRetryFail()
    {
        RetryRoutine retryRoutine = (disposition) ->
            {
            disposition.dispositionAttempt(retryIdentifier, IoGRetryManager.Disposition.Failure);
            };
        RetryRoutine decoyRetryRoutine = (disposition) ->
            {
            if (decoyRetryCount < com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyRetrySuccessIteration)
                {
                disposition.dispositionAttempt(decoyRetryIdentifier, IoGRetryManager.Disposition.Failure);
                }
            else if (decoyRetryCount == com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyRetrySuccessIteration)
                {
                disposition.dispositionAttempt(decoyRetryIdentifier, IoGRetryManager.Disposition.Success);
                }
            else
                {
                Assert.fail();
                }
            decoyRetryCount++;
            };
        try
            {
            retryIdentifier = IoGRetryManager.getSharedManager().startRetries(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.retryDelay, IoGRetryManager.RetryLifespan.CountLimited, com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.maxCount, 0, null, retryRoutine);
            decoyRetryIdentifier = IoGRetryManager.getSharedManager().startRetries(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyRetryDelay, IoGRetryManager.RetryLifespan.CountLimited, com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyMaxCount, 0, null, decoyRetryRoutine);
            assertEquals(decoyRetryIdentifier, retryIdentifier + 1);
            Thread.sleep(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.retryTestExpirationCheckTimeout);
            assertTrue(callbackInvoked);
            assertFalse(retrySucceeded);
            assertEquals(retryResult, IoGRetryManager.RetryResult.CountExceeded);
            }
        catch (Exception ex)
            {
            Assert.fail();
            }
    }

    @Test
    public void testInfiniteRetrySuccess()
    {
        RetryRoutine retryRoutine = (disposition) ->
            {
            if (retryCount < com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.infiniteRetrySuccessIteration)
                {
                disposition.dispositionAttempt(retryIdentifier, IoGRetryManager.Disposition.Failure);
                }
            else
                {
                disposition.dispositionAttempt(retryIdentifier, IoGRetryManager.Disposition.Success);
                }
            retryCount++;
            };
        RetryRoutine decoyRetryRoutine = (disposition) ->
            {
            if (decoyRetryCount < com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyRetrySuccessIteration)
                {
                disposition.dispositionAttempt(decoyRetryIdentifier, IoGRetryManager.Disposition.Failure);
                }
            else if (decoyRetryCount == com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyRetrySuccessIteration)
                {
                disposition.dispositionAttempt(decoyRetryIdentifier, IoGRetryManager.Disposition.Success);
                }
            else
                {
                Assert.fail();
                }
            decoyRetryCount++;
            };
        try
            {
            retryIdentifier = IoGRetryManager.getSharedManager().startRetries(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.retryDelay, IoGRetryManager.RetryLifespan.Infinite, 0, 0, null, retryRoutine);
            decoyRetryIdentifier = IoGRetryManager.getSharedManager().startRetries(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyRetryDelay, IoGRetryManager.RetryLifespan.CountLimited, com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyMaxCount, 0, null, decoyRetryRoutine);
            assertEquals(decoyRetryIdentifier, retryIdentifier + 1);
            Thread.sleep(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.infiniteRetryTestExpirationCheckTimeout);
            assertTrue(callbackInvoked);
            assertTrue(retrySucceeded);
            }
        catch (Exception ex)
            {
            Assert.fail();
            }
    }

    @Test
    public void testExpirationNoExpirationDateFailure()
    {
        RetryRoutine retryRoutine = (disposition) -> {};
        retryIdentifier = IoGRetryManager.getSharedManager().startRetries(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.retryDelay, IoGRetryManager.RetryLifespan.ExpirationLimited, 0, 0, null, retryRoutine);
        assertEquals(retryIdentifier, -1);
    }

    @Test
    public void testTimeLimitNoTimespanFailure()
    {
        RetryRoutine retryRoutine = (disposition) -> {};
        retryIdentifier = IoGRetryManager.getSharedManager().startRetries(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.retryDelay, IoGRetryManager.RetryLifespan.TimeLimited, 0, 0, null, retryRoutine);
        assertEquals(retryIdentifier, -1);
    }

    @Test
    public void testCountLimitedNoMaxCountFailure()
    {
        RetryRoutine retryRoutine = (disposition) -> {};
        retryIdentifier = IoGRetryManager.getSharedManager().startRetries(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.retryDelay, IoGRetryManager.RetryLifespan.CountLimited, 0, 0, null, retryRoutine);
        assertEquals(retryIdentifier, -1);
    }

    @Test
    public void testCancelRetries()
    {
        RetryRoutine retryRoutine = (disposition) ->
            {
            if (retryCount < com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.retrySuccessIteration)
                {
                disposition.dispositionAttempt(retryIdentifier, IoGRetryManager.Disposition.Failure);
                }
            else
                {
                disposition.dispositionAttempt(retryIdentifier, IoGRetryManager.Disposition.Success);
                }
            retryCount++;
            };
        RetryRoutine decoyRetryRoutine = (disposition) ->
            {
            if (decoyRetryCount < com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyRetrySuccessIteration)
                {
                disposition.dispositionAttempt(decoyRetryIdentifier, IoGRetryManager.Disposition.Failure);
                }
            else if (decoyRetryCount == com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyRetrySuccessIteration)
                {
                disposition.dispositionAttempt(decoyRetryIdentifier, IoGRetryManager.Disposition.Success);
                }
            else
                {
                Assert.fail();
                }
            decoyRetryCount++;
            };
        try
            {
            retryIdentifier = IoGRetryManager.getSharedManager().startRetries(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.retryDelay, IoGRetryManager.RetryLifespan.CountLimited, com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.maxCount, 0, null, retryRoutine);
            decoyRetryIdentifier = IoGRetryManager.getSharedManager().startRetries(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyRetryDelay, IoGRetryManager.RetryLifespan.CountLimited, com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.decoyMaxCount, 0, null, decoyRetryRoutine);
            IoGRetryManager.getSharedManager().cancelRetries(retryIdentifier);
            assertEquals(decoyRetryIdentifier, retryIdentifier + 1);
            Thread.sleep(com.infusionsofgrandeur.ioginfrastructure.IoGTestConfigurationManager.retryTestExpirationCheckTimeout);
            assertFalse(callbackInvoked);
            assertFalse(retrySucceeded);
            }
        catch (Exception ex)
            {
            Assert.fail();
            }
    }

    public void retrySessionCompleted(int requestID, IoGRetryManager.RetryResult result)
    {
        if (requestID == retryIdentifier)
            {
            callbackInvoked = true;
            retryResult = result;
            if (result == IoGRetryManager.RetryResult.Success)
                {
                retrySucceeded = true;
                }
            else
                {
                retrySucceeded = false;
                }
            }
    }
}
