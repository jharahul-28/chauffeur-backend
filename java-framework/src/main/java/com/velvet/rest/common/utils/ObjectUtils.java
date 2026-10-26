package com.velvet.rest.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

/**
 * Utility Class ObjectUtils.
 *
 * @author Mohit
 * @version $Id: $Id
 */
@SuppressWarnings("rawtypes")
public class ObjectUtils {

	/**
	 * Instantiates a new object utils.
	 */
	private ObjectUtils() {
	}

	/**
	 * Deep copy.
	 *
	 * @param o the o
	 * @return the object
	 */
	public static Object deepCopy(Object o) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(o);

			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			// do nothing
		}

		return null;
	}

	/**
	 * <p>
	 * toByteArray.
	 * </p>
	 *
	 * @param o a {@link java.lang.Object} object.
	 * @return an array of {@link byte} objects.
	 */
	public static byte[] toByteArray(Object o) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(o);

			return baos.toByteArray();
		} catch (Exception e) {
			// do nothing
		}

		return null;
	}

	/**
	 * <p>
	 * toObject.
	 * </p>
	 *
	 * @param bytes an array of {@link byte} objects.
	 * @return a {@link java.lang.Object} object.
	 */
	public static Object toObject(byte[] bytes) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			// do nothing
		}

		return null;
	}

	/**
	 * Checks for data.
	 *
	 * @param o the o
	 * @return a boolean.
	 */
	public static boolean hasData(Object o) {
		try {
			if (o != null) {
				if (o instanceof List) {
					if (((List) o).isEmpty())
						return false;
					else
						return true;
				} else if (o instanceof Map) {
					if (((Map) o).isEmpty())
						return false;
					else
						return true;
				} else if (o instanceof Long) {
					if (((Long) o) > 0)
						return true;
					else
						return false;
				} else if (o instanceof Integer) {
					if (((Integer) o) > 0)
						return true;
					else
						return false;
				} else if (o instanceof Double) {
					if (((Double) o) > 0)
						return true;
					else
						return false;
				} else if (o instanceof Object[]) {
					if (((Object[]) o).length > 0)
						return true;
					else
						return false;
				} else if (o instanceof String) {
					if (((String) o).isEmpty())
						return false;
					else
						return true;
				}

				else {
					return true;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
}
