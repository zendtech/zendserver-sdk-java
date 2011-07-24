package org.zend.php.zendserver.deployment.ui.contentassist;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.zend.php.zendserver.deployment.ui.Activator;

public class ZendComponentsProvider implements IProposalProvider  {

	public static class ZSComponent {
		public String name;
		
		public ZSComponent(String name) {
			this.name = name;
		}
	}
	
	private static List<ZSComponent> components;
	
	public void init() {
		if (components == null) {
			components = new ArrayList<ZSComponent>();
			CSVLoader csvloader = new CSVLoader();
			InputStream in = getClass().getResourceAsStream("zscomponents.csv"); //$NON-NLS-1$
			
			String[][] csv;
			try {
				csv = csvloader.load(in);
			} catch (IOException e) {
				Activator.log(e);
				return;
			}
			
			for (int i = 0; i < csv.length; i++) {
				components.add(new ZSComponent(csv[i][0]));
			}
		}
	}

	public String[] getNames() {
		String[] names = new String[components.size()];
		int i = 0;
		for (ZSComponent dir : components) {
			names[i++] = dir.name;
		}
		
		return names;
	}
}
