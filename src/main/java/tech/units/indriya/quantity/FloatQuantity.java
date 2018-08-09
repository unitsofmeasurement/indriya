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

import java.math.BigDecimal;

import javax.measure.Quantity;
import javax.measure.Unit;

import tech.units.indriya.AbstractQuantity;
import tech.units.indriya.ComparableQuantity;

/**
 * An amount of quantity, consisting of a float and a Unit. FloatQuantity objects are immutable.
 * 
 * @see AbstractQuantity
 * @see Quantity
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @author Otavio de Santana
 * @param <Q>
 *          The type of the quantity.
 * @version 0.6, $Date: 2018-07-20 $
 * @since 1.0
 */
final class FloatQuantity<Q extends Quantity<Q>> extends JavaNumberQuantity<Q> {

  private static final long serialVersionUID = 5992028803791009345L;

  private static final BigDecimal FLOAT_MAX_VALUE = new BigDecimal(Float.MAX_VALUE);

  final float value;

  public FloatQuantity(float value, Unit<Q> unit) {
    super(unit);
    this.value = value;
  }

  @Override
  public Float getValue() {
    return value;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public ComparableQuantity<Q> inverse() {
    return (AbstractQuantity<Q>) new FloatQuantity(1f / value, getUnit().inverse());
  }

  @Override
  public boolean isBig() {
    return false;
  }

  @Override
  public boolean isDecimal() {
    return true;
  }

  @Override
  public int getSize() {
    return Float.SIZE;
  }

  @Override
  public Class<?> getNumberType() {
    return float.class;
  }

  @Override
  Number castFromBigDecimal(BigDecimal value) {
    return (float) value.doubleValue();
  }

  @Override
  boolean isOverflowing(BigDecimal value) {
    return value.compareTo(FLOAT_MAX_VALUE.negate()) < 0 || value.compareTo(FLOAT_MAX_VALUE) > 0;
  }

  @Override
  public Quantity<Q> negate() {
    return new FloatQuantity<Q>(-value, getUnit());
  }
}
