package Model;

public class Customer {
	private int idCustomer;
	private String customer_name;
	private String mail_address;
	private String password;

	public Customer() {}

	public Customer(int idCustomer, String customer_name, String mail_address, String password) {
		this.idCustomer = idCustomer;
		this.customer_name = customer_name;
		this.mail_address = mail_address;
		this.password = password;
	}

	public int getIdCustomer() {
		return idCustomer;
	}

	public void setIdCustomer(int idCustomer) {
		this.idCustomer = idCustomer;
	}

	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	public String getMail_address() {
		return mail_address;
	}

	public void setMail_address(String mail_address) {
		this.mail_address = mail_address;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public String toString() {
		return "Customer{" +
				"idCustomer=" + idCustomer +
				", customerName='" + customer_name + '\'' +
				", mailAddress='" + mail_address + '\'' +
				'}';
	}
}

