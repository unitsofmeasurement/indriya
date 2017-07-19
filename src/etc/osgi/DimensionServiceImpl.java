/**
 *  Unit-API - Units of Measurement API for Java
 *  Copyright (c) 2005-2014, Jean-Marie Dautelle, Werner Keil, V2COM.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of JSR-363 nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
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
package org.unitsofmeasurement.ri.internal.osgi;

import org.unitsofmeasurement.ri.AbstractDimension;
import org.unitsofmeasurement.quantity.Quantity;

/**
 * <p> This interface represents the service to retrieve the dimension
 *     given a quantity type.</p>
 * 
 * <p> Bundles providing new quantity types and/or dimensions should publish 
 *     instances of this class in order for the framework to be able
 *     to determinate the dimension associated to the new quantities.</p>
 * 
 * <p> When activated the unit-ri bundle publishes the dimensional 
 *     mapping of all quantities defined in the 
 *     <code>org.unitsofmeasurement.quantity</code> package.</p>

 * <p> Published instances are typically used to check the dimensional 
 *     consistency of physical quantities.</p>
 *     
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 5.1, April 7, 2014
 */
public class DimensionServiceImpl implements DimensionService {
// FIXME class,  not interface
    /**
     * Returns the dimension for the specified quantity or <code>null</code> if
     * unknown.
     *
     * @return the corresponding dimension or <code>null</code>
     */
    <Q extends Quantity<Q>> Dimension getDimension(Class<Q> quantityType);
    
}
