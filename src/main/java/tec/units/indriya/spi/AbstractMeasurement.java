/*
 * Next Generation Units of Measurement Implementation
 * Copyright (c) 2005-2017, Jean-Marie Dautelle, Werner Keil, V2COM.
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
 * 3. Neither the name of JSR-363, Indriya nor the names of their contributors may be used to endorse or promote products
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
package tec.units.indriya.spi;

import java.time.Instant;

import javax.measure.Quantity;

import tec.units.indriya.AbstractUnit;
import tec.units.indriya.ComparableQuantity;

/**
 * <p>
 * This class represents the immutable result of a measurement stated in a known quantity.
 * </p>
 *
 * <p>
 * All instances of this class shall be immutable.
 * </p>
 *
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 0.4 $Date: 2016-10-15 $
 */
abstract class AbstractMeasurement<Q extends Quantity<Q>> implements Measurement<Q>, Comparable<Measurement<Q>> {

  /**
	*
	*/
  private static final long serialVersionUID = 2417644773551236879L;

  private final Quantity<Q> quantity;
  private final Instant instant;

  /**
   * constructor.
   */
  protected AbstractMeasurement(Quantity<Q> q, Instant i) {
    this.quantity = q;
    this.instant = i;
  }

  /**
   * constructor.
   */
  protected AbstractMeasurement(Quantity<Q> q, long t) {
    this(q, Instant.ofEpochMilli(t));
  }

  /**
   * constructor.
   */
  protected AbstractMeasurement(Quantity<Q> q) {
    this(q, System.currentTimeMillis());
  }

  /**
   * Returns the measurement quantity.
   *
   * @return the quantity.
   */
  public Quantity<Q> getQuantity() {
    return quantity;
  }

  /**
   * Returns the measurement instant.
   *
   * @return the instant.
   */
  public final Instant getInstant() {
    return instant;
  }

  /**
   * Returns the measurement timestamp.
   *
   * @return the timestamp.
   */
  public final long getTimestamp() {
    return instant.toEpochMilli();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    AbstractMeasurement<?> that = (AbstractMeasurement<?>) o;

    return quantity.equals(that.quantity) && instant.equals(that.instant);
  }

  @Override
  public int hashCode() {
    int result = quantity.hashCode();
    result = 31 * result + instant.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "Measurement{" + "quantity=" + quantity + ", instant=" + instant + '}';
  }

  /**
   * This class represents the default measurement.
   */
  @SuppressWarnings("rawtypes")
  static final class Default<Q extends Quantity<Q>> extends AbstractMeasurement<Q> {

    /**
		  *
		  */
    private static final long serialVersionUID = 823899472806334856L;

    @SuppressWarnings({ "unchecked" })
    protected Default(Quantity q, Instant i) {
      super(q, i);
    }

    @SuppressWarnings({ "unchecked" })
    protected Default(Quantity q, long t) {
      super(q, t);
    }

    @SuppressWarnings("unchecked")
    protected Default(Quantity q) {
      super(q);
    }

    @SuppressWarnings("unchecked")
    @Override
    public int compareTo(Measurement<Q> m) {
      if (getQuantity().getUnit() instanceof AbstractUnit) {
        return ((AbstractUnit) getQuantity().getUnit()).compareTo(m.getQuantity().getUnit())
            + NumberComparator.getInstance().compare(getQuantity().getValue(), m.getQuantity().getValue()) + getInstant().compareTo(m.getInstant());
      } else {
        // don't compare unit if it's not an AbstractUnit
        return NumberComparator.getInstance().compare(getQuantity().getValue(), m.getQuantity().getValue()) + getInstant().compareTo(m.getInstant());
      }
    }
  }

  /**
   * This class represents the default measurement.
   */
  @SuppressWarnings("rawtypes")
  static final class DefaultComparable<Q extends Quantity<Q>> extends AbstractMeasurement<Q> {

    /**
		 * 
		 */
    private static final long serialVersionUID = -175450754835481596L;

    @SuppressWarnings({ "unchecked" })
    protected DefaultComparable(ComparableQuantity q, Instant i) {
      super(q, i);
    }

    @SuppressWarnings({ "unchecked" })
    protected DefaultComparable(ComparableQuantity q, long t) {
      super(q, t);
    }

    @SuppressWarnings("unchecked")
    protected DefaultComparable(ComparableQuantity q) {
      super(q);
    }

    @Override
    public ComparableQuantity<Q> getQuantity() {
      return (ComparableQuantity<Q>) super.getQuantity();
    }

    @SuppressWarnings("unchecked")
    @Override
    public int compareTo(Measurement<Q> m) {
      if (m instanceof DefaultComparable) {
        DefaultComparable dm = (DefaultComparable) m;
        if (getQuantity() instanceof ComparableQuantity) {
          return getQuantity().compareTo(dm.getQuantity()) + getInstant().compareTo(m.getInstant());
        }
      }
      return 0;
    }
  }
}
