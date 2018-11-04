package com.ss.tools.certmgr.process;

import java.security.KeyStore;
import java.util.LinkedList;
import java.util.Set;

import com.ss.tools.certmgr.KeyStoreRegistry;
import com.ss.tools.certmgr.SessionStore;

/**
 * 
 * @author 	Shyam Sivaraman
 * Date:	04-Nov-2018
 * 
 */

public class CommandProcessor {
	
	public static enum COMMAND {
		LOAD_KEYSTORE, DISP_ALL_KEYSTORE, DISP_KEYSTORE_ENTRIES, COMPARE_KEYSTORE, MOVE_CERTS, SAVE_SESSION, FIND_CERT
	}
	
	public void process(COMMAND command, LinkedList<String> operandStack, boolean internal) {
		if(command == COMMAND.LOAD_KEYSTORE) {
			if(operandStack.size() != 2)
				return;
			
			KeystoreLoader kl = new KeystoreLoader();
			kl.process(operandStack.get(1), operandStack.get(0));
			
			if(!internal)
				SessionStore.getInstance().addSession(command.name(), operandStack);
		} else if(command == COMMAND.DISP_ALL_KEYSTORE) {
			Set<String> keystores = KeyStoreRegistry.getInstance().getKeystores();
			for(String name : keystores) {
				System.out.println(" * " + name);
			}
		} else if(command == COMMAND.DISP_KEYSTORE_ENTRIES) {
			KeyStoreExplorer kexp = new KeyStoreExplorer();
			String storeName = operandStack.get(1);
			boolean detailed = operandStack.get(0).equalsIgnoreCase("Y");
			KeyStore keyStore = KeyStoreRegistry.getInstance().getKeyStore(storeName);
			kexp.setKeyStore(storeName, keyStore);
			kexp.displayAll(detailed);
		} else if(command == COMMAND.FIND_CERT) {
			CertificateFinder cf = new CertificateFinder();
			String searchType = operandStack.get(1);
			String searchText = operandStack.get(0);
			cf.search((searchType.trim().equals("")) ? 1 : Integer.parseInt(searchType), searchText);
		} else if(command == COMMAND.SAVE_SESSION) {
			SessionStore.getInstance().store();
			System.out.println("Saved");
		}
	}
}
