/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2019, Units of Measurement project.
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

import java.time.Instant;
import java.util.Objects;
import java.util.function.Supplier;

import tech.uom.lib.common.function.Nameable;

/**
 * TimedData is a container for a data value that keeps track of its age. This class keeps track of the birth time of a bit of data, i.e. time the
 * object is instantiated.<br>
 * The TimedData MUST be immutable.
 * 
 * @param <T>
 *          The data value.
 * 
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 0.7
 * @since 1.0
 * @see <a href="http://en.wikipedia.org/wiki/Time_series"> Wikipedia: Time Series</a>
 */
public class TimedData<T> implements Nameable, Supplier<T> {
  private final T value;
  private final long timestamp;
  private final Instant instant;
  private final String name;

  /**
   * Construct an instance of TimedData with a value and timestamp.
   *
   * @param data
   *          The value of the TimedData.
   * @param time
   *          The timestamp of the TimedData.
   */
  protected TimedData(T value, long time) {
    this(value, time, null);
  }

  /**
   * Construct an instance of TimedData with a value, a timestamp and a name.
   *
   * @param data
   *          The value of the TimedData.
   * @param time
   *          The timestamp of the TimedData.
   * @param name
   *          The name of the TimedData.
   */
  protected TimedData(T value, long time, String name) {
    this.value = value;
    this.timestamp = time;
    this.instant = Instant.ofEpochMilli(time);
    this.name = name;
  }

  /**
   * Returns a {@code TimedData} with the specified values.
   *
   * @param <T>
   *          the class of the value
   * @param val
   *          The value for the timed data.
   * @param time
   *          The timestamp.
   * @return an {@code TimedData} with the given values
   */
  public static <T> TimedData<T> of(T val, long time) {
    return new TimedData<>(val, time);
  }

  /**
   * Returns a {@code TimedData} with the specified values.
   *
   * @param <T>
   *          the class of the value
   * @param val
   *          The value for the timed data.
   * @param time
   *          The timestamp.
   * @param name
   *          The name.
   * @return an {@code TimedData} with the given values
   */
  public static <T> TimedData<T> of(T val, long time, String name) {
    return new TimedData<>(val, time, name);
  }

  /**
   * Returns the time with which this TimedData was created.
   * 
   * @return the time of creation
   */
  public long getTimestamp() {
    return timestamp;
  }

  public String getName() {
    return name;
  }

  public T get() {
    return value;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof TimedData<?>) {
      @SuppressWarnings("unchecked")
      final TimedData<T> other = (TimedData<T>) obj;
      return Objects.equals(get(), other.get()) && Objects.equals(getTimestamp(), other.getTimestamp()) && Objects.equals(getName(), other.getName());

    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, timestamp, name);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder().append("data= ").append(get()).append(", timestamp= ").append(getTimestamp());
    if (name != null) {
      sb.append(", name= ").append(getName());
    }
    return sb.toString();
  }

  public Instant getInstant() {
    return instant;
  }

}
