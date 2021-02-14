/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2021, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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
import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.format.QuantityFormat;
import javax.measure.quantity.Dimensionless;

import static javax.measure.Quantity.Scale.ABSOLUTE;

import tech.units.indriya.format.SimpleQuantityFormat;
import tech.units.indriya.format.SimpleUnitFormat;
import tech.units.indriya.function.Calculus;
import tech.units.indriya.internal.function.ScaleHelper;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.spi.NumberSystem;
import tech.uom.lib.common.function.UnitSupplier;
import tech.uom.lib.common.function.ValueSupplier;

/**
 * <p>
 * This class represents the immutable result of a scalar quantity stated in a known unit.
 * </p>
 *
 * <p>
 * To avoid any loss of precision, known exact quantities (e.g. physical constants) should not be created from <code>double</code> constants but from
 * their decimal representation.<br>
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
 * <br>
 * // Complex number quantities.<br>
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
 * @author Andi Huber
 * @version 2.2, Feb 14, 2021
 * @since 1.0
 */
@SuppressWarnings("unchecked")
public abstract class AbstractQuantity<Q extends Quantity<Q>> implements ComparableQuantity<Q>, UnitSupplier<Q>, ValueSupplier<Number> {

   /**
    * 
    */
    private static final long serialVersionUID = 293852425369811882L;

    private final Unit<Q> unit;

    private final Scale scale;
    
    /**
     * Holds a dimensionless quantity of none (exact).
     */
    public static final Quantity<Dimensionless> NONE = Quantities.getQuantity(0, AbstractUnit.ONE);

    /**
     * Holds a dimensionless quantity of one (exact).
     */
    public static final Quantity<Dimensionless> ONE = Quantities.getQuantity(1, AbstractUnit.ONE);

    /**
     * Constructor.
     * @param unit a unit
     * @param sca the scale, absolute or relative
     */
    protected AbstractQuantity(Unit<Q> unit, Scale sca) {
        this.unit = unit;
        this.scale = sca;
    }

    /**
     * Constructor. Applies {@code ABSOLUTE} {@code Scale} if none was given.
     * @param unit a unit
     */
    protected AbstractQuantity(Unit<Q> unit) {
        this(unit, ABSOLUTE);
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
     * Returns the absolute or relative scale.
     *
     * @return the scale.
     */
    @Override
    public Scale getScale() {
        return scale;
    }

    /**
     * Returns this quantity after conversion to specified unit. The default implementation returns
     * <code>NumberQuantity.of(doubleValue(unit), unit)</code> . If this quantity is already stated in the specified unit, then this quantity is
     * returned and no conversion is performed.
     *
     * @param anotherUnit
     *            the unit in which the returned quantity is stated.
     * @return this quantity or a new quantity equivalent to this quantity stated in the specified unit.
     * @throws ArithmeticException
     *             if the result is inexact and the quotient has a non-terminating decimal expansion.
     */
    @Override
    public ComparableQuantity<Q> to(Unit<Q> anotherUnit) {
        if (anotherUnit.equals(this.getUnit())) {
            return this;
        }
        return ScaleHelper.convertTo(this, anotherUnit);
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
    public boolean isEquivalentTo(Quantity<Q> that) {
        return this.compareTo(that) == 0;
    }

    /**
     * 
     * FIXME[220] update java-doc
     * Compares this quantity to the specified quantity. The default implementation compares the {@link AbstractQuantity#doubleValue(Unit)}
     * of both this quantity and the specified quantity stated in the same unit (this quantity's {@link #getUnit() unit}).
     *
     * @return a negative integer, zero, or a positive integer as this quantity is less than, equal to, or greater than the specified Measurement
     *         quantity.
     * @see {@link tech.uom.lib.common.util.NaturalQuantityComparator NaturalQuantityComparator}
     */
    @Override
    public int compareTo(Quantity<Q> that) {
        if (this.getUnit().equals(that.getUnit())) {
            return numberSystem().compare(this.getValue(), that.getValue());
        }
        return numberSystem().compare(this.getValue(), that.to(this.getUnit()).getValue());
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
     *            the object to compare with.
     * @return <code>this.getUnit.equals(obj.getUnit())
     *         && this.getLevel().equals(obj.getLevel()
     *         && this.getValue().equals(obj.getValue())</code>
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Quantity<?>) {
            Quantity<?> that = (Quantity<?>) obj;
            return Objects.equals(getUnit(), that.getUnit()) && 
                    Objects.equals(getScale(), that.getScale()) && 
                    Objects.equals(getValue(), that.getValue());
        }
        return false;
    }

    /**
     * Returns the hash code for this quantity.
     *
     * @return the hash code value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getUnit(), getScale(), getValue());
    }

    /**
     * Returns the <code>String</code> representation of this quantity. The string produced for a given quantity is always the same; it is not
     * affected by locale. This means that it can be used as a canonical string representation for exchanging quantity, or as a key for a Hashtable,
     * etc. Locale-sensitive quantity formatting and parsing is handled by the {@link QuantityFormat} implementations and its subclasses.
     *
     * @return <code>SimpleQuantityFormat.getInstance().format(this)</code>
     */
    @Override
    public String toString() {
        return SimpleQuantityFormat.getInstance().format(this);
    }

    @Override
    public <T extends Quantity<T>, E extends Quantity<E>> ComparableQuantity<E> 
    divide(Quantity<T> that, Class<E> asTypeQuantity) {
        return divide(Objects.requireNonNull(that))
                .asType(Objects.requireNonNull(asTypeQuantity));
    }

    @Override
    public <T extends Quantity<T>, E extends Quantity<E>> ComparableQuantity<E> 
    multiply(Quantity<T> that, Class<E> asTypeQuantity) {
        return multiply(Objects.requireNonNull(that))
                .asType(Objects.requireNonNull(asTypeQuantity));
    }

    @Override
    public <T extends Quantity<T>> ComparableQuantity<T> inverse(Class<T> quantityClass) {
        return inverse().asType(quantityClass);
    }

    /**
     * Casts this quantity to a parameterized quantity of specified nature or throw a <code>ClassCastException</code> if the dimension of the
     * specified quantity and its unit's dimension do not match. For example:<br>
     * <code>
     *     Quantity<Length> length = AbstractQuantity.parse("2 km").asType(Length.class);
     * </code>
     *
     * @param type
     *            the quantity class identifying the nature of the quantity.
     * @return this quantity parameterized with the specified type.
     * @throws ClassCastException
     *             if the dimension of this unit is different from the specified quantity dimension.
     * @throws UnsupportedOperationException
     *             if the specified quantity class does not have a public static field named "UNIT" holding the SI unit for the quantity.
     * @see Unit#asType(Class)
     */
    public final <T extends Quantity<T>> ComparableQuantity<T> 
    asType(Class<T> type) throws ClassCastException {
        this.getUnit().asType(type); // ClassCastException if dimension mismatches.
        return (ComparableQuantity<T>) this;
    }

    /**
     * Returns the quantity of unknown type corresponding to the specified representation. This method can be used to parse dimensionless
     * quantities.<br>
     * <code>
     *     Quantity<Dimensionless> proportion = AbstractQuantity.parse("0.234").asType(Dimensionless.class);
     * </code>
     *
     * <p>
     * <b>Note:</b> This method handles only {@link SimpleUnitFormat#getStandard standard} unit format. Locale-sensitive or {@link tech.units.indriya.quantity.MixedQuantity mixed} quantity parsing is currently not
     * supported by this class. If you need flexible parsing of both single and mixed quantities, please use <code>getQuantity()</code> method of the {@link tech.units.indriya.quantity.Quantities Quantities} facade instead.
     * </p>
     *
     * @param csq
     *            the decimal value and its unit (if any) separated by space(s).
     * @return <code>SimpleQuantityFormat.getInstance().parse(csq)</code>
     */
    public static Quantity<?> parse(CharSequence csq) {
        return SimpleQuantityFormat.getInstance().parse(csq);
    }

    protected boolean hasFraction(double value) {
        return Math.round(value) != value;
    }

    protected boolean hasFraction(BigDecimal value) {
        return value.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0;
    }

    protected NumberSystem numberSystem() {
        return Calculus.currentNumberSystem();
    } 
}
