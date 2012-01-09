/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
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
