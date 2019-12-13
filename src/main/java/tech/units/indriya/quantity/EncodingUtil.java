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
package tech.units.indriya.quantity;

import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

import javax.measure.Quantity;

import tech.units.indriya.quantity.time.TemporalQuantity;
import tech.units.indriya.quantity.time.TimeUnitQuantity;

/**
 * Provides support for JavaBeans XML serialization of {@link Quantity} instances.
 * @see {@link java.beans.XMLEncoder}
 * @author Andi Huber
 * @since 2.0.2
 */
public class EncodingUtil {

    public static void setupXMLEncoder(XMLEncoder encoder) {
        encoder.setPersistenceDelegate(NumberQuantity.class, QUANTITY_PERSISTENCE_DELEGATE);
        encoder.setPersistenceDelegate(TemporalQuantity.class, QUANTITY_PERSISTENCE_DELEGATE);
        encoder.setPersistenceDelegate(TimeUnitQuantity.class, QUANTITY_PERSISTENCE_DELEGATE);
    }
    
    public static String base64Encode(Quantity<?> quantity) throws IOException {
        
        byte[] buffer;
        
        try(ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(quantity);
            oos.close();
            buffer = bos.toByteArray();
        }
        
        return Base64.getEncoder().encodeToString(buffer);
    }
    
    public static Quantity<?> base64Decode(String base64) throws IOException, ClassNotFoundException {
        
        final byte[] buffer = Base64.getDecoder().decode(base64);
        
        Quantity<?> q;
        
        try(ByteArrayInputStream bis = new ByteArrayInputStream(buffer)) {
            ObjectInputStream ois = new ObjectInputStream(bis);
            q = (Quantity<?>) ois.readObject();
        }
        
        return q;
    }

    
    // -- HELPER
    
    private final static PersistenceDelegateForQuantity QUANTITY_PERSISTENCE_DELEGATE = 
            new PersistenceDelegateForQuantity();
    
    private static class PersistenceDelegateForQuantity extends PersistenceDelegate {
        
        @Override
        protected Expression instantiate(Object oldInstance, Encoder out) {

            Quantity<?> quantity = (Quantity<?>) oldInstance;
            String base64EncodedQuantity;
            
            try {
                base64EncodedQuantity = base64Encode(quantity);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
            
            return new Expression(
                    oldInstance,
                    EncodingUtil.class,
                    "base64Decode",
                    new Object[]{
                            base64EncodedQuantity
                    });
        }
    }
    

}
