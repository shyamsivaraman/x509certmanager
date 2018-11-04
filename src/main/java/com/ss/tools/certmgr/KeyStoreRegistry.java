package com.ss.tools.certmgr;

import java.security.KeyStore;
import java.util.HashMap;
import java.util.Set;

public class KeyStoreRegistry {

	private HashMap<String,KeyStore> keyStoreMap = new HashMap<>();
	private static final KeyStoreRegistry m_Instance = new KeyStoreRegistry();
	
	public static KeyStoreRegistry getInstance() {
		return m_Instance;
	}

	public void registerKeystore(String fileName, KeyStore keyStore) {
		keyStoreMap.put(fileName, keyStore);
	}
	
	public Set<String> getKeystores() {
		return this.keyStoreMap.keySet();
	}
	
	public KeyStore getKeyStore(String name) {
		return this.keyStoreMap.get(name);
	}
}
