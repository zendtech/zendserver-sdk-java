package org.zend.php.zendserver.deployment.ui.contentassist;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.zend.php.zendserver.deployment.ui.Activator;

/**
 * PHP Directives from http://www.php.net/manual/en/ini.list.php
 *
 */
public class PHPDirectivesProvider implements IProposalProvider {

	public static class PHPDirective {
		public String name;
		public String type;
		public String phpVersion;
		
		public PHPDirective(String name, String type, String phpVersion) {
			this.name = name;
			this.type = type;
			this.phpVersion = phpVersion;
		}
	}
	
	private static List<PHPDirective> directives;
	
	/* (non-Javadoc)
	 * @see org.zend.php.zendserver.deployment.ui.contentassist.ProposalProvider#init()
	 */
	public void init() {
		if (directives == null) {
			directives = new ArrayList<PHPDirective>();
			CSVLoader csvloader = new CSVLoader();
			InputStream in = getClass().getResourceAsStream("directives.csv"); //$NON-NLS-1$
			
			String[][] csv;
			try {
				csv = csvloader.load(in);
			} catch (IOException e) {
				Activator.log(e);
				return;
			}
			
			for (int i = 0; i < csv.length; i++) {
				String since = csv[i].length >= 4 ? csv[i][3] : null;
				directives.add(new PHPDirective(csv[i][0], null, since));
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.zend.php.zendserver.deployment.ui.contentassist.ProposalProvider#getNames()
	 */
	public String[] getNames() {
		String[] names = new String[directives.size()];
		int i = 0;
		for (PHPDirective dir : directives) {
			names[i++] = dir.name;
		}
		
		return names;
	}
	
}
