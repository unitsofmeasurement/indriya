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
package tech.units.indriya.quantity;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.measure.LevelOfMeasurement;
import javax.measure.Quantity;
import javax.measure.Unit;

import tech.units.indriya.AbstractQuantity;
import tech.units.indriya.ComparableQuantity;

/**
 * An amount of quantity, implementation of {@link ComparableQuantity} that uses {@link BigInteger} as implementation of {@link Number}, this object
 * is immutable. Note: all operations which involves {@link Number}, this implementation will convert to {@link BigInteger}.
 *
 * @param <Q>
 *            The type of the quantity.
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @see AbstractQuantity
 * @see Quantity
 * @see ComparableQuantity
 * @version 0.5
 * @since 2.0
 */
final class BigIntegerQuantity<Q extends Quantity<Q>> extends JavaNumberQuantity<Q> {

    private static final long serialVersionUID = -593014349777834846L;
    private final BigInteger value;

    public BigIntegerQuantity(BigInteger value, Unit<Q> unit, LevelOfMeasurement level) {
        super(unit, level);
        this.value = value;
    }

    public BigIntegerQuantity(BigInteger value, Unit<Q> unit) {
        super(unit);
        this.value = value;
    }

    public BigIntegerQuantity(long value, Unit<Q> unit) {
        this(BigInteger.valueOf(value), unit);
    }

    /**
     * <p>
     * Returns a {@code BigIntegerQuantity} with same Unit, but whose value is {@code(-this.getValue())}. </p>
     * 
     * @return {@code -this}.
     */
    public BigIntegerQuantity<Q> negate() {
        return new BigIntegerQuantity<Q>(value.negate(), getUnit());
    }

    @Override
    public BigInteger getValue() {
        return value;
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public ComparableQuantity<Q> inverse() {
        return (ComparableQuantity<Q>) Quantities.getQuantity(BigInteger.ONE.divide(value), getUnit().inverse());
    }

    @Override
    public boolean isBig() {
        return true;
    }

    @Override
    public boolean isDecimal() {
        return false;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public Class<?> getNumberType() {
        return BigInteger.class;
    }

    @Override
    Number castFromBigDecimal(BigDecimal aValue) {
        return aValue.toBigInteger();
    }

    @Override
    boolean isOverflowing(BigDecimal aValue) {
        return false;
    }
}
