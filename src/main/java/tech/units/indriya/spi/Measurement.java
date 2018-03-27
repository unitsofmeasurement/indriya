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
package tech.units.indriya.spi;

import static tech.units.indriya.spi.AbstractMeasurement.Default;

import java.io.Serializable;
import java.time.Instant;

import javax.measure.Quantity;

import tech.units.indriya.ComparableQuantity;
import tech.uom.lib.common.function.QuantitySupplier;

/**
 * A Measurement contains a {@link Quantity} and a timestamp.
 *
 * <p>
 * A {@code Measurement} object is used for maintaining the tuple of quantity and time-stamp.
 * The value is represented as {@linkplain Quantity}
 * and the time as {@linkplain Instant} plus <type>long</type> for backward-compatibility.
 * <p>
 *
 * @see {@link QuantitySupplier}
 * @author werner
 * @version 0.7
 * @param <Q>
 * @since 1.0
 */
public interface Measurement<Q extends Quantity<Q>> extends QuantitySupplier<Q>, Serializable {

	/**
	 * Returns the timestamp of this {@link Measurement}.
	 *
	 * @return a timestamp.
	 */
	long getTimestamp();

	/**
	 * Returns the {@linkplain Instant} as timestamp.
	 *
	 * @return an instant.
	 */
	Instant getInstant();

	@SuppressWarnings({ })
	static <Q extends Quantity<Q>> Measurement<Q> of(Quantity<Q> q) {
		return new Default<>(q);
	}

	static <Q extends Quantity<Q>> Measurement<Q> of(Quantity<Q> q, Instant i) {
		return new Default<>(q, i);
	}

	static <Q extends Quantity<Q>> Measurement<Q> of(ComparableQuantity<Q> q) {
		return new AbstractMeasurement.DefaultComparable<>(q);
	}

	static <Q extends Quantity<Q>> Measurement<Q> of(ComparableQuantity<Q> q, Instant i) {
		return new AbstractMeasurement.DefaultComparable<>(q, i);
	}
}
