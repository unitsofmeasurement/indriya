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
package tech.units.indriya.quantity;

import static javax.measure.Quantity.Scale;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.measure.MeasurementException;
import javax.measure.Quantity;
import javax.measure.Unit;

import tech.units.indriya.format.SimpleQuantityFormat;
import tech.uom.lib.common.function.QuantityConverter;

/**
 * <p>
 * This class represents multi-radix quantities (like "1 hour, 5 min, 30 sec" or "6 ft, 3 in").
 * </p>
 * 
 * @param <Q>
 *            The type of the quantity.
 * 
 * @author <a href="mailto:werner@units.tech">Werner Keil</a>
 * @version 1.2, April 14, 2019
 * @see <a href="http://www.thefreedictionary.com/Compound+quantity">Free Dictionary: Compound Quantity</a>
 */
public class CompoundQuantity<Q extends Quantity<Q>> implements QuantityConverter<Q>, Serializable {
    // TODO could it be final?
    /**
    * 
    */
    private static final long serialVersionUID = 5863961588282485676L;

    private final Map<Unit<Q>, Quantity<Q>> quantMap = new LinkedHashMap<>();

    /**
     * @param quantities the list of quantities to construct this CompoundQuantity.
     * @throws NullPointerException
     *             if the given quantities are <code>null</code>.
    * @throws MeasurementException
    *             if this CompositeQuantity is empty or contains only <code>null</code> values.
    */
    protected CompoundQuantity(final List<Quantity<Q>> quantities) {
        Objects.requireNonNull(quantities);
        final Scale firstScale = quantities.get(0).getScale();        
        for (Quantity<Q> q : quantities) {
            if (firstScale.equals(q.getScale())) {
                quantMap.put(q.getUnit(), q);
            } else {
                throw new MeasurementException("Quantities do not have the same scale.");
            }
        }
    }

    /**
     * Returns an {@code CompositeQuantity} with the specified values.
     * 
     * @param <Q>
     *            The type of the quantity.
     */
    @SafeVarargs
    public static <Q extends Quantity<Q>> CompoundQuantity<Q> of(Quantity<Q>... quantities) {
        return of(Arrays.asList(quantities));
    }
    
    /**
     * Returns an {@code CompositeQuantity} with the specified values.
     * 
     * @param <Q>
     *            The type of the quantity.
     */
    public static <Q extends Quantity<Q>> CompoundQuantity<Q> of(List<Quantity<Q>> quantities) {
        return new CompoundQuantity<>(quantities);
    }

    /**
     * Gets the set of units in this CompositeQuantity.
     * <p>
     * This set can be used in conjunction with {@link #get(Unit)} to access the entire quantity.
     *
     * @return a set containing the units, not null
     */
    public Set<Unit<Q>> getUnits() {
        return quantMap.keySet();
    }

    /**
     * Gets quantities in this CompositeQuantity.
     *
     * @return a collection containing the quantities, not null
     */
    public Collection<Quantity<Q>> getQuantities() {
        return quantMap.values();
    }

    /**
     * Gets the Quantity of the requested Unit.
     * <p>
     * This returns a value for each Unit in this CompositeQuantity. Or <type>null</type> if the given unit is not included.
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
        return SimpleQuantityFormat.getInstance().format(this);
    }

    /**
     * Returns the <b>sum</b> of all quantity values in this CompositeQuantity converted into another (compatible) unit.
     * @param unit
     *            the {@code Unit unit} in which the returned quantity is stated.
     * @return the sum of all quantities in this CompositeQuantity or a new quantity stated in the specified unit.
     * @throws ArithmeticException
     *             if the result is inexact and the quotient has a non-terminating decimal expansion.
     * @throws IllegalArgumentException
     *             if this CompositeQuantity is empty or contains only <code>null</code> values.
     */
    @Override
    public Quantity<Q> to(Unit<Q> unit) {
        if (quantMap.isEmpty()) {
            throw new IllegalArgumentException("No quantity found, cannot convert an empty value");
        }
        Quantity<Q> result = null;
        for (Quantity<Q> q : quantMap.values()) {
            if (result == null) {
                result = q;
            } else {
                result = result.add(q);
            }
        }
        return result.to(unit);
    }

    /**
     * Indicates if this mixed quantity is considered equal to the specified object (both are mixed units with same composing units in the same order).
     *
     * @param obj
     *            the object to compare for equality.
     * @return <code>true</code> if <code>this</code> and <code>obj</code> are considered equal; <code>false</code>otherwise.
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof CompoundQuantity) {
            CompoundQuantity<?> c = (CompoundQuantity<?>) obj;
            return Objects.equals(quantMap, c.quantMap);
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(quantMap);
    }
}
