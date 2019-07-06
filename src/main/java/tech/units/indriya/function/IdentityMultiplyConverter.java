/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2019, Units of Measurement project.
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

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.measure.UnitConverter;

/**
 * @author Andi Huber
 * @author Werner Keil
 * @since 2.0
 */
final class IdentityMultiplyConverter implements MultiplyConverter, Serializable {

    private static final long serialVersionUID = 1L;
    
    final static IdentityMultiplyConverter INSTANCE = new IdentityMultiplyConverter();

    private IdentityMultiplyConverter() {
        // hidden
    }
    
    @Override
    public boolean isIdentity() {
        return true;
    }

    @Override
    public UnitConverter inverse() {
        return this;
    }

    @Override
    public Number convert(Number value) {
        return value;
    }

    @Override
    public double convert(double value) {
        return value;
    }

    @Override
    public UnitConverter concatenate(UnitConverter converter) {
        return converter;
    }

    @Override
    public List<? extends UnitConverter> getConversionSteps() {
        return Collections.emptyList();
    }

    @Override
    public Number getValue() {
        return 1;
    }

    @Override
    public int compareTo(UnitConverter o) {
        return AbstractConverter.IDENTITY.compareTo(o);
    }
    
    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof IdentityMultiplyConverter) {
			IdentityMultiplyConverter other = (IdentityMultiplyConverter) obj;
			if(this.isIdentity() && other.isIdentity()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getValue());
	}
}
