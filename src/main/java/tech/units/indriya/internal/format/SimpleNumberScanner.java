/* Units of Measurement Reference Implementation
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
package tech.units.indriya.internal.format;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;

import javax.measure.format.MeasurementParseException;

import tech.units.indriya.format.SimpleQuantityFormat;
import tech.units.indriya.function.RationalNumber;

/**
 * Support class for {@link SimpleQuantityFormat} and {@link RationalNumberFormat}.
 * <p>
 * In addition to decimal formats this also parses rarional number format {@code (5 ÷ 3)}.
 * 
 * @author Andi Huber
 *
 */
public class SimpleNumberScanner {

    private final CharSequence csq; 
    private final ParsePosition cursor;
    private final NumberFormat numberFormat;
    
    private int openedParenthesis = 0;
    private int divisorDetected = 0;
    
    public SimpleNumberScanner(CharSequence csq, ParsePosition cursor, NumberFormat numberFormat) {
        this.csq = csq;
        this.cursor = cursor;
        this.numberFormat = numberFormat;
    }
    
    private int scanForStart(int pos) {
        while (pos < csq.length()) {
            char c = csq.charAt(pos); 
            if(Character.isWhitespace(c)) {
                pos++;
                continue;
            }
            if(c == '÷') {
                pos++;
                divisorDetected++;
                continue;
            }
            if(c == '(') {
                pos++;
                openedParenthesis++;
                continue;
            }
            break;
        }
        return pos;       
    }
    
    private int scanForEnd(int pos) {
        while (pos < csq.length()) {
            char c = csq.charAt(pos);
            if(c == ')') {
                openedParenthesis--;
                cursor.setIndex(pos + 2);
                return pos;
            }
            if(Character.isWhitespace(c)) {
                break;
            } 
            pos++;
        }
        cursor.setIndex(pos + 1);
        return pos;        
    }

    public Number getNumber() {
        
        BigInteger dividend = null;
        BigInteger divisor = null;
        Number decimalNumber = null;
        
        do {
            int startDecimal = scanForStart(cursor.getIndex());
            int endDecimal = scanForEnd(startDecimal+1);
            
            String numberLiteral = csq.subSequence(startDecimal, endDecimal).toString();
            
            if(divisorDetected>0) {
                divisor = new BigInteger(numberLiteral);
                divisorDetected--;
            } else if(openedParenthesis>0) {
                dividend = new BigInteger(numberLiteral);
            } else {
                
                if(numberFormat==null) {
                    return new BigDecimal(numberLiteral);
                }
                
                try {
                    decimalNumber = numberFormat.parse(numberLiteral);
                } catch (ParseException e) {
                    throw new MeasurementParseException(e);
                }
            }
            
        } while (openedParenthesis>0);
        
        if(decimalNumber!=null) {
            return decimalNumber;
        }
        
        return RationalNumber.of(dividend, divisor);
        
    }
    
}
