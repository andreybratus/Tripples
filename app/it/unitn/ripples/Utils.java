package it.unitn.ripples;

import java.io.Closeable;
import java.io.IOException;

/**
 * Class containing help utilities for the project
 *
 */

public class Utils {
/**
 * Method to safely close {@link Closeable} potentially leaking resource,
 * I rethrow set to true, the exception will be thrown, if false if the exception
 * occures it will be swallowed by this method.
 * 
 * @param closeable
 * @param rethrow
 * @throws IOException
 */
	
	public static void safeClose(Closeable closeable, boolean rethrow)
			throws IOException {

		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException ex) {
				if (rethrow) {
					throw ex;
				} else {
					ex.printStackTrace();
				}
			}
		}
	}


}
