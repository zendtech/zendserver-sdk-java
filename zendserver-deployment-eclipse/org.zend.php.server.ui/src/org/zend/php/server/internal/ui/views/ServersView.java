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

import org.eclipse.jface.action.Action;
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
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.IServersManagerListener;
import org.eclipse.php.internal.server.core.manager.ServerManagerEvent;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.eclipse.php.server.ui.types.IServerType;
import org.eclipse.php.server.ui.types.ServerTypesManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.zend.php.server.internal.ui.actions.ActionContributionsManager;
import org.zend.php.server.internal.ui.actions.AddServerAction;
import org.zend.php.server.internal.ui.actions.EditServerAction;
import org.zend.php.server.internal.ui.actions.RemoveServerAction;

/**
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class ServersView extends ViewPart implements IServersManagerListener {

	public static final String ID = "org.zend.php.server.ui.views.ServersView"; //$NON-NLS-1$

	private TreeViewer viewer;

	private DrillDownAdapter drillDownAdapter;
	private Action action1;
	private Action action2;

	private IAction editAction;
	private IAction removeAction;

	public ServersView() {
		ServersManager.addManagerListener(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setInput(ServersManager.getInstance());

		createActions();

		contributeToActionBars();
		hookDoubleClickAction();

		hookContextMenu();

		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(viewer.getControl(), ID);
	}

	@Override
	public void dispose() {
		ServersManager.removeManagerListener(this);
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

	public ISelectionProvider getSelectionProvider() {
		return viewer;
	}

	private void createActions() {
		editAction = new EditServerAction(getSelectionProvider());
		removeAction = new RemoveServerAction(getSelectionProvider());
	}

	private void contributeToActionBars() {
		IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager toolBarManager = actionBars.getToolBarManager();
		toolBarManager.add(new AddServerAction());
		toolBarManager.add(editAction);
		toolBarManager.add(removeAction);
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				editAction.run();
			}
		});
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

	// TODO to remove

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
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		List<Server> servers = getSelection(getSelectionProvider());
		if (servers != null && servers.size() > 0) {
			if (servers.size() == 1) {
				Server server = servers.get(0);
				IServerType type = ServerTypesManager.getInstance().getType(
						server);
				IAction[] actions = ActionContributionsManager.getInstance()
						.getActions(type, getSelectionProvider());
				if (actions != null) {
					for (IAction action : actions) {
						manager.add(action);
					}
				}
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

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

}