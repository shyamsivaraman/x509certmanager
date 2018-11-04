package com.ss.tools.certmgr.model;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.xml.bind.DatatypeConverter;

/**
 * 
 * @author 	Shyam Sivaraman
 * Date:	04-Nov-2018
 * 
 */

public class KeystoreEntry {

	private static final String PRIVATE = "Private";
	private static final String PUBLIC = "Public";
	private static final String[] cols = {
		"Entry Type", "Cert chain length", "ID", "Subject"	
	};
	
	private Key key;
	private String keyType;
	private Certificate[] certChain;
	
	public KeystoreEntry(Key key, Certificate[] certificateChain) {
		this.key = key;
		this.certChain = certificateChain;
		this.keyType = KeystoreEntry.PRIVATE;
	}
	
	public KeystoreEntry(Certificate[] certificateChain) {
		this.certChain = certificateChain;
		this.keyType = KeystoreEntry.PUBLIC;
	}

	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();

		data.append("KeyType \t\t\t:" + this.keyType + "\n");
		data.append("Certificate chain length \t:" + certChain.length + "\n");
		data.append("\n");
		
		int counter = 1;
		for(Certificate c : certChain) {
			data.append("Certificate " + counter++ + "\n");
			this.printCertDetail(data, c);
			data.append("\n");
		}
		
		return data.toString();
	}

	private void printCertDetail(StringBuilder data, Certificate c) {
		String indent = "    ";
		data.append(indent + "Certificate type \t\t:" + c.getType() + "\n");
		
		X509Certificate cert = (X509Certificate)c;
		data.append(indent + "Algorithm \t\t\t:" + cert.getPublicKey().getAlgorithm() + "\n");
		data.append(indent + "From Date \t\t\t:" + cert.getNotBefore() + "\n");
		data.append(indent + "To Date \t\t\t:" + cert.getNotAfter() + "\n");
		data.append(indent + "Version \t\t\t:" + cert.getVersion() + "\n");
		
		Principal principal = cert.getSubjectDN();
        String subjectDn = principal.getName();
        data.append(indent + "SubjectDN \t\t\t:\n");
        this._printLdapCN(data, subjectDn);
        
        principal = cert.getIssuerDN();
        String issuerDn = principal.getName();
        data.append(indent + "IssuerDN \t\t\t:\n");
        this._printLdapCN(data, issuerDn);
        
		data.append(indent + "Thumbprint (SHA1):\t\t:" + this._getThumbprint(cert) + "\n");
	}
	
	private void _printLdapCN(StringBuilder data, String cn) {
		try {
			LdapName ldapName = new LdapName(cn);
			List<Rdn> rdns = ldapName.getRdns();
			for(Rdn rdn : rdns) {
				data.append("\t\t\t\t " + rdn.getType() + "\t:" + rdn.getValue() + "\n");
			}
		} catch (InvalidNameException e) {
			e.printStackTrace();
		}
	}
	
	public String getThumbprint() {
		X509Certificate xcert = (X509Certificate)certChain[0];
		
		try {
			return DatatypeConverter.printHexBinary(MessageDigest.getInstance("SHA-1").digest(xcert.getEncoded())).toLowerCase();
		} catch (CertificateEncodingException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return "-";
	}
	
	private String _getThumbprint(X509Certificate cert) {
		try {
			return DatatypeConverter.printHexBinary(MessageDigest.getInstance("SHA-1").digest(cert.getEncoded())).toLowerCase();
		} catch (CertificateEncodingException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return "-";
	}

	public boolean matches(String attribute, String searchText) {
		X509Certificate xcert = (X509Certificate)certChain[0];
		
		if(attribute.equals("thumbprint")) {
			return (this.getThumbprint().equals(searchText));
		} else if(attribute.equals("subject")) {
			return xcert.getSubjectX500Principal().getName().toLowerCase().contains(searchText);
		} else if(attribute.equals("expiry")) {
			Date endDate = xcert.getNotAfter();
			LocalDate endLdt = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate startDt = LocalDate.now();
			Period period = Period.between(startDt, endLdt);
			long monthsBetweenDates = period.toTotalMonths();
			return monthsBetweenDates < Long.parseLong(searchText);
		} else if(attribute.equals("selfSigned")) {
			return xcert.getSubjectX500Principal().getName().toLowerCase().equals(
					xcert.getIssuerX500Principal().getName().toLowerCase());
		}
		
		return false;
	}
}
