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
package tech.units.indriya;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tech.units.indriya.function.RationalNumber;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

class IndriyaPerformanceTest {
    private static final Logger LOGGER = Logger.getLogger(IndriyaPerformanceTest.class.getName());
    private static final Level LOG_LEVEL = Level.FINE;
	
    @BeforeEach
    void setUp() throws Exception {
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    void testAdd() {

    	print("-- ADD");
        
        final _StopWatch t = new _StopWatch();
        
        int count = (int) 10E5;
        
        BigDecimal a = new BigDecimal(5);
        BigDecimal b = new BigDecimal("5.2");

        t.start();
        for (int i = 0; i < count; i++) {
            BigDecimal c = a.add(b);
            _Blackhole.consume(c);
        }
        print("bigdecimal " + t);
        
        Quantity<Length> q1 = Quantities.getQuantity(5, Units.METRE);
        Quantity<Length> q2 = Quantities.getQuantity(RationalNumber.of(52, 10), Units.METRE);
        t.start();
        for (int i = 0; i < count; i++) {
            _Blackhole.consume(q1.add(q2).getValue());
        }
        print("quantities " + t);

    }
    
    @Test
    void testScalarMul() {
        
    	print("-- SCALAR MUL");

        _StopWatch t = new _StopWatch();
        
        int count = (int) 10E5;
        
        BigDecimal a = new BigDecimal(5);
        BigDecimal b = new BigDecimal("5.2");
        
        t.start();
        for (int i = 0; i < count; i++) {
            BigDecimal c = a.multiply(b);
            _Blackhole.consume(c);
        }
        print("bigdecimal "+t);
        
        Quantity<Length> q1 = Quantities.getQuantity(5, Units.METRE);
        Number f = RationalNumber.of(52, 10);

        t.start();
        for (int i = 0; i < count; i++) {
            _Blackhole.consume(q1.multiply(f).getValue());
        }
        print("quantities "+t);

    }
    
    @Test
    void testMul() {
        
    	print("-- MUL");

        final _StopWatch t = new _StopWatch();
        
        int count = (int) 10E5;
                
        BigDecimal a = new BigDecimal(5);
        BigDecimal b = new BigDecimal("5.2");
        t.start();
        for (int i = 0; i < count; i++) {
            BigDecimal c = a.multiply(b);
            _Blackhole.consume(c);
        }
        print("bigdecimal "+ t);
        
        Quantity<Length> q1 = Quantities.getQuantity(5, Units.METRE);
        Number f = RationalNumber.of(52, 10);
        Quantity<Length> q2 = Quantities.getQuantity(f, Units.METRE);
        t.start();
        for (int i = 0; i < count; i++) {
            _Blackhole.consume(q1.multiply(q2).getValue());
        }
        print("quantities "+ t);

    }
    
    // -- HELPER
    
    private static final class _Blackhole {

        /**
         * Consume object. This call provides a side effect preventing JIT to eliminate dependent computations.
         *
         * @param obj object to consume.
         */
        public static void consume(Object obj) {

            int tlrMask = internal.tlrMask; // volatile read
            int tlr = (internal.tlr = (internal.tlr * 1664525 + 1013904223));

            if ((tlr & tlrMask) == 0) {
                // SHOULD ALMOST NEVER HAPPEN
                internal.obj1 = new WeakReference<>(obj);
                internal.tlrMask = (tlrMask << 1) + 1;
            }

        }

        // -- HELPER

        public static final class _Blackhole_Internal {

            public int tlr;
            public volatile int tlrMask;
            public volatile Object obj1;

            public _Blackhole_Internal() {
                Random r = new Random(System.nanoTime());
                tlr = r.nextInt();
                tlrMask = 1;
                obj1 = new Object();
            }

        }

        private static final _Blackhole_Internal internal = new _Blackhole_Internal();

    }
    
    /**
     * Non thread safe start/stop watch utilizing the currently running
     * JVM's high-resolution time source.
     * @implNote using {@link System#nanoTime()} as this is best suited to measure elapsed time
     */
    static final class _StopWatch {

        private long t0 = 0;
        private long t1 = 0;
        private boolean stopped;

        private _StopWatch(long startedAtSystemNanos) {
            t0 = startedAtSystemNanos;
        }

        private _StopWatch() {
            start();
        }

        public _StopWatch start() {
            t0 = System.nanoTime();
            stopped = false;
            return this;
        }

        public _StopWatch stop() {
            t1 = System.nanoTime();
            stopped  = true;
            return this;
        }

        public double getSeconds() {
            return 0.001 * getMillis();
        }

        /**
         * @return elapsed nano seconds since started 
         * (or when stopped, the time interval between started and stopped) 
         */
        public long getNanos() {
            return stopped ? t1 - t0 : System.nanoTime() - t0 ;
        }
        
        /**
         * @return elapsed micro seconds since started
         * (or when stopped, the time interval between started and stopped)
         */
        public long getMicros() {
            return getNanos()/1000L;
        }
        
        /**
         * @return elapsed milli seconds since started
         * (or when stopped, the time interval between started and stopped)
         */
        public long getMillis() {
            return getNanos()/1000_000L;
        }
        
        @Override
        public String toString() {
            return String.format(Locale.US, "%d ms", getMillis());
        }

    }
    
    private void print(String msg) {
    	LOGGER.log(LOG_LEVEL, msg);
    }
}
