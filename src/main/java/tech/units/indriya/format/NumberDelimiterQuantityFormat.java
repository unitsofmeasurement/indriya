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

import static tech.units.indriya.format.FormatBehavior.LOCALE_NEUTRAL;
import static tech.units.indriya.format.CommonFormatter.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParsePosition;
import javax.measure.MeasurementException;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.format.MeasurementParseException;
import javax.measure.format.UnitFormat;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.CompoundQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.CompoundUnit;

/**
 * An implementation of {@link javax.measure.format.QuantityFormat QuantityFormat} combining {@linkplain NumberFormat} and {@link UnitFormat}
 * separated by a delimiter.
 * 
 * @version 1.5, $Date: 2019-02-12 $
 * @since 2.0
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class NumberDelimiterQuantityFormat extends AbstractQuantityFormat {

    /**
     * Holds the default format instance (EBNFUnitFormat).
     */
    private static final NumberDelimiterQuantityFormat EBNF = new NumberDelimiterQuantityFormat(NumberFormat.getInstance(),
            EBNFUnitFormat.getInstance());

    /**
     * Holds the localized format instance.
     */
    private static final NumberDelimiterQuantityFormat LOCAL = new NumberDelimiterQuantityFormat(NumberFormat.getInstance(),
            LocalUnitFormat.getInstance());

    /**
     * 
     */
    private static final long serialVersionUID = 3546952599885869402L;

    private final transient NumberFormat numberFormat;

    private final UnitFormat unitFormat;
    
    private final String delimiter;

    private final String compoundDelimiter;
    
    private NumberDelimiterQuantityFormat(NumberFormat numberFormat, UnitFormat unitFormat, String delim, String compDelim) {
        this.numberFormat = numberFormat;
        this.unitFormat = unitFormat;
        this.delimiter = delim;
        this.compoundDelimiter = compDelim;
    }
    
    private NumberDelimiterQuantityFormat(NumberFormat numberFormat, UnitFormat unitFormat, String delim) {
        this(numberFormat, unitFormat, delim, null);
    }

    private NumberDelimiterQuantityFormat(NumberFormat numberFormat, UnitFormat unitFormat) {
        this(numberFormat, unitFormat, DEFAULT_DELIMITER, DEFAULT_DELIMITER);
    }
    
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

    @Override
    public Appendable format(Quantity<?> quantity, Appendable dest) throws IOException {
        int fract = 0;
        if (quantity instanceof CompoundQuantity) {
            final CompoundQuantity<?> compQuant = (CompoundQuantity<?>) quantity;
            if (compQuant.getUnit() instanceof CompoundUnit) {
                final CompoundUnit<?> compUnit = (CompoundUnit<?>) compQuant.getUnit();
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
                            sb.append(compoundDelimiter);
                        }
                    }
                    return sb;
                } else {
                    throw new IllegalArgumentException(
                            String.format("%s values don't match %s in Compound Unit", values.length, compUnit.getUnits().size()));
                }
            } else {
                throw new MeasurementException("A Compound Quantity must contain a Compound Unit");
            }
        } else {
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
        }
    }

    @Override
    public ComparableQuantity<?> parse(CharSequence csq, ParsePosition cursor) throws IllegalArgumentException, MeasurementParseException {
        final String str = csq.toString();
        if (compoundDelimiter != null && !compoundDelimiter.equals(delimiter)) {
            return parseCompound(str, numberFormat, unitFormat, delimiter, compoundDelimiter);
        } 
        final Number number = numberFormat.parse(str, cursor);
        if (number == null)
            throw new IllegalArgumentException("Number cannot be parsed");
        final String[] parts = str.split(delimiter);
        if (parts.length < 2) {
            throw new IllegalArgumentException("No Unit found");
        }
        final Unit unit = unitFormat.parse(parts[1]);
        return Quantities.getQuantity(number, unit);
    }

    @Override
    protected ComparableQuantity<?> parse(CharSequence csq, int index) throws IllegalArgumentException, MeasurementParseException {
        return parse(csq, new ParsePosition(index));
    }

    @Override
    public ComparableQuantity<?> parse(CharSequence csq) throws IllegalArgumentException, MeasurementParseException {
        return parse(csq, 0);
    }

    /**
     * Returns the culture invariant format based upon {@link BigDecimal} canonical format and the {@link UnitFormat#getStandardInstance() standard}
     * unit format. This format <b>is not</b> locale-sensitive and can be used for unambiguous electronic communication of quantities together with
     * their units without loss of information. For example: <code>"1.23456789 kg.m/s2"</code> returns
     * <code>Quantities.getQuantity(new BigDecimal("1.23456789"), AbstractUnit.parse("kg.m/s2")));</code>
     *
     * @param style
     *            the format style to apply.
     * @return <code>NumberDelimiterQuantityFormat.getInstance(NumberFormat.getInstance(), UnitFormat.getInstance())</code>
     */
    public static NumberDelimiterQuantityFormat getInstance(FormatBehavior style) {
        switch (style) {
            case LOCALE_NEUTRAL:
                return EBNF;
            case LOCALE_SENSITIVE:
                return LOCAL;
            default:
                return EBNF;
        }
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
        return new NumberDelimiterQuantityFormat(numberFormat, unitFormat);
    }
    
    /**
     * Returns the quantity format using the specified number format, unit format and delimiter. The number and unit are separated by the delimiter.
     *
     * @param numberFormat
     *            the number format.
     * @param unitFormat
     *            the unit format.
     * @param delimiter
     *            the delimiter.
     * @return the corresponding format.
     */
    public static NumberDelimiterQuantityFormat getInstance(NumberFormat numberFormat, UnitFormat unitFormat, String delimiter) {
        return new NumberDelimiterQuantityFormat(numberFormat, unitFormat, delimiter);
    }
    
    /**
     * Returns the quantity format using the specified number format, unit format and delimiter. The numbers and units are separated by the delimiter.
     *
     * @param numberFormat
     *            the number format.
     * @param unitFormat
     *            the unit format.
     * @param delimiter
     *            the delimiter.
     * @return the corresponding format.
     */
    public static NumberDelimiterQuantityFormat getCompoundInstance(NumberFormat numberFormat, UnitFormat unitFormat, String delimiter) {
        return new NumberDelimiterQuantityFormat(numberFormat, unitFormat, delimiter, null);
    }
    
    /**
     * Returns the quantity format using the specified number format, unit format and delimiters. The numbers and units are separated by the delimiters.
     *
     * @param numberFormat
     *            the number format.
     * @param unitFormat
     *            the unit format.
     * @param delimiter
     *            the delimiter.
     * @param compDelimiter
     *            the compound delimiter.
     * @return the corresponding format.
     */
    public static NumberDelimiterQuantityFormat getCompoundInstance(NumberFormat numberFormat, UnitFormat unitFormat, String delimiter, String compDelimiter) {
        return new NumberDelimiterQuantityFormat(numberFormat, unitFormat, delimiter, compDelimiter);
    }
}