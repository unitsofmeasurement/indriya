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
package tech.units.indriya.quantity;

import static javax.measure.Quantity.Scale.ABSOLUTE;
import static org.apiguardian.api.API.Status.MAINTAINED;

import javax.measure.Quantity;
import javax.measure.Unit;

import org.apiguardian.api.API;

import tech.units.indriya.AbstractQuantity;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.internal.function.Calculator;
import tech.units.indriya.internal.function.ScaleHelper;

/**
 * Implementation of {@link ComparableQuantity} that holds a Java {@link Number}, 
 * which represented this quantity's amount.
 * <p> 
 * This object is immutable.
 * <p>
 *
 * @see AbstractQuantity
 * @see Quantity
 * @see ComparableQuantity
 * @param <Q>
 *          The type of the quantity.
 * @author Andi Huber
 * @author Werner Keil
 * @version 2.3, May 14, 2021
 * @since 1.0
 * 
 */
@API(status=MAINTAINED)
public final class NumberQuantity<Q extends Quantity<Q>> extends AbstractQuantity<Q> {

    private static final long serialVersionUID = -6494337491031528402L;
    
    private final Number value;

    /**
     * @since 2.0
     */
    protected NumberQuantity(Number number, Unit<Q> unit, Scale sc) {
      super(unit, sc);
      value = Calculator.of(number).peek(); // takes care of invalid number values (infinity, ...)
    }
    
    protected NumberQuantity(Number number, Unit<Q> unit) {
        this(number, unit, ABSOLUTE); 
    }

    @Override
    public ComparableQuantity<Q> add(Quantity<Q> that) {
        return ScaleHelper.addition(this, that, 
                (thisValue, thatValue) -> Calculator.of(thisValue).add(thatValue).peek());
    }

    @Override
    public ComparableQuantity<Q> subtract(Quantity<Q> that) {
        return ScaleHelper.addition(this, that, 
                (thisValue, thatValue) -> Calculator.of(thisValue).subtract(thatValue).peek());
    }

    @Override
    public ComparableQuantity<?> divide(Quantity<?> that) {
        return ScaleHelper.multiplication(this, that, 
                (thisValue, thatValue) -> Calculator.of(thisValue).divide(thatValue).peek(),
                (thisUnit, thatUnit) -> thisUnit.divide(thatUnit));
    }

    @Override
    public ComparableQuantity<Q> divide(Number divisor) {
        return ScaleHelper.scalarMultiplication(this, thisValue -> 
                Calculator.of(thisValue).divide(divisor).peek());
    }

    @Override
    public ComparableQuantity<?> multiply(Quantity<?> that) {
        return ScaleHelper.multiplication(this, that, 
                (thisValue, thatValue) -> Calculator.of(thisValue).multiply(thatValue).peek(),
                (thisUnit, thatUnit) -> thisUnit.multiply(thatUnit));
    }

    @Override
    public ComparableQuantity<Q> multiply(Number factor) {
        return ScaleHelper.scalarMultiplication(this, thisValue -> 
                Calculator.of(thisValue).multiply(factor).peek());
    }

    @Override
    public ComparableQuantity<?> inverse() {
        final Number resultValueInThisUnit = Calculator
                .of(getValue())
                .reciprocal()
                .peek();
        return Quantities.getQuantity(resultValueInThisUnit, getUnit().inverse(), getScale());
    }

    @Override
    public Quantity<Q> negate() {
        final Number resultValueInThisUnit = Calculator
                .of(getValue())
                .negate()
                .peek();
        return Quantities.getQuantity(resultValueInThisUnit, getUnit(), getScale());
    }

    @Override
    public Number getValue() {
        return value;
    }
}