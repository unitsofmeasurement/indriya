/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2020, Units of Measurement project.
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
package tech.units.indriya;


/**
 * This error is used to indicate problems with creating, retrieving and manipulating units of measurement objects.
 * </p>
 *
 * implSpec This class is intended for use in a single thread. Exception thrown when errors occur during Units of Measurement operations.
 *
 * @author <a href="mailto:werner@uom.technology">Werner Keil</a>
 * @version 1.0, Jan 11, 2020
 * @since 2.0.2
 */
// TODO this could be moved into the API at a later point
public class MeasurementError extends Error {

    /**
	 * 
	 */
	private static final long serialVersionUID = -412360965273525777L;

	/**
     * Constructs a {@code MeasurementError} with no detail message.
     */
    public MeasurementError() {
        super();
    }

    /**
     * Constructs a {@code MeasurementError} with the specified detail
     * message.
     *
     * @param   s   the detail message.
     */
    public MeasurementError(String s) {
        super(s);
    }

    /**
     * Constructs a {@code MeasurementError} with the specified detail
     * message and cause.
     *
     * @param s     the detail message.
     * @param cause the cause, may be {@code null}
     */
    public MeasurementError(String s, Throwable cause) {
        super(s, cause);
    }
}
