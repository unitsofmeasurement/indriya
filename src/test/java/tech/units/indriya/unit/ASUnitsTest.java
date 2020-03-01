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
package tech.units.indriya.unit;

import static tech.units.indriya.format.UnitStyle.*;

import java.util.logging.Logger;

import javax.measure.Unit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tech.units.indriya.AbstractSystemOfUnits;
import tech.units.indriya.unit.Units;

import static org.junit.jupiter.api.Assertions.*;
import static tech.units.indriya.unit.Units.*;

/**
 * Tests the AbstractSystemOfUnits aspect of the Units class.
 * 
 * @author Werner Keil
 */
public class ASUnitsTest {
    static final Logger logger = Logger.getLogger(ASUnitsTest.class.getName());

    private AbstractSystemOfUnits sou;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @BeforeEach
    public void setUp() throws Exception {
        sou = Units.getInstance();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @AfterEach
    public void tearDown() throws Exception {
        sou = null;
    }

    @Test
    public void testByNameM() {
        final Unit<?> u = sou.getUnit("Metre", NAME);
        assertNotNull(u);
        assertEquals(METRE, u);
    }
    
    @Test
    public void testByNameMIgnoreCase() {
        final Unit<?> u = sou.getUnit("mEtre", NAME, true);
        assertNotNull(u);
        assertEquals(METRE, u);
    }

    @Test
    public void testByNameKg() {
        final Unit<?> u = sou.getUnit("Kilogram", NAME);
        assertNotNull(u);
        assertEquals(KILOGRAM, u);
    }
    
    @Test
    public void testByNameKgIgnoreCase() {
        final Unit<?> u = sou.getUnit("kilogram", NAME, true);
        assertNotNull(u);
        assertEquals(KILOGRAM, u);
    }

    @Test
    public void testBySymbolKg() {
        final Unit<?> u = sou.getUnit("kg", SYMBOL);
        assertNotNull(u);
        assertEquals(KILOGRAM, u);
    }

    @Test
    public void testBySymbolW() {
        final Unit<?> u = sou.getUnit("W", SYMBOL);
        assertNotNull(u);
        assertEquals(WATT, u);
    }
    	
    @Test
    public void testByNameW() {
        final Unit<?> u = sou.getUnit("Watt", NAME);
        assertNotNull(u);
        assertEquals(WATT, u);
    }
    
    @Test
    public void testByNameWIgnoreCase() {
        final Unit<?> u = sou.getUnit("WATT", NAME, true);
        assertNotNull(u);
        assertEquals(WATT, u);
    }
    
    @Test
    public void testBySymbolS() {
        final Unit<?> u = sou.getUnit("s", SYMBOL);
        assertNotNull(u);
        assertEquals(SECOND, u);
    }
    	
    @Test
    public void testByNameS() {
        final Unit<?> u = sou.getUnit("Second", NAME);
        assertNotNull(u);
        assertEquals(SECOND, u);
    }
    
    @Test
    public void testBySymbolSIgnoreCase() {
        final Unit<?> u = sou.getUnit("S", SYMBOL, true);
        assertNotNull(u);
        assertEquals(SIEMENS, u);
        // Here we actually get SIEMENS, only in a UNIQUE system like UCUM this can be avoided, otherwise it should be case-sensitive.
    }
    
    @Test
    public void testByNameSIgnoreCase() {
        final Unit<?> u = sou.getUnit("second", NAME, true);
        assertNotNull(u);
        assertEquals(SECOND, u);
    }
    
    @Test
    public void testByNameCel() {
        final Unit<?> u = sou.getUnit("Celsius", NAME);
        assertNotNull(u);
        assertEquals(CELSIUS, u);
    }
    
    @Test
    public void testByNameCelIgnoreCase() {
        final Unit<?> u = sou.getUnit("CELSIUS", NAME, true);
        assertNotNull(u);
        assertEquals(CELSIUS, u);
    }
    
    @Test
    public void testByNameKat() {
        final Unit<?> u = sou.getUnit("Katal", NAME);
        assertNotNull(u);
        assertEquals(KATAL, u);
    }
    
    @Test
    public void testByNameKatIgnoreCase() {
        final Unit<?> u = sou.getUnit("KaTAL", NAME, true);
        assertNotNull(u);
        assertEquals(KATAL, u);
    }

}