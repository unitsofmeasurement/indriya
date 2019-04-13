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

import java.math.BigDecimal;

import javax.measure.Quantity;
import javax.measure.Unit;

import tech.units.indriya.AbstractQuantity;
import tech.units.indriya.ComparableQuantity;

/**
 * An amount of quantity, consisting of a long and a Unit. LongQuantity objects are immutable.
 * 
 * @see AbstractQuantity
 * @see Quantity
 * @author <a href="mailto:werner@uom.technology">Werner Keil</a>
 * @param <Q>
 *            The type of the quantity.
 * @version 0.5, $Date: 2018-10-31 $
 * @since 1.0
 */
final class LongQuantity<Q extends Quantity<Q>> extends JavaNumericQuantity<Q> {

    private static final long serialVersionUID = 3092808554937634365L;

    private static final BigDecimal LONG_MAX_VALUE = new BigDecimal(Long.MAX_VALUE);
    private static final BigDecimal LONG_MIN_VALUE = new BigDecimal(Long.MIN_VALUE);

    private final long value;

    LongQuantity(long value, Unit<Q> unit,  Scale sc) {
        super(unit, sc);
        this.value = value;
    }

    LongQuantity(long value, Unit<Q> unit) {
        super(unit);
        this.value = value;
    }

    @Override
    public Long getValue() {
        return value;
    }

    @Override
    boolean isOverflowing(BigDecimal aValue) {
        return aValue.compareTo(LONG_MIN_VALUE) < 0 || aValue.compareTo(LONG_MAX_VALUE) > 0;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ComparableQuantity<Q> inverse() {
        return new LongQuantity(1 / value, getUnit().inverse());
    }

    @Override
    public boolean isBig() {
        return false;
    }

    @Override
    public boolean isDecimal() {
        return false;
    }

    @Override
    public int getSize() {
        return Long.SIZE;
    }

    @Override
    public Class<?> getNumberType() {
        return long.class;
    }

    @Override
    Number castFromBigDecimal(BigDecimal aValue) {
        return aValue.longValue();
    }

    @Override
    public Quantity<Q> negate() {
        return new LongQuantity<Q>(-value, getUnit());
    }
}
