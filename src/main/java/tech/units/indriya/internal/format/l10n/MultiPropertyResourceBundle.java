/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2022, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

/**
 * Extends <code>ResourceBundle</code> with 2 new capabilities. The first is to store the path where the properties file used to create the
 * <code>InputStream</code> is located and the second is to allow additional <code>ResourceBundle</code> properties to be merged into an instance.
 * <br>
 * <br>
 * To allow a <code>SystemOfUnits</code> to locate and merge extension module properties files.
 * <br>
 * 
 * @author Werner Keil
 * @version 1.2
 */
public class MultiPropertyResourceBundle extends ResourceBundle {
// also see https://github.com/vitorzachi/tcc-multitenancy/blob/master/tccMultitenancy/src/net/sf/trugger/util/MultiResourceBundle.java but that code might be older
	
  /**
   * The location of the properties file that was used to instantiate the <code>MultiPropertyResourceBundle</code> instance. This field is set by the
   * constructor.
   */
  private String resourcePath = null;

  /**
   * @return The location of the properties file that was used to instantiate the <code>MultiPropertyResourceBundle</code> instance.
   */
  public String getResourcePath() {
    return resourcePath;
  }

  /**
   * A {@link Map} containing all the properties that have been merged from multiple {@link ResourceBundle} instances.
   */
  private final Map<String, Object> resources = new HashMap<>();

  /**
   * A {@link StringBuilder} instance containing all the paths of the {@link ResourceBundle} instances that have been merged into this instance. This
   * value is intended to be use to help generate a key for caching JSON formatted resource output in the {@link AbstractWebScript} class.
   */
  private final StringBuilder mergedBundlePaths = new StringBuilder();

  /**
   * @return Returns the {@link StringBuilder} instance containing the paths of all the {@link ResourceBundle} instances that have been merged into
   *         this instance.
   */
  public StringBuilder getMergedBundlePaths() {
    return mergedBundlePaths;
  }

  /**
   * Instantiates a new <code>MultiPropertyResourceBundle</code>.
   * 
   * @param stream
   *          The <code>InputStream</code> passed on to the super class constructor.
   * @param resourcePath
   *          The location of the properties file used to create the <code>InputStream</code>
   * @throws IOException
   */
  public MultiPropertyResourceBundle(InputStream stream, String resourcePath) throws IOException {
    final ResourceBundle resourceBundle = new PropertyResourceBundle(stream);
    this.resourcePath = resourcePath;
    merge(resourceBundle, resourcePath);
  }

  /**
   * Constructor for instantiating from an existing {@link ResourceBundle}. This calls the <code>merge</code> method to copy the properties from the
   * bundle into the <code>resources</code> map.
   * 
   * @param baseBundle
   * @param resourcePath
   */
  public MultiPropertyResourceBundle(ResourceBundle baseBundle, String resourcePath) {
    super();
    this.resourcePath = resourcePath;
    merge(baseBundle, resourcePath);
  }

  /**
   * Merges the properties of a <code>ResourceBundle</code> into the current <code>MultiPropertyResourceBundle</code> instance. This will override any
   * values mapped to duplicate keys in the current merged properties.
   * 
   * @param resourceBundle
   *          The <code>ResourceBundle</code> to merge the properties of.
   * @param aResourcePath
   */
  public void merge(ResourceBundle resourceBundle, String aResourcePath) {
    if (resourceBundle != null) {
      Enumeration<String> keys = resourceBundle.getKeys();
      while (keys.hasMoreElements()) {
        String key = keys.nextElement();
        this.resources.put(key, resourceBundle.getObject(key));
      }
    }

    // Update the paths merged in this bundle
    mergedBundlePaths.append(aResourcePath);
    mergedBundlePaths.append(":");
  }

  /**
   * Overrides the super class implementation to return an object located in the merged bundles
   * 
   * @return An <code>Object</code> from the merged bundles
   */
  @Override
  public Object handleGetObject(String key) {
	  Objects.requireNonNull(key);
	  return this.resources.get(key);
  }

  /**
   * Overrides the super class implementation to return an enumeration of keys from all the merged bundles
   * 
   * @return An <code>Enumeration</code> of the keys across all the merged bundles.
   */
  @Override
  public Enumeration<String> getKeys() {
	  Vector<String> keys = new Vector<>(this.resources.keySet());
	  return keys.elements();
  }

  /**
   * Overrides the super class implementation to return the <code>Set</code> of keys from all merged bundles
   * 
   * @return A <code>Set</code> of keys obtained from all merged bundles
   */
  @Override
  protected Set<String> handleKeySet() {
    return this.resources.keySet();
  }

  /**
   * Overrides the super class implementation to check the existence of a key across all merged bundles
   * 
   * @return <code>true</code> if the key is present and <code>false</code> otherwise.
   */
  @Override
  public boolean containsKey(String key) {
    return this.resources.containsKey(key);
  }

  /**
   * Overrides the super class implementation to return the <code>Set</code> of keys from all merged bundles
   * 
   * @return A <code>Set</code> of keys obtained from all merged bundles
   */
  @Override
  public Set<String> keySet() {
    return this.resources.keySet();
  }
}
