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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.measure.quantity.Length;
import javax.measure.quantity.Temperature;

import org.junit.jupiter.api.Test;

import tech.units.indriya.unit.Units;

public class JavaNumberQuantityTest {

  private static final DoubleQuantity<Temperature> ZERO_DEGREES_CELSIUS = new DoubleQuantity<Temperature>(0.0d, Units.CELSIUS);

  private static final ByteQuantity<Length> BYTE_QUANTITY = new ByteQuantity<Length>((byte) 1, Units.METRE);
  private static final ShortQuantity<Length> SHORT_QUANTITY = new ShortQuantity<Length>((short) 1, Units.METRE);
  private static final IntegerQuantity<Length> INTEGER_QUANTITY = new IntegerQuantity<Length>(1, Units.METRE);
  private static final LongQuantity<Length> LONG_QUANTITY = new LongQuantity<Length>(1L, Units.METRE);
  private static final FloatQuantity<Length> FLOAT_QUANTITY = new FloatQuantity<Length>(1F, Units.METRE);
  private static final DoubleQuantity<Length> DOUBLE_QUANTITY = new DoubleQuantity<Length>(1D, Units.METRE);
  private static final BigIntegerQuantity<Length> BIG_INTEGER_QUANTITY = new BigIntegerQuantity<Length>(BigInteger.ONE, Units.METRE);
  private static final DecimalQuantity<Length> DECIMAL_QUANTITY = new DecimalQuantity<Length>(BigDecimal.ONE, Units.METRE);

  /**
   * Verifies that canWiden always returns the correct value for ByteQuantity as the source type.
   */
  @Test
  public void canWidenReturnsCorrectResultForByteQuantity() {
    assertFalse(BYTE_QUANTITY.canWidenTo(BYTE_QUANTITY));
    assertTrue(BYTE_QUANTITY.canWidenTo(SHORT_QUANTITY));
    assertTrue(BYTE_QUANTITY.canWidenTo(INTEGER_QUANTITY));
    assertTrue(BYTE_QUANTITY.canWidenTo(LONG_QUANTITY));
    assertTrue(BYTE_QUANTITY.canWidenTo(BIG_INTEGER_QUANTITY));
    assertTrue(BYTE_QUANTITY.canWidenTo(FLOAT_QUANTITY));
    assertTrue(BYTE_QUANTITY.canWidenTo(DOUBLE_QUANTITY));
    assertTrue(BYTE_QUANTITY.canWidenTo(DECIMAL_QUANTITY));
  }

  /**
   * Verifies that canWiden always returns the correct value for ShortQuantity as the source type.
   */
  @Test
  public void canWidenReturnsCorrectResultForShortQuantity() {
    assertFalse(SHORT_QUANTITY.canWidenTo(BYTE_QUANTITY));
    assertFalse(SHORT_QUANTITY.canWidenTo(SHORT_QUANTITY));
    assertTrue(SHORT_QUANTITY.canWidenTo(INTEGER_QUANTITY));
    assertTrue(SHORT_QUANTITY.canWidenTo(LONG_QUANTITY));
    assertTrue(SHORT_QUANTITY.canWidenTo(BIG_INTEGER_QUANTITY));
    assertTrue(SHORT_QUANTITY.canWidenTo(FLOAT_QUANTITY));
    assertTrue(SHORT_QUANTITY.canWidenTo(DOUBLE_QUANTITY));
    assertTrue(SHORT_QUANTITY.canWidenTo(DECIMAL_QUANTITY));
  }

  /**
   * Verifies that canWiden always returns the correct value for IntegerQuantity as the source type.
   */
  @Test
  public void canWidenReturnsCorrectResultForIntegerQuantity() {
    assertFalse(INTEGER_QUANTITY.canWidenTo(BYTE_QUANTITY));
    assertFalse(INTEGER_QUANTITY.canWidenTo(SHORT_QUANTITY));
    assertFalse(INTEGER_QUANTITY.canWidenTo(INTEGER_QUANTITY));
    assertTrue(INTEGER_QUANTITY.canWidenTo(LONG_QUANTITY));
    assertTrue(INTEGER_QUANTITY.canWidenTo(BIG_INTEGER_QUANTITY));
    assertTrue(INTEGER_QUANTITY.canWidenTo(FLOAT_QUANTITY));
    assertTrue(INTEGER_QUANTITY.canWidenTo(DOUBLE_QUANTITY));
    assertTrue(INTEGER_QUANTITY.canWidenTo(DECIMAL_QUANTITY));
  }

  /**
   * Verifies that canWiden always returns the correct value for LongQuantity as the source type.
   */
  @Test
  public void canWidenReturnsCorrectResultForLongQuantity() {
    assertFalse(LONG_QUANTITY.canWidenTo(BYTE_QUANTITY));
    assertFalse(LONG_QUANTITY.canWidenTo(SHORT_QUANTITY));
    assertFalse(LONG_QUANTITY.canWidenTo(INTEGER_QUANTITY));
    assertFalse(LONG_QUANTITY.canWidenTo(LONG_QUANTITY));
    assertTrue(LONG_QUANTITY.canWidenTo(BIG_INTEGER_QUANTITY));
    assertTrue(LONG_QUANTITY.canWidenTo(FLOAT_QUANTITY));
    assertTrue(LONG_QUANTITY.canWidenTo(DOUBLE_QUANTITY));
    assertTrue(LONG_QUANTITY.canWidenTo(DECIMAL_QUANTITY));
  }

  /**
   * Verifies that canWiden always returns the correct value for BigIntegerQuantity as the source type.
   */
  @Test
  public void canWidenReturnsCorrectResultForBigIntegerQuantity() {
    assertFalse(BIG_INTEGER_QUANTITY.canWidenTo(BYTE_QUANTITY));
    assertFalse(BIG_INTEGER_QUANTITY.canWidenTo(SHORT_QUANTITY));
    assertFalse(BIG_INTEGER_QUANTITY.canWidenTo(INTEGER_QUANTITY));
    assertFalse(BIG_INTEGER_QUANTITY.canWidenTo(LONG_QUANTITY));
    assertFalse(BIG_INTEGER_QUANTITY.canWidenTo(BIG_INTEGER_QUANTITY));
    assertTrue(BIG_INTEGER_QUANTITY.canWidenTo(FLOAT_QUANTITY));
    assertTrue(BIG_INTEGER_QUANTITY.canWidenTo(DOUBLE_QUANTITY));
    assertTrue(BIG_INTEGER_QUANTITY.canWidenTo(DECIMAL_QUANTITY));
  }

  /**
   * Verifies that canWiden always returns the correct value for FloatQuantity as the source type.
   */
  @Test
  public void canWidenReturnsCorrectResultForFloatQuantity() {
    assertFalse(FLOAT_QUANTITY.canWidenTo(BYTE_QUANTITY));
    assertFalse(FLOAT_QUANTITY.canWidenTo(SHORT_QUANTITY));
    assertFalse(FLOAT_QUANTITY.canWidenTo(INTEGER_QUANTITY));
    assertFalse(FLOAT_QUANTITY.canWidenTo(LONG_QUANTITY));
    assertFalse(FLOAT_QUANTITY.canWidenTo(BIG_INTEGER_QUANTITY));
    assertFalse(FLOAT_QUANTITY.canWidenTo(FLOAT_QUANTITY));
    assertTrue(FLOAT_QUANTITY.canWidenTo(DOUBLE_QUANTITY));
    assertTrue(FLOAT_QUANTITY.canWidenTo(DECIMAL_QUANTITY));
  }

  /**
   * Verifies that canWiden always returns the correct value for DoubleQuantity as the source type.
   */
  @Test
  public void canWidenReturnsCorrectResultForDoubleQuantity() {
    assertFalse(DOUBLE_QUANTITY.canWidenTo(BYTE_QUANTITY));
    assertFalse(DOUBLE_QUANTITY.canWidenTo(SHORT_QUANTITY));
    assertFalse(DOUBLE_QUANTITY.canWidenTo(INTEGER_QUANTITY));
    assertFalse(DOUBLE_QUANTITY.canWidenTo(LONG_QUANTITY));
    assertFalse(DOUBLE_QUANTITY.canWidenTo(BIG_INTEGER_QUANTITY));
    assertFalse(DOUBLE_QUANTITY.canWidenTo(FLOAT_QUANTITY));
    assertFalse(DOUBLE_QUANTITY.canWidenTo(DOUBLE_QUANTITY));
    assertTrue(DOUBLE_QUANTITY.canWidenTo(DECIMAL_QUANTITY));
  }

  /**
   * Verifies that canWiden always returns the correct value for DecimalQuantity as the source type.
   */
  @Test
  public void canWidenReturnsCorrectResultForDecimalQuantity() {
    assertFalse(DECIMAL_QUANTITY.canWidenTo(BYTE_QUANTITY));
    assertFalse(DECIMAL_QUANTITY.canWidenTo(SHORT_QUANTITY));
    assertFalse(DECIMAL_QUANTITY.canWidenTo(INTEGER_QUANTITY));
    assertFalse(DECIMAL_QUANTITY.canWidenTo(LONG_QUANTITY));
    assertFalse(DECIMAL_QUANTITY.canWidenTo(BIG_INTEGER_QUANTITY));
    assertFalse(DECIMAL_QUANTITY.canWidenTo(FLOAT_QUANTITY));
    assertFalse(DECIMAL_QUANTITY.canWidenTo(DOUBLE_QUANTITY));
    assertFalse(DECIMAL_QUANTITY.canWidenTo(DECIMAL_QUANTITY));
  }

  /**
   * Verifies that widen always returns the correct value for ByteQuantity as the source type.
   */
  @Test
  public void widenReturnsCorrectResultForByteQuantity() {
    assertEquals(SHORT_QUANTITY, BYTE_QUANTITY.widenTo(SHORT_QUANTITY));
    assertEquals(ShortQuantity.class, BYTE_QUANTITY.widenTo(SHORT_QUANTITY).getClass());
    assertEquals(INTEGER_QUANTITY, BYTE_QUANTITY.widenTo(INTEGER_QUANTITY));
    assertEquals(IntegerQuantity.class, BYTE_QUANTITY.widenTo(INTEGER_QUANTITY).getClass());
    assertEquals(LONG_QUANTITY, BYTE_QUANTITY.widenTo(LONG_QUANTITY));
    assertEquals(LongQuantity.class, BYTE_QUANTITY.widenTo(LONG_QUANTITY).getClass());
    assertEquals(BIG_INTEGER_QUANTITY, BYTE_QUANTITY.widenTo(BIG_INTEGER_QUANTITY));
    assertEquals(BigIntegerQuantity.class, BYTE_QUANTITY.widenTo(BIG_INTEGER_QUANTITY).getClass());
    assertEquals(FLOAT_QUANTITY, BYTE_QUANTITY.widenTo(FLOAT_QUANTITY));
    assertEquals(FloatQuantity.class, BYTE_QUANTITY.widenTo(FLOAT_QUANTITY).getClass());
    assertEquals(DOUBLE_QUANTITY, BYTE_QUANTITY.widenTo(DOUBLE_QUANTITY));
    assertEquals(DoubleQuantity.class, BYTE_QUANTITY.widenTo(DOUBLE_QUANTITY).getClass());
    assertEquals(DECIMAL_QUANTITY, BYTE_QUANTITY.widenTo(DECIMAL_QUANTITY));
    assertEquals(DecimalQuantity.class, BYTE_QUANTITY.widenTo(DECIMAL_QUANTITY).getClass());
  }

  /**
   * Verifies that widen always returns the correct value for ShortQuantity as the source type.
   */
  @Test
  public void widenReturnsCorrectResultForShortQuantity() {
    assertEquals(INTEGER_QUANTITY, SHORT_QUANTITY.widenTo(INTEGER_QUANTITY));
    assertEquals(IntegerQuantity.class, SHORT_QUANTITY.widenTo(INTEGER_QUANTITY).getClass());
    assertEquals(LONG_QUANTITY, SHORT_QUANTITY.widenTo(LONG_QUANTITY));
    assertEquals(LongQuantity.class, SHORT_QUANTITY.widenTo(LONG_QUANTITY).getClass());
    assertEquals(BIG_INTEGER_QUANTITY, SHORT_QUANTITY.widenTo(BIG_INTEGER_QUANTITY));
    assertEquals(BigIntegerQuantity.class, SHORT_QUANTITY.widenTo(BIG_INTEGER_QUANTITY).getClass());
    assertEquals(FLOAT_QUANTITY, SHORT_QUANTITY.widenTo(FLOAT_QUANTITY));
    assertEquals(FloatQuantity.class, SHORT_QUANTITY.widenTo(FLOAT_QUANTITY).getClass());
    assertEquals(DOUBLE_QUANTITY, SHORT_QUANTITY.widenTo(DOUBLE_QUANTITY));
    assertEquals(DoubleQuantity.class, SHORT_QUANTITY.widenTo(DOUBLE_QUANTITY).getClass());
    assertEquals(DECIMAL_QUANTITY, SHORT_QUANTITY.widenTo(DECIMAL_QUANTITY));
    assertEquals(DecimalQuantity.class, SHORT_QUANTITY.widenTo(DECIMAL_QUANTITY).getClass());
  }

  /**
   * Verifies that widen always returns the correct value for IntegerQuantity as the source type.
   */
  @Test
  public void widenReturnsCorrectResultForIntegerQuantity() {
    assertEquals(LONG_QUANTITY, INTEGER_QUANTITY.widenTo(LONG_QUANTITY));
    assertEquals(LongQuantity.class, INTEGER_QUANTITY.widenTo(LONG_QUANTITY).getClass());
    assertEquals(BIG_INTEGER_QUANTITY, INTEGER_QUANTITY.widenTo(BIG_INTEGER_QUANTITY));
    assertEquals(BigIntegerQuantity.class, INTEGER_QUANTITY.widenTo(BIG_INTEGER_QUANTITY).getClass());
    assertEquals(FLOAT_QUANTITY, INTEGER_QUANTITY.widenTo(FLOAT_QUANTITY));
    assertEquals(FloatQuantity.class, INTEGER_QUANTITY.widenTo(FLOAT_QUANTITY).getClass());
    assertEquals(DOUBLE_QUANTITY, INTEGER_QUANTITY.widenTo(DOUBLE_QUANTITY));
    assertEquals(DoubleQuantity.class, INTEGER_QUANTITY.widenTo(DOUBLE_QUANTITY).getClass());
    assertEquals(DECIMAL_QUANTITY, INTEGER_QUANTITY.widenTo(DECIMAL_QUANTITY));
    assertEquals(DecimalQuantity.class, INTEGER_QUANTITY.widenTo(DECIMAL_QUANTITY).getClass());
  }

  /**
   * Verifies that widen always returns the correct value for LongQuantity as the source type.
   */
  @Test
  public void widenReturnsCorrectResultForLongQuantity() {
    assertEquals(BIG_INTEGER_QUANTITY, LONG_QUANTITY.widenTo(BIG_INTEGER_QUANTITY));
    assertEquals(BigIntegerQuantity.class, LONG_QUANTITY.widenTo(BIG_INTEGER_QUANTITY).getClass());
    assertEquals(FLOAT_QUANTITY, LONG_QUANTITY.widenTo(FLOAT_QUANTITY));
    assertEquals(FloatQuantity.class, LONG_QUANTITY.widenTo(FLOAT_QUANTITY).getClass());
    assertEquals(DOUBLE_QUANTITY, LONG_QUANTITY.widenTo(DOUBLE_QUANTITY));
    assertEquals(DoubleQuantity.class, LONG_QUANTITY.widenTo(DOUBLE_QUANTITY).getClass());
    assertEquals(DECIMAL_QUANTITY, LONG_QUANTITY.widenTo(DECIMAL_QUANTITY));
    assertEquals(DecimalQuantity.class, LONG_QUANTITY.widenTo(DECIMAL_QUANTITY).getClass());
  }

  /**
   * Verifies that widen always returns the correct value for BigIntegerQuantity as the source type.
   */
  @Test
  public void widenReturnsCorrectResultForBigIntegerQuantity() {
    assertEquals(FLOAT_QUANTITY, BIG_INTEGER_QUANTITY.widenTo(FLOAT_QUANTITY));
    assertEquals(FloatQuantity.class, BIG_INTEGER_QUANTITY.widenTo(FLOAT_QUANTITY).getClass());
    assertEquals(DOUBLE_QUANTITY, BIG_INTEGER_QUANTITY.widenTo(DOUBLE_QUANTITY));
    assertEquals(DoubleQuantity.class, BIG_INTEGER_QUANTITY.widenTo(DOUBLE_QUANTITY).getClass());
    assertEquals(DECIMAL_QUANTITY, BIG_INTEGER_QUANTITY.widenTo(DECIMAL_QUANTITY));
    assertEquals(DecimalQuantity.class, BIG_INTEGER_QUANTITY.widenTo(DECIMAL_QUANTITY).getClass());
  }

  /**
   * Verifies that widen always returns the correct value for FloatQuantity as the source type.
   */
  @Test
  public void widenReturnsCorrectResultForFloatQuantity() {
    assertEquals(DOUBLE_QUANTITY, FLOAT_QUANTITY.widenTo(DOUBLE_QUANTITY));
    assertEquals(DoubleQuantity.class, FLOAT_QUANTITY.widenTo(DOUBLE_QUANTITY).getClass());
    assertEquals(DECIMAL_QUANTITY, FLOAT_QUANTITY.widenTo(DECIMAL_QUANTITY));
    assertEquals(DecimalQuantity.class, FLOAT_QUANTITY.widenTo(DECIMAL_QUANTITY).getClass());
  }

  /**
   * Verifies that widen always returns the correct value for DoubleQuantity as the source type.
   */
  @Test
  public void widenReturnsCorrectResultForDoubleQuantity() {
    assertEquals(DECIMAL_QUANTITY, DOUBLE_QUANTITY.widenTo(DECIMAL_QUANTITY));
    assertEquals(DecimalQuantity.class, DOUBLE_QUANTITY.widenTo(DECIMAL_QUANTITY).getClass());
  }

  /**
   * Verifies that addition is performed in the system unit. E.g. addition of two temperatures in degrees Celsius is done in Kelvin and converted back
   * to Celsius.
   */
  @Test
  public void additionIsPerformedInSystemUnit() {
    DoubleQuantity<Temperature> expected = new DoubleQuantity<Temperature>(273.15d, Units.CELSIUS);
    assertEquals(expected, ZERO_DEGREES_CELSIUS.add(ZERO_DEGREES_CELSIUS));
  }
}
