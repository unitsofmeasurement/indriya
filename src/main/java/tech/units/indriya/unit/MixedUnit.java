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

import javax.measure.UnitConverter;

import tech.units.indriya.AbstractUnit;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * <p>
 * This class represents multi-radix units (such as "hour:min:sec" or "ft, in"). Instances of this class are created using the {@link Unit#mix
 * Unit.mix} method.
 * </p>
 * 
 * <p>
 * Examples of mixed units:<code> Unit<Time> HOUR_MINUTE_SECOND = HOUR.mix(MINUTE).mix(SECOND); <br>Unit<Length> FOOT_INCH =
 * FOOT.mix(INCH); </code>
 * </p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 1.13, March 25, 2019
 * @since 2.0
 */
public final class MixedUnit<Q extends Quantity<Q>> extends AbstractUnit<Q> {

    /**
     * 
     */
    private static final long serialVersionUID = -6588505921476784171L;

    /**
     * Holds the list of units.
     */
    private final List<Unit<Q>> units;

    /**
     * Creates a mixed unit from the specified units.
     *
     * @param units
     *            the units.
     * @throws IllegalArgumentException
     *             if all units do not have the same system unit.
     */
    @SafeVarargs
    private MixedUnit(final Unit<Q>... units) {
        Objects.requireNonNull(units);
        final Unit<Q> systemUnit = units[0].getSystemUnit();
        for (Unit<Q> unit : units) {
            if (!systemUnit.equals(unit.getSystemUnit()))
                throw new IllegalArgumentException("Units do not have the same system unit");
        }
        this.units = Arrays.asList(units);
    }

    /**
     * Returns the list of units uniquely defining the value of this MixedUnit. The list is a snapshot of the units at the time {@code getUnits} is
     * called and is not mutable. The units are ordered in as they were added to this MixedUnit.
     *
     * implSpec The list of units completely and uniquely represents the state of the object without omissions, overlaps or duplication. The units are
     * in order they were added.
     *
     * @return the List of {@code Units}; not null
     */
    public List<Unit<Q>> getUnits() {
        return units;
    }

    /**
     * Indicates if this mixed unit is considered equal to the specified object (both are mixed units with same composing units in the same
     * order).
     *
     * @param obj
     *            the object to compare for equality.
     * @return <code>true</code> if <code>this</code> and <code>obj</code> are considered equal; <code>false</code>otherwise.
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof MixedUnit) {
            MixedUnit<?> thatUnit = (MixedUnit<?>) obj;
            return Objects.equals(units, thatUnit.getUnits());
        }
        if (obj instanceof AbstractUnit) {
            return AbstractUnit.Equalizer.areEqual(this, (AbstractUnit<?>) obj);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(units);
    }

    @Override
    public UnitConverter getSystemConverter() {
        /*
        UnitConverter sysConverter = null;
        for (Unit<Q> u : units) {
            if (sysConverter == null) {
                sysConverter = ((AbstractUnit<Q>)u).getSystemConverter();
            } else {
                sysConverter = sysConverter.concatenate(((AbstractUnit<Q>)u).getSystemConverter());
            }
        }
        return sysConverter;
        */
        //return ((AbstractUnit<Q>) units.get(0)).getSystemConverter();
        return ((AbstractUnit<Q>) getLeadUnit()).getSystemConverter();
    }

    @Override
    protected Unit<Q> toSystemUnit() {
        return getLeadUnit().getSystemUnit();
    }

    @Override
    public Map<? extends Unit<?>, Integer> getBaseUnits() {
        return getLeadUnit().getBaseUnits();
    }

    @Override
    public Dimension getDimension() {
        return getLeadUnit().getDimension();
    }
    
    /**
     * Returns the lead unit for this MixedUnit. The lead unit is used for all occasions where this MixedUnit has to behave like a single unit.
     * The lead unit shall be a member of {@link #getUnits()}.
     * @return the lead unit; not null
     * @see #getUnits()
     */
    public Unit<Q> getLeadUnit() {
      return units.get(0);
    }
    
    @Override
    public String getSymbol() {
      return getLeadUnit().getSymbol();
    }
    
    /**
     * Returns a mixed unit from the specified units.
     *
     * @param units
     *            the units.
     * @throws IllegalArgumentException
     *             if not all units have the same system unit.
     */
    @SafeVarargs
    public static <Q extends Quantity<Q>> MixedUnit<Q> of(final Unit<Q>... units) {
        return new MixedUnit<>(units);
    }
}
