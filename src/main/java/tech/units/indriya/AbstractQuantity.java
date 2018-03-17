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
package tech.units.indriya;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Comparator;
import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.quantity.Dimensionless;

import tech.units.indriya.format.AbstractQuantityFormat;
import tech.units.indriya.function.NaturalQuantityComparator;
import tech.units.indriya.quantity.Quantities;
import tech.uom.lib.common.function.UnitSupplier;
import tech.uom.lib.common.function.ValueSupplier;

/**
 * <p>
 * This class represents the immutable result of a scalar measurement stated in a known unit.
 * </p>
 *
 * <p>
 * To avoid any loss of precision, known exact quantities (e.g. physical constants) should not be created from <code>double</code> constants but from
 * their decimal representation.<br/>
 * <code>
 *         public static final Quantity&lt;Velocity&gt; C = NumberQuantity.parse("299792458 m/s").asType(Velocity.class);
 *         // Speed of Light (exact).
 *    </code>
 * </p>
 * 
 * <p>
 * Quantities can be converted to different units.<br>
 * <code>
 *         Quantity&lt;Velocity&gt; milesPerHour = C.to(MILES_PER_HOUR); // Use double implementation (fast).
 *         System.out.println(milesPerHour);
 * 
 *         &gt; 670616629.3843951 m/h
 *     </code>
 * </p>
 * 
 * <p>
 * Applications may sub-class {@link AbstractQuantity} for particular quantity types.<br>
 * <code>
 *         // Quantity of type Mass based on double primitive types.<br>
 * public class MassAmount extends AbstractQuantity&lt;Mass&gt; {<br>
 * private final double kilograms; // Internal SI representation.<br>
 * private Mass(double kg) { kilograms = kg; }<br>
 * public static Mass of(double value, Unit&lt;Mass&gt; unit) {<br>
 * return new Mass(unit.getConverterTo(SI.KILOGRAM).convert(value));<br>
 * }<br>
 * public Unit&lt;Mass&gt; getUnit() { return SI.KILOGRAM; }<br>
 * public Double getValue() { return kilograms; }<br>
 * ...<br>
 * }<br>
 * <p>
 * // Complex numbers measurements.<br>
 * public class ComplexQuantity
 * &lt;Q extends Quantity&gt;extends AbstractQuantity
 * &lt;Q&gt;{<br>
 * public Complex getValue() { ... } // Assuming Complex is a Number.<br>
 * ...<br>
 * }<br>
 * <br>
 * // Specializations of complex numbers quantities.<br>
 * public final class Current extends ComplexQuantity&lt;ElectricCurrent&gt; {...}<br>
 * public final class Tension extends ComplexQuantity&lt;ElectricPotential&gt; {...} <br>
 * </code>
 * </p>
 * 
 * <p>
 * All instances of this class shall be immutable.
 * </p>
 *
 * @author <a href="mailto:werner@uom.technology">Werner Keil</a>
 * @version 1.1, March 12, 2018
 * @since 1.0
 */
@SuppressWarnings("unchecked")
public abstract class AbstractQuantity<Q extends Quantity<Q>> implements ComparableQuantity<Q>, UnitSupplier<Q>, ValueSupplier<Number> {

  /**
    * 
    */
  private static final long serialVersionUID = 293852425369811882L;

  private final Unit<Q> unit;

  /**
   * Holds a dimensionless quantity of none (exact).
   */
  public static final Quantity<Dimensionless> NONE = Quantities.getQuantity(0, AbstractUnit.ONE);

  /**
   * Holds a dimensionless quantity of one (exact).
   */
  public static final Quantity<Dimensionless> ONE = Quantities.getQuantity(1, AbstractUnit.ONE);

  /**
   * constructor.
   */
  protected AbstractQuantity(Unit<Q> unit) {
    this.unit = unit;
  }

  /**
   * Returns the numeric value of the quantity.
   *
   * @return the quantity value.
   */
  @Override
  public abstract Number getValue();

  /**
   * Returns the measurement unit.
   *
   * @return the measurement unit.
   */
  @Override
  public Unit<Q> getUnit() {
    return unit;
  }

  /**
   * Convenient method equivalent to {@link #to(javax.measure.Unit) to(this.getUnit().toSystemUnit())}.
   *
   * @return this quantity or a new quantity equivalent to this quantity stated in SI units.
   * @throws ArithmeticException
   *           if the result is inexact and the quotient has a non-terminating decimal expansion.
   */
  public Quantity<Q> toSystemUnit() {
    return to(this.getUnit().getSystemUnit());
  }

  /**
   * Returns this quantity after conversion to specified unit. The default implementation returns
   * <code>Measure.valueOf(doubleValue(unit), unit)</code> . If this quantity is already stated in the specified unit, then this quantity is returned
   * and no conversion is performed.
   *
   * @param unit
   *          the unit in which the returned measure is stated.
   * @return this quantity or a new quantity equivalent to this quantity stated in the specified unit.
   * @throws ArithmeticException
   *           if the result is inexact and the quotient has a non-terminating decimal expansion.
   */
  @Override
  public ComparableQuantity<Q> to(Unit<Q> unit) {
    if (unit.equals(this.getUnit())) {
      return this;
    }
    UnitConverter t = getUnit().getConverterTo(unit);
    Number convertedValue = t.convert(getValue());
    return Quantities.getQuantity(convertedValue, unit);
  }

  /**
   * Returns this measure after conversion to specified unit. The default implementation returns
   * <code>Measure.valueOf(decimalValue(unit, ctx), unit)</code>. If this measure is already stated in the specified unit, then this measure is
   * returned and no conversion is performed.
   *
   * @param unit
   *          the unit in which the returned measure is stated.
   * @param ctx
   *          the math context to use for conversion.
   * @return this measure or a new measure equivalent to this measure but stated in the specified unit.
   * @throws ArithmeticException
   *           if the result is inexact but the rounding mode is <code>UNNECESSARY</code> or <code>mathContext.precision == 0</code> and the quotient
   *           has a non-terminating decimal expansion.
   */
  public Quantity<Q> to(Unit<Q> unit, MathContext ctx) {
    if (unit.equals(this.getUnit())) {
      return this;
    }
    return Quantities.getQuantity(decimalValue(unit, ctx), unit);
  }

  @Override
  public boolean isGreaterThan(Quantity<Q> that) {
    return this.compareTo(that) > 0;
  }

  @Override
  public boolean isGreaterThanOrEqualTo(Quantity<Q> that) {
    return this.compareTo(that) >= 0;
  }

  @Override
  public boolean isLessThan(Quantity<Q> that) {
    return this.compareTo(that) < 0;
  }

  @Override
  public boolean isLessThanOrEqualTo(Quantity<Q> that) {
    return this.compareTo(that) <= 0;
  }

  @Override
  public boolean isEquivalentOf(Quantity<Q> that) {
    return this.compareTo(that) == 0;
  }

  /**
   * Compares this measure to the specified Measurement quantity. The default implementation compares the {@link AbstractQuantity#doubleValue(Unit)}
   * of both this measure and the specified Measurement stated in the same unit (this measure's {@link #getUnit() unit}).
   *
   * @return a negative integer, zero, or a positive integer as this measure is less than, equal to, or greater than the specified Measurement
   *         quantity.
   * @see {@link NaturalQuantityComparator}
   */
  @Override
  public int compareTo(Quantity<Q> that) {
    final Comparator<Quantity<Q>> comparator = new NaturalQuantityComparator<>();
    return comparator.compare(this, that);
  }

  /**
   * Compares this quantity against the specified object for <b>strict</b> equality (same unit and same amount).
   *
   * <p>
   * Similarly to the {@link BigDecimal#equals} method which consider 2.0 and 2.00 as different objects because of different internal scales,
   * quantities such as <code>Quantities.getQuantity(3.0, KILOGRAM)</code> <code>Quantities.getQuantity(3, KILOGRAM)</code> and
   * <code>Quantities.getQuantity("3 kg")</code> might not be considered equals because of possible differences in their implementations.
   * </p>
   *
   * <p>
   * To compare quantities stated using different units or using different amount implementations the {@link #compareTo compareTo} or
   * {@link #equals(javax.measure.Quantity, double, javax.measure.unit.Unit) equals(Quantity, epsilon, epsilonUnit)} methods should be used.
   * </p>
   *
   * @param obj
   *          the object to compare with.
   * @return <code>this.getUnit.equals(obj.getUnit())
   *         && this.getValue().equals(obj.getValue())</code>
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof AbstractQuantity<?>) {
      AbstractQuantity<?> that = (AbstractQuantity<?>) obj;
      return Objects.equals(getUnit(), that.getUnit()) && Objects.equals(getValue(), that.getValue());
    }
    return false;
  }

  /**
   * Compares this quantity and the specified quantity to the given accuracy. Quantities are considered approximately equals if their absolute
   * differences when stated in the same specified unit is less than the specified epsilon.
   *
   * @param that
   *          the quantity to compare with.
   * @param epsilon
   *          the absolute error stated in epsilonUnit.
   * @param epsilonUnit
   *          the epsilon unit.
   * @return <code>abs(this.doubleValue(epsilonUnit) - that.doubleValue(epsilonUnit)) &lt;= epsilon</code>
   */
  public boolean equals(AbstractQuantity<Q> that, double epsilon, Unit<Q> epsilonUnit) {
    return Math.abs(this.doubleValue(epsilonUnit) - that.doubleValue(epsilonUnit)) <= epsilon;
  }

  /**
   * Returns the hash code for this quantity.
   *
   * @return the hash code value.
   */
  @Override
  public int hashCode() {
    return Objects.hash(getUnit(), getValue());
  }

  public abstract boolean isBig();

  /**
   * Returns the <code>String</code> representation of this quantity. The string produced for a given quantity is always the same; it is not affected
   * by locale. This means that it can be used as a canonical string representation for exchanging quantity, or as a key for a Hashtable, etc.
   * Locale-sensitive quantity formatting and parsing is handled by the {@link MeasurementFormat} class and its subclasses.
   *
   * @return <code>UnitFormat.getInternational().format(this)</code>
   */
  @Override
  public String toString() {
    return String.valueOf(getValue()) + " " + String.valueOf(getUnit());
  }

  public abstract BigDecimal decimalValue(Unit<Q> unit, MathContext ctx) throws ArithmeticException;

  public abstract double doubleValue(Unit<Q> unit) throws ArithmeticException;

  public final int intValue(Unit<Q> unit) throws ArithmeticException {
    long longValue = longValue(unit);
    if ((longValue < Integer.MIN_VALUE) || (longValue > Integer.MAX_VALUE)) {
      throw new ArithmeticException("Cannot convert " + longValue + " to int (overflow)");
    }
    return (int) longValue;
  }

  protected long longValue(Unit<Q> unit) throws ArithmeticException {
    double result = doubleValue(unit);
    if ((result < Long.MIN_VALUE) || (result > Long.MAX_VALUE)) {
      throw new ArithmeticException("Overflow (" + result + ")");
    }
    return (long) result;
  }

  protected final float floatValue(Unit<Q> unit) {
    return (float) doubleValue(unit);
  }

  @Override
  public <T extends Quantity<T>, E extends Quantity<E>> ComparableQuantity<E> divide(Quantity<T> that, Class<E> asTypeQuantity) {

    return divide(Objects.requireNonNull(that)).asType(Objects.requireNonNull(asTypeQuantity));

  }

  @Override
  public <T extends Quantity<T>, E extends Quantity<E>> ComparableQuantity<E> multiply(Quantity<T> that, Class<E> asTypeQuantity) {
    return multiply(Objects.requireNonNull(that)).asType(Objects.requireNonNull(asTypeQuantity));
  }

  @Override
  public <T extends Quantity<T>> ComparableQuantity<T> inverse(Class<T> quantityClass) {
    return inverse().asType(quantityClass);
  }

  /**
   * Casts this quantity to a parameterized quantity of specified nature or throw a <code>ClassCastException</code> if the dimension of the specified
   * quantity and its unit's dimension do not match. For example:<br/>
   * <code>
   *     Quantity<Length> length = AbstractQuantity.parse("2 km").asType(Length.class);
   * </code>
   *
   * @param type
   *          the quantity class identifying the nature of the quantity.
   * @return this quantity parameterized with the specified type.
   * @throws ClassCastException
   *           if the dimension of this unit is different from the specified quantity dimension.
   * @throws UnsupportedOperationException
   *           if the specified quantity class does not have a public static field named "UNIT" holding the SI unit for the quantity.
   * @see Unit#asType(Class)
   */
  public final <T extends Quantity<T>> ComparableQuantity<T> asType(Class<T> type) throws ClassCastException {
    this.getUnit().asType(type); // Raises ClassCastException if dimension
    // mismatches.
    return (ComparableQuantity<T>) this;
  }

  /**
   * Returns the quantity of unknown type corresponding to the specified representation. This method can be used to parse dimensionless quantities.<br/>
   * <code>
   *     Quatity<Dimensionless> proportion = AbstractQuantity.parse("0.234").asType(Dimensionless.class);
   * </code>
   *
   * <p>
   * Note: This method handles only {@link SimpleUnitFormat#getStandard standard} unit format. Locale-sensitive quantity parsing is currently not
   * supported.
   * </p>
   *
   * @param csq
   *          the decimal value and its unit (if any) separated by space(s).
   * @return <code>QuantityFormat.getInstance().parse(csq)</code>
   */
  public static Quantity<?> parse(CharSequence csq) {
    return AbstractQuantityFormat.getInstance().parse(csq);
  }

  protected boolean hasFraction(double value) {
    return Math.round(value) != value;
  }

  protected boolean hasFraction(BigDecimal value) {
    return value.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0;
  }

  /**
   * Utility class for number comparison and equality
   */
  protected static final class Equalizer {

    /**
     * Converts a number to {@link BigDecimal}
     *
     * @param value
     *          the value to be converted
     * @return the value converted
     */
    public static BigDecimal toBigDecimal(Number value) {
      if (BigDecimal.class.isInstance(value)) {
        return BigDecimal.class.cast(value);
      } else if (BigInteger.class.isInstance(value)) {
        return new BigDecimal(BigInteger.class.cast(value));
      }
      return BigDecimal.valueOf(value.doubleValue());
    }

    /**
     * Converts a number to {@link BigInteger}
     *
     * @param value
     *          the value to be converted
     * @return the value converted
     */
    public static BigInteger toBigInteger(Number value) {
      if (BigInteger.class.isInstance(value)) {
        return BigInteger.class.cast(value);
      } else if (BigDecimal.class.isInstance(value)) {
        return (BigDecimal.class.cast(value)).toBigInteger();
      }
      return BigInteger.valueOf(value.longValue());
    }

    /**
     * Check if the both value has equality number, in other words, 1 is equals to 1.0000 and 1.0.
     * 
     * If the first value is a <type>Number</type> of either <type>Double</type>, <type>Float</type>, <type>Integer</type>, <type>Long</type>,
     * <type>Short</type> or <type>Byte</type> it is compared using the respective <code>*value()</code> method of <type>Number</type>. Otherwise it
     * is checked, if {@link BigDecimal#compareTo(Object)} is equal to zero.
     *
     * @param valueA
     *          the value a
     * @param valueB
     *          the value B
     * @return {@link BigDecimal#compareTo(Object)} == zero
     */
    public static boolean hasEquality(Number valueA, Number valueB) {
      Objects.requireNonNull(valueA);
      Objects.requireNonNull(valueB);

      if (valueA instanceof Double && valueB instanceof Double) {
        return valueA.doubleValue() == valueB.doubleValue();
      } else if (valueA instanceof Float && valueB instanceof Float) {
        return valueA.floatValue() == valueB.floatValue();
      } else if (valueA instanceof Integer && valueB instanceof Integer) {
        return valueA.intValue() == valueB.intValue();
      } else if (valueA instanceof Long && valueB instanceof Long) {
        return valueA.longValue() == valueB.longValue();
      } else if (valueA instanceof Short && valueB instanceof Short) {
        return valueA.shortValue() == valueB.shortValue();
      } else if (valueA instanceof Byte && valueB instanceof Byte) {
        return valueA.byteValue() == valueB.byteValue();
      }
      return toBigDecimal(valueA).compareTo(toBigDecimal(valueB)) == 0;
    }
  }
}
