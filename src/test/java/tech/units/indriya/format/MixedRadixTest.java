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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static tech.units.indriya.unit.Units.HOUR;
import static tech.units.indriya.unit.Units.MINUTE;
import static tech.units.indriya.unit.Units.SECOND;

import java.text.DecimalFormat;
import java.util.Locale;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import tech.units.indriya.NumberAssertions;
import tech.units.indriya.format.MixedQuantityFormat.MixedRadixFormatOptions;
import tech.units.indriya.function.MixedRadix;
import tech.units.indriya.function.MixedRadix.PrimaryUnitPick;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

/**
 *
 * @author Andi Huber
 */
public class MixedRadixTest {

    private static class USCustomary {

        public static final Unit<Length> FOOT = Units.METRE.multiply(0.3048).asType(Length.class);
        public static final Unit<Length> INCH = Units.METRE.multiply(0.0254).asType(Length.class);
        public static final Unit<Length> PICA = Units.METRE.multiply(0.0042).asType(Length.class);

        static {
            SimpleUnitFormat.getInstance().label(FOOT, "ft");
            SimpleUnitFormat.getInstance().label(INCH, "in");
            SimpleUnitFormat.getInstance().label(PICA, "P̸");
        }
        
    }

    @Test
    public void cannotRassignPrimaryUnit() {
        assertThrows(IllegalStateException.class, ()->{
            MixedRadix.ofPrimary(HOUR).mixPrimary(MINUTE);
        });
    }
    
    @Test
    public void assignPrimaryUnitByLeadingConvention() {

        // given
        MixedRadix.PRIMARY_UNIT_PICK = PrimaryUnitPick.LEADING_UNIT;
        
        // when
        MixedRadix<Time> mixedRadix = MixedRadix.of(HOUR).mix(MINUTE).mix(SECOND);
        
        // then
        assertEquals(HOUR, mixedRadix.getPrimaryUnit());
        
    }
    
    @Test
    public void assignPrimaryUnitByTrailingConvention() {

        // given
        MixedRadix.PRIMARY_UNIT_PICK = PrimaryUnitPick.TRAILING_UNIT;
        
        // when
        MixedRadix<Time> mixedRadix = MixedRadix.of(HOUR).mix(MINUTE).mix(SECOND);
        
        // then
        assertEquals(SECOND, mixedRadix.getPrimaryUnit());
        
    }
    
    @Test
    public void assignPrimaryUnitExplicitlyLeading() {

        // given
        MixedRadix<Time> mixedRadix = MixedRadix.ofPrimary(HOUR).mix(MINUTE).mix(SECOND);
        
        // then
        assertEquals(HOUR, mixedRadix.getPrimaryUnit());
        
    }
    
    @Test
    public void assignPrimaryUnitExplicitlyOnMix() {

        // given
        MixedRadix<Time> mixedRadix1 = MixedRadix.of(HOUR).mixPrimary(MINUTE).mix(SECOND);
        MixedRadix<Time> mixedRadix2 = MixedRadix.of(HOUR).mix(MINUTE).mixPrimary(SECOND);
        
        // then
        assertEquals(MINUTE, mixedRadix1.getPrimaryUnit());
        assertEquals(SECOND, mixedRadix2.getPrimaryUnit());
        
    }
    

    @Test @Disabled("check is not yet implemented") //TODO[211] enable once implemented
    public void wrongOrderOfSignificance() {
        assertThrows(IllegalArgumentException.class, ()->{
            MixedRadix.ofPrimary(USCustomary.INCH).mix(USCustomary.FOOT);
        });
        
        fail("disabled"); // to satisfy code quality check?
    }
    
    @Test
    public void quantityConstruction() {
        
        // given
        
        MixedRadix<Length> mixedRadix = MixedRadix.ofPrimary(USCustomary.FOOT).mix(USCustomary.INCH);
        
        // when 
        
        Quantity<Length> lengthQuantity = mixedRadix.createQuantity(1, 2);
        
        // then
        
        assertEquals(USCustomary.FOOT, mixedRadix.getPrimaryUnit());
        NumberAssertions.assertNumberEquals(1.1666666666666667, lengthQuantity.getValue(), 1E-9);
    }
    
    @Test
    public void createQuantityTooManyArguments() {

        // given
        
        MixedRadix<Length> mixedRadix = MixedRadix.ofPrimary(USCustomary.FOOT);

        // then
        
        assertThrows(IllegalArgumentException.class, ()->{
            mixedRadix.createQuantity(1, 2);
        });
    }
    
    @Test
    public void createQuantityLessThanAllowedArguments() {
        
        // given
        
        MixedRadix<Length> mixedRadix = MixedRadix.ofPrimary(USCustomary.FOOT).mix(USCustomary.INCH);
        
        // when
        
        Quantity<Length> lengthQuantity = mixedRadix.createQuantity(1);
        
        // then
        
        assertEquals("1 ft", lengthQuantity.toString());
    }
    
    
    @Test
    public void radixExtraction() {
        
        // given
        
        MixedRadix<Length> mixedRadix = MixedRadix
                .ofPrimary(USCustomary.FOOT)
                .mix(USCustomary.INCH)
                .mix(USCustomary.PICA);
        
        Quantity<Length> lengthQuantity = mixedRadix.createQuantity(1, 2, 3);
        
        // when 
        
        Number[] valueParts = mixedRadix.extractValues(lengthQuantity);
        
        // then
        
        NumberAssertions.assertNumberArrayEquals(new Number[] {1, 2, 3}, valueParts, 1E-9);
    }
    
    @Test @Disabled("not well defined yet, how to handle negative numbers") //TODO[211] enable once clarified
    public void radixExtractionWhenNegative() {
        
        // given
        
        MixedRadix<Length> mixedRadix = MixedRadix
                .ofPrimary(USCustomary.FOOT)
                .mix(USCustomary.INCH)
                .mix(USCustomary.PICA);
        
        Quantity<Length> lengthQuantity = mixedRadix.createQuantity(-1, 2, 3);
        
        // when 
        
        Number[] valueParts = mixedRadix.extractValues(lengthQuantity);
        
        System.out.println(lengthQuantity);
        
        
        // then
        
        NumberAssertions.assertNumberArrayEquals(new Number[] {-1, 2, 3}, valueParts, 1E-9);
        
        fail("disabled"); // to satisfy code quality check?
    }
    
    
    @Test
    public void formatting() {
        
        // given
        
        MixedRadix<Length> mixedRadix = MixedRadix
                .ofPrimary(USCustomary.FOOT)
                .mix(USCustomary.INCH)
                .mix(USCustomary.PICA);
        
        Quantity<Length> lengthQuantity = mixedRadix.createQuantity(1, 2, 3);
        
        DecimalFormat realFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.ENGLISH);
        realFormat.setDecimalSeparatorAlwaysShown(true);
        realFormat.setMaximumFractionDigits(3);
        
        MixedRadixFormatOptions mixedRadixFormatOptions = new MixedQuantityFormat.MixedRadixFormatOptions()
                .realFormat(realFormat)
                .unitFormat(SimpleUnitFormat.getInstance())
                .numberToUnitDelimiter(" ")
                .radixPartsDelimiter(" ");
        
        MixedQuantityFormat<Length> mixedRadixFormat = mixedRadix.createFormat(mixedRadixFormatOptions);
        
        // when
        String formatedOutput = mixedRadixFormat.format(lengthQuantity);
        
        // then
        assertEquals("1 ft 2 in 3. P̸", formatedOutput);
        
    }
    
    @Test @Disabled("parsing not yet implemented") //TODO[211] enable once implemented
    public void parsing() {
        
        // given
        
        MixedRadix<Length> mixedRadix = MixedRadix.ofPrimary(USCustomary.FOOT).mix(USCustomary.INCH);
        MixedQuantityFormat.MixedRadixFormatOptions mixedRadixFormatOptions = new MixedQuantityFormat.MixedRadixFormatOptions();
        MixedQuantityFormat<Length> mixedRadixFormat = mixedRadix.createFormat(mixedRadixFormatOptions);

        // when 
        Quantity<Length> lengthQuantity = mixedRadixFormat.parse("1 ft 2 in");
        
        // then
        NumberAssertions.assertNumberEquals(1.1666666666666667, lengthQuantity.getValue(), 1E-9);
        
        
        fail("disabled"); // to satisfy code quality check?
    }
    
    @Test @Disabled("not yet optimized to do this") //TODO[211] enable once implemented
    public void leastSignificantShouldDriveArithmetic() {
        
        // given
    
        MixedRadix<Time> mixedRadix = MixedRadix.ofPrimary(HOUR).mix(MINUTE).mixPrimary(SECOND);
        
        Quantity<Time> startTime = mixedRadix.createQuantity(9, 20, 0);
        Quantity<Time> duration = Quantities.getQuantity(30, SECOND);

        // when
        
        Quantity<Time> endTime = startTime.add(duration);
        Number[] timeParts = mixedRadix.extractValues(endTime);
        
        // then
        
        assertEquals(9, timeParts[0]); // should be actually an int
        assertEquals(20, timeParts[1]); // should be actually an int
        assertEquals(30, timeParts[2]); // should be actually an int
        
    }



}
