package org.zend.sdkcli.internal.ssh;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.util.FS;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class GithubSshSessionFactory extends JschConfigSessionFactory {

	private String passphrase;
	private String key;

	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	public void setKeyLocation(String key) {
		this.key = key;
	}

	@Override
	protected void configure(Host hc, Session session) {
		// do nothing
	}

	@Override
	protected JSch createDefaultJSch(FS fs) throws JSchException {
		final JSch jsch = new JSch();
		knownHosts(jsch, fs);
		if (key != null) {
			jsch.addIdentity(new File(key).getAbsolutePath(),
					passphrase);
		} else {
			final File home = fs.userHome();
			if (home == null) {
				return jsch;
			}
			final File sshdir = new File(home, ".ssh");
			if (sshdir.isDirectory()) {
				jsch.addIdentity(new File(sshdir, "id_rsa").getAbsolutePath(),
						passphrase);
			}
		}
		return jsch;
	}

	private static void knownHosts(final JSch sch, FS fs) throws JSchException {
		final File home = fs.userHome();
		if (home == null)
			return;
		final File known_hosts = new File(new File(home, ".ssh"), "known_hosts");
		try {
			final FileInputStream in = new FileInputStream(known_hosts);
			try {
				sch.setKnownHosts(in);
			} finally {
				in.close();
			}
		} catch (FileNotFoundException none) {
			// Oh well. They don't have a known hosts in home.
		} catch (IOException err) {
			// Oh well. They don't have a known hosts in home.
		}
	}

}
