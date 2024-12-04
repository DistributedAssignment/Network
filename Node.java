

import java.io.FileWriter;
import java.net.URL;
import java.io.IOException;
import java.net.DatagramPacket;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Base64;
import java.lang.ProcessBuilder;
import java.io.File;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

public class Node implements Runnable{
	private  String ip;
	private  InetAddress IP;
	private  int port;
	private  DatagramSocket socket_s;
	private  DatagramSocket socket_c;
	private  int port_s;
	private  int initial_port;
	private  String initial_ip;
	private  Queue<String> accounts;	
	private  Queue<byte[]> messages;
	private  InetAddress initial_IP;
	private  DatagramSocket socket;
	private  Account[] account_list;
	private  int[] port_list;
	private  InetAddress[] IP_list;
	private  String[] ip_list;
	private  int[] l_port_list;
	private  String[] node_list;
	private  String name;
	private  boolean exists;
	public Node() {
		this.accounts = new LinkedList<String>();
		this.messages = new LinkedList<byte[]>();
		this.account_list = new Account[2048];
		this.exists = false;
		this.ip = null;
		this.IP = null;
		this.ip_list = new String[2048];
		this.port = 1;
		this.socket = null;
		this.initial_port = 1;
		this.socket_s = null;
		this.socket_c = null;
		this.port_s = 1;
		this.initial_ip = null;
		this.initial_IP = null;
		this.port_list = new int[2048];
		this.IP_list = new InetAddress[2048];
		this.node_list = null;
		this.name = null;
	}
	
	public  void run() {
		/**TO DO**/
		/* TUESDAY
		* TEST BY GRADUALLY ADDING THE STUFF CREATED START WITH CREATING AN INITIAL NODE AND TEST BY ADDING MORE STRUCTURE
		 * FINISH 3
		 * FINISH 1
		 * 
		 * WENSDAY
		 * FINISH 4
		 * REFER TO ASSIGNMENT SHEET AND CHECK OF STUFF
		 * 
		 * THURSDAY
		 * ADD ANY ADDITIONAL STUFF
		 * TEST
		 * WRITE REPORT 
		 * 
		 */
		//This sets the node up for being apart of a network
		initialise();

		//Now the node has been created we can now run the manager
		/*try {
			manager();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	
	private void  manager() throws Exception {
		//This will be the interface that the client deals with when managing there accounts
		final String[] MENU_LIST = {"create","manage","disconnect"};
    	Scanner myObj = new Scanner(System.in);
		boolean finished = false;
		int number = 0;
		String account = null;
		byte[] data = new byte[1028];
		while (!finished) {
			try {
		    	//Menu
				System.out.println("Please press: ");
		    	for (int i=0; i<MENU_LIST.length; i++) {
		    		int n = i+1;
		    		System.out.println(n+": "+MENU_LIST[i]);
		    		
		    	}
		    	System.out.print("Input: ");
		    	account = myObj.nextLine();
		    	account.trim();
		    	number = Integer.parseInt(account);
				}
				catch(Exception NumberFormatException) {
				  number = 0;
				}
			
			switch(number) {
			  case 1:
				  createAccount();
			    break;
			  case 2:
				try {
		    	System.out.print("Account number: ");
		    	account = myObj.nextLine();
		    	account.trim();  
				} catch (Exception e) {
					account = "0";
				}
			    if (retrieveData(account)) {
			    	while (accountManagement(account));
			    }		    
			    break;
			  case 3:
				  
			  default:
	    		  System.out.println("Invalid request");
	    		  break;
			}	
		}
	}

	 private void  createAccount() throws Exception {
		  Scanner myObj = new Scanner(System.in);
			boolean create = false;
			int overdraft = 0;
			while (!create) {
				String overdraft_str = null;
		    	System.out.print("Please input your desired overdraft limit (0-1500): ");
				try {
			    	overdraft_str = myObj.nextLine();
			    	overdraft_str.trim();
			    	overdraft = Integer.parseInt(overdraft_str);
		
					if (overdraft >= 0 && overdraft <= 1500) {
						create = true;
					}
					}
					catch(Exception NumberFormatException) {
					System.out.println("Not a number"); 
					}
			}	
			int number = 0;
			//Finds an account with free space
			for (int i = 0; i < account_list.length; i++) {
				if (account_list[i] == null) {
					break;
				}
				number += 1;
			}
			/**NEED TO VERIFY THE ACCOUNT NUMBER IS CORRECT BY CONSULTING WITH THE INITIAL NODE**/
			
			Account a = new Account(number,0,overdraft);
		    while (accountManagement(Integer.toString(a.getNumber())));	
	}

	private boolean accountManagement(String account) throws Exception{
			final String[] REQUEST_LIST = {"retreive","withdraw","deposit","close","exit"};
	    	Scanner myObj = new Scanner(System.in);
	    	int number = 0;
	    	String choice;
	    	System.out.println("Account "+account);
	    	System.out.println("Please press: ");
	    	for (int i=0; i<REQUEST_LIST.length; i++) {
	    		int n = i+1;
	    		System.out.println(n+": "+REQUEST_LIST[i]);
	    		
	    	}
			try {
		    	System.out.print("Input: ");
		    	choice = myObj.nextLine();
		    	choice.trim();
		    	number = Integer.parseInt(choice);
				}
				catch(Exception NumberFormatException) {
				  number = 0;
				}
	    	
	    	switch(number) {
	    	  case 1:
	    		boolean b = retrieveData(account);
	    		return b;
	    	  case 2:
	    		 withdraw(account);
	    		 accounts.add(account);
	    		return true;
	    	  case 3:
	    		deposit(account);
	    		accounts.add(account);
	    		return true;
	    	  case 4:
	      		closeData(account);
	      		accounts.add(account);
	    		return false;
	    	  case 5:
	    		return false;
	    	  default:
	    		  System.out.println("Invalid request");
	    		  return true;
	    	}
	    }
	
	 private void  closeData(String account) {
		int num = Integer.parseInt(account);
		account_list[num] = null;	
	}

	private void  deposit(String account) {
		Scanner myObj = new Scanner(System.in);
	 	boolean deposit =  false;
	 	int money = 0;
	 	String money_str = null;
	 	while (!(deposit)) {
	 	
	 	//Here the user is asked to input there desired withdraw
		try {
    	System.out.print("How much would you like to deposit: ");
    	money_str = myObj.nextLine();
    	money_str.trim();
    	money= Integer.parseInt(account);
		}
		catch(Exception NumberFormatException) {
		 money = -1;
		}
		
		
		int num = Integer.parseInt(account);
		Account a = account_list[num];
		System.out.println("");
		System.out.println("--------------------------");
		if (a==null) {
			System.out.println("Account no longer exists");
			System.out.println("--------------------------");
			System.out.println("");
			
		} 
		
		int d = a.deposit(money);
		
		if (d==-1) {
			System.out.println("Invalid Input");
			System.out.println("--------------------------");
			System.out.println("");
		}else {
			System.out.println("Account balanca: " + d);
			System.out.println("--------------------------");
			System.out.println("");
			deposit = true;
		}
	 	}
	}

	private void  withdraw(String account) {
		Scanner myObj = new Scanner(System.in);
	 	boolean withdraw = false;
	 	int money = 0;
	 	String money_str = null;
	 	while (!(withdraw)) {
	 	
	 	//Here the user is asked to input there desired withdraw
		try {
    	System.out.print("How much would you like to withdraw: ");
    	money_str = myObj.nextLine();
    	money_str.trim();
    	money= Integer.parseInt(account);
		}
		catch(Exception NumberFormatException) {
		 money = -1;
		 money_str = "-1";
		}
		int num = Integer.parseInt(account);
		Account a = account_list[num];
		System.out.println("");
		System.out.println("--------------------------");
		if (a==null) {
			System.out.println("Account no longer exists");
			System.out.println("--------------------------");
			System.out.println("");
			break;
		}
		
		int w = a.withdraw(money);
		if (w == -1) {
			System.out.println("Insuffcient funds");
			System.out.println("--------------------------");
			System.out.println("");
			withdraw = true;
			
		} else if (w == -2) {
			System.out.println("Inavlid input");
			System.out.println("--------------------------");
			System.out.println("");
			
		}else {
			System.out.println("Account balance: " + w);
			System.out.println("--------------------------");
			System.out.println("");
			withdraw = true;
		}
	 	}
		
	}

	private boolean retrieveData(String account) {
		try {
			int num = Integer.parseInt(account);
			Account a = account_list[num];
			if (account == null) {
				System.out.println("This account does not exist");
				return false;
			} else {
				System.out.println();
				System.out.println("-----------------------------");
				System.out.println("Account Number: "+a.getNumber());
				System.out.println("Balance: "+a.getMoney());
				System.out.println("Overdraft: "+a.getOverdraft());
				System.out.println("-----------------------------");
				System.out.println();
				return true;
			}
		} catch (Exception e) {
			System.out.println("This account does not exist");
			return false;
		}
	}

	private synchronized void initialise() {
		System.out.println("1. STARTED");
		//Deal with ip
		try {
			ip = getLocalAddress();
			IP = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		System.out.println("2. IP");
		//Create a socket this is done by trying to create one until a port not in use is found
		boolean found = false;
		while (!found) {
			try {
				socket = new DatagramSocket(port,IP);
				socket_c = new DatagramSocket(port+1,IP);
				found = true;
			} catch (Exception e) {
				found = false;
				port += 1;
			}
		}
		found = false;
		while (!found) {
			try {
				socket_s = new DatagramSocket(port_s,IP);
				found = true;
			} catch (Exception e) {
				found = false;
				port_s += 1;
			}
		}
		System.out.println("3. PORT");
		//Now the node needs to be connected to the network
		/*There was a previous network who's data is still in the repository or there is an election being held for a new initial node,
		 * This node will listen for the initial node to see if it is still there,
		 * If an election is being held this will be stated in the data and then the node will wait for the election to be finished to join the network
		 */
		
		//Import.exe file is ran to get the up to date data
		try {
		ArrayList<String> command = new ArrayList<String>();
		command.add(System.getProperty("user.dir")+File.separator+"Import.bat");
		ProcessBuilder pb = new ProcessBuilder(command);
		pb.directory(new File("I:\\git\\Network"));
		Process p = pb.start();
		System.out.println("4. IMPORT");
		//Reads data
		BufferedReader br = new BufferedReader(new FileReader("Data.txt"));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();

		while (line != null) {
		sb.append(line);
		sb.append(System.lineSeparator());
		line = br.readLine();	
		}
		String everything = sb.toString();
		System.out.println("5. READ FILE");	
		//Processes the data
		String[] data = everything.split("\n");
		String[] node_data = data[0].split(" ");
		//Change later
		String[] port_data = data[1].split(" ");
		String[] IP_data = data[2].split(" ");
		String[] account_data = data[3].split(",");

		//Records the node list 

		for (int i=0; i<2024; i++){
			port_list[i]=Integer.parseInt(port_data[i]);

			if (IP_data[i].equals("NULL")){IP_list[i]=null; ip_list[i]=null;
			} else {IP_list[i]=InetAddress.getByName(IP_data[i].trim()); ip_list[i]=IP_data[i].trim();}

			if (account_data[i].equals("NULL")){account_list[i]=null;
			} else {account_list[i] = new Account(account_data[i]);}
		}
		} catch (Exception e) {
			e.printStackTrace();	
		}
		System.out.println("6. PROCESSED DATA");

			//Updates the node lists 
			int index = 0;
			for (int i=0; i<2024; i++){
				if (IP_list[i]==null){
					index = i;
					break;
				}
			}
			port_list[index] = port; 
			try { IP_list[index] = InetAddress.getByName(ip);
			} catch (Exception e) {}
			ip_list[index] = ip;
			System.out.println(ip);
			port_list[index] = port;

			//Changes the data file and updates the repository
			try{FileWriter myWriter = new FileWriter("Data.txt");
			myWriter.write(ip);
			myWriter.write("\n");
			myWriter.write(Integer.toString(port));
			IP_list[index] = InetAddress.getByName(ip);
			for (int j = 1; j<2048; j++) {

				myWriter.write(port_list[j]+" ");
			}
			myWriter.write("\n");
			for (int j = 1; j<2048; j++) {
			if (ip_list[j]==null){myWriter.write("NULL ");
			} else {myWriter.write(ip_list[j]+" ");}
			myWriter.write(" NULL");
			}
			myWriter.write("\n");
			for (int j = 1; j<2048; j++) {
			if (account_list[j]==null){myWriter.write("NULL ");
			} else {myWriter.write(account_list[j]+",");}
			myWriter.write(",NULL");
			}
			myWriter.write("\n");
			//This is the account list which will be gotten by a new node as well
			for (int j = 0; j<2048; j++) {
			myWriter.write("NULL,");
			}
			myWriter.close();
			ArrayList<String> command = new ArrayList<String>();
			command.add(System.getProperty("user.dir")+File.separator+"Commit.bat");
			ProcessBuilder pb = new ProcessBuilder(command);
			pb.directory(new File("I:\\git\\Network"));
			Process p = pb.start();
			System.out.println("10. NEW NODE COMMIT");
			} catch (Exception e){

			}
			//Notifys all of the changes
			String temp = "New Node "+port+" "+ip;
			byte[] data = temp.getBytes();
			try{for (int i=0; i<2024; i++){
			if (IP_list[i]!=null){
				DatagramPacket packet = new DatagramPacket(data, data.length,IP_list[i] ,port_list[i]);
				socket_s.send(packet);
				System.out.println("10. SENT MESSAGE");}
			}
			} catch (Exception e) {}
		}

		//FINISH
	
	
	//This returns the IP that the computer is operating on
	private static String getLocalAddress() {
	    try (DatagramSocket skt = new DatagramSocket()) {
	        // Use default gateway and arbitrary port
	        skt.connect(InetAddress.getByName("192.168.1.1"), 12345);
	        return skt.getLocalAddress().getHostAddress();
	    } catch (Exception e) {
	        return null;
	    }
		}
	
	//The main method simply creates the node and starts running it
	public static void main(String args[]) {
		Node node = new Node();
		node.run();
	}

private class Account {

	private volatile int account_number;
	private volatile int money;
	private volatile int overdraft;
	//The different constructers are useful as account data can exists in a few different forms depending on how it is being processsed
	public Account(){
		this.account_number = 0;
		this.money = 0;
		this.overdraft = 0;
	}
	
	public Account(int account_number, int money, int overdraft){
		this.account_number = account_number;
		this.money = money;
		this.overdraft = overdraft;
	}
	
	public Account(String data){
		String[] a = data.split(" ");
		if (a.length == 3){
		this.account_number =Integer.parseInt(a[0].trim());
		this.money = Integer.parseInt(a[1].trim());
		this.overdraft = Integer.parseInt(a[2].trim());
		}
	}

	public Account(String[] a){
		if (a.length == 3){
		this.account_number =Integer.parseInt(a[0].trim());
		this.money = Integer.parseInt(a[1].trim());
		this.overdraft = Integer.parseInt(a[2].trim());
		}
	}

	public int getNumber() {
		return account_number;
	}
	
	public int getMoney() {
		return money;
	}

	public int getOverdraft() {
		return overdraft;
	}
	
	//These are the three methods required to edit an account locally within a node
	public int withdraw(int withdrawl) {
	if (withdrawl>=0) {
		if (money-withdrawl < 0) {
			if (-1*(money-withdrawl) < overdraft) {
				money = money-withdrawl;
				return money;
			} else {
				return -1;
			}
		} else {
			money = money-withdrawl;
			return money;				
		}
	} else {
		return -2;
	}
	}
	
	public int deposit(int deposit) {
	if (deposit>=0) {
		money += deposit;
		return money;
	} else {
		return -1;
	}
	}
	
	public synchronized void  close() {
		account_number = -1000;			
	}

	//Data for an account can be retrieved in the form of a byte[] array or String
	public byte[] getDataFormat() {
		String a = Integer.toString(account_number) + " "  +Integer.toString(money) + " "  +Integer.toString(overdraft);
		byte[] b = a.getBytes();
		return b;
	}
	
	public String getStringFormat() {
		String a = Integer.toString(account_number) + " "  +Integer.toString(money) + " "  +Integer.toString(overdraft);
		return a;
	}	
	}
}