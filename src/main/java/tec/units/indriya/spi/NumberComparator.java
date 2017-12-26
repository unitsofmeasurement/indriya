/*
 * Next Generation Units of Measurement Implementation
 * Copyright (c) 2005-2017, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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
 * 3. Neither the name of JSR-363, Indriya nor the names of their contributors may be used to endorse or promote products
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
package tec.units.indriya.spi;

import static java.lang.Character.isDigit;
import static java.lang.Character.isSpaceChar;

import java.util.Comparator;

// TODO move this to uom-lib-common
class NumberComparator implements Comparator<Object> {

  private NumberComparator() {
    // Singleton
  }

  protected static char charAt(String s, int i) {
    if (i >= s.length()) {
      return '\000';
    }

    return s.charAt(i);
  }

  protected int compareRight(String a, String b) {
    int bias = 0;
    int ia = 0;
    int ib = 0;
    while (true) {
      char ca = charAt(a, ia);
      char cb = charAt(b, ib);

      if ((!isDigit(ca)) && (!isDigit(cb))) {
        return bias;
      }
      if (!isDigit(ca)) {
        return -1;
      }
      if (!isDigit(cb)) {
        return 1;
      }
      if (ca < cb) {
        if (bias == 0) {
          bias = -1;
        }
      } else if (ca > cb) {
        if (bias == 0)
          bias = 1;
      } else if ((ca == 0) && (cb == 0))
        return bias;
      ia++;
      ib++;
    }
  }

  public int compare(Object o1, Object o2) {
    String a = o1.toString();
    String b = o2.toString();

    int ia = 0;
    int ib = 0;
    int nza;
    int nzb;
    while (true) {
      nza = nzb = 0;

      char ca = charAt(a, ia);
      char cb = charAt(b, ib);

      while ((isSpaceChar(ca)) || (ca == '0')) {
        if (ca == '0') {
          nza++;
        } else {
          nza = 0;
        }

        ca = charAt(a, ++ia);
      }

      while ((isSpaceChar(cb)) || (cb == '0')) {
        if (cb == '0') {
          nzb++;
        } else {
          nzb = 0;
        }

        cb = charAt(b, ++ib);
      }
      int result;
      if ((isDigit(ca)) && (isDigit(cb)) && ((result = compareRight(a.substring(ia), b.substring(ib))) != 0)) {
        return result;
      }

      if ((ca == 0) && (cb == 0)) {
        return nza - nzb;
      }

      if (ca < cb) {
        return -1;
      }
      if (ca > cb) {
        return 1;
      }

      ia++;
      ib++;
    }
  }

  private static NumberComparator INSTANCE;

  public static NumberComparator getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new NumberComparator();
    }
    return INSTANCE;
  }

}