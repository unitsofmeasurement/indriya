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
package tec.units.indriya.internal.format.l10n;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Implements an Enumeration that combines elements from a Set and an Enumeration. Used by {@link MultiPropertyResourceBundle}.
 */
class ResourceBundleEnumeration implements Serializable, Enumeration<String> {

  /**
   * For serialize.
   */
  private static final long serialVersionUID = -1259498757256943174L;

  private Set<String> set;
  private Iterator<String> iterator;
  private Enumeration<String> enumeration;
  private String next = null;

  /**
   * Constructor.
   */
  public ResourceBundleEnumeration() {
    super();
  }

  /**
   * Constructs a resource bundle enumeration.
   * 
   * @param set
   *          a set providing some elements of the enumeration
   * @param enumeration
   *          an enumeration providing more elements of the enumeration. enumeration may be null.
   */
  ResourceBundleEnumeration(Set<String> set, Enumeration<String> enumeration) {
    this.set = set;
    this.iterator = set.iterator();
    this.enumeration = enumeration;
  }

  public boolean hasMoreElements() {
    if (next == null) {
      if (iterator.hasNext()) {
        next = iterator.next();
      } else if (enumeration != null) {
        while (next == null && enumeration.hasMoreElements()) {
          next = enumeration.nextElement();
          if (set.contains(next)) {
            next = null;
          }
        }
      }
    }
    return next != null;
  }

  public String nextElement() {
    if (hasMoreElements()) {
      String result = next;
      next = null;
      return result;
    } else {
      throw new NoSuchElementException();
    }
  }

}