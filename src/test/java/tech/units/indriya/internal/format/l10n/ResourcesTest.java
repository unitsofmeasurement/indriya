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
package tech.units.indriya.internal.format.l10n;

import static org.junit.Assert.*;

import java.util.ResourceBundle;

import org.junit.Test;

import tech.units.indriya.internal.format.l10n.MultiPropertyResourceBundle;

public class ResourcesTest {

  @Test
  public void testResources() {
    ResourceBundle bundle = ResourceBundle.getBundle("format/messages");

    assertNotNull(bundle.getString("res1"));

    MultiPropertyResourceBundle multiBundle = new MultiPropertyResourceBundle(bundle, "format");
    assertNotNull(multiBundle.getString("res1"));

    ResourceBundle bundle2 = ResourceBundle.getBundle("other_format/more_messages");
    assertNotNull(bundle2.getString("more1"));

    multiBundle.merge(bundle2, "other_format");

    assertNotNull(multiBundle.getString("res1"));
    assertNotNull(multiBundle.getString("res2"));
    assertNotNull(multiBundle.getString("res3"));

    assertEquals("res1", multiBundle.getString("res1"));
    assertEquals("res2", multiBundle.getString("res2"));
    assertEquals("res3", multiBundle.getString("res3"));
    assertEquals("more message1", multiBundle.getString("more1"));
  }

}
