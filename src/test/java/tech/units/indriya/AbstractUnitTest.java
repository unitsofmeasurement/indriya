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
package tech.units.indriya;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tech.units.indriya.format.SimpleUnitFormat;

import static javax.measure.MetricPrefix.KILO;

import static tech.units.indriya.unit.Units.WATT;

import java.util.Map;

import javax.measure.Dimension;
import javax.measure.Unit;
import javax.measure.UnitConverter;

/**
 * Unit tests on the AbstractUnit class.
 *
 */
public class AbstractUnitTest {

  private static final String SYMBOL = "symbol";
  private static final String NAME = "name";
  private static final String LABEL = "label";

  /**
   * Trivial implementation of the AbstractUnit class, in order to test the functionality provided in the abstract class.
   *
   */
  class TestUnit extends AbstractUnit {

    private TestUnit(String symbol) {
      super(symbol);
    }

    public int compareTo(Object arg0) {
      return 0;
    }

    @Override
    protected Unit toSystemUnit() {
      return null;
    }

    @Override
    public UnitConverter getSystemConverter() {
      return null;
    }

    @Override
    public Map getBaseUnits() {
      return null;
    }

    @Override
    public Dimension getDimension() {
      return null;
    }

    @Override
    public boolean equals(Object obj) {
      return false;
    }

    @Override
    public int hashCode() {
      return 0;
    }

  }

  /**
   * Verifies that the constructor has the symbol parameter wired correctly.
   */
  @Test
  public void constructorWithSymbolHasTheSymbolWiredCorrectly() {
    TestUnit unit = new TestUnit(SYMBOL);
    assertEquals(SYMBOL, unit.getSymbol());
  }

  /**
   * Verifies that the setter and getter for name are wired correctly.
   */
  @Test
  public void setterAndGetterForNameAreWiredCorrectly() {
    TestUnit unit = new TestUnit(SYMBOL);
    unit.setName(NAME);
    assertEquals(NAME, unit.getName());
  }

  /**
   * Verifies that the parse method is wired to a parser.
   */
  @Test
  public void parseMethodIsWiredToAParser() {
    assertEquals(KILO(WATT), AbstractUnit.parse("kW"));
  }

  /**
   * Verifies that the toString method is wired to the SimpleUnitFormat formatter.
   */
  @Test
  public void toStrindMethodIsWiredToAFormatter() {
    TestUnit unit = new TestUnit(SYMBOL);
    SimpleUnitFormat.getInstance().label(unit, LABEL);
    assertEquals(LABEL, unit.toString());
  }

  /**
   * Verifies that a unit is equal to itself according to the compareTo method.
   */
  @Test
  public void compareToReturnsZeroWhenObjectIsProvidedAsArgument() {
    TestUnit unit = new TestUnit(SYMBOL);
    assertEquals(0, unit.compareTo(unit));
  }
  
  /**
   * Verifies that a unit is equal to another unit with the same symbol according to the compareTo method.
   */
  @Test
  public void compareToReturnsZeroWhenUnitWithSameSymbolIsProvided() {
    TestUnit unit = new TestUnit(SYMBOL);
    TestUnit otherUnit = new TestUnit(SYMBOL);
    assertEquals(0, unit.compareTo(otherUnit));
  }
  
  
  /**
   * Verifies that a unit is equal to another unit with the same symbol and name according to the compareTo method.
   */
  @Test
  public void compareToReturnsZeroWhenUnitWithSameSymbolAndNameIsProvided() {
    TestUnit unit = new TestUnit(SYMBOL);
    unit.setName(NAME);
    TestUnit otherUnit = new TestUnit(SYMBOL);
    otherUnit.setName(NAME);
    assertEquals(0, unit.compareTo(otherUnit));
  }
  
  /**
   * Verifies that when compared to a unit with a smaller unit, the compareTo method returns a positive number.
   */
  @Test
  public void compareToReturnsAPositiveIntegerWhenUnitWithSmallerSymbolIsProvided() {
    TestUnit unit = new TestUnit(SYMBOL);
    TestUnit otherUnit = new TestUnit("a");
    assertTrue(unit.compareTo(otherUnit) > 0);
  }

  /**
   * Verifies that when compared to a unit with the same symbol but a smaller name, the compareToMethod returns a positive number.
   */
  @Test
  public void compareToReturnsAPositiveNumberWhenUnitWithSameSymbolButSmallerNameIsProvided() {
    TestUnit unit = new TestUnit(SYMBOL);
    unit.setName(NAME);
    TestUnit otherUnit = new TestUnit(SYMBOL);
    otherUnit.setName("a");
    assertTrue(unit.compareTo(otherUnit) > 0);
  }
  
  /**
   * Verifies that a unit is equivalent to itself.
   */
  @Test
  public void unitIsEquivalentToItself() {
    TestUnit unit = new TestUnit(SYMBOL);
    assertTrue(unit.isEquivalentOf(unit));
  }

}
