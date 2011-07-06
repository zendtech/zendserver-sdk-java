package org.zend.php.zendserver.deployment.ui.contentassist;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PHPDirectivesProvider {

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
	
	private List<PHPDirective> directives = new ArrayList<PHPDirective>();
	
	public void initDirectives() {
		CSVLoader csvloader = new CSVLoader();
		InputStream in = getClass().getResourceAsStream("directives.csv");
		
		String[][] csv;
		try {
			csv = csvloader.load(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		for (int i = 0; i < csv.length; i++) {
			String since = csv[i].length >= 4 ? csv[i][3] : null;
			directives.add(new PHPDirective(csv[i][0], null, since));
		}
	}

	public String[] getDirectiveNames() {
		String[] names = new String[directives.size()];
		int i = 0;
		for (PHPDirective dir : directives) {
			names[i++] = dir.name;
		}
		
		return names;
	}
	
}
