package Controller;

import Model.Orders;
import Model.OrdersDetail;
import Model.Wine;
import Util.ApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderController {
	private final ApiClient apiClient;
	private final Gson gson;
	private final WineController wineController = new WineController();

	public OrderController() {
		this.apiClient = ApiClient.getInstance();
		this.gson = new Gson();
	}

	public List<Orders> getAllOrders() {
		try {
			String response = apiClient.get("/orders/");
			Type ordersListType = new TypeToken<List<Orders>>(){}.getType();
			List<Orders> orders = gson.fromJson(response, ordersListType);
			return orders;
		} catch(IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<OrdersDetail> getOrderDetails(int orderId) {
		try {
			String response = apiClient.get("/orders/" + orderId + "/details");
			Type OrdersDetailListType = new TypeToken<List<OrdersDetail>>(){}.getType();
			List<OrdersDetail> details = gson.fromJson(response, OrdersDetailListType);
			return details;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean addOrderDetail(int orderId, int wineId, int quantity) {
		Map<String, Object> data = new HashMap<>();
		data.put("quantity", quantity);
		data.put("status", "processing");
		data.put("Wine_idWine", wineId);
		String json = gson.toJson(data);
		try {
			apiClient.post("/orders/" + orderId + "/details", json);
			return true;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean removeOrderDetail(int orderId ,int orderDetailId) {
		try {
			apiClient.delete("/orders/" + orderId + "/details/" + orderDetailId);
			return true;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean createProviderOrderWithDetails(int providerId, List<OrdersDetail> details) {
		Map<String, Object> payload = new HashMap<>();
		payload.put("provider_idprovider", providerId);
		payload.put("customer_idcustomer", null);
		payload.put("status", "processing");
		payload.put("order_details", details);
		String json = gson.toJson(payload);
		try {
			apiClient.post("/orders/", json);
			return true;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean createCustomerOrderWithDetails(int customerId, List<OrdersDetail> details) {
		Map<String, Object> payload = new HashMap<>();
		payload.put("provider_idprovider", null);
		payload.put("customer_idcustomer", customerId);
		payload.put("status", "processing");
		payload.put("order_details", details);
		String json = gson.toJson(payload);
		try {
			apiClient.post("/orders/", json);
			return true;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteOrder(int orderId) {
		try {
			apiClient.delete("/orders/" + orderId);
			return true;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean validateOrder(Orders order) {
		order.setStatus("OK");
		String json = gson.toJson(order);
		try {
			apiClient.put("/orders/" + order.getIdOrders(), json);
			if (order.getProvider_idProvider() != null) {
				for (OrdersDetail detail : getOrderDetails(order.getIdOrders())) {
					Wine wine = wineController.getWineById(detail.getWine_idWine());
					wine.setStock_quantity(wine.getStock_quantity() + detail.getQuantity());
					wineController.updateWine(wine);
				}
			}
			return true;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}
}

