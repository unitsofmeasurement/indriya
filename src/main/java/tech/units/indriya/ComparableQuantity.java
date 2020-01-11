/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2020, Units of Measurement project.
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

import java.io.Serializable;

import javax.measure.Quantity;
import javax.measure.Unit;

import tech.uom.lib.common.function.QuantityConverter;

/**
 * Quantity specialized for the Java SE platform. It extends {@link Quantity} with {@linkplain Comparable} and {@linkplain Serializable }
 * 
 * @see {@link Quantity}
 * @author otaviojava
 * @author werner
 * @param <Q>
 * @version 1.1, July 2, 2019
 * @since 1.0
 */
public interface ComparableQuantity<Q extends Quantity<Q>> extends Quantity<Q>, Comparable<Quantity<Q>>, QuantityConverter<Q>, Serializable {

  /**
   * @see Quantity#add(Quantity)
   */
  ComparableQuantity<Q> add(Quantity<Q> that);

  /**
   * @see Quantity#subtract(Quantity)
   */
  ComparableQuantity<Q> subtract(Quantity<Q> that);

  /**
   * @see Quantity#divide(Quantity)
   */
  ComparableQuantity<?> divide(Quantity<?> that);

  /**
   * @see Quantity#divide(Number)
   */
  ComparableQuantity<Q> divide(Number that);

  /**
   * @see Quantity#multiply(Quantity)
   */
  ComparableQuantity<?> multiply(Quantity<?> multiplier);

  /**
   * @see Quantity#multiply(Number)
   */
  ComparableQuantity<Q> multiply(Number multiplier);

  /**
   * @see Quantity#inverse()
   */
  ComparableQuantity<?> inverse();

  /**
   * invert and already cast to defined quantityClass
   * 
   * @param quantityClass
   *          Quantity to be converted
   * @see Quantity#inverse()
   * @see Quantity#asType(Class)
   */
  <T extends Quantity<T>> ComparableQuantity<T> inverse(Class<T> quantityClass);

  /**
   * @see Quantity#to(Unit)
   */
  ComparableQuantity<Q> to(Unit<Q> unit);

  /**
   * @see Quantity#asType(Class)
   */
  <T extends Quantity<T>> ComparableQuantity<T> asType(Class<T> type) throws ClassCastException;

  /**
   * Compares two instances of {@link Quantity <Q>}. Conversion of unit can happen if necessary
   *
   * @param that
   *          the {@code quantity<Q>} to be compared with this instance.
   * @return {@code true} if {@code that > this}.
   * @throws NullPointerException
   *           if the that is null
   */
  boolean isGreaterThan(Quantity<Q> that);

  /**
   * Compares two instances of {@link Quantity <Q>}, doing the conversion of unit if necessary.
   *
   * @param that
   *          the {@code quantity<Q>} to be compared with this instance.
   * @return {@code true} if {@code that >= this}.
   * @throws NullPointerException
   *           if the that is null
   */
  boolean isGreaterThanOrEqualTo(Quantity<Q> that);

  /**
   * Compares two instances of {@link Quantity <Q>}, doing the conversion of unit if necessary.
   *
   * @param that
   *          the {@code quantity<Q>} to be compared with this instance.
   * @return {@code true} if {@code that < this}.
   * @throws NullPointerException
   *           if the quantity is null
   */
  boolean isLessThan(Quantity<Q> that);

  /**
   * Compares two instances of {@link Quantity <Q>}, doing the conversion of unit if necessary.
   *
   * @param that
   *          the {@code quantity<Q>} to be compared with this instance.
   * @return {@code true} if {@code that < this}.
   * @throws NullPointerException
   *           if the quantity is null
   */
  boolean isLessThanOrEqualTo(Quantity<Q> that);

  /**
   * Compares two instances of {@code Quantity <Q>}, doing the conversion of unit if necessary.
   *
   * @param that
   *          the {@code quantity<Q>} to be compared with this instance.
   * @return {@code true} if {@code that < this}.
   * @throws NullPointerException
   *           if the quantity is null
   *           
   * @see <a href= "https://dictionary.cambridge.org/dictionary/english/equivalent">Cambridge Dictionary: equivalent</a>
   * @see <a href= "https://www.lexico.com/en/definition/equivalent">LEXICO: equivalent</a>       
   */
  boolean isEquivalentTo(Quantity<Q> that);

  /**
   * Multiply and cast the {@link ComparableQuantity}
   * 
   * @param that
   *          quantity to be multiplied
   * @param asTypeQuantity
   *          quantity to be converted
   * @return the QuantityOperations multiplied and converted
   * @see Quantity#divide(Quantity)
   * @see Quantity#asType(Class)
   * @exception NullPointerException
   */
  <T extends Quantity<T>, E extends Quantity<E>> ComparableQuantity<E> divide(Quantity<T> that, Class<E> asTypeQuantity);

  /**
   * Divide and cast the {@link ComparableQuantity}
   * 
   * @param that
   *          quantity to be divided
   * @param asTypeQuantity
   *          quantity to be converted
   * @return the QuantityOperations multiplied and converted
   * @see QuantityOperations
   * @see QuantityOperations#of(Quantity, Class)
   * @see Quantity#asType(Class)
   * @see Quantity#multiply(Quantity)
   * @exception NullPointerException
   */
  <T extends Quantity<T>, E extends Quantity<E>> ComparableQuantity<E> multiply(Quantity<T> that, Class<E> asTypeQuantity);
}
