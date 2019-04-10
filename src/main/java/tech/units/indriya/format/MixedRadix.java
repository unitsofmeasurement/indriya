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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.measure.MeasurementException;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.format.UnitFormat;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.function.Calculus;
import tech.units.indriya.function.Calculus.IntegerAndFraction;
import tech.units.indriya.quantity.Quantities;

/**
 * Immutable, typesafe utility class to cover 'mixed-radix' related use-cases.
 * 
 * @author Andi Huber
 * @version 1.0
 * @since 2.0
 */
public class MixedRadix<Q extends Quantity<Q>> {

    /**
     * TODO
     * @param <X>
     * @param unit
     * @return
     */
    public static <X extends Quantity<X>> MixedRadix<X> ofPrimary(Unit<X> unit) {
        Objects.requireNonNull(unit);
        return new MixedRadix<>(unit, Collections.emptyList());
    }

    /**
     * TODO
     * @param fractionalUnit
     * @return
     */
    public MixedRadix<Q> mix(Unit<Q> fractionalUnit) {

        Objects.requireNonNull(fractionalUnit);

        final List<Unit<Q>> fractionalUnits = new ArrayList<>(this.fractionalUnits);
        fractionalUnits.add(fractionalUnit);

        return new MixedRadix<>(primaryUnit, fractionalUnits);
    }

    /**
     * TODO
     * @param primaryValue
     * @param fractionalValues
     * @return
     */
    public Quantity<Q> createQuantity(Number primaryValue, Number ... fractionalValues) {

        Objects.requireNonNull(primaryValue);

        Quantity<Q> quantity = Quantities.getQuantity(primaryValue, primaryUnit);

        if(fractionalValues==null) {
            return quantity;  
        }

        if(fractionalValues.length > fractionalUnits.size()) {
            String message = String.format(
                    "number of fractionalValues <%d> exceeds the number of fractional units available <%d>", 
                    fractionalValues.length, fractionalUnits.size());
            throw new IllegalArgumentException(message);
        }

        for(int i=0;  (i<fractionalValues.length) && (i<fractionalUnits.size()); ++i) {
            final Number fractionalValue = fractionalValues[i];
            final Unit<Q> fractionalUnit = fractionalUnits.get(i); 
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
    public Appendable format(Quantity<Q> quantity, Appendable dest, MixedRadixFormatOptions options) 
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
    public final String format(Quantity<Q> quantity, MixedRadixFormatOptions options) {
        try {
            return (this.format(quantity, new StringBuffer(), options)).toString();
        } catch (IOException ex) {
            // Should never happen, because StringBuffer is an Appendable, that does not throw here
            throw new MeasurementException(ex); 
        }
    }
    
    // -- FORMAT OPTIONS
    
    public static class MixedRadixFormatOptions {

        private DecimalFormat integerFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
        {
            integerFormat.setMaximumFractionDigits(0);    
            integerFormat.setDecimalSeparatorAlwaysShown(false);
        }
                
        private DecimalFormat realFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
        private UnitFormat unitFormat = SimpleUnitFormat.getInstance();
        private String numberToUnitDelimiter = AbstractQuantityFormat.DEFAULT_DELIMITER;
        private String radixPartsDelimiter = " ";

        /**
         * @return the {@link DecimalFormat} to be used for formatting the whole-number parts
         */
        public DecimalFormat getIntegerFormat() {
            return integerFormat;
        }
        
        /**
         * Sets the integerFormat parameter to the given {@code DecimalFormat}.
         * @param integerFormat the {@link DecimalFormat}
         * @throws NullPointerException if {@code integerFormat} is {@code null}
         * @return this {@code MixedRadixFormatOptions}
         */
        public MixedRadixFormatOptions integerFormat(DecimalFormat integerFormat) {
            Objects.requireNonNull(integerFormat);
            this.integerFormat = integerFormat;
            return this;
        }

        /**
         * @return the {@link DecimalFormat} to be used for formatting the real-number part 
         * (which is the last part)
         */
        public DecimalFormat getRealFormat() {
            return realFormat;
        }
        
        /**
         * Sets the realFormat parameter to the given {@code DecimalFormat}.
         * @param realFormat the {@link DecimalFormat}
         * @throws NullPointerException if {@code realFormat} is {@code null}
         * @return this {@code MixedRadixFormatOptions}    
         */
        public MixedRadixFormatOptions realFormat(DecimalFormat realFormat) {
            Objects.requireNonNull(realFormat);
            this.realFormat = realFormat;
            return this;
        }
        
        public UnitFormat getUnitFormat() {
            return unitFormat;
        }
        
        public MixedRadixFormatOptions unitFormat(UnitFormat unitFormat) {
            Objects.requireNonNull(unitFormat);
            this.unitFormat = unitFormat;
            return this;
        }
        
        public String getNumberToUnitDelimiter() {
            return numberToUnitDelimiter;
        }
        
        public MixedRadixFormatOptions numberToUnitDelimiter(String numberToUnitDelimiter) {
            Objects.requireNonNull(numberToUnitDelimiter);
            this.numberToUnitDelimiter = numberToUnitDelimiter;
            return this;
        }
        
        public String getRadixPartsDelimiter() {
            return radixPartsDelimiter;
        }
        
        public MixedRadixFormatOptions radixPartsDelimiter(String radixPartsDelimiter) {
            Objects.requireNonNull(radixPartsDelimiter);
            this.radixPartsDelimiter = radixPartsDelimiter;
            return this;
        }

    }

    // -- IMPLEMENTATION DETAILS

    private final Unit<Q> primaryUnit;
    private final List<Unit<Q>> fractionalUnits;

    private MixedRadix(Unit<Q> primaryUnit, List<Unit<Q>> fractionalUnits) {
        this.primaryUnit = primaryUnit;
        this.fractionalUnits = fractionalUnits;
    }

    private int totalParts() {
        return 1 + fractionalUnits.size();
    }

    @FunctionalInterface
    private static interface PartVisitor<Q extends Quantity<Q>> {
        public void accept(int index, Unit<Q> unit, Number value);
    }

    private void visitRadixPartsOfQuantity(
            Quantity<Q> quantity,
            int maxPartsToVisit,
            PartVisitor<Q> partVisitor) {

        final Number value_inPrimaryUnits = quantity.to(primaryUnit).getValue();

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
            partVisitor.accept(0, primaryUnit, value_inPrimaryUnits);
            return;
        }

        // for partsToVisitCount >= 2

        final int maxTargetIndex = partsToVisitCount - 1;
        for(int i=0; i<=maxTargetIndex; ++i) {

            if(i==0) {
                remaining = Calculus.roundTowardsZeroWithRemainder(value_inPrimaryUnits);
                currentUnit = primaryUnit;
                partVisitor.accept(0, primaryUnit, remaining.getInteger());
                continue;
            } 

            // convert remaining fraction to next fractionalUnit
            Unit<Q> fractionalUnit = fractionalUnits.get(i-1);
            Quantity<Q> remainingQuantity = 
                    Quantities.getQuantity(remaining.getFraction(), currentUnit);            
            Number value_inFractionalUnits = remainingQuantity.to(fractionalUnit).getValue();

            if(i==maxTargetIndex) {
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
