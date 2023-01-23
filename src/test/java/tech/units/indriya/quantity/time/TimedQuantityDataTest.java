/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2023, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.KILOGRAM;
import static tech.units.indriya.unit.Units.LITRE;
import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;

import org.junit.jupiter.api.Test;

import tech.units.indriya.quantity.Quantities;

/**
 * Unit tests on the {@code TimedQuantityData} class.
 */
public class TimedQuantityDataTest {

  /**
   * Verifies that the factory method {@code of} has the value wired correctly.
   */
  @Test
  public void valueWiredCorrectlyInFactoryMethodOf() {
    final Quantity<Volume> testValue = Quantities.getQuantity(4.2, LITRE);
    TimedQuantityData<Volume> td = TimedQuantityData.of(testValue, 1L);
    assertEquals(testValue, td.get());
  }


  /**
   * Verifies that a {@code TimedQuantityData} instance is not equal to an instance with a different value. Notice that this isn't a strict requirement on the
   * hashCode method, and that hash collisions may occur, but in general, objects that aren't equal shouldn't have an equal hash code.
   */
  @Test
  public void hashCodeIsDifferentForInstanceWithADifferentValue() {
    Quantity<Length> tenM = Quantities.getQuantity(10, METRE);
    Quantity<Length> twentyM = Quantities.getQuantity(20, METRE);
    long time = System.currentTimeMillis();
    TimedQuantityData<Length> td1 = TimedQuantityData.of(tenM, time);
    TimedQuantityData<Length> td2 = TimedQuantityData.of(twentyM, time);
    assertFalse(td1.hashCode() == td2.hashCode());
  }

  /**
   * Verifies that a {@code TimedQuantityData} instance is not equal to an instance with a different time. Notice that this isn't a strict requirement on the
   * hashCode method, and that hash collisions may occur, but in general, objects that aren't equal shouldn't have an equal hash code.
   */
  @Test
  public void hashCodeIsDifferentForInstanceWithADifferentTime() {
	Quantity<Mass> value = Quantities.getQuantity(42D, KILOGRAM);
    TimedQuantityData<Mass> td1 = TimedQuantityData.of(value, 1L);
    TimedQuantityData<Mass> td2 = TimedQuantityData.of(value, 2L);
    assertFalse(td1.hashCode() == td2.hashCode());
  }
}
