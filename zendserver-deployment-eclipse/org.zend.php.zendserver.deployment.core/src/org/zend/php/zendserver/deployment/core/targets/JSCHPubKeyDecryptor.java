package org.zend.php.zendserver.deployment.core.targets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jsch.internal.core.IConstants;
import org.eclipse.jsch.internal.core.JSchCorePlugin;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.zend.php.zendserver.deployment.core.Messages;
import org.zend.sdklib.internal.target.PublicKeyBuilder;
import org.zend.sdklib.internal.target.PublicKeyNotFoundException;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;

public class JSCHPubKeyDecryptor implements PublicKeyBuilder {
	
	private Map<String, String> passphrases = new HashMap<String, String>();

	class PassphrasePrompt implements Runnable {
		private String message;
		private String passphrase;

		PassphrasePrompt(String message) {
			this.message = message;
		}

		public void run() {
			Display display = Display.getCurrent();
			Shell shell = display.getActiveShell();
			boolean newShell = false;
			if (shell == null) {
				newShell = true;
				shell = new Shell(display);
			}
			PassphraseDialog dialog = new PassphraseDialog(shell, message);
			dialog.open();
			if (newShell) {
				shell.dispose();
			}
			passphrase = dialog.getPassphrase();
		}

		public String getPassphrase() {
			return passphrase;
		}
	}
	
	class ErrorMessage implements Runnable {
		private String message;

		ErrorMessage(String message) {
			this.message = message;
		}

		public void run() {
			Display display = Display.getCurrent();
			Shell shell = display.getActiveShell();
			boolean newShell = false;
			if (shell == null) {
				newShell = true;
				shell = new Shell(display);
			}
			MessageDialog.openError(shell, Messages.JSCHPubKeyDecryptor_SshErrorTitle, message);
			if (newShell) {
				shell.dispose();
			}
		}
	}
	
	public void isValidPrivateKey(String pkey) throws PublicKeyNotFoundException {
		try {
			KeyPair.load(getJSch(), pkey);
		} catch (JSchException e) {
			throw new PublicKeyNotFoundException(e);
		}
	}

	public String getPublicKey(String pkeyab) throws PublicKeyNotFoundException {

		KeyPair _kpair;
		try {
			_kpair = KeyPair.load(getJSch(), pkeyab);
		} catch (JSchException e) {
			throw new PublicKeyNotFoundException(e);
		}
		PassphrasePrompt prompt = null;
		while (_kpair.isEncrypted()) {
			if (prompt == null) {
				prompt = new PassphrasePrompt(MessageFormat.format(
						Messages.JSCHPubKeyDecryptor_PassphrasePrompt, pkeyab));
			}
			Display.getDefault().syncExec(prompt);
			String passphrase = prompt.getPassphrase();
			if (passphrase == null)
				break;
			if (_kpair.decrypt(passphrase)) {
				passphrases.put(pkeyab, passphrase);
				break;
			}
			Display.getDefault().syncExec(
					new ErrorMessage(NLS.bind(Messages.JSCHPubKeyDecryptor_SshError,
							new String[] { pkeyab })));
		}
		if (_kpair.isEncrypted()) {
			return null;
		}
		KeyPair kpair = _kpair;
		String _type = (kpair.getKeyType() == KeyPair.DSA) ? IConstants.DSA
				: IConstants.RSA;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		String kpairComment = _type + "-1024"; //$NON-NLS-1$
		kpair.writePublicKey(out, kpairComment);
		try {
			out.close();
		} catch (IOException e) {
			// IOException in ByteArrayOutputStream? hmm...
		}
		String result = out.toString();
		return result;
	}
	
	public String getPassphase(final String privateKey) throws PublicKeyNotFoundException {
		String passphrase = passphrases.get(privateKey);
		if (passphrase == null) {
			KeyPair _kpair;
			try {
				_kpair = KeyPair.load(getJSch(), privateKey);
			} catch (JSchException e) {
				throw new PublicKeyNotFoundException(e);
			}
			PassphrasePrompt prompt = null;
			while (_kpair.isEncrypted()) {
				if (prompt == null) {
					prompt = new PassphrasePrompt(MessageFormat.format(
							Messages.JSCHPubKeyDecryptor_PassphrasePrompt,
							privateKey));
				}
				Display.getDefault().syncExec(prompt);
				passphrase = prompt.getPassphrase();
				if (passphrase == null) {
					break;
				}
				if (_kpair.decrypt(passphrase)) {
					passphrases.put(privateKey, passphrase);
					return passphrase;
				}
				Display.getDefault().syncExec(
						new ErrorMessage(MessageFormat.format(
								Messages.JSCHPubKeyDecryptor_InvalidPassphrase, privateKey)));
			}
		}
		return passphrase;
	}

	private JSch getJSch() {
		return JSchCorePlugin.getPlugin().getJSch();
	}

}
