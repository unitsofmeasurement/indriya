/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2019, Units of Measurement project.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.measure.MeasurementException;
import javax.measure.Quantity;
import javax.measure.Unit;

import tech.units.indriya.format.MixedRadixFormat.MixedRadixFormatOptions;
import tech.units.indriya.function.Calculus;
import tech.units.indriya.function.Calculus.IntegerAndFraction;
import tech.units.indriya.quantity.Quantities;

/**
 * Immutable, typesafe utility class to cover 'mixed-radix' related use-cases.
 * 
 * @author Andi Huber
 * @since 2.0
 */
public class MixedRadix<Q extends Quantity<Q>> {

    // -- PRIVATE FIELDS 

    private final PrimaryUnitPickState pickState; 
    private final Unit<Q> primaryUnit;
    private final List<Unit<Q>> mixedRadixUnits;
    //private final List<Unit<Q>> converter;
    
    // -- PRIMARY UNIT PICK CONVENTION
    
    public static enum PrimaryUnitPick {
        LEADING_UNIT,
        TRAILING_UNIT
    }
    
    public static final PrimaryUnitPick PRIMARY_UNIT_PICK_DEFAULT = PrimaryUnitPick.TRAILING_UNIT;
    
    public static PrimaryUnitPick PRIMARY_UNIT_PICK = PRIMARY_UNIT_PICK_DEFAULT;
    
    // -- FACTORIES
    
    public static <X extends Quantity<X>> MixedRadix<X> of(Unit<X> leadingUnit) {
        Objects.requireNonNull(leadingUnit);
        return new MixedRadix<>(
                PrimaryUnitPickState.pickByConvention(), 
                Collections.singletonList(leadingUnit));
    }
    
    public static <X extends Quantity<X>> MixedRadix<X> ofPrimary(Unit<X> primaryUnit) {
        Objects.requireNonNull(primaryUnit);
        return new MixedRadix<>(
                PrimaryUnitPickState.pickLeading(), 
                Collections.singletonList(primaryUnit));
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
    
    public Unit<Q> getLeadingUnit() {
        return mixedRadixUnits.get(0);
    }
    
    public Unit<Q> getTrainlingUnit() {
        return mixedRadixUnits.get(mixedRadixUnits.size()-1);
    }
    
    public List<Unit<Q>> getUnits() {
        return new ArrayList<>(mixedRadixUnits);
    }
    
    public int getUnitCount() {
        return mixedRadixUnits.size();
    }
    
    // -- QUANTITY FACTORY
    
    public Quantity<Q> createQuantity(Number leadingValue, Number ... lessSignificantValues) {

        Objects.requireNonNull(leadingValue);
        Objects.requireNonNull(lessSignificantValues); // allow empty but not <null>

        int totalValuesGiven = 1 + lessSignificantValues.length;
        int totalValuesAllowed = getUnitCount();
        
        if(totalValuesGiven > totalValuesAllowed) {
            String message = String.format(
                    "number of values given <%d> exceeds the number of mixed-radix units available <%d>", 
                    totalValuesGiven, totalValuesAllowed);
            throw new IllegalArgumentException(message);
        }

        int valuesToBeProcessed = Math.min(totalValuesAllowed, totalValuesGiven);
        
        Quantity<Q> quantity = Quantities.getQuantity(0, primaryUnit);
        
        for(int i=0; i<valuesToBeProcessed; ++i) {
            final Number fractionalValue = i==0
                    ? leadingValue
                            : lessSignificantValues[i-1];
            final Unit<Q> fractionalUnit = mixedRadixUnits.get(i); 
            quantity = quantity.add(Quantities.getQuantity(fractionalValue, fractionalUnit));
        }

        return quantity;
    }
    
    // -- VALUE EXTRACTION

    public Number[] extractValues(Quantity<Q> quantity) {
        Objects.requireNonNull(quantity);
        final Number[] target = new Number[getUnitCount()];
        return extractValuesInto(quantity, target);
    }

    public Number[] extractValuesInto(Quantity<Q> quantity, Number[] target) {
        Objects.requireNonNull(quantity);
        Objects.requireNonNull(target);

        visitQuantity(quantity, target.length, (index, unit, value)->{
            target[index] = value;
        });

        return target;
    }
    
    // -- THE VISITOR
    
    @FunctionalInterface
    public static interface MixedRadixVisitor<Q extends Quantity<Q>> {
        public void accept(int index, Unit<Q> unit, Number value);
    }
    
    public void visitQuantity(
            Quantity<Q> quantity,
            int maxPartsToVisit,
            MixedRadixVisitor<Q> partVisitor) {

        // calculate the leading part and fractions
        // these are all integers (whole numbers) except for the very last part

        IntegerAndFraction remaining = null;
        Unit<Q> currentUnit = null;

        final int partsToVisitCount = Math.min(maxPartsToVisit, getUnitCount());

        // corner case (partsToVisitCount == 0)

        if(partsToVisitCount==0) {
            return;
        }
        
        // corner case (partsToVisitCount == 1)
        
        final Number value_inLeadingUnits = quantity.to(getLeadingUnit()).getValue();
        
        if(partsToVisitCount==1) {
            partVisitor.accept(0, getLeadingUnit(), value_inLeadingUnits);
            return;
        }

        // for partsToVisitCount >= 2

        final int maxIndexToVisit = partsToVisitCount - 1;
        for(int i=0; i<=maxIndexToVisit; ++i) {

            if(i==0) {
                remaining = Calculus.roundTowardsZeroWithRemainder(value_inLeadingUnits);
                currentUnit = getLeadingUnit();
                partVisitor.accept(0, getLeadingUnit(), remaining.getInteger());
                continue;
            } 

            // convert remaining fraction to next fractionalUnit
            Unit<Q> fractionalUnit = mixedRadixUnits.get(i);
            Quantity<Q> remainingQuantity = 
                    Quantities.getQuantity(remaining.getFraction(), currentUnit);            
            Number value_inFractionalUnits = remainingQuantity.to(fractionalUnit).getValue();

            if(i==maxIndexToVisit) {
                partVisitor.accept(i, fractionalUnit, value_inFractionalUnits);
                break; // we're done
            }

            // split the remaining fraction
            remaining = Calculus.roundTowardsZeroWithRemainder(value_inFractionalUnits);
            partVisitor.accept(i, fractionalUnit, remaining.getInteger());
            currentUnit = fractionalUnit;
        }
    }
    
    // -- FORMATTING 
    
    public MixedRadixFormat<Q> createFormat(final MixedRadixFormatOptions options) {
        return MixedRadixFormat.of(this, options);
    }
    
    // -- IMPLEMENTATION DETAILS

    /**
     * 
     * @param primaryUnitIndex - if negative, the index is relative to the number of units
     * @param mixedRadixUnits
     */
    private MixedRadix(PrimaryUnitPickState pickState, List<Unit<Q>> mixedRadixUnits) {
        this.pickState = pickState;
        this.mixedRadixUnits = mixedRadixUnits;
        this.primaryUnit = mixedRadixUnits.get(pickState.nonNegativePrimaryUnitIndex(getUnitCount()));
    }
    
    private MixedRadix<Q> append(PrimaryUnitPickState state, Unit<Q> mixedRadixUnit) {
        
        Unit<Q> tail = getTrainlingUnit(); 
        
        assertDecreasingOrderOfSignificance(tail, mixedRadixUnit);
        
        final List<Unit<Q>> mixedRadixUnits = new ArrayList<>(this.mixedRadixUnits);
        mixedRadixUnits.add(mixedRadixUnit);
        return new MixedRadix<>(state, mixedRadixUnits);
    }
    
    private void assertDecreasingOrderOfSignificance(Unit<Q> u1, Unit<Q> u2) {
        
        Number factor = u2.getConverterTo(u1).convert(1.);
        
        if(!Calculus.isLessThanOne(Calculus.abs(factor))) {
            String message = String.format("the appended mixed-radix unit <%s> "
                    + "must be of lesser significance "
                    + "than the one it is appended to: <%s>", 
                    u1.getClass(),
                    u2.getClass());
            throw new IllegalArgumentException(message);
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
                throw new MeasurementException(String.format("internal error: unmatched switch case <%s>", PRIMARY_UNIT_PICK));
                
            }
            
            return new PrimaryUnitPickState(false, pickedIndex_byConvention);
        }

        private void assertNotExplicitlyPicked() {
            if(explicitlyPicked) {
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
            return pickedIndex < 0
                    ? unitCount + pickedIndex
                            : pickedIndex;
        }
        
    
    }
    

}
