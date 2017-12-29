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
package tec.units.indriya.quantity;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.measure.MeasurementException;
import javax.measure.Quantity;
import javax.measure.Unit;

import tec.uom.lib.common.function.QuantityConverter;

/**
 * <p>
 * This class represents multi-radix quantities (like "1 hour: 5 min: 30 sec" or "6 ft, 3 in").
 * </p>
 * 
 * @param <Q>
 *          The type of the quantity.
 * 
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 0.2, December 29, 2017
 * @see <a href="http://www.thefreedictionary.com/Compound+quantity">Free Dictionary: Compound Quantity</a>
 */
public class CompoundQuantity<Q extends Quantity<Q>> implements QuantityConverter<Q>, Serializable {
  // TODO could it be final?
  /**
	 * 
	 */
  private static final long serialVersionUID = 5863961588282485676L;

  private final Map<Unit<Q>, Quantity<Q>> quantMap = new LinkedHashMap<>();

  @SafeVarargs
  protected CompoundQuantity(final Quantity<Q>... quantities) {
    for (Quantity<Q> q : quantities) {
      quantMap.put(q.getUnit(), q);
    }
  }

  /**
   * Returns an {@code CompoundQuantity} with the specified values.
   * 
   * @param <Q>
   *          The type of the quantity.
   */
  @SafeVarargs
  public static <Q extends Quantity<Q>> CompoundQuantity<Q> of(Quantity<Q>... quantities) {
    return new CompoundQuantity<>(quantities);
  }

  /**
   * Gets the set of units in this CompoundQuantity.
   * <p>
   * This set can be used in conjunction with {@link #get(Unit)} to access the entire quantity.
   *
   * @return a set containing the units, not null
   */
  public Set<Unit<Q>> getUnits() {
    return quantMap.keySet();
  }

  /**
   * Gets the Quantity of the requested Unit.
   * <p>
   * This returns a value for each Unit in this CompoundQuantity. Or <type>null</type> if the given unit is not included.
   *
   */
  public Quantity<Q> get(Unit<Q> unit) {
    return quantMap.get(unit);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    int pos = 0;
    for (Quantity<Q> q : quantMap.values()) {
      sb.append(q);
      pos++;
      if (pos < quantMap.size()) {
        sb.append(": "); // TODO the pattern/separator should be customizable
      }
    }
    return sb.toString();
  }

  /**
   * Returns the <b>sum</b> of all quantity values in this CompoundQuantity converted into another (compatible) unit.
   * 
   * @return the sum of all quantities in this CompoundQuantity or a new quantity stated in the specified unit.
   * @throws ArithmeticException
   *           if the result is inexact and the quotient has a non-terminating decimal expansion.
   * @throws MeasurementException
   *           if this CompoundQuantity is empty or contains only <code>null</code> values.
   */
  @Override
  public Quantity<Q> to(Unit<Q> type) {
    if (quantMap.isEmpty()) {
      throw new MeasurementException("No quantity found, cannot convert an empty value");
    }
    Quantity<Q> result = null;
    for (Quantity<Q> q : quantMap.values()) {
      if (result == null) {
        result = q;
      } else {
        result = result.add(q);
      }
    }
    return result.to(type);
  }
}
