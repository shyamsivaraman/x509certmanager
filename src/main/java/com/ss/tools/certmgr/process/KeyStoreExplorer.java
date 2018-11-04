package com.ss.tools.certmgr.process;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import com.ss.tools.certmgr.model.KeystoreEntry;

/**
 * 
 * @author 	Shyam Sivaraman
 * Date:	04-Nov-2018
 * 
 */

public class KeyStoreExplorer {

	private String keystoreName;
	private KeyStore keystore;
	
	public void setKeyStore(String name, KeyStore keyStore) {
		this.keystoreName = name;
		this.keystore = keyStore;
	}

	public void displayAll(boolean detailed) {
		System.out.println("\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588 Keystore details : " + this.keystoreName + " \u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\n");
		try {
			int counter = 1;
			Enumeration<String> aliases = this.keystore.aliases();
			
			while(aliases.hasMoreElements()) {
				KeystoreEntry ke = null;
				String alias = aliases.nextElement();
				Certificate[] certificateChain = this.keystore.getCertificateChain(alias);
				
				if(this.keystore.isKeyEntry(alias)) {
					ke = new KeystoreEntry(this.keystore.getKey(alias, "Passw0rd".toCharArray()), 
							(certificateChain == null) ? new Certificate[] { this.keystore.getCertificate(alias) }: certificateChain);
				} else {
					ke = new KeystoreEntry((certificateChain != null) ? certificateChain : new Certificate[] { this.keystore.getCertificate(alias) });
				}
				
				if(detailed) {
					this._printDetailed(ke, counter++, alias);
				} else {
					String b = this._getBlock("  " + counter++ + " : " + alias, 54);
					System.out.println(b + ": " + ke.getThumbprint());
				}
			}
			
			System.out.println("");
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnrecoverableEntryException e) {
			e.printStackTrace();
		}
	}
	
	private String _getBlock(String str, int blockSize) {
		int spaces = blockSize - str.length();
		String padding = "";
		for(int i=0; i<spaces; i++) {
			padding += " ";
		}
		
		return str + padding;
	}

	private void _printDetailed(KeystoreEntry ke, int counter, String alias) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
		System.out.println("\n\u2588\u2593\u2593\u2593\u2593\u2592\u2591 (" + counter + ") Alias: " + alias + " \u2591\u2592\u2593\u2593\u2593\u2593\u2588\n");
		System.out.print(ke.toString());
	}

	public List<KeystoreEntry> findCertificates(String attribute, String searchText) {
		LinkedList<KeystoreEntry> keList = new LinkedList<>();
		boolean searchHit = false;
		
		try {
			Enumeration<String> aliases = this.keystore.aliases();
			
			while(aliases.hasMoreElements()) {
				KeystoreEntry ke = null;
				String alias = aliases.nextElement();
				Certificate[] certificateChain = this.keystore.getCertificateChain(alias);
				
				if(this.keystore.isKeyEntry(alias)) {
					ke = new KeystoreEntry(this.keystore.getKey(alias, "Passw0rd".toCharArray()), 
							(certificateChain == null) ? new Certificate[] { this.keystore.getCertificate(alias) }: certificateChain);
				} else {
					ke = new KeystoreEntry((certificateChain != null) ? certificateChain : new Certificate[] { this.keystore.getCertificate(alias) });
				}
				
				boolean match = ke.matches(attribute, searchText);
				
				if(match) {
					String b = this._getBlock("    " + alias, 54);
					System.out.println(b + ": " + ke.getThumbprint());
					searchHit = true;
				}
			}
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnrecoverableEntryException e) {
			e.printStackTrace();
		}
		
		if(!searchHit)
			System.out.println("    None matched");
		
		return keList;
	}
}
