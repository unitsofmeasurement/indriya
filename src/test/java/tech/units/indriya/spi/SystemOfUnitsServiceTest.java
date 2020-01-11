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
package tech.units.indriya.spi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Collection;
import javax.measure.BinaryPrefix;
import javax.measure.MetricPrefix;
import javax.measure.Prefix;
import javax.measure.spi.ServiceProvider;
import javax.measure.spi.SystemOfUnits;
import javax.measure.spi.SystemOfUnitsService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class SystemOfUnitsServiceTest {
  private static SystemOfUnitsService service;

  @BeforeAll
  public static void setUp() {
    service = ServiceProvider.current().getSystemOfUnitsService();
  }

  @Test
  public void testGetUnitSystem() {
    assertNotNull(service);
    SystemOfUnits system = service.getSystemOfUnits();
    assertNotNull(system);
    assertEquals("Units", system.getClass().getSimpleName());
    assertNotNull(system.getUnits());
    // for (Unit<?> u : system.getUnits()) {
    // System.out.println(u.toString());
    // }
    /*
     * Set<? extends Unit<?>> units = system.getUnits(); List list =
     * Arrays.asList((units.toArray())); Collections.sort(list); for (Object
     * o : list) { System.out.println(o.toString()); }
     */
    assertEquals(43, system.getUnits().size());
  }

  @Test
  public void testGetMetricPrefixes() {
    assertNotNull(service);
    Collection<MetricPrefix> prefixes = service.getPrefixes(MetricPrefix.class);
    assertNotNull(prefixes);
    assertEquals(20, prefixes.size());
  }

  @Test
  public void testGetBinaryPrefixes() {
    assertNotNull(service);
    Collection<BinaryPrefix> prefixes = service.getPrefixes(BinaryPrefix.class);
    assertNotNull(prefixes);
    assertEquals(8, prefixes.size());
  }

  @Test
  public void testWrongPrefix() {
	    assertThrows(ClassCastException.class, () -> {
	    	@SuppressWarnings("unused")
			Collection<Prefix> prefixes = service.getPrefixes((Class) BigDecimal.class);
        });
  }
}
