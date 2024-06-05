package org.m_tag.jfind.books;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * config for sqlite3 dbs.
 */
public class Config {
  private static final String BUNDLE_NAME = "config"; //$NON-NLS-1$

  private static ResourceBundle bundle = null;

  private Config() {}

  /**
   * get value from config(Environment variable or properties file).
   *
   * @param key key
   * @return value from config, null if not exists the value.
   */
  public static String getString(String key) {
    String value = System.getenv(key);
    if (value != null) {
      return value;
    }
    if (bundle == null) {
      try {
        bundle = ResourceBundle.getBundle(BUNDLE_NAME);
      } catch (MissingResourceException e) {
        return null;
      }
    }
    try {
      return bundle.getString(key);
    } catch (MissingResourceException e) {
      return null;
    }
  }
}
