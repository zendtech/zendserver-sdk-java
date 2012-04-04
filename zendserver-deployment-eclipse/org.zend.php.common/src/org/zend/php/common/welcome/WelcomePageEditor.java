package org.zend.php.common.welcome;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.browser.WebBrowserEditor;
import org.eclipse.ui.internal.browser.WebBrowserEditorInput;
import org.osgi.service.prefs.Preferences;
import org.zend.php.common.Activator;
import org.zend.php.common.ZendCatalogViewer;


public class WelcomePageEditor extends WebBrowserEditor {
	private static final String OUTLINE_VIEW = "org.eclipse.ui.views.ContentOutline";

	public static final String EDITOR_ID = "org.zend.customization.welcome.welcomePageEditor"; //$NON-NLS-1$

	private static final String IS_FIRST_WELCOME_STARTUP = "isFirstWelcomeStartup"; //$NON-NLS-1$

	private ZendCatalogViewer viewer;

	public WelcomePageEditor() {
		// TODO Auto-generated constructor stub
	}

	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
	}

	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void createPartControl(Composite parent) {
		FillLayout fl = (FillLayout) parent.getLayout();
		fl.marginHeight = 0;
		fl.marginWidth = 0;
		fl.spacing = 0;
		parent.setLayout(fl);
		final Composite editor = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(3, true);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		editor.setLayout(layout);
		editor.setBackgroundMode(SWT.INHERIT_FORCE);
		Color c = new Color(Display.getDefault(), 95, 166, 48);
		editor.setBackground(c);
		createWelcomePageContent(editor);
		createFeatureManager(editor);
		
		if (getEditorInput() instanceof WelcomePageEditorInput) {
			WelcomePageEditorInput welcomeInput = (WelcomePageEditorInput)getEditorInput();
			welcomeInput.initFeaturesViewer(viewer);
			viewer.setStatusHandler(welcomeInput.getStatusHandler());
		}
		
		Display.getCurrent().asyncExec(new Runnable() {
			public void run() {
				if (!editor.isDisposed()) {
					viewer.updateCatalog();
				}
			}
		});
		//PlatformUI.getWorkbench().getHelpSystem()
			//	.setHelp(parent, IStudioHelpContextIds.WELCOME_PAGE);
	}

	private void createWelcomePageContent(Composite parent) {
		Composite composite = new Composite(parent, SWT.TRANSPARENT);
		GridLayout layout = new GridLayout(1, true);
		composite.setLayout(layout);
		GridData td = new GridData(SWT.FILL, SWT.FILL, true, true);
		td.horizontalSpan = 2;
		composite.setLayoutData(td);
		composite.setBackgroundMode(SWT.INHERIT_FORCE);
		Browser br = new Browser(composite, SWT.NONE);
		br.setLayoutData(new GridData(GridData.FILL_BOTH));
		br.setUrl(((WebBrowserEditorInput) getEditorInput()).getURL()
				.toString());

		final WelcomeLinkListenerManager m = new WelcomeLinkListenerManager();
		br.addStatusTextListener(new StatusTextListener() {

			public void changed(StatusTextEvent event) {
				if (event.text != null) {
					final Runnable runnable = m.getRunnable(event.text);

					if (runnable != null) {
						Display.getDefault().asyncExec(runnable);
					}
				}
			}
		});
		
		hideWelcome();
	}

	private void createFeatureManager(Composite parent) {
		IRunnableContext context = new ProgressMonitorDialog(parent.getShell());
		IShellProvider provider = new IShellProvider() {
			public Shell getShell() {
				return Activator.getDefault().getWorkbench()
						.getActiveWorkbenchWindow().getShell();
			}
		};
		final Composite viewerComposite = new Composite(parent, SWT.TRANSPARENT);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 1;
		viewerComposite.setLayoutData(gd);
		GridLayout gdL = new GridLayout(1, true);
		gdL.verticalSpacing = 0;
		gdL.horizontalSpacing = 0;
		viewerComposite.setLayout(gdL);
		viewer = new ZendCatalogViewer(provider,
				context);
		viewer.createControl(viewerComposite);
		//PlatformUI
			//	.getWorkbench()
				//.getHelpSystem()
				//.setHelp(
					//	viewerComposite,
						//IStudioHelpContextIds.CUSTOMIZING_ZEND_STUDIO_USING_THE_WELCOME_PAGE);
	}

	public void setFocus() {

	}

	public void dispose() {
		final Preferences preferences = ConfigurationScope.INSTANCE
				.getNode(Activator.PLUGIN_ID);

		if (preferences.getBoolean(IS_FIRST_WELCOME_STARTUP, true)) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					reopenOutlineView();
					preferences.putBoolean(IS_FIRST_WELCOME_STARTUP, false);
				}
			});
		}
		super.dispose();
	}
	
	private void hideWelcome() {
		IWorkbench wb = null;
		try {
			wb = PlatformUI.getWorkbench();
		} catch (IllegalArgumentException ex) {
			return;
		}
		
		IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
		if (window == null) {
			return;
		}
		IWorkbenchPage page = window.getActivePage();
		if (page == null) {
			return;
		}
		
		IViewReference outlineView = page
				.findViewReference(OUTLINE_VIEW);
		
		if (outlineView != null) {
			page.hideView(outlineView);
		}
	}

	protected void reopenOutlineView() {
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (window == null) {
			return;
		}
		IWorkbenchPage page = window.getActivePage();
		if (page == null) {
			return;
		}
		
		try {
			page.showView(OUTLINE_VIEW);
		} catch (PartInitException e) {
			Activator.log(e);
		}
	}

}
