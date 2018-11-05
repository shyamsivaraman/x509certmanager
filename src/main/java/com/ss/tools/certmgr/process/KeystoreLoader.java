package com.ss.tools.certmgr.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import com.ss.tools.certmgr.KeyStoreRegistry;

public class KeystoreLoader {

	public void process(String keyStorePath, String keyStorePassword) throws KeyStoreException, 
		NoSuchAlgorithmException, CertificateException, IOException {
		File f = new File(keyStorePath);
		String fileName = f.getName();
		
		FileInputStream fis = new FileInputStream(f);
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		keyStore.load(fis, keyStorePassword.toCharArray());
		System.out.println("INFO: Keystore " + fileName + " loaded");
		KeyStoreRegistry.getInstance().registerKeystore(fileName, keyStore);
	}
}
