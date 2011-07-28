package org.zend.php.zendserver.deployment.ui.targets;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.ContributionManager;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.part.ResourceTransfer;

public class DropTransferListener extends ViewerDropAdapter {

	private static final String DROP_MENU = "dropMenu"; //$NON-NLS-1$
	private static final String MENU_LOCATION_URI = "menu:org.zend.php.zendserver.deployment.ui.targets.TargetsViewer"; //$NON-NLS-1$
	private Viewer viewer;
	
	protected DropTransferListener(Viewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	@Override
	public boolean performDrop(Object data) {
		IResource[] resources = (IResource[]) data; // TODO pass resources and selected target to command
		
		Display display = Display.getDefault();
		final Menu menu = new Menu(viewer.getControl().getShell(), SWT.POP_UP);
		
		ContributionManager cm = new ContributionManager() {
			
			public void update(boolean force) {
				for (IContributionItem item : getItems()) {
					item.fill(menu, -1);
				}
			}
		};
		cm.add(new GroupMarker(DROP_MENU));
		IMenuService service = (IMenuService) PlatformUI.getWorkbench().getService(IMenuService.class);
		service.populateContributionManager(cm, MENU_LOCATION_URI);
		cm.update(false);
		
        menu.setLocation(getCurrentEvent().x, getCurrentEvent().y);
        menu.setVisible(true);
        while (!menu.isDisposed() && menu.isVisible()) {
          if (!display.readAndDispatch())
            display.sleep();
        }
        menu.dispose();
		
		return true;
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData type) {
		return ResourceTransfer.getInstance().isSupportedType(type);
	}

}
