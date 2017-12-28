/*
 * Next Generation Units of Measurement Implementation
 * Copyright (c) 2005-2017, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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

/**
 * Provides support for <a href="http://en.wikipedia.org/wiki/Dimensional_analysis">Dimensional Analysis</a>.
 * 
 * <p>
 * The difference between dimensional models lies in the assumptions each makes and, in consequence,the operations each permits. For example, the
 * summation of a {@link javax.measure.quantity.Length length} and a {@link javax.measure.quantity.Time time} is not allowed by the standard model,
 * but is quite valid in a relativistic context.
 * </p>
 * 
 * <p>
 * The names and characteristics of the models are presented in the following table:
 * </p>
 * 
 * <table border="1" cellspacing="1">
 * <tr align="center" valign="bottom">
 * <th>Model</th>
 * <th>Class</th>
 * <th>Defining Characteristics</th>
 * <th>DefaultQuantityFactory Output CommonUnits</th>
 * </tr>
 * 
 * <tr align="left" valign="middle">
 * <td align="left">Standard</td>
 * <td align="left"><samp>"StandardModel"</samp></td>
 * <td align="left">per Syst&egrave;me Internationale</td>
 * <td align="left"><samp>Length</samp>:&nbsp;<i>m</i>;&nbsp;
 * 
 * <samp>Mass</samp>:&nbsp;<i>kg</i>;&nbsp; <samp>Duration</samp>:&nbsp;<i>s</i>;&nbsp; <samp>ElectricCurrent</samp>:&nbsp;<i>A</i>;&nbsp;
 * 
 * <samp>Temperature</samp>:&nbsp;<i>K</i>;&nbsp; <samp>AmountOfSubstance</samp>:&nbsp;<i>mol</i>;&nbsp;
 * <samp>LuminousIntensity</samp>:&nbsp;<i>cd</i>
 * 
 * </td>
 * </tr>
 * 
 * <tr align="left" valign="middle">
 * <td align="left">Relativistic</td>
 * <td align="left"><samp>"RelativisticModel"</samp></td>
 * <td align="left">1 <i>= c</i></td>
 * <td align="left"><samp>Length</samp>, <samp>Duration</samp>:&nbsp;<i>s</i>;&nbsp; <samp>Mass</samp>:&nbsp;<i>eV</i>;&nbsp;
 * <samp>ElectricCurrent</samp>:&nbsp;<i>A</i>;&nbsp;
 * 
 * <samp>Temperature</samp>:&nbsp;<i>K</i>;&nbsp; <samp>AmountOfSubstance</samp>:&nbsp;<i>mol</i>;&nbsp;
 * <samp>LuminousIntensity</samp>:&nbsp;<i>cd</i>
 * 
 * </td>
 * </tr>
 * 
 * <tr align="left" valign="middle">
 * <td align="left">High-Energy</td>
 * <td align="left"><samp>"HighEnergyModel"</samp></td>
 * <td align="left">1<i> = c<br>
 * &nbsp;&nbsp; = k<br>
 * &nbsp;&nbsp; = ePlus</i></td>
 * <td align="left"><samp>Length</samp>, <samp>Duration</samp>:&nbsp;<i>ns</i>;&nbsp; <samp>Mass</samp>,
 * <samp>Temperature</samp>:&nbsp;<i>GeV</i>;&nbsp;
 * 
 * <samp>ElectricCurrent</samp>:&nbsp;<i>1/ns</i>;&nbsp; <samp>AmountOfSubstance</samp>:&nbsp;<i>mol</i>;&nbsp;
 * <samp>LuminousIntensity</samp>:&nbsp;<i>cd</i></td>
 * </tr>
 * 
 * <tr align="left" valign="middle">
 * <td align="left">Quantum</td>
 * <td align="left"><samp>"QuantumModel"</samp></td>
 * <td align="left">1<i> = c<br>
 * &nbsp;&nbsp; = k<br>
 * &nbsp;&nbsp; = µ0<br>
 * &nbsp;&nbsp; = hBar</i></td>
 * <td align="left"><samp>Length</samp>, <samp>Duration</samp>:&nbsp;<i>1/GeV</i>;&nbsp; <samp>Mass</samp>, <samp>Temperature</samp>,
 * <samp>ElectricCurrent</samp>:&nbsp;<i>GeV</i>;&nbsp;
 * 
 * <samp>AmountOfSubstance</samp>:&nbsp;<i>mol</i>;&nbsp; <samp>LuminousIntensity</samp>:&nbsp;<i>cd</i></td>
 * </tr>
 * 
 * <tr align="left" valign="middle">
 * <td align="left">Natural</td>
 * <td align="left"><samp>"NaturalModel"</samp></td>
 * <td align="left">1<i> = c<br>
 * &nbsp;&nbsp; = k<br>
 * &nbsp;&nbsp; = µ0<br>
 * &nbsp;&nbsp; = hBar<br>
 * &nbsp;&nbsp; = G</i></td>
 * <td align="left"><samp>Length</samp>, <samp>Mass</samp>, <samp>Duration</samp>, <samp>ElectricCurrent</samp>,
 * <samp>Temperature</samp>:&nbsp;1;&nbsp; <samp>AmountOfSubstance</samp>:&nbsp;<i>mol</i>;&nbsp; <samp>LuminousIntensity</samp>:&nbsp;<i>cd</i></td>
 * </tr>
 * </table>
 * This class represents the standard model.
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 1.0, December 28, 2017
 * @since 1.0
 */
class StandardModel extends DimensionalModel {

  /**
   * StandardModel constructor.
   */
  public StandardModel() {
  }

}