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
package tech.units.indriya.quantity;

import java.math.BigDecimal;
import java.util.logging.Logger;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import org.junit.jupiter.api.Test;

import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;
import tech.uom.lib.common.function.QuantityConverter;

import static org.junit.jupiter.api.Assertions.*;
import static javax.measure.MetricPrefix.*;

/**
 *
 * @author Werner Keil
 */
public class CompositeQuantityTest {
    static final Logger logger = Logger.getLogger(CompositeQuantityTest.class.getName());

    @Test
    public void testLengthSingleValueCompositeUnit() {
        CompositeQuantity<Length> mixLen = CompositeQuantity.of(Quantities.getQuantity(1, Units.METRE));
      
        assertEquals("[m]", mixLen.getUnits().toString());
        assertEquals("1 m", mixLen.toString());
        
        Quantity<Length> l2 = mixLen.to(Units.METRE);
        assertEquals(Integer.valueOf(1), l2.getValue());
    }
    
    @Test
    public void testLengths() {
        @SuppressWarnings("unchecked")
        final Quantity<Length>[] quants = new Quantity[] { Quantities.getQuantity(1, Units.METRE),  Quantities.getQuantity(70, CENTI(Units.METRE)) };
        CompositeQuantity<Length> mixLen = CompositeQuantity.of(quants);
      
        assertEquals("[m, cm]", mixLen.getUnits().toString());
        assertEquals("1 m 70 cm", mixLen.toString());
        
        Quantity<Length> l2 = mixLen.to(Units.METRE);
        assertEquals(BigDecimal.valueOf(1.7d), l2.getValue());
        Quantity<Length> l3 = mixLen.to(CENTI(Units.METRE));
        assertEquals(BigDecimal.valueOf(170d), l3.getValue());
    }

    /**
     * Inspired by Time conversion in https://reference.wolfram.com/language/ref/MixedUnit.html
     */
    @Test
    public void testTimes() {
        @SuppressWarnings("unchecked")
        final Quantity<Time>[] quants = new Quantity[] { Quantities.getQuantity(3, Units.DAY),  Quantities.getQuantity(4, Units.HOUR), 
                Quantities.getQuantity(48, Units.MINUTE)};
        final CompositeQuantity<Time> t1 = CompositeQuantity.of(quants);
       
        assertEquals("[day, h, min]", t1.getUnits().toString());
        assertEquals("3 day 4 h 48 min", t1.toString());
        assertEquals(BigDecimal.valueOf(3.2d), ((BigDecimal) t1.to(Units.DAY).getValue()).stripTrailingZeros());
        final Quantity<Time> t2 = t1.to(Units.MINUTE);

        assertEquals(new BigDecimal("4608.0000000000000000000000000000"), t2.getValue());
        final Quantity<Time> t3 = t1.to(Units.SECOND);
        assertEquals(new BigDecimal("276480.0000000000000000000000000000"), t3.getValue());
    }

    /**
     * Verifies that an mixed quantity is not equal to another quantity.
     */
    @Test
    public void mixedQuantityIsNotEqualToAnotherQuantity() {
        @SuppressWarnings("unchecked")
        final Quantity<Time>[] numList = new Quantity[] { Quantities.getQuantity(2, Units.HOUR),  Quantities.getQuantity(6, Units.MINUTE) };
        CompositeQuantity<Time> mixTime = CompositeQuantity.of(numList);
        Quantity<Time> compareTime = Quantities.getQuantity(2.5d, Units.HOUR);
        assertNotEquals(mixTime, compareTime);
        assertNotEquals(mixTime.to(Units.HOUR), compareTime);
    }

    /**
     * Verifies that an mixed quantity is not equal to another quantity that has the same numeric value.
     */
    @Test
    public void mixedQuantityIsEqualToAQuantityOfTheSameNumericValue() {
        @SuppressWarnings("unchecked")
        final Quantity<Time>[] numList = new Quantity[] { Quantities.getQuantity(2, Units.HOUR),  Quantities.getQuantity(30, Units.MINUTE) };
        CompositeQuantity<Time> mixTime = CompositeQuantity.of(numList);
        Quantity<Time> compareTime = Quantities.getQuantity(2.5d, Units.HOUR);
        assertNotEquals(mixTime, compareTime);
        assertEquals(mixTime.to(Units.HOUR), compareTime);    
     }
    
    /**
     * Verifies that an mixed quantity can be represented as QuantityConverter.
     */
    @Test
    public void mixedQuantityAsConverter() {
        @SuppressWarnings("unchecked")
        final Quantity<Time>[] numList = new Quantity[] { Quantities.getQuantity(2, Units.HOUR),  Quantities.getQuantity(30, Units.MINUTE) };
        QuantityConverter<Time> convTime = CompositeQuantity.of(numList);
        Quantity<Time> compareTime = Quantities.getQuantity(2.5d, Units.HOUR);
        assertNotEquals(convTime, compareTime);
        assertEquals(convTime.to(Units.HOUR), compareTime);    
     }
}