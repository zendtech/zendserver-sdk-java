package org.zend.php.common;

import java.util.List;

import org.eclipse.core.runtime.IStatus;

public interface IProfileModificationListener {

	void profileChanged(List<String> added, List<String> removed, IStatus status);

}
