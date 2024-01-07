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
package tech.units.indriya.function;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.measure.quantity.Dimensionless;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

class DefaultNumberSystemTest {

    DefaultNumberSystem ns;

    @BeforeEach
    void setUp() {
        ns = new DefaultNumberSystem();
    }

    // -- NUMBER COMPARING TESTS

    @ParameterizedTest
    @MethodSource("provideOneSamples")
    void one(final Number x) {
        assertTrue(ns.isOne(x));
    }

    @ParameterizedTest
    @MethodSource("provideZeroSamples")
    void zero(final Number x) {
        assertTrue(ns.isZero(x));
    }

    @ParameterizedTest
    @MethodSource("provideZeroSamples")
    void notOne(final Number x) {
        assertFalse(ns.isOne(x));
    }

    @ParameterizedTest
    @MethodSource("provideOneSamples")
    void notZero(final Number x) {
        assertFalse(ns.isZero(x));
    }

    @ParameterizedTest
    @MethodSource("provideZeroSamples")
    void lessThanOne(final Number x) {
        assertTrue(ns.isLessThanOne(x));
    }

    @ParameterizedTest
    @MethodSource("provideOneSamples")
    void notLessThanOne(final Number x) {
        assertFalse(ns.isLessThanOne(x));
    }

    @ParameterizedTest
    @MethodSource("provideOneSamples")
    void minusOneIsLessThanOne(final Number x) {
        assertTrue(ns.isLessThanOne(ns.negate(x)));
    }

    @ParameterizedTest
    @MethodSource("provideOneSamples")
    void oneToMaxDoubleComparison(final Number one) {

        final ComparableQuantity<Dimensionless> maxDoubleQuantity =
                Quantities.getQuantity(Double.MAX_VALUE, AbstractUnit.ONE);

        final boolean isOneLessThanMaxDouble =
                ns.compare(one, maxDoubleQuantity.getValue()) < 0;

        assertTrue(isOneLessThanMaxDouble);

        final boolean isMaxDoubleGreaterThanOne =
                ns.compare(maxDoubleQuantity.getValue(), one) > 0;

        assertTrue(isMaxDoubleGreaterThanOne);
    }

    @ParameterizedTest
    @MethodSource("provideOneSamples")
    void oneToMaxIntComparison(final Number one) {

        final ComparableQuantity<Dimensionless> maxIntQuantity =
                Quantities.getQuantity(Integer.MAX_VALUE, AbstractUnit.ONE);

        final boolean isOneLessThanMaxInt =
                ns.compare(one, maxIntQuantity.getValue()) < 0;

        assertTrue(isOneLessThanMaxInt);

        final boolean isMaxIntGreaterThanOne =
                ns.compare(maxIntQuantity.getValue(), one) > 0;

        assertTrue(isMaxIntGreaterThanOne);
    }

    @ParameterizedTest
    @MethodSource("provideOneSamples")
    void oneToMaxLongComparison(final Number one) {

        final ComparableQuantity<Dimensionless> maxLongQuantity =
                Quantities.getQuantity(Long.MAX_VALUE, AbstractUnit.ONE);

        final boolean isOneLessThanMaxLong =
                ns.compare(one, maxLongQuantity.getValue()) < 0;

        assertTrue(isOneLessThanMaxLong);

        final boolean isMaxLongGreaterThanOne =
                ns.compare(maxLongQuantity.getValue(), one) > 0;

        assertTrue(isMaxLongGreaterThanOne);
    }

    @ParameterizedTest
    @MethodSource("provideOneSamples")
    void oneToLargeIntComparison(final Number one) {

        final ComparableQuantity<Dimensionless> largeIntQuantity =
                Quantities.getQuantity(
                        BigInteger
                            .valueOf(Long.MAX_VALUE)
                            .add(BigInteger.valueOf(Long.MAX_VALUE)),
                        AbstractUnit.ONE);

        final boolean isOneLessThanLargeInt =
                ns.compare(one, largeIntQuantity.getValue()) < 0;

        assertTrue(isOneLessThanLargeInt);

        final boolean isLargeIntGreaterThanOne =
                ns.compare(largeIntQuantity.getValue(), one) > 0;

        assertTrue(isLargeIntGreaterThanOne);
    }

    @ParameterizedTest
    @MethodSource("provideOneSamples")
    void oneToLargeDecimalComparison(final Number one) {

        final ComparableQuantity<Dimensionless> largeDecimalQuantity =
                Quantities.getQuantity(
                        BigDecimal
                            .valueOf(Double.MAX_VALUE)
                            .add(BigDecimal.valueOf(Double.MAX_VALUE)),
                        AbstractUnit.ONE);

        final boolean isOneLessThanLargeDecimal =
                ns.compare(one, largeDecimalQuantity.getValue()) < 0;

        assertTrue(isOneLessThanLargeDecimal);

        final boolean isLargeDecimalGreaterThanOne =
                ns.compare(largeDecimalQuantity.getValue(), one) > 0;

        assertTrue(isLargeDecimalGreaterThanOne);
    }

    // -- BIG DECIMAL IS-FRACTIONAL TESTS

    static Stream<BigDecimal> provideNonFractionalBigDecimalSamples() {
        return Stream.of(
                BigDecimal.valueOf(-1),
                BigDecimal.valueOf(0),
                BigDecimal.valueOf(1),
                BigDecimal.valueOf(100, 2), // 100 * 10^-2 == 1
                BigDecimal.valueOf(1, -2), // 1 * 10^2 == 100
                new BigDecimal("1234.000") // trailing zeros, should not make this decimal a non integer 
                );
    }
    @ParameterizedTest
    @MethodSource("provideNonFractionalBigDecimalSamples")
    void bigDecimalIntegerChecks(final BigDecimal decimal) {
        assertFalse(DefaultNumberSystem.isFractional(decimal));
    }
    @ParameterizedTest
    @MethodSource("provideNonFractionalBigDecimalSamples")
    void bigDecimalFractionalChecks(final BigDecimal decimal) {
        final BigDecimal fractionalPart = BigDecimal.valueOf(1, 2); // 1 * 10^-2 == 0.01
        assertTrue(DefaultNumberSystem.isFractional(decimal.add(fractionalPart)));
    }

    // -- BIGINTEGER IS-FRACTIONAL SPEED TESTS

    /** For performance comparison, the algorithm in use till version 2.1.4 */
    static boolean isFractionalLegacy(final BigDecimal decimal) {
        try {
            decimal.toBigIntegerExact();
            return false;
        } catch (ArithmeticException e) {
            return true;
        }
    }
    static Stream<Arguments> namedPredicates() {
        return Stream.of(
            Arguments.of(Named.of("Old Method",
                        (Predicate<BigDecimal>)DefaultNumberSystemTest::isFractionalLegacy)),
            Arguments.of(Named.of("New Method",
                    (Predicate<BigDecimal>)DefaultNumberSystem::isFractional))
        );
    }
    @ParameterizedTest
    @MethodSource("namedPredicates")
    void bigDecimalFractionalCheckSpeed(final Predicate<BigDecimal> isFractional) {
        // calculate some arbitrary samples
        final BigDecimal fractionalSample = new BigDecimal(
                "3.141592653589793238462643383279502884197169399375105820974944592307816406286"
                + "208998628034825342117067982148086513282306647093844609550582231725359408128481"
                + "117450284102701938521105559644622948954930381964428810975665933446128475648233"
                + "786783165271201909145648566923460348610454326648213393607260249141273724587006"
                + "606315588174881520920962829254091715364367892590360011330530548820466521384146"
                + "951941511609433057270365759591953092186117381932611793105118548074462379962749"
                + "567351885752724891227938183011949129833673362440656643086021394946395224737190"
                + "702179860943702770539217176293176752384674818467669405132000568127145263560827",
                MathContext.UNLIMITED);
        final BigDecimal integerSample = fractionalSample.multiply(BigDecimal.valueOf(1, -624), MathContext.UNLIMITED);

        assertTrue(isFractional.test(fractionalSample));
        assertFalse(isFractional.test(integerSample));

        final BigDecimal[] samples = new BigDecimal[] {
                fractionalSample,
                integerSample};

        // speed test (doing some indirection to trick the JVM into not optimizing)
        for (int i = 0; i < 300_000; i++) {
            int index = i%2;
            BigDecimal sample = samples[index];
            if(index==0) {
                assertTrue(isFractional.test(sample));
            } else {
                assertFalse(isFractional.test(sample));
            }
        }

    }

    // -- SAMPLER

    static Stream<Number> provideOneSamples() {
        return Stream.<Number>of(
                (byte) 1,
                (short) 1,
                1,
                1L,
                1.,
                1.f,
                BigInteger.ONE,
                BigDecimal.ONE,
                RationalNumber.ONE,
                new AtomicInteger(1),
                new AtomicLong(1L)
                );
    }

    static Stream<Number> provideZeroSamples() {
        return Stream.<Number>of(
                (byte) 0,
                (short) 0,
                0,
                0L,
                0.,
                0.f,
                BigInteger.ZERO,
                BigDecimal.ZERO,
                RationalNumber.ZERO,
                new AtomicInteger(0),
                new AtomicLong(0L)
                );
    }

}
