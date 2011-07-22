package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;
import org.zend.php.zendserver.deployment.ui.Messages;

public class TextAssistField extends TextField {

	private final String[] proposals;
	private KeyStroke instance = null;

	public TextAssistField(IModelObject target, Feature key, String label,
			String[] proposals) {
		super(target, key, label, SWT.SINGLE, false);
		this.proposals = proposals;
	}

	@Override
	protected void createTextControl(Composite parent, FormToolkit toolkit) {
		super.createTextControl(parent, toolkit);
		SimpleContentProposalProvider proposalProvider = new SimpleContentProposalProvider(
				proposals);
		proposalProvider.setFiltering(true);
		ContentProposalAdapter adapter;
		try {
			instance = KeyStroke.getInstance("Ctrl+Space"); //$NON-NLS-1$
		} catch (ParseException e) {
		}
		adapter = new ContentProposalAdapter(text, new TextContentAdapter(),
				proposalProvider, instance, null);
		adapter.setPropagateKeys(true);
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
	}

	@Override
	protected void createControlDecoration() {
		super.createControlDecoration();

		if (instance != null) {
			FieldDecoration img = FieldDecorationRegistry.getDefault()
					.getFieldDecoration(
							FieldDecorationRegistry.DEC_CONTENT_PROPOSAL);
			controlDecoration.setImage(img.getImage());
			final String desc = NLS.bind(Messages.TextHintField_0, instance.format()) ;
			controlDecoration
					.setDescriptionText(desc); 
			controlDecoration.show();
			text.addFocusListener(new FocusListener() {
				public void focusLost(FocusEvent e) {
				}
				public void focusGained(FocusEvent e) {
					controlDecoration.showHoverText(desc);
				}
			});
		}
	}

}
