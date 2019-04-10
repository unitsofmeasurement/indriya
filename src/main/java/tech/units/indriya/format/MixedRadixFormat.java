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
import java.util.Locale;
import java.util.Objects;

import javax.measure.MeasurementException;
import javax.measure.Quantity;
import javax.measure.format.MeasurementParseException;
import javax.measure.format.QuantityFormat;
import javax.measure.format.UnitFormat;

/**
 * Typesafe utility class to parse and format quantities using 'mixed-radix' format.
 * 
 * @author Andi Huber
 * @version 1.0
 * @since 2.0
 */
public class MixedRadixFormat<Q extends Quantity<Q>> implements QuantityFormat {

    // -- FACTORIES

    public static <Q extends Quantity<Q>> MixedRadixFormat<Q> of(MixedRadix<Q> mixedRadix, MixedRadixFormat.MixedRadixFormatOptions mixedRadixFormatOptions) {
        return new MixedRadixFormat<>(mixedRadix, mixedRadixFormatOptions);
    }

    public static <Q extends Quantity<Q>> MixedRadixFormat<Q> of(MixedRadix<Q> mixedRadix) {
        return new MixedRadixFormat<>(mixedRadix, new MixedRadixFormat.MixedRadixFormatOptions());
    }

    // -- IMPLEMENTATION

    @Override
    public Appendable format(Quantity<?> quantity, Appendable destination) throws IOException {
        Quantity<Q> quantity_typed = castOrFail(quantity);
        return mixedRadix.format(quantity_typed, destination, mixedRadixFormatOptions);
    }

    @Override
    public String format(Quantity<?> quantity) {
        Quantity<Q> quantity_typed = castOrFail(quantity);
        return mixedRadix.format(quantity_typed, mixedRadixFormatOptions);
    }

    @Override
    public Quantity<Q> parse(CharSequence csq, ParsePosition pos)
            throws IllegalArgumentException, MeasurementParseException {
        return mixedRadix.parse(csq, pos, mixedRadixFormatOptions);
    }

    @Override
    public Quantity<Q> parse(CharSequence csq) throws MeasurementParseException {
        return mixedRadix.parse(csq, mixedRadixFormatOptions);
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

    private final MixedRadix<Q> mixedRadix;
    private final MixedRadixFormat.MixedRadixFormatOptions mixedRadixFormatOptions;

    private MixedRadixFormat(MixedRadix<Q> mixedRadix, MixedRadixFormat.MixedRadixFormatOptions mixedRadixFormatOptions) {
        this.mixedRadix = mixedRadix;
        this.mixedRadixFormatOptions = mixedRadixFormatOptions;
    }

    @SuppressWarnings("unchecked")
    private Quantity<Q> castOrFail(Quantity<?> quantity) {
        try {
            return (Quantity<Q>) quantity;    
        } catch (ClassCastException e) {
            throw new MeasurementException("Generic type of given 'quantity' "
                    + "does not match the required MixedRadix's generic type!", e);
        }
    }


}
