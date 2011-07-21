package org.zend.php.zendserver.deployment.ui.contentassist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVLoader {

	public String[][] load(InputStream in) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		List<String[]> result = new ArrayList<String[]>();
		String line;
		while ((line = br.readLine()) != null) {
			result.add(line.split(",")); //$NON-NLS-1$
		}
		
		return result.toArray(new String[result.size()][]);
	}
}
