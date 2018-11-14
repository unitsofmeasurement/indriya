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

import javax.measure.Quantity;
import javax.measure.Unit;

import tech.units.indriya.AbstractQuantity;
import tech.units.indriya.ComparableQuantity;

/**
 * An amount of quantity, consisting of a short and a Unit. ByteQuantity objects are immutable.
 * 
 * @see AbstractQuantity
 * @see Quantity
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @param <Q>
 *            The type of the quantity.
 * @version 0.3, $Date: 2018-10-31 $
 * @since 1.0
 */
final class ByteQuantity<Q extends Quantity<Q>> extends JavaNumericQuantity<Q> {

    private static final long serialVersionUID = 6325849816534488248L;

    private static final BigDecimal BYTE_MIN_VALUE = new BigDecimal(Byte.MIN_VALUE);
    private static final BigDecimal BYTE_MAX_VALUE = new BigDecimal(Byte.MAX_VALUE);

    private final byte value;

    ByteQuantity(byte value, Unit<Q> unit, boolean abs) {
        super(unit, abs);
        this.value = value;
    }

    ByteQuantity(byte value, Unit<Q> unit) {
        super(unit);
        this.value = value;
    }

    @Override
    public Byte getValue() {
        return value;
    }

    @Override
    public boolean isBig() {
        return false;
    }

    @Override
    boolean isOverflowing(BigDecimal aValue) {
        return aValue.compareTo(BYTE_MIN_VALUE) < 0 || aValue.compareTo(BYTE_MAX_VALUE) > 0;
    }

    @Override
    public ComparableQuantity<?> inverse() {
        return NumberQuantity.of(1 / value, getUnit().inverse());
    }

    @Override
    public boolean isDecimal() {
        return false;
    }

    @Override
    public int getSize() {
        return Byte.SIZE;
    }

    @Override
    public Class<?> getNumberType() {
        return byte.class;
    }

    @Override
    Number castFromBigDecimal(BigDecimal aValue) {
        return (byte) aValue.longValue();
    }

    @Override
    public Quantity<Q> negate() {
        return new ByteQuantity<Q>((byte) (-value), getUnit());
    }
}