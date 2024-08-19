/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2024, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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

import static org.junit.jupiter.api.Assertions.*;
import static tech.units.indriya.unit.Units.GRAM;
import static tech.units.indriya.unit.Units.KILOGRAM;
import static tech.units.indriya.unit.Units.METRE;
import static javax.measure.Quantity.Scale.*;

import javax.measure.MetricPrefix;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.measure.quantity.Mass;

import org.junit.jupiter.api.Test;

public class QuantityRangeTest {
  private final Quantity<Mass> zeroKilogram = Quantities.getQuantity(0d, KILOGRAM);
  private final Quantity<Mass> oneKilogram = Quantities.getQuantity(1d, KILOGRAM);
  private final Quantity<Mass> twoKilogram = Quantities.getQuantity(2d, KILOGRAM);
  private final Quantity<Mass> tenKilogram = Quantities.getQuantity(10d, KILOGRAM);
  private final Quantity<Mass> twentyKilogram = Quantities.getQuantity(20d, KILOGRAM);
  @SuppressWarnings("unchecked")
  private final QuantityRange<Mass> oneToTenKilogram = QuantityRange.of(oneKilogram, tenKilogram);
  @SuppressWarnings("unchecked")
  private final QuantityRange<Mass> oneToTenKilogramWithTwoKilogramResolution = QuantityRange.of(oneKilogram, tenKilogram, twoKilogram);
  @SuppressWarnings("unchecked")
  private final QuantityRange<Mass> oneKilogramOrMore = QuantityRange.of(oneKilogram, null);
  @SuppressWarnings("unchecked")
  private final QuantityRange<Mass> upToTenKilogram = QuantityRange.of(null, tenKilogram);
  private final Quantity<Length> oneMetre = Quantities.getQuantity(1d, METRE);

  private class NonComparableMassQuantity implements Quantity<Mass> {

    private final Number value;
    private final Unit<Mass> unit;
    private final Scale scale;
    
    NonComparableMassQuantity(double value, Unit<Mass> unit, Scale sc) {
        this.value = value;
        this.unit = unit;
        this.scale = sc;
      }
    
    NonComparableMassQuantity(double value, Unit<Mass> unit) {
        this(value, unit, ABSOLUTE);
    }

    @Override
    public Quantity<Mass> add(Quantity<Mass> arg0) {
      return null;
    }

    @Override
    public <T extends Quantity<T>> Quantity<T> asType(Class<T> arg0) throws ClassCastException {
      return null;
    }

    @Override
    public Quantity<?> divide(Quantity<?> arg0) {
      return null;
    }

    @Override
    public Quantity<Mass> divide(Number arg0) {
      return null;
    }

    @Override
    public Unit<Mass> getUnit() {
      return unit;
    }

    @Override
    public Number getValue() {
      return value;
    }

    @Override
    public Quantity<?> inverse() {
      return null;
    }

    @Override
    public Quantity<?> multiply(Quantity<?> arg0) {
      return null;
    }

    @Override
    public Quantity<Mass> multiply(Number arg0) {
      return null;
    }

    @Override
    public Quantity<Mass> subtract(Quantity<Mass> arg0) {
      return null;
    }

    @Override
    public Quantity<Mass> to(Unit<Mass> u) {
      return new NonComparableMassQuantity(getUnit().getConverterTo(u).convert(getValue()).doubleValue(), u);
    }

	@Override
	public Quantity<Mass> negate() {
		return new NonComparableMassQuantity(-value.doubleValue(), unit);
	}
	
	@Override
	public Scale getScale() {
	    return scale;
	}

	@Override
	public boolean isEquivalentTo(Quantity<Mass> that) {
		return false;
	}
  }

  /**
   * Verifies that the factory method without a resolution throws an IllegalArgumentException when incompatible quantities are used.
   */
  @Test
  public void factoryMethodWithoutResolutionThrowsIllegalArgumentExceptionOnIncompatibleQuantities() {
    assertThrows(IllegalArgumentException.class, () -> {
      QuantityRange.of(oneKilogram, oneMetre);
    });
  }

  /**
   * Verifies that the factory method with a resolution throws an IllegalArgumentException when an incompatible minimum is used.
   */
  @Test
  public void factoryMethodWithResolutionThrowsIllegalArgumentExceptionOnIncompatibleMinimum() {
    assertThrows(IllegalArgumentException.class, () -> {
      QuantityRange.of(oneMetre, oneKilogram, oneKilogram);
    });
  }

  /**
   * Verifies that the factory method with a resolution throws an IllegalArgumentException when an incompatible maximum is used.
   */
  @Test
  public void factoryMethodWithResolutionThrowsIllegalArgumentExceptionOnIncompatibleMaximum() {
    assertThrows(IllegalArgumentException.class, () -> {
      QuantityRange.of(oneKilogram, oneMetre, oneKilogram);
    });
  }

  /**
   * Verifies that the factory method with a resolution throws an IllegalArgumentException when an incompatible resolution is used.
   */
  @Test
  public void factoryMethodWithResolutionThrowsIllegalArgumentExceptionOnIncompatibleResolution() {
    assertThrows(IllegalArgumentException.class, () -> {
      QuantityRange.of(oneKilogram, tenKilogram, oneMetre);
    });
  }

  
  /**
   * Verifies that the parameter minimum is wired correctly in factory method and constructor with resolution.
   */
  @Test
  public void minimumIsWiredCorrectlyForFactoryMethodWithResolution() {
    assertEquals(oneKilogram, oneToTenKilogramWithTwoKilogramResolution.getMinimum());
  }

  /**
   * Verifies that the parameter minimum is wired correctly in factory method and constructor without resolution.
   */
  @Test
  public void minimumIsWiredCorrectlyForFactoryMethodWithoutResolution() {
    assertEquals(oneKilogram, oneToTenKilogram.getMinimum());
  }

  /**
   * Verifies that the parameter maximum is wired correctly in factory method and constructor with resolution.
   */
  @Test
  public void maximumIsWiredCorrectlyForFactoryMethodWithResolution() {
    assertEquals(tenKilogram, oneToTenKilogramWithTwoKilogramResolution.getMaximum());
  }

  /**
   * Verifies that the parameter maximum is wired correctly in factory method and constructor without resolution.
   */
  @Test
  public void maximumIsWiredCorrectlyForFactoryMethodWithoutResolution() {
    assertEquals(tenKilogram, oneToTenKilogram.getMaximum());
  }

  /**
   * Verifies that the resolution is null if the fasctory method without a resolution is used.
   */
  @Test
  public void resolutionIsNullWithFactoryMethodWithoutResolution() {
    assertNull(oneToTenKilogram.getResolution());
  }

  /**
   * Verifies that the parameter resolution is wired correctly in factory method and constructor.
   */
  @Test
  public void resolutionIsWiredCorrectlyForFactoryMethodWithResolution() {
    assertEquals(twoKilogram, oneToTenKilogramWithTwoKilogramResolution.getResolution());
  }

  /**
   * Verifies that the factory method with resolution returns an instance of QuantityRange.
   */
  @Test
  public void factoryMethodWithResolutionReturnsQuantityRangeInstance() {
    assertTrue(oneToTenKilogramWithTwoKilogramResolution.getClass().equals(QuantityRange.class));
  }

  /**
   * Verifies that the factory method without resolution returns an instance of QuantityRange.
   */
  @Test
  public void factoryMethodWithoutResolutionReturnsQuantityRangeInstance() {
    assertTrue(oneToTenKilogram.getClass().equals(QuantityRange.class));
  }

  /**
   * Verifies that the toString method produces the correct result for a range with a resolution.
   */
  @Test
  public void toStringProducesCorrectResultWithResolution() {
    assertEquals("min=1 kg, max=10 kg, res=2 kg", oneToTenKilogramWithTwoKilogramResolution.toString());
  }

  /**
   * Verifies that the toString method produces the correct result for a range without a resolution.
   */
  @Test
  public void toStringProducesCorrectResultWithoutResolution() {
    assertEquals("min=1 kg, max=10 kg", oneToTenKilogram.toString());
  }

  /**
   * Verifies that the contains method return true for a value between the minimum and the maximum value.
   */
  @Test
  public void containsReturnsTrueOnValueBetweenMinimumAndMaximum() {
    assertTrue(oneToTenKilogramWithTwoKilogramResolution.contains(Quantities.getQuantity(5d, KILOGRAM)));
  }

  /**
   * Verifies that the contains method return true for a non-comparable value between the minimum and the maximum value.
   */
  @Test
  public void containsReturnsTrueOnNonComparableValueBetweenMinimumAndMaximum() {
    assertTrue(oneToTenKilogramWithTwoKilogramResolution.contains(new NonComparableMassQuantity(5000d, GRAM)));
  }

  /**
   * Verifies that the contains method return true for the minimum value.
   */
  @Test
  public void containsReturnsTrueForMinimum() {
    assertTrue(oneToTenKilogramWithTwoKilogramResolution.contains(oneKilogram));
  }

  /**
   * Verifies that the contains method return true for a non-comparable minimum value.
   */
  @Test
  public void containsReturnsTrueForNonComparableMinimum() {
    assertTrue(oneToTenKilogramWithTwoKilogramResolution.contains(new NonComparableMassQuantity(0.001d, MetricPrefix.MEGA(GRAM))));
  }

  /**
   * Verifies that the contains method return false for a value below the minimum.
   */
  @Test
  public void containsReturnsFalseOnValueBelowTheMinimum() {
    assertFalse(oneToTenKilogramWithTwoKilogramResolution.contains(zeroKilogram));
  }

  /**
   * Verifies that the contains method return false for a non-comparable value below the minimum.
   */
  @Test
  public void containsReturnsFalseOnNonComparableValueBelowTheMinimum() {
    assertFalse(oneToTenKilogramWithTwoKilogramResolution.contains(new NonComparableMassQuantity(5d, MetricPrefix.CENTI(GRAM))));
  }

  /**
   * Verifies that the contains method return true for the maximum value.
   */
  @Test
  public void containsReturnsTrueForMaximum() {
    assertTrue(oneToTenKilogramWithTwoKilogramResolution.contains(tenKilogram));
  }

  /**
   * Verifies that the contains method return true for a non-comparable maximum value.
   */
  @Test
  public void containsReturnsTrueForNonComparableMaximum() {
    assertTrue(oneToTenKilogramWithTwoKilogramResolution.contains(new NonComparableMassQuantity(100d, MetricPrefix.HECTO(GRAM))));
  }

  /**
   * Verifies that the contains method return false for a value above the maximum.
   */
  @Test
  public void containsReturnsFalseOnValueAboveTheMaximum() {
    assertFalse(oneToTenKilogramWithTwoKilogramResolution.contains(twentyKilogram));
  }

  /**
   * Verifies that the contains method return false for a non-comparable value above the maximum.
   */
  @Test
  public void containsReturnsFalseOnNonComparableValueAboveTheMaximum() {
    assertFalse(upToTenKilogram.contains(new NonComparableMassQuantity(0.1d, MetricPrefix.MEGA(GRAM))));
  }

  /**
   * Verifies that the contains method return true for a value between the minimum and the maximum value after conversion.
   */
  @Test
  public void containsReturnsTrueOnConvertedValueBetweenMinimumAndMaximum() {
    assertTrue(oneToTenKilogramWithTwoKilogramResolution.contains(Quantities.getQuantity(3000, GRAM)));
  }

  /**
   * Verifies that the contains method returns true for value below maximum of half-range.
   */
  @Test
  public void containsReturnsTrueForValueBelowMaximumOfHalfRange() {
    assertTrue(upToTenKilogram.contains(oneKilogram));
  }

  /**
   * Verifies that the contains method returns true for maximum of half-range.
   */
  @Test
  public void containsReturnsTrueForMaximumOfHalfRange() {
    assertTrue(upToTenKilogram.contains(tenKilogram));
  }

  /**
   * Verifies that the contains method returns false for value above maximum of half-range.
   */
  @Test
  public void containsReturnsFalseForValueAboveMaximumOfHalfRange() {
    assertFalse(upToTenKilogram.contains(twentyKilogram));
  }

  /**
   * Verifies that the contains method returns true for value above minimum of half-range.
   */
  @Test
  public void containsReturnsTrueForValueAboveMinimumOfHalfRange() {
    assertTrue(oneKilogramOrMore.contains(tenKilogram));
  }

  /**
   * Verifies that the contains method returns true for minimum of half-range.
   */
  @Test
  public void containsReturnsTrueForMinimumOfHalfRange() {
    assertTrue(oneKilogramOrMore.contains(oneKilogram));
  }

  /**
   * Verifies that the contains method returns false for value above maximum of half-range.
   */
  @Test
  public void containsReturnsFalseForValueBelowMinimumOfHalfRange() {
    assertFalse(oneKilogramOrMore.contains(zeroKilogram));
  }

  /**
   * Verifies that contains throws an exception when null is provided as parameter.
   */
  @Test
  public void containsThrowsExceptionForNull() {
    assertThrows(Exception.class, () -> {
      oneToTenKilogram.contains(null);
    });
  }

  /**
   * Verifies that a quantity range isn't equal to null.
   */
  @Test
  public void quantityRangeIsNotEqualToNull() {
    assertNotNull(oneToTenKilogram);
  }

  /**
   * Verifies that a quantity range is equal to itself.
   */
  @Test
  public void quantityRangeIsEqualToItself() {
    assertEquals(oneToTenKilogram, oneToTenKilogram);
  }

  /**
   * Verifies that a quantity range is not equal to a quantity range with a different minimum.
   */
  @Test
  public void quantityRangeIsNotEqualToAnotherQuantityRangeWithADifferentMinimum() {
    assertFalse(oneToTenKilogram.equals(QuantityRange.of(twoKilogram, tenKilogram)));
  }

  /**
   * Verifies that a quantity range is not equal to a quantity range with a different maximum.
   */
  @Test
  public void quantityRangeIsNotEqualToAnotherQuantityRangeWithADifferentMaximum() {
    assertFalse(oneToTenKilogram.equals(QuantityRange.of(oneKilogram, twoKilogram)));
  }

  /**
   * Verifies that a quantity range is not equal to a quantity range with a different resolution.
   */
  @Test
  public void quantityRangeIsNotEqualToAnotherQuantityRangeWithADifferentResolution() {
    assertFalse(oneToTenKilogramWithTwoKilogramResolution.equals(QuantityRange.of(oneKilogram, tenKilogram, oneKilogram)));
  }

  /**
   * Verifies that the hash codes of two quantity ranges with different minimums aren't equal. Notice that this isn't a strict requirement on the
   * hashCode method, and that hash collisions may occur, but in general, objects that aren't equal shouldn't have an equal hash code.
   */
  @Test
  public void hashCodeShouldBeDifferentForQuantityRangesWithDifferentMinimums() {
    assertFalse(oneToTenKilogram.hashCode() == QuantityRange.of(twoKilogram, tenKilogram).hashCode());
  }

  /**
   * Verifies that the hash codes of two quantity ranges with different maximums aren't equal. Notice that this isn't a strict requirement on the
   * hashCode method, and that hash collisions may occur, but in general, objects that aren't equal shouldn't have an equal hash code.
   */
  @Test
  public void hashCodeShouldBeDifferentForQuantityRangesWithDifferentMaximums() {
    assertFalse(oneToTenKilogram.hashCode() == QuantityRange.of(oneKilogram, twoKilogram).hashCode());
  }

  /**
   * Verifies that the hash codes of two quantity ranges with different resolutions aren't equal. Notice that this isn't a strict requirement on the
   * hashCode method, and that hash collisions may occur, but in general, objects that aren't equal shouldn't have an equal hash code.
   */
  @Test
  public void hashCodeShouldBeDifferentForQuantityRangesWithDifferentResolutions() {
    assertFalse(oneToTenKilogram.hashCode() == QuantityRange.of(oneKilogram, tenKilogram, twoKilogram).hashCode());
  }

  /**
   * Verifies that a quantity range is not equal to an object of a different class.
   */
  @Test
  public void quantityRangeIsNotEqualToObjectOfDifferentClass() {
    assertNotEquals(oneToTenKilogram, oneKilogram);
  }
}
