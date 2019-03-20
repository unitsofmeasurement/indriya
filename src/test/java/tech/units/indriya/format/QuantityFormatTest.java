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

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static javax.measure.MetricPrefix.*;
import static tech.units.indriya.unit.Units.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParsePosition;
import java.util.Locale;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.format.MeasurementParseException;
import javax.measure.format.QuantityFormat;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tech.units.indriya.quantity.CompoundQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

/**
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 *
 */
public class QuantityFormatTest {
    private Quantity<Length> sut;
    private SimpleQuantityFormat format;

    @BeforeEach
    public void init() {
        // sut =
        // DefaultQuantityFactoryService.getQuantityFactory(Length.class).create(10,
        // METRE);
        sut = Quantities.getQuantity(10, METRE);
        format = SimpleQuantityFormat.getInstance();
    }

    @Test
    public void testFormat() {
        Unit<Frequency> hz = HERTZ;
        Quantity<Frequency> tenHz = Quantities.getQuantity(10, hz);
        assertEquals("10 Hz", format.format(tenHz));
    }

    @Test
    public void testFormat2() {
        Unit<Frequency> mhz = MEGA(HERTZ);
        Quantity<Frequency> oneMHz = Quantities.getQuantity(1, mhz);
        assertEquals("1 MHz", oneMHz.toString());
    }

    @Test
    public void testFormat3() {
        Unit<Frequency> khz = KILO(HERTZ);
        Quantity<Frequency> fiveKhz = Quantities.getQuantity(5, khz);
        assertEquals("5 kHz", format.format(fiveKhz));
    }

    @Test
    public void testFormatDelim() {
        final NumberDelimiterQuantityFormat format1 = NumberDelimiterQuantityFormat.getInstance(DecimalFormat.getInstance(),
                SimpleUnitFormat.getInstance(), "_");
        final Unit<Length> cm = CENTI(Units.METRE);
        final Quantity<Length> l1 = Quantities.getQuantity(150, cm);
        assertEquals("150_cm", format1.format(l1));
    }

    @Test
    public void testFormatCompound1() {
        final NumberDelimiterQuantityFormat format1 = NumberDelimiterQuantityFormat.getInstance(DecimalFormat.getInstance(),
                SimpleUnitFormat.getInstance());
        final Unit<Length> compLen = Units.METRE.compound(CENTI(Units.METRE));
        final Number[] numList = { 1, 70 };
        final Quantity<Length> l1 = Quantities.getCompoundQuantity(numList, compLen);

        assertEquals("1 m 70 cm", format1.format(l1));
    }

    @Test
    public void testFormatCompound2() {
        final NumberDelimiterQuantityFormat format1 = NumberDelimiterQuantityFormat.getInstance(new DecimalFormat("#.000", DecimalFormatSymbols.getInstance(Locale.ENGLISH)),
                SimpleUnitFormat.getInstance());
        final Unit<Length> compLen = Units.METRE.compound(CENTI(Units.METRE));
        final Number[] numList = { 1, 70 };
        final Quantity<Length> l1 = Quantities.getCompoundQuantity(numList, compLen);

        assertEquals("1.000 m 70.000 cm", format1.format(l1));
    }

    @Test
    public void testFormatCompoundDelim() {
        final NumberDelimiterQuantityFormat format1 = new NumberDelimiterQuantityFormat.Builder()
                .setNumberFormat(DecimalFormat.getInstance(Locale.ENGLISH))
                .setUnitFormat(SimpleUnitFormat.getInstance())
                .setDelimiter("_").setCompoundDelimiter("_")
                .build();
        final Unit<Length> compLen = Units.METRE.compound(CENTI(Units.METRE));
        final Number[] numList = { 1, 70 };
        final Quantity<Length> l1 = Quantities.getCompoundQuantity(numList, compLen);

        assertEquals("1_m_70_cm", format1.format(l1));
    }

    @Test
    public void testFormatCompoundDelims() {
        final NumberDelimiterQuantityFormat format1 = new NumberDelimiterQuantityFormat.Builder()
                .setNumberFormat(DecimalFormat.getInstance(Locale.ENGLISH))
                .setUnitFormat(SimpleUnitFormat.getInstance())
                .setDelimiter("_")
                .setCompoundDelimiter(" ")
                .build();
        final Unit<Length> compLen = Units.METRE.compound(CENTI(Units.METRE));
        final Number[] numList = { 1, 70 };
        final Quantity<Length> l1 = Quantities.getCompoundQuantity(numList, compLen);

        assertEquals("1_m 70_cm", format1.format(l1));
    }

    @Test
    public void testFormatCompoundDelims2() {
        final NumberDelimiterQuantityFormat format1 = new NumberDelimiterQuantityFormat.Builder()
                .setNumberFormat(DecimalFormat.getInstance(Locale.ENGLISH))
                .setUnitFormat(SimpleUnitFormat.getInstance())
                .setDelimiter(" ").setCompoundDelimiter(":").build();
        Unit<Time> compTime = Units.HOUR.compound(Units.MINUTE).compound(Units.SECOND);
        final Number[] numList = { 1, 40, 10 };
        final Quantity<Time> t1 = Quantities.getCompoundQuantity(numList, compTime);
        assertEquals("1 h:40 min:10 s", format1.format(t1));
    }

    @Test
    public void testParseSimpleTime() {
        Quantity<?> parsed1 = SimpleQuantityFormat.getInstance().parse("10 min");
        assertNotNull(parsed1);
        assertEquals(BigDecimal.valueOf(10), parsed1.getValue());
        assertEquals(Units.MINUTE, parsed1.getUnit());
    }

    @Test
    public void testParseSimpleLen() {
        Quantity<?> parsed1 = format.parse("60 m");
        assertNotNull(parsed1);
        assertEquals(BigDecimal.valueOf(60), parsed1.getValue());
        assertEquals(Units.METRE, parsed1.getUnit());
    }

    @Test
    public void testParseAsType() {
        Quantity<Length> parsed1 = SimpleQuantityFormat.getInstance().parse("60 m").asType(Length.class);
        assertNotNull(parsed1);
        assertEquals(BigDecimal.valueOf(60), parsed1.getValue());
        assertEquals(Units.METRE, parsed1.getUnit());
    }

    @Test
    public void testParseSimpleMass() {
        try {
            Quantity<?> parsed1 = format.parse("5 kg");
            assertNotNull(parsed1);
            assertEquals(BigDecimal.valueOf(5), parsed1.getValue());
            assertNotNull(parsed1.getUnit());
            assertEquals("kg", parsed1.getUnit().getSymbol());
            assertEquals(KILOGRAM, parsed1.getUnit());
        } catch (MeasurementParseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testPattern() {
        final SimpleQuantityFormat patternFormat = SimpleQuantityFormat.getInstance("n u");
        assertEquals("n u", patternFormat.getPattern());
        assertEquals("10 m", patternFormat.format(sut));
    }

    @Test
    public void testAnotherPattern() {
        final SimpleQuantityFormat patternFormat = SimpleQuantityFormat.getInstance("n_u");
        assertEquals("n_u", patternFormat.getPattern());
        assertEquals("10_m", patternFormat.format(sut));
    }
    
    @Test
    public void testCondensedPattern() {
        final SimpleQuantityFormat patternFormat = SimpleQuantityFormat.getInstance("nu");
        assertEquals("nu", patternFormat.getPattern());
        assertEquals("10m", patternFormat.format(sut));
    }

    @Test
    public void testNDFBuilder() {
        QuantityFormat quantFormat = new NumberDelimiterQuantityFormat.Builder().
			setNumberFormat(DecimalFormat.getInstance(Locale.ENGLISH)).
			setUnitFormat(SimpleUnitFormat.getInstance()).setDelimiter("_").
                build();
        final Unit<Length> cm = CENTI(Units.METRE);
        final Quantity<Length> l1 = Quantities.getQuantity(150, cm);
        assertEquals("150_cm", quantFormat.format(l1));
    }
    
    @Test
    public void testNDFBuilderNullNumFormat() {
        assertThrows(NullPointerException.class, () -> {
            @SuppressWarnings("unused")
            QuantityFormat quantFormat = new NumberDelimiterQuantityFormat.Builder().
                    setNumberFormat(null).build();
        });
    }
    
    @Test
    public void testNDFBuilderNullUnitFormat() {
        assertThrows(NullPointerException.class, () -> {
            @SuppressWarnings("unused")
            QuantityFormat quantFormat = new NumberDelimiterQuantityFormat.Builder().
                    setUnitFormat(null).build();
        });
    }

    @Test
    public void testParseDelim1() {
        QuantityFormat format1 = NumberDelimiterQuantityFormat.getInstance(DecimalFormat.getInstance(), SimpleUnitFormat.getInstance());
        Quantity<?> parsed1 = format1.parse("1 m");
        assertEquals(1L, parsed1.getValue());
        assertEquals(METRE, parsed1.getUnit());
    }

    @Test
    public void testParseDelim2() {
        assertThrows(IllegalArgumentException.class, () -> {
            QuantityFormat format1 = NumberDelimiterQuantityFormat.getInstance(DecimalFormat.getInstance(), SimpleUnitFormat.getInstance());
            @SuppressWarnings("unused")
            Quantity<?> parsed1 = format1.parse("1");
        });
    }

    @Test
    public void testParseDelim3() {
        assertThrows(IllegalArgumentException.class, () -> {
            QuantityFormat format1 = NumberDelimiterQuantityFormat.getInstance(DecimalFormat.getInstance(), SimpleUnitFormat.getInstance());
            @SuppressWarnings("unused")
            Quantity<?> parsed1 = format1.parse("m");
        });
    }
    
    @Test
    public void testParseAtPosition() {
        QuantityFormat format1 = NumberDelimiterQuantityFormat.getInstance(DecimalFormat.getInstance(), SimpleUnitFormat.getInstance());
        Quantity<?> parsed1 = format1.parse("1 km 2 m", new ParsePosition(5));
        assertEquals(2L, parsed1.getValue());
        assertEquals(METRE, parsed1.getUnit());
    }
    
    @Test
    public void testParseCompoundSimpleTime() {
        QuantityFormat format1 = SimpleQuantityFormat.getInstance("n u~:");
        Quantity<?> parsed1 = format1.parse("1 h:30 min:10 s");
        assertEquals(5410L, parsed1.getValue());
        assertEquals(HOUR.compound(MINUTE).compound(SECOND), parsed1.getUnit());
        assertTrue(parsed1 instanceof CompoundQuantity);
        final Number[] values = ((CompoundQuantity<?>)parsed1).getValues();
        assertEquals(3, values.length);
        assertEquals(1L, values[0]);
        assertEquals(30L, values[1]);
        assertEquals(10L, values[2]);
    }
    
    @Test
    public void testParseCompoundSimpleSameDelimTime() {
        QuantityFormat format1 = SimpleQuantityFormat.getInstance("n u~ ");
        Quantity<?> parsed1 = format1.parse("1 h 30 min 10 s");
        assertEquals(5410L, parsed1.getValue());
        assertEquals(HOUR.compound(MINUTE).compound(SECOND), parsed1.getUnit());
        assertTrue(parsed1 instanceof CompoundQuantity);
        final Number[] values = ((CompoundQuantity<?>)parsed1).getValues();
        assertEquals(3, values.length);
        assertEquals(1L, values[0]);
        assertEquals(30L, values[1]);
        assertEquals(10L, values[2]);
    }
    
    @Test
    public void testParseCompoundNumDelimiterLen() {
        QuantityFormat format1 = NumberDelimiterQuantityFormat.getCompoundInstance(DecimalFormat.getInstance(Locale.ENGLISH), SimpleUnitFormat.getInstance(), " ", ";");
        Quantity<?> parsed1 = format1.parse("1 m;30 cm");
        assertEquals(130L, parsed1.getValue());
        assertEquals(METRE.compound(CENTI(METRE)), parsed1.getUnit());
        assertTrue(parsed1 instanceof CompoundQuantity);
        assertEquals(2, ((CompoundQuantity<?>)parsed1).getValues().length);
    }
    
    @Test
    public void testParseCompoundNumDelimiterSameDelimLen() {
        QuantityFormat format1 = NumberDelimiterQuantityFormat.getCompoundInstance(DecimalFormat.getInstance(Locale.ENGLISH), SimpleUnitFormat.getInstance(), " ", " ");
        Quantity<?> parsed1 = format1.parse("1 m 30 cm");
        assertEquals(130L, parsed1.getValue());
        assertEquals(METRE.compound(CENTI(METRE)), parsed1.getUnit());
        assertTrue(parsed1 instanceof CompoundQuantity);
        assertEquals(2, ((CompoundQuantity<?>)parsed1).getValues().length);
    }
}
