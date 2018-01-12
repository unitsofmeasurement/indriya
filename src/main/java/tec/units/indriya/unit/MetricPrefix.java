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
package tec.units.indriya.unit;

import tec.units.indriya.function.RationalConverter;
import tec.uom.lib.common.function.SymbolSupplier;
import tec.uom.lib.common.function.UnitConverterSupplier;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;

import java.math.BigInteger;

/**
 * <p>
 * This class provides support for the 20 prefixes used in the metric system (decimal multiples and submultiples of units). For example:
 * 
 * <pre>
 * <code>
 *     import static tec.units.indriya.unit.Units.*;  // Static import.
 *     import static tec.units.indriya.unit.MetricPrefix.*; // Static import.
 *     import javax.measure.*;
 *     import javax.measure.quantity.*;
 *     ...
 *     Unit<Pressure> HECTOPASCAL = HECTO(PASCAL);
 *     Unit<Length> KILOMETRE = KILO(METRE);
 *     </code>
 * </pre>
 * 
 * </p>
 *
 * @see <a href="http://en.wikipedia.org/wiki/Metric_prefix">Wikipedia: Metric Prefix</a>
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 1.0, 2017-12-25
 * @since 1.0
 */
public enum MetricPrefix implements SymbolSupplier, UnitConverterSupplier, Prefix {
  YOTTA("Y", new RationalConverter(BigInteger.TEN.pow(24), BigInteger.ONE)), ZETTA("Z", new RationalConverter(BigInteger.TEN.pow(21), BigInteger.ONE)), EXA(
      "E", new RationalConverter(BigInteger.TEN.pow(18), BigInteger.ONE)), PETA("P", new RationalConverter(BigInteger.TEN.pow(15), BigInteger.ONE)), TERA(
      "T", new RationalConverter(BigInteger.TEN.pow(12), BigInteger.ONE)), GIGA("G", new RationalConverter(BigInteger.TEN.pow(9), BigInteger.ONE)), MEGA(
      "M", new RationalConverter(BigInteger.TEN.pow(6), BigInteger.ONE)), KILO("k", new RationalConverter(BigInteger.TEN.pow(3), BigInteger.ONE)), HECTO(
      "h", new RationalConverter(BigInteger.TEN.pow(2), BigInteger.ONE)), DEKA("da", new RationalConverter(BigInteger.TEN.pow(1), BigInteger.ONE)), DECI(
      "d", new RationalConverter(BigInteger.ONE, BigInteger.TEN.pow(1))), CENTI("c", new RationalConverter(BigInteger.ONE, BigInteger.TEN.pow(2))), MILLI(
      "m", new RationalConverter(BigInteger.ONE, BigInteger.TEN.pow(3))), MICRO("µ", new RationalConverter(BigInteger.ONE, BigInteger.TEN.pow(6))), NANO(
      "n", new RationalConverter(BigInteger.ONE, BigInteger.TEN.pow(9))), PICO("p", new RationalConverter(BigInteger.ONE, BigInteger.TEN.pow(12))), FEMTO(
      "f", new RationalConverter(BigInteger.ONE, BigInteger.TEN.pow(15))), ATTO("a", new RationalConverter(BigInteger.ONE, BigInteger.TEN.pow(18))), ZEPTO(
      "z", new RationalConverter(BigInteger.ONE, BigInteger.TEN.pow(21))), YOCTO("y", new RationalConverter(BigInteger.ONE, BigInteger.TEN.pow(24)));

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
  MetricPrefix(String symbol, RationalConverter converter) {
    this.symbol = symbol;
    this.converter = converter;
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
   * Returns the corresponding unit converter.
   *
   * @return the unit converter.
   */
  public UnitConverter getConverter() {
    return converter;
  }

  /**
   * Returns the specified unit multiplied by the factor <code>10<sup>24</sup></code>
   *
   * @param <Q>
   *          The type of the quantity measured by the unit.
   * @param unit
   *          any unit.
   * @return <code>unit.times(1e24)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> YOTTA(Unit<Q> unit) {
    return unit.transform(YOTTA.getConverter());
  }

  /**
   * Returns the specified unit multiplied by the factor <code>10<sup>21</sup></code>
   *
   * @param <Q>
   *          The type of the quantity measured by the unit.
   * @param unit
   *          any unit.
   * @return <code>unit.times(1e21)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> ZETTA(Unit<Q> unit) {
    return unit.transform(ZETTA.getConverter());
  }

  /**
   * Returns the specified unit multiplied by the factor <code>10<sup>18</sup></code>
   *
   * @param <Q>
   *          The type of the quantity measured by the unit.
   * @param unit
   *          any unit.
   * @return <code>unit.times(1e18)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> EXA(Unit<Q> unit) {
    return unit.transform(EXA.getConverter());
  }

  /**
   * Returns the specified unit multiplied by the factor <code>10<sup>15</sup></code>
   *
   * @param <Q>
   *          The type of the quantity measured by the unit.
   * @param unit
   *          any unit.
   * @return <code>unit.times(1e15)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> PETA(Unit<Q> unit) {
    return unit.transform(PETA.getConverter());
  }

  /**
   * Returns the specified unit multiplied by the factor <code>10<sup>12</sup></code>
   *
   * @param <Q>
   *          The type of the quantity measured by the unit.
   * @param unit
   *          any unit.
   * @return <code>unit.times(1e12)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> TERA(Unit<Q> unit) {
    return unit.transform(TERA.getConverter());
  }

  /**
   * Returns the specified unit multiplied by the factor <code>10<sup>9</sup></code>
   *
   * @param <Q>
   *          The type of the quantity measured by the unit.
   * @param unit
   *          any unit.
   * @return <code>unit.times(1e9)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> GIGA(Unit<Q> unit) {
    return unit.transform(GIGA.getConverter());
  }

  /**
   * Returns the specified unit multiplied by the factor <code>10<sup>6</sup></code>
   *
   * @param <Q>
   *          The type of the quantity measured by the unit.
   * @param unit
   *          any unit.
   * @return <code>unit.times(1e6)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> MEGA(Unit<Q> unit) {
    return unit.transform(MEGA.getConverter());
  }

  /**
   * Returns the specified unit multiplied by the factor <code>10<sup>3</sup></code>
   *
   * @param <Q>
   *          The type of the quantity measured by the unit.
   * @param unit
   *          any unit.
   * @return <code>unit.times(1e3)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> KILO(Unit<Q> unit) {
    return unit.transform(KILO.getConverter());
  }

  /**
   * Returns the specified unit multiplied by the factor <code>10<sup>2</sup></code>
   *
   * @param <Q>
   *          The type of the quantity measured by the unit.
   * @param unit
   *          any unit.
   * @return <code>unit.times(1e2)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> HECTO(Unit<Q> unit) {
    return unit.transform(HECTO.getConverter());
  }

  /**
   * Returns the specified unit multiplied by the factor <code>10<sup>1</sup></code>
   *
   * @param <Q>
   *          The type of the quantity measured by the unit.
   * @param unit
   *          any unit.
   * @return <code>unit.times(1e1)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> DEKA(Unit<Q> unit) {
    return unit.transform(DEKA.getConverter());
  }

  /**
   * Returns the specified unit multiplied by the factor <code>10<sup>-1</sup></code>
   *
   * @param <Q>
   *          The type of the quantity measured by the unit.
   * @param unit
   *          any unit.
   * @return <code>unit.times(1e-1)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> DECI(Unit<Q> unit) {
    return unit.transform(DECI.getConverter());
  }

  /**
   * Returns the specified unit multiplied by the factor <code>10<sup>-2</sup></code>
   *
   * @param <Q>
   *          The type of the quantity measured by the unit.
   * @param unit
   *          any unit.
   * @return <code>unit.times(1e-2)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> CENTI(Unit<Q> unit) {
    return unit.transform(CENTI.getConverter());
  }

  /**
   * Returns the specified unit multiplied by the factor <code>10<sup>-3</sup></code>
   *
   * @param <Q>
   *          The type of the quantity measured by the unit.
   * @param unit
   *          any unit.
   * @return <code>unit.times(1e-3)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> MILLI(Unit<Q> unit) {
    return unit.transform(MILLI.getConverter());
  }

  /**
   * Returns the specified unit multiplied by the factor <code>10<sup>-6</sup></code>
   *
   * @param <Q>
   *          The type of the quantity measured by the unit.
   * @param unit
   *          any unit.
   * @return <code>unit.times(1e-6)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> MICRO(Unit<Q> unit) {
    return unit.transform(MICRO.getConverter());
  }

  /**
   * Returns the specified unit multiplied by the factor <code>10<sup>-9</sup></code>
   *
   * @param <Q>
   *          The type of the quantity measured by the unit.
   * @param unit
   *          any unit.
   * @return <code>unit.times(1e-9)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> NANO(Unit<Q> unit) {
    return unit.transform(NANO.getConverter());
  }

  /**
   * Returns the specified unit multiplied by the factor <code>10<sup>-12</sup></code>
   *
   * @param <Q>
   *          The type of the quantity measured by the unit.
   * @param unit
   *          any unit.
   * @return <code>unit.times(1e-12)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> PICO(Unit<Q> unit) {
    return unit.transform(PICO.getConverter());
  }

  /**
   * Returns the specified unit multiplied by the factor <code>10<sup>-15</sup></code>
   *
   * @param <Q>
   *          The type of the quantity measured by the unit.
   * @param unit
   *          any unit.
   * @return <code>unit.times(1e-15)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> FEMTO(Unit<Q> unit) {
    return unit.transform(FEMTO.getConverter());
  }

  /**
   * Returns the specified unit multiplied by the factor <code>10<sup>-18</sup></code>
   *
   * @param <Q>
   *          The type of the quantity measured by the unit.
   * @param unit
   *          any unit.
   * @return <code>unit.times(1e-18)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> ATTO(Unit<Q> unit) {
    return unit.transform(ATTO.getConverter());
  }

  /**
   * Returns the specified unit multiplied by the factor <code>10<sup>-21</sup></code>
   *
   * @param <Q>
   *          The type of the quantity measured by the unit.
   * @param unit
   *          any unit.
   * @return <code>unit.times(1e-21)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> ZEPTO(Unit<Q> unit) {
    return unit.transform(ZEPTO.getConverter());
  }

  /**
   * Returns the specified unit multiplied by the factor <code>10<sup>-24</sup></code>
   *
   * @param <Q>
   *          The type of the quantity measured by the unit.
   * @param unit
   *          any unit.
   * @return <code>unit.times(1e-24)</code>.
   */
  public static <Q extends Quantity<Q>> Unit<Q> YOCTO(Unit<Q> unit) {
    return unit.transform(YOCTO.getConverter());
  }
}
