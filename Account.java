package Network;

public class Account {
	private int account_number;
	private int money;
	private int overdraft;
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

