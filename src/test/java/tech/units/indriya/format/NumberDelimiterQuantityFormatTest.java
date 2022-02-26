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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.measure.Quantity;

import org.junit.jupiter.api.Test;

import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

/**
 * @author Brad Hards
 *
 */
public class NumberDelimiterQuantityFormatTest {
    private final Quantity<?> quantityInteger = Quantities.getQuantity(Short.valueOf("10"), Units.CELSIUS);
    private final Quantity<?> quantityWithDecimals = Quantities.getQuantity(Double.valueOf("10.123"), Units.CELSIUS);

    @Test
    public void testFormatDefaultInstance() {
        NumberDelimiterQuantityFormat uut = NumberDelimiterQuantityFormat.getInstance();
        assertEquals(uut.format(quantityInteger), "10 ℃");
        assertEquals(uut.format(quantityWithDecimals), "10.123 ℃");
    }

    @Test
    public void testFormatNumberFormatInstance() {
        NumberFormat numberFormat = new DecimalFormat();
        numberFormat.setMinimumFractionDigits(2); // used
        numberFormat.setMaximumFractionDigits(2); // ignored
        NumberDelimiterQuantityFormat uut = NumberDelimiterQuantityFormat.getInstance(numberFormat, SimpleUnitFormat.getInstance());
        assertEquals(uut.format(quantityInteger), "10.00 ℃");
        assertEquals(uut.format(quantityWithDecimals), "10.123 ℃");
    }

    @Test
    public void testFormatBuilderOverrideDefault() {
        NumberFormat numberFormat = new DecimalFormat();
        numberFormat.setMinimumFractionDigits(2); // used
        numberFormat.setMaximumFractionDigits(2); // ignored
        NumberDelimiterQuantityFormat uut = new NumberDelimiterQuantityFormat.Builder()
            .setNumberFormat(numberFormat)
            .setUnitFormat(SimpleUnitFormat.getInstance())
            .build();
        assertEquals(uut.format(quantityInteger), "10.00 ℃");
        assertEquals(uut.format(quantityWithDecimals), "10.123 ℃");
    }

    @Test
    public void testFormatBuilderOverrideExplicit() {
        NumberFormat numberFormat = new DecimalFormat();
        numberFormat.setMinimumFractionDigits(2); // used
        numberFormat.setMaximumFractionDigits(2); // ignored
        NumberDelimiterQuantityFormat uut = new NumberDelimiterQuantityFormat.Builder()
            .setNumberFormat(numberFormat)
            .setUnitFormat(SimpleUnitFormat.getInstance())
            .setOverrideNumberFormatFractionalDigits(true)
            .build();
        assertEquals(uut.format(quantityInteger), "10.00 ℃");
        assertEquals(uut.format(quantityWithDecimals), "10.123 ℃");
    }

    @Test
    public void testFormatBuilderNoOverrideMinDigits() {
        NumberFormat numberFormat = new DecimalFormat();
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        NumberDelimiterQuantityFormat uut = new NumberDelimiterQuantityFormat.Builder()
            .setNumberFormat(numberFormat)
            .setUnitFormat(SimpleUnitFormat.getInstance())
            .setOverrideNumberFormatFractionalDigits(false)
            .build();
        assertEquals(uut.format(quantityInteger), "10.00 ℃");
        assertEquals(uut.format(quantityWithDecimals), "10.12 ℃");
    }

}
