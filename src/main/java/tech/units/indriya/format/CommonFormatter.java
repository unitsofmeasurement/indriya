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
package tech.units.indriya.format;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.format.MeasurementParseException;
import javax.measure.format.UnitFormat;

import tech.units.indriya.internal.format.RationalNumberFormat;
import tech.units.indriya.quantity.MixedQuantity;
import tech.units.indriya.quantity.Quantities;

/**
 * Common helper class that handles internals of formatting in {@link SimpleQuantityFormat}, {@link NumberDelimiterQuantityFormat}
 * 
 * @author keilw
 * @version 2.0
 * @since 2.0
 */
abstract class CommonFormatter {
    private static final String ERR_PRIMARY_UNIT_NOT_FOUND = "The primary unit <%s> is not part of the compound units <%s>"; //$NON-NLS-1$
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    static MixedQuantity<?> parseMixed(final String str, final NumberFormat numberFormat, final UnitFormat unitFormat, final String delimiter,
            final String mixDelimiter, final int position) throws IllegalArgumentException, MeasurementParseException {
        final String section = str.substring(position);
        final String[] sectionParts = section.split(mixDelimiter);
        final List<Quantity> quants = new ArrayList<>();
        final RationalNumberFormat rationalNumberFormat = RationalNumberFormat.wrap(numberFormat);  
        for (String compStr : sectionParts) {
            final String[] parts = compStr.split(delimiter);
            if (parts.length < 2) {
                throw new IllegalArgumentException("No Unit found");
            } else {
                Number num = null;
                try {
                    num = rationalNumberFormat.parse(parts[0]);
                } catch (ParseException pe) {
                    throw new MeasurementParseException(pe);
                }
                Unit unit = unitFormat.parse(parts[1]);
                if (num != null && unit != null) {
                    quants.add(Quantities.getQuantity(num, unit));
                }
            }
        }
        final Quantity[] qArray = new Quantity[quants.size()];
        quants.toArray(qArray);
        MixedQuantity<?> comp = MixedQuantity.of(qArray);
        return comp;
    }
    
    static MixedQuantity<?> parseMixed(final String str, final NumberFormat numberFormat, final UnitFormat unitFormat, final String delimiter,
            final String mixDelimiter) throws IllegalArgumentException, MeasurementParseException {
        return parseMixed(str, numberFormat, unitFormat, delimiter, mixDelimiter, 0);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    static MixedQuantity<?> parseMixed(final String str, final NumberFormat numberFormat, final UnitFormat unitFormat, final String delimiter, final int position) throws IllegalArgumentException, MeasurementParseException {
        Objects.requireNonNull(str);
        final String section = str.substring(position);
        final List<Quantity<?>> quants = new ArrayList<>();
        final RationalNumberFormat rationalNumberFormat = RationalNumberFormat.wrap(numberFormat);
        final String[] parts = section.split(delimiter);
        if (parts.length < 2) {
            throw new IllegalArgumentException("No Unit found");
        } else {
            for (int i=0; i < parts.length-1; i++) {
                Number num = null;
                try {
                    num = rationalNumberFormat.parse(parts[i]);
                } catch (ParseException pe) {
                    throw new MeasurementParseException(pe);
                }
                Unit unit = unitFormat.parse(parts[i+1]);
                if (num != null && unit != null) {
                    quants.add(Quantities.getQuantity(num, unit));
                }
                i++; // get to next number
            }
        }
        Quantity[] qArray = new Quantity[quants.size()];
        qArray= quants.toArray(qArray);
        MixedQuantity<?> comp = MixedQuantity.of(qArray);
        return comp;
    }
    
    static MixedQuantity<?> parseMixed(final String str, final NumberFormat numberFormat, final UnitFormat unitFormat, final String delimiter) throws IllegalArgumentException, MeasurementParseException {
        return parseMixed(str, numberFormat, unitFormat, delimiter, 0);
    }
    
    // Decompose into Quantity
    
    @SuppressWarnings("unchecked")
    static  Quantity<?> parseMixedAsLeading(final String str, final NumberFormat numberFormat, final UnitFormat unitFormat, final String delimiter, final String compDelim, final int position) throws IllegalArgumentException, MeasurementParseException {
        final MixedQuantity<?> comp = parseMixed(str, numberFormat, unitFormat, delimiter, compDelim, position);
        @SuppressWarnings("rawtypes")
        final Unit u = getLeadingUnit(comp);
        return comp.to(u);
    }
    
    @SuppressWarnings("unchecked")
    static  Quantity<?> parseMixedAsLeading(final String str, final NumberFormat numberFormat, final UnitFormat unitFormat, final String delimiter, final int position) throws IllegalArgumentException, MeasurementParseException {
        final MixedQuantity<?> comp = parseMixed(str, numberFormat, unitFormat, delimiter, position);
        @SuppressWarnings("rawtypes")
        final Unit u = getLeadingUnit(comp);
        return comp.to(u);
    }
    
    static  Quantity<?> parseMixedAsLeading(final String str, final NumberFormat numberFormat, final UnitFormat unitFormat, final String delimiter) throws IllegalArgumentException, MeasurementParseException {
        return parseMixedAsLeading(str, numberFormat, unitFormat, delimiter, 0);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    static Quantity<?> parseMixedAsPrimary(final String str, final NumberFormat numberFormat, final UnitFormat unitFormat, final Unit primaryUnit, final String delimiter, final String compDelim, final int position) throws IllegalArgumentException, MeasurementParseException {
        final MixedQuantity<?> comp = parseMixed(str, numberFormat, unitFormat, delimiter, compDelim, position);
        if (comp.getUnits().contains(primaryUnit)) {
            return comp.to(primaryUnit);
        } else {
            throw new IllegalArgumentException(String.format(ERR_PRIMARY_UNIT_NOT_FOUND, primaryUnit, comp.getUnits()));
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    static Quantity<?> parseMixedAsPrimary(final String str, final NumberFormat numberFormat, final UnitFormat unitFormat, final Unit primaryUnit, final String delimiter, final int position) throws IllegalArgumentException, MeasurementParseException {
        final MixedQuantity<?> comp = parseMixed(str, numberFormat, unitFormat, delimiter, position);
        if (comp.getUnits().contains(primaryUnit)) {
            return comp.to(primaryUnit);
        } else {
            throw new IllegalArgumentException(String.format(ERR_PRIMARY_UNIT_NOT_FOUND, primaryUnit, comp.getUnits()));
        }
    }
    
    static Quantity<?> parseMixedAsPrimary(final String str, final NumberFormat numberFormat, final UnitFormat unitFormat, @SuppressWarnings("rawtypes") final Unit primaryUnit, final String delimiter) throws IllegalArgumentException, MeasurementParseException {
        return parseMixedAsPrimary(str, numberFormat, unitFormat, primaryUnit, delimiter, 0);
    }
    
    // Private helpers
    
    private static final <Q extends Quantity<Q>> Unit<Q> getLeadingUnit(final MixedQuantity<Q> comp) {
        Objects.requireNonNull(comp);
        Unit<Q> unit = null;
        for(Unit<Q> u: comp.getUnits()) {
            unit = u;
            break;
        }
        return unit;
    }
}
