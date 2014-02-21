package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.zend.php.zendserver.deployment.core.descriptor.ChangeEvent;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorChangeListener;
import org.zend.php.zendserver.deployment.core.descriptor.IModelContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;

public abstract class ModelObject implements IModelObject {

	private IModelContainer parent;

	protected List<IDescriptorChangeListener> listeners;

	protected Feature[] properties;
	private int[] offsets;

	public ModelObject(Feature[] properties) {
		this.properties = properties;
		offsets = new int[properties.length];
	}

	public void copy(IModelObject source) {
		throw new UnsupportedOperationException(MessageFormat.format(
				"Can't copy {0}", this)); //$NON-NLS-1$
	}

	public void set(Feature key, boolean value) {
		throw new IllegalArgumentException(MessageFormat.format(
				"Can't set feature {0} to value {1} in {2}", key, value, this)); //$NON-NLS-1$
	}

	public boolean getBoolean(Feature key) {
		throw new IllegalArgumentException(MessageFormat.format(
				"Can't get feature {0} from {1}", key, this)); //$NON-NLS-1$
	}

	public Feature[] getPropertyNames() {
		return properties;
	}

	public void addListener(IDescriptorChangeListener listener) {
		if (listeners == null) {
			listeners = Collections
					.synchronizedList(new ArrayList<IDescriptorChangeListener>());
		}
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public void removeListener(IDescriptorChangeListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	protected void fireChange(ChangeEvent event) {
		if (listeners != null) {
			synchronized (listeners) {
				if (listeners != null) {
					for (IDescriptorChangeListener l : listeners) {
						l.descriptorChanged(event);
					}
				}

				if (parent != null) {
					((ModelObject) parent).fireChange(event);
				}
			}
		}
	}

	protected void fireChange(IModelObject target, Feature key, int type,
			Object newValue, Object oldValue) {
		if ((newValue == null && oldValue != null)
				|| (newValue != null && oldValue == null)
				|| (newValue != null && !newValue.equals(oldValue))) {
			fireChange(new ChangeEvent(target, key, type, newValue, oldValue));
		}
	}

	protected void fireChange(Feature key, Object newValue, Object oldValue) {
		fireChange(this, key, IDescriptorChangeListener.SET, newValue, oldValue); // for
																					// basic
																					// properties,
																					// oldValue
																					// is
																					// always
																					// null
	}

	public void setParent(IModelContainer container) {
		this.parent = container;
	}

	public int getOffset(Feature f) {
		return offsets[Arrays.asList(properties).indexOf(f)];
	}

	public void setOffset(Feature f, int offset) {
		offsets[Arrays.asList(properties).indexOf(f)] = offset;
	}

	public boolean isChildrenFirst() {
		// by default add children after properties
		return false;
	}
}
