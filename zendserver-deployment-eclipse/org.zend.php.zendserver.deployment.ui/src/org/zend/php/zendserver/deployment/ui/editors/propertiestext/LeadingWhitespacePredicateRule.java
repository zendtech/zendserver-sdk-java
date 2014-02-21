package org.zend.php.zendserver.deployment.ui.editors.propertiestext;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.WordPatternRule;

/**
 * A leading white space predicate rule.
 */
public final class LeadingWhitespacePredicateRule extends WordPatternRule {

	private static class DummyDetector implements IWordDetector {

		/*
		 * @see IWordDetector#isWordStart
		 */
		public boolean isWordStart(char c) {
			return false;
		}

		/*
		 * @see IWordDetector#isWordPart
		 */
		public boolean isWordPart(char c) {
			return false;
		}
	}

	/**
	 * Creates a white space rule for the given <code>token</code>.
	 * 
	 * @param token
	 *            the token to be returned on success
	 */
	public LeadingWhitespacePredicateRule(IToken token, String whitespace) {
		super(new DummyDetector(), whitespace, "dummy", token); //$NON-NLS-1$
		setColumnConstraint(0);
	}

	/*
	 * @see
	 * org.eclipse.jface.text.rules.WordPatternRule#endSequenceDetected(org.
	 * eclipse.jface.text.rules.ICharacterScanner)
	 */
	@Override
	protected boolean endSequenceDetected(ICharacterScanner scanner) {
		int c;
		do {
			c = scanner.read();
		} while (Character.isWhitespace((char) c));

		scanner.unread();

		return true;
	}
}
