package org.zend.php.zendserver.deployment.ui.targets;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.nebula.widgets.gallery.NoGroupRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.sdklib.target.IZendTarget;

public class SelectTargetTypePage extends WizardPage {

	public static final String PROP_TYPE = "type"; //$NON-NLS-1$

	public static final String PROP_DOUBLECLICK = "doubleclick"; //$NON-NLS-1$
	
	private static final String DATA_CLASSNAME = "className"; //$NON-NLS-1$

	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	protected IZendTarget result;

	private String type;

	private Gallery gallery;

	private static class Contribution {
		
		String name;
		
		String image;
		
		String wizardPageClassName;
		
		public Contribution(String name, String image, String wizardPageClassName) {
			this.name = name;
			this.image = image;
			this.wizardPageClassName = wizardPageClassName;
		}
	}
	
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}
	
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}
	
	public void setType(String type) {
		String oldType = this.type;
		this.type = type;
		changeSupport.firePropertyChange(PROP_TYPE, oldType, type);
	}
	public String getType() {
		return type;
	}
	
	protected SelectTargetTypePage() {
		super(Messages.SelectTargetTypePage_SelectTargetType);
		setTitle(Messages.SelectTargetTypePage_AddTarget);
		setDescription(Messages.SelectTargetTypePage_SelectTargetType);
		setImageDescriptor(Activator.getImageDescriptor(Activator.IMAGE_WIZBAN_DEP));
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(1, false));
		
		Label lbl = new Label(composite, SWT.NONE);
		lbl.setText(Messages.SelectTargetTypePage_SelectTargetFromList);
		
		gallery = new Gallery(composite, SWT.V_SCROLL|SWT.BORDER);
		gallery.setVirtualGroups(true);
		gallery.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		gallery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(e.item != null);
				
				if (e.item == null) {
					return; // ignore empty selection
				}
				
				GalleryItem gitem = (GalleryItem) e.item;
				setType((String)gitem.getData(DATA_CLASSNAME));
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
		
		Contribution[] elements = getElements();
		
		for (int i = 0; i < elements.length; i++) {
			GalleryItem item1 = new GalleryItem(top, SWT.NONE);
			item1.setText(elements[i].name);
			item1.setImage(Activator.getDefault().getImage(elements[i].image));
			item1.setData(DATA_CLASSNAME, elements[i].wizardPageClassName);
			
		}
		
		setControl(composite);
		setPageComplete(gallery.getSelectionCount() > 0);
	}

	private Contribution[] getElements() {
		return new Contribution[] {
				new Contribution(Messages.SelectTargetTypePage_ZendServer, Activator.IMAGE_ZEND, ZendTargetDetailsComposite.class.getName()),
				new Contribution(Messages.SelectTargetTypePage_DevCloud, Activator.IMAGE_CLOUD, DevCloudDetailsComposite.class.getName()),
		};
	}
}
