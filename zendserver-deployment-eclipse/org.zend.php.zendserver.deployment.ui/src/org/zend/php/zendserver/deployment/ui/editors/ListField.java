package org.zend.php.zendserver.deployment.ui.editors;

import java.util.List;

import org.zend.php.zendserver.deployment.core.descriptor.IModelContainer;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;

public class ListField extends TextField {

	private IModelContainer target;
	
	public ListField(IModelContainer target, Feature key, String label) {
		super(target, key, label);
		this.target = target;
	}

	public void refresh() {
		isRefresh = true;
		try {
			List<Object> value = target != null ? target.getChildren(key) : null;
			StringBuilder sb = new StringBuilder();
			if (value != null) {
				for (Object o : value) {
					sb.append(o);
				}
			}
			text.setText(sb.toString());
		} finally {
			isRefresh = false;
		}
	}
}
