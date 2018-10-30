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
package tech.units.indriya.function;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.measure.UnitConverter;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import tech.units.indriya.AbstractConverter;

class ConverterTypeUtil {

  /**
   * Complete set of built-in unit converters, with their individual identity transformations and 
   * some concrete examples.  
   */
  public static enum ConverterType {

    ID(AbstractConverter.class, 
        ()->AbstractConverter.IDENTITY, // identity as expressed by this type
        AbstractConverter.IDENTITY, // concrete example a
        AbstractConverter.IDENTITY  ), // concrete example b
    POWER(PowerOfIntConverter.class, 
        ()->PowerOfIntConverter.of(1, 0), // identity as expressed by this type ... x -> 1^0 * x
        PowerOfIntConverter.of(3, 7), // concrete example a ... x -> 3^7 * x
        PowerOfIntConverter.of(7, -3)   ), // concrete example b ... x -> 7^-3 * x
    RATIONAL(RationalConverter.class, 
        ()->RationalConverter.of(1, 1), // identity as expressed by this type ... x -> 1/1 * x
        RationalConverter.of(17, 13), // concrete example a ... x -> 17/13 * x
        RationalConverter.of(-34, 17)   ), // concrete example b ... x -> -34/17 * x
    MULTIPLY(MultiplyConverter.class, 
        ()->new MultiplyConverter(1.), // identity as expressed by this type ... x -> 1.0 * x
        new MultiplyConverter(17.23), // concrete example a ... x -> 17.23 * x
        new MultiplyConverter(-0.333) ), // concrete example b ... x -> -0.333 * x
    ADD(AddConverter.class, 
        ()->new AddConverter(0.), // identity as expressed by this type ... x -> 0 + x
        new AddConverter(-4.5), // concrete example a ... x -> -4.5 + x
        new AddConverter(0.315) ), // concrete example b ... x -> 0.315 + x
    LOG(LogConverter.class, 
        null, // log has no identity
        new LogConverter(4.5), // concrete example a ... x -> Log(base=4.5, x)
        new LogConverter(0.1) ), // concrete example b ... x -> Log(base=0.1, x)
    EXP(ExpConverter.class, 
        null, // exp has no identity
        new ExpConverter(4.5), // concrete example a ... x -> 4.5^x
        new ExpConverter(0.1) ), // concrete example b ... x -> 0.1^x
    PI(PowerOfPiConverter.class, 
        ()->PowerOfPiConverter.of(0), // identity as expressed by this type ... x -> π^0 * x
        PowerOfPiConverter.of(1), // concrete example a ... x -> π^1
        PowerOfPiConverter.of(-1) ), // concrete example b ... x -> π^-1
    // when adding entries, also increment the typeCount!
    ;

    public static final int typeCount = 8; // should be equal to ConverterType.values().length 
    public static final int candidatesPerType = 2;
    public static final int candidateCount = typeCount * candidatesPerType;

    private final Class<? extends UnitConverter> type;
    private final UnitConverter[] candidates;
    private Supplier<? extends UnitConverter> identitySupplier;

    public Class<? extends UnitConverter> getType() { return type; }
    public UnitConverter[] getCandidates() { return candidates; }

    @SafeVarargs
    private <T extends UnitConverter> ConverterType(
        Class<T> type,
        Supplier<T> identitySupplier,
        T ... instances) {
      this.type = type;
      this.identitySupplier = identitySupplier;
      this.candidates = instances;
    }

    public boolean hasIdentity() {
      return identitySupplier!=null;
    }

    public UnitConverter getIdentity() {
      return identitySupplier.get();
    }

  }

  // -- HELPER - PARAMETER PROVIDER - 1  

  static class ConverterTypesForTests implements ParameterResolver {

    private Map<String, Integer> indexByContext = new HashMap<>();

    @Override
    public boolean supportsParameter(
        ParameterContext parameterContext,
        ExtensionContext extensionContext) throws ParameterResolutionException {
      return parameterContext.getParameter().getType() == ConverterType.class;
    }

    @Override
    public Object resolveParameter(
        ParameterContext parameterContext,
        ExtensionContext extensionContext) throws ParameterResolutionException {

      String conextKey = parameterContext.getDeclaringExecutable().toString()+":"+parameterContext.getIndex();

      int next = indexByContext.compute(conextKey, (__, index)->index!=null ? index+1 : 0);
      int modulus = BigInteger.valueOf(ConverterType.candidateCount).pow(1+parameterContext.getIndex()).intValue();
      int divisor = BigInteger.valueOf(ConverterType.candidateCount).pow(parameterContext.getIndex()).intValue();

      next = (next % modulus) / divisor;

      ConverterType candidate = ConverterType.values()[next%ConverterType.typeCount];

      return candidate;
    }
  }

  // -- HELPER - PARAMETER PROVIDER - 2  

  static class UnitConverterForCompositionTests implements ParameterResolver {

    private Map<String, Integer> indexByContext = new HashMap<>();

    @Override
    public boolean supportsParameter(
        ParameterContext parameterContext,
        ExtensionContext extensionContext) throws ParameterResolutionException {
      return parameterContext.getParameter().getType() == UnitConverter.class;
    }

    @Override
    public Object resolveParameter(
        ParameterContext parameterContext,
        ExtensionContext extensionContext) throws ParameterResolutionException {

      String conextKey = parameterContext.getDeclaringExecutable().toString()+":"+parameterContext.getIndex();

      int next = indexByContext.compute(conextKey, (__, index)->index!=null ? index+1 : 0);
      int modulus = BigInteger.valueOf(ConverterType.candidateCount).pow(1+parameterContext.getIndex()).intValue();
      int divisor = BigInteger.valueOf(ConverterType.candidateCount).pow(parameterContext.getIndex()).intValue();

      next = (next % modulus) / divisor;

      UnitConverter candidate = ConverterType.values()[next/ConverterType.candidatesPerType]
          .getCandidates()[next%ConverterType.candidatesPerType];

      return candidate;
    }
  }

}
