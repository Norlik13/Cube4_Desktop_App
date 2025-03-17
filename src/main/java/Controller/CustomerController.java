package Controller;

import Model.Customer;
import Util.ApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerController {
	private static final ApiClient apiClient = ApiClient.getInstance();
	private static final Gson gson = new Gson();

	// Get all customers
	public List<Customer> getAllCustomers() {
		try {
			String response = apiClient.get("/customers/");
			Type customersListType = new TypeToken<List<Customer>>(){}.getType();
			return gson.fromJson(response, customersListType);
		} catch(IOException | InterruptedException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	// Create a new customer
	public boolean createCustomer(String customerName, String mailAddress, String password) {
		Map<String, Object> data = new HashMap<>();
		data.put("customer_name", customerName);
		data.put("mail_address", mailAddress);
		data.put("password", password);
		String json = gson.toJson(data);
		try {
			apiClient.post("/customers/", json);
			return true;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Update a customer
	public boolean updateCustomer(Customer customer) {
		String json = gson.toJson(customer);
		try {
			apiClient.put("/customers/" + customer.getIdCustomer(), json);
			return true;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Delete a customer
	public boolean deleteCustomer(int idCustomer) {
		try {
			apiClient.delete("/customers/" + idCustomer);
			return true;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Get customer by ID
	public Customer getCustomerById(int customerId) {
		try {
			String response = apiClient.get("/customers/" + customerId);
			return gson.fromJson(response, Customer.class);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
