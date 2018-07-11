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
package tech.units.indriya.quantity;

import javax.measure.Quantity;

import tech.units.indriya.ComparableQuantity;

/**
 * Interface extending {@link ComparableQuantity}, indicating that a quantity class holds a Java {@link Number} to store the value.
 * 
 * @param <Q>
 *          The type of the quantity.
 */
interface JavaNumberQuantity<Q extends Quantity<Q>> extends ComparableQuantity<Q> {

  /**
   * Indicates whether the quantity holds a big value {@link Number} type.
   * 
   * @return True if the value type is big.
   */
  boolean isBig();

  /**
   * Indicates whether the quantity holds a decimal value {@link Number} type.
   * 
   * @return True if the value type is decimal.
   */
  boolean isDecimal();

  /**
   * Returns the size of the {@link Number} type of the quantity, or zero if the {@link Number} type doesn't have a defined size.
   * 
   * @return The size of the {@link Number} type of the quantity, or zero if a size is not applicable to the {@link Number} type.
   */
  int getSize();

  /**
   * Returns the {@link Number} type of the quantity.
   * 
   * @return The {@link Number} type.
   */
  Class<?> getNumberType();
}
