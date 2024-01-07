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
package tech.units.indriya;

import java.math.BigDecimal;
import java.util.Objects;

import org.opentest4j.AssertionFailedError;

import tech.units.indriya.function.Calculus;

/**
 *
 * @author Andi Huber
 */
public class NumberAssertions {

    /**
     * <em>Asserts</em> that {@code expected} and {@code actual} are equal within the given {@code delta}.
     * <p>
     * Arguments are converted to BigDecimal before comparison.
     * 
     * @param expected
     * @param actual
     * @param delta
     */
    public static void assertNumberEquals(Number expected, Number actual, Number delta) {

        final boolean equal_withinDelta = testNumberEquals(expected, actual, delta);

        if(!equal_withinDelta) {
            String message = String.format("expected: <%s> but was: <%s>", ""+expected, ""+actual);
            throw new AssertionFailedError(message);    
        }
    }

    /**
     * <em>Asserts</em> that {@code expected} and {@code actual} Number arrays are equal.
     * <p>Equality imposed by this method is consistent with 
     * {@link NumberAssertions#assertNumberEquals(Number, Number, Number)}.</p>
     * 
     * @param expected
     * @param actual
     * @param delta
     */
    public static void assertNumberArrayEquals(Number[] expected, Number[] actual, Number delta) {

        Objects.requireNonNull(delta);

        assertArraysNotNull(expected, actual);
        assertArraysHaveSameLength(expected.length, actual.length);

        final BigDecimal delta_asBig = NumberAssertionsCalculus.toBigDecimal(delta);


        for(int i=0; i<expected.length; ++i) {

            Number expected_element = expected[i];
            Number actual_element = actual[i];

            if(testNumberEquals(expected_element, actual_element, delta_asBig)) {
                continue;
            }

            failArraysNotEqual(expected_element, actual_element, delta, i);
        }

    }

    // -- HELPER

    private static boolean testNumberEquals(Number expected, Number actual, Number delta) {

        if(expected==null && actual==null) {
            return true;
        }

        if(expected==null || actual==null) {
            return false;
        }

        Objects.requireNonNull(delta);

        final BigDecimal expected_asBig = NumberAssertionsCalculus.toBigDecimal(expected);
        final BigDecimal actual_asBig = NumberAssertionsCalculus.toBigDecimal(actual);
        final BigDecimal delta_asBig = NumberAssertionsCalculus.toBigDecimal(delta);

        final boolean equal_withinDelta =
                expected_asBig.subtract(actual_asBig, Calculus.MATH_CONTEXT).abs()
                .compareTo(delta_asBig)<0;

        return equal_withinDelta;
    }

    private static void assertArraysNotNull(Object expected, Object actual) {

        if (expected == null) {
            throw new AssertionFailedError("expected array was <null>");
        }
        if (actual == null) {
            throw new AssertionFailedError("actual array was <null>");
        }
    }

    private static void assertArraysHaveSameLength(int expected, int actual) {

        if (expected != actual) {
            String message = "array lengths differ, expected: <" + expected
                    + "> but was: <" + actual + ">";
            throw new AssertionFailedError(message);
        }
    }

    private static void failArraysNotEqual(Number expected, Number actual, Number delta, int atIndex) {
        
        String message = String.format("array contents differ at index [%d], "
                + "expected: <%s> but was: <%s>", atIndex, ""+expected, ""+actual);
        
        throw new AssertionFailedError(message);
    }

}
