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
package tech.units.indriya.unit;

import java.math.BigDecimal;
import java.util.logging.Logger;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import org.junit.jupiter.api.Test;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

import static org.junit.jupiter.api.Assertions.*;
import static javax.measure.MetricPrefix.*;

/**
 *
 * @author Werner Keil
 */
public class CompoundUnitTest {
  static final Logger logger = Logger.getLogger(CompoundUnitTest.class.getName());

  @Test
  public void testLength() {
    Unit<Length> compLen =  ((AbstractUnit<Length>)Units.METRE).compound(CENTI(Units.METRE));
    Quantity<Length> l1 = Quantities.getQuantity(1.70, compLen);
    assertEquals(1.7d, l1.getValue());
    assertEquals("m:cm", l1.getUnit().toString()); // TODO this does not make much sense yet
  }
  
  @Test
  public void testLengths() {
    Unit<Length> compLen =  ((AbstractUnit<Length>)Units.METRE).compound(CENTI(Units.METRE));
    Number[] numList = {1, 70};
    Quantity<Length> l1 = Quantities.getCompoundQuantity(numList, compLen);
    assertEquals(BigDecimal.valueOf(1.7d), l1.getValue());
    assertEquals("m", l1.getUnit().toString());
  }
  
  @Test
  public void testConvert() {
    Unit<Length> compLen =  ((AbstractUnit<Length>)Units.METRE).compound(CENTI(Units.METRE));
    Quantity<Length> l1 = Quantities.getQuantity(170, CENTI(Units.METRE));
    assertEquals(170, l1.getValue());
    assertEquals("cm", l1.getUnit().toString());
    Quantity<Length> l2 = l1.to(compLen);
    logger.info(String.valueOf(l2));
  }

}