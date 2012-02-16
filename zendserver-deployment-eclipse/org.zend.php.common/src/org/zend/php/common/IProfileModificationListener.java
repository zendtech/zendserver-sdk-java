package org.zend.php.common;

import java.util.Collection;

import org.eclipse.core.runtime.IStatus;

/**
 * Notifies about profile modifications from within welcome page.
 */
public interface IProfileModificationListener {

	/**
	 * Fired before making changes.
	 * May be used to hold, break or affect the changes. This method is invoked synchronously, so it holds the progress.
	 * It will cancel the process when an IStatus.CANCEL is returned. Implementors may change the parameters to change profile modification. 
	 * 
	 * 
	 * @param setToAdd List of IU id's to install
	 * @param setToRemove List of IU id's to remove
	 * @return status should return IStatus.CANCEL to cancel the modification operation
	 */
	IStatus aboutToChange(Collection<String> setToAdd, Collection<String> setToRemove);

	/**
	 * Fired after making changes to profile.
	 * SetToAdd and setToRemove arguments contain the initial modification change request and the status contains the result.
	 * 
	 * @param setToAdd List of IU id's to install
	 * @param setToRemove List of IU id's to remove
	 * @param status Profile change operation result
	 */
	void profileChanged(Collection<String> setToAdd, Collection<String> setToRemove,
			IStatus status);

}
