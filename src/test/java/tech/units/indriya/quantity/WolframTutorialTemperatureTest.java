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

import static javax.measure.MetricPrefix.KILO;
import static javax.measure.Quantity.Scale.ABSOLUTE;
import static javax.measure.Quantity.Scale.RELATIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.units.indriya.NumberAssertions.assertNumberEquals;

import javax.measure.Quantity;
import javax.measure.Quantity.Scale;
import javax.measure.Unit;
import javax.measure.quantity.Temperature;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.function.AbstractConverter;
import tech.units.indriya.function.AddConverter;
import tech.units.indriya.function.MultiplyConverter;
import tech.units.indriya.unit.TransformedUnit;
import tech.units.indriya.unit.Units;

/**
 * Playground inspired by <a href=
 * "https://reference.wolfram.com/language/tutorial/TemperatureUnits.html">Mathematica/Wolfram
 * examples</a> , what would it look like in Java?
 * 
 * @author Andi Huber
 */
class WolframTutorialTemperatureTest {

    // K = 5/9 * (F - 32) + 273.15
    private static final AbstractConverter fahrenheitToKelvin = (AbstractConverter) 
            new AddConverter(273.15)
            .concatenate(MultiplyConverter.ofRational(5, 9))
            .concatenate(new AddConverter(-32));

    private static final Unit<Temperature> DegreesFahrenheit = 
            new TransformedUnit<>("°F", "DegreesFahrenheit", Units.KELVIN, fahrenheitToKelvin);



    @Test
    public void fahrenheit() {
        assertNumberEquals(273.15, fahrenheitToKelvin.convert(32), 1E-9);
        assertNumberEquals(283.15, fahrenheitToKelvin.convert(50), 1E-9);
        assertNumberEquals(5./9., fahrenheitToKelvin.linearFactor().get(), 1E-9);
        assertFalse(fahrenheitToKelvin.isLinear());
    }

    // -- (1) -- Absolute Temperature versus Temperature Difference

    @Test
    @DisplayName("UnitConvert[Quantity[3., 'DegreesFahrenheit'] *2, 'Kelvins'] -> 258.706 K") // unexpected result
    public void in1() {
        final Quantity<Temperature> t_f = Quantities.getQuantity(3., DegreesFahrenheit, ABSOLUTE);
        assertEquals(ABSOLUTE, t_f.getScale());
        final Quantity<Temperature> t_2f = t_f.multiply(2.);
        assertFahrenheit(465.67, t_2f, ABSOLUTE);

        final Quantity<Temperature> t_k = t_2f.to(Units.KELVIN);
        assertKelvin(514.078, t_k);
    }

    @Test
    @DisplayName("UnitConvert[Quantity[3., 'DegreesFahrenheit'], 'Kelvins']*2 -> 514.078 K")
    public void in2() {
        final Quantity<Temperature> t_f = Quantities.getQuantity(3., DegreesFahrenheit, ABSOLUTE);
        final Quantity<Temperature> t_k = t_f.to(Units.KELVIN).multiply(2.);
        assertKelvin(514.078, t_k);
    }

    @Test
    @DisplayName("UnitConvert[Quantity[3., 'DegreesFahrenheitDifference'], 'KelvinsDifference']*2 -> 3.333333 K")
    public void in3() {
        final Quantity<Temperature> t_f = Quantities.getQuantity(3., DegreesFahrenheit, RELATIVE);
        assertEquals(RELATIVE, t_f.getScale());
        final Quantity<Temperature> t_k = t_f.to(Units.KELVIN).multiply(2.);
        assertKelvin(3.333333, t_k);
    }

    @Test
    @DisplayName("UnitConvert[Quantity[3., 'DegreesFahrenheitDifference']*2, 'KelvinsDifference'] -> 3.333333 K")
    public void in4() {
        final Quantity<Temperature> t_f = Quantities.getQuantity(3., DegreesFahrenheit, RELATIVE);
        final Quantity<Temperature> t_k = t_f.multiply(2.).to(Units.KELVIN);
        assertKelvin(3.333333, t_k);
    }

    // -- (2) -- Adding Temperatures

    @Test
    @DisplayName("Quantity[3, 'DegreesFahrenheit'] + Quantity[2, 'DegreesFahrenheitDifference'] -> 5 °F")
    public void in5() {
        final Quantity<Temperature> t_f1 = Quantities.getQuantity(3., DegreesFahrenheit, ABSOLUTE);
        final Quantity<Temperature> t_f2 = Quantities.getQuantity(2., DegreesFahrenheit, RELATIVE);
        final Quantity<Temperature> t_f = t_f1.add(t_f2);
        assertFahrenheit(5., t_f, ABSOLUTE);
    }

    @Test
    @DisplayName("Quantity[3, 'DegreesFahrenheitDifference'] + Quantity[2, 'DegreesCelsiusDifference'] -> 33/5 °F")
    public void in6() {

        final Quantity<Temperature> t_f1 = Quantities.getQuantity(3., DegreesFahrenheit, RELATIVE);
        final Quantity<Temperature> t_c = Quantities.getQuantity(2., Units.CELSIUS, RELATIVE);
        final Quantity<Temperature> t_f = t_f1.add(t_c);

        assertFahrenheit(33. / 5., t_f, RELATIVE);
    }

    @Test
    @DisplayName("Quantity[3, 'DegreesFahrenheit'] + Quantity[2, 'DegreesCelsius'] -> 498.27 °F")
    public void in7() {

        final Quantity<Temperature> t_f1 = Quantities.getQuantity(3., DegreesFahrenheit, ABSOLUTE);
        final Quantity<Temperature> t_c = Quantities.getQuantity(2., Units.CELSIUS, ABSOLUTE);

        assertFahrenheit(498.27, t_f1.add(t_c), ABSOLUTE);
    }

    @Test
    @DisplayName("Quantity[3, 'DegreesFahrenheit'] + Quantity[2, 'DegreesFahrenheit'] -> 5 °F")
    public void in8() {

        final Quantity<Temperature> t_f1 = Quantities.getQuantity(3., DegreesFahrenheit, RELATIVE);
        final Quantity<Temperature> t_f2 = Quantities.getQuantity(2., DegreesFahrenheit, RELATIVE);
        final Quantity<Temperature> t_f = t_f1.add(t_f2);

        assertFahrenheit(5., t_f, RELATIVE);
    }

    @Test
    @DisplayName("Quantity[3, 'Kilokelvins'] + Quantity[4, 'Kelvins'] -> 751/250 K")
    public void in9() {

        final Quantity<Temperature> t_k1 = Quantities.getQuantity(3., KILO(Units.KELVIN));
        final Quantity<Temperature> t_k2 = Quantities.getQuantity(4., Units.KELVIN);
        final Quantity<Temperature> t_k = t_k1.add(t_k2);

        assertKiloKelvin(751. / 250., t_k);
    }

    // -- 3 -- Multiplying Temperatures

    @Test
    @DisplayName("UnitConvert[Quantity[3., 'DegreesCelsius'] *18.2, 'Kelvins'] -> 327.75 K") // unexpected result
    public void in10() {

        final Quantity<Temperature> t_c = Quantities.getQuantity(3d, Units.CELSIUS, ABSOLUTE);
        final Quantity<Temperature> t_c2 = t_c.multiply(18.2);
        assertCelsius((18.2 * (3. + 273.15)) - 273.15, t_c2, ABSOLUTE);

        final Quantity<Temperature> t_k = t_c2.to(Units.KELVIN);
        assertKelvin((18.2 * (3. + 273.15)), t_k);
    }

    @Test
    @DisplayName("UnitConvert[Quantity[3., 'DegreesCelsius'], 'Kelvins']*18.2 -> 5025.93 K")
    public void in11() {

        final Quantity<Temperature> t_c = Quantities.getQuantity(3., Units.CELSIUS);
        final Quantity<Temperature> t_k = t_c.to(Units.KELVIN).multiply(18.2);

        assertKelvin(5025.93, t_k);
    }

    @Test
    @DisplayName("Quantity[1.4, 'DegreesCelsius']/Quantity[8, 'DegreesFahrenheit'] -> ???")
    public void in12() {

        final Quantity<Temperature> t_c = Quantities.getQuantity(1.4, Units.CELSIUS, RELATIVE);
        final Quantity<Temperature> t_f = Quantities.getQuantity(8., DegreesFahrenheit, RELATIVE);

        assertDimensionLess(1.4/(8.*5./9.), t_c.divide(t_f));
    }

    @Test
    @DisplayName("UnitConvert[Quantity[1.4, 'DegreesCelsius'], 'Kelvins'] /"
            + " UnitConvert[Quantity[8, 'DegreesFahrenheit'], 'Kelvins'] -> 1.05671")
    public void in13() {

        final Quantity<Temperature> t_c = Quantities.getQuantity(1.4, Units.CELSIUS, ABSOLUTE);
        final Quantity<Temperature> t_f = Quantities.getQuantity(8., DegreesFahrenheit, ABSOLUTE);

        final Quantity<Temperature> t_k1 = t_c.to(Units.KELVIN);
        final Quantity<Temperature> t_k2 = t_f.to(Units.KELVIN);

        assertDimensionLess(1.05671, t_k1.divide(t_k2));
    }

    // -- HELPER

    private static void assertKelvin(Number expected, Quantity<?> x) {
        assertEquals("K", x.getUnit().toString());
        assertEquals(ABSOLUTE, x.getScale()); // should be ABSOLUTE by convention
        assertNumberEquals(expected, x.getValue(), 1E-3);
    }

    private static void assertKiloKelvin(Number expected, Quantity<?> x) {
        assertEquals("kK", x.getUnit().toString());
        assertEquals(ABSOLUTE, x.getScale()); // should be ABSOLUTE by convention
        assertNumberEquals(expected, x.getValue(), 1E-3);
    }

    private static void assertFahrenheit(Number expected, Quantity<?> x, Scale scale) {
        assertEquals("°F", x.getUnit().toString());
        assertEquals(scale, x.getScale());
        assertNumberEquals(expected, x.getValue(), 1E-3);
    }

    private static void assertCelsius(Number expected, Quantity<?> x, Scale scale) {
        assertEquals("℃", x.getUnit().toString());
        assertEquals(scale, x.getScale());
        assertNumberEquals(expected, x.getValue(), 1E-3);
    }

    private static void assertDimensionLess(Number expected, Quantity<?> x) {
        assertTrue(x.getUnit().isCompatible(AbstractUnit.ONE)); // scalar
        assertEquals(ABSOLUTE, x.getScale()); // should be ABSOLUTE by convention
        assertNumberEquals(expected, x.getValue(), 1E-3);
    }


}
