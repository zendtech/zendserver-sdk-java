package org.zend.php.zendserver.deployment.ui.editors.propertiestext;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.spelling.SpellingReconcileStrategy;
import org.eclipse.ui.texteditor.spelling.SpellingService;

/**
 * Configuration for a source viewer which shows a properties file.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 */
public class PropertiesFileSourceViewerConfiguration extends
		TextSourceViewerConfiguration {

	private class SingleTokenJavaScanner extends AbstractPropertyScanner {

		private String[] fProperty;

		public SingleTokenJavaScanner(String property) {
			super(fColorManager, fPreferenceStore);
			fProperty = new String[] { property };
			initialize();
		}

		/*
		 * @see AbstractJavaScanner#getTokenProperties()
		 */
		@Override
		protected String[] getTokenProperties() {
			return fProperty;
		}

		/*
		 * @see AbstractJavaScanner#createRules()
		 */
		@Override
		protected List<IRule> createRules() {
			setDefaultReturnToken(getToken(fProperty[0]));
			return null;
		}
	}

	private class PropertyValueScanner extends AbstractPropertyScanner {

		public class AssignmentDetector implements IWordDetector {

			/*
			 * @see IWordDetector#isWordStart
			 */
			public boolean isWordStart(char c) {
				if ('=' != c && ':' != c || fDocument == null)
					return false;

				try {
					// check whether it is the first '=' in the logical line

					int i = fOffset - 2;
					while (Character.isWhitespace(fDocument.getChar(i))) {
						i--;
					}

					ITypedRegion partition = null;
					if (fDocument instanceof IDocumentExtension3)
						partition = ((IDocumentExtension3) fDocument)
								.getPartition(
										IPropertiesFilePartitions.PROPERTIES_FILE_PARTITIONING,
										i, false);
					return partition != null
							&& IDocument.DEFAULT_CONTENT_TYPE.equals(partition
									.getType());
				} catch (BadLocationException ex) {
					return false;
				} catch (BadPartitioningException e) {
					return false;
				}
			}

			/*
			 * @see IWordDetector#isWordPart
			 */
			public boolean isWordPart(char c) {
				return false;
			}
		}

		private String[] fgTokenProperties = { PROPERTIES_FILE_COLORING_VALUE,
				PROPERTIES_FILE_COLORING_ARGUMENT,
				PROPERTIES_FILE_COLORING_ASSIGNMENT };

		/**
		 * Creates a property value code scanner
		 */
		public PropertyValueScanner() {
			super(fColorManager, fPreferenceStore);
			initialize();
		}

		/*
		 * @see
		 * org.eclipse.jdt.internal.ui.text.AbstractJavaScanner#getTokenProperties
		 * ()
		 */
		@Override
		protected String[] getTokenProperties() {
			return fgTokenProperties;
		}

		/*
		 * @see
		 * org.eclipse.jdt.internal.ui.text.AbstractJavaScanner#createRules()
		 */
		@Override
		protected List<IRule> createRules() {
			setDefaultReturnToken(getToken(PROPERTIES_FILE_COLORING_VALUE));
			List<IRule> rules = new ArrayList<IRule>();

			// Add rule for arguments.
			IToken token = getToken(PROPERTIES_FILE_COLORING_ARGUMENT);
			rules.add(new ArgumentRule(token));

			// Add word rule for assignment operator.
			token = getToken(PROPERTIES_FILE_COLORING_ASSIGNMENT);
			WordRule wordRule = new WordRule(new AssignmentDetector(), token);
			rules.add(wordRule);

			// Add generic whitespace rule.
			rules.add(new WhitespaceRule(new IWhitespaceDetector() {

				public boolean isWhitespace(char c) {
					return Character.isWhitespace(c);
				}

			}));

			return rules;
		}
	}

	public static final String PROPERTIES_FILE_COLORING_KEY = "pf_coloring_key"; //$NON-NLS-1$
	public static final String PROPERTIES_FILE_COLORING_COMMENT = "pf_coloring_comment"; //$NON-NLS-1$
	public static final String PROPERTIES_FILE_COLORING_VALUE = "pf_coloring_value"; //$NON-NLS-1$
	public static final String PROPERTIES_FILE_COLORING_ASSIGNMENT = "pf_coloring_assignment"; //$NON-NLS-1$
	public static final String PROPERTIES_FILE_COLORING_ARGUMENT = "pf_coloring_argument"; //$NON-NLS-1$

	/** Properties file content type */
	private static final IContentType PROPERTIES_CONTENT_TYPE = Platform
			.getContentTypeManager().getContentType(
					"org.eclipse.jdt.core.javaProperties"); //$NON-NLS-1$

	/**
	 * The property key scanner.
	 */
	private AbstractPropertyScanner fPropertyKeyScanner;
	/**
	 * The comment scanner.
	 */
	private AbstractPropertyScanner fCommentScanner;
	/**
	 * The property value scanner.
	 */
	private AbstractPropertyScanner fPropertyValueScanner;
	/**
	 * The color manager.
	 */
	private PropertiesColorManager fColorManager;

	/**
	 * Creates a new properties file source viewer configuration for viewers in
	 * the given editor using the given preference store, the color manager and
	 * the specified document partitioning.
	 * 
	 * @param colorManager
	 *            the color manager
	 * @param preferenceStore
	 *            the preference store, can be read-only
	 * @param editor
	 *            the editor in which the configured viewer(s) will reside
	 * @param partitioning
	 *            the document partitioning for this configuration
	 */
	public PropertiesFileSourceViewerConfiguration(
			PropertiesColorManager colorManager,
			IPreferenceStore preferenceStore) {
		super(preferenceStore);
		fColorManager = colorManager;
		// fTextEditor= editor;
		initializeScanners();
	}

	/**
	 * Initializes the scanners.
	 */
	private void initializeScanners() {
		fPropertyKeyScanner = new SingleTokenJavaScanner(
				PROPERTIES_FILE_COLORING_KEY);
		fPropertyValueScanner = new PropertyValueScanner();
		fCommentScanner = new SingleTokenJavaScanner(
				PROPERTIES_FILE_COLORING_COMMENT);
	}

	/**
	 * Returns the property key scanner for this configuration.
	 * 
	 * @return the property key scanner
	 */
	protected RuleBasedScanner getPropertyKeyScanner() {
		return fPropertyKeyScanner;
	}

	/**
	 * Returns the comment scanner for this configuration.
	 * 
	 * @return the comment scanner
	 */
	protected RuleBasedScanner getCommentScanner() {
		return fCommentScanner;
	}

	/**
	 * Returns the property value scanner for this configuration.
	 * 
	 * @return the property value scanner
	 */
	protected RuleBasedScanner getPropertyValueScanner() {
		return fPropertyValueScanner;
	}

	/**
	 * Returns the color manager for this configuration.
	 * 
	 * @return the color manager
	 */
	protected PropertiesColorManager getColorManager() {
		return fColorManager;
	}

	/*
	 * @see SourceViewerConfiguration#getPresentationReconciler(ISourceViewer)
	 */
	@Override
	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {

		PresentationReconciler reconciler = new PropertiesPresentationReconciler();
		reconciler
				.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(
				getPropertyKeyScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr = new DefaultDamagerRepairer(getCommentScanner());
		reconciler.setDamager(dr, IPropertiesFilePartitions.COMMENT);
		reconciler.setRepairer(dr, IPropertiesFilePartitions.COMMENT);

		dr = new DefaultDamagerRepairer(getPropertyValueScanner());
		reconciler.setDamager(dr, IPropertiesFilePartitions.PROPERTY_VALUE);
		reconciler.setRepairer(dr, IPropertiesFilePartitions.PROPERTY_VALUE);

		return reconciler;
	}

	/*
	 * @see SourceViewerConfiguration#getConfiguredContentTypes(ISourceViewer)
	 */
	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		int length = IPropertiesFilePartitions.PARTITIONS.length;
		String[] contentTypes = new String[length + 1];
		contentTypes[0] = IDocument.DEFAULT_CONTENT_TYPE;
		for (int i = 0; i < length; i++)
			contentTypes[i + 1] = IPropertiesFilePartitions.PARTITIONS[i];

		return contentTypes;
	}

	/*
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#
	 * getConfiguredDocumentPartitioning
	 * (org.eclipse.jface.text.source.ISourceViewer)
	 */
	@Override
	public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) {
		/*
		 * if (fDocumentPartitioning != null) return fDocumentPartitioning;
		 * return super.getConfiguredDocumentPartitioning(sourceViewer);
		 */
		return IPropertiesFilePartitions.PROPERTIES_FILE_PARTITIONING;
	}

	/**
	 * Determines whether the preference change encoded by the given event
	 * changes the behavior of one of its contained components.
	 * 
	 * @param event
	 *            the event to be investigated
	 * @return <code>true</code> if event causes a behavioral change
	 */
	public boolean affectsTextPresentation(PropertyChangeEvent event) {
		return fPropertyKeyScanner.affectsBehavior(event)
				|| fCommentScanner.affectsBehavior(event)
				|| fPropertyValueScanner.affectsBehavior(event);
	}

	/**
	 * Adapts the behavior of the contained components to the change encoded in
	 * the given event.
	 * 
	 * @param event
	 *            the event to which to adapt
	 * @see PropertiesFileSourceViewerConfiguration#PropertiesFileSourceViewerConfiguration(IColorManager,
	 *      IPreferenceStore, ITextEditor, String)
	 */
	public void handlePropertyChangeEvent(PropertyChangeEvent event) {
		if (fPropertyKeyScanner.affectsBehavior(event))
			fPropertyKeyScanner.adaptToPreferenceChange(event);
		if (fCommentScanner.affectsBehavior(event))
			fCommentScanner.adaptToPreferenceChange(event);
		if (fPropertyValueScanner.affectsBehavior(event))
			fPropertyValueScanner.adaptToPreferenceChange(event);
	}

	/*
	 * @see
	 * org.eclipse.ui.editors.text.TextSourceViewerConfiguration#getReconciler
	 * (org.eclipse.jface.text.source.ISourceViewer)
	 */
	@Override
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		if (!EditorsUI.getPreferenceStore().getBoolean(
				SpellingService.PREFERENCE_SPELLING_ENABLED))
			return null;

		IReconcilingStrategy strategy = new SpellingReconcileStrategy(
				sourceViewer, EditorsUI.getSpellingService()) {
			@Override
			protected IContentType getContentType() {
				return PROPERTIES_CONTENT_TYPE;
			}
		};

		MonoReconciler reconciler = new MonoReconciler(strategy, false);
		reconciler.setDelay(500);
		return reconciler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.source.SourceViewerConfiguration#getDefaultPrefixes
	 * (org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
	 */
	@Override
	public String[] getDefaultPrefixes(ISourceViewer sourceViewer,
			String contentType) {
		return new String[] { "#", "" }; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/*
	 * @see
	 * org.eclipse.jface.text.source.SourceViewerConfiguration#getTabWidth(org
	 * .eclipse.jface.text.source.ISourceViewer)
	 */
	public int getTabWidth(ISourceViewer sourceViewer) {
		return 4;
	}

}
