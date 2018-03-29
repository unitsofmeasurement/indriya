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
package tech.units.indriya.unit;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.spi.Prefix;
import tech.units.indriya.function.RationalConverter;

/**
 * <p>
 * This class provides support for common binary prefixes to be used by units.
 * </p>
 *
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 1.3, March 29, 2018
 * @see <a href="https://en.wikipedia.org/wiki/Binary_prefix">Wikipedia: Binary Prefix</a>
 * @since 2.0
 */
public enum BinaryPrefix implements Prefix {
  KIBI("Ki", RationalConverter.of(1024, 1)), //
  MEBI("Mi", RationalConverter.of(1024, 2)), //
  GIBI("Gi", RationalConverter.of(1024, 3)), //
  TEBI("Ti", RationalConverter.of(1024, 4)), //
  PEBI("Pi", RationalConverter.of(1024, 5)), //
  EXBI("Ei", RationalConverter.of(1024, 6)), //
  ZEBI("Zi", RationalConverter.of(1024, 7)), //
  YOBI("Yi", RationalConverter.of(1024, 8));

  /**
   * The symbol of this prefix, as returned by {@link #getSymbol}.
   *
   * @serial
   * @see #getSymbol()
   */
  private final String symbol;

  /**
   * The <code>UnitConverter</code> of this prefix, as returned by {@link #getConverter}.
   *
   * @serial
   * @see #getConverter()
   * @see {@link UnitConverter}
   */
  private final UnitConverter converter;

  /**
   * Creates a new prefix.
   *
   * @param symbol
   *          the symbol of this prefix.
   * @param converter
   *          the associated unit converter.
   */
  private BinaryPrefix(String symbol, UnitConverter converter) {
    this.symbol = symbol;
    this.converter = converter;
  }

  /**
   * Returns the specified unit multiplied by the factor <code>1024</code> (binary prefix).
   * 
   * @param unit
   *          any unit.
   * @return <code>unit.multiply(1024)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> KIBI(Unit<Q> unit) {
    return unit.transform(KIBI.getConverter());
  }

  /**
   * Returns the specified unit multiplied by the factor <code>1024<sup>2</sup></code> (binary prefix).
   * 
   * @param unit
   *          any unit.
   * @return <code>unit.multiply(1048576)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> MEBI(Unit<Q> unit) {
    return unit.transform(MEBI.getConverter());
  }

  /**
   * Returns the specified unit multiplied by the factor <code>1024<sup>3</sup></code> (binary prefix).
   * 
   * @param unit
   *          any unit.
   * @return <code>unit.multiply(1073741824)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> GIBI(Unit<Q> unit) {
    return unit.transform(GIBI.getConverter());
  }

  /**
   * Returns the specified unit multiplied by the factor <code>1024<sup>4</sup></code> (binary prefix).
   * 
   * @param unit
   *          any unit.
   * @return <code>unit.multiply(1099511627776L)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> TEBI(Unit<Q> unit) {
    return unit.transform(TEBI.getConverter());
  }

  /**
   * Returns the specified unit multiplied by the factor <code>1024<sup>5</sup></code> (binary prefix).
   * 
   * @param unit
   *          any unit.
   * @return <code>unit.multiply(1125899906842624L)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> PEBI(Unit<Q> unit) {
    return unit.transform(PEBI.getConverter());
  }

  /**
   * Returns the specified unit multiplied by the factor <code>1024<sup>6</sup></code> (binary prefix).
   * 
   * @param unit
   *          any unit.
   * @return <code>unit.multiply(1152921504606846976L)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> EXBI(Unit<Q> unit) {
    return unit.transform(EXBI.getConverter());
  }

  /**
   * Returns the specified unit multiplied by the factor <code>1024<sup>7</sup></code> (binary prefix).
   * 
   * @param unit
   *          any unit.
   * @return <code>unit.multiply(1152921504606846976d)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> ZEBI(Unit<Q> unit) {
    return unit.transform(ZEBI.getConverter());
  }

  /**
   * Returns the specified unit multiplied by the factor <code>1024<sup>8</sup></code> (binary prefix).
   * 
   * @param unit
   *          any unit.
   * @return <code>unit.multiply(1208925819614629174706176d)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> YOBI(Unit<Q> unit) {
    return unit.transform(YOBI.getConverter());
  }

  /**
   * Returns the corresponding unit converter.
   *
   * @return the unit converter.
   */
  public UnitConverter getConverter() {
    return converter;
  }

  /**
   * Returns the symbol of this prefix.
   *
   * @return this prefix symbol, not {@code null}.
   */
  public String getSymbol() {
    return symbol;
  }

  /**
   * Returns a Set containing the BinaryPrefix values.<br>
   * This method may be used to iterate over the prefixes as follows:
   * 
   * <pre>
   * <code>
   *    for(Prefix p : BinaryPrefix.prefixes())
   *        System.out.println(p);
   * </code>
   * </pre>
   * 
   * @return a set containing the constant values of this Prefix type, in the order they're declared
   */
  public static Set<Prefix> prefixes() {
    return Collections.<Prefix> unmodifiableSet(EnumSet.allOf(BinaryPrefix.class));
  }
}
