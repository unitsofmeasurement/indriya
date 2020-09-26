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
package tech.units.indriya.format;

import static tech.units.indriya.format.FormatBehavior.LOCALE_NEUTRAL;
import static tech.units.indriya.format.CommonFormatter.parseCompoundAsLeading;
import static tech.units.indriya.format.CommonFormatter.parseCompoundAsPrimary;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.format.MeasurementParseException;
import javax.measure.format.UnitFormat;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.quantity.CompoundQuantity;
import tech.units.indriya.quantity.Quantities;

/**
 * An implementation of {@link javax.measure.format.QuantityFormat QuantityFormat} combining {@linkplain NumberFormat} and {@link UnitFormat}
 * separated by a delimiter.
 *
 * @author <a href="mailto:werner@units.tech">Werner Keil</a>
 * @author <a href="mailto:thodoris.bais@gmail.com">Thodoris Bais</a>
 *
 * @version 2.2, $Date: 2020-09-27 $
 * @since 2.0
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class NumberDelimiterQuantityFormat extends AbstractQuantityFormat {

    /**
     * Holds the default format instance (SimpleUnitFormat).
     */
    private static final NumberDelimiterQuantityFormat SIMPLE_INSTANCE = new NumberDelimiterQuantityFormat.Builder()
            .setNumberFormat(NumberFormat.getInstance(Locale.ROOT)).setUnitFormat(SimpleUnitFormat.getInstance()).build();

    /**
     * Holds the localized format instance.
     */
    private static final NumberDelimiterQuantityFormat LOCAL_INSTANCE = new NumberDelimiterQuantityFormat.Builder()
            .setNumberFormat(NumberFormat.getInstance())
            .setUnitFormat(LocalUnitFormat.getInstance())
            .setLocaleSensitive(true).build();

    /**
     *
     */
    private static final long serialVersionUID = 3546952599885869402L;

    private transient NumberFormat numberFormat;
    private transient UnitFormat unitFormat;
    private transient Unit primaryUnit;
    private String delimiter;
    private String mixDelimiter;
    private boolean localeSensitive;

    private NumberDelimiterQuantityFormat() {
        /* private constructor */ }

    /**
     * A fluent Builder to easily create new instances of <code>NumberDelimiterQuantityFormat</code>.
     */
    public static class Builder {

        private transient NumberFormat numberFormat;
        private transient UnitFormat unitFormat;
        private transient Unit primaryUnit;
        private transient String delimiter = DEFAULT_DELIMITER;
        private transient String mixedRadixDelimiter;
        private boolean localeSensitive;

        /**
         * Sets the numberFormat parameter to the given {@code NumberFormat}.
         * @param numberFormat the {@link NumberFormat}
         * @throws NullPointerException if {@code numberFormat} is {@code null}
         * @return this {@code NumberDelimiterQuantityFormat.Builder}
         */
        public Builder setNumberFormat(NumberFormat numberFormat) {
            Objects.requireNonNull(numberFormat);
            this.numberFormat = numberFormat;
            return this;
        }

        /**
         * Sets the unitFormat parameter to the given {@code UnitFormat}.
         * @param unitFormat the {@link UnitFormat}
         * @throws NullPointerException if {@code unitFormat} is {@code null}
         * @return this {@code NumberDelimiterQuantityFormat.Builder}
         */
        public Builder setUnitFormat(UnitFormat unitFormat) {
        	Objects.requireNonNull(unitFormat);
            this.unitFormat = unitFormat;
            return this;
        }

        /**
         * Sets the primary unit parameter for multiple {@link CompoundQuantity mixed quantities} to the given {@code Unit}.
         * @param primary the primary {@link Unit}
         * @throws NullPointerException if {@code primary} is {@code null}
         * @return this {@code NumberDelimiterQuantityFormat.Builder}
         */
        public Builder setPrimaryUnit(final Unit primary) {
            Objects.requireNonNull(primary);
            this.primaryUnit = primary;
            return this;
        }

        /**
         * Sets the delimiter between a {@code NumberFormat} and {@code UnitFormat}.
         * @param delimiter the delimiter to use
         * @throws NullPointerException if {@code delimiter} is {@code null}
         * @return this {@code NumberDelimiterQuantityFormat.Builder}
         */
        public Builder setDelimiter(String delimiter) {
        	Objects.requireNonNull(delimiter);
            this.delimiter = delimiter;
            return this;
        }

        /**
         * Sets the radix delimiter between multiple {@link CompoundQuantity mixed quantities}.
         * @param radixPartsDelimiter the delimiter to use
         * @throws NullPointerException if {@code radixPartsDelimiter} is {@code null}
         * @return this {@code NumberDelimiterQuantityFormat.Builder}
         */
        public Builder setRadixPartsDelimiter(String radixPartsDelimiter) {
            Objects.requireNonNull(radixPartsDelimiter);
            this.mixedRadixDelimiter = radixPartsDelimiter;
            return this;
        }

        /**
         * Sets the {@code localeSensitive} flag.
         * @param localeSensitive the flag, if the {@code NumberDelimiterQuantityFormat} to be built will depend on a {@code Locale} to perform its tasks.
         * @return this {@code NumberDelimiterQuantityFormat.Builder}
         * @see UnitFormat#isLocaleSensitive()
         */
        public Builder setLocaleSensitive(boolean localeSensitive) {
            this.localeSensitive = localeSensitive;
            return this;
        }

        public NumberDelimiterQuantityFormat build() {
            NumberDelimiterQuantityFormat quantityFormat = new NumberDelimiterQuantityFormat();
            quantityFormat.numberFormat = this.numberFormat;
            quantityFormat.unitFormat = this.unitFormat;
            quantityFormat.primaryUnit = this.primaryUnit;
            quantityFormat.delimiter = this.delimiter;
            quantityFormat.mixDelimiter = this.mixedRadixDelimiter;
            quantityFormat.localeSensitive = this.localeSensitive;
            return quantityFormat;
        }
    }

    /**
     * Returns an instance of {@link NumberDelimiterQuantityFormat} with a particular {@link FormatBehavior}, either locale-sensitive or locale-neutral.
     * For example: <code>NumberDelimiterQuantityFormat.getInstance(LOCALE_NEUTRAL))</code> returns<br>
     * <code>new NumberDelimiterQuantityFormat.Builder()
            .setNumberFormat(NumberFormat.getInstance(Locale.ROOT)).setUnitFormat(SimpleUnitFormat.getInstance()).build();</code>
     *
     * @param behavior
     *            the format behavior to apply.
     * @return <code>NumberDelimiterQuantityFormat.getInstance(NumberFormat.getInstance(), UnitFormat.getInstance())</code>
     */
    public static NumberDelimiterQuantityFormat getInstance(final FormatBehavior behavior) {
        switch (behavior) {
			case LOCALE_SENSITIVE:
				return LOCAL_INSTANCE;
            case LOCALE_NEUTRAL:
            default:
                return SIMPLE_INSTANCE;
        }
    }

    /**
     * Returns a new instance of {@link Builder}.
     *
     * @return a new {@link Builder}.
     */
    public static final Builder builder() {
        return new Builder();
    }

    /**
     * Returns the default format.
     *
     * @return the desired format.
     */
    public static NumberDelimiterQuantityFormat getInstance() {
        return getInstance(LOCALE_NEUTRAL);
    }

    /**
     * Returns the quantity format using the specified number format and unit format (the number and unit are separated by one space).
     *
     * @param numberFormat
     *            the number format.
     * @param unitFormat
     *            the unit format.
     * @return the corresponding format.
     */
    public static NumberDelimiterQuantityFormat getInstance(NumberFormat numberFormat, UnitFormat unitFormat) {
        return new NumberDelimiterQuantityFormat.Builder().setNumberFormat(numberFormat).setUnitFormat(unitFormat).build();
    }

    @Override
    public Appendable format(Quantity<?> quantity, Appendable dest) throws IOException {
        int fract = 0;
        /*
        if (quantity instanceof MixedQuantity) {
            final MixedQuantity<?> compQuant = (MixedQuantity<?>) quantity;
            if (compQuant.getUnit() instanceof MixedUnit) {
                final MixedUnit<?> compUnit = (MixedUnit<?>) compQuant.getUnit();
                final Number[] values = compQuant.getValues();
                if (values.length == compUnit.getUnits().size()) {
                    final StringBuffer sb = new StringBuffer(); // we use StringBuffer here because of java.text.Format compatibility
                    for (int i = 0; i < values.length; i++) {
                        if (values[i] != null) {
                            fract = getFractionDigitsCount(values[i].doubleValue());
                        } else {
                            fract = 0;
                        }
                        if (fract > 1) {
                            numberFormat.setMaximumFractionDigits(fract + 1);
                        }
                        sb.append(numberFormat.format(values[i]));
                        sb.append(delimiter);
                        sb.append(unitFormat.format(compUnit.getUnits().get(i)));
                        if (i < values.length - 1) {
                            sb.append((mixDelimiter != null ? mixDelimiter : DEFAULT_DELIMITER)); // we need null for parsing but not
                                                                                                            // formatting
                        }
                    }
                    return sb;
                } else {
                    throw new IllegalArgumentException(
                            String.format("%s values don't match %s in mixed unit", values.length, compUnit.getUnits().size()));
                }
            } else {
                throw new MeasurementException("A mixed quantity must contain a mixed unit");
            }
        } else {
        */
            if (quantity != null && quantity.getValue() != null) {
                fract = getFractionDigitsCount(quantity.getValue().doubleValue());
            }
            if (fract > 1) {
                numberFormat.setMaximumFractionDigits(fract + 1);
            }
            dest.append(numberFormat.format(quantity.getValue()));
            if (quantity.getUnit().equals(AbstractUnit.ONE))
                return dest;
            dest.append(delimiter);
            return unitFormat.format(quantity.getUnit(), dest);
        //}
    }

    @Override
    public Quantity<?> parse(CharSequence csq, ParsePosition cursor) throws IllegalArgumentException, MeasurementParseException {
        final String str = csq.toString();
        final int index = cursor.getIndex();
        if (mixDelimiter != null && !mixDelimiter.equals(delimiter)) {
            if (primaryUnit != null) {
                return parseCompoundAsPrimary(str, numberFormat, unitFormat, primaryUnit, delimiter, mixDelimiter, index);
            } else {
                return parseCompoundAsLeading(str, numberFormat, unitFormat, delimiter, mixDelimiter, index);
            }
        } else if (mixDelimiter != null && mixDelimiter.equals(delimiter)) {
            if (primaryUnit != null) {
                return parseCompoundAsPrimary(str, numberFormat, unitFormat, primaryUnit, delimiter, index);
            } else {
                return parseCompoundAsLeading(str, numberFormat, unitFormat, delimiter, index);
            }
        }
        final Number number = numberFormat.parse(str, cursor);
        if (number == null)
            throw new IllegalArgumentException("Number cannot be parsed");
        final String[] parts = str.substring(index).split(delimiter);
        if (parts.length < 2) {
            throw new IllegalArgumentException("No Unit found");
        }
        final Unit unit = unitFormat.parse(parts[1]);
        return Quantities.getQuantity(number, unit);
    }

    @Override
    protected Quantity<?> parse(CharSequence csq, int index) throws IllegalArgumentException, MeasurementParseException {
        return parse(csq, new ParsePosition(index));
    }

    @Override
    public Quantity<?> parse(CharSequence csq) throws IllegalArgumentException, MeasurementParseException {
        return parse(csq, 0);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean isLocaleSensitive() {
        return localeSensitive;
    }

    @Override
    protected StringBuffer formatCompound(CompoundQuantity<?> comp, StringBuffer dest) {
        final StringBuffer sb = new StringBuffer();
        int i = 0;
        for (Quantity<?> q : comp.getQuantities()) {
            sb.append(format(q));
            if (i < comp.getQuantities().size() - 1 ) {
                sb.append((mixDelimiter != null ? mixDelimiter : DEFAULT_DELIMITER)); // we need null for parsing but not
            }
            i++;
        }
        return sb;
    }

    public CompoundQuantity<?> parseCompound(CharSequence csq, ParsePosition cursor) throws IllegalArgumentException, MeasurementParseException {
        final String str = csq.toString();
        final int index = cursor.getIndex();
        if (mixDelimiter != null && !mixDelimiter.equals(delimiter)) {
                return CommonFormatter.parseCompound(str, numberFormat, unitFormat, delimiter, mixDelimiter, index);
        } else if (mixDelimiter != null && mixDelimiter.equals(delimiter)) {
                return CommonFormatter.parseCompound(str, numberFormat, unitFormat, delimiter, index);
        }
        final Number number = numberFormat.parse(str, cursor);
        if (number == null)
            throw new IllegalArgumentException("Number cannot be parsed");
        final String[] parts = str.substring(index).split(delimiter);
        if (parts.length < 2) {
            throw new IllegalArgumentException("No Unit found");
        }
        final Unit unit = unitFormat.parse(parts[1]);
        return CompoundQuantity.of(Quantities.getQuantity(number, unit));
    }

    protected CompoundQuantity<?> parseCompound(CharSequence csq, int index) throws IllegalArgumentException, MeasurementParseException {
        return parseCompound(csq, new ParsePosition(index));
    }

    public CompoundQuantity<?> parseCompound(CharSequence csq) throws IllegalArgumentException, MeasurementParseException {
        return parseCompound(csq, 0);
    }

    // Private helper methods

    private static int getFractionDigitsCount(double d) {
        if (d >= 1) { // we only need the fraction digits
            d = d - (long) d;
        }
        if (d == 0) { // nothing to count
            return 0;
        }
        d *= 10; // shifts 1 digit to left
        int count = 1;
        while (d - (long) d != 0) { // keeps shifting until there are no more
            // fractions
            d *= 10;
            count++;
        }
        return count;
    }

}
