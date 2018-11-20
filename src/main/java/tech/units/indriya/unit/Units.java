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
package tech.units.indriya.unit;

import tech.units.indriya.AbstractSystemOfUnits;
import tech.units.indriya.AbstractUnit;
import tech.units.indriya.function.AddConverter;
import tech.units.indriya.function.RationalConverter;
import tech.units.indriya.quantity.QuantityDimension;
import static tech.units.indriya.AbstractUnit.ONE;

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
import javax.measure.spi.SystemOfUnits;

/**
 * <p>
 * This class defines commonly used units.
 *
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 1.3.1, November 20, 2018
 * @since 1.0
 */
public class Units extends AbstractSystemOfUnits {

  protected Units() {
  }

  private static final Units INSTANCE = new Units();

  public String getName() {
    return Units.class.getSimpleName();
  }

  // //////////////
  // BASE UNITS //
  // //////////////

  /**
   * The SI base unit for electric current quantities (standard name <code>A</code>). The Ampere is that constant current which, if maintained in two
   * straight parallel conductors of infinite length, of negligible circular cross-section, and placed 1 meter apart in vacuum, would produce between
   * these conductors a force equal to 2 * 10-7 newton per meter of length. It is named after the French physicist Andre Ampere (1775-1836).
   * 
   * @implNote SI Base Unit
   */
  public static final Unit<ElectricCurrent> AMPERE = addUnit(new BaseUnit<ElectricCurrent>("A", QuantityDimension.ELECTRIC_CURRENT),
      ElectricCurrent.class);

  /**
   * The SI base unit for luminous intensity quantities (standard name <code>cd</code>). The candela is the luminous intensity, in a given direction,
   * of a source that emits monochromatic radiation of frequency 540 * 1012 hertz and that has a radiant intensity in that direction of 1/683 watt per
   * steradian
   * 
   * @see <a href="http://en.wikipedia.org/wiki/Candela"> Wikipedia: Candela</a>
   *
   * @implNote SI Base Unit
   */
  public static final Unit<LuminousIntensity> CANDELA = addUnit(new BaseUnit<LuminousIntensity>("cd", QuantityDimension.LUMINOUS_INTENSITY),
      LuminousIntensity.class);

  /**
   * The SI base unit for thermodynamic temperature quantities (standard name <code>K</code>). It is defined by the <a href="https://en.wikipedia.org/wiki/Boltzmann_constant">Boltzmann constant</a> <code>k</code> having a value of <code>1.3806505×10−23 J/K</code><br>
   * Before the <a href="https://en.wikipedia.org/wiki/2019_redefinition_of_SI_base_units">2019 redefinition of SI base units</a>, the kelvin was the 1/273.16th of the thermodynamic
   * temperature of the triple point of water. It is named after the Scottish mathematician and physicist William Thomson 1st Lord Kelvin (1824-1907)
   * 
   * @see #JOULE
   * @implNote SI Base Unit
   */
  public static final Unit<Temperature> KELVIN = addUnit(new BaseUnit<Temperature>("K", QuantityDimension.TEMPERATURE), Temperature.class);

  /**
   * The SI base unit for mass quantities (standard name <code>kg</code>). It is the only SI unit with a prefix as part of its name and symbol.
   * The kilogram is defined by taking the fixed numerical value of the <a href="https://en.wikipedia.org/wiki/Planck_constant">Planck constant</a> <code>h</code> to be <code>6.62607015×10−34</code> when expressed in the unit <code>J⋅s</code>, which is equal to <code>kg⋅m2⋅s−1</code>, where the <code>metre</code> and the <code>second</code> are defined in terms of <code>c</code> and <code>ΔνCs</code>.<br>
   * Before the <a href="https://en.wikipedia.org/wiki/2019_redefinition_of_SI_base_units">2019 redefinition of SI base units</a>, the kilogram was equal to the mass of an international prototype in the form of a platinum-iridium cylinder kept at Sevres in France.
   * 
   * @see <a href="https://en.wikipedia.org/wiki/Kilogram">Wikipedia: Kilogram</a>
   * @see #GRAM
   * @see #METRE
   * @see #SECOND
   *
   * @implNote SI Base Unit
   */
  public static final Unit<Mass> KILOGRAM = addUnit(new BaseUnit<Mass>("kg", QuantityDimension.MASS), Mass.class);

  /**
   * The SI base unit for length quantities (standard name <code>m</code>). One metre was redefined in 1983 as the distance traveled by light in a
   * vacuum in 1/299,792,458 of a second.
   * 
   * @implNote SI Base Unit
   */
  public static final Unit<Length> METRE = addUnit(new BaseUnit<>("m", QuantityDimension.LENGTH), Length.class);

  /**
   * The SI base unit for amount of substance quantities (standard name <code>mol</code>). The mole is the amount of substance of a system which
   * contains as many elementary entities as there are atoms in 0.012 kilogram of carbon 12.
   * 
   * @implNote SI Base Unit
   */
  public static final Unit<AmountOfSubstance> MOLE = addUnit(new BaseUnit<>("mol", QuantityDimension.AMOUNT_OF_SUBSTANCE), AmountOfSubstance.class);

  /**
   * The SI base unit for duration quantities (standard name <code>s</code>). It is defined as the duration of 9,192,631,770 cycles of radiation
   * corresponding to the transition between two hyperfine levels of the ground state of cesium (1967 Standard).
   * 
   * @implNote SI Base Unit
   */
  public static final Unit<Time> SECOND = addUnit(new BaseUnit<>("s", QuantityDimension.TIME), Time.class);

  // //////////////////////////////
  // SI DERIVED ALTERNATE UNITS //
  // //////////////////////////////

  /**
   * The SI derived unit for mass quantities (standard name <code>g</code>). The base unit for mass quantity is {@link #KILOGRAM}.
   */
  public static final Unit<Mass> GRAM = addUnit(KILOGRAM.divide(1000));
  // = new TransformedUnit(KILOGRAM, MetricPrefix.KILO.getConverter());

  /**
   * The SI unit for plane angle quantities (standard name <code>rad</code>). One radian is the angle between two radii of a circle such that the
   * length of the arc between them is equal to the radius.
   */
  public static final Unit<Angle> RADIAN = addUnit(new AlternateUnit<>(ONE, "rad"), Angle.class);

  /**
   * The SI unit for solid angle quantities (standard name <code>sr</code>). One steradian is the solid angle subtended at the center of a sphere by
   * an area on the surface of the sphere that is equal to the radius squared. The total solid angle of a sphere is 4*Pi steradians.
   */
  public static final Unit<SolidAngle> STERADIAN = addUnit(new AlternateUnit<SolidAngle>(ONE, "sr"), SolidAngle.class);

  /**
   * The SI unit for frequency (standard name <code>Hz</code>). A unit of frequency equal to one cycle per second. After Heinrich Rudolf Hertz
   * (1857-1894), German physicist who was the first to produce radio waves artificially.
   */
  public static final Unit<Frequency> HERTZ = addUnit(new AlternateUnit<Frequency>(ONE.divide(SECOND), "Hz"), Frequency.class);

  /**
   * The SI unit for force (standard name <code>N</code>). One newton is the force required to give a mass of 1 kilogram an Force of 1 metre per
   * second per second. It is named after the English mathematician and physicist Sir Isaac Newton (1642-1727).
   */
  public static final Unit<Force> NEWTON = addUnit(new AlternateUnit<Force>(METRE.multiply(KILOGRAM).divide(SECOND.pow(2)), "N"), Force.class);

  /**
   * The SI unit for pressure, stress (standard name <code>Pa</code>). One pascal is equal to one newton per square meter. It is named after the
   * French philosopher and mathematician Blaise Pascal (1623-1662).
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static final Unit<Pressure> PASCAL = addUnit(new AlternateUnit(NEWTON.divide(METRE.pow(2)), "Pa"), Pressure.class);

  /**
   * The SI unit for energy, work, quantity of heat (<code>J</code>). One joule is the amount of work done when an applied force of 1 newton moves
   * through a distance of 1 metre in the direction of the force. It is named after the English physicist James Prescott Joule (1818-1889).
   */
  public static final Unit<Energy> JOULE = addUnit(new AlternateUnit<Energy>(NEWTON.multiply(METRE), "J"), Energy.class);

  /**
   * The SI unit for power, radiant, flux (standard name <code>W</code>). One watt is equal to one joule per second. It is named after the British
   * scientist James Watt (1736-1819).
   */
  public static final Unit<Power> WATT = addUnit(new AlternateUnit<Power>(JOULE.divide(SECOND), "W"), Power.class);

  /**
   * The SI unit for electric charge, quantity of electricity (standard name <code>C</code>). One Coulomb is equal to the quantity of charge
   * transferred in one second by a steady current of one ampere. It is named after the French physicist Charles Augustin de Coulomb (1736-1806).
   */
  public static final Unit<ElectricCharge> COULOMB = addUnit(new AlternateUnit<ElectricCharge>(SECOND.multiply(AMPERE), "C"), ElectricCharge.class);

  /**
   * The SI unit for electric potential difference, electromotive force (standard name <code>V</code>). One Volt is equal to the difference of
   * electric potential between two points on a conducting wire carrying a constant current of one ampere when the power dissipated between the points
   * is one watt. It is named after the Italian physicist Count Alessandro Volta (1745-1827).
   */
  public static final Unit<ElectricPotential> VOLT = addUnit(new AlternateUnit<ElectricPotential>(WATT.divide(AMPERE), "V"), ElectricPotential.class);

  /**
   * The SI unit for capacitance (standard name <code>F</code>). One Farad is equal to the capacitance of a capacitor having an equal and opposite
   * charge of 1 coulomb on each plate and a potential difference of 1 volt between the plates. It is named after the British physicist and chemist
   * Michael Faraday (1791-1867).
   */
  public static final Unit<ElectricCapacitance> FARAD = addUnit(new AlternateUnit<ElectricCapacitance>(COULOMB.divide(VOLT), "F"),
      ElectricCapacitance.class);

  /**
   * The SI unit for electric resistance (standard name <code>Ohm</code>). One Ohm is equal to the resistance of a conductor in which a current of one
   * ampere is produced by a potential of one volt across its terminals. It is named after the German physicist Georg Simon Ohm (1789-1854).
   */
  public static final Unit<ElectricResistance> OHM = addUnit(new AlternateUnit<ElectricResistance>(VOLT.divide(AMPERE), "Ω"),
      ElectricResistance.class);

  /**
   * The SI unit for electric conductance (standard name <code>S</code>). One Siemens is equal to one ampere per volt. It is named after the German
   * engineer Ernst Werner von Siemens (1816-1892).
   */
  public static final Unit<ElectricConductance> SIEMENS = addUnit(new AlternateUnit<ElectricConductance>(AMPERE.divide(VOLT), "S"),
      ElectricConductance.class);

  /**
   * The SI unit for magnetic flux (standard name <code>Wb</code>). One Weber is equal to the magnetic flux that in linking a circuit of one turn
   * produces in it an electromotive force of one volt as it is uniformly reduced to zero within one second. It is named after the German physicist
   * Wilhelm Eduard Weber (1804-1891).
   */
  public static final Unit<MagneticFlux> WEBER = addUnit(new AlternateUnit<MagneticFlux>(VOLT.multiply(SECOND), "Wb"), MagneticFlux.class);

  /**
   * The alternate unit for magnetic flux density (standard name <code>T</code>). One Tesla is equal equal to one weber per square metre. It is named
   * after the Serbian-born American electrical engineer and physicist Nikola Tesla (1856-1943).
   */
  public static final Unit<MagneticFluxDensity> TESLA = addUnit(new AlternateUnit<MagneticFluxDensity>(WEBER.divide(METRE.pow(2)), "T"),
      MagneticFluxDensity.class);

  /**
   * The alternate unit for inductance (standard name <code>H</code>). One Henry is equal to the inductance for which an induced electromotive force
   * of one volt is produced when the current is varied at the rate of one ampere per second. It is named after the American physicist Joseph Henry
   * (1791-1878).
   */
  public static final Unit<ElectricInductance> HENRY = addUnit(new AlternateUnit<ElectricInductance>(WEBER.divide(AMPERE), "H"),
      ElectricInductance.class);

  /**
   * The SI unit for Celsius temperature (standard name <code>Cel</code>). This is a unit of temperature such as the freezing point of water (at one
   * atmosphere of pressure) is 0 Cel, while the boiling point is 100 Cel.
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static final Unit<Temperature> CELSIUS = addUnit(new TransformedUnit(KELVIN, new AddConverter(273.15)));
  // Not mapping to Temperature since temperature is mapped to Kelvin.

  /**
   * The SI unit for luminous flux (standard name <code>lm</code>). One Lumen is equal to the amount of light given out through a solid angle by a
   * source of one candela intensity radiating equally in all directions.
   */
  public static final Unit<LuminousFlux> LUMEN = addUnit(new AlternateUnit<LuminousFlux>(CANDELA.multiply(STERADIAN), "lm"), LuminousFlux.class);

  /**
   * The SI unit for illuminance (standard name <code>lx</code>). One Lux is equal to one lumen per square metre.
   */
  public static final Unit<Illuminance> LUX = addUnit(new AlternateUnit<Illuminance>(LUMEN.divide(METRE.pow(2)), "lx"), Illuminance.class);

  /**
   * The SI unit for activity of a radionuclide (standard name <code>Bq</code> ). One becquerel is the radiation caused by one disintegration per
   * second. It is named after the French physicist, Antoine-Henri Becquerel (1852-1908).
   */
  public static final Unit<Radioactivity> BECQUEREL = addUnit(new AlternateUnit<Radioactivity>(ONE.divide(SECOND), "Bq"), Radioactivity.class);

  /**
   * The SI unit for absorbed dose, specific energy (imparted), kerma (standard name <code>Gy</code>). One gray is equal to the dose of one joule of
   * energy absorbed per one kilogram of matter. It is named after the British physician L. H. Gray (1905-1965).
   */
  public static final Unit<RadiationDoseAbsorbed> GRAY = addUnit(new AlternateUnit<RadiationDoseAbsorbed>(JOULE.divide(KILOGRAM), "Gy"),
      RadiationDoseAbsorbed.class);

  /**
   * The SI unit for dose equivalent (standard name <code>Sv</code>). One Sievert is equal is equal to the actual dose, in grays, multiplied by a
   * "quality factor" which is larger for more dangerous forms of radiation. It is named after the Swedish physicist Rolf Sievert (1898-1966).
   */
  public static final Unit<RadiationDoseEffective> SIEVERT = addUnit(new AlternateUnit<RadiationDoseEffective>(JOULE.divide(KILOGRAM), "Sv"),
      RadiationDoseEffective.class);

  /**
   * The SI unit for catalytic activity (standard name <code>kat</code>).
   */
  public static final Unit<CatalyticActivity> KATAL = addUnit(new AlternateUnit<CatalyticActivity>(MOLE.divide(SECOND), "kat"),
      CatalyticActivity.class);

  // ////////////////////////////
  // SI DERIVED PRODUCT UNITS //
  // ////////////////////////////

  /**
   * The SI unit for velocity quantities (standard name <code>m/s</code>).
   */
  public static final Unit<Speed> METRE_PER_SECOND = addUnit(new ProductUnit<>(METRE.divide(SECOND)), Speed.class);

  /**
   * The SI unit for acceleration quantities (standard name <code>m/s2</code> ).
   */
  public static final Unit<Acceleration> METRE_PER_SQUARE_SECOND = addUnit(new ProductUnit<>(METRE_PER_SECOND.divide(SECOND)), Acceleration.class);

  /**
   * The SI unit for area quantities (standard name <code>m2</code>).
   */
  public static final Unit<Area> SQUARE_METRE = addUnit(new ProductUnit<>(METRE.multiply(METRE)), Area.class);

  /**
   * The SI unit for volume quantities (standard name <code>m3</code>).
   */
  public static final Unit<Volume> CUBIC_METRE = addUnit(new ProductUnit<Volume>(SQUARE_METRE.multiply(METRE)), Volume.class);

  /**
   * A unit of velocity expressing the number of international {@link #KILOMETRE kilometres} per {@link #HOUR hour} (abbreviation <code>km/h</code>).
   */
  public static final Unit<Speed> KILOMETRE_PER_HOUR = addUnit(METRE_PER_SECOND.multiply(0.277778d)).asType(Speed.class);

  // ///////////////////////////////////////////////////////////////
  // Common Units outside the SI that are accepted for use with the SI. //
  // ///////////////////////////////////////////////////////////////

  /**
   * A dimensionless unit accepted for use with SI units (standard name <code>%</code>).
   */
  public static final Unit<Dimensionless> PERCENT = addUnit(new TransformedUnit<>(ONE, new RationalConverter(1, 100)));

  // ////////
  // Time //
  // ////////
  /**
   * A time unit accepted for use with SI units (standard name <code>min</code>).
   */
  public static final Unit<Time> MINUTE = addUnit(new TransformedUnit<>("min", SECOND, SECOND, new RationalConverter(60, 1)));

  /**
   * A time unit accepted for use with SI units (standard name <code>h</code> ).
   */
  public static final Unit<Time> HOUR = addUnit(new TransformedUnit<>("h", SECOND, SECOND, new RationalConverter(60 * 60, 1)));

  /**
   * A time unit accepted for use with SI units (standard name <code>d</code> ).
   */
  public static final Unit<Time> DAY = addUnit(new TransformedUnit<>("d", SECOND, SECOND, new RationalConverter(24 * 60 * 60, 1)));

  /**
   * A unit of duration equal to 7 {@link #DAY} (standard name <code>week</code>).
   */
  public static final Unit<Time> WEEK = addUnit(DAY.multiply(7));

  /**
   * A time unit accepted for use with SI units (standard name <code>y</code> ).
   */
  public static final Unit<Time> YEAR = addUnit(Units.DAY.multiply(365.2425));

  /**
   * A volume unit accepted for use with SI units (standard name <code>l</code>).
   * 
   * @see <a href="https://en.wikipedia.org/wiki/Litre"> Wikipedia: Litre</a>
   */
  public static final Unit<Volume> LITRE = AbstractSystemOfUnits.Helper.addUnit(INSTANCE.units, new TransformedUnit<Volume>(CUBIC_METRE,
      new RationalConverter(1, 1000)), "Litre", "l");

  /**
   * Returns the unique instance of this class.
   * 
   * @return the Units instance.
   */
  public static SystemOfUnits getInstance() {
    return INSTANCE;
  }

  static {
    // have to add AbstractUnit.ONE as Dimensionless, too
    addUnit(ONE);
    INSTANCE.quantityToUnit.put(Dimensionless.class, ONE);
  }

  /**
   * Adds a new unit not mapped to any specified quantity type.
   *
   * @param unit
   *          the unit being added.
   * @return <code>unit</code>.
   */
  private static <U extends Unit<?>> U addUnit(U unit) {
    INSTANCE.units.add(unit);
    return unit;
  }

  /**
   * Adds a new unit and maps it to the specified quantity type.
   *
   * @param unit
   *          the unit being added.
   * @param type
   *          the quantity type.
   * @return <code>unit</code>.
   */
  private static <U extends AbstractUnit<?>> U addUnit(U unit, Class<? extends Quantity<?>> type) {
    INSTANCE.units.add(unit);
    INSTANCE.quantityToUnit.put(type, unit);
    return unit;
  }
}
