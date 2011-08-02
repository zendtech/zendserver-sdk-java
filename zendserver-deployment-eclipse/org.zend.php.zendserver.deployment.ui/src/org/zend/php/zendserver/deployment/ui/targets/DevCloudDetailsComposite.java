package org.zend.php.zendserver.deployment.ui.targets;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.target.ZendDevPaasDetect;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

/**
 * DevCloud details editing composite: username and password.
 */
public class DevCloudDetailsComposite extends AbstractTargetDetailsComposite {

	private Text usernameText;
	private Text passwordText;

	public Composite create(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(2, false));

		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.DevCloudDetailsComposite_Username);
		usernameText = new Text(composite, SWT.BORDER);
		usernameText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true,
				false));
		usernameText
				.setToolTipText(Messages.DevCloudDetailsComposite_UsernameTooltip);

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.DevCloudDetailsComposite_Password);
		passwordText = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		passwordText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true,
				false));
		passwordText
				.setToolTipText(Messages.DevCloudDetailsComposite_PasswordTooltip);

		return composite;
	}

	public void setDefaultTargetSettings(IZendTarget defaultTarget) {
		// empty, can't restore DevCloud account details from IZendTarget
	}

	public String[] getData() {
		return new String[] { usernameText.getText(), passwordText.getText() };
	}

	public IZendTarget createTarget(String[] data) throws SdkException,
			IOException {
		ZendDevPaasDetect detect = new ZendDevPaasDetect();
		String username = data[0];
		String password = data[1];
		
		IZendTarget[] target = detect.detectTarget(username, password);
		if (target == null || target.length == 0) {
			return null;
		}

		TargetsManager tm = TargetsManagerService.INSTANCE.getTargetManager();
		
		String uniqueId = tm.createUniqueId(null);
		return tm.createTarget(uniqueId, target[0].getHost().toString(), target[0].getKey(), target[0].getSecretKey());
	}
	

	private void keyStoreMagic (String keyfile, String certfile) {
        
        // change this if you want another password by default
        String keypass = "importkey";
        
        // change this if you want another alias by default
        String defaultalias = "importkey";

        // change this if you want another keystorefile by default
        String keystorename = System.getProperty("keystore");

        if (keystorename == null)
            keystorename = System.getProperty("user.home")+
                System.getProperty("file.separator")+
                "keystore.ImportKey"; // especially this ;-)

        try {
        	
            // initializing and clearing keystore 
            KeyStore ks = KeyStore.getInstance("JKS", "SUN");
            ks.load( null , keypass.toCharArray());
            System.out.println("Using keystore-file : "+keystorename);
            ks.store(new FileOutputStream ( keystorename  ),
                    keypass.toCharArray());
            ks.load(new FileInputStream ( keystorename ),
                    keypass.toCharArray());

            // loading Key
            InputStream fl = fullStream (keyfile);
            byte[] key = new byte[fl.available()];
            KeyFactory kf = KeyFactory.getInstance("RSA");
            fl.read ( key, 0, fl.available() );
            fl.close();
            PKCS8EncodedKeySpec keysp = new PKCS8EncodedKeySpec ( key );
            PrivateKey ff = kf.generatePrivate (keysp);

            // loading CertificateChain
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream certstream = fullStream (certfile);

            Collection<? extends Certificate> c = cf.generateCertificates(certstream) ;
            Certificate[] certs = new Certificate[c.toArray().length];

            if (c.size() == 1) {
                certstream = fullStream (certfile);
                System.out.println("One certificate, no chain.");
                Certificate cert = cf.generateCertificate(certstream) ;
                certs[0] = cert;
            } else {
                System.out.println("Certificate chain length: "+c.size());
                certs = (Certificate[])c.toArray();
            }

            // storing keystore
            ks.setKeyEntry(defaultalias, ff, 
                           keypass.toCharArray(),
                           certs );
            System.out.println ("Key and certificate stored.");
            System.out.println ("Alias:"+defaultalias+"  Password:"+keypass);
            ks.store(new FileOutputStream ( keystorename ),
                     keypass.toCharArray());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
	
	  /* @param fname The filename
	     * @return The filled InputStream
	     * @exception IOException, if the Streams couldn't be created.
	     **/
	    private static InputStream fullStream ( String fname ) throws IOException {
	        FileInputStream fis = new FileInputStream(fname);
	        DataInputStream dis = new DataInputStream(fis);
	        byte[] bytes = new byte[dis.available()];
	        dis.readFully(bytes);
	        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
	        return bais;
	    }

}
