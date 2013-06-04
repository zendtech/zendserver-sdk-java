/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.library.internal.ui.preferences;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.zend.php.library.core.composer.ComposerService;
import org.zend.php.library.internal.core.PackagistService;
import org.zend.php.library.internal.core.RepositoryPackage;

/**
 * @author Wojciech Galanciak, 2013
 *
 */
public class SearchLibraryPage extends WizardPage {
	
	private class TableLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			return element.toString();
		}
	}

	private static class ContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			return new Object[0];
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	private class RepositoryPages {
		private String query;
		private int pagesNumber;
		private int currentPage;

		private List<RepositoryPackage> packages;

		private RepositoryPages(String query) {
			super();
			this.query = query;
		}

		public boolean hasNext() {
			return currentPage < pagesNumber;
		}

		public boolean hasPrevious() {
			return currentPage > 1;
		}

		public void nextPage() {
			currentPage++;
			packages = PackagistService.getPackages(query, currentPage);
		}

		public void previousPage() {
			currentPage--;
			packages = PackagistService.getPackages(query, currentPage);
		}

		public void init() {
			int total = PackagistService.getPagesSize(query);
			nextPage();
			if (packages.size() > 0) {
				pagesNumber = total / packages.size();
				if (pagesNumber % packages.size() != 0) {
					pagesNumber++;
				}
			} else {
				pagesNumber = 0;
			}
		}

		public List<RepositoryPackage> getPackages() {
			return packages;
		}

		public String getLabel() {
			return (pagesNumber > 0 ? String.valueOf(currentPage) : 0) + "/" //$NON-NLS-1$
					+ pagesNumber;
		}

	}

	private Text searchText;
	private Label pagesLabel;
	private Table table;
	private TableViewer tableViewer;

	private Button nextButton;
	private Button previousButton;

	private RepositoryPages pages;
	private RepositoryPackage selectedPackage;
	
	/**
	 * Create the wizard.
	 */
	public SearchLibraryPage() {
		super("wizardPage"); //$NON-NLS-1$
		setPageComplete(false);
		setTitle(Messages.SearchLibraryPage_2);
		setDescription(Messages.SearchLibraryPage_3);
	}

	public RepositoryPackage getSelection() {
		return selectedPackage;
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		searchText = new Text(container, SWT.BORDER);
		searchText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		searchText.addFocusListener(new FocusAdapter() {

			public void focusGained(FocusEvent e) {
				table.setSelection(-1);
				setPageComplete(false);
			}
		});
		searchText.addKeyListener(new KeyAdapter() {

			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.CR) {
					doSearch();
				}
			}
		});

		Button searchButton = new Button(container, SWT.NONE);
		searchButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false,
				false, 1, 1));
		searchButton.setText(Messages.SearchLibraryPage_4);
		searchButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ComposerService.search("monolog/monolog", null); //$NON-NLS-1$
				doSearch();
			}
		});

		tableViewer = new TableViewer(container, SWT.BORDER
				| SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		TableViewerColumn nameColumn = new TableViewerColumn(tableViewer,
				SWT.NONE);
		nameColumn.getColumn().setWidth(180);
		nameColumn.getColumn().setText(Messages.SearchLibraryPage_6);
		TableViewerColumn descColumn = new TableViewerColumn(tableViewer,
				SWT.NONE);
		descColumn.getColumn().setWidth(250);
		descColumn.getColumn().setText(Messages.SearchLibraryPage_7);
		tableViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {

					public void selectionChanged(SelectionChangedEvent event) {
						selectedPackage = (RepositoryPackage) ((StructuredSelection) event
								.getSelection()).getFirstElement();
						setPageComplete(validatePage());
					}
				});
		tableViewer.setLabelProvider(new TableLabelProvider() {
			public String getColumnText(Object element, int columnIndex) {
				if (element instanceof RepositoryPackage) {
					if (columnIndex == 0) {
						return ((RepositoryPackage) element).getName();
					} else if (columnIndex == 1) {
						return ((RepositoryPackage) element).getDescription();
					}
				}
				return super.getColumnText(element, columnIndex);
			}

			public String getText(Object element) {
				if (element instanceof RepositoryPackage) {
					return ((RepositoryPackage) element).getName();
				}
				return ""; //$NON-NLS-1$
			}
		});
		tableViewer.setContentProvider(new ContentProvider() {
			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof List<?>) {
					return ((List<?>) inputElement).toArray(new Object[0]);
				}
				return new Object[0];
			}
		});
		Composite pagesNavigation = new Composite(container, SWT.NONE);
		pagesNavigation.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
				false, false, 2, 1));
		GridLayout pageNavigationLayout = new GridLayout(3, false);
		pageNavigationLayout.verticalSpacing = 0;
		pageNavigationLayout.marginHeight = 0;
		pageNavigationLayout.horizontalSpacing = 0;
		pagesNavigation.setLayout(pageNavigationLayout);

		previousButton = new Button(pagesNavigation, SWT.NONE);
		GridData gd_previousButton = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_previousButton.widthHint = 100;
		previousButton.setLayoutData(gd_previousButton);
		previousButton.setText(Messages.SearchLibraryPage_9);
		previousButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					if (pages.hasPrevious()) {
						doSwitchPrevious();
					}
				} catch (InvocationTargetException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		pagesLabel = new Label(pagesNavigation, SWT.CENTER);
		GridData gd_pagesLabel = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_pagesLabel.widthHint = 60;
		gd_pagesLabel.minimumWidth = 60;
		pagesLabel.setLayoutData(gd_pagesLabel);
		pagesLabel.setText("0/0"); //$NON-NLS-1$

		nextButton = new Button(pagesNavigation, SWT.NONE);
		GridData gd_nextButton = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_nextButton.widthHint = 100;
		nextButton.setLayoutData(gd_nextButton);
		nextButton.setText(Messages.SearchLibraryPage_11);
		nextButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					if (pages.hasNext()) {
						doSwitchNext();
					}
				} catch (InvocationTargetException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		setControl(container);
	}

	private boolean validatePage() {
		return tableViewer.getSelection() != null;
	}

	private void doSwitchPrevious() throws InvocationTargetException,
			InterruptedException {
		getContainer().run(true, false, new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				monitor.beginTask("Switching page...", IProgressMonitor.UNKNOWN); //$NON-NLS-1$
				pages.previousPage();
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						tableViewer.setInput(pages.getPackages());
						if (!pages.hasPrevious()) {
							previousButton.setEnabled(false);
						}
						nextButton.setEnabled(true);
						pagesLabel.setText(pages.getLabel());
					}
				});
			}
		});
	}

	private void doSwitchNext() throws InvocationTargetException,
			InterruptedException {
		getContainer().run(true, false, new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				monitor.beginTask("Switching page...", IProgressMonitor.UNKNOWN); //$NON-NLS-1$
				pages.nextPage();
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						tableViewer.setInput(pages.getPackages());
						if (!pages.hasNext()) {
							nextButton.setEnabled(false);
						}
						previousButton.setEnabled(true);
						pagesLabel.setText(pages.getLabel());
					}
				});
			}
		});
	}

	private void doSearch() {
		try {
			pages = new RepositoryPages(searchText.getText());
			getContainer().run(true, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					monitor.beginTask("Searching...", IProgressMonitor.UNKNOWN); //$NON-NLS-1$
					pages.init();
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							tableViewer.setInput(pages.getPackages());
							if (!pages.hasNext()) {
								nextButton.setEnabled(false);
							}
							previousButton.setEnabled(false);
							pagesLabel.setText(pages.getLabel());
						}
					});
				}
			});
		} catch (InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
