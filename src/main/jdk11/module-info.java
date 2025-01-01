/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2025, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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
module tech.units.indriya {
    requires transitive java.logging;
    requires transitive java.measure;
    requires transitive tech.uom.lib.common;
    requires transitive jakarta.inject;
    
    requires static org.osgi.core;
    requires static org.osgi.compendium;
    requires static org.osgi.annotation;
    
    exports tech.units.indriya;
    exports tech.units.indriya.format;
    exports tech.units.indriya.function;
    exports tech.units.indriya.quantity;
    exports tech.units.indriya.quantity.time;
    exports tech.units.indriya.spi;
    exports tech.units.indriya.unit;

    provides javax.measure.spi.FormatService with
        tech.units.indriya.format.DefaultFormatService;
    provides javax.measure.spi.ServiceProvider with
    	tech.units.indriya.spi.DefaultServiceProvider;
    provides javax.measure.spi.SystemOfUnitsService with
    	tech.units.indriya.unit.DefaultSystemOfUnitsService;    
    provides tech.units.indriya.spi.NumberSystem with
    	tech.units.indriya.function.DefaultNumberSystem;
    
    uses javax.measure.format.QuantityFormat;
    uses javax.measure.format.UnitFormat;
    
    uses javax.measure.spi.FormatService;
    uses javax.measure.spi.ServiceProvider;
    uses javax.measure.spi.SystemOfUnitsService;
    uses tech.units.indriya.spi.NumberSystem;
}
