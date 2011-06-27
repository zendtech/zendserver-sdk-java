package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.zend.php.zendserver.deployment.core.descriptor.ChangeEvent;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorChangeListener;
import org.zend.php.zendserver.deployment.core.descriptor.IModelContainer;

/**
 * Wrapper for {@link List} that generates notifications on add/remove/change of any of list elements.
 *
 * @param <E>
 */
public class ObservableList<E> implements List<E> {

	private List<E> list;
	private IModelContainer container;
	private Feature feature;
	private List<IDescriptorChangeListener> listeners;
	
	public ObservableList(IModelContainer container, Feature feature, List<IDescriptorChangeListener> listeners) {
		this.list = new ArrayList<E>();
		this.feature = feature;
		this.listeners = listeners;
		this.container = container;
	}
	
	private void fireChange(int type, Object newValue, Object oldValue) {
		ChangeEvent event = new ChangeEvent(container, feature, type, newValue, oldValue);
		for (IDescriptorChangeListener l : listeners) {
			l.descriptorChanged(event);
		}
	}
	
	public boolean add(E e) {
		boolean added = list.add(e);
		if (added) {
			fireChange(IDescriptorChangeListener.ADD, e, null);
		}
		return added;
	}

	public void add(int index, E element) {
		list.add(index, element);
		fireChange(IDescriptorChangeListener.ADD, element, null);
	}

	public boolean addAll(Collection<? extends E> c) {
		boolean added = list.addAll(c);
		if (added) {
			for (E e: c) {
				fireChange(IDescriptorChangeListener.ADD, e, null);
			}
		}
		return added;
	}

	public boolean addAll(int index, Collection<? extends E> c) {
		boolean added = list.addAll(index, c);
		if (added) {
			for (E e : c) {
				fireChange(IDescriptorChangeListener.ADD, e, null);
			}
		}
		return added;
	}

	public void clear() {
		boolean cleared = list.isEmpty();
		Object[] toRemove = list.toArray();
		list.clear();
		if (cleared) {
			for (Object o : toRemove) {
				fireChange(IDescriptorChangeListener.REMOVE, null, o);
			}
		}
	}

	public boolean contains(Object o) {
		return list.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	public E get(int index) {
		return list.get(index);
	}

	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public Iterator<E> iterator() {
		return list.iterator();
	}

	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	public ListIterator<E> listIterator() {
		return list.listIterator();
	}

	public ListIterator<E> listIterator(int index) {
		return list.listIterator(index);
	}

	public boolean remove(Object o) {
		boolean removed = list.remove(o);
		if (removed) {
			fireChange(IDescriptorChangeListener.REMOVE, null, o);
		}
		return removed;
	}

	public E remove(int index) {
		E removed = list.remove(index);
		if (removed != null) {
			fireChange(IDescriptorChangeListener.REMOVE, null, removed);
		}
		return removed;
	}

	public boolean removeAll(Collection<?> c) {
		boolean removed = list.removeAll(c);
		if (removed) {
			for (Object o : c) {
				fireChange(IDescriptorChangeListener.REMOVE, null, o);
			}
		}
		return removed;
	}

	public boolean retainAll(Collection<?> c) {
		boolean changed = list.retainAll(c);
		if (changed) {
			fireChange(IDescriptorChangeListener.REMOVE, null, null); // TODO fix that
		}
		return changed;
	}

	public E set(int index, E element) {
		E prev =list.set(index, element);
		fireChange(IDescriptorChangeListener.SET, element, prev);
		return prev;
	}

	public int size() {
		return list.size();
	}

	public List<E> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	public Object[] toArray() {
		return list.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

}
