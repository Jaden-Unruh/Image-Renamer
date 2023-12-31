package us.akana.tools.photo_renamer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Used to get Strings from messages.properties
 * @author Jaden
 */
public class Messages {

	/**
	 * A bundle representing all of the Strings
	 */
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");

	/**
	 * Gets the specified string from {@link Messages#RESOURCE_BUNDLE}
	 * @param key the key of the String we need
	 * @return the string itself
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
	/**
	 * Gets the image extensions from ImageExtensions.dat file
	 * @return the extensions
	 */
	public static String[] getExtensions() {
		try (InputStream in = Messages.class.getResourceAsStream("/ImageExtensions.dat");
				BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			return reader.lines().toArray(String[]::new);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
