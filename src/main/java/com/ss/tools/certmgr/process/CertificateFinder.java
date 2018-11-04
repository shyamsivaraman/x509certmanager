package com.ss.tools.certmgr.process;

import java.security.KeyStore;
import java.util.Set;

import com.ss.tools.certmgr.KeyStoreRegistry;

public class CertificateFinder {
	
	private String[] SEARCH_TYPE = { 
		"thumbprint", "subject", "expiry", "selfSigned"
	};

	public void search(int searchType, String searchText) {
		Set<String> keystores = KeyStoreRegistry.getInstance().getKeystores();
		
		for(String k : keystores) {
			KeyStore keystore = KeyStoreRegistry.getInstance().getKeyStore(k);
			KeyStoreExplorer kexp = new KeyStoreExplorer();
			kexp.setKeyStore(k, keystore);
			
			System.out.println("@ Searching in keystore : [ " + k + " ]");
			kexp.findCertificates(SEARCH_TYPE[searchType-1], searchText);
		}
	}

}
