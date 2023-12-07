/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2023, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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
package tech.units.indriya.spi;

import java.util.Locale;
import java.util.ResourceBundle.Control;
import java.util.spi.LocaleServiceProvider;

/**
 * An abstract class for service providers that
 * provide localized unit symbols and display names for the
 * {@link javax.measure.Unit unit} class.
 * Note that unit symbols are considered names when determining
 * behaviors described in the
 * {@link java.util.spi.LocaleServiceProvider LocaleServiceProvider}
 * specification.
 *
 * @since        2.0.3
 */
public abstract class UnitNameProvider extends LocaleServiceProvider {

    /**
     * Sole constructor.  (For invocation by subclass constructors, typically
     * implicit.)
     */
    protected UnitNameProvider() {
    }

    /**
     * Gets the symbol of the given unit for the specified locale.
     * For example, for "m" (metre), the symbol is "m" if the specified
     * locale is the US, while for other locales it may be "M". If no
     * symbol can be determined, null should be returned.
     *
     * @param unitCode the unit code, which
     *     consists of three upper-case letters between 'A' (U+0041) and
     *     'Z' (U+005A)
     * @param locale the desired locale
     * @return the symbol of the given unit for the specified locale, or null if
     *     the symbol is not available for the locale
     * @exception NullPointerException if <code>unitCode</code> or
     *     <code>locale</code> is null
     * @exception IllegalArgumentException if <code>unitCode</code> is not in
     *     the form of three upper-case letters, or <code>locale</code> isn't
     *     one of the locales returned from
     *     {@link java.util.spi.LocaleServiceProvider#getAvailableLocales()
     *     getAvailableLocales()}.
     * @see javax.measure.Unit#getSymbol(java.util.Locale)
     */
    public abstract String getSymbol(String unitCode, Locale locale);

    /**
     * Returns a name for the unit that is appropriate for display to the
     * user.  The default implementation returns null.
     *
     * @param unitCode the ISO 4217 unit, which
     *     consists of three upper-case letters between 'A' (U+0041) and
     *     'Z' (U+005A)
     * @param locale the desired locale
     * @return the name for the unit that is appropriate for display to the
     *     user, or null if the name is not available for the locale
     * @exception IllegalArgumentException if <code>unitCode</code> is not in
     *     the form of three upper-case letters, or <code>locale</code> isn't
     *     one of the locales returned from
     *     {@link java.util.spi.LocaleServiceProvider#getAvailableLocales()
     *     getAvailableLocales()}.
     * @exception NullPointerException if <code>unitCode</code> or
     *     <code>locale</code> is <code>null</code>
     */
    public String getDisplayName(String unitCode, Locale locale) {
        if (unitCode == null || locale == null) {
            throw new NullPointerException();
        }

        // Check whether the unitCode is valid
        char[] charray = unitCode.toCharArray();
        if (charray.length != 3) {
            throw new IllegalArgumentException("The unitCode is not in the form of three upper-case letters.");
        }
        for (char c : charray) {
            if (c < 'A' || c > 'Z') {
                throw new IllegalArgumentException("The unit is not in the form of three upper-case letters.");
            }
        }

        // Check whether the locale is valid
        // TODO revise for Java 9 and above (Modules)
//      Control c = Control.getNoFallbackControl(Control.FORMAT_DEFAULT);
//      for (Locale l : getAvailableLocales()) {
//          if (c.getCandidateLocales("", l).contains(locale)) {
//              return null;
//          }
//      }

        throw new IllegalArgumentException("The locale is not available");
    }
}

