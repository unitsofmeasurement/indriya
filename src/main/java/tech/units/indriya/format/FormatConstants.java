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
