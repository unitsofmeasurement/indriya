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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.measure.Prefix;
import javax.measure.UnitConverter;

import tech.units.indriya.function.PowerConverter;
import tech.units.indriya.function.UnitComparator;
import tech.uom.lib.common.function.Converter;

/**
 * <p>
 * The base class for our {@link UnitConverter} implementations.
 * </p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 1.5, April 19, 2018
 * @since 1.0
 */
public abstract class AbstractConverter
		implements UnitConverter, Converter<Number, Number>, Serializable, Comparable<UnitConverter> {

	/**
	*
	*/
	private static final long serialVersionUID = 5790242858468427131L;

	/**
	 * The ratio of the circumference of a circle to its diameter.
	 **/
	protected static final double PI = 3.1415926535897932384626433832795;

	/**
	 * Holds identity converter.
	 */
	// [ahuber] potentially misused: checking whether a UnitConverter is an identity operator
	// should be done with unitConverter.isIdentity() rather then unitConverter == AbstractConverter.IDENTITY
	public static final AbstractConverter IDENTITY = new Identity();

	/**
	 * DefaultQuantityFactory constructor.
	 */
	protected AbstractConverter() {
	}

	/**
	 * Guard for {@link #simpleCompose(AbstractConverter)}
	 * @param that
	 * @return whether or not a 'simple' composition of transformations is possible
	 */
	protected abstract boolean isSimpleCompositionWith(AbstractConverter that);
	
	/**
	 * Guarded by {@link #isSimpleCompositionWith(AbstractConverter)}
	 * @param that
	 * @return a new AbstractConverter that adds no additional conversion step
	 */
	protected AbstractConverter simpleCompose(AbstractConverter that) {
		throw new IllegalStateException(
				String.format("Concrete UnitConverter '%s' does not implement simpleCompose(...).", this)); 
	}

	/**
	 * Creates a converter with the specified Prefix.
	 * 
	 * @param prefix
	 *            the prefix for the factor.
	 */
	public static UnitConverter of(Prefix prefix) {
		return PowerConverter.of(prefix);
	}

	@Override
	public abstract boolean equals(Object cvtr);

	@Override
	public abstract int hashCode();

	@Override
	public abstract AbstractConverter inverse();

	@Override
	public final UnitConverter concatenate(UnitConverter converter) {
		Objects.requireNonNull(converter, "Can not concatenate null.");
		if(converter instanceof AbstractConverter) {
			// let Simplifier decide
			AbstractConverter other = (AbstractConverter) converter;
			return AbstractUnit.Simplifier.compose(this, other);
		}
		// converter is not known to this implementation ...
		if(converter.isIdentity()) {
			return this;
		}
		if(this.isIdentity()) {
			return converter;
		}
		return new Pair(this, converter);
	}

	@Override
	public List<? extends UnitConverter> getConversionSteps() {
		List<AbstractConverter> converters = new ArrayList<>();
		converters.add(this);
		return converters;
	}

	/**
	 * @throws IllegalArgumentException
	 *             if the value is </code>null</code>.
	 */
	public final Number convert(Number value) {
		if(isIdentity()) {
			return value;
		}
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		if (value instanceof BigDecimal) {
			return convert((BigDecimal) value, MathContext.DECIMAL128);
		}
		if (value instanceof BigInteger) {
			return convert((BigInteger) value, MathContext.DECIMAL128);
		}
		return convert(value.doubleValue());
	}

	@Override
	public abstract double convert(double value);

	protected Number convert(BigInteger value, MathContext ctx) {
		return convert(new BigDecimal(value), ctx);
	}
	
	public abstract BigDecimal convert(BigDecimal value, MathContext ctx) throws ArithmeticException;

	/**
	 * This class represents the identity converter (singleton).
	 */
	private static final class Identity extends AbstractConverter {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4460463244427587361L;

		@Override
		public boolean isIdentity() {
			return true;
		}

		@Override
		public Identity inverse() {
			return this;
		}

		@Override
		public double convert(double value) {
			return value;
		}

		@Override
		public Number convert(BigInteger value, MathContext ctx) {
			return value;
		}
		
		@Override
		public BigDecimal convert(BigDecimal value, MathContext ctx) {
			return value;
		}

		@Override
		public boolean equals(Object cvtr) {
			return (cvtr instanceof Identity);
		}

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public boolean isLinear() {
			return true;
		}

		@Override
		public int compareTo(UnitConverter o) {
			if (o instanceof Identity) {
				return 0;
			}
			return -1;
		}

		@Override
		protected boolean isSimpleCompositionWith(AbstractConverter that) {
			return true;
		}
	}

	/**
	 * This class represents converters made up of two or more separate converters
	 * (in matrix notation <code>[pair] = [left] x [right]</code>).
	 */
	public static final class Pair extends AbstractConverter implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -123063827821728331L;

		/**
		 * Holds the first converter.
		 */
		private final UnitConverter left;

		/**
		 * Holds the second converter.
		 */
		private final UnitConverter right;

		/**
		 * Creates a pair converter resulting from the combined transformation of the
		 * specified converters.
		 *
		 * @param left
		 *            the left converter, not <code>null</code>.
		 * @param right
		 *            the right converter.
		 * @throws IllegalArgumentException
		 *             if either the left or right converter are </code> null</code>
		 */
		public Pair(UnitConverter left, UnitConverter right) {
			if (left != null && right != null) {
				this.left = left;
				this.right = right;
			} else {
				throw new IllegalArgumentException("Converters cannot be null");
			}
		}

		@Override
		public boolean isLinear() {
			return left.isLinear() && right.isLinear();
		}

		@Override
		public boolean isIdentity() {
			return false;
		}

		@Override
		public List<UnitConverter> getConversionSteps() {
			final List<UnitConverter> steps = new ArrayList<>();
			List<? extends UnitConverter> leftCompound = left.getConversionSteps();
			List<? extends UnitConverter> rightCompound = right.getConversionSteps();
			steps.addAll(leftCompound);
			steps.addAll(rightCompound);
			return steps;
		}

		@Override
		public Pair inverse() {
			return new Pair(right.inverse(), left.inverse());
		}

		@Override
		public double convert(double value) {
			return left.convert(right.convert(value));
		}
		
		@Override
		public Number convert(BigInteger value, MathContext ctx) {
			if (right instanceof AbstractConverter) {
				return ((AbstractConverter) left).convert(((AbstractConverter) right).convert(value));
			}
			return convert(new BigDecimal(value), ctx);
		}

		@Override
		public BigDecimal convert(BigDecimal value, MathContext ctx) {
			if (right instanceof AbstractConverter) {
				return ((AbstractConverter) left).convert(((AbstractConverter) right).convert(value, ctx), ctx);
			}
			return (BigDecimal) left.convert(right.convert(value));
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj instanceof Pair) {
				Pair that = (Pair) obj;
				return Objects.equals(left, that.left) && Objects.equals(right, that.right);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hash(left, right);
		}

		public UnitConverter getLeft() {
			return left;
		}

		public UnitConverter getRight() {
			return right;
		}

		@SuppressWarnings("unchecked")
		@Override
		public int compareTo(UnitConverter obj) {
			if (this == obj) {
				return 0;
			}
			if (obj instanceof Pair) {
				Pair that = (Pair) obj;
				@SuppressWarnings("rawtypes")
				Comparator c = new UnitComparator<>();
				return Objects.compare(left, that.left, c) + Objects.compare(right, that.right, c);
			}
			return -1;
		}
		
		@Override
		public String toString() {
			return String.format("AbstractConverter.Pair[%s]",
					getConversionSteps().stream()
					.map(UnitConverter::toString)
					.collect(Collectors.joining(", ")) );
		}

		@Override
		protected boolean isSimpleCompositionWith(AbstractConverter that) {
			return false;
		}
		
	}


}
