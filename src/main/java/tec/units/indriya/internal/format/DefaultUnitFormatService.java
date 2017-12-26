/*
 * Next Generation Units of Measurement Implementation
 * Copyright (c) 2005-2017, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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
 * 3. Neither the name of JSR-363, Indriya nor the names of their contributors may be used to endorse or promote products
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
package tec.units.indriya.internal.format;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.measure.format.UnitFormat;
import javax.measure.spi.UnitFormatService;

import tec.units.indriya.format.EBNFUnitFormat;
import tec.units.indriya.format.LocalUnitFormat;
import tec.units.indriya.format.SimpleUnitFormat;
import tec.units.indriya.format.SimpleUnitFormat.Flavor;
import tec.uom.lib.common.function.IntPrioritySupplier;

/**
 * Default format service.
 *
 * @author Werner Keil
 * @version 0.5, January 19, 2017
 */
public class DefaultUnitFormatService implements UnitFormatService, IntPrioritySupplier {
  static final int PRIO = 1000;

  private static final String DEFAULT_FORMAT = Flavor.Default.name();

  private final Map<String, UnitFormat> formats = new HashMap<>();

  public DefaultUnitFormatService() {
    formats.put(DEFAULT_FORMAT, SimpleUnitFormat.getInstance());
    formats.put(Flavor.ASCII.name(), SimpleUnitFormat.getInstance(Flavor.ASCII));
    formats.put("EBNF", EBNFUnitFormat.getInstance());
    formats.put("Local", LocalUnitFormat.getInstance());
  }

  /*
   * (non-Javadoc)
   * 
   * @see UnitFormatService#getUnitFormat(String)
   */
  @Override
  public UnitFormat getUnitFormat(String formatName) {
    Objects.requireNonNull(formatName, "Format name required");
    return formats.get(formatName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see UnitFormatService#getUnitFormat()
   */
  @Override
  public UnitFormat getUnitFormat() {
    return getUnitFormat(DEFAULT_FORMAT);
  }

  public Set<String> getAvailableFormatNames() {
    return formats.keySet();
  }

  @Override
  public int getPriority() {
    return PRIO;
  }
}
