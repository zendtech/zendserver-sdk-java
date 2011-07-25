package org.zend.php.zendserver.deployment.ui.editors;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;
import org.zend.php.zendserver.deployment.ui.editors.DescriptorEditorPage.FormDecoration;

/**
 * Contains editor fields and handles finding and updating their decorations
 *
 */
public class FieldsContainer {

	private Map<Feature, EditorField> fields = new LinkedHashMap<Feature, EditorField>();

	public EditorField add(EditorField field) {
		fields.put(field.getKey(), field);
		return field;
	}

	public void refreshMarkers(Map<Feature, FormDecoration> toShow,
			List<Feature> toRemove) {
		if (toRemove != null) {
			for (Feature feature : toRemove) {
				EditorField field = fields.get(feature);
				if (field != null) {
					field.setDecoration(null);
				}
			}
		}
		
		if (toShow != null) {
			for (Map.Entry<Feature, FormDecoration> entry : toShow.entrySet()) {
				FormDecoration status = entry.getValue();
				EditorField field = fields.get(entry.getKey());
				if (field != null) {
					field.setDecoration(status);
				}
			}
		}
	}

	public Map<Integer, Feature> getFeatureIds() {
		Set<Feature> keyset = fields.keySet();
		Map<Integer, Feature> featureIds = new HashMap<Integer, Feature>();
		for (Feature f : keyset) {
			featureIds.put(f.id, f);
		}
		
		return featureIds;
	}

	public Collection<Feature> keySet() {
		return fields.keySet();
	}

	public void refresh() {
		for (EditorField e : fields.values()) {
			e.refresh();
		}
	}

	public Collection<EditorField> fields() {
		return fields.values();
	}

	public void setInput(IModelObject input) {
		for (EditorField e : fields.values()) {
			e.setInput(input);
		}
	}
	
}
