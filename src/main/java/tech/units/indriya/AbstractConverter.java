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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.measure.Prefix;
import javax.measure.UnitConverter;

import tech.units.indriya.function.Calculus;
import tech.units.indriya.function.PowerOfIntConverter;
import tech.units.indriya.function.UnitComparator;
import tech.units.indriya.function.UnitCompositionHandler;
import tech.uom.lib.common.function.Converter;

/**
 * <p>
 * The base class for our {@link UnitConverter} implementations.
 * </p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @author Andi Huber
 * @version 1.7, Oct. 27, 2018
 * @since 1.0
 */
public abstract class AbstractConverter
		implements UnitConverter, Converter<Number, Number>, Serializable, Comparable<UnitConverter> {

	/**
	*
	*/
	private static final long serialVersionUID = 5790242858468427131L;

	/**
	 * Default identity converter implementing AbstractConverter.
	 * <p>
	 * Note: Checking whether a UnitConverter is an identity operator should be done with 
	 * {@code UnitConverter.isIdentity()} rather than checking for object identity 
	 * {@code unitConverter == AbstractConverter.IDENTITY}.
	 */
	public static final AbstractConverter IDENTITY = new Identity();
	
	/**
	 * Allows for plug in of a custom UnitCompositionHandler.
	 */
	public static UnitCompositionHandler UNIT_COMPOSITION_HANDLER = UnitCompositionHandler.yieldingNormalForm();

	/**
	 * memoization for getConversionSteps
	 */
	protected List<? extends UnitConverter> conversionSteps; 

	/**
	 * DefaultQuantityFactory constructor.
	 */
	protected AbstractConverter() {
	}

	/**
	 * Creates a converter with the specified Prefix.
	 * 
	 * @param prefix
	 *            the prefix for the factor.
	 */
	public static UnitConverter of(Prefix prefix) {
		return PowerOfIntConverter.of(prefix);
	}

	@Override
	public abstract boolean equals(Object cvtr);

	@Override
	public abstract int hashCode();
	
	// -- TO-STRING - CONTRACT AND INTERFACE IMPLEMENTATION (FINAL)
	
	/**
	 * Non-API
	 * <p>
	 * Returns a String describing the transformation that is represented by this converter. 
	 * Contributes to converter's {@code toString} method. If null or empty
	 * {@code toString} output becomes simplified.
	 * </p>
	 * @return 
	 */
	protected abstract String transformationLiteral();
	
	@Override
	public final String toString() {
		String converterName = getClass().getSimpleName();
		// omit trailing 'Converter'
		if(converterName.endsWith("Converter")) {
			converterName = converterName.substring(0, converterName.length()-"Converter".length());
		}
		if(isIdentity()) {
			return String.format("%s(IDENTITY)", converterName);
		}
		final String transformationLiteral = transformationLiteral();
		if(transformationLiteral==null || transformationLiteral.length()==0) {
			return String.format("%s", converterName);
		}
		return String.format("%s(%s)", converterName, transformationLiteral);
	}

	// -- INVERSION - CONTRACT AND INTERFACE IMPLEMENTATION (FINAL)
	
	/**
	 * Non-API
	 * <p>
	 * Returns an AbstractConverter that represents the inverse transformation of this converter,
	 * for cases where the transformation is not the identity transformation.
	 * </p>  
	 * @return 
	 */
	protected abstract AbstractConverter inverseWhenNotIdentity();
	
	@Override
	public final AbstractConverter inverse() {
		if(isIdentity()) {
			return this;
		}
		return inverseWhenNotIdentity();
	}
	
	// -- COMPOSITION CONTRACTS (TO BE IMPLEMENTED BY SUB-CLASSES)

	/**
	 * Non-API
	 * Guard for {@link #reduce(AbstractConverter)}
	 * @param that
	 * @return whether or not a composition with given {@code that} is possible, such 
	 * that no additional conversion steps are required, with respect to the steps already 
	 * in place by this converter 
	 */
	protected abstract boolean canReduceWith(AbstractConverter that);
	
	/**
	 * Non-API
	 * Guarded by {@link #canReduceWith(AbstractConverter)}
	 * @param that
	 * @return a new AbstractConverter that adds no additional conversion steps, with respect 
	 * to the steps already in place by this converter 
	 */
	protected AbstractConverter reduce(AbstractConverter that) {
		throw new IllegalStateException(
				String.format("Concrete UnitConverter '%s' does not implement reduce(...).", this)); 
	}
	
	// -- COMPOSITION INTERFACE IMPLEMENTATION (FINAL)
	
	@Override
	public final UnitConverter concatenate(UnitConverter converter) {
		Objects.requireNonNull(converter, "Cannot compose with converter that is null.");
		
		if(converter instanceof AbstractConverter) {
		    final AbstractConverter other = (AbstractConverter) converter;
		    return UNIT_COMPOSITION_HANDLER.compose(this, other, 
		            AbstractConverter::canReduceWith,
		            AbstractConverter::reduce);
		}
		// converter is not a sub-class of AbstractConverter, we do the best we can ...
		if(converter.isIdentity()) {
			return this;
		}
		if(this.isIdentity()) {
			return converter;
		}
		//[ahuber] we don't know how to reduce to a 'normal-form' with 'foreign' converters,
		// so we just return the straightforward composition, which no longer allows for proper
		// composition equivalence test
		return new Pair(this, converter);
	}

	@Override
	public final List<? extends UnitConverter> getConversionSteps() {
		if(conversionSteps != null) {
			return conversionSteps;  
		}
		if(this instanceof Pair) {
			return conversionSteps = ((Pair)this).createConversionSteps();
		}
		return conversionSteps = Collections.singletonList(this);
	}
	
	// -- CONVERSION CONTRACTS (TO BE IMPLEMENTED BY SUB-CLASSES)
	
	/**
	 * Non-API
	 * @param value
	 * @return transformed value 
	 */
	protected abstract double convertWhenNotIdentity(double value);

	/**
	 * Non-API
	 * @param value
	 * @param ctx
	 * @return transformed value (most likely a BigInteger or BigDecimal)
	 */
	protected Number convertWhenNotIdentity(BigInteger value, MathContext ctx) {
		return convertWhenNotIdentity(new BigDecimal(value), ctx);
	}
	
	/**
	 * Non-API
	 * @param value
	 * @param ctx
	 * @return transformed value
	 */
	protected abstract BigDecimal convertWhenNotIdentity(BigDecimal value, MathContext ctx);

	// -- CONVERSION INTERFACE IMPLEMENTATION (FINAL)
	
	@Override
	public final double convert(double value) {
		if(isIdentity()) {
			return value;
		}
		return convertWhenNotIdentity(value);
	}
	
	/**
	 * @throws IllegalArgumentException
	 *             if the value is <code>null</code>.
	 */
	@Override
	public final Number convert(Number value) {
		if(isIdentity()) {
			return value;
		}
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		if (value instanceof BigDecimal) {
			return convertWhenNotIdentity((BigDecimal) value, Calculus.MATH_CONTEXT);
		}
		if (value instanceof BigInteger) {
			return convertWhenNotIdentity((BigInteger) value, Calculus.MATH_CONTEXT);
		}
		return convertWhenNotIdentity(value.doubleValue());
	}
	
	// -- DEFAULT IMPLEMENTATION OF IDENTITY

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
		public double convertWhenNotIdentity(double value) {
		    throw unreachable();
		}

		@Override
		public Number convertWhenNotIdentity(BigInteger value, MathContext ctx) {
		    throw unreachable();
		}
		
		@Override
		public BigDecimal convertWhenNotIdentity(BigDecimal value, MathContext ctx) {
		    throw unreachable();
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
		protected boolean canReduceWith(AbstractConverter that) {
		    throw unreachable();
		}
		
		@Override
		protected AbstractConverter reduce(AbstractConverter that) {
		    throw unreachable();
		}

		@Override
		protected AbstractConverter inverseWhenNotIdentity() {
			throw unreachable();
		}
		
		@Override
		protected String transformationLiteral() {
			return null;
		}
		
		private IllegalStateException unreachable() {
		    return new IllegalStateException("code was reached, that is expected unreachable");
		}
		
	}
	
	// -- BINARY TREE (PAIR)

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

		/*
		 * Non-API
		 */
		protected List<? extends UnitConverter> createConversionSteps(){
			List<? extends UnitConverter> leftCompound = left.getConversionSteps();
			List<? extends UnitConverter> rightCompound = right.getConversionSteps();
			final List<UnitConverter> steps = new ArrayList<>(leftCompound.size() + rightCompound.size());
			steps.addAll(leftCompound);
			steps.addAll(rightCompound);
			return steps;
		}

		@Override
		public Pair inverseWhenNotIdentity() {
			return new Pair(right.inverse(), left.inverse());
		}

		@Override
		public double convertWhenNotIdentity(double value) {
			return left.convert(right.convert(value));
		}
		
		@Override
		public Number convertWhenNotIdentity(BigInteger value, MathContext ctx) {
			if (right instanceof AbstractConverter) {
			    //Implementation Note: assumes left is always instance of AbstractConverter
				final AbstractConverter _left = (AbstractConverter) left;
				final AbstractConverter _right = (AbstractConverter) right;
				
				final Number rightValue = _right.convertWhenNotIdentity(value, ctx);
				if(rightValue instanceof BigDecimal) {
					return _left.convertWhenNotIdentity((BigDecimal) rightValue, ctx);
				}
				if(rightValue instanceof BigInteger) {
					return _left.convertWhenNotIdentity((BigInteger) rightValue, ctx);
				}
				return _left.convertWhenNotIdentity(Calculus.toBigDecimal(rightValue), ctx);
			}
			return convertWhenNotIdentity(new BigDecimal(value), ctx);
		}

		@Override
		public BigDecimal convertWhenNotIdentity(BigDecimal value, MathContext ctx) {
			if (right instanceof AbstractConverter) {
				//Implementation Note: assumes left is always instance of AbstractConverter
				final AbstractConverter _left = (AbstractConverter) left;
				final AbstractConverter _right = (AbstractConverter) right;
				return _left.convertWhenNotIdentity(_right.convertWhenNotIdentity(value, ctx), ctx);
			}
			return Calculus.toBigDecimal(left.convert(right.convert(value)));
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
		protected String transformationLiteral() {
			return String.format("%s",
				getConversionSteps().stream()
				.map(UnitConverter::toString)
				.collect(Collectors.joining(" ○ ")) );
		}
		

		@Override
		protected boolean canReduceWith(AbstractConverter that) {
			return false;
		}
		
	}


}
