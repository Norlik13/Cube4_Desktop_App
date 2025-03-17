package Model;


public class OrdersDetail {
	private int idOrdersDetail;
	private int quantity;
	private int Wine_idWine;
	private int Orders_idOrders;

	public OrdersDetail() {}

	public OrdersDetail(int idOrdersDetail, int quantity, int Wine_idWine, int Orders_idOrders) {
		this.idOrdersDetail = idOrdersDetail;
		this.quantity = quantity;
		this.Wine_idWine = Wine_idWine;
		this.Orders_idOrders = Orders_idOrders;
	}

	public int getIdOrdersDetail() {
		return idOrdersDetail;
	}

	public void setIdOrdersDetail(int idOrdersDetail) {
		this.idOrdersDetail = idOrdersDetail;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getWine_idWine() {
		return Wine_idWine;
	}

	public void setWine_idWine(int Wine_idWine) {
		this.Wine_idWine = Wine_idWine;
	}

	public int getOrders_idOrders() {
		return Orders_idOrders;
	}

	public void setOrders_idOrders(int Orders_idOrders) {
		this.Orders_idOrders = Orders_idOrders;
	}

	@Override
	public String toString() {
		return "OrdersDetail{" +
				"idOrderDetail=" + idOrdersDetail +
				", quantity=" + quantity +
				", wineId=" + Wine_idWine +
				", orderId=" + Orders_idOrders +
				'}';
	}
}
