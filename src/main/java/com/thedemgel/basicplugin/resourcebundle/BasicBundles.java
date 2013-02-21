package com.thedemgel.basicplugin.resourcebundle;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.yaml.snakeyaml.Yaml;
import sun.util.ResourceBundleEnumeration;

public class BasicBundles {

	public ResourceBundle getBundle(String bundle, Locale loc) {

		ResourceBundle rb = ResourceBundle.getBundle(bundle, loc, new ResourceBundle.Control() {
			
			@Override
			public List<String> getFormats(String baseName) {
				if (baseName == null) {
					throw new NullPointerException();
				}
				return Arrays.asList("yml", "java.properties");
			}

			@Override
			public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
				if (baseName == null || locale == null
					|| format == null || loader == null) {
					throw new NullPointerException();
				}
				
				String bundleName = toBundleName("lang." + baseName, locale);
				
				ResourceBundle bundle = null;
				if (format.equals("yml")) {
					String resourceName = toResourceName(bundleName, format);
					InputStream stream = null;
					if (reload) {
						URL url = loader.getResource(resourceName);
						if (url != null) {
							URLConnection connection = url.openConnection();
							if (connection != null) {
								// Disable caches to get fresh data for
								// reloading.
								connection.setUseCaches(false);
								stream = connection.getInputStream();
							}
						}
					} else {
						stream = loader.getResourceAsStream(resourceName);
					}
					if (stream != null) {
						BufferedInputStream bis = new BufferedInputStream(stream);
						bundle = new YamlResourceBundle(bis);
						bis.close();
					}
				} else {
					bundle = super.newBundle(bundleName, locale, format, loader, reload);
				}
				return bundle;
			}
		});
		return rb;
	}

	private static class YamlResourceBundle extends ResourceBundle {

		private ConcurrentMap<String, String> lookup = new ConcurrentHashMap<String, String>();

		public YamlResourceBundle(InputStream stream) throws IOException {
			Yaml loader = new Yaml();
			for (Object item : ((LinkedHashMap) loader.load(stream)).entrySet()) {
				Entry<String, String> itemEntry = (Entry) item;
				lookup.put(itemEntry.getKey(), itemEntry.getValue());
			}
		}

		protected Object handleGetObject(String key) {
			return lookup.get(key);
		}

		public Enumeration<String> getKeys() {
			return new ResourceBundleEnumeration(lookup.keySet(), (parent != null) ? parent.getKeys() : null);
		}
	}
}
