package org.zend.php.zendserver.deployment.core.targets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jsch.internal.core.IConstants;
import org.eclipse.jsch.internal.core.JSchCorePlugin;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.zend.sdklib.internal.target.PublicKeyBuilder;
import org.zend.sdklib.internal.target.PublicKeyNotFoundException;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;

public class JSCHPubKeyDecryptor implements PublicKeyBuilder {

	class PassphrasePrompt implements Runnable {
		private String message;
		private String passphrase;

		PassphrasePrompt(String message) {
			this.message = message;
		}

		public void run() {
			Display display = Display.getCurrent();
			Shell shell = new Shell(display);
			PassphraseDialog dialog = new PassphraseDialog(shell, message);
			dialog.open();
			shell.dispose();
			passphrase = dialog.getPassphrase();
		}

		public String getPassphrase() {
			return passphrase;
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
				prompt = new PassphrasePrompt(NLS.bind("Passphrase {0}",
						new String[] { pkeyab }));
			}
			Display.getDefault().syncExec(prompt);
			String passphrase = prompt.getPassphrase();
			if (passphrase == null)
				break;
			if (_kpair.decrypt(passphrase)) {
				break;
			}
			MessageDialog.openError(getShell(), "SSH Key Error",
					NLS.bind("Error {0}", new String[] { pkeyab }));
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
		String fingerprint = kpair.getFingerPrint();

		return result;
	}

	private Shell getShell() {
		return Display.getDefault().getActiveShell();
	}

	private JSch getJSch() {
		return JSchCorePlugin.getPlugin().getJSch();
	}

}
