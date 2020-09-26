/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2020, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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
package tech.units.indriya.format;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:werner@uom.technology">Werner Keil</a>
 * @version 1.1
 */
public class FormatExceptionsTest {  

    @Test
    public void testTokenException() {
        TokenException e = assertThrows(TokenException.class, () -> {
            throw new TokenException();
        });
        assertNotNull(e.getMessage());
        assertEquals("", e.getMessage());
        assertNull(e.getCause());
    }

    @Test
    public void testTokenExceptionWithMessage() {
        TokenException e = assertThrows(TokenException.class, () -> {
            throw new TokenException("error");
        });

        assertEquals("error", e.getMessage());
        assertNull(e.getCause());
    }
	
    @Test
    public void testTokenMgrError() {
        TokenMgrError e = assertThrows(TokenMgrError.class, () -> {
            throw new TokenMgrError();
        });

        assertNull(e.getMessage());
        assertNull(e.getCause());
    }
    
    @Test
    public void testTokenMgrErrorWithMessage() {
        TokenMgrError e = assertThrows(TokenMgrError.class, () -> {
            throw new TokenMgrError("error");
        });

        assertEquals("error", e.getMessage());
        assertNull(e.getCause());
    }
    
    @Test
    public void testTokenMgrErrorWithMessageAndReason() {
        TokenMgrError e = assertThrows(TokenMgrError.class, () -> {
            throw new TokenMgrError("error", 0);
        });

        assertNotNull(e.getMessage());
        assertEquals("error", e.getMessage()); 
    }
}
