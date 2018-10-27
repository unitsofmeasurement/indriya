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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.Test;

/**
 * Unit tests on the {@code TimedData} class.
 */
public class TimedDataTest {

  /**
   * Verifies that the factory method {@code of} with a value and a time only has the value wired correctly.
   */
  @Test
  public void valueWiredCorrectlyInFactoryMethodOfWithValueAndTimeOnly() {
    final Double testValue = 4.2;
    TimedData<Double> td = TimedData.of(testValue, 1L);
    assertEquals(testValue, td.get());
  }

  /**
   * Verifies that the factory method {@code of} with a value and a time only has the time wired correctly.
   */
  @Test
  public void timeWiredCorrectlyInFactoryMethodOfWithValueAndTimeOnly() {
    final long time = 42L;
    TimedData<Double> td = TimedData.of(1.0D, time);
    assertEquals(time, td.getTimestamp());
  }

  /**
   * Verifies that the factory method {@code of} with a value and a time only sets the name to {@code null}.
   */
  @Test
  public void factoryMethodOfWithValueAndTimeOnlySetsNameToNull() {
    TimedData<Double> td = TimedData.of(1.0D, 1L);
    assertNull(td.getName());
  }

  /**
   * Verifies that the factory method {@code of} with a value, a time and a name has the value wired correctly.
   */
  @Test
  public void valueWiredCorrectlyInFactoryMethodOfWithValueTimeAndName() {
    final Double testValue = 4.2;
    TimedData<Double> td = TimedData.of(testValue, 1L, "name");
    assertEquals(testValue, td.get());
  }

  /**
   * Verifies that the factory method {@code of} with a value, a time and a name has the time wired correctly.
   */
  @Test
  public void timeWiredCorrectlyInFactoryMethodOfWithValueTimeAndName() {
    final long time = 42L;
    TimedData<Double> td = TimedData.of(1.0D, time, "name");
    assertEquals(time, td.getTimestamp());
  }

  /**
   * Verifies that the factory method {@code of} with a value, a time and a name has the name wired correctly.
   */
  @Test
  public void nameWiredCorrectlyInFactoryMethodOfWithValueTimeAndName() {
    final String name = "name";
    TimedData<Double> td = TimedData.of(1.0D, 1L, name);
    assertEquals(name, td.getName());
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
   * Verifies that the {@code toString} method produces the correct result for a value, a timestamp and a name.
   */
  @Test
  public void toStringProducesCorrectResultForValueTimestampAndName() {
    TimedData<Double> td = TimedData.of(1D, 42L, "foo");
    assertEquals("data= 1.0, timestamp= 42, name= foo", td.toString());
  }

  /**
   * Verifies that a {@code TimedData} instance is equal to itself.
   */
  @Test
  public void timedDataIsEqualToItself() {
    TimedData<Double> td = TimedData.of(1D, 42L, "foo");
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
    assertNotEquals("a string", String.valueOf(td));
  }

  /**
   * Verifies that a {@code TimedData} instance is not equal to an instance with a different value.
   */
  @Test
  public void timedDataIsNotEqualToAnInstanceWithADifferentValue() {
    long time = 42L;
    String name = "foo";
    TimedData<Double> td1 = TimedData.of(1D, time, name);
    TimedData<Double> td2 = TimedData.of(2D, time, name);
    assertFalse(td1.equals(td2));
  }

  /**
   * Verifies that a {@code TimedData} instance is not equal to an instance with a different time.
   */
  @Test
  public void timedDataIsNotEqualToAnInstanceWithADifferentTime() {
    Double value = 42D;
    String name = "foo";
    TimedData<Double> td1 = TimedData.of(value, 1L, name);
    TimedData<Double> td2 = TimedData.of(value, 2L, name);
    assertNotEquals(td1, td2);
  }

  /**
   * Verifies that a {@code TimedData} instance is not equal to an instance with a different name.
   */
  @Test
  public void timedDataIsNotEqualToAnInstanceWithADifferentName() {
    long time = 42L;
    Double value = 42D;
    TimedData<Double> td1 = TimedData.of(value, time, "foo");
    TimedData<Double> td2 = TimedData.of(value, time, "bar");
    assertNotEquals(td1, td2);
  }

  /**
   * Verifies that a {@code TimedData} instance is equal to an instance with the same value, time and name.
   */
  @Test
  public void timedDataIsEqualToAnInstanceWithTheSameValueTimestampAndName() {
    Double value = 42D;
    long time = 1L;
    String name = "foo";
    TimedData<Double> td1 = TimedData.of(value, time, name);
    TimedData<Double> td2 = TimedData.of(value, time, name);
    assertEquals(td1, td2);
  }

  /**
   * Verifies that two {@code TimedData} instances with the same value and time have the same hashCode.
   */
  @Test
  public void timedDataInstancesWithTheSameValueTimestampAndNameHaveTheSameHashCode() {
    Double value = 42D;
    long time = 1L;
    String name = "foo";
    TimedData<Double> td1 = TimedData.of(value, time, name);
    TimedData<Double> td2 = TimedData.of(value, time, name);
    assertEquals(td1.hashCode(), td2.hashCode());
  }

  /**
   * Verifies that a {@code TimedData} instance is not equal to an instance with a different value. Notice that this isn't a strict requirement on the
   * hashCode method, and that hash collisions may occur, but in general, objects that aren't equal shouldn't have an equal hash code.
   */
  @Test
  public void hashCodeIsDifferentForInstanceWithADifferentValue() {
    long time = 42L;
    String name = "foo";
    TimedData<Double> td1 = TimedData.of(1D, time, name);
    TimedData<Double> td2 = TimedData.of(2D, time, name);
    assertFalse(td1.hashCode() == td2.hashCode());
  }

  /**
   * Verifies that a {@code TimedData} instance is not equal to an instance with a different time. Notice that this isn't a strict requirement on the
   * hashCode method, and that hash collisions may occur, but in general, objects that aren't equal shouldn't have an equal hash code.
   */
  @Test
  public void hashCodeIsDifferentForInstanceWithADifferentTime() {
    Double value = 42D;
    String name = "foo";
    TimedData<Double> td1 = TimedData.of(value, 1L, name);
    TimedData<Double> td2 = TimedData.of(value, 2L, name);
    assertFalse(td1.hashCode() == td2.hashCode());
  }

  /**
   * Verifies that a {@code TimedData} instance is not equal to an instance with a different name. Notice that this isn't a strict requirement on the
   * hashCode method, and that hash collisions may occur, but in general, objects that aren't equal shouldn't have an equal hash code.
   */
  @Test
  public void hashCodeIsDifferentForInstanceWithADifferentName() {
    Double value = 42D;
    long time = 42L;
    TimedData<Double> td1 = TimedData.of(value, time, "foo");
    TimedData<Double> td2 = TimedData.of(value, time, "bar");
    assertFalse(td1.hashCode() == td2.hashCode());
  }
}
