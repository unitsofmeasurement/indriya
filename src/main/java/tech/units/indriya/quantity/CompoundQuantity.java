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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.measure.Quantity;
import javax.measure.Quantity.Scale;
import javax.measure.Unit;

import tech.units.indriya.format.SimpleQuantityFormat;
import tech.units.indriya.function.Calculus;
import tech.units.indriya.function.MixedRadix;
import tech.units.indriya.internal.function.calc.Calculator;
import tech.units.indriya.spi.NumberSystem;
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
 * @author Andi Huber
 * @version 1.4, June 12, 2019
 * @see <a href="http://www.thefreedictionary.com/Compound+quantity">Free Dictionary: Compound Quantity</a>
 */
public class CompoundQuantity<Q extends Quantity<Q>> implements QuantityConverter<Q>, Serializable {
    // TODO could it be final?
    /**
    * 
    */
    private static final long serialVersionUID = 5863961588282485676L;

    private final List<Quantity<Q>> quantityList;
    private final Object[] quantityArray;
    private final List<Unit<Q>> unitList;
    private Unit<Q> leastSignificantUnit;
    private Scale commonScale;
    
    // MixedRadix is optimized for best accuracy, when calculating the radix sum, so we try to use it if possible
    private MixedRadix<Q> mixedRadixIfPossible;

    /**
     * @param quantities - the list of quantities to construct this CompoundQuantity.
     */
    protected CompoundQuantity(final List<Quantity<Q>> quantities) {
        
        final List<Unit<Q>> unitList = new ArrayList<>();
        
        for (Quantity<Q> q : quantities) {
            
            final Unit<Q> unit = q.getUnit();
            
            unitList.add(unit);
            
            commonScale = q.getScale();
            
            // keep track of the least significant unit, thats the one that should 'drive' arithmetic operations

            
            if(leastSignificantUnit==null) {
                leastSignificantUnit = unit;
            } else {
                final NumberSystem ns = Calculus.currentNumberSystem();
                final Number leastSignificantToCurrentFactor = leastSignificantUnit.getConverterTo(unit).convert(1);
                final boolean isLessSignificant = ns.isLessThanOne(ns.abs(leastSignificantToCurrentFactor));
                if(isLessSignificant) {
                    leastSignificantUnit = unit;
                }
            }
            
        }
        
        this.quantityList = Collections.unmodifiableList(new ArrayList<>(quantities));
        this.quantityArray = quantities.toArray();
        
        this.unitList = Collections.unmodifiableList(unitList);
        
        try {
                        
            // - will throw if units are not in decreasing order of significance
            mixedRadixIfPossible = MixedRadix.of(getUnits());
            
        } catch (Exception e) {
            
            mixedRadixIfPossible = null;
        }
        
    }

    /**
     * @param <Q>
     * @param quantities
     * @return a {@code CompoundQuantity} with the specified {@code quantities}
     * @throws IllegalArgumentException
     *             if given {@code quantities} is {@code null} or empty 
     *             or contains any <code>null</code> values
     *             or contains quantities of mixed scale
     * 
     */
    @SafeVarargs
    public static <Q extends Quantity<Q>> CompoundQuantity<Q> of(Quantity<Q>... quantities) {
        guardAgainstIllegalQuantitiesArgument(quantities);
        return new CompoundQuantity<>(Arrays.asList(quantities));
    }

    /**
     * @param <Q>
     * @param quantities
     * @return a {@code CompoundQuantity} with the specified {@code quantities}
     * @throws IllegalArgumentException
     *             if given {@code quantities} is {@code null} or empty 
     *             or contains any <code>null</code> values
     *             or contains quantities of mixed scale
     * 
     */
    public static <Q extends Quantity<Q>> CompoundQuantity<Q> of(List<Quantity<Q>> quantities) {
        guardAgainstIllegalQuantitiesArgument(quantities);
        return new CompoundQuantity<>(quantities);
    }

    /**
     * Gets the set of units in this CompoundQuantity.
     * <p>
     * This set can be used in conjunction with {@link #get(Unit)} to access the entire quantity.
     *
     * @return a set containing the units, not null
     */
    public List<Unit<Q>> getUnits() {
        return unitList;
    }

    /**
     * Gets quantities in this CompoundQuantity.
     *
     * @return a list containing the quantities, not null
     */
    public List<Quantity<Q>> getQuantities() {
        return quantityList;
    }

//TODO[211] deprecated    
//    /**
//     * Gets the Quantity of the requested Unit.
//     * <p>
//     * This returns a value for each Unit in this CompoundQuantity. Or <type>null</type> if the given unit is not included.
//     *
//     */
//    public Quantity<Q> get(Unit<Q> unit) {
//        return quantMap.get(unit);
//    }

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
     * Returns the <b>sum</b> of all quantity values in this CompoundQuantity converted into another (compatible) unit.
     * @param unit
     *            the {@code Unit unit} in which the returned quantity is stated.
     * @return the sum of all quantities in this CompoundQuantity or a new quantity stated in the specified unit.
     * @throws ArithmeticException
     *             if the result is inexact and the quotient has a non-terminating decimal expansion.
     */
    @Override
    public Quantity<Q> to(Unit<Q> unit) {
        
        // MixedRadix is optimized for best accuracy, when calculating the radix sum, so we use it if possible
        if(mixedRadixIfPossible!=null) {
            Number[] values = getQuantities()
            .stream()
            .map(Quantity::getValue)
            .collect(Collectors.toList())
            .toArray(new Number[0]);
            
            return mixedRadixIfPossible.createQuantity(values).to(unit);            
        }
        
        // fallback

        final Calculator calc = Calculator.of(0);
        
        for (Quantity<Q> q : quantityList) {
            
            final Number termInLeastSignificantUnits = 
                    q.getUnit().getConverterTo(leastSignificantUnit).convert(q.getValue());
            
            calc.add(termInLeastSignificantUnits);
        }
        
        final Number sumInLeastSignificantUnits = calc.peek();
        
        return Quantities.getQuantity(sumInLeastSignificantUnits, leastSignificantUnit, commonScale).to(unit);
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
            return Arrays.equals(quantityArray, c.quantityArray);
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(quantityArray);
    }
    
    // -- IMPLEMENTATION DETAILS
    
    private static void guardAgainstIllegalQuantitiesArgument(Quantity<?>[] quantities) {
        if (quantities == null || quantities.length < 1) {
            throw new IllegalArgumentException("At least one quantity is required.");
        }
        Scale firstScale = null;  
        for(Quantity<?> q : quantities) {
            if(q==null) {
                throw new IllegalArgumentException("Quantities must not contain null.");
            }
            if(firstScale==null) {
                firstScale = q.getScale();
                if(firstScale==null) {
                    throw new IllegalArgumentException("Quantities must have a scale.");
                }   
            }
            if (!firstScale.equals(q.getScale())) {
                throw new IllegalArgumentException("Quantities do not have the same scale.");
            }
        }
    }
    
    // almost a duplicate of the above, this is to keep heap pollution at a minimum
    private static <Q extends Quantity<Q>> void guardAgainstIllegalQuantitiesArgument(List<Quantity<Q>> quantities) {
        if (quantities == null || quantities.size() < 1) {
            throw new IllegalArgumentException("At least one quantity is required.");
        }
        Scale firstScale = null;  
        for(Quantity<Q> q : quantities) {
            if(q==null) {
                throw new IllegalArgumentException("Quantities must not contain null.");
            }
            if(firstScale==null) {
                firstScale = q.getScale();
                if(firstScale==null) {
                    throw new IllegalArgumentException("Quantities must have a scale.");
                }
            }
            if (!firstScale.equals(q.getScale())) {
                throw new IllegalArgumentException("Quantities do not have the same scale.");
            }
        }
    }


}
