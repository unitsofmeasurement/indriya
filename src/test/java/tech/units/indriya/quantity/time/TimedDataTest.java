/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2018, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 *    and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of JSR-385, Indriya nor the names of their contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package tech.units.indriya.quantity.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.Test;

/**
 * Unit tests on the {@code TimedData} class.
 */
public class TimedDataTest {

  /**
   * Verifies that the factory method {@code of} has the value wired correctly.
   */
  @Test
  public void valueWiredCorrectlyInFactoryMethodOf() {
    final Double testValue = 4.2;
    TimedData<Double> td = TimedData.of(testValue, 1L);
    assertEquals(testValue, td.get());
  }

  /**
   * Verifies that the factory method {@code of} has the time wired correctly.
   */
  @Test
  public void timeWiredCorrectlyInFactoryMethodOf() {
    final long time = 42L;
    TimedData<Double> td = TimedData.of(1.0D, time);
    assertEquals(time, td.getTimestamp());
  }

  /**
   * Verifies that the constructor sets the instant correctly based on the provided timestamp.
   */
  @Test
  public void instantSetCorrectlyByConstructor() {
    final long time = 42L;
    Instant instant = Instant.ofEpochMilli(time);
    TimedData<Double> td = TimedData.of(1.0D, time);
    assertEquals(instant, td.getInstant());
  }

  /**
   * Verifies that the {@code toString} method produces the correct result for a value and a timestamp.
   */
  @Test
  public void toStringProducesCorrectResultForValueAndTimestamp() {
    TimedData<Double> td = TimedData.of(1D, 42L);
    assertEquals("data= 1.0, timestamp= 42", td.toString());
  }

  /**
   * Verifies that a {@code TimedData} instance is equal to itself.
   */
  @Test
  public void timedDataIsEqualToItself() {
    TimedData<Double> td = TimedData.of(1D, 42L);
    assertTrue(td.equals(td));
  }

  /**
   * Verifies that a {@code TimedData} instance is not equal to null.
   */
  @Test
  public void timedDataIsNotEqualToNull() {
    TimedData<Double> td = TimedData.of(1D, 42L);
    assertFalse(td.equals(null));
  }

  /**
   * Verifies that a {@code TimedData} instance is not equal to an object of a different class.
   */
  @Test
  public void timedDataIsNotEqualToObjectOfDifferentClass() {
    TimedData<Double> td = TimedData.of(1D, 42L);
    assertFalse(td.equals("a string"));
  }

  /**
   * Verifies that a {@code TimedData} instance is not equal to an instance with a different value.
   */
  @Test
  public void timedDataIsNotEqualToAnInstanceWithADifferentValue() {
    long time = 42L;
    TimedData<Double> td1 = TimedData.of(1D, time);
    TimedData<Double> td2 = TimedData.of(2D, time);
    assertFalse(td1.equals(td2));
  }

  /**
   * Verifies that a {@code TimedData} instance is not equal to an instance with a different time.
   */
  @Test
  public void timedDataIsNotEqualToAnInstanceWithADifferentTime() {
    Double value = 42D;
    TimedData<Double> td1 = TimedData.of(value, 1L);
    TimedData<Double> td2 = TimedData.of(value, 2L);
    assertFalse(td1.equals(td2));
  }

  /**
   * Verifies that a {@code TimedData} instance is equal to an instance with the same value and time.
   */
  @Test
  public void timedDataIsEqualToAnInstanceWithTheSameValueAndTimestamp() {
    Double value = 42D;
    long time = 1L;
    TimedData<Double> td1 = TimedData.of(value, time);
    TimedData<Double> td2 = TimedData.of(value, time);
    assertTrue(td1.equals(td2));
  }

  /**
   * Verifies that two {@code TimedData} instances with the same value and time have the same hashCode.
   */
  @Test
  public void timedDataInstancesWithTheSameValueAndTimestampHaveTheSameHashCode() {
    Double value = 42D;
    long time = 1L;
    TimedData<Double> td1 = TimedData.of(value, time);
    TimedData<Double> td2 = TimedData.of(value, time);
    assertEquals(td1.hashCode(), td2.hashCode());
  }

  /**
   * Verifies that a {@code TimedData} instance is not equal to an instance with a different value. Notice that this isn't a strict requirement on the
   * hashCode method, and that hash collisions may occur, but in general, objects that aren't equal shouldn't have an equal hash code.
   */
  @Test
  public void hashCodeIsDifferentForInstanceWithADifferentValue() {
    long time = 42L;
    TimedData<Double> td1 = TimedData.of(1D, time);
    TimedData<Double> td2 = TimedData.of(2D, time);
    assertFalse(td1.hashCode() == td2.hashCode());
  }

  /**
   * Verifies that a {@code TimedData} instance is not equal to an instance with a different time. Notice that this isn't a strict requirement on the
   * hashCode method, and that hash collisions may occur, but in general, objects that aren't equal shouldn't have an equal hash code.
   */
  @Test
  public void hashCodeIsDifferentForInstanceWithADifferentTime() {
    Double value = 42D;
    TimedData<Double> td1 = TimedData.of(value, 1L);
    TimedData<Double> td2 = TimedData.of(value, 2L);
    assertFalse(td1.hashCode() == td2.hashCode());
  }
}
