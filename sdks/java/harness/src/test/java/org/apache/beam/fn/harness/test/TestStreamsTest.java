/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.beam.fn.harness.test;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests for {@link TestStreams}. */
@RunWith(JUnit4.class)
public class TestStreamsTest {
  @Test
  public void testOnNextIsCalled() {
    AtomicBoolean onNextWasCalled = new AtomicBoolean();
    TestStreams.withOnNext(onNextWasCalled::set).build().onNext(true);
    assertTrue(onNextWasCalled.get());
  }

  @Test
  public void testIsReadyIsCalled() {
    final AtomicBoolean isReadyWasCalled = new AtomicBoolean();
    assertFalse(TestStreams.withOnNext(null)
        .withIsReady(() -> isReadyWasCalled.getAndSet(true))
        .build()
        .isReady());
    assertTrue(isReadyWasCalled.get());
  }

  @Test
  public void testOnCompletedIsCalled() {
    AtomicBoolean onCompletedWasCalled = new AtomicBoolean();
    TestStreams.withOnNext(null)
        .withOnCompleted(() -> onCompletedWasCalled.set(true))
        .build()
        .onCompleted();
    assertTrue(onCompletedWasCalled.get());
  }

  @Test
  public void testOnErrorRunnableIsCalled() {
    RuntimeException throwable = new RuntimeException();
    AtomicBoolean onErrorWasCalled = new AtomicBoolean();
    TestStreams.withOnNext(null)
        .withOnError(() -> onErrorWasCalled.set(true))
        .build()
        .onError(throwable);
    assertTrue(onErrorWasCalled.get());
  }

  @Test
  public void testOnErrorConsumerIsCalled() {
    RuntimeException throwable = new RuntimeException();
    Collection<Throwable> onErrorWasCalled = new ArrayList<>();
    TestStreams.withOnNext(null)
        .withOnError(onErrorWasCalled::add)
        .build()
        .onError(throwable);
    assertThat(onErrorWasCalled, contains(throwable));
  }
}
