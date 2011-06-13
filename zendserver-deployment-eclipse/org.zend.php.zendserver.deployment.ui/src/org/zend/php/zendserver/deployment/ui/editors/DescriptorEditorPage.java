package org.zend.php.zendserver.deployment.ui.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.zend.php.zendserver.deployment.core.internal.validation.ValidationStatus;

public abstract class DescriptorEditorPage extends FormPage {

	Map<String, TextField> fields = new HashMap<String, TextField>();
	
	public DescriptorEditorPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	@Override
	public void setActive(boolean active) {
		refresh();
	}
	
	public abstract void refresh();
	
	public void showStatuses(List<ValidationStatus> statuses) {
		Set<String> keys = fields.keySet();
		
		Map<String, ValidationStatus> toShow = new HashMap<String, ValidationStatus>();
		
		for (ValidationStatus s : statuses) {
			if (keys.contains(s.getProperty())) {
				toShow.put(s.getProperty(), s);
			}
		}
		
		for (TextField f : fields.values()) {
			String key = f.getKey();
			ValidationStatus status = toShow.get(key);
			if (status == null) {
				f.setErrorMessage(null);
			} else if (status.getSeverity() == ValidationStatus.ERROR){
				f.setErrorMessage(status.getMessage());
			} else if (status.getSeverity() == ValidationStatus.WARNING) {
				f.setWarningMessage(status.getMessage());
			}
		}
	}
	
	protected TextField addField(TextField field) {
		fields.put(field.getKey(), field);
		return field;
	}
}
