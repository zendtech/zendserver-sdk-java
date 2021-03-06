/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.zend.php.server.internal.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.php.internal.server.core.Activator;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.IServersManagerListener;
import org.eclipse.php.internal.server.core.manager.ServerManagerEvent;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.eclipse.php.server.core.types.IServerType;
import org.eclipse.php.server.core.types.ServerTypesManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.internal.help.WorkbenchHelpSystem;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.part.ViewPart;
import org.zend.php.server.internal.ui.actions.ActionContributionsManager;
import org.zend.php.server.internal.ui.actions.AddServerAction;
import org.zend.php.server.internal.ui.actions.EditServerAction;
import org.zend.php.server.internal.ui.actions.RemoveServerAction;
import org.zend.php.server.internal.ui.actions.SetDefaultServerAction;

/**
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class ServersView extends ViewPart implements IServersManagerListener,
		IPreferenceChangeListener {

	public static final String ID = "org.zend.php.server.ui.views.ServersView"; //$NON-NLS-1$

	private TreeViewer viewer;
	private IAction editAction;
	private IAction removeAction;
	private IAction setDefaultAction;

	public ServersView() {
		ServersManager.addManagerListener(this);
		IEclipsePreferences prefs = InstanceScope.INSTANCE
				.getNode(Activator.PLUGIN_ID);
		prefs.addPreferenceChangeListener(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider(viewer.getTree()
				.getFont()));
		viewer.setInput(ServersManager.getInstance());

		DropTransferListener transferListener = new DropTransferListener(viewer);
		transferListener.setExpandEnabled(false);
		ResourceTransfer resourceTransfer = ResourceTransfer.getInstance();
		Transfer[] transfers = new Transfer[] { resourceTransfer };
		viewer.addDropSupport(DND.DROP_COPY | DND.DROP_MOVE, transfers,
				transferListener);

		createActions();
		contributeToActionBars();
		hookDoubleClickAction();
		hookContextMenu();

		viewer.getControl().setData(WorkbenchHelpSystem.HELP_KEY, ID);
		viewer.getControl().addHelpListener(new HelpListener() {
			public void helpRequested(HelpEvent arg0) {
				org.eclipse.swt.program.Program.launch(ID);
			}
		});
	}

	@Override
	public void dispose() {
		ServersManager.removeManagerListener(this);
		IEclipsePreferences prefs = InstanceScope.INSTANCE
				.getNode(Activator.PLUGIN_ID);
		prefs.removePreferenceChangeListener(this);
		super.dispose();
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public void serverAdded(ServerManagerEvent event) {
		refreshViewer();
	}

	@Override
	public void serverRemoved(ServerManagerEvent event) {
		refreshViewer();
	}

	@Override
	public void serverModified(ServerManagerEvent event) {
		refreshViewer();
	}

	@Override
	public void preferenceChange(PreferenceChangeEvent event) {
		if (ServersManager.DEFAULT_SERVER_PREFERENCES_KEY
				.equals(event.getKey())) {
			refreshViewer();
		}
	}

	public ISelectionProvider getSelectionProvider() {
		return viewer;
	}

	private void createActions() {
		setDefaultAction = new SetDefaultServerAction(getSelectionProvider());
		editAction = new EditServerAction(getSelectionProvider());
		removeAction = new RemoveServerAction(getSelectionProvider());
	}

	private void contributeToActionBars() {
		IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager toolBarManager = actionBars.getToolBarManager();
		toolBarManager.add(new AddServerAction());
		toolBarManager.add(setDefaultAction);
		toolBarManager.add(editAction);
		toolBarManager.add(removeAction);
	}

	private void refreshViewer() {
		if (viewer == null || viewer.getTree() == null
				|| viewer.getTree().isDisposed()) {
			return;
		}
		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			public void run() {
				viewer.refresh();
			}
		});
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				editAction.run();
			}
		});
	}
	
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ServersView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
		
		// ZSTD-4911 - make the viewer selection available to avoid other
		// plugins contributing arbitrary items to this context menu
		getSite().setSelectionProvider(viewer);
	}

	private void fillContextMenu(IMenuManager manager) {
		List<Server> servers = getSelection(getSelectionProvider());
		if (servers != null && servers.size() > 0) {
			if (servers.size() == 1) {
				Server server = servers.get(0);
				IServerType type = ServerTypesManager.getInstance().getType(
						server);
				IAction[] actions = ActionContributionsManager.getInstance()
						.getActions(type, server);
				if (actions != null) {
					for (IAction action : actions) {
						manager.add(action);
					}
				}
				manager.add(setDefaultAction);
				manager.add(editAction);
			}
			manager.add(removeAction);
			manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		}
	}

	private List<Server> getSelection(ISelectionProvider provider) {
		List<Server> result = new ArrayList<Server>();
		if (provider != null) {
			ISelection selection = provider.getSelection();
			if (selection != null && !selection.isEmpty()) {
				List<?> list = ((IStructuredSelection) selection).toList();
				for (Object object : list) {
					if (object instanceof Server) {
						result.add((Server) object);
					}
				}
			}
		}
		return result;
	}

}