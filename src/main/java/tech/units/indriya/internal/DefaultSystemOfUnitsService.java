/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2018, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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
package tech.units.indriya.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.measure.spi.Prefix;
import javax.measure.spi.SystemOfUnits;
import javax.measure.spi.SystemOfUnitsService;

import tech.units.indriya.unit.BinaryPrefix;
import tech.units.indriya.unit.MetricPrefix;
import tech.units.indriya.unit.Units;

/**
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 0.5, March 30, 2018
 */
public class DefaultSystemOfUnitsService implements SystemOfUnitsService {

  private final Map<String, SystemOfUnits> souMap = new ConcurrentHashMap<>();

  private final Map<String, Set<Prefix>> prefixMap = new ConcurrentHashMap<>();

  public DefaultSystemOfUnitsService() {
    souMap.put(Units.class.getSimpleName(), Units.getInstance());
    prefixMap.put(MetricPrefix.class.getSimpleName(), MetricPrefix.prefixes());
    prefixMap.put(BinaryPrefix.class.getSimpleName(), BinaryPrefix.prefixes());
  }

  public Collection<SystemOfUnits> getAvailableSystemsOfUnits() {
    return souMap.values();
  }

  @Override
  public SystemOfUnits getSystemOfUnits() {
    return getSystemOfUnits(Units.class.getSimpleName());
  }

  @Override
  public SystemOfUnits getSystemOfUnits(String name) {
    return souMap.get(name);
  }

  @Override
  public Collection<Prefix> getPrefixes(String name) {
    return prefixMap.get(name);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Collection<Prefix> getPrefixes(@SuppressWarnings("rawtypes") Class c) {
    if (c.isEnum()) {
      return Collections.<Prefix> unmodifiableSet(EnumSet.allOf(c));
    } else {
      return Collections.<Prefix> emptyList();
    }
  }
}
