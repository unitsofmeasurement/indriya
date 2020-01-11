/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2020, Units of Measurement project.
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

import static org.junit.jupiter.api.Assertions.*;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Acceleration;
import javax.measure.quantity.Area;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Volume;

import org.junit.jupiter.api.Test;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.unit.ProductUnit;
import tech.units.indriya.unit.Units;

/**
 * This set of unit tests ensures that all base units have been defined and registered with the correct unit.
 */
public class UnitsDerivedProductUnitsTest {

  /**
   * Parses a symbol, asserts that it is a base unit and that it is registered for the provided quantity.
   * 
   * @param symbol
   *          The symbol to be parsed.
   * @param quantityClass
   *          The quantity class for which it is expected to be registered.
   */
  private static <Q extends Quantity<Q>> void parseSymbolAndAssertThatItIsTheProductUnitRegisteredForTheQuantity(String symbol, Class<Q> quantityClass) {
    Unit<?> parsedUnit = AbstractUnit.parse(symbol);
    assertEquals(ProductUnit.class, parsedUnit.getClass());
    assertEquals(Units.getInstance().getUnit(quantityClass), parsedUnit);
  }

  /**
   * Ensures that the symbol "m/s" can be parsed to a product unit that's registered for the speed quantity.
   */
  @Test
  public void mustParseSymbolMPerSToTheProductUnitForSpeed() {
    parseSymbolAndAssertThatItIsTheProductUnitRegisteredForTheQuantity("m/s", Speed.class);
  }

  /**
   * Ensures that the symbol "m/s^2" can be parsed to a product unit that's registered for the acceleration quantity.
   */
  @Test
  public void mustParseSymbolMPerSquareSToTheProductUnitForAccelaration() {
    parseSymbolAndAssertThatItIsTheProductUnitRegisteredForTheQuantity("m/s^2", Acceleration.class);
  }

  /**
   * Ensures that the symbol "m^2" can be parsed to a product unit that's registered for the area quantity.
   */
  @Test
  public void mustParseSymbolSquareMToTheProductUnitForArea() {
    parseSymbolAndAssertThatItIsTheProductUnitRegisteredForTheQuantity("m^2", Area.class);
  }

  /**
   * Ensures that the symbol "m^3" can be parsed to a product unit that's registered for the volume quantity.
   */
  @Test
  public void mustParseSymbolCubicMToTheProductUnitForVolume() {
    parseSymbolAndAssertThatItIsTheProductUnitRegisteredForTheQuantity("m^3", Volume.class);
  }
}
