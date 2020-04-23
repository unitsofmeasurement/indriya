/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2020, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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
package tech.units.indriya.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.measure.MeasurementException;
import javax.measure.Quantity;
import javax.measure.Quantity.Scale;
import javax.measure.Unit;
import javax.measure.UnitConverter;

import tech.units.indriya.internal.function.Calculator;
import tech.units.indriya.internal.function.radix.MixedRadixSupport;
import tech.units.indriya.internal.function.radix.Radix;
import tech.units.indriya.quantity.CompoundQuantity;
import tech.units.indriya.quantity.Quantities;

/**
 * Immutable class that represents mixed-radix units (like "hour:min:sec" or
 * "ft, in")
 * 
 * 
 * @author Andi Huber
 * @author Werner Keil
 * @version 1.10, Jun 25, 2019
 * @since 2.0
 * @see <a href="https://en.wikipedia.org/wiki/Mixed_radix">Wikipedia: Mixed
 *      radix</a>
 * @see <a href=
 *      "https://reference.wolfram.com/language/ref/MixedUnit.html">Wolfram
 *      Language & System: MixedUnit</a>
 * @see <a href="https://en.wikipedia.org/wiki/Metrication">Wikipedia:
 *      Metrication</a>
 * @see CompoundQuantity
 */
public class MixedRadix<Q extends Quantity<Q>> {
// TODO could it be final or is there a use case for extending it?

	// -- PRIVATE FIELDS

	@Override
	public String toString() {
		return "MixedRadix [units=" + mixedRadixUnits + "]";
	}

	private final PrimaryUnitPickState pickState;
	private final Unit<Q> primaryUnit;
	private final List<Unit<Q>> mixedRadixUnits;
	private final MixedRadixSupport mixedRadixSupport;

	// -- PRIMARY UNIT PICK CONVENTION

	public static enum PrimaryUnitPick {
		LEADING_UNIT, TRAILING_UNIT
	}

	public static final PrimaryUnitPick PRIMARY_UNIT_PICK_DEFAULT = PrimaryUnitPick.TRAILING_UNIT;

	public static PrimaryUnitPick PRIMARY_UNIT_PICK = PRIMARY_UNIT_PICK_DEFAULT;

	// -- FACTORIES

	public static <X extends Quantity<X>> MixedRadix<X> of(Unit<X> leadingUnit) {
		Objects.requireNonNull(leadingUnit);
		return new MixedRadix<>(PrimaryUnitPickState.pickByConvention(), Collections.singletonList(leadingUnit));
	}

	@SafeVarargs
	public static <X extends Quantity<X>> MixedRadix<X> of(Unit<X>... units) {
		if (units == null || units.length < 1) {
			throw new IllegalArgumentException("at least the leading unit is required");
		}
		return of(Arrays.asList(units));
	}

	public static <X extends Quantity<X>> MixedRadix<X> of(Collection<Unit<X>> units) {
		if (units == null || units.size() < 1) {
			throw new IllegalArgumentException("at least the leading unit is required");
		}
		MixedRadix<X> mixedRadix = null;
		for (Unit<X> unit : units) {
			mixedRadix = mixedRadix == null ? of(unit) : mixedRadix.mix(unit);
		}
		return mixedRadix;
	}

	public static <X extends Quantity<X>> MixedRadix<X> ofPrimary(Unit<X> primaryUnit) {
		Objects.requireNonNull(primaryUnit);
		return new MixedRadix<>(PrimaryUnitPickState.pickLeading(), Collections.singletonList(primaryUnit));
	}

	public MixedRadix<Q> mix(Unit<Q> mixedRadixUnit) {
		Objects.requireNonNull(mixedRadixUnit);
		return append(pickState, mixedRadixUnit); // pickState is immutable, so reuse
	}

	public MixedRadix<Q> mixPrimary(Unit<Q> mixedRadixUnit) {
		pickState.assertNotExplicitlyPicked();
		Objects.requireNonNull(mixedRadixUnit);
		return append(PrimaryUnitPickState.pickByExplicitIndex(getUnitCount()), mixedRadixUnit);
	}

	// -- GETTERS

	public Unit<Q> getPrimaryUnit() {
		return primaryUnit;
	}

	private Unit<Q> getTrailingUnit() {
		return mixedRadixUnits.get(mixedRadixUnits.size() - 1);
	}

	public List<Unit<Q>> getUnits() {
		return Collections.unmodifiableList(mixedRadixUnits);
	}

	private int getUnitCount() {
		return mixedRadixUnits.size();
	}

	// -- QUANTITY FACTORY

	/**
	 * Creates a {@link Quantity} from given {@code values} and {@code scale}.
	 * @param values - numbers corresponding to the radices in most significant first order, 
     *      allowed to be of shorter length than the total count of radices of this {@code MixedRadix} instance
	 * @param scale - the {@link Scale} to be used for the returned {@link Quantity}
	 */
	public Quantity<Q> createQuantity(final Number[] values, final Scale scale) {
	    Objects.requireNonNull(scale);
	    guardAgainstIllegalNumbersArgument(values);

		Number sum = mixedRadixSupport.sumMostSignificant(values);

		return Quantities.getQuantity(sum, getTrailingUnit(), scale).to(getPrimaryUnit());
	}

    public Quantity<Q> createQuantity(Number... values) {
		return createQuantity(values, Scale.ABSOLUTE);
	}

    /**
     * Creates a {@link CompoundQuantity} from given {@code values} and {@code scale}.
     * <p>
     * Note: Not every {@code CompoundQuantity} can be represented by a {@code MixedRadix}. 
     * {@code MixedRadix} strictly requires its coefficients to be in decreasing order of significance, 
     * while a {@code CompoundQuantity} in principle does not.
     * 
     * @param values - numbers corresponding to the radix coefficients in most significant first order, 
     *      allowed to be of shorter length than the total count of radix coefficients of this 
     *      {@code MixedRadix} instance
     * @param scale - the {@link Scale} to be used for the elements of the returned {@link CompoundQuantity}
     */
	public CompoundQuantity<Q> createCompoundQuantity(final Number[] values, final Scale scale) {
		Objects.requireNonNull(scale);
		guardAgainstIllegalNumbersArgument(values);

		List<Quantity<Q>> quantities = new ArrayList<>();
		for (int i = 0; i < values.length; i++) {
			quantities.add(Quantities.getQuantity(values[i], mixedRadixUnits.get(i), scale));
		}
		return CompoundQuantity.of(quantities);
	}

	public CompoundQuantity<Q> createCompoundQuantity(Number... values) {
		return createCompoundQuantity(values, Scale.ABSOLUTE);
	}

	// -- VALUE EXTRACTION

	public Number[] extractValues(Quantity<Q> quantity) {
		Objects.requireNonNull(quantity);
		final Number[] target = new Number[mixedRadixUnits.size()];
		return extractValuesInto(quantity, target);
	}

	public Number[] extractValuesInto(Quantity<Q> quantity, Number[] target) {
		Objects.requireNonNull(quantity);
		Objects.requireNonNull(target);

		visitQuantity(quantity, target.length, (index, unit, value) -> {
			target[index] = value;
		});

		return target;
	}

	// -- THE VISITOR

	@FunctionalInterface
	public static interface MixedRadixVisitor<Q extends Quantity<Q>> {
		public void accept(int index, Unit<Q> unit, Number value);
	}

	// -- IMPLEMENTATION DETAILS
	
   private void guardAgainstIllegalNumbersArgument(Number[] values) {
        if (values == null || values.length < 1) {
            throw new IllegalArgumentException("at least the leading unit's number is required");
        }

        int totalValuesGiven = values.length;
        int totalValuesAllowed = mixedRadixUnits.size();

        if (totalValuesGiven > totalValuesAllowed) {
            String message = String.format(
                    "number of values given <%d> exceeds the number of mixed-radix units available <%d>",
                    totalValuesGiven, totalValuesAllowed);
            throw new IllegalArgumentException(message);
        }
    }
	
	void visitQuantity(Quantity<Q> quantity, int maxPartsToVisit, MixedRadixVisitor<Q> partVisitor) {

		final int partsToVisitCount = Math.min(maxPartsToVisit, getUnitCount());

		// corner case (partsToVisitCount == 0)

		if (partsToVisitCount == 0) {
			return;
		}

		// for partsToVisitCount >= 1

		final Number value_inTrailingUnits = quantity.to(getTrailingUnit()).getValue();
		final List<Number> extractedValues = new ArrayList<>(getUnitCount());

		mixedRadixSupport.visitRadixNumbers(value_inTrailingUnits, extractedValues::add);

		for (int i = 0; i < partsToVisitCount; ++i) {
			int invertedIndex = getUnitCount() - 1 - i;
			partVisitor.accept(i, mixedRadixUnits.get(i), extractedValues.get(invertedIndex));
		}
	}

	/**
	 * 
	 * @param primaryUnitIndex - if negative, the index is relative to the number of
	 *                         units
	 * @param mixedRadixUnits
	 */
	private MixedRadix(PrimaryUnitPickState pickState, final List<Unit<Q>> mixedRadixUnits) {
		this.pickState = pickState;
		this.mixedRadixUnits = mixedRadixUnits;
		this.primaryUnit = mixedRadixUnits.get(pickState.nonNegativePrimaryUnitIndex(getUnitCount()));

		final Radix[] radices = new Radix[getUnitCount() - 1];
		for (int i = 0; i < radices.length; ++i) {
			Unit<Q> higher = mixedRadixUnits.get(i);
			Unit<Q> lesser = mixedRadixUnits.get(i + 1);
			radices[i] = toRadix(higher.getConverterTo(lesser));
		}

		this.mixedRadixSupport = new MixedRadixSupport(radices);

	}

	private Radix toRadix(UnitConverter converter) {
		return Radix.ofMultiplyConverter(converter);
	}

	private MixedRadix<Q> append(PrimaryUnitPickState state, Unit<Q> mixedRadixUnit) {

		Unit<Q> tail = getTrailingUnit();

		assertDecreasingOrderOfSignificanceAndLinearity(tail, mixedRadixUnit);

		final List<Unit<Q>> mixedRadixUnits = new ArrayList<>(this.mixedRadixUnits);
		mixedRadixUnits.add(mixedRadixUnit);
		return new MixedRadix<>(state, mixedRadixUnits);
	}

	private void assertDecreasingOrderOfSignificanceAndLinearity(Unit<Q> tail, Unit<Q> appended) {

		final UnitConverter converter = appended.getConverterTo(tail);
		if (!converter.isLinear()) {
			String message = String.format("the appended mixed-radix unit <%s> " + "must be linear",
					appended.getClass());
			throw new IllegalArgumentException(message);
		}

		final Number factor = appended.getConverterTo(tail).convert(1);

		if (Calculator.of(factor).abs().isLessThanOne()) {
			String message = String.format("the appended mixed-radix unit <%s> " + "must be of lesser significance "
					+ "than the one it is appended to: <%s>", appended.getClass(), tail.getClass());
			throw new MeasurementException(message);
		}
	}

	private static class PrimaryUnitPickState {

		private final static int LEADING_IS_PRIMARY_UNIT = 0;
		private final static int TRAILING_IS_PRIMARY_UNIT = -1;
		private final boolean explicitlyPicked;
		private final int pickedIndex;

		private static PrimaryUnitPickState pickByConvention() {

			final int pickedIndex_byConvention;

			switch (PRIMARY_UNIT_PICK) {
			case LEADING_UNIT:
				pickedIndex_byConvention = LEADING_IS_PRIMARY_UNIT;
				break;

			case TRAILING_UNIT:
				pickedIndex_byConvention = TRAILING_IS_PRIMARY_UNIT;
				break;

			default:
				throw new MeasurementException(
						String.format("internal error: unmatched switch case <%s>", PRIMARY_UNIT_PICK));

			}

			return new PrimaryUnitPickState(false, pickedIndex_byConvention);
		}

		private void assertNotExplicitlyPicked() {
			if (explicitlyPicked) {
				throw new IllegalStateException("a primary unit was already picked");
			}
		}

		private static PrimaryUnitPickState pickByExplicitIndex(int explicitIndex) {
			return new PrimaryUnitPickState(true, explicitIndex);
		}

		private static PrimaryUnitPickState pickLeading() {
			return new PrimaryUnitPickState(true, LEADING_IS_PRIMARY_UNIT);
		}

		private PrimaryUnitPickState(boolean explicitlyPicked, int pickedIndex) {
			this.explicitlyPicked = explicitlyPicked;
			this.pickedIndex = pickedIndex;
		}

		private int nonNegativePrimaryUnitIndex(int unitCount) {
			return pickedIndex < 0 ? unitCount + pickedIndex : pickedIndex;
		}

	}
}
