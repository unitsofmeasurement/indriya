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
package tech.units.indriya.quantity;

import javax.measure.Quantity;
import javax.measure.Quantity.Scale;
import javax.measure.Unit;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Volume;

import static javax.measure.MetricPrefix.KILO;
import static javax.measure.MetricPrefix.MILLI;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tech.units.indriya.format.SimpleUnitFormat;
import tech.units.indriya.function.RationalNumber;
import tech.units.indriya.unit.Units;

import static tech.units.indriya.NumberAssertions.assertNumberEquals;

/**
 *
 * @author Andi Huber
 */
class RelativeQuantitiesTest {

    private static Unit<Energy> UNIT_CAL;
    private static Unit<Volume> UNIT_MILLILITRE;
    private static Unit<Energy> UNIT_KILO_WATT_HOUR;

    @BeforeAll
    static void setUp() {
        UNIT_CAL = Units.JOULE.multiply(RationalNumber.of(4184, 1000)); // as per definition
        UNIT_MILLILITRE = MILLI(Units.LITRE);
        UNIT_KILO_WATT_HOUR = KILO(Units.WATT.multiply(Units.HOUR)).asType(Energy.class);    
        SimpleUnitFormat.getInstance().label(UNIT_CAL, "cal"); // customize formatting
    }

    //[indriya#295]
    @Test @DisplayName("Unit Conversion should carry over Scale")
    void relativeTemperatureRoundTrip() {

          Quantity<Temperature> celsiusRelative = Quantities.getQuantity(1, Units.CELSIUS, Scale.RELATIVE);
          Quantity<Temperature> kelvinRelative = celsiusRelative.to(Units.KELVIN);

          assertEquals(kelvinRelative.getScale(), Scale.RELATIVE);
          assertNumberEquals(1, kelvinRelative.getValue(), 1E-12);

          Quantity<Temperature> celsiusRelativeAfterRoundtrip = kelvinRelative.to(Units.CELSIUS);
          
          assertEquals(celsiusRelativeAfterRoundtrip.getScale(), Scale.RELATIVE);
          assertNumberEquals(1, celsiusRelativeAfterRoundtrip.getValue(), 1E-12);

    }
    
    //[indriya#294]
    @Test @DisplayName("Multiplication should carry over operand Units (abs. scale)")
    void intuitivelyKeepUnits_whenAbsolute() {
        
        // given
        Quantity<?> energyPerVolume = Quantities.getQuantity(1, UNIT_CAL.divide(UNIT_MILLILITRE));
        assertEquals("1 cal/ml", energyPerVolume.toString());
        
        // when scalar multiply, keep units
        assertEquals("2 cal/ml", energyPerVolume.multiply(2).toString());
        
        // when multiply by 'ml', keep 'cal' as unit (don't convert to system units)
        assertEquals("1 cal", energyPerVolume.multiply(Quantities.getQuantity(1, UNIT_MILLILITRE)).toString());
    }
    
    
    //[indriya#294]
    @Test @DisplayName("Multiplication should carry over operand Units if possible")
    void intuitivelyKeepUnits_whenRelative() {
        
        // given
        Quantity<Temperature> deltaT = Quantities.getQuantity(10, Units.CELSIUS, Scale.RELATIVE);
        Quantity<?> temperaturePerTime = deltaT.divide(Quantities.getQuantity(1, Units.HOUR));
        
        // when scalar multiply, keep units (don't convert to system units)
        assertEquals("20 ℃", deltaT.multiply(2).toString());
    
        // convert Celsius[℃] to Kelvin[K], but keep Hour[h] (don't convert to system unit Second[s])
        assertEquals("10 K/h", temperaturePerTime.toString());
    }
    
    
    @Test @DisplayName("Heat Requirement Calculation with relative-scoped Celsius")
    void relativeTemperatureMultiplication() {

        // given
        Quantity<?> ENERGY_NEEDED_PER_MILLILITRE_AND_KELVIN =
                Quantities.getQuantity(1, UNIT_CAL.divide(Units.KELVIN).divide(UNIT_MILLILITRE));
        Quantity<Volume> givenVolume = Quantities.getQuantity(1000, Units.LITRE);
        Quantity<Temperature> deltaT = Quantities.getQuantity(20, Units.CELSIUS, Scale.RELATIVE);

        // what's the energy requirement we need to put into given volume to heat it up a temperature amount of deltaT?
        Quantity<Energy> energyRequirementToHeatUpGivenVolume = 
                ENERGY_NEEDED_PER_MILLILITRE_AND_KELVIN
                .multiply(givenVolume) // 1000 Litre
                .multiply(deltaT) // 20ºC
                .asType(Energy.class); 

        // convert to kWh
        Quantity<Energy> kWh = energyRequirementToHeatUpGivenVolume
                .to(UNIT_KILO_WATT_HOUR);
        
        assertNumberEquals(RationalNumber.of(1046, 45), kWh.getValue(), 1E-20); // ~ 23.2444 kWh
    }


}