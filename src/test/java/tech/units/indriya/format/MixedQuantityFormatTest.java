/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2024, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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

import static javax.measure.MetricPrefix.CENTI;
import static javax.measure.MetricPrefix.KILO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static tech.units.indriya.NumberAssertions.assertNumberEquals;
import static tech.units.indriya.function.MixedRadixTest.USCustomary.FOOT;
import static tech.units.indriya.function.MixedRadixTest.USCustomary.INCH;
import static tech.units.indriya.function.MixedRadixTest.USCustomary.PICA;
import static tech.units.indriya.unit.Units.HOUR;
import static tech.units.indriya.unit.Units.KILOGRAM;
import static tech.units.indriya.unit.Units.METRE;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.measure.Quantity;
import javax.measure.format.QuantityFormat;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import org.junit.jupiter.api.Test;

import tech.units.indriya.NumberAssertions;
import tech.units.indriya.function.MixedRadix;
import tech.units.indriya.quantity.MixedQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

/**
 * @author <a href="mailto:werner@units.tech">Werner Keil</a>
 * @author Andi Huber
 *
 */
public class MixedQuantityFormatTest {

    static {
        SimpleUnitFormat.getInstance().label(FOOT, "ft");
        SimpleUnitFormat.getInstance().label(INCH, "in");
        SimpleUnitFormat.getInstance().label(PICA, "P̸");
    }
	
/*
    @Test
    public void testFormatMixed1() {
        final NumberDelimiterQuantityFormat format1 = NumberDelimiterQuantityFormat.getInstance(DecimalFormat.getInstance(),
                SimpleUnitFormat.getInstance());
        final Unit<Length> compLen = Units.METRE.mix(CENTI(Units.METRE));
        final Number[] numList = { 1, 70 };
        final Quantity<Length> l1 = Quantities.getMixedQuantity(numList, compLen);

        assertEquals("1 m 70 cm", format1.format(l1));
    }

    @Test
    public void testFormatMixed2() {
        final NumberDelimiterQuantityFormat format1 = NumberDelimiterQuantityFormat.getInstance(new DecimalFormat("#.000", DecimalFormatSymbols.getInstance(Locale.ENGLISH)),
                SimpleUnitFormat.getInstance());
        final Unit<Length> compLen = Units.METRE.mix(CENTI(Units.METRE));
        final Number[] numList = { 1, 70 };
        final Quantity<Length> l1 = Quantities.getMixedQuantity(numList, compLen);

        assertEquals("1.000 m 70.000 cm", format1.format(l1));
    }

    @Test
    public void testFormatMixDelim() {
        final NumberDelimiterQuantityFormat format1 = new NumberDelimiterQuantityFormat.Builder()
                .setNumberFormat(DecimalFormat.getInstance(Locale.ENGLISH))
                .setUnitFormat(SimpleUnitFormat.getInstance())
                .setDelimiter("_").setMixDelimiter("_")
                .build();
        final Unit<Length> compLen = Units.METRE.mix(CENTI(Units.METRE));
        final Number[] numList = { 1, 70 };
        final Quantity<Length> l1 = Quantities.getMixedQuantity(numList, compLen);

        assertEquals("1_m_70_cm", format1.format(l1));
    }

    @Test
    public void testFormatMixDelims() {
        final NumberDelimiterQuantityFormat format1 = new NumberDelimiterQuantityFormat.Builder()
                .setNumberFormat(DecimalFormat.getInstance(Locale.ENGLISH))
                .setUnitFormat(SimpleUnitFormat.getInstance())
                .setDelimiter("_").setMixDelimiter(" ")
                .build();
        final Unit<Length> compLen = Units.METRE.mix(CENTI(Units.METRE));
        final Number[] numList = { 1, 70 };
        final Quantity<Length> l1 = Quantities.getMixedQuantity(numList, compLen);

        assertEquals("1_m 70_cm", format1.format(l1));
    }

    @Test
    public void testFormatMixDelims2() {
        final NumberDelimiterQuantityFormat format1 = new NumberDelimiterQuantityFormat.Builder()
                .setNumberFormat(DecimalFormat.getInstance(Locale.ENGLISH))
                .setUnitFormat(SimpleUnitFormat.getInstance())
                .setDelimiter(" ").setMixDelimiter(":").build();
        Unit<Time> compTime = Units.HOUR.mix(Units.MINUTE).mix(Units.SECOND);
        final Number[] numList = { 1, 40, 10 };
        final Quantity<Time> t1 = Quantities.getMixedQuantity(numList, compTime);
        assertEquals("1 h:40 min:10 s", format1.format(t1));
    }
*/
      
    @Test
    public void testParseCompSimpleTime() {
        QuantityFormat format1 = SimpleQuantityFormat.getInstance("n u~:");
        Quantity<Time> parsed1 = format1.parse("1 h:30 min:10 s").asType(Time.class);
        assertNumberEquals(1.5027777777777778d, parsed1.getValue(), 1E-12);
        assertEquals(HOUR, parsed1.getUnit());
    }
    
    @Test
    public void testParseCompSimpleSameDelimTime() {
        QuantityFormat format1 = SimpleQuantityFormat.getInstance("n u~ ");
        Quantity<?> parsed1 = format1.parse("1 h 30 min 10 s");
        assertNumberEquals(1.5027777777777778d, parsed1.getValue(), 1E-12);
        assertEquals(HOUR, parsed1.getUnit());
    }
    
    @Test
    public void testParseCompSingleNumDelimLen() {
        QuantityFormat format1 = new NumberDelimiterQuantityFormat.Builder()
                .setNumberFormat(DecimalFormat.getInstance(Locale.ENGLISH))
                .setUnitFormat(SimpleUnitFormat.getInstance())
                .setDelimiter(" ")
                .setRadixPartsDelimiter(";")
                .build();
        Quantity<?> parsed1 = format1.parse("1 m;30 cm");
        
        assertNumberEquals(1.3d, parsed1.getValue(), 1E-12);
        assertEquals(METRE, parsed1.getUnit());
    }
    
    @Test
    public void testParseCompSingleRationalNumDelimLen() {
        QuantityFormat format1 = new NumberDelimiterQuantityFormat.Builder()
                .setNumberFormat(DecimalFormat.getInstance(Locale.ENGLISH))
                .setUnitFormat(SimpleUnitFormat.getInstance())
                .setDelimiter(" ")
                .setRadixPartsDelimiter(";")
                .build();
        Quantity<?> parsed1 = format1.parse("1÷2 m;10 cm");
        
        assertNumberEquals(new BigDecimal("0.6"), parsed1.getValue(), 1E-12);
        assertEquals(METRE, parsed1.getUnit());
    }
 
    @Test
    public void testParseCompSingleNumDelimSameDelimLen() {
        final QuantityFormat format1 = new NumberDelimiterQuantityFormat.Builder()
                .setNumberFormat(DecimalFormat.getInstance(Locale.ENGLISH))
                .setUnitFormat(SimpleUnitFormat.getInstance())
                .setDelimiter(" ").setRadixPartsDelimiter(" ")
                .build();
        Quantity<?> parsed1 = format1.parse("1 m 30 cm");
        assertNumberEquals(1.3d, parsed1.getValue(), 1E-12);
        assertEquals(METRE, parsed1.getUnit());
    }
    
    @Test
    public void testParseCompSingleRationalNumDelimSameDelimLen() {
        final QuantityFormat format1 = new NumberDelimiterQuantityFormat.Builder()
                .setNumberFormat(DecimalFormat.getInstance(Locale.ENGLISH))
                .setUnitFormat(SimpleUnitFormat.getInstance())
                .setDelimiter(" ").setRadixPartsDelimiter(" ")
                .build();
        Quantity<?> parsed1 = format1.parse("1÷2 m 10 cm");
        assertNumberEquals(new BigDecimal("0.6"), parsed1.getValue(), 1E-12);
        assertEquals(METRE, parsed1.getUnit());
    }
    
    @Test
    public void testParseCompSingleNumDelimSameDelimPrimaryUnitLen() {
        final QuantityFormat format1 = new NumberDelimiterQuantityFormat.Builder()
                .setNumberFormat(DecimalFormat.getInstance(Locale.ENGLISH))
                .setUnitFormat(SimpleUnitFormat.getInstance())
                .setDelimiter(" ").setRadixPartsDelimiter(" ")
                .setPrimaryUnit((CENTI(METRE)))
                .build();
        Quantity<?> parsed1 = format1.parse("1 m 30 cm");
        assertNumberEquals(130, parsed1.getValue(), 1E-12);
        assertEquals(CENTI(METRE), parsed1.getUnit());
    }
    
    @Test
    public void testParseCompSingleNumDelimSameDelimWrongPrimaryUnitLen() {
        final QuantityFormat format1 = NumberDelimiterQuantityFormat.builder()
                .setNumberFormat(DecimalFormat.getInstance(Locale.ENGLISH))
                .setUnitFormat(SimpleUnitFormat.getInstance())
                .setPrimaryUnit((KILO(METRE)))
                .setDelimiter(" ").setRadixPartsDelimiter(" ")
                .build();
        
        assertThrows(IllegalArgumentException.class, () -> {
            @SuppressWarnings("unused")
            Quantity<?> parsed1 = format1.parse("1 m 30 cm");
        });
    }
    
    @Test
    public void testParseCompSingleNumDelimSameDelimWrongPrimaryUnitQuantityLen() {
        final QuantityFormat format1 = NumberDelimiterQuantityFormat.builder()
                .setNumberFormat(DecimalFormat.getInstance(Locale.ENGLISH))
                .setUnitFormat(SimpleUnitFormat.getInstance())
                .setPrimaryUnit(KILOGRAM)
                .setDelimiter(" ").setRadixPartsDelimiter(" ")
                .build();
        
        assertThrows(IllegalArgumentException.class, () -> {
            @SuppressWarnings("unused")
            Quantity<?> parsed1 = format1.parse("1 m 30 cm");
        });
    }
    
    @Test
    public void testFormatCompDelims() {
        final NumberDelimiterQuantityFormat format1 = new NumberDelimiterQuantityFormat.Builder()
                .setNumberFormat(DecimalFormat.getInstance(Locale.ENGLISH))
                .setUnitFormat(SimpleUnitFormat.getInstance())
                .setDelimiter("_").setRadixPartsDelimiter(" ")
                .build();
        final MixedQuantity<Length> l1 = MixedQuantity.of(Quantities.getQuantity(1, Units.METRE), 
                                                          Quantities.getQuantity(70, CENTI(Units.METRE)));
        assertEquals("1_m 70_cm", format1.format(l1));
    }
    
    @Test
    public void testFormatCompDelimsArray() {
        final NumberDelimiterQuantityFormat format1 = new NumberDelimiterQuantityFormat.Builder()
                .setNumberFormat(DecimalFormat.getInstance(Locale.ENGLISH))
                .setUnitFormat(SimpleUnitFormat.getInstance())
                .setDelimiter("_").setRadixPartsDelimiter(" ")
                .build();
        @SuppressWarnings("unchecked")
        final Quantity<Length>[] quants = new Quantity[] { Quantities.getQuantity(1, Units.METRE),  Quantities.getQuantity(70, CENTI(Units.METRE)) };
        final MixedQuantity<Length> l1 = MixedQuantity.of(quants);
        assertEquals("1_m 70_cm", format1.format(l1));
    }
    
    @Test
    public void testFormatCompDelimsList() {
        final NumberDelimiterQuantityFormat format1 = new NumberDelimiterQuantityFormat.Builder()
                .setNumberFormat(DecimalFormat.getInstance(Locale.ENGLISH))
                .setUnitFormat(SimpleUnitFormat.getInstance())
                .setDelimiter("_").setRadixPartsDelimiter(" ")
                .build();
        final List<Quantity<Length>> quants = new ArrayList<>();
        quants.add(Quantities.getQuantity(1, Units.METRE));
        quants.add(Quantities.getQuantity(70, CENTI(Units.METRE)));
        final MixedQuantity<Length> l1 = MixedQuantity.of(quants);
        assertEquals("1_m 70_cm", format1.format(l1));
    }
    
    @Test
    public void testFormatCompDelimsMultipleQuantities() {
        final NumberDelimiterQuantityFormat format1 = new NumberDelimiterQuantityFormat.Builder()
                .setNumberFormat(DecimalFormat.getInstance(Locale.ENGLISH))
                .setUnitFormat(SimpleUnitFormat.getInstance())
                .setDelimiter("_").setRadixPartsDelimiter(" ")
                .build();
        @SuppressWarnings("unchecked")
        final Quantity<Length>[] quants = new Quantity[] { Quantities.getQuantity(1, Units.METRE),  Quantities.getQuantity(70, CENTI(Units.METRE)) };
        MixedQuantity<Length> l1 = MixedQuantity.of(quants);

        assertEquals("1_m 70_cm", format1.format(l1));
        
        @SuppressWarnings("unchecked")
        final Quantity<Time>[] timeQuants = new Quantity[] { Quantities.getQuantity(3, Units.DAY),  Quantities.getQuantity(4, Units.HOUR), 
                Quantities.getQuantity(48, Units.MINUTE)};
        final MixedQuantity<Time> t1 = MixedQuantity.of(timeQuants);
        assertEquals("3_day 4_h 48_min", format1.format(t1));
    }
    
    @Test
    public void testParseCompNumDelimLen() {
        NumberDelimiterQuantityFormat format1 = new NumberDelimiterQuantityFormat.Builder()
                .setNumberFormat(DecimalFormat.getInstance(Locale.ENGLISH))
                .setUnitFormat(SimpleUnitFormat.getInstance())
                .setDelimiter(" ")
                .setRadixPartsDelimiter(";")
                .build();
        final MixedQuantity<?> parsed1 = format1.parseMixed("1 m;30 cm");
        assertNotNull(parsed1);
        assertThat(parsed1.getUnits(), hasSize(2));
        assertThat(parsed1.getUnits(), contains(Units.METRE, CENTI(Units.METRE)));
    }
    
    @Test
    public void testParseCompNumDelimTime() {
        final NumberDelimiterQuantityFormat format1 = new NumberDelimiterQuantityFormat.Builder()
            .setNumberFormat(DecimalFormat.getInstance(Locale.ENGLISH))
            .setUnitFormat(SimpleUnitFormat.getInstance())
            .setDelimiter(" ").setRadixPartsDelimiter(":").build();
        final MixedQuantity<?> parsed1 = format1.parseMixed("1 h:40 min:10 s");
        assertThat(parsed1.getUnits(), hasSize(3));
        assertThat(parsed1.getUnits(), contains(Units.HOUR, Units.MINUTE, Units.SECOND));
    }
    
    @Test
    public void mixedFormatting() {
        
        // given
        
        MixedRadix<Length> mixedRadix = MixedRadix
                .ofPrimary(FOOT)
                .mix(INCH)
                .mix(PICA);
        
        Quantity<Length> lengthQuantity = mixedRadix.createQuantity(1, 2, 3);
        MixedQuantity<Length> lengthMixed = mixedRadix.createMixedQuantity(1, 2, 3);
        
        DecimalFormat realFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.ENGLISH);
        realFormat.setDecimalSeparatorAlwaysShown(true);
        realFormat.setMaximumFractionDigits(3);
        
        final SimpleQuantityFormat simpleFormat = SimpleQuantityFormat.getInstance();
        final NumberDelimiterQuantityFormat ndFormat = new NumberDelimiterQuantityFormat.Builder()
                .setUnitFormat(SimpleUnitFormat.getInstance()).setNumberFormat(realFormat)
                .setDelimiter(" ").setRadixPartsDelimiter(" ").build();
        
        // when
        String formatedOutput = ndFormat.format(lengthMixed);
        String simpleFormattedSingle = simpleFormat.format(lengthQuantity);
        String ndFormattedSingle = ndFormat.format(lengthQuantity);
        
        // then
        assertEquals("1. ft 2. in 3. P̸", formatedOutput);
        //assertEquals("29÷24 ft", simpleFormattedSingle);
        assertEquals("1.208333333333333333333333333333333 ft", simpleFormattedSingle);
        assertEquals("1.2083333333333333 ft", ndFormattedSingle);
    }
    
    @Test
    public void mixedParsing() {
        
        // given
        final NumberDelimiterQuantityFormat mixedRadixFormat = NumberDelimiterQuantityFormat.builder()
                .setUnitFormat(SimpleUnitFormat.getInstance()).setNumberFormat(NumberFormat.getInstance())
                .setDelimiter(" ").setRadixPartsDelimiter(" ").build();

        // when 
        @SuppressWarnings("rawtypes")
		MixedQuantity lengthComp = mixedRadixFormat.parseMixed("1 ft 2 in");
        Quantity<?> lengthSingle = mixedRadixFormat.parse("1 ft 2 in");
        
        // then
		@SuppressWarnings("unchecked")
		Quantity<Length> lengthQuantity = lengthComp.to(FOOT).asType(Length.class);
        NumberAssertions.assertNumberEquals(1.1666666666666667, lengthQuantity.getValue(), 1E-9);
        NumberAssertions.assertNumberEquals(1.1666666666666667, lengthSingle.getValue(), 1E-9);
    }
    
    @Test
    public void mixedParsingSimple() {
        
        // given
        final SimpleQuantityFormat simpleFormat = SimpleQuantityFormat.getInstance("n u~ ");

        // when 
        Quantity<?> lengthSingle = simpleFormat.parse("1 ft 2 in");       
        
        // then
        NumberAssertions.assertNumberEquals(1.1666666666666667, lengthSingle.getValue(), 1E-9);
    }
}
