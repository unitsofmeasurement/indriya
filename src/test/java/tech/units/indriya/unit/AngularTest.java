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
package tech.units.indriya.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.units.indriya.NumberAssertions.assertNumberEquals;
import static tech.units.indriya.unit.Units.RADIAN;

import java.util.Locale;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Angle;
import javax.measure.quantity.SolidAngle;

import org.junit.jupiter.api.Test;

import tech.units.indriya.function.MixedRadix;
import tech.units.indriya.function.MultiplyConverter;
import tech.units.indriya.quantity.Quantities;

public class AngularTest {

    public final Unit<Angle> ANGULAR_DEGREES = 
            new TransformedUnit<>("°", RADIAN, RADIAN, MultiplyConverter.ofPiExponent(1)
                    .concatenate(MultiplyConverter.ofRational(1, 180)));
            
    public final Unit<Angle> ANGULAR_MINUTE = 
            new TransformedUnit<>("′", RADIAN, RADIAN, MultiplyConverter.ofPiExponent(1)
                    .concatenate(MultiplyConverter.ofRational(1, 180*60)));
    
    public final Unit<Angle> ANGULAR_SECOND = 
            new TransformedUnit<>("′′", RADIAN, RADIAN, MultiplyConverter.ofPiExponent(1)
                    .concatenate(MultiplyConverter.ofRational(1, 180*60*60)));

    private final MixedRadix<Angle> DMS = MixedRadix.ofPrimary(ANGULAR_DEGREES)
            .mix(ANGULAR_MINUTE)
            .mix(ANGULAR_SECOND);
    
    @Test
    void testDms() {
        Locale.setDefault(Locale.ROOT);
        Quantity<Angle> angle = Quantities.getQuantity(1, RADIAN);
        Number[] values = DMS.extractValues(angle);
        String dmsFormat = String.format("%d°%d′%.2f′′", (Object[])values);
        
        assertEquals("57°17′44.81′′", dmsFormat);
        
    }
    
    @Test
    void testCommensurableAngleAddition() {
        
        assertTrue(RADIAN.isCompatible(ANGULAR_DEGREES));
        
        Quantity<Angle> angle1 = Quantities.getQuantity(1, ANGULAR_DEGREES);
        Quantity<Angle> angle2 = Quantities.getQuantity(2, RADIAN);
        
        assertNumberEquals(2.017453292519943, angle2.add(angle1).getValue(), 1E-12); // rad
        assertNumberEquals(115.591559026164641, angle1.add(angle2).getValue(), 1E-12); // degree        
    }
    
    @Test @SuppressWarnings({ "rawtypes", "unchecked" })
    void testIncommensurableAngleAddition() {
        
        Dimension SOLID_ANGLE_DIMENSION = UnitDimension.parse('Ω');
        Unit<SolidAngle> mySTERADIAN = new BaseUnit<SolidAngle>("sr", SOLID_ANGLE_DIMENSION);
        
        // TODO proposed API extension, to allow for refinement of the Unit.isCompatible check,
        // such that only Units that have same 'Dimensions' and 'Categories' are treated compatible.
        //
        // Unit<SolidAngle> mySTERADIAN = Units.STERADIAN.category('Ω');
        
        assertFalse(RADIAN.isCompatible(mySTERADIAN));
        
        Quantity angle1 = Quantities.getQuantity(2, RADIAN);
        Quantity angle2 = Quantities.getQuantity(3, mySTERADIAN);
        
        assertThrows(Exception.class, ()->{
            angle1.add(angle2); // not allowed, should throw
        });
    }
        
}
