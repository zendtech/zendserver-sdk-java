/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkhelp.docs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

/**
 * simple template engine %loop% ... %end-loop% is used to loop over arrays of
 * strings
 * 
 * @author Roy, 2011
 */
public class HtmlTemplate {

	public static void writeTemplate(BufferedReader reader, PrintStream stream,
			String[][] blocks) throws IOException {

		String line = reader.readLine();

		while (line != null) {
			if (line.equals("%loop%")) {
				StringBuilder builder = new StringBuilder();
				line = "";
				while (line != null && !line.equals("%end-loop%")) {
					builder.append(line);
					line = reader.readLine();
				}
				for (String[] block : blocks) {
					String loop = builder.toString();
					int i = 0;
					for (String strings : block) {
						loop = loop.replace("{" + i++ + "}", strings);
					}
					stream.println(loop);
				}
			} else {
				stream.println(line);
			}
			line = reader.readLine();
		}
	}

}
