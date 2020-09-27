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
package tech.units.indriya.spi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import javax.measure.spi.FormatService;
import javax.measure.spi.ServiceProvider;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link ServiceProvider} and services provided by it.
 */
public class ServiceTest {

    @Test
    public void testGetServices() throws Exception {
        final List<ServiceProvider> services = ServiceProvider.available();
        assertNotNull(services);
        assertFalse(services.isEmpty());
        // assertTrue(services.contains("service1"));
        assertTrue(services.size() > 0);
        // assertTrue(services.contains("service2"));
        // services = Collection.class.cast(Bootstrap.getServices(Runtime.class));
        // assertNotNull(services);
        // assertTrue(services.isEmpty());
    }

    @Test
    public void testGetFormatService() throws Exception {
        final FormatService fs = ServiceProvider.current().getFormatService();
        assertNotNull(fs);
        assertNotNull(fs.getUnitFormat());
        assertEquals("DefaultFormat", fs.getUnitFormat().getClass().getSimpleName());
    }

    @Test
    public void testOf() throws Exception {
        final ServiceProvider provider = ServiceProvider.of("Default");
        assertNotNull(provider);
        assertEquals("Default", provider.toString());
        assertEquals("DefaultServiceProvider", provider.getClass().getSimpleName());
    }

    @Test
    public void testOfNull() {
        assertThrows(NullPointerException.class, () -> {
            @SuppressWarnings("unused")
            ServiceProvider provider = ServiceProvider.of(null);
        });
    }

    @Test
    public void testOfNotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            @SuppressWarnings("unused")
            ServiceProvider provider = ServiceProvider.of("ThisServiceProviderWontExistHere");
        });
    }
}
