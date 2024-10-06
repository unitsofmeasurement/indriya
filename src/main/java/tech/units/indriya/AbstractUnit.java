/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2024, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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

import static javax.measure.Quantity.Scale.ABSOLUTE;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.measure.Dimension;
import javax.measure.IncommensurableException;
import javax.measure.Prefix;
import javax.measure.Quantity;
import javax.measure.UnconvertibleException;
import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.Quantity.Scale;
import javax.measure.format.MeasurementParseException;
import javax.measure.quantity.Dimensionless;

import org.apiguardian.api.API;

import tech.units.indriya.format.LocalUnitFormat;
import tech.units.indriya.format.SimpleUnitFormat;
import tech.units.indriya.function.AbstractConverter;
import tech.units.indriya.function.AddConverter;
import tech.units.indriya.function.Calculus;
import tech.units.indriya.function.MultiplyConverter;
import tech.units.indriya.function.RationalNumber;
import tech.units.indriya.internal.function.Calculator;
import tech.units.indriya.spi.DimensionalModel;
import tech.units.indriya.unit.AlternateUnit;
import tech.units.indriya.unit.AnnotatedUnit;
import tech.units.indriya.unit.ProductUnit;
import tech.units.indriya.unit.TransformedUnit;
import tech.units.indriya.unit.UnitDimension;
import tech.units.indriya.unit.Units;
import tech.uom.lib.common.function.Nameable;
import tech.uom.lib.common.function.PrefixOperator;
import tech.uom.lib.common.function.SymbolSupplier;

/**
 * <p>
 * The class represents units founded on the seven <b>SI</b> base units for
 * seven base quantities assumed to be mutually independent.
 * </p>
 *
 * <p>
 * For all physics units, unit conversions are symmetrical:
 * <code>u1.getConverterTo(u2).equals(u2.getConverterTo(u1).inverse())</code>.
 * Non-physical units (e.g. currency units) for which conversion is not
 * symmetrical should have their own separate class hierarchy and are considered
 * distinct (e.g. financial units), although they can always be combined with
 * physics units (e.g. "€/Kg", "$/h").
 * </p>
 *
 * @see <a href=
 *      "http://en.wikipedia.org/wiki/International_System_of_Units">Wikipedia:
 *      International System of Units</a>
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:werner@units.tech">Werner Keil</a>
 * @version 4.1, October 4, 2024
 * @since 1.0
 */
public abstract class AbstractUnit<Q extends Quantity<Q>>
		implements Unit<Q>, Comparable<Unit<Q>>, PrefixOperator<Q>, Nameable, Serializable, SymbolSupplier {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4344589505537030204L;

	/**
	 * Holds the dimensionless unit <code>ONE</code>.
	 * 
	 * @see <a href=
	 *      "https://en.wikipedia.org/wiki/Natural_units#Choosing_constants_to_normalize">
	 *      Wikipedia: Natural Units - Choosing constants to normalize</a>
	 * @see <a href= "http://www.av8n.com/physics/dimensionless-units.htm">Units of
	 *      Dimension One</a>
	 */
	public static final Unit<Dimensionless> ONE = new ProductUnit<>();

	/**
	 * Holds the name.
	 */
	private String name;

	/**
	 * Holds the symbol.
	 */
	private String symbol;
	
    /**
     * Holds the measurement scale
     */
	protected Scale scale = ABSOLUTE;

	/**
	 * Holds the unique symbols collection (base units or alternate units).
	 */
	protected static final transient Map<String, Unit<?>> SYMBOL_TO_UNIT = new HashMap<>();

	/**
	 * Default constructor.
	 */
	protected AbstractUnit() {
	}

	/**
	 * Constructor setting a symbol.
	 * 
	 * @param symbol the unit symbol.
	 */
	protected AbstractUnit(String symbol) {
		this.symbol = symbol;
	}

	protected Type getActualType() {
		ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
		return parameterizedType.getActualTypeArguments()[0].getClass().getGenericInterfaces()[0];
	}

	/**
	 * Indicates if this unit belongs to the set of coherent SI units (unscaled SI
	 * units).
	 * 
	 * The base and coherent derived units of the SI form a coherent set, designated
	 * the set of coherent SI units. The word coherent is used here in the following
	 * sense: when coherent units are used, equations between the numerical values
	 * of quantities take exactly the same form as the equations between the
	 * quantities themselves. Thus if only units from a coherent set are used,
	 * conversion factors between units are never required.
	 * 
	 * @return <code>equals(toSystemUnit())</code>
	 */
	public boolean isSystemUnit() {
		Unit<Q> sys = this.toSystemUnit();
		return this == sys || this.equals(sys);
	}
	
	/**
	 * Returns the converter from this unit to its unscaled {@link #toSystemUnit
	 * System Unit} unit.
	 *
	 * @return <code>getConverterTo(this.toSystemUnit())</code>
	 * @see #toSystemUnit
	 */
	public abstract UnitConverter getSystemConverter();
	
	/**
	 * Returns the unscaled {@link SI} unit from which this unit is derived.
	 * 
	 * The SI unit can be be used to identify a quantity given the unit. For
	 * example:<code> static boolean isAngularVelocity(AbstractUnit<?> unit) {
	 * return unit.toSystemUnit().equals(RADIAN.divide(SECOND)); } assert(REVOLUTION.divide(MINUTE).isAngularVelocity()); // Returns true. </code>
	 *
	 * @return the unscaled metric unit from which this unit is derived.
	 */
	protected abstract Unit<Q> toSystemUnit();

	/**
	 * Annotates the specified unit. Annotation does not change the unit semantic.
	 * Annotations are often written between curly braces behind units. For
	 * example:<br>
	 * <code> Unit<Volume> PERCENT_VOL = ((AbstractUnit)Units.PERCENT).annotate("vol"); // "%{vol}" Unit<Mass> KG_TOTAL =
	 * ((AbstractUnit)Units.KILOGRAM).annotate("total"); // "kg{total}" Unit<Dimensionless> RED_BLOOD_CELLS = ((AbstractUnit)Units.ONE).annotate("RBC"); // "{RBC}" </code>
	 *
	 * Note: Annotation of system units are not considered themselves as system
	 * units.
	 *
	 * @param annotation the unit annotation.
	 * @return the annotated unit.
	 */
    public final Unit<Q> annotate(String annotation) {
      return new AnnotatedUnit<>(this, annotation);
    }

	/**
	 * Returns the abstract unit represented by the specified characters as per
	 * default format.
	 *
	 * Locale-sensitive unit parsing could be handled using {@link LocalUnitFormat}
	 * in subclasses of AbstractUnit.
	 *
	 * <p>
	 * Note: The standard format supports dimensionless
	 * units.<code> AbstractUnit<Dimensionless> PERCENT =
	 * AbstractUnit.parse("100").inverse().asType(Dimensionless.class); </code>
	 * </p>
	 *
	 * @param charSequence the character sequence to parse.
	 * @return <code>SimpleUnitFormat.getInstance().parse(csq)</code>
	 * @throws MeasurementParseException if the specified character sequence cannot
	 *                                   be correctly parsed (e.g. not UCUM
	 *                                   compliant).
	 */
	public static Unit<?> parse(CharSequence charSequence) {
		return SimpleUnitFormat.getInstance().parse(charSequence);
	}

	/**
	 * Returns the standard representation of this physics unit. The string produced
	 * for a given unit is always the same; it is not affected by the locale. It can
	 * be used as a canonical string representation for exchanging units, or as a
	 * key for a Hashtable, etc.
	 *
	 * Locale-sensitive unit parsing could be handled using {@link LocalUnitFormat}
	 * in subclasses of AbstractUnit.
	 *
	 * @return <code>SimpleUnitFormat.getInstance().format(this)</code>
	 */
	@Override
	public String toString() {
		return SimpleUnitFormat.getInstance().format(this);
	}

	// ///////////////////////////////////////////////////////
	// Implements javax.measure.Unit<Q> interface //
	// ///////////////////////////////////////////////////////

	/**
	 * Returns the system unit (unscaled SI unit) from which this unit is derived.
	 * They can be be used to identify a quantity given the unit. For example:<br>
	 * <code> static boolean isAngularVelocity(AbstractUnit<?> unit) {<br>&nbsp;&nbsp;return unit.getSystemUnit().equals(RADIAN.divide(SECOND));<br>}
	 * <br>assert(REVOLUTION.divide(MINUTE).isAngularVelocity()); // Returns true. </code>
	 *
	 * @return the unscaled metric unit from which this unit is derived.
	 */
	@Override
	public final Unit<Q> getSystemUnit() {
		return toSystemUnit();
	}

	/**
	 * Indicates if this unit is compatible with the unit specified. To be
	 * compatible both units must be physics units having the same fundamental
	 * dimension.
	 *
	 * @param that the other unit.
	 * @return <code>true</code> if this unit and that unit have the same
	 *         fundamental dimension according to the current dimensional model;
	 *         <code>false</code> otherwise.
	 */
	@Override
	public final boolean isCompatible(Unit<?> that) {
		return internalIsCompatible(that, true);
	}

	/**
	 * Casts this unit to a parameterized unit of specified nature or throw a
	 * ClassCastException if the dimension of the specified quantity and this unit's
	 * dimension do not match (regardless whether or not the dimensions are
	 * independent or not).
	 *
	 * @param type the quantity class identifying the nature of the unit.
	 * @throws ClassCastException if the dimension of this unit is different from
	 *                            the SI dimension of the specified type.
	 * @see Units#getUnit(Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public final <T extends Quantity<T>> Unit<T> asType(Class<T> type) {
		Dimension typeDimension = UnitDimension.of(type);
		if (typeDimension != null && !typeDimension.equals(this.getDimension()))
			throw new ClassCastException("The unit: " + this + " is not compatible with quantities of type " + type);
		return (Unit<T>) this;
	}

	@Override
	public abstract Map<? extends Unit<?>, Integer> getBaseUnits();

	@Override
	public abstract Dimension getDimension();

	protected void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getSymbol() {
		return symbol;
	}

	protected void setSymbol(String s) {
		this.symbol = s;
	}

	@Override
	public final UnitConverter getConverterTo(Unit<Q> that) throws UnconvertibleException {
		return internalGetConverterTo(that, true);
	}

	@Override
	public final UnitConverter getConverterToAny(Unit<?> that) throws IncommensurableException, UnconvertibleException {
		return getConverterToAny(that, ABSOLUTE);
	}
	
   /**
     * Returns a converter of numeric values from this unit to another unit of same type. This method performs the same work as
     * {@link #getConverterToAny(Unit)} without raising checked exception.
     *
     * @param that
     *          the unit of same type to which to convert the numeric values.
     * @param scale the measurement scale.          
     * @return the converter from this unit to {@code that} unit in the given {@code scale}.
     * @throws UnconvertibleException
     *           if a converter cannot be constructed.
     *
     * @see #getConverterToAny(Unit)
     */
	@API(status=EXPERIMENTAL)
	public final UnitConverter getConverterTo(Unit<Q> that, Scale scale) throws UnconvertibleException {
		this.scale = scale;
		return getConverterTo(that);
	}
	
	/**
     * Returns a converter from this unit to the specified unit of type unknown in the given scale. This method can be used when the quantity type of the specified unit is
     * unknown at compile-time or when dimensional analysis allows for conversion between units of different type.
     *
     * <p>
     * To convert to a unit having the same parameterized type, {@link #getConverterTo(Unit, Scale)} is preferred (no checked exception raised).
     * </p>
     *
     * @param that
     *          the unit to which to convert the numeric values.
     * @param scale the measurement scale.
     * @return the converter from {@code this} unit to {@code that} unit using the given {@code scale}.
     * @throws IncommensurableException
     *           if this unit is not {@linkplain #isCompatible(Unit) compatible} with {@code that}.
     * @throws UnconvertibleException
     *           if a converter cannot be constructed.
     *
     * @see #getConverterTo(Unit)
     * @see #isCompatible(Unit)
     */
	@API(status=EXPERIMENTAL)
	@SuppressWarnings("rawtypes")
	public final UnitConverter getConverterToAny(Unit<?> that, Scale scale) throws IncommensurableException, UnconvertibleException {
		if (!isCompatible(that))
			throw new IncommensurableException(this + " is not compatible with " + that);
		this.scale = scale;
		final AbstractUnit thatAbstr = (AbstractUnit) that; // Since both units are
		// compatible they must both be abstract units.
		final DimensionalModel model = DimensionalModel.current();
		Unit thisSystemUnit = this.getSystemUnit();
		UnitConverter thisToDimension = model.getDimensionalTransform(thisSystemUnit.getDimension())
				.concatenate(this.getSystemConverter());
		Unit thatSystemUnit = thatAbstr.getSystemUnit();
		UnitConverter thatToDimension = model.getDimensionalTransform(thatSystemUnit.getDimension())
				.concatenate(thatAbstr.getSystemConverter());
		return thatToDimension.inverse().concatenate(thisToDimension);
	}

	@Override
	public final Unit<Q> alternate(String newSymbol) {
		return new AlternateUnit<>(this, newSymbol);
	}

	@Override
	public final Unit<Q> transform(UnitConverter operation) {
		Unit<Q> systemUnit = this.getSystemUnit();
		UnitConverter cvtr;
		if (this.isSystemUnit()) {
			cvtr = this.getSystemConverter().concatenate(operation);
		} else {
			cvtr = operation;
		}
		return cvtr.isIdentity() ? systemUnit : new TransformedUnit<>(null, this, systemUnit, cvtr);
	}

	@Override
	public final Unit<Q> shift(Number offset) {
		if (Calculus.currentNumberSystem().isZero(offset))
			return this;
		return transform(new AddConverter(offset));
	}

	@Override
	public final Unit<Q> multiply(Number factor) {
		if (Calculus.currentNumberSystem().isOne(factor))
			return this;
		return transform(MultiplyConverter.of(factor));
	}

	@Override
	public Unit<Q> shift(double offset) {
		return shift(RationalNumber.of(offset));
	}

	@Override
	public Unit<Q> multiply(double multiplier) {
		return multiply(RationalNumber.of(multiplier));
	}

	@Override
	public Unit<Q> divide(double divisor) {
		return divide(RationalNumber.of(divisor));
	}
	
	/**
	 * Internal helper for isCompatible
	 */
	private final boolean internalIsCompatible(Unit<?> that, boolean checkEquals) {
		if (checkEquals) {
			if (this == that || this.equals(that))
				return true;
		} else {
			if (this == that)
				return true;
		}
		if (!(that instanceof Unit))
			return false;
		Dimension thisDimension = this.getDimension();
		Dimension thatDimension = that.getDimension();
		if (thisDimension.equals(thatDimension))
			return true;
		DimensionalModel model = DimensionalModel.current(); // Use
		// dimensional
		// analysis
		// model.
		return model.getFundamentalDimension(thisDimension).equals(model.getFundamentalDimension(thatDimension));
	}

	protected final UnitConverter internalGetConverterTo(Unit<Q> that, boolean useEquals)
			throws UnconvertibleException {
		if (useEquals) {
			if (this == that || this.equals(that))
				return AbstractConverter.IDENTITY;
		} else {
			if (this == that)
				return AbstractConverter.IDENTITY;
		}
		Unit<Q> thisSystemUnit = this.getSystemUnit();
		Unit<Q> thatSystemUnit = that.getSystemUnit();
		if (!thisSystemUnit.equals(thatSystemUnit))
			try {
				return getConverterToAny(that);
			} catch (IncommensurableException e) {
				throw new UnconvertibleException(e);
			}
		UnitConverter thisToSI = this.getSystemConverter();
		UnitConverter thatToSI = that.getConverterTo(thatSystemUnit);
		return thatToSI.inverse().concatenate(thisToSI);
	}

	/**
	 * Returns the product of this physical unit with the one specified.
	 *
	 * @param that the physical unit multiplicand.
	 * @return <code>this * that</code>
	 */
	public final Unit<?> multiply(Unit<?> that) {
		if (this.equals(ONE))
			return that;
		if (that.equals(ONE))
			return this;
		return ProductUnit.ofProduct(this, that);
	}

	/**
	 * Returns the inverse of this physical unit.
	 *
	 * @return <code>1 / this</code>
	 */
	@Override
	public final Unit<?> inverse() {
		if (this.equals(ONE))
			return this;
		return ProductUnit.ofQuotient(ONE, this);
	}

	/**
	 * Returns the result of dividing this unit by the specified divisor. If the
	 * factor is an integer value, the division is exact. For example:
	 * 
	 * <pre>
	 * <code>
	 *    QUART = GALLON_LIQUID_US.divide(4); // Exact definition.
	 * </code>
	 * </pre>
	 * 
	 * @param divisor the divisor value.
	 * @return this unit divided by the specified divisor.
	 */
	@Override
	public final Unit<Q> divide(Number divisor) {
	    if (Calculus.currentNumberSystem().isOne(divisor))
			return this;
	    Number factor = Calculator.of(divisor).reciprocal().peek(); 
		return transform(MultiplyConverter.of(factor));
	}

	/**
	 * Returns the quotient of this unit with the one specified.
	 *
	 * @param that the unit divisor.
	 * @return <code>this.multiply(that.inverse())</code>
	 */
	@Override
	public final Unit<?> divide(Unit<?> that) {
		return this.multiply(that.inverse());
	}

	/**
	 * Returns a unit equals to the given root of this unit.
	 *
	 * @param n the root's order.
	 * @return the result of taking the given root of this unit.
	 * @throws ArithmeticException if <code>n == 0</code> or if this operation would
	 *                             result in an unit with a fractional exponent.
	 */
	@Override
	public final Unit<?> root(int n) {
		if (n > 0)
			return ProductUnit.ofRoot(this, n);
		else if (n == 0)
			throw new ArithmeticException("Root's order of zero");
		else
			// n < 0
			return ONE.divide(this.root(-n));
	}

	/**
	 * Returns a unit equals to this unit raised to an exponent.
	 *
	 * @param n the exponent.
	 * @return the result of raising this unit to the exponent.
	 */
	@Override
	public Unit<?> pow(int n) {
		if (n > 0)
			return this.multiply(this.pow(n - 1));
		else if (n == 0)
			return ONE;
		else
			// n < 0
			return ONE.divide(this.pow(-n));
	}

	@Override
	public Unit<Q> prefix(Prefix prefix) {
		return this.transform(MultiplyConverter.ofPrefix(prefix));
	}
	
	/**
	 * Compares this unit to the specified unit. The default implementation compares
	 * the name and symbol of both this unit and the specified unit, giving
	 * precedence to the symbol.
	 *
	 * @return a negative integer, zero, or a positive integer as this unit is less
	 *         than, equal to, or greater than the specified unit.
	 */
	@Override
	public int compareTo(Unit<Q> that) {
		int symbolComparison = compareToWithPossibleNullValues(getSymbol(), that.getSymbol());
		if (symbolComparison == 0) {
			return compareToWithPossibleNullValues(name, that.getName());
		} else {
			return symbolComparison;
		}
	}

	private int compareToWithPossibleNullValues(String a, String b) {
		if (a == null) {
			return (b == null) ? 0 : -1;
		} else {
			return (b == null) ? 1 : a.compareTo(b);
		}
	}

	@Override
	public boolean isEquivalentTo(Unit<Q> that) {
		return this.getConverterTo(that).isIdentity();
	}

	// //////////////////////////////////////////////////////////////
	// Ensures that sub-classes implement the hashCode method.
	// //////////////////////////////////////////////////////////////

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public abstract int hashCode();

	/**
	 * Utility class for number comparison and equality
	 */
	protected static final class Equalizer {
		/**
		 * Indicates if this unit is considered equals to the specified object. order).
		 *
		 * @param obj the object to compare for equality.
		 * @return <code>true</code> if <code>this</code> and <code>obj</code> are
		 *         considered equal; <code>false</code>otherwise.
		 */
		public static boolean areEqual(@SuppressWarnings("rawtypes") Unit u1,
				@SuppressWarnings("rawtypes") Unit u2) {
			/*
			 * if (u1 != null && u2 != null) { if (u1.getName() != null && u1.getSymbol() !=
			 * null) { return u1.getName().equals(u2.getName()) &&
			 * u1.getSymbol().equals(u2.getSymbol()) && u1.internalIsCompatible(u2, false);
			 * } else if (u1.getSymbol() != null) { return
			 * u1.getSymbol().equals(u2.getSymbol()) && u1.internalIsCompatible(u2, false);
			 * } else { return u1.toString().equals(u2.toString()) &&
			 * u1.internalIsCompatible(u2, false); } } else {
			 */
			if (u1 != null && u1.equals(u2))
				return true;
			return false;
		}
	}
}
