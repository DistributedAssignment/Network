
	
	/**Below are the classes which will be used by the network**/
	
	//This listens for a message from a node to see if it is still on the network
	//It can also be used to test if the previous network which existed is still in use
	private class MessageHandler implements Runnable{
		public MessageHandler() {
			
		}
		
		
		public synchronized void  run() {
			while (true){
			try {Thread.sleep(500);
			} catch (Exception e){
				e.printStackTrace();
			}
			String[][] updates = new String[20][5];
			String[][] nodes = new String[20][5];
			int[] times = new int[20];
			int n = 0;
			int k = 0;
			boolean ups = false;
			boolean noes = false;
			for (int i = 0; i<20; i++) {
				try {
				String[] m = (new String(messages.remove())).split(" ");
				//Preps the updates and new nodes to be handled separately
				System.out.println(m[0]); 
				System.out.println(m[1]); 
				System.out.println(m[2]); 
				if (m[0].trim().equals("Update")) {
					updates[n] = m;
					n+=1;
					ups = true;
				} else if (m[0].trim().equals("New:Node")){
					nodes[k] = m;
					k+=1;
					noes = true;
				} else if (m[0].trim().equals("Initial:Fail")){
					/***ADD LATER***/
				}  else if (m[0].trim().equals("Initial:New:Node")){
					System.out.println("New Node Connecting");
					(new Thread (new NodeManager(m[1].trim(),m[2].trim()))).start();
				} else if (m[0].trim().equals("New:Account")){
					/***ADD LATER***/
				}
				} catch (Exception e) {
					//If all messages have been put in the block
					break;
				}
			}
			//So the update management is started
			if (ups){(new Thread (new UpdateHandler(updates, times))).start();}
			if (noes){
				System.out.println("Updatating node list");
				try {
				int j =0;
				for (int i = 0; i<nodes.length;i++) {
						j = Integer.parseInt(nodes[i][3].trim());
						port_list[j] = Integer.parseInt(nodes[i][1].trim());
						IP_list[j] = InetAddress.getByName(nodes[i][2].trim());
						Listener l = new Listener("Node",j);
						(new Thread(l)).start();
				}
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
		}
		}
	}
	
	private class UpdateHandler implements Runnable{
		String[][] updates;
		public UpdateHandler(String[][] updates, int[] times ) {
			this.updates = updates;
		}
		
		
		public synchronized void  run() {
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
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
private class Listener implements Runnable{
		private volatile String type;
		private volatile int index;
		private volatile DatagramSocket l_socket;
		private volatile Thread t;
		private volatile int l_port;
		private volatile boolean listen;
		public Listener(String t, int i) {
			this.type = t;
			this.l_socket = null;
			this.l_port = 1;
			this.t = null;
			this.index = i;
			this.listen = true;
		}
		
		public synchronized void  destroy() throws Exception {
			l_socket.close();
		}
		
		public synchronized void  run() {
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
		
		private synchronized void  initialise() {
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
			public synchronized void  run() {			
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


private class Receiver implements Runnable{
		public Receiver() {
		}
		
		public synchronized void  run() {
			byte[] receive;
			while (true) {
				System.out.println("Waiting");
		receive = new byte[1028];
				//Waits to receive a connection request from a client
				DatagramPacket packet = new DatagramPacket(receive, receive.length);
				try {
					socket.receive(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("Received");
				messages.add(receive);
				packet = null;
				}	
			}
		
		public byte[] getMessage() {
			return messages.remove();
		}

		public synchronized void  destroy(){
			socket.close();
			messages = null;
			port = 0;
			}
	}

private class Updater implements Runnable{
		//When ever an account is changed the node adds the account number to the accounts queue
		private volatile DatagramSocket u_socket;
		private volatile int u_port;
		public Updater(){
			this.u_socket = null;
			this.u_port = 1;
			
		}
		
		public synchronized void  run() {
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
		
		private synchronized void  initialise() {
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
		
		private synchronized void  initialise() {
			
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
		
		public synchronized void  run() {
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
							socket_s.send(packet);
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
	

	//This is the thread that updates the node list in the initial node
private class NodeManager implements Runnable{
		int ne_port;
		String ne_ip;
		public NodeManager(String p, String i){
			this.ne_port = Integer.parseInt(p);
			this.ne_ip = i;
		}
		
		public synchronized void  run() {
		System.out.println("Managing");
		int index = 0;
		for (int i=0; i<2024; i++){
			if (IP_list[i]==null){
				index = i;
				break;
			}
		}
		port_list[index] = ne_port; 
		try { IP_list[index] = InetAddress.getByName(ne_ip);
		} catch (Exception e) {}
		ip_list[index] = ne_ip;
		System.out.println(ne_ip);
		port_list[index] = ne_port;
		for (int i = 0 ;i<ip_list.length;i++)
		{
			System.out.print(ip_list[i]);
		}
		try {
			//updates the repository
			FileWriter myWriter = new FileWriter("Data.txt");
			myWriter.write(Integer.toString(port));
			myWriter.write(" ");
			myWriter.write(ip);
			myWriter.write("\n");
			myWriter.write(Integer.toString(port));
			IP_list[index] = InetAddress.getByName(ne_ip);
			for (int j = 1; j<2048; j++) {

				myWriter.write(port_list[j]+" ");
			}
			myWriter.write("\n");
			for (int j = 1; j<2048; j++) {
				System.out.println(ip_list[j]);
			if (ip_list[j]==null){myWriter.write("NULL ");
			} else {System.out.println(ip_list[j]); myWriter.write(ip_list[j]+" ");}
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

			//Send new information to the new node
			String temp_data = "New:Node "+Integer.toString(ne_port) +" "+ne_ip+" "+index;
			byte[] data = temp_data.getBytes();
			//So that it is sent to the checker socket not the usual socket this is so the checker can be closed when it is finished
			DatagramPacket packet = new DatagramPacket(data, data.length,IP_list[index],ne_port +1);
			socket_s.send(packet);
			System.out.println("10. SENT MESSAGE");
			new NodeUpdater(data, ne_port).run();
		} catch (Exception e){e.printStackTrace();}
	
		}
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

	


