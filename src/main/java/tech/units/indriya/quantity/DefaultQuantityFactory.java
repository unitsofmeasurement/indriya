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

import static tech.units.indriya.unit.Units.AMPERE;
import static tech.units.indriya.unit.Units.BECQUEREL;
import static tech.units.indriya.unit.Units.CANDELA;
import static tech.units.indriya.unit.Units.COULOMB;
import static tech.units.indriya.unit.Units.CUBIC_METRE;
import static tech.units.indriya.unit.Units.FARAD;
import static tech.units.indriya.unit.Units.GRAY;
import static tech.units.indriya.unit.Units.HENRY;
import static tech.units.indriya.unit.Units.HERTZ;
import static tech.units.indriya.unit.Units.JOULE;
import static tech.units.indriya.unit.Units.KATAL;
import static tech.units.indriya.unit.Units.KELVIN;
import static tech.units.indriya.unit.Units.KILOGRAM;
import static tech.units.indriya.unit.Units.LUMEN;
import static tech.units.indriya.unit.Units.LUX;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.METRE_PER_SECOND;
import static tech.units.indriya.unit.Units.METRE_PER_SQUARE_SECOND;
import static tech.units.indriya.unit.Units.MOLE;
import static tech.units.indriya.unit.Units.NEWTON;
import static tech.units.indriya.unit.Units.OHM;
import static tech.units.indriya.unit.Units.PASCAL;
import static tech.units.indriya.unit.Units.RADIAN;
import static tech.units.indriya.unit.Units.SECOND;
import static tech.units.indriya.unit.Units.SIEMENS;
import static tech.units.indriya.unit.Units.SIEVERT;
import static tech.units.indriya.unit.Units.SQUARE_METRE;
import static tech.units.indriya.unit.Units.STERADIAN;
import static tech.units.indriya.unit.Units.TESLA;
import static tech.units.indriya.unit.Units.VOLT;
import static tech.units.indriya.unit.Units.WATT;
import static tech.units.indriya.unit.Units.WEBER;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Acceleration;
import javax.measure.quantity.AmountOfSubstance;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Area;
import javax.measure.quantity.CatalyticActivity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.ElectricCapacitance;
import javax.measure.quantity.ElectricCharge;
import javax.measure.quantity.ElectricConductance;
import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.ElectricInductance;
import javax.measure.quantity.ElectricPotential;
import javax.measure.quantity.ElectricResistance;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Force;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Illuminance;
import javax.measure.quantity.Length;
import javax.measure.quantity.LuminousFlux;
import javax.measure.quantity.LuminousIntensity;
import javax.measure.quantity.MagneticFlux;
import javax.measure.quantity.MagneticFluxDensity;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Power;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.RadiationDoseAbsorbed;
import javax.measure.quantity.RadiationDoseEffective;
import javax.measure.quantity.Radioactivity;
import javax.measure.quantity.SolidAngle;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Time;
import javax.measure.quantity.Volume;
import javax.measure.spi.QuantityFactory;

import tech.units.indriya.AbstractUnit;

/**
 * A factory producing simple quantities instances (tuples {@link Number}/ {@link Unit}).<br>
 *
 * For example:<br>
 * <code>
 *      Mass m = DefaultQuantityFactory.getInstance(Mass.class).create(23.0, KILOGRAM); // 23.0 kg<br>
 *      Time m = DefaultQuantityFactory.getInstance(Time.class).create(124, MILLI(SECOND)); // 124 ms
 * </code>
 * 
 * @param <Q>
 *          The type of the quantity.
 *
 * @author <a href="mailto:martin.desruisseaux@geomatys.com">Martin Desruisseaux</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:otaviojava@java.net">Otavio Santana</a>
 * @version 1.2.1, $Date: 2018-07-03 $
 * @since 1.0
 */
public class DefaultQuantityFactory<Q extends Quantity<Q>> implements QuantityFactory<Q> {
  @SuppressWarnings("rawtypes")
  static final Map<Class, QuantityFactory> INSTANCES = new HashMap<>();

  static final Logger logger = Logger.getLogger(DefaultQuantityFactory.class.getName());

  static final Level LOG_LEVEL = Level.FINE;

  /**
   * The type of the quantities created by this factory.
   */
  private final Class<Q> type;

  /**
   * The system unit for quantities created by this factory.
   */
  private final Unit<Q> systemUnit;

  @SuppressWarnings("rawtypes")
  private static final Map<Class, Unit> CLASS_TO_SYSTEM_UNIT = new ConcurrentHashMap<>();

  static {
    CLASS_TO_SYSTEM_UNIT.put(Dimensionless.class, AbstractUnit.ONE);
    CLASS_TO_SYSTEM_UNIT.put(ElectricCurrent.class, AMPERE);
    CLASS_TO_SYSTEM_UNIT.put(LuminousIntensity.class, CANDELA);
    CLASS_TO_SYSTEM_UNIT.put(Temperature.class, KELVIN);
    CLASS_TO_SYSTEM_UNIT.put(Mass.class, KILOGRAM);
    CLASS_TO_SYSTEM_UNIT.put(Length.class, METRE);
    CLASS_TO_SYSTEM_UNIT.put(AmountOfSubstance.class, MOLE);
    CLASS_TO_SYSTEM_UNIT.put(Time.class, SECOND);
    CLASS_TO_SYSTEM_UNIT.put(Angle.class, RADIAN);
    CLASS_TO_SYSTEM_UNIT.put(SolidAngle.class, STERADIAN);
    CLASS_TO_SYSTEM_UNIT.put(Frequency.class, HERTZ);
    CLASS_TO_SYSTEM_UNIT.put(Force.class, NEWTON);
    CLASS_TO_SYSTEM_UNIT.put(Pressure.class, PASCAL);
    CLASS_TO_SYSTEM_UNIT.put(Energy.class, JOULE);
    CLASS_TO_SYSTEM_UNIT.put(Power.class, WATT);
    CLASS_TO_SYSTEM_UNIT.put(ElectricCharge.class, COULOMB);
    CLASS_TO_SYSTEM_UNIT.put(ElectricPotential.class, VOLT);
    CLASS_TO_SYSTEM_UNIT.put(ElectricCapacitance.class, FARAD);
    CLASS_TO_SYSTEM_UNIT.put(ElectricResistance.class, OHM);
    CLASS_TO_SYSTEM_UNIT.put(ElectricConductance.class, SIEMENS);
    CLASS_TO_SYSTEM_UNIT.put(MagneticFlux.class, WEBER);
    CLASS_TO_SYSTEM_UNIT.put(MagneticFluxDensity.class, TESLA);
    CLASS_TO_SYSTEM_UNIT.put(ElectricInductance.class, HENRY);
    CLASS_TO_SYSTEM_UNIT.put(LuminousFlux.class, LUMEN);
    CLASS_TO_SYSTEM_UNIT.put(Illuminance.class, LUX);
    CLASS_TO_SYSTEM_UNIT.put(Radioactivity.class, BECQUEREL);
    CLASS_TO_SYSTEM_UNIT.put(RadiationDoseAbsorbed.class, GRAY);
    CLASS_TO_SYSTEM_UNIT.put(RadiationDoseEffective.class, SIEVERT);
    CLASS_TO_SYSTEM_UNIT.put(CatalyticActivity.class, KATAL);
    CLASS_TO_SYSTEM_UNIT.put(Speed.class, METRE_PER_SECOND);
    CLASS_TO_SYSTEM_UNIT.put(Acceleration.class, METRE_PER_SQUARE_SECOND);
    CLASS_TO_SYSTEM_UNIT.put(Area.class, SQUARE_METRE);
    CLASS_TO_SYSTEM_UNIT.put(Volume.class, CUBIC_METRE);
  }

  @SuppressWarnings("unchecked")
  private DefaultQuantityFactory(Class<Q> quantity) {
    type = quantity;
    systemUnit = CLASS_TO_SYSTEM_UNIT.get(type);
  }

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
  public static <Q extends Quantity<Q>> QuantityFactory<Q> getInstance(final Class<Q> type) {
    logger.log(LOG_LEVEL, "Type: " + type + ": " + type.isInterface());
    QuantityFactory<Q> factory;
    factory = INSTANCES.get(type);
    if (factory != null) {
      return factory;
    }
    if (!Quantity.class.isAssignableFrom(type)) {
      // This exception is not documented because it should never
      // happen if the
      // user don't try to trick the Java generic types system with
      // unsafe cast.
      throw new ClassCastException();
    }
    factory = new DefaultQuantityFactory<Q>(type);
    INSTANCES.put(type, factory);
    return factory;
  }

  public String toString() {
    return "tech.units.indriya.DefaultQuantityFactory <" + type.getName() + '>';
  }

  public boolean equals(Object obj) {
    if (DefaultQuantityFactory.class.isInstance(obj)) {
      @SuppressWarnings("rawtypes")
      DefaultQuantityFactory other = DefaultQuantityFactory.class.cast(obj);
      return Objects.equals(type, other.type);
    }
    return false;
  }

  public int hashCode() {
    return type.hashCode();
  }

  public Quantity<Q> create(Number value, Unit<Q> unit) {
    return Quantities.getQuantity(value, unit);
  }

  public Unit<Q> getSystemUnit() {
    return systemUnit;
  }
}
