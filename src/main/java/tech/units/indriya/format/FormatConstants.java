/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2021, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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

/**
 * Format related constants
 * 
 * @author keilw
 * @since 2.0
 */
interface FormatConstants {
    /** Operator precedence for the addition and subtraction operations */
    static final int ADDITION_PRECEDENCE = 0;

    /** Operator precedence for the multiplication and division operations */
    static final int PRODUCT_PRECEDENCE = ADDITION_PRECEDENCE + 2;

    /** Operator precedence for the exponentiation and logarithm operations */
    static final int EXPONENT_PRECEDENCE = PRODUCT_PRECEDENCE + 2;

    static final char MIDDLE_DOT = '\u00b7'; // $NON-NLS-1$

    /** Exponent 1 character */
    static final char EXPONENT_1 = '\u00b9'; // $NON-NLS-1$

    /** Exponent 2 character */
    static final char EXPONENT_2 = '\u00b2'; // $NON-NLS-1$

    /**
     * Operator precedence for a unit identifier containing no mathematical operations (i.e., consisting exclusively of an identifier and possibly a
     * prefix). Defined to be <code>Integer.MAX_VALUE</code> so that no operator can have a higher precedence.
     */
    static final int NOOP_PRECEDENCE = Integer.MAX_VALUE;
}
