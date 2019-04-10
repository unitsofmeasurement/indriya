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

import static javax.measure.MetricPrefix.KILO;
import static javax.measure.Quantity.Scale.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.measure.MeasurementException;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Temperature;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tech.units.indriya.function.AddConverter;
import tech.units.indriya.function.RationalConverter;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.TransformedUnit;
import tech.units.indriya.unit.Units;

/**
 * Playground inspired by
 *  <a href="https://reference.wolfram.com/language/tutorial/TemperatureUnits.html">Mathematica/Wolfram examples</a>
 *  , what would it look like in Java?
 * @author Andi Huber
 */
class AbsoluteVsRelativeTest {

  public static final Unit<Temperature> DegreesFahrenheit = 
      new TransformedUnit<>(Units.KELVIN, RationalConverter.of(5, 9).concatenate(new AddConverter(459.67)));
  
  public static final Unit<Temperature> DegreesFahrenheitDifference = 
      new TransformedUnit<>(Units.KELVIN, RationalConverter.of(5, 9));
  
  public static final Unit<Temperature> KelvinsDifference = 
      new TransformedUnit<>(Units.KELVIN, RationalConverter.of(1, 1));
  
  public static final Unit<Temperature> DegreesCelsiusDifference = 
      new TransformedUnit<>(Units.KELVIN, RationalConverter.of(1, 1));
  
  // -- (1) -- Absolute Temperature versus Temperature Difference
  
  @Test
  @DisplayName("UnitConvert[Quantity[3., 'DegreesFahrenheit'] *2, 'Kelvins'] -> 258.706 K")
  public void in1() {
    
    final Quantity<Temperature> t_f = Quantities.getQuantity(3., DegreesFahrenheit, RELATIVE);
    final Quantity<Temperature> t_k =  t_f.multiply(2.).to(Units.KELVIN);
    
    assertEquals(RELATIVE, t_f.getScale());
    assertEquals(ABSOLUTE, t_k.getScale());
    assertEquals(258.706, t_k.getValue().doubleValue(), 1E-3);
  }       
  
  @Test
  @DisplayName("UnitConvert[Quantity[3., 'DegreesFahrenheit'], 'Kelvins']*2 -> 514.078 K")
  public void in2() {
    
    final Quantity<Temperature> t_f = Quantities.getQuantity(3., DegreesFahrenheit);
    final Quantity<Temperature> t_k =  t_f.to(Units.KELVIN).multiply(2.);
    
    assertEquals(514.078, t_k.getValue().doubleValue(), 1E-3);
  }
  
  @Test
  @DisplayName("UnitConvert[Quantity[3., 'DegreesFahrenheitDifference'], 'KelvinsDifference']*2 -> 3.333333 K")
  public void in3() {
    
    final Quantity<Temperature> t_f = Quantities.getQuantity(3., DegreesFahrenheitDifference);
    final Quantity<Temperature> t_k =  t_f.to(KelvinsDifference).multiply(2.);
    
    assertEquals(3.333333, t_k.getValue().doubleValue(), 1E-6);
  }
  
  @Test
  @DisplayName("UnitConvert[Quantity[3., 'DegreesFahrenheitDifference']*2, 'KelvinsDifference'] -> 3.333333 K")
  public void in4() {
    
    final Quantity<Temperature> t_f = Quantities.getQuantity(3., DegreesFahrenheitDifference);
    final Quantity<Temperature> t_k =  t_f.to(KelvinsDifference).multiply(2.);
    
    assertEquals(3.333333, t_k.getValue().doubleValue(), 1E-6);
  }
  
  // -- (2) -- Adding Temperatures
  
  @Test
  @DisplayName("Quantity[3, 'DegreesFahrenheit'] + Quantity[2, 'DegreesFahrenheitDifference'] -> 5 °F")
  public void in5() {
    
    final Quantity<Temperature> t_f1 = Quantities.getQuantity(3., DegreesFahrenheit);
    final Quantity<Temperature> t_f2 = Quantities.getQuantity(2., DegreesFahrenheitDifference);
    final Quantity<Temperature> t_f =  t_f1.add(t_f2);
    
    assertEquals(5., t_f.getValue().doubleValue(), 1E-3);
  }
  
  @Test
  @DisplayName("Quantity[3, 'DegreesFahrenheitDifference'] + Quantity[2, 'DegreesCelsiusDifference'] -> 33/5 °F")
  public void in6() {
    
    final Quantity<Temperature> t_f1 = Quantities.getQuantity(3., DegreesFahrenheitDifference);
    final Quantity<Temperature> t_c = Quantities.getQuantity(2., DegreesCelsiusDifference);
    final Quantity<Temperature> t_f =  t_f1.add(t_c);
    
    assertEquals(33./5., t_f.getValue().doubleValue(), 1E-3);
  }
  
  @Test @Disabled("Indriya actually calculates this!")
  @DisplayName("Quantity[3, 'DegreesFahrenheit'] + Quantity[2, 'DegreesCelsius'] -> ???")
  public void in7() {
    
    final Quantity<Temperature> t_f1 = Quantities.getQuantity(3., DegreesFahrenheit, RELATIVE);
    final Quantity<Temperature> t_c = Quantities.getQuantity(2., Units.CELSIUS, RELATIVE);
    
    //TODO should throw, or return something like a NonEvaluatedQuantity 
    assertThrows(MeasurementException.class, ()->t_f1.add(t_c));
  }
  
  @Test @Disabled("Indriya does implicit conversion to Kelvin before addition") //TODO this could be done e.g. based on LevelOfMeasurement
  @DisplayName("Quantity[3, 'DegreesFahrenheit'] + Quantity[2, 'DegreesFahrenheit'] -> 5 °F")
  public void in8() {
    
    final Quantity<Temperature> t_f1 = Quantities.getQuantity(3., DegreesFahrenheit, RELATIVE);
    final Quantity<Temperature> t_f2 = Quantities.getQuantity(2., DegreesFahrenheit, RELATIVE);
    final Quantity<Temperature> t_f =  t_f1.add(t_f2);
    
    assertEquals(5., t_f.getValue().doubleValue(), 1E-3);
  }
  
  @Test
  @DisplayName("Quantity[3, 'Kilokelvins'] + Quantity[4, 'Kelvins'] -> 751/250 K")
  public void in9() {
    
    final Quantity<Temperature> t_k1 = Quantities.getQuantity(3., KILO(Units.KELVIN));
    final Quantity<Temperature> t_k2 = Quantities.getQuantity(4., Units.KELVIN);
    final Quantity<Temperature> t_k =  t_k1.add(t_k2);
    
    assertEquals(751./250., t_k.getValue().doubleValue(), 1E-3);
  }
  
  // -- 3 -- Multiplying Temperatures
  
  @Test
  @DisplayName("UnitConvert[Quantity[3., 'DegreesCelsius'] *18.2, 'Kelvins'] -> 327.75 K")
  public void in10() {
    
    final Quantity<Temperature> t_c = Quantities.getQuantity(3d, Units.CELSIUS);
    final Quantity<Temperature> t_k =  t_c.multiply(18.2).to(Units.KELVIN);
    
    assertEquals(327.75, t_k.getValue().doubleValue(), 1E-3);
  }
  
  @Test
  @DisplayName("UnitConvert[Quantity[3., 'DegreesCelsius'], 'Kelvins']*18.2 -> 5025.93 K")
  public void in11() {
    
    final Quantity<Temperature> t_c = Quantities.getQuantity(3., Units.CELSIUS);
    final Quantity<Temperature> t_k =  t_c.to(Units.KELVIN).multiply(18.2);
    
    assertEquals(5025.93, t_k.getValue().doubleValue(), 1E-3);
  }
  
  @Test @Disabled("Indriya actually calculates this!")
  @DisplayName("Quantity[1.4, 'DegreesCelsius']/Quantity[8, 'DegreesFahrenheit'] -> ???")
  public void in12() {
    
    final Quantity<Temperature> t_c = Quantities.getQuantity(1.4, Units.CELSIUS, RELATIVE);
    final Quantity<Temperature> t_f = Quantities.getQuantity(8., DegreesFahrenheit, RELATIVE);
    
    //TODO should throw, or return something like a NonEvaluatedQuantity 
    //This could be done e.g. based on LevelOfMeasurement
    assertThrows(MeasurementException.class, ()->t_c.divide(t_f));
  }
  
  @Test
  @DisplayName("UnitConvert[Quantity[1.4, 'DegreesCelsius'], 'Kelvins'] /" + 
      " UnitConvert[Quantity[8, 'DegreesFahrenheit'], 'Kelvins'] -> 1.05671")
  public void in13() {
    
    final Quantity<Temperature> t_c = Quantities.getQuantity(1.4, Units.CELSIUS);
    final Quantity<Temperature> t_f = Quantities.getQuantity(8., DegreesFahrenheit);
    
    final Quantity<Temperature> t_k1 = t_c.to(Units.KELVIN);
    final Quantity<Temperature> t_k2 = t_f.to(Units.KELVIN);
    
    final Quantity<?> dimensionless = t_k1.divide(t_k2);
    
    assertEquals(1.05671, dimensionless.getValue().doubleValue(), 1E-3);
  }

}
