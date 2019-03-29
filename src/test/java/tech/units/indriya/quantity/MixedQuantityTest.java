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

import javax.measure.MeasurementException;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import org.junit.jupiter.api.Test;

import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

import static org.junit.jupiter.api.Assertions.*;
import static javax.measure.MetricPrefix.*;

/**
 *
 * @author Werner Keil
 */
public class MixedQuantityTest {
    static final Logger logger = Logger.getLogger(MixedQuantityTest.class.getName());

    @Test
    public void testLengthSingleValueMixedUnit() {
        final Unit<Length> mixUnit = Units.METRE.mix(CENTI(Units.METRE));
        assertThrows(MeasurementException.class, () -> {
            @SuppressWarnings("unused")
            Quantity<Length> l1 = Quantities.getQuantity(1.70, mixUnit);
        });
    }

    @Test
    public void testLengths() {
        final Unit<Length> mixUnit = Units.METRE.mix(CENTI(Units.METRE));
        final Number[] numList = { 1, 70 };
        Quantity<Length> l1 = Quantities.getMixedQuantity(numList, mixUnit);
        assertEquals(BigDecimal.valueOf(1.7d), l1.getValue());
        assertEquals("m;cm", l1.getUnit().toString());
        assertEquals("1 m 70 cm", l1.toString());
        Quantity<Length> l2 = l1.to(Units.METRE);
        assertEquals(BigDecimal.valueOf(1.7d), l2.getValue());
        Quantity<Length> l3 = l1.to(CENTI(Units.METRE));
        assertEquals(BigDecimal.valueOf(170d), l3.getValue());
    }

    /**
     * Inspired by Time conversion in https://reference.wolfram.com/language/ref/MixedUnit.html
     */
    @Test
    public void testTimes() {
        final Unit<Time> mixUnit = Units.DAY.mix(Units.HOUR).mix(Units.MINUTE);
        final Number[] numList = { 3, 4, 48 };
        Quantity<Time> t1 = Quantities.getMixedQuantity(numList, mixUnit);
        assertEquals(BigDecimal.valueOf(3.2d), ((BigDecimal) t1.getValue()).stripTrailingZeros());
        assertEquals("day;h;min", t1.getUnit().toString());
        assertEquals("3 day 4 h 48 min", t1.toString());
        final Quantity<Time> t2 = t1.to(Units.MINUTE);

        assertEquals(new BigDecimal("4608.0000000000000000000000000000"), t2.getValue());
        final Quantity<Time> t3 = t1.to(Units.SECOND);
        assertEquals(new BigDecimal("276480.0000000000000000000000000000"), t3.getValue());
    }

    @Test
    public void testArrayNoMixedUnit() {
        Number[] numList = { 1, 70 };
        assertThrows(MeasurementException.class, () -> {
            @SuppressWarnings("unused")
            Quantity<Time> t1 = Quantities.getMixedQuantity(numList, Units.DAY);
        });
    }

    @Test
    public void testSizeMismatch() {
        Unit<Time> compTime = Units.HOUR.mix(Units.MINUTE).mix(Units.SECOND);
        Number[] numList = { 1, 70 };
        assertThrows(IllegalArgumentException.class, () -> {
            @SuppressWarnings("unused")
            Quantity<Time> t1 = Quantities.getMixedQuantity(numList, compTime);
        });
    }

    @Test
    public void testConvertToMixed() {
        Unit<Length> mixUnit = Units.METRE.mix(CENTI(Units.METRE));
        Quantity<Length> l1 = Quantities.getQuantity(170, CENTI(Units.METRE));
        assertEquals(170, l1.getValue());
        assertEquals("cm", l1.getUnit().toString());
        assertThrows(MeasurementException.class, () -> {
            Quantity<Length> l2 = l1.to(mixUnit);
            // TODO UnitConverter implementations should also decompose a quantity into a MixedQuantity, so this no longer throws an exception

            logger.warning(String.valueOf(l2));
        });
    }

    /**
     * Verifies that an mixed quantity is not equal to another quantity.
     */
    @Test
    public void mixedQuantityIsNotEqualToAnotherQuantity() {
        final Unit<Time> mixUnit = (Units.HOUR).mix(Units.MINUTE);
        final Number[] numList = { 2, 6 };
        Quantity<Time> mixTime = Quantities.getMixedQuantity(numList, mixUnit);
        Quantity<Time> compareTime = Quantities.getQuantity(2.5d, Units.HOUR);
        assertNotEquals(mixTime, compareTime);
        assertNotEquals(mixTime.getValue().doubleValue(), compareTime.getValue().doubleValue());

    }

    /**
     * Verifies that an mixed quantity is not equal to another quantity that has the same numeric value.
     */
    @Test
    public void mixedQuantityIsNotEqualToAQuantityOfTheSameNumericValue() {
        final Unit<Time> mixUnit = (Units.HOUR).mix(Units.MINUTE);
        final Number[] numList = { 2, 30 };
        Quantity<Time> mixTime = Quantities.getMixedQuantity(numList, mixUnit);
        Quantity<Time> compareTime = Quantities.getQuantity(2.5d, Units.HOUR);
        assertNotEquals(mixTime, compareTime);
        assertEquals(mixTime.getValue().doubleValue(), compareTime.getValue().doubleValue());
    }
}