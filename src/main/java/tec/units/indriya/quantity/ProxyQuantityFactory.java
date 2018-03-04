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
package tec.units.indriya.quantity;

import static tec.units.indriya.unit.Units.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.*;
import javax.measure.spi.QuantityFactory;

import tec.units.indriya.AbstractQuantity;
import tec.units.indriya.AbstractUnit;

/**
 * A factory producing simple quantities instances (tuples {@link Number}/{@link Unit}). This implementation of {@link QuantityFactory} uses the
 * DynamicProxy features of Java reflection API.<br>
 * <br>
 *
 * For example:<br>
 * <code>
 *      Quantity<Mass> m = ProxyQuantityFactory.getInstance(Mass.class).create(23.0, KILOGRAM); // 23.0 kg<br/>
 *      Quantity<Time> t = ProxyQuantityFactory.getInstance(Time.class).create(124, MILLI(SECOND)); // 124 ms
 * </code>
 * 
 * @param <Q>
 *          The type of the quantity.
 *
 * @author <a href="mailto:martin.desruisseaux@geomatys.com">Martin Desruisseaux</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 1.1, $Date: 2018-03-04 $
 * @since 1.0
 */
public abstract class ProxyQuantityFactory<Q extends Quantity<Q>> implements QuantityFactory<Q> {

  /**
   * Holds the current instances.
   */
  @SuppressWarnings("rawtypes")
  private static final Map<Class, ProxyQuantityFactory> INSTANCES = new ConcurrentHashMap<>();

  private static final Logger logger = Logger.getLogger(ProxyQuantityFactory.class.getName());

  private static final Level LOG_LEVEL = Level.FINE;

  /**
   * Returns the default instance for the specified quantity type.
   *
   * @param <Q>
   *          The type of the quantity
   * @param type
   *          the quantity type
   * @return the quantity factory for the specified type
   */
  @SuppressWarnings("unchecked")
  public static <Q extends Quantity<Q>> ProxyQuantityFactory<Q> getInstance(final Class<Q> type) {

    logger.log(LOG_LEVEL, "Type: " + type + ": " + type.isInterface());
    ProxyQuantityFactory<Q> factory;
    if (!type.isInterface()) {
      if (type != null && type.getInterfaces() != null & type.getInterfaces().length > 0) {
        logger.log(LOG_LEVEL, "Type0: " + type.getInterfaces()[0]);
        Class<?> type2 = type.getInterfaces()[0];

        factory = INSTANCES.get(type2);
        if (factory != null)
          return factory;
        if (!AbstractQuantity.class.isAssignableFrom(type2))
          // This exception is not documented because it should never happen if the
          // user don't try to trick the Java generic types system with unsafe cast.
          throw new ClassCastException();
        factory = new Default<>((Class<Q>) type2);
        INSTANCES.put(type2, factory);
      } else {
        factory = INSTANCES.get(type);
        if (factory != null)
          return factory;
        if (!AbstractQuantity.class.isAssignableFrom(type))
          // This exception is not documented because it should never happen if the
          // user don't try to trick the Java generic types system with unsafe cast.
          throw new ClassCastException();
        factory = new Default<>(type);
        INSTANCES.put(type, factory);
      }
    } else {
      factory = INSTANCES.get(type);
      if (factory != null)
        return factory;
      if (!Quantity.class.isAssignableFrom(type))
        // This exception is not documented because it should never happen if the
        // user don't try to trick the Java generic types system with unsafe cast.
        throw new ClassCastException();
      factory = new Default<>(type);
      INSTANCES.put(type, factory);
    }
    return factory;
  }

  /**
   * Overrides the default implementation of the factory for the specified quantity type.
   *
   * @param <Q>
   *          The type of the quantity
   * @param type
   *          the quantity type
   * @param factory
   *          the quantity factory
   */
  protected static <Q extends Quantity<Q>> void setInstance(final Class<Q> type, ProxyQuantityFactory<Q> factory) {
    if (!AbstractQuantity.class.isAssignableFrom(type))
      // This exception is not documented because it should never happen if the
      // user don't try to trick the Java generic types system with unsafe cast.
      throw new ClassCastException();
    INSTANCES.put(type, factory);
  }

  /**
   * Returns the metric unit for quantities produced by this factory or <code>null</code> if unknown.
   *
   * @return the metric units for this factory quantities.
   */
  public abstract Unit<Q> getSystemUnit();

  /**
   * The default factory implementation. This factory uses reflection for providing a default implementation for every {@link AbstractMeasurement}
   * sub-types.
   *
   * @param <Q>
   *          The type of the quantity
   */
  private static final class Default<Q extends Quantity<Q>> extends ProxyQuantityFactory<Q> {

    /**
     * The type of the quantities created by this factory.
     */
    private final Class<Q> type;

    /**
     * The metric unit for quantities created by this factory.
     */
    private final Unit<Q> metricUnit;

    /**
     * Creates a new factory for quantities of the given type.
     *
     * @param type
     *          The type of the quantities created by this factory.
     */
    @SuppressWarnings("unchecked")
    Default(final Class<Q> type) {
      this.type = type;
      metricUnit = CLASS_TO_METRIC_UNIT.get(type);
    }

    @SuppressWarnings("rawtypes")
    static final HashMap<Class, Unit> CLASS_TO_METRIC_UNIT = new HashMap<>();
    static {
      CLASS_TO_METRIC_UNIT.put(Dimensionless.class, AbstractUnit.ONE);
      CLASS_TO_METRIC_UNIT.put(ElectricCurrent.class, AMPERE);
      CLASS_TO_METRIC_UNIT.put(LuminousIntensity.class, CANDELA);
      CLASS_TO_METRIC_UNIT.put(Temperature.class, KELVIN);
      CLASS_TO_METRIC_UNIT.put(Mass.class, KILOGRAM);
      CLASS_TO_METRIC_UNIT.put(Length.class, METRE);
      CLASS_TO_METRIC_UNIT.put(AmountOfSubstance.class, MOLE);
      CLASS_TO_METRIC_UNIT.put(Time.class, SECOND);
      // CLASS_TO_METRIC_UNIT.put(MagnetomotiveForce.class, AMPERE_TURN);
      CLASS_TO_METRIC_UNIT.put(Angle.class, RADIAN);
      CLASS_TO_METRIC_UNIT.put(SolidAngle.class, STERADIAN);
      // CLASS_TO_METRIC_UNIT.put(Information.class, BIT);
      CLASS_TO_METRIC_UNIT.put(Frequency.class, HERTZ);
      CLASS_TO_METRIC_UNIT.put(Force.class, NEWTON);
      CLASS_TO_METRIC_UNIT.put(Pressure.class, PASCAL);
      CLASS_TO_METRIC_UNIT.put(Energy.class, JOULE);
      CLASS_TO_METRIC_UNIT.put(Power.class, WATT);
      CLASS_TO_METRIC_UNIT.put(ElectricCharge.class, COULOMB);
      CLASS_TO_METRIC_UNIT.put(ElectricPotential.class, VOLT);
      CLASS_TO_METRIC_UNIT.put(ElectricCapacitance.class, FARAD);
      CLASS_TO_METRIC_UNIT.put(ElectricResistance.class, OHM);
      CLASS_TO_METRIC_UNIT.put(ElectricConductance.class, SIEMENS);
      CLASS_TO_METRIC_UNIT.put(MagneticFlux.class, WEBER);
      CLASS_TO_METRIC_UNIT.put(MagneticFluxDensity.class, TESLA);
      CLASS_TO_METRIC_UNIT.put(ElectricInductance.class, HENRY);
      CLASS_TO_METRIC_UNIT.put(LuminousFlux.class, LUMEN);
      CLASS_TO_METRIC_UNIT.put(Illuminance.class, LUX);
      CLASS_TO_METRIC_UNIT.put(Radioactivity.class, BECQUEREL);
      CLASS_TO_METRIC_UNIT.put(RadiationDoseAbsorbed.class, GRAY);
      CLASS_TO_METRIC_UNIT.put(RadiationDoseEffective.class, SIEVERT);
      CLASS_TO_METRIC_UNIT.put(CatalyticActivity.class, KATAL);
      CLASS_TO_METRIC_UNIT.put(Speed.class, METRE_PER_SECOND);
      CLASS_TO_METRIC_UNIT.put(Acceleration.class, METRE_PER_SQUARE_SECOND);
      CLASS_TO_METRIC_UNIT.put(Area.class, SQUARE_METRE);
      CLASS_TO_METRIC_UNIT.put(Volume.class, CUBIC_METRE);
    }

    @Override
    public Unit<Q> getSystemUnit() {
      return metricUnit;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Quantity<Q> create(Number value, Unit<Q> unit) {
      // System.out.println("Type: " + type);
      return (Q) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type }, new GenericHandler<>(value, unit));
    }
  }

  /**
   * The method invocation handler for implementation backed by any kind of {@link Number}. This is a fall back used when no specialized handler is
   * available for the number type.
   */
  private static final class GenericHandler<Q extends Quantity<Q>> implements InvocationHandler {
    final Unit<Q> unit;
    final Number value;

    GenericHandler(final Number value, final Unit<Q> unit) {
      this.unit = unit;
      this.value = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) {
      final String name = method.getName();
      switch (name) {
        case "doubleValue": { // Most frequent.
          final Unit<Q> toUnit = (Unit<Q>) args[0];
          if ((toUnit == unit) || (toUnit.equals(unit)))
            return value.doubleValue(); // Returns value directly.
          return unit.getConverterTo(toUnit).convert(value.doubleValue());
        }
        case "longValue": {
          final Unit<Q> toUnit = (Unit<Q>) args[0];
          if ((toUnit == unit) || (toUnit.equals(unit)))
            return value.longValue(); // Returns value directly.
          double doubleValue = unit.getConverterTo(toUnit).convert(value.doubleValue());
          if ((doubleValue < Long.MIN_VALUE) || (doubleValue > Long.MAX_VALUE))
            throw new ArithmeticException("Overflow: " + doubleValue + " cannot be represented as a long");
          return (long) doubleValue;
        }
        case "getValue":
          return value;
        case "getUnit":
          return unit;
        case "toString":
          return String.valueOf(value) + ' ' + unit;
        case "hashCode":
          return value.hashCode() * 31 + unit.hashCode();
        case "equals": {
          final Object obj = args[0];
          if (!(obj instanceof AbstractQuantity))
            return false;
          final AbstractQuantity<Q> that = (AbstractQuantity<Q>) obj;
          return unit.isCompatible((AbstractUnit<?>) that.getUnit()) && value.doubleValue() == (that).doubleValue(unit);
        }
        case "compareTo": {
          final AbstractQuantity<Q> that = (AbstractQuantity<Q>) args[0];
          return Double.compare(value.doubleValue(), that.doubleValue(unit));
        }
        default:
          throw new UnsupportedOperationException(name);
      }
    }
  }
}
