package Model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;

public class Orders {
	private int idOrders;
	private LocalDate date;
	private String status;

	@SerializedName("customer_idcustomer")
	private Integer Customer_idCustomer;

	@SerializedName("provider_idprovider")
	private Integer Provider_idProvider;

	public Orders() {}

	public Orders(int idOrders, LocalDate date, String status, Integer Customer_idCustomer, Integer Provider_idProvider) {
		this.idOrders = idOrders;
		this.date = date;
		this.status = status;
		this.Customer_idCustomer = Customer_idCustomer;
		this.Provider_idProvider = Provider_idProvider;
	}

	public int getIdOrders() {
		return idOrders;
	}

	public void setIdOrders(int idOrders) {
		this.idOrders = idOrders;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getCustomer_idCustomer() {
		return Customer_idCustomer;
	}

	public void setCustomer_idCustomer(Integer customer_idCustomer) {
		this.Customer_idCustomer = customer_idCustomer;
	}

	public Integer getProvider_idProvider() {
		return Provider_idProvider;
	}

	public void setProvider_idProvider(Integer provider_idProvider) {
		this.Provider_idProvider = provider_idProvider;
	}
}
