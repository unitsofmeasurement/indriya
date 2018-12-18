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
import java.math.MathContext;
import java.text.NumberFormat;

import javax.measure.Quantity;
import javax.measure.Unit;

import tech.units.indriya.AbstractQuantity;
import tech.units.indriya.ComparableQuantity;

/**
 * An amount of quantity, implementation of {@link ComparableQuantity} that uses {@link BigDecimal} as implementation of {@link Number}, this object
 * is immutable. Note: all operations which involves {@link Number}, this implementation will convert to {@link BigDecimal}, and all operation of
 * BigDecimal will use {@link MathContext#DECIMAL128}.
 *
 * @param <Q>
 *          The type of the quantity.
 * @author otaviojava
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @see AbstractQuantity
 * @see Quantity
 * @see ComparableQuantity
 * @version 1.3
 * @since 1.0
 */
final class DecimalQuantity<Q extends Quantity<Q>> extends JavaNumericQuantity<Q> {

  private static final long serialVersionUID = 6504081836032983882L;

  private final BigDecimal value;

  public DecimalQuantity(BigDecimal value, Unit<Q> unit,  Scale sc) {
      super(unit, sc);
      this.value = value;
  }
  
  public DecimalQuantity(BigDecimal value, Unit<Q> unit) {
    super(unit);
    this.value = value;
  }

  public DecimalQuantity(double value, Unit<Q> unit) {
    super(unit);
    this.value = BigDecimal.valueOf(value);
  }

  @Override
  public BigDecimal getValue() {
    return value;
  }

  @SuppressWarnings("unchecked")
  @Override
  public ComparableQuantity<Q> inverse() {
    return (ComparableQuantity<Q>) Quantities.getQuantity(BigDecimal.ONE.divide(value), getUnit().inverse());
  }

  @Override
  public boolean isBig() {
    return true;
  }

  @Override
  public boolean isDecimal() {
    return true;
  }

  @Override
  public int getSize() {
    return 0;
  }

  @Override
  public Class<?> getNumberType() {
    return BigDecimal.class;
  }

  @Override
  Number castFromBigDecimal(BigDecimal aValue) {
    return aValue;
  }

  /**
   * <p>
   * Returns a {@code DecimalQuantity} with same Unit, but whose value is {@code(-this.getValue())}. </p>
   * 
   * @return {@code -this}.
   */
  public DecimalQuantity<Q> negate() {
    return new DecimalQuantity<Q>(value.negate(), getUnit());
  }
  
  /**
   * Returns the <code>String</code> representation of this quantity. The string produced for a given quantity is always the same; it is not
   * affected by locale. This means that it can be used as a canonical string representation for exchanging quantity, or as a key for a Hashtable,
   * etc. Locale-sensitive quantity formatting and parsing is handled by the {@link QuantityFormat} implementations and its subclasses.
   *
   * @return <code>UnitFormat.getInternational().format(this)</code>
   */
  @Override
  public String toString() {
      return NumberFormat.getInstance().format(value) + " " + String.valueOf(getUnit());
  }

  @Override
  boolean isOverflowing(BigDecimal aValue) {
    return false;
  }
}
