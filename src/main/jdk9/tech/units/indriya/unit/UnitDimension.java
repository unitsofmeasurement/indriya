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
package tech.units.indriya.unit;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.Unit;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.unit.BaseUnit;
import tech.units.indriya.unit.ProductUnit;
import tech.units.indriya.unit.Units;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * This class represents a dimension of a unit of measurement.
 * </p>
 *
 * <p>
 * The dimension associated to any given quantity are given by the published
 * {@link Dimension} instances. For convenience, a static method
 * <code>UnitDimension.of(Class)</code> aggregating the results of all
 *
 * {@link Dimension} instances is provided.<br>
 * <br>
 * <code>
 *        Dimension speedDimension
 *            = UnitDimension.of(Speed.class);
 *     </code>
 * </p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:werner@units.tech">Werner Keil</a>
 * @author  Martin Desruisseaux (Geomatys)
 * @author  Andi Huber
 * @version 2.0, $Date: 2021-03-12 $
 * @since 2.0
 */
public class UnitDimension implements Dimension, Serializable {
	/**	*/
	private static final long serialVersionUID = 7806787530512644696L;

	private static final Logger LOGGER = System.getLogger(UnitDimension.class.getPackage().getName());

	/**
	 * Holds dimensionless.
	 *
	 * @since 1.0
	 */
	public static final Dimension NONE = new UnitDimension(AbstractUnit.ONE);

	/**
	 * Holds length dimension (L).
	 *
	 * @since 1.0
	 */
	public static final Dimension LENGTH = new UnitDimension('L');

	/**
	 * Holds mass dimension (M).
	 *
	 * @since 1.0
	 */
	public static final Dimension MASS = new UnitDimension('M');

	/**
	 * Holds time dimension (T).
	 *
	 * @since 1.0
	 */
	public static final Dimension TIME = new UnitDimension('T');

	/**
	 * Holds electric current dimension (I).
	 *
	 * @since 1.0
	 */
	public static final Dimension ELECTRIC_CURRENT = new UnitDimension('I');

	/**
	 * Holds temperature dimension (Θ).
	 *
	 * @since 1.0
	 */
	public static final Dimension TEMPERATURE = new UnitDimension('\u0398');

	/**
	 * Holds amount of substance dimension (N).
	 *
	 * @since 1.0
	 */
	public static final Dimension AMOUNT_OF_SUBSTANCE = new UnitDimension('N');

	/**
	 * Holds luminous intensity dimension (J).
	 */
	public static final Dimension LUMINOUS_INTENSITY = new UnitDimension('J');

	/**
	 * Holds the pseudo unit associated to this dimension.
	 */
	private final Unit<?> pseudoUnit;

	/**
	 * Returns the dimension for the specified quantity type by aggregating the
	 * results from the default {@link javax.measure.spi.SystemOfUnits SystemOfUnits} or <code>null</code> if the specified
	 * quantity is unknown.
	 *
	 * @param quantityType the quantity type.
	 * @return the dimension for the quantity type or <code>null</code>.
	 * @since 1.1
	 */
	public static <Q extends Quantity<Q>> Dimension of(Class<Q> quantityType) {
		// TODO: Track services and aggregate results (register custom types)
		Unit<Q> siUnit = Units.getInstance().getUnit(quantityType);
		if (siUnit == null) {
			if (LOGGER.isLoggable(Level.DEBUG)) {
				LOGGER.log(Level.DEBUG, "Quantity type: " + quantityType + " unknown");
			}
		}
		return (siUnit != null) ? siUnit.getDimension() : null;
	}

	/**
	 * Returns the dimension for the specified symbol.
	 *
	 * @param sambol the quantity symbol.
	 * @return the dimension for the given symbol.
	 * @since 1.0.1
	 */
	public static Dimension parse(char symbol) {
		return new UnitDimension(symbol);
	}

	/**
	 * Returns the unit dimension having the specified symbol.
	 *
	 * @param symbol the associated symbol.
	 */
	@SuppressWarnings("rawtypes")
	private UnitDimension(char symbol) {
		pseudoUnit = new BaseUnit("[" + symbol + ']', NONE);
	}

	/**
	 * Constructor from pseudo-unit (not visible).
	 *
	 * @param pseudoUnit the pseudo-unit.
	 */
	private UnitDimension(Unit<?> pseudoUnit) {
		this.pseudoUnit = pseudoUnit;
	}

	/**
	 * Default Constructor (not visible).
	 *
	 */
	protected UnitDimension() {
		this(AbstractUnit.ONE);
	}

	/**
	 * Returns the product of this dimension with the one specified. If the
	 * specified dimension is not a <code>UnitDimension</code>, then
	 * <code>that.multiply(this)</code> is returned.
	 *
	 * @param that the dimension multiplicand.
	 * @return <code>this * that</code>
	 * @since 1.0
	 */
	public Dimension multiply(Dimension that) {
		return that instanceof UnitDimension
		        ? this.multiply((UnitDimension) that)
                : that.multiply(this);
	}

	/**
	 * Returns the product of this dimension with the one specified.
	 *
	 * @param that the dimension multiplicand.
	 * @return <code>this * that</code>
	 * @since 1.0
	 */
	private UnitDimension multiply(UnitDimension that) {
		return new UnitDimension(this.pseudoUnit.multiply(that.pseudoUnit));
	}

	/**
	 * Returns the quotient of this dimension with the one specified.
	 * If the specified dimension is not a <code>UnitDimension</code>, then
     * <code>that.divide(this).pow(-1)</code> is returned.
	 *
	 * @param that the dimension divisor.
	 * @return <code>that / this</code>
	 * @since 1.0
	 */
	public Dimension divide(Dimension that) {
		return that instanceof UnitDimension ? this.divide((UnitDimension) that) : this.divide(that);
	}

	/**
	 * Returns the quotient of this dimension with the one specified.
	 *
	 * @param that the dimension divisor.
	 * @return <code>that / this</code>
	 * @since 1.0
	 */
	private UnitDimension divide(UnitDimension that) {
		return new UnitDimension(ProductUnit.ofQuotient(pseudoUnit, that.pseudoUnit));
	}

	/**
	 * Returns this dimension raised to an exponent.
	 *
	 * @param n the exponent.
	 * @return the result of raising this dimension to the exponent.
	 * @since 1.0
	 */
	public UnitDimension pow(int n) {
		return new UnitDimension(this.pseudoUnit.pow(n));
	}

	/**
	 * Returns the given root of this dimension.
	 *
	 * @param n the root's order.
	 * @return the result of taking the given root of this dimension.
	 * @throws ArithmeticException if <code>n == 0</code>.
	 * @since 1.0
	 */
	public UnitDimension root(int n) {
		return new UnitDimension(this.pseudoUnit.root(n));
	}

	/**
	 * Returns the fundamental (base) dimensions and their exponent whose product is
	 * this dimension or <code>null</code> if this dimension is a fundamental
	 * dimension.
	 *
	 * @return the mapping between the base dimensions and their exponent.
	 * @since 1.0
	 */
	@SuppressWarnings("rawtypes")
	public Map<? extends Dimension, Integer> getBaseDimensions() {
		Map<? extends Unit, Integer> pseudoUnits = pseudoUnit.getBaseUnits();
		if (pseudoUnits == null) {
			return null;
		}
		final Map<UnitDimension, Integer> baseDimensions = new HashMap<>();
		for (Map.Entry<? extends Unit, Integer> entry : pseudoUnits.entrySet()) {
			baseDimensions.put(new UnitDimension(entry.getKey()), entry.getValue());
		}
		return baseDimensions;
	}

	@Override
	public String toString() {
		return pseudoUnit.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof UnitDimension) {
			UnitDimension other = (UnitDimension) obj;
			return Objects.equals(pseudoUnit, other.pseudoUnit);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pseudoUnit);
	}
}
