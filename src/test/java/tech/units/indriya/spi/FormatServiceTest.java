/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2023, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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

import static org.junit.jupiter.api.Assertions.*;
import static javax.measure.spi.FormatService.FormatType.*;
import java.util.List;

import javax.measure.format.QuantityFormat;
import javax.measure.format.UnitFormat;
import javax.measure.spi.FormatService;
import javax.measure.spi.ServiceProvider;

import org.junit.jupiter.api.Test;

/**
 * Tests for services provided via {@link ServiceProvider}.
 */
public class FormatServiceTest {
    
  private static final int QUANTITY_FORMAT_COUNT = 4;


  @Test
  public void testCountServices() throws Exception {
    List<ServiceProvider> services = ServiceProvider.available();
    assertNotNull(services);
    assertFalse(services.isEmpty());
    assertEquals(1, services.size());
  }

  @Test
  public void testGetDefaultService() throws Exception {
    final FormatService fs = ServiceProvider.current().getFormatService();
    assertNotNull(fs);
    assertNotNull(fs.getUnitFormat());
    final UnitFormat uf = fs.getUnitFormat();
    assertNotNull(uf);
    assertEquals("DefaultFormat", uf.getClass().getSimpleName());
  }

  @Test
  public void testGetFormatFound() throws Exception {
	final FormatService fs = ServiceProvider.current().getFormatService();
    assertNotNull(fs);
    final UnitFormat uf = fs.getUnitFormat("EBNF");
    assertNotNull(uf);
    assertEquals("EBNFUnitFormat", uf.toString());
  }

  @Test
  public void testGetFormatNotFound() throws Exception {
	final FormatService fs = ServiceProvider.current().getFormatService();
    assertNotNull(fs);
    assertNull(fs.getUnitFormat("XYZ"));
  }
  
  @Test
  public void testGetServices() throws Exception {
    List<ServiceProvider> services = ServiceProvider.available();
    assertNotNull(services);
    assertFalse(services.isEmpty());
    assertEquals(1, services.size());
  }

  @Test
  public void testGetService() throws Exception {
    FormatService fs = ServiceProvider.current().getFormatService();
    assertNotNull(fs);
    final UnitFormat uf = fs.getUnitFormat();
    assertNotNull(uf);
    assertEquals("DefaultFormat", uf.getClass().getSimpleName());
    assertEquals("SimpleUnitFormat", uf.toString());
  }

  @Test
  public void testGetUnitFormatFound() throws Exception {
    final FormatService fs = ServiceProvider.current().getFormatService();
    assertNotNull(fs);
    final UnitFormat uf = fs.getUnitFormat("EBNF");
    assertNotNull(uf);
    assertEquals("EBNFUnitFormat", uf.toString());
  }
  
  @Test
  public void testGetUnitFormatFoundLC() throws Exception {
    final FormatService fs = ServiceProvider.current().getFormatService();
    assertNotNull(fs);
    final UnitFormat uf = fs.getUnitFormat("ebnf");
    assertNotNull(uf);
    assertEquals("EBNFUnitFormat", uf.toString());
  }
  
  @Test
  public void testGetUnitFormatFoundUC() throws Exception {
    final FormatService fs = ServiceProvider.current().getFormatService();
    assertNotNull(fs);
    final UnitFormat uf = fs.getUnitFormat("DEFAULT");
    assertNotNull(uf);
    assertEquals("DefaultFormat", uf.getClass().getSimpleName());
    assertEquals("SimpleUnitFormat", uf.toString());
  }
  
  @Test
  public void testGetUnitFormatVariant() throws Exception {
    final FormatService fs = ServiceProvider.current().getFormatService();
    assertNotNull(fs);
    final UnitFormat uf = fs.getUnitFormat("Simple", "ASCII");
    assertNotNull(uf);
    assertEquals("ASCIIFormat", uf.getClass().getSimpleName());
    assertEquals("SimpleUnitFormat - ASCII", uf.toString());
  }

  @Test
  public void testGetUnitFormatLocalFound() throws Exception {
    final FormatService fs = ServiceProvider.current().getFormatService();
    assertNotNull(fs);
    final UnitFormat uf = fs.getUnitFormat("Local");
    assertNotNull(uf);
    assertEquals("LocalUnitFormat", uf.toString());
  }

  @Test
  public void testGetUnitFormatNotFound() throws Exception {
    final FormatService fs = ServiceProvider.current().getFormatService();
    assertNotNull(fs);
    assertNull(fs.getUnitFormat("XYZ"));
  }

  @Test
  public void testGetUnitFormatNames() throws Exception {
    final FormatService fs = ServiceProvider.current().getFormatService();
    assertNotNull(fs);
    assertEquals(4, fs.getAvailableFormatNames(UNIT_FORMAT).size());
  }

  @Test
  public void testGetQuantityFormatFound() throws Exception {
    final FormatService fs = ServiceProvider.current().getFormatService();
    assertNotNull(fs);
    final QuantityFormat qf = fs.getQuantityFormat("Simple");
    assertNotNull(qf);
    assertEquals("SimpleQuantityFormat", qf.toString());
  }
  
  @Test
  public void testGetNumDelimQuantityFormatFound() throws Exception {
    final FormatService fs = ServiceProvider.current().getFormatService();
    assertNotNull(fs);
    final QuantityFormat qf = fs.getQuantityFormat("NumberDelimiter");
    assertNotNull(qf);
    assertEquals("NumberDelimiterQuantityFormat", qf.toString());
  }
  
  @Test
  public void testGetEBNFQuantityFormatFound() throws Exception {
    final FormatService fs = ServiceProvider.current().getFormatService();
    assertNotNull(fs);
    final QuantityFormat qf = fs.getQuantityFormat("EBNF");
    assertNotNull(qf);
    assertEquals("NumberDelimiterQuantityFormat", qf.toString());
    assertFalse(qf.isLocaleSensitive());
  }
  
  @Test
  public void testGetEBNFQuantityFormatFoundLC() throws Exception {
    final FormatService fs = ServiceProvider.current().getFormatService();
    assertNotNull(fs);
    final QuantityFormat qf = fs.getQuantityFormat("ebnf");
    assertNotNull(qf);
    assertEquals("NumberDelimiterQuantityFormat", qf.toString());
    assertFalse(qf.isLocaleSensitive());
  }
    
  @Test
  public void testGetAliasQuantityFormatFound() throws Exception {
    final FormatService fs = ServiceProvider.current().getFormatService();
    assertNotNull(fs);
    final QuantityFormat qf = fs.getQuantityFormat("NumberSpace");
    assertNotNull(qf);
    assertEquals("NumberDelimiterQuantityFormat", qf.toString());
  }
  
  @Test
  public void testGetAliasLCQuantityFormatFound() throws Exception {
    final FormatService fs = ServiceProvider.current().getFormatService();
    assertNotNull(fs);
    final QuantityFormat qf = fs.getQuantityFormat("numberspace");
    assertNotNull(qf);
    assertEquals("NumberDelimiterQuantityFormat", qf.toString());
  }
  
  @Test
  public void testGetLocalQuantityFormatFound() throws Exception {
    final FormatService fs = ServiceProvider.current().getFormatService();
    assertNotNull(fs);
    final QuantityFormat qf = fs.getQuantityFormat("Local");
    assertNotNull(qf);
    assertEquals("NumberDelimiterQuantityFormat", qf.toString());
    assertTrue(qf.isLocaleSensitive());
  }
  
  @Test
  public void testGetLocalQuantityFormatFoundUC() throws Exception {
    final FormatService fs = ServiceProvider.current().getFormatService();
    assertNotNull(fs);
    final QuantityFormat qf = fs.getQuantityFormat("LOCAL");
    assertNotNull(qf);
    assertEquals("NumberDelimiterQuantityFormat", qf.toString());
    assertTrue(qf.isLocaleSensitive());
  }
  
  @Test
  public void testGetQuantityFormatNotFound() throws Exception {
    final FormatService fs = ServiceProvider.current().getFormatService();
    assertNotNull(fs);
    assertNull(fs.getQuantityFormat("XYZ"));
  }

  @Test
  public void testGetQuantityFormatNames() throws Exception {
    final FormatService fs = ServiceProvider.current().getFormatService();
    assertNotNull(fs);
    assertEquals(QUANTITY_FORMAT_COUNT, fs.getAvailableFormatNames(QUANTITY_FORMAT).size());
  }
  
  @Test
  public void testGetQuantityService() throws Exception {
    FormatService fs = ServiceProvider.current().getFormatService();
    assertNotNull(fs);
    assertNotNull(fs.getQuantityFormat());
    final QuantityFormat qf = fs.getQuantityFormat();
    assertNotNull(qf);
    assertEquals("SimpleQuantityFormat", qf.getClass().getSimpleName());
    assertFalse(qf.isLocaleSensitive());
  }  
}
