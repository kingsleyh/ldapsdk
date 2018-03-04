/*
 * Copyright 2018 Ping Identity Corporation
 * All Rights Reserved.
 */
/*
 * Copyright (C) 2018 Ping Identity Corporation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (GPLv2 only)
 * or the terms of the GNU Lesser General Public License (LGPLv2.1 only)
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 */
package com.unboundid.util;



import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.testng.annotations.Test;

import com.unboundid.ldap.sdk.LDAPSDKTestCase;



/**
 * This class provides a set of test cases for the {@code CloseableLock} class.
 */
public final class CloseableLockTestCase
       extends LDAPSDKTestCase
{
  /**
   * Tests basic lock functionality.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testLock()
         throws Exception
  {
    final CloseableLock lock = new CloseableLock();

    assertFalse(lock.isFair());
    assertFalse(lock.isLocked());
    assertFalse(lock.isHeldByCurrentThread());
    assertEquals(lock.getHoldCount(), 0);
    assertFalse(lock.hasQueuedThreads());
    assertFalse(lock.hasQueuedThread(Thread.currentThread()));
    assertEquals(lock.getQueueLength(), 0);
    assertNotNull(lock.toString());

    try (final CloseableLock.Lock l1 = lock.lock())
    {
      assertNotNull(l1);

      assertFalse(lock.isFair());
      assertTrue(lock.isLocked());
      assertTrue(lock.isHeldByCurrentThread());
      assertEquals(lock.getHoldCount(), 1);
      assertFalse(lock.hasQueuedThreads());
      assertFalse(lock.hasQueuedThread(Thread.currentThread()));
      assertEquals(lock.getQueueLength(), 0);
      assertNotNull(lock.toString());

      try (final CloseableLock.Lock l2 = lock.lockInterruptibly())
      {
        assertNotNull(l2);

        assertFalse(lock.isFair());
        assertTrue(lock.isLocked());
        assertTrue(lock.isHeldByCurrentThread());
        assertEquals(lock.getHoldCount(), 2);
        assertFalse(lock.hasQueuedThreads());
        assertFalse(lock.hasQueuedThread(Thread.currentThread()));
        assertEquals(lock.getQueueLength(), 0);
        assertNotNull(lock.toString());

        try (final CloseableLock.Lock l3 = lock.tryLock(1L, TimeUnit.SECONDS))
        {
          assertNotNull(l3);

          assertFalse(lock.isFair());
          assertTrue(lock.isLocked());
          assertTrue(lock.isHeldByCurrentThread());
          assertEquals(lock.getHoldCount(), 3);
          assertFalse(lock.hasQueuedThreads());
          assertFalse(lock.hasQueuedThread(Thread.currentThread()));
          assertEquals(lock.getQueueLength(), 0);
          assertNotNull(lock.toString());

          try (final CloseableLock.Lock l4 = lock.tryLock(0L, TimeUnit.SECONDS))
          {
            fail("Expected an exception when trying to acquire the lock with " +
                 "a timeout of zero seconds.  Instead, got lock " + l4);
          }
          catch (final LDAPSDKUsageException e)
          {
            // This was expected.
          }

          assertFalse(lock.isFair());
          assertTrue(lock.isLocked());
          assertTrue(lock.isHeldByCurrentThread());
          assertEquals(lock.getHoldCount(), 3);
          assertFalse(lock.hasQueuedThreads());
          assertFalse(lock.hasQueuedThread(Thread.currentThread()));
          assertEquals(lock.getQueueLength(), 0);
          assertNotNull(lock.toString());

          assertNotNull(l3);
        }

        assertFalse(lock.isFair());
        assertTrue(lock.isLocked());
        assertTrue(lock.isHeldByCurrentThread());
        assertEquals(lock.getHoldCount(), 2);
        assertFalse(lock.hasQueuedThreads());
        assertFalse(lock.hasQueuedThread(Thread.currentThread()));
        assertEquals(lock.getQueueLength(), 0);
        assertNotNull(lock.toString());

        assertNotNull(l2);
      }

      assertFalse(lock.isFair());
      assertTrue(lock.isLocked());
      assertTrue(lock.isHeldByCurrentThread());
      assertEquals(lock.getHoldCount(), 1);
      assertFalse(lock.hasQueuedThreads());
      assertFalse(lock.hasQueuedThread(Thread.currentThread()));
      assertEquals(lock.getQueueLength(), 0);
      assertNotNull(lock.toString());

      assertNotNull(l1);
    }

    assertFalse(lock.isFair());
    assertFalse(lock.isLocked());
    assertFalse(lock.isHeldByCurrentThread());
    assertEquals(lock.getHoldCount(), 0);
    assertFalse(lock.hasQueuedThreads());
    assertFalse(lock.hasQueuedThread(Thread.currentThread()));
    assertEquals(lock.getQueueLength(), 0);
    assertNotNull(lock.toString());
  }



  /**
   * Tests the behavior when trying to acquire the lock while it is held by
   * another thread.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testLockThatIsAlreadyHeld()
         throws Exception
  {
    final CloseableLock lock = new CloseableLock(true);
    final AtomicBoolean isLocked = new AtomicBoolean(false);
    final AtomicBoolean tryLockFailed = new AtomicBoolean(false);

    assertTrue(lock.isFair());
    assertFalse(lock.isLocked());
    assertFalse(lock.isHeldByCurrentThread());
    assertEquals(lock.getHoldCount(), 0);
    assertFalse(lock.hasQueuedThreads());
    assertFalse(lock.hasQueuedThread(Thread.currentThread()));
    assertEquals(lock.getQueueLength(), 0);
    assertNotNull(lock.toString());

    final CloseableLockTestCaseThread thread =
         new CloseableLockTestCaseThread(lock, isLocked, tryLockFailed);
    thread.start();

    while (! isLocked.get())
    {
      Thread.sleep(1L);
    }

    assertTrue(lock.isFair());
    assertTrue(lock.isLocked());
    assertFalse(lock.isHeldByCurrentThread());
    assertEquals(lock.getHoldCount(), 0); // Not held by current thread.
    assertFalse(lock.hasQueuedThreads());
    assertFalse(lock.hasQueuedThread(Thread.currentThread()));
    assertEquals(lock.getQueueLength(), 0);
    assertNotNull(lock.toString());

    try (CloseableLock.Lock l = lock.tryLock(1L, TimeUnit.MILLISECONDS))
    {
      fail("Expected to fail to acquire the lock held by another thread, but " +
           "instead got " + l);
    }
    catch (final TimeoutException e)
    {
      // This was expected.
      assertNotNull(e.getMessage());
    }

    tryLockFailed.set(true);
    thread.join();
  }
}
