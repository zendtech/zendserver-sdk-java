package org.zend.php.zendserver.deployment.ui.contentassist;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.zend.php.zendserver.deployment.ui.Activator;

/**
 * http://files.zend.com/help/Zend-Server-Community-Edition/zend_server_ce_php_5.3_extensions.htm
 *
 */
public class PHPExtensionsProvider implements IProposalProvider {

	public static class PHPExtension {
		public String name;
		public String description;
		
		public PHPExtension(String name, String description) {
			this.name = name;
			this.description = description;
		}
	}
	
	private static List<PHPExtension> directives;
	
	public void init() {
		if (directives == null) {
			directives = new ArrayList<PHPExtension>();
			CSVLoader csvloader = new CSVLoader();
			InputStream in = getClass().getResourceAsStream("phpextensions.csv"); //$NON-NLS-1$
			
			String[][] csv;
			try {
				csv = csvloader.load(in);
			} catch (IOException e) {
				Activator.log(e);
				return;
			}
			
			for (int i = 0; i < csv.length; i++) {
				directives.add(new PHPExtension(csv[i][0], csv[i][1]));
			}
		}
	}

	public String[] getNames() {
		String[] names = new String[directives.size()];
		int i = 0;
		for (PHPExtension dir : directives) {
			names[i++] = dir.name;
		}
		
		return names;
	}
}
