/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2024, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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
package tech.units.indriya.format;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.measure.BinaryPrefix;
import javax.measure.MetricPrefix;
import javax.measure.Prefix;
import javax.measure.Unit;

import tech.units.indriya.format.UnitFormatRoundtripUtil.NonPrefixedUnits;
import tech.units.indriya.function.RationalNumber;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.quantity.time.TemporalQuantity;
import tech.units.indriya.unit.Units;

/**
 * Samples Units, Numbers and Prefixes to feed test cases.
 */
abstract class SamplingUtil {

	public static Stream<Number> numbers() {
		return Stream.of(1.2345d, RationalNumber.of(2, 3), 1234, 1234L, BigInteger.valueOf(1234L),
				new BigDecimal("0.1"));
	}

	public static Stream<Prefix> prefixes() {
		return Stream.of(MetricPrefix.values(), BinaryPrefix.values()).flatMap(Stream::of);
	}

	public static Stream<Unit<?>> units() {
		return Stream
				.of(nonPrefixedUnits(), additionalUnits(), temporalQuantityUnits(), composedUnits(), mixedUnits())
				.flatMap(Function.identity());
	}

	// -- HELPER

	private static Stream<Unit<?>> nonPrefixedUnits() {
		return Stream.of(NonPrefixedUnits.values()).map(u -> u.unit);
	}

	private static Stream<Unit<?>> additionalUnits() {
		return Stream.of(Units.PERCENT, Units.SQUARE_METRE, Units.CUBIC_METRE);
	}

	private static Stream<Unit<?>> composedUnits() {
		return Stream.of(
				Quantities.getQuantity(1, Units.METRE).multiply(Quantities.getQuantity(1, Units.SECOND)).getUnit(),
				Quantities.getQuantity(1, Units.METRE).divide(Quantities.getQuantity(1, Units.SECOND)).getUnit()
		);
	}

	private static Stream<Unit<?>> mixedUnits() {
		return Stream.of(
				Quantities.getQuantity(1, Units.METRE).multiply(Quantities.getQuantity(1, Units.SECOND)).getUnit(),
				Quantities.getQuantity(1, Units.METRE).divide(Quantities.getQuantity(1, Units.SECOND)).getUnit()
		);
	}

	private static Stream<Unit<?>> temporalQuantityUnits() {

		return Stream.of(ChronoUnit.values()).map(chronoUnit -> {
			try {
				return TemporalQuantity.of(1, chronoUnit);
			} catch (Exception e) {
				// TemporalQuantity only supports DAYS, HOURS, MICROS, MILLIS, MINUTES, NANOS,
				// SECONDS
				return null;
			}
		}).filter(Objects::nonNull).map(TemporalQuantity::getUnit);
	}

}
