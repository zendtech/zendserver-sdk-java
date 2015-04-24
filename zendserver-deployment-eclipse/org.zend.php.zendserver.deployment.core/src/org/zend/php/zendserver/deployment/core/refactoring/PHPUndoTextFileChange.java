package org.zend.php.zendserver.deployment.core.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.ltk.core.refactoring.ContentStamp;
import org.eclipse.ltk.core.refactoring.UndoTextFileChange;
import org.eclipse.text.edits.UndoEdit;

public class PHPUndoTextFileChange extends UndoTextFileChange{

	protected PHPUndoTextFileChange(String name, IFile file, UndoEdit undo,
			ContentStamp stamp, int saveMode) {
		super(name, file, undo, stamp, saveMode);
		// TODO Auto-generated constructor stub
	}

}
