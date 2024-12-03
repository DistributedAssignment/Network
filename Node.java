

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
	private String ip;
	private InetAddress IP;
	private int port;
	private int initial_port;
	private String initial_ip;
	private Queue<String> accounts;	
	private Queue<byte[]> messages;
	private InetAddress initial_IP;
	private DatagramSocket socket;
	private Account[] account_list;
	private int[] port_list;
	private InetAddress[] IP_list;
	private int[] l_port_list;
	private String[] node_list;
	private String name;
	private int index;
	private boolean exists;
	public Node() {
		this.accounts = new LinkedList<String>();
		this.messages = new LinkedList<byte[]>();
		this.account_list = new Account[2048];
		this.exists = false;
		this.ip = null;
		this.IP = null;
		this.port = 1;
		this.socket = null;
		this.initial_port = 1;
		this.initial_ip = null;
		this.initial_IP = null;
		this.port_list = new int[2048];
		this.IP_list = new InetAddress[2048];
		this.node_list = null;
		this.name = null;
		this.index = 0;
	}
	
	public void run() {
		/**TO DO**/
		/* TUESDAY
		 * FINISH 3
		 * FINISH 1
		 * TEST BY GRADUALLY ADDING THE STUFF CREATED START WITH CREATING AN INITIAL NODE AND TEST BY ADDING MORE STRUCTURE
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
	
	private void manager() throws Exception {
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

	 private void createAccount() throws Exception {
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
	
	 private void closeData(String account) {
		int num = Integer.parseInt(account);
		account_list[num] = null;	
	}

	private void deposit(String account) {
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

	private void withdraw(String account) {
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

	private void initialise() {
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
				found = true;
			} catch (Exception e) {
				found = false;
				port += 1;
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
		initial_port = Integer.parseInt(node_data[0].trim());
		initial_ip = node_data[1].trim();
		initial_IP = InetAddress.getByName(initial_ip);

		//Records the node list 

		for (int i=0; i<2024; i++){
			if (port_data[i].equals("0")){port_list[i] = 0;
			} else {port_list[i]=Integer.parseInt(port_data[i]);}

			if (IP_data[i].equals("NULL")){IP_list[i]=null;
			} else {IP_list[i]=InetAddress.getByName(IP_data[i]);}

			if (account_data[i].equals("NULL")){account_list[i]=null;
			} else {account_list[i] = new Account(account_data[i]);}
		}
		} catch (Exception e) {
			e.printStackTrace();	
		}
		System.out.println("6. PROCESSED DATA");
		//Now that we have the data file we can use it to initialise the connection to the nextwork
		//The data also contains the node list
		
		//Now the node listen's too see if this node still exists
		Listener search = new Listener("Connect",index);
		try {
			//Create the waiter so that it can be ready to catch the response
			Checker c = new Checker();
			(new Thread (c)).start();
			//Communication is made with the initial node
			String temp_data = "New Node Initial "+Integer.toString(port) +" "+ip+" "+index;
			byte[] data = temp_data.getBytes();
			DatagramPacket packet = new DatagramPacket(data, data.length,initial_IP,initial_port);
			socket.send(packet);
			packet = null;

			//Waiter is started
			Wait w = new Wait();
			(new Thread (w)).start();
			while (!(c.getFinished() || w.getFinished())) {
				//Waits in this while loop for one of the processes to finish
				Thread.sleep(1000);
				System.out.println("Waiting... ");
			}
			c = null;
			w = null;
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("7. LISTENED");
		/*
		*If the initial node does not exists the first things that needs to be done is the repository needs to be updated
		*
		*/
		System.out.print(exists);
		if (!exists) {
			try {
			FileWriter myWriter = new FileWriter("Data.txt");
			myWriter.write(Integer.toString(port));
			myWriter.write(" ");
			myWriter.write(ip);
			myWriter.write("\n");

			myWriter.write(Integer.toString(port));
			for (int j = 1; j<2048; j++) {
			myWriter.write(" 0");
			}
			myWriter.write("\n");

			myWriter.write(ip);
			for (int j = 1; j<2048; j++) {
			myWriter.write(" NULL");
			}
			myWriter.write("\n");
			
			//This is the account list which will be gotten by a new node as well
			for (int j = 0; j<2048; j++) {
			myWriter.write("NULL,");
			Thread.sleep(500);
			}
			myWriter.write("\n");
			myWriter.close();

			System.out.println("8. WRITTEN");
			ArrayList<String> command = new ArrayList<String>();
			command.add(System.getProperty("user.dir")+File.separator+"Commit.bat");
			ProcessBuilder pb = new ProcessBuilder(command);
			pb.directory(new File("I:\\git\\Network"));
			Process p = pb.start();
			} catch (Exception e) {
			e.printStackTrace();
			}	
			System.out.println("9. COMMIT");
		} else {

		}

	}
	
	
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
	
	/**Below are the classes which will be used by the network**/
	
	//This listens for a message from a node to see if it is still on the network
	//It can also be used to test if the previous network which existed is still in use
	private class MessageHandler implements Runnable{
		public MessageHandler() {
			
		}
		
		
		public void run() {
			String[][] updates = new String[20][5];
			String[][] nodes = new String[20][5];
			int[] times = new int[20];
			int n = 0;
			int k = 0;
			for (int i = 0; i<20; i++) {
				try {
				String[] m = (new String(messages.remove())).split(" ");
				//Preps the updates to handled separately 
				if (m[1].equals("Update")) {
					updates[n] = m;
					n+=1;
				} else if (m[1].equals("New Node")){
					nodes[k] = m;
					n+=1;
				} else if (m[1].equals("Initial Fail")){
					/***ADD LATER***/
				}  else if (m[1].equals("Initial New Node")){
					/***ADD LATER***/
				} else if (m[1].equals("New Account")){
					/***ADD LATER***/
				}
				} catch (Exception e) {
					//If all messages have been put in the block
					break;
				}
			}
			//So the update management is started
			(new Thread (new UpdateHandler(updates, times))).start();
			try {
			int j =0;
			for (int i = 0; i<nodes.length;i++) {
					j = Integer.parseInt(nodes[i][4]);
					port_list[j] = Integer.parseInt(nodes[i][1]);
					IP_list[j] = InetAddress.getByName(nodes[i][2]);
					Listener l = new Listener("Node",j);
					(new Thread(l)).start();
			}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class UpdateHandler implements Runnable{
		String[][] updates;
		public UpdateHandler(String[][] updates, int[] times ) {
			this.updates = updates;
		}
		
		
		public void run() {
			try {
			int j =0;
			for (int i = 0; i<updates.length;i++) {
				j = Integer.parseInt(updates[i][4]);
				port_list[j] = Integer.parseInt(updates[i][1]);
				IP_list[j] = InetAddress.getByName(updates[i][2]);
				Listener l = new Listener("Node",j);
				(new Thread(l)).start();
		
		}
		}
			catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private class Listener implements Runnable{
		private String type;
		private int index;
		private DatagramSocket l_socket;
		private Thread t;
		private int l_port;
		private boolean listen;
		public Listener(String t, int i) {
			this.type = t;
			this.l_socket = null;
			this.l_port = 1;
			this.t = null;
			this.index = i;
			this.listen = true;
		}
		
		public void destroy() throws Exception {
			l_socket.close();
		}
		
		public void run() {
			initialise();
			l_port_list[index] = l_port;
			while(listen) {
				//As this is listening for one node one time it just starts the times and waits
				byte[] receive = new byte[1028];
				t = (new Thread (new Timer()));
				t.start();
				try {
					DatagramPacket packet = new DatagramPacket(receive, receive.length);
					l_socket.receive(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
				//If a package is received the timer is stopped and the run() method terminates
				t.interrupt();
			}
		}
		
		private void initialise() {
			//Create a socket this is done by trying to create one until a port not in use is found
			boolean found = false;
			while (!found) {
				try {
					l_socket = new DatagramSocket(l_port,IP);
					found = true;
				} catch (Exception e) {
					found = false;
					l_port += 1;
				}
			}
		}
			
		private class Timer implements Runnable{
			long wait;
			long start_time;
			public Timer() {
				this.start_time = System.currentTimeMillis();
				this.wait = 25;
			}
			public void run() {			
			while(true) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				long end_time = System.currentTimeMillis();
				long time =(end_time - start_time)/1000;
				if (time >= wait) {
					//Begins the process of removing the node from the list
					//Unless it is of type Initial
					if (type.equals("Initial")) {
						//Process for creating new initial node
					} else {
						//Process of removing node from list
					}
				}
			}
		}
		}
	}
	
	//This waits to see if the initial node exists at point of connection
	private class Wait implements Runnable{
	long wait;
	long start_time;
	boolean finished;
	public Wait() {
		this.start_time = System.currentTimeMillis();
		this.wait = 5;
		this.finished = false;
	}
	public void run() {			
	while(true) {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long end_time = System.currentTimeMillis();
		long time =(end_time - start_time)/1000;
		if (time >= wait) {
			exists = false;
			finished = true;		
			break;
		}
	}
	}

	public boolean getFinished(){
		return finished;
	}
	}

	//This is the other part of this process
	private class Checker implements Runnable{
	boolean finished;
	public Checker() {
			this.finished = false;
	}
	public void run() {			
			try {
				byte[] receive = new byte[1028];
				DatagramPacket packet = new DatagramPacket(receive, receive.length);
				socket.receive(packet);
				System.out.println(new String(receive));
				String n = receive.toString();
				String[] node = n.split(" ");
				port_list[Integer.parseInt(node[5].trim())] = Integer.parseInt(node[1].trim());
				try {IP_list[Integer.parseInt(node[5].trim())] = InetAddress.getByName(node[2].trim());
				} catch (Exception e) {
					e.printStackTrace();	
				}
				exists = true;
				finished = true;
				
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
		public boolean getFinished(){
		return finished;	
	}
	}
	private class Receiver implements Runnable{
		private DatagramSocket socket_r;
		int r_port;
		public Receiver() {
			this.socket_r = null;
			this.r_port = 1;
		}
		
		public void run() {
			initialise();
			byte[] receive;
			while (true) {
				receive = new byte[1028];
				//Waits to receive a connection request from a client
				DatagramPacket packet = new DatagramPacket(receive, receive.length);
				try {
					socket_r.receive(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
				messages.add(receive);
				}	
			}
		
		public byte[] getMessage() {
			return messages.remove();
		}
		
		private int initialise() {
		    boolean setup= false;
			while (setup == false) {
	    	try {
	    		setup = true;
	    		socket = new DatagramSocket(port,IP);
			} catch (SocketException e) {
				setup = false;
				port +=1;
			}
			}
			return port;
		}
		
		public void destroy(){
			socket_r.close();
			messages = null;
			port = 0;
			}
	}

	private class Updater implements Runnable{
		//When ever an account is changed the node adds the account number to the accounts queue
		private DatagramSocket u_socket;
		private int u_port;
		public Updater(){
			this.u_socket = null;
			this.u_port = 1;
			
		}
		
		public void run() {
			//The socket is created 
			initialise();
			
			while (true) {
			try {
			//Tries to retrieve an account
			String temp_data;
			byte[] data;
			String account = accounts.remove();	
			/**If this an account that has been removed**/
			if (account_list[Integer.parseInt(account)] == null) {
				//Now this will be sent to every node in the network
				for (int i = 0; i < port_list.length ;i++) {
				temp_data = "Update "+account+" null "+Integer.toString(port) +" "+ip+" "+i;
				data = temp_data.getBytes();
				DatagramPacket packet = new DatagramPacket(data, data.length,IP_list[i],port_list[i]);
				u_socket.send(packet);
				packet = null;
				}
				break;
			}
			
			/**If this is an account that still exists or has been created**/
			String account_data = account_list[Integer.parseInt(account)].getStringFormat();
			
			//Now this will be sent to every node in the network
			for (int i = 0; i < port_list.length ;i++) {
			temp_data = "Update "+account_data+" "+Integer.toString(port) +" "+ip+" "+i;
			data = temp_data.getBytes();
			DatagramPacket packet = new DatagramPacket(data, data.length,IP_list[i],port_list[i]);
			u_socket.send(packet);
			packet = null;
			}
			System.err.println(name + " Updater; Sending update of account");
			} catch (NoSuchElementException | IOException e) {
				System.err.println(name + " Updater; No accounts to update");
			}
			}
		}
		
		private void initialise() {
			//Create a socket this is done by trying to create one until a port not in use is found
			boolean found = false;
			while (!found) {
				try {
					u_socket = new DatagramSocket(u_port,IP);
					found = true;
				} catch (Exception e) {
					found = false;
					u_port += 1;
				}
			}
		}
		
	}	

	private class Ping implements Runnable{
		int p_port;
		DatagramSocket p_socket;
		public Ping(String ip) {
			//Initialise the information about the socket
			this.p_port =  1;
			this.p_socket = null;
		}
		
		private void initialise() {
			
		    boolean setup= false;
			while (setup == false) {
		    	try {
		    		setup = true;
		    		p_socket = new DatagramSocket(p_port,IP);
				} catch (SocketException e) {
					setup = false;
					p_port +=1;
				}
		    }
		}
		
		public void run() {
			initialise();
			//So the ping reads from the node arrays and sends a ping to the listener 
			int i = 0;
			while (true) {
				while (i<IP_list.length) {
					if (!(IP_list[i]==null)) {
						String data_str = Integer.toString(p_port);
						byte[] data = data_str.getBytes();
						DatagramPacket packet;
						try {
							packet = new DatagramPacket(data, (data).length,IP_list[i], l_port_list[i]);
							socket.send(packet);
						} catch (Exception e) {
							e.printStackTrace();
						}
						i += 1;
					}
				}
				i = 0;
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
	private class NodeUpdater implements Runnable{
		public NodeUpdater(byte[] data){
		}
		
		public void run() {

		}
	}

	private class NodeManager implements Runnable{
		public NodeManager(){
			
		}
		
		public void run() {
			
		}
	}

	private class Account {

	private int account_number;
	private int money;
	private int overdraft;
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
	
	public void close() {
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

	


