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

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import javax.measure.Dimension;
import javax.measure.IncommensurableException;
import javax.measure.Quantity;
import javax.measure.UnconvertibleException;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.measure.quantity.Volume;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tech.units.indriya.unit.Units;

/**
 * Testing Dimension Equality.
 *
 */
class QuantityDimensionEqualityTest {
  
  private static class USCustomary {
    public static final Unit<Length> MILE = Units.METRE.multiply(1609.344).asType(Length.class);
    public static final Unit<Volume> GALLON_LIQUID = Units.LITRE.multiply(3.785411784).asType(Volume.class);
  }

  interface FuelConsumption extends Quantity<FuelConsumption> {
    static final Unit<FuelConsumption> LITRE_PER_100KM  = 
        Units.LITRE.divide(Units.METRE).divide(100_000).asType(FuelConsumption.class);
  }

  interface FuelEconomy extends Quantity<FuelEconomy> {
    static final Unit<FuelEconomy> MILES_PER_GALLON = 
        USCustomary.MILE.divide(USCustomary.GALLON_LIQUID).asType(FuelEconomy.class);
  }
  
  @Test
  @DisplayName("dimensions should be equal when same (shared) BaseUnit")
  public void dimensionsShouldBeEqual() {
    
    // multiplication of these two results in a dimensionless entity  
    Dimension consumptionDim = FuelConsumption.LITRE_PER_100KM.getDimension();
    Dimension economyDim = FuelEconomy.MILES_PER_GALLON.getDimension();
    
    assertTrue(consumptionDim.equals(economyDim.pow(-1)));
    assertTrue(economyDim.equals(consumptionDim.pow(-1)));
    
    assertTrue(economyDim.equals(consumptionDim.pow(-1)));
    assertTrue(consumptionDim.equals(economyDim.pow(-1)));
    
  }

  @Test
  @DisplayName("units should be compatible when 'same' dimensions")
  public void unitsShouldBeCompatible() 
      throws UnconvertibleException, IncommensurableException {
    
    // given: a and b, having compatible dimensions
    
    Unit<FuelConsumption> a = FuelConsumption.LITRE_PER_100KM;
    Unit<?> b = FuelEconomy.MILES_PER_GALLON.pow(-1);
    
    // when: even though a and b are different
    
    assertFalse(a.getConverterToAny(b).isIdentity());
    
    // then: a and b should be compatible
    
    assertTrue(a.isCompatible(b));
    
  }
  
}
