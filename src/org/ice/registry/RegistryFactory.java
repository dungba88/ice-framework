package org.ice.registry;

import org.ice.exception.IceException;
import org.ice.utils.FieldUtils;

public class RegistryFactory {
	
	private static IRegistry registry;

	public static void setupRegistry(String registryClass) throws IceException {
		try {
			IRegistry registry = (IRegistry) FieldUtils.loadClass(registryClass);
			RegistryFactory.registry = registry;
		} catch (ClassCastException ex) {
			throw new IceException("Invalid registry class name: "+registryClass);
		} catch (Exception ex) {
			throw new IceException(ex.toString());
		}
	}
	
	public static IRegistry getRegistry() {
		return registry;
	}
}
