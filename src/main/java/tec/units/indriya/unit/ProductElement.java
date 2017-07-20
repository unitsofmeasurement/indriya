/*
 * Next Generation Units of Measurement Implementation
 * Copyright (c) 2005-2017, Jean-Marie Dautelle, Werner Keil, V2COM.
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
 * 3. Neither the name of JSR-363, Indriya nor the names of their contributors may be used to endorse or promote products
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
package tec.units.indriya.unit;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * Inner product element represents a rational power of a single unit.
 * 
 * @deprecated Seems unused
 */
final class ElementProduct<T extends Quantity<T>> {

  /**
   * Holds the single unit.
   */
  final Unit<T> unit;

  /**
   * Holds the power exponent.
   */
  final int pow;

  /**
   * Holds the root exponent.
   */
  final int root;

  /**
   * Structural constructor.
   *
   * @param unit
   *          the unit.
   * @param pow
   *          the power exponent.
   * @param root
   *          the root exponent.
   */
  ElementProduct(Unit<T> unit, int pow, int root) {
    this.unit = unit;
    this.pow = pow;
    this.root = root;
  }

  /**
   * Returns this element's unit.
   *
   * @return the single unit.
   */
  public Unit<?> getUnit() {
    return unit;
  }

  /**
   * Returns the power exponent. The power exponent can be negative but is always different from zero.
   *
   * @return the power exponent of the single unit.
   */
  public int getPow() {
    return pow;
  }

  /**
   * Returns the root exponent. The root exponent is always greater than zero.
   *
   * @return the root exponent of the single unit.
   */
  public int getRoot() {
    return root;
  }
}