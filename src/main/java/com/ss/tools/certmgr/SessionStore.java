package com.ss.tools.certmgr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

public class SessionStore {

	private String sessionFile = System.getenv("TEMP") + File.separator + "certmgr.session";
	private HashMap<String, CommandSession> sessionData = new HashMap<>();
	private static final SessionStore m_Instance = new SessionStore();
	
	public static class CommandSession implements Serializable {
		private String command;
		private String name;
		private LinkedList<String> parameters;
		private static int counter = 1;
		
		public CommandSession(String c, LinkedList<String> p) {
			this.command = c;
			this.parameters = p;
			this.name = c + "-" + counter++;
		}
		
		public int hashCode() {
			int h = 1;
			h = h * 31 + getCommand().hashCode();
			h = h * 31 + getParameters().hashCode();
			
			return h;
		}
		
		public String getName() {
			return this.name;
		}

		public String getCommand() {
			return command;
		}

		public LinkedList<String> getParameters() {
			return parameters;
		}
	}
	
	public static SessionStore getInstance() {
		return m_Instance;
	}
	
	public void addSession(String command, LinkedList<String> parameters) {
		LinkedList<String> cmdList = new LinkedList<String>();
		cmdList.addAll(parameters);
		
		CommandSession cs = new CommandSession(command, cmdList);
		
		this.getSessionData().put(cs.getName(), cs);
	}
	
	public void store() {
		FileOutputStream fos = null;
		
		File f = new File(sessionFile);
		try {
			if(!f.exists()) {
					f.createNewFile();
			}
			
			fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			//System.out.println(">>>> Saving session data: " + this.getSessionData());
			oos.writeObject(this.getSessionData());
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void load() {
		FileInputStream sessionIo = null;
		
		try {
			File f = new File(sessionFile);
			if(!f.exists()) {
				return;
			}
			
			sessionIo = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(sessionIo);
			
			Object obj =  ois.readObject();
			if(obj instanceof HashMap) {
				System.out.println("******* Session store data loading *******");
				this.sessionData = (HashMap<String, CommandSession>)obj;
				//System.out.println(getSessionData());
			} else {
				System.out.println("xxxxxxx No session xxxxxxx");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(sessionIo != null) {
				try {
					sessionIo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public HashMap<String, CommandSession> getSessionData() {
		return sessionData;
	}
}
