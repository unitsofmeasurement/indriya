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
import java.text.DecimalFormat;
import java.util.Locale;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import tech.units.indriya.NumberAssertions;
import tech.units.indriya.format.MixedQuantityFormat.MixedRadixFormatOptions;
import tech.units.indriya.function.MixedRadix;
import tech.units.indriya.function.MixedRadixTest.USCustomary;

/**
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 *
 */
public class MixedQuantityFormatTest {
    
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
                .setRealFormat(realFormat)
                .setUnitFormat(SimpleUnitFormat.getInstance())
                .setNumberToUnitDelimiter(" ")
                .setRadixPartsDelimiter(" ");
        
        MixedQuantityFormat<Length> mixedRadixFormat = MixedQuantityFormat.of(mixedRadix, mixedRadixFormatOptions);
        SimpleQuantityFormat simpleFormat = SimpleQuantityFormat.getInstance();
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
        MixedQuantityFormat<Length> mixedRadixFormat = MixedQuantityFormat.of(mixedRadix, mixedRadixFormatOptions);

        // when 
        Quantity<Length> lengthQuantity = mixedRadixFormat.parse("1 ft 2 in");
        
        // then
        NumberAssertions.assertNumberEquals(1.1666666666666667, lengthQuantity.getValue(), 1E-9);
        
        
        fail("disabled"); // to satisfy code quality check?
    }

}
