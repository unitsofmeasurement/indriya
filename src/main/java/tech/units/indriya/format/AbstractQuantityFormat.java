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
package tech.units.indriya.format;

import java.io.IOException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

import javax.measure.Quantity;
import javax.measure.format.ParserException;
import tech.units.indriya.format.QuantityFormat;
import tech.units.indriya.AbstractQuantity;
import tech.units.indriya.ComparableQuantity;
import tec.uom.lib.common.function.Parser;

/**
 * <p>
 * This class provides the interface for formatting and parsing {@link Quantity quantities}.
 * </p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:werner@uom.technology">Werner Keil</a>
 * @version 1.1, $Date: 2018-04-06 $
 * @since 1.0
 * 
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractQuantityFormat extends Format implements QuantityFormat, Parser<CharSequence, ComparableQuantity> {
  // TODO for later, see https://github.com/unitsofmeasurement/indriya/issues/17
  // * <p>
  // * Instances of this class should be able to format quantities stated in {@link CompoundUnit}. See {@link #formatCompound formatCompound(...)}.
  // * </p>

  /**
   *
   */
  private static final long serialVersionUID = -4628006924354248662L;

  /**
   * Formats the specified quantity into an <code>Appendable</code>.
   *
   * @param quantity
   *          the quantity to format.
   * @param dest
   *          the appendable destination.
   * @return the specified <code>Appendable</code>.
   * @throws IOException
   *           if an I/O exception occurs.
   */
  public abstract Appendable format(Quantity<?> quantity, Appendable dest) throws IOException;

  /**
   * Parses a portion of the specified <code>CharSequence</code> from the specified position to produce an object. If parsing succeeds, then the index
   * of the <code>cursor</code> argument is updated to the index after the last character used.
   *
   * @param csq
   *          the <code>CharSequence</code> to parse.
   * @param cursor
   *          the cursor holding the current parsing index.
   * @return the object parsed from the specified character sub-sequence.
   * @throws IllegalArgumentException
   *           if any problem occurs while parsing the specified character sequence (e.g. illegal syntax).
   */
  public abstract ComparableQuantity<?> parse(CharSequence csq, ParsePosition cursor) throws IllegalArgumentException, ParserException;

  /**
   * Parses a portion of the specified <code>CharSequence</code> from the specified position to produce an object. If parsing succeeds, then the index
   * of the <code>cursor</code> argument is updated to the index after the last character used.
   *
   * @param csq
   *          the <code>CharSequence</code> to parse.
   * @param cursor
   *          the cursor holding the current parsing index.
   * @return the object parsed from the specified character sub-sequence.
   * @throws IllegalArgumentException
   *           if any problem occurs while parsing the specified character sequence (e.g. illegal syntax).
   */
  @Override
  public abstract ComparableQuantity<?> parse(CharSequence csq) throws ParserException;

  /**
   * Parses a portion of the specified <code>CharSequence</code> from the specified position to produce an object. If parsing succeeds, then the index
   * of the <code>cursor</code> argument is updated to the index after the last character used.
   * 
   * @param csq
   *          the <code>CharSequence</code> to parse.
   * @param index
   *          the current parsing index.
   * @return the object parsed from the specified character sub-sequence.
   * @throws IllegalArgumentException
   *           if any problem occurs while parsing the specified character sequence (e.g. illegal syntax).
   */
  abstract ComparableQuantity<?> parse(CharSequence csq, int index) throws IllegalArgumentException, ParserException;

  @Override
  public final StringBuffer format(Object obj, final StringBuffer toAppendTo, FieldPosition pos) {
    if (!(obj instanceof AbstractQuantity<?>))
      throw new IllegalArgumentException("obj: Not an instance of Quantity");
    if ((toAppendTo == null) || (pos == null))
      throw new NullPointerException();
    try {
      return (StringBuffer) format((AbstractQuantity<?>) obj, toAppendTo);
    } catch (IOException ex) {
      throw new Error(ex); // Cannot happen.
    }
  }

  @Override
  public final Quantity<?> parseObject(String source, ParsePosition pos) {
    try {
      return parse(source, pos);
    } catch (IllegalArgumentException | ParserException e) {
      return null;
    }
  }

  /**
   * Convenience method equivalent to {@link #format(AbstractQuantity, Appendable)} except it does not raise an IOException.
   *
   * @param quantity
   *          the quantity to format.
   * @param dest
   *          the appendable destination.
   * @return the specified <code>StringBuilder</code>.
   */
  public final StringBuilder format(AbstractQuantity<?> quantity, StringBuilder dest) {
    try {
      return (StringBuilder) this.format(quantity, (Appendable) dest);
    } catch (IOException ex) {
      throw new RuntimeException(ex); // Should not happen.
    }
  }
}
