package org.zend.php.zendserver.deployment.ui.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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

	private List<EditorField> fields = new ArrayList<EditorField>();
	
	public EditorField add(EditorField field) {
		fields.add(field);
		return field;
	}

	public void refreshMarkers(Map<Feature, FormDecoration> toShow,
			List<Feature> toRemove) {
		if (toRemove != null) {
			for (Feature feature : toRemove) {
				EditorField[] fields = getFields(feature);
				for (EditorField field : fields) {
					field.setDecoration(null);
				}
			}
		}
		
		if (toShow != null) {
			for (Map.Entry<Feature, FormDecoration> entry : toShow.entrySet()) {
				FormDecoration status = entry.getValue();
				EditorField[] fields = getFields(entry.getKey());
				for (EditorField field : fields) {
					field.setDecoration(status);
				}
			}
		}
	}

	private EditorField[] getFields(Feature key) {
		List<EditorField> out = new ArrayList<EditorField>();
		for (EditorField f : fields) {
			if (key.equals(f.getKey())) {
				out.add(f);
			}
		}
		return out.toArray(new EditorField[out.size()]);
	}
	
	public Map<Integer, Feature> getFeatureIds() {
		Collection<Feature> keyset = keySet();
		Map<Integer, Feature> featureIds = new HashMap<Integer, Feature>();
		for (Feature f : keyset) {
			featureIds.put(f.id, f);
		}
		
		return featureIds;
	}

	public Collection<Feature> keySet() {
		Set<Feature> out = new HashSet<Feature>();
		for (EditorField f : fields) {
			out.add(f.getKey());
		}
		return out;
	}

	public void refresh() {
		for (EditorField e : fields) {
			e.refresh();
		}
	}

	public Collection<EditorField> fields() {
		return fields;
	}

	public void setInput(IModelObject input) {
		for (EditorField e : fields) {
			e.setInput(input);
		}
	}
	
}
