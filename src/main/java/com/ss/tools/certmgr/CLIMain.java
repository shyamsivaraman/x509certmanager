package com.ss.tools.certmgr;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import com.ss.tools.certmgr.SessionStore.CommandSession;
import com.ss.tools.certmgr.process.CommandProcessor;
import com.ss.tools.certmgr.process.CommandProcessor.COMMAND;

/**
 * 
 * @author 	Shyam Sivaraman
 * Date:	03-Nov-2018
 * 
 */

public class CLIMain {
	
	private HashMap<String,CommandProcessor.COMMAND> commandsList = new HashMap<>();
	private HashMap<String,Integer> stackFrameLength = new HashMap<String,Integer>();
	private HashMap<String,List<String>> menuFlow = new HashMap<String,List<String>>();
	
	private LinkedList<String> operandStack = new LinkedList<String>();

	{
		commandsList.put("1", CommandProcessor.COMMAND.LOAD_KEYSTORE);
		commandsList.put("2", CommandProcessor.COMMAND.DISP_ALL_KEYSTORE);
		commandsList.put("3", CommandProcessor.COMMAND.DISP_KEYSTORE_ENTRIES);
		commandsList.put("4", CommandProcessor.COMMAND.FIND_CERT);
		commandsList.put("5", CommandProcessor.COMMAND.COMPARE_KEYSTORE);
		commandsList.put("6", CommandProcessor.COMMAND.MOVE_CERTS);
		commandsList.put("9", CommandProcessor.COMMAND.SAVE_SESSION);
		
		//Menu 1
		menuFlow.put("1", Arrays.asList(new String[] {
					"> Enter keystore file path: ",
					"> Enter keystore password: ",
					"$PROCESS$",
					"# Do want to add another keystore? [Y/N]: "
				}));
		stackFrameLength.put("1", 2);
		
		//Menu 2
		menuFlow.put("2", Arrays.asList(new String[] {
				"* List of loaded keystores *",
				"$PROCESS$"
			}));
		stackFrameLength.put("2", 0);
		
		//Menu 3
		menuFlow.put("3", Arrays.asList(new String[] {
				"> Enter keystore name: ",
				"> Show detailed? [Y/N]: ",
				"$PROCESS$",
				"# Do want to list another keystore? [Y/N]: "
			}));
		stackFrameLength.put("3", 2);
		
		//Menu 4
		menuFlow.put("4", Arrays.asList(new String[] {
				"* Search certificate by certificate field: ",
				"*  1. Thumbprint",
				"*  2. Having Subject text",
				"*  3. Expiring in X month(s)",
				"*  4. Self-signed",
				"> Please enter the search field: ",
				"> Enter relevant search text: ",
				"$PROCESS$"
			}));
		stackFrameLength.put("4", 1);
		
		//Menu 5
		menuFlow.put("5", Arrays.asList(new String[] {
				"> Enter keystores to compare (comma separated): "
			}));
		stackFrameLength.put("5", 1);
		
		//Menu 9
		menuFlow.put("9", Arrays.asList(new String[] {
				"* Saving session... ",
				"$PROCESS$"
			}));
		stackFrameLength.put("9", 0);
	}
	
	public static void main(String[] args) {
		CLIMain main = new CLIMain();
		SessionStore.getInstance().load();
		main._processSession();
		main.run(args);
	}
	
	private void _processSession() {
		HashMap<String, CommandSession> sessionData = SessionStore.getInstance().getSessionData();
		
		for(String key : sessionData.keySet()) {
			CommandSession cs = sessionData.get(key);
			if(cs.getCommand().equals("LOAD_KEYSTORE")) {
				new CommandProcessor().process(COMMAND.LOAD_KEYSTORE, cs.getParameters(), true);
			}
		}
	}

	private void run(String[] args) {
		int curMenuItem = 0;
		this.printMenu("0");
		Scanner input = new Scanner(System.in);
		String menuItem = input.nextLine();
		
		while(menuItem != null && !menuItem.equals("0") && !menuItem.equals("")) {
			if(menuFlow.get(menuItem) != null && curMenuItem < menuFlow.get(menuItem).size()) {
				String m = menuFlow.get(menuItem).get(curMenuItem++);
				if(m.startsWith(">")) {
					System.out.println("");
					String val = this.promptAndGet(input, m);
					val = (val == null) ? "" : val;
					operandStack.push(val);
				} else if(m.equals("$PROCESS$")) {
					this.processInput(menuItem);
					operandStack.clear();
				} else if(m.startsWith("#")) {
					menuItem = this.displayContinuationMenu(input, menuItem, m);
					curMenuItem = 0;
					System.out.println("");
				} else {
					System.out.println("");
					System.out.print(m);
					operandStack.clear();
				}
			} else {
				this.printMenu("0");
				menuItem = input.nextLine();
				System.out.println("");
				curMenuItem = 0;
			}
		}
		
		System.out.println("Program Exited\n");
	}
	
	private void processInput(String menuItem) {
		System.out.println("");
		//System.out.println("Processing command " + menuItem + " with stack size: (" + operandStack.size() + ")");
		
		new CommandProcessor().process(commandsList.get(menuItem), operandStack, false);
	}

	private String displayContinuationMenu(Scanner input, String menuItem, String m) {
		String val = this.promptAndGet(input, m);
		
		if(!"Y".equalsIgnoreCase(val)) {
			this.printMenu("0");
			menuItem = input.nextLine();
		}
		
		return menuItem;
	}

	private String promptAndGet(Scanner s, String prompt) {
		System.out.print(prompt);
		return s.nextLine();
	}

	private void printMenu(String menu) {
		System.out.println("\n+----- Select a task -----------+");
		System.out.println("| 1. Load Keystore(s)\t\t|");
		System.out.println("| 2. Display all Keystores\t|");
		System.out.println("| 3. Display Keystore Entries\t|");
		System.out.println("| 4. Find Certificates\t\t|");
		System.out.println("| 5. Compare Keystores\t\t|");
		System.out.println("| 6. Move Certificates\t\t|");
		System.out.println("| 7. Generate Diff Report\t|");
		System.out.println("| 8. Export Certificate file\t|");
		System.out.println("| 9. Save Session\t\t|");
		System.out.println("+-------------------------------+");
		System.out.print("Enter selection [0]: ");
	}

}
