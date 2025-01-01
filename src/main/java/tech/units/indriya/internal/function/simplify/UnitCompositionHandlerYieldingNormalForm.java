/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2025, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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
package tech.units.indriya.internal.function.simplify;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;

import tech.units.indriya.function.AbstractConverter;
import tech.units.indriya.function.Calculus;
import tech.units.indriya.function.ConverterCompositionHandler;
import tech.units.indriya.function.PowerOfIntConverter;

/**
 * UnitCompositionHandler yielding a normal-form.
 * A normal-form is required to decide whether two UnitConverters are equivalent.
 * 
 * @author Andi Huber
 * @version 1.1
 * @since 2.0
 */
public class UnitCompositionHandlerYieldingNormalForm implements ConverterCompositionHandler {

  private final Map<Class<? extends AbstractConverter>, Integer> normalFormOrder;

  public UnitCompositionHandlerYieldingNormalForm() {
    normalFormOrder = Calculus.getNormalFormOrder();
  }

  @Override
  public AbstractConverter compose(
      AbstractConverter a, 
      AbstractConverter b,
      BiPredicate<AbstractConverter, AbstractConverter> canReduce,
      BinaryOperator<AbstractConverter> doReduce) {

    if(a.isIdentity()) {
      if(b.isIdentity()) {
        return isNormalFormOrderWhenIdentity(a, b) ? a : b;
      }
      return b;
    }
    if(b.isIdentity()) {
      return a;
    }

    if(canReduce.test(a, b)) {
      return doReduce.apply(a, b);
    }

    final boolean commutative = a.isLinear() && b.isLinear(); 
    final boolean swap = commutative && !isNormalFormOrderWhenCommutative(a, b);

    final AbstractConverter.Pair nonSimplifiedForm = swap 
        ? new AbstractConverter.Pair(b, a) 
            : new AbstractConverter.Pair(a, b); 

        return new CompositionTask(
            this::isNormalFormOrderWhenIdentity,
            this::isNormalFormOrderWhenCommutative,
            canReduce, 
            doReduce)
            .reduceToNormalForm(nonSimplifiedForm.getConversionSteps());

  }

  // -- HELPER

  private boolean isNormalFormOrderWhenIdentity(AbstractConverter a, AbstractConverter b) {
    if(a.getClass().equals(b.getClass())) {
      return true;
    }
    return normalFormOrder.get(a.getClass()) <= normalFormOrder.get(b.getClass());
  }

  private boolean isNormalFormOrderWhenCommutative(AbstractConverter a, AbstractConverter b) {
    if(a.getClass().equals(b.getClass())) {
      if(a instanceof PowerOfIntConverter) {
        return  ((PowerOfIntConverter)a).getBase() <= ((PowerOfIntConverter)b).getBase();
      }
      return true;
    }

    Integer orderA = Objects.requireNonNull(normalFormOrder.get(a.getClass()), 
        ()->String.format("no normal-form order defined for class '%s'", a.getClass().getName()));
    Integer orderB = Objects.requireNonNull(normalFormOrder.get(b.getClass()), 
        ()->String.format("no normal-form order defined for class '%s'", b.getClass().getName()));

    return orderA <= orderB;
  }


}
