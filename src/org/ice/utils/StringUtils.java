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
package org.ice.utils;

public class StringUtils {

	public static String capitalize(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		}
		return new StringBuffer(strLen)
				.append(Character.toTitleCase(str.charAt(0)))
				.append(str.substring(1)).toString();
	}
	
	public static String ucwords(String str, char delimiter)	{
		return ucwords(str, delimiter, false);
	}

	public static String ucwords(String str, char delimiter, boolean firstCharUppercase)	{
		if (str == null || str.length() == 0) {
			return str;
		}
		int strLen = str.length();
		StringBuffer buffer = new StringBuffer(strLen);
			
		boolean capitalizeNext = firstCharUppercase;
		for (int i = 0; i < strLen; i++) {
			char ch = str.charAt(i);
			if(ch == delimiter) {
				buffer.append(ch);
				capitalizeNext = true;
			} else if (capitalizeNext) {
				buffer.append(Character.toTitleCase(ch));
				capitalizeNext = false;
			} else {
				buffer.append(ch);
			}
		}
		return buffer.toString();
	}
	
	public static String strip(String str, String stripChars)	{
		if (isEmpty(str)) return str;
		str = stripStart(str, stripChars);
		return stripEnd(str, stripChars);
	}
	
	public static String stripStart(String str, String stripChars)	{
		if (isEmpty(str)) return str;
		int strLen = str.length();
		
		int start = 0;
		if (stripChars == null) {
			while ((start != strLen) && Character.isWhitespace(str.charAt(start))) {
				start++;
			}
		} else if (stripChars.length() == 0) {
			return str;
		} else {
			while ((start != strLen) && (stripChars.indexOf(str.charAt(start)) != -1)) {
				start++;
			}
		}
		return str.substring(start);
	}
	
	public static String stripEnd(String str, String stripChars)	{
		if (isEmpty(str)) return str;
		int end = str.length();
		
		if (stripChars == null) {
			while ((end != 0) && Character.isWhitespace(str.charAt(end - 1))) {
				end--;
			}
		} else if (stripChars.length() == 0) {
			return str;
		} else {
			while ((end != 0) && (stripChars.indexOf(str.charAt(end - 1)) != -1)) {
				end--;
			}
		}
		return str.substring(0, end);
	}
	
	public static boolean isEmpty(String str)	{
		return (str == null || str.length() == 0);
	}
}
