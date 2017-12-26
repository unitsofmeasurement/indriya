/*
 * Next Generation Units of Measurement Implementation
 * Copyright (c) 2005-2017, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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
package tec.units.indriya.format;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParsePosition;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.format.ParserException;
import javax.measure.format.UnitFormat;

import tec.units.indriya.AbstractUnit;
import tec.units.indriya.ComparableQuantity;
import tec.units.indriya.quantity.Quantities;

@SuppressWarnings({ "rawtypes", "unchecked" })
class NumberSpaceQuantityFormat extends QuantityFormat {

  private final NumberFormat numberFormat;

  private final UnitFormat unitFormat;

  NumberSpaceQuantityFormat(NumberFormat numberFormat, UnitFormat unitFormat) {
    this.numberFormat = numberFormat;
    this.unitFormat = unitFormat;
  }

  static int getFractionDigitsCount(double d) {
    if (d >= 1) { // we only need the fraction digits
      d = d - (long) d;
    }
    if (d == 0) { // nothing to count
      return 0;
    }
    d *= 10; // shifts 1 digit to left
    int count = 1;
    while (d - (long) d != 0) { // keeps shifting until there are no more
      // fractions
      d *= 10;
      count++;
    }
    return count;
  }

  @Override
  public Appendable format(Quantity<?> quantity, Appendable dest) throws IOException {
    // dest.append(numberFormat.format(quantity.getValue()));
    // if (quantity.getUnit().equals(AbstractUnit.ONE))
    // return dest;
    // dest.append(' ');
    // return unitFormat.format(quantity.getUnit(), dest);
    int fract = 0;
    if (quantity != null && quantity.getValue() != null) {
      fract = getFractionDigitsCount(quantity.getValue().doubleValue());
    }
    if (fract > 1) {
      numberFormat.setMaximumFractionDigits(fract + 1);
    }
    dest.append(numberFormat.format(quantity.getValue()));
    if (quantity.getUnit().equals(AbstractUnit.ONE))
      return dest;
    dest.append(' ');
    return unitFormat.format(quantity.getUnit(), dest);
  }

  @Override
  public ComparableQuantity<?> parse(CharSequence csq, ParsePosition cursor) throws IllegalArgumentException, ParserException {
    String str = csq.toString();
    Number number = numberFormat.parse(str, cursor);
    if (number == null)
      throw new IllegalArgumentException("Number cannot be parsed");

    Unit unit = unitFormat.parse(csq);
    return Quantities.getQuantity(number.longValue(), unit);
  }

  @Override
  ComparableQuantity<?> parse(CharSequence csq, int index) throws IllegalArgumentException, ParserException {
    return parse(csq, new ParsePosition(index));
  }

  @Override
  public ComparableQuantity<?> parse(CharSequence csq) throws IllegalArgumentException, ParserException {
    return parse(csq, 0);
  }

  private static final long serialVersionUID = 1L;

}