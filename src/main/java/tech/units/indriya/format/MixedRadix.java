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

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.measure.MeasurementException;
import javax.measure.Quantity;
import javax.measure.Unit;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.function.Calculus;
import tech.units.indriya.function.Calculus.IntegerAndFraction;
import tech.units.indriya.quantity.Quantities;

/**
 * Immutable, typesafe utility class to cover 'mixed-radix' related use-cases.
 * 
 * @author Andi Huber
 * @version 1.1
 * @since 2.0
 */
public class MixedRadix<Q extends Quantity<Q>> {

    // -- PRIVATE FIELDS 
    
    private final static int FIRST_PART_IS_PRIMARY_UNIT = 0;
    private final static int LAST_PART_IS_PRIMARY_UNIT = -1;
    
    private final int primaryUnitIndex;
    private final Unit<Q> primaryUnit;
    private final List<Unit<Q>> mixedRadixUnits;
    
    // -- FACTORIES
    
    /**
     * TODO
     * @param <X>
     * @param primaryUnit
     * @return
     */
    public static <X extends Quantity<X>> MixedRadix<X> ofPrimary(Unit<X> primaryUnit) {
        Objects.requireNonNull(primaryUnit);
        return new MixedRadix<>(FIRST_PART_IS_PRIMARY_UNIT, Collections.singletonList(primaryUnit));
    }
    
    /**
     * TODO
     * @param <X>
     * @param unit
     * @return
     */
    public static <X extends Quantity<X>> MixedRadix<X> ofLeastSignificantAsPrimary(Unit<X> unit) {
        Objects.requireNonNull(unit);
        return new MixedRadix<>(LAST_PART_IS_PRIMARY_UNIT, Collections.singletonList(unit));
    }

    /**
     * TODO
     * @param mixedRadixUnit
     * @return
     */
    public MixedRadix<Q> mix(Unit<Q> mixedRadixUnit) {
        Objects.requireNonNull(mixedRadixUnit);
        
        //FIXME[211] create a converter from current least-significant unit to given mixedRadixUnit
        // and ensure there is a decreasing order of significance
        
        final List<Unit<Q>> mixedRadixUnits = new ArrayList<>(this.mixedRadixUnits);
        mixedRadixUnits.add(mixedRadixUnit);
        return new MixedRadix<>(primaryUnitIndex, mixedRadixUnits);
    }

    /**
     * TODO
     * @return
     */
    public Unit<Q> getPrimaryUnit() {
        return primaryUnit;
    }
    
    /**
     * TODO
     * @param leadingValue
     * @param lessSignificantValues
     * @return
     */
    public Quantity<Q> createQuantity(Number leadingValue, Number ... lessSignificantValues) {

        Objects.requireNonNull(leadingValue);
        Objects.requireNonNull(lessSignificantValues); // allow empty but not <null>

        int totalValuesGiven = 1 + lessSignificantValues.length;
        int totalValuesAllowed = totalParts();
        
        if(totalValuesGiven > totalValuesAllowed) {
            String message = String.format(
                    "number of values given <%d> exceeds the number of mixid-radix units available <%d>", 
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

    /**
     * TODO
     * @param quantity
     * @return
     */
    public Number[] extractValues(Quantity<Q> quantity) {
        Objects.requireNonNull(quantity);
        final Number[] target = new Number[totalParts()];
        return extractValuesInto(quantity, target);
    }

    /**
     * TODO
     * @param quantity
     * @param target
     * @return
     */
    public Number[] extractValuesInto(Quantity<Q> quantity, Number[] target) {
        Objects.requireNonNull(quantity);
        Objects.requireNonNull(target);

        visitRadixPartsOfQuantity(quantity, target.length, (index, unit, value)->{
            target[index] = value;
        });

        return target;
    }

    /**
     * TODO
     * @param options
     * @param quantity
     * @param dest
     * @return
     * @throws IOException
     */
    public Appendable format(Quantity<Q> quantity, Appendable dest, MixedRadixFormat.MixedRadixFormatOptions options) 
            throws IOException {

        final int lastIndex = totalParts()-1;
        
        visitRadixPartsOfQuantity(quantity, totalParts(), (index, unit, value)->{
            try {
                
                boolean isLast = index == lastIndex;
                
                DecimalFormat numberFormat = isLast
                        ? options.getRealFormat() // to format real number value
                                : options.getIntegerFormat(); // to format whole number value

                // number value 
                dest.append(numberFormat.format(value));
                
                if (!quantity.getUnit().equals(AbstractUnit.ONE)) {
                    // number to unit delimiter 
                    dest.append(options.getNumberToUnitDelimiter());
                    
                    // unit
                    options.getUnitFormat().format(unit, dest);
                }
                
                if(!isLast) {
                    
                    // radix delimiter
                    dest.append(options.getRadixPartsDelimiter());
                    
                } 
                
            } catch (IOException e) {
                throw new MeasurementException(e);
            }
        });

        return dest;
    }

    /**
     * TODO
     * @param options
     * @param quantity
     * @return
     */
    public final String format(Quantity<Q> quantity, MixedRadixFormat.MixedRadixFormatOptions options) {
        try {
            return (this.format(quantity, new StringBuffer(), options)).toString();
        } catch (IOException ex) {
            // Should never happen, because StringBuffer is an Appendable, that does not throw here
            throw new MeasurementException(ex); 
        }
    }
    
    /**
     * TODO
     * @param input
     * @param options
     * @return
     */
    public Quantity<Q> parse(CharSequence csq, ParsePosition pos, MixedRadixFormat.MixedRadixFormatOptions options) {
        throw new IllegalStateException("no implement yet"); //FIXME[211] implement
    }
    
    /**
     * TODO
     * @param csq
     * @param options
     * @return
     */
    public Quantity<Q> parse(CharSequence csq, MixedRadixFormat.MixedRadixFormatOptions options) {
        return this.parse(csq, new ParsePosition(0), options);
    }
    
    public MixedRadixFormat<Q> createFormat(final MixedRadixFormat.MixedRadixFormatOptions options) {
        return MixedRadixFormat.of(this, options);
    }
    
    // -- IMPLEMENTATION DETAILS

    /**
     * 
     * @param primaryUnitIndex - if negative, the index is relative to the number of parts
     * @param mixedRadixUnits
     */
    private MixedRadix(int primaryUnitIndex, List<Unit<Q>> mixedRadixUnits) {
        this.primaryUnitIndex = primaryUnitIndex;
        this.mixedRadixUnits = mixedRadixUnits;
        this.primaryUnit = mixedRadixUnits.get(nonNegativePrimaryUnitIndex());
    }
    
    private int nonNegativePrimaryUnitIndex() {
        return primaryUnitIndex < 0
                ? mixedRadixUnits.size() + primaryUnitIndex
                        : primaryUnitIndex;
    }

    private int totalParts() {
        return mixedRadixUnits.size();
    }
    
    private Unit<Q> leadingUnit() {
        return mixedRadixUnits.get(0);
    }

    @FunctionalInterface
    private static interface PartVisitor<Q extends Quantity<Q>> {
        public void accept(int index, Unit<Q> unit, Number value);
    }

    private void visitRadixPartsOfQuantity(
            Quantity<Q> quantity,
            int maxPartsToVisit,
            PartVisitor<Q> partVisitor) {

        final Number value_inLeadingUnits = quantity.to(leadingUnit()).getValue();
        
        // calculate the primary part and fractions
        // these are all integers (whole numbers) except for the very last part

        IntegerAndFraction remaining = null;
        Unit<Q> currentUnit = null;

        final int partsToVisitCount = Math.min(maxPartsToVisit, totalParts());

        // corner cases

        if(partsToVisitCount==0) {
            return;
        }
        if(partsToVisitCount==1) {
            partVisitor.accept(0, leadingUnit(), value_inLeadingUnits);
            return;
        }

        // for partsToVisitCount >= 2

        final int maxIndexToVisit = partsToVisitCount - 1;
        for(int i=0; i<=maxIndexToVisit; ++i) {

            if(i==0) {
                remaining = Calculus.roundTowardsZeroWithRemainder(value_inLeadingUnits);
                currentUnit = leadingUnit();
                partVisitor.accept(0, leadingUnit(), remaining.getInteger());
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





}
