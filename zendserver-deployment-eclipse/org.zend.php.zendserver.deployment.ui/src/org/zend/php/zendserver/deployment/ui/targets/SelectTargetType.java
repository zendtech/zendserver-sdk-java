package org.zend.php.zendserver.deployment.ui.targets;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.nebula.widgets.gallery.NoGroupRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.Messages;

public class SelectTargetType {
	
	public static final String PROP_TYPE = "type"; //$NON-NLS-1$

	public static final String PROP_DOUBLECLICK = "doubleclick"; //$NON-NLS-1$
	
	private static final String DATA_CLASSNAME = "className"; //$NON-NLS-1$

	
	private String type;
	
	private Contribution[] elements;
	
	private Gallery gallery;
	
	public SelectTargetType(Contribution[] elements) {
		this.elements = elements;
	}

	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}
	
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}
	
	public String getType() {
		return type;
	}
	
	public void create(Composite composite) {
		Label lbl = new Label(composite, SWT.NONE);
		lbl.setText(Messages.SelectTargetTypePage_SelectTargetFromList);
		
		gallery = new Gallery(composite, SWT.V_SCROLL|SWT.BORDER);
		gallery.setVirtualGroups(true);
		gallery.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		gallery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				GalleryItem gitem = (GalleryItem) e.item;
				String type = (gitem != null) ? (String)gitem.getData(DATA_CLASSNAME) : null;
				setType(type);
			}
		});
		gallery.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				changeSupport.firePropertyChange(PROP_DOUBLECLICK, null, null);
				
			}
		});
		
		GalleryItem top = new GalleryItem(gallery, SWT.NONE);
		top.setExpanded(true);
		gallery.setGroupRenderer(new NoGroupRenderer());
		
		for (int i = 0; i < elements.length; i++) {
			GalleryItem item1 = new GalleryItem(top, SWT.NONE);
			item1.setText(elements[i].name);
			item1.setImage(Activator.getDefault().getImage(elements[i].image));
			item1.setData(DATA_CLASSNAME, elements[i].control.getName());
			
		}
	}
	
	public void setType(String type) {
		String oldType = this.type;
		this.type = type;
		changeSupport.firePropertyChange(PROP_TYPE, oldType, type);
	}
	
	public void clearSelection() {
		this.type = null;
		gallery.deselectAll();
	}

	public int getSelectionCount() {
		return gallery.getSelectionCount();
	}
}