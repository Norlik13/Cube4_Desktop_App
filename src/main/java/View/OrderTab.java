package View;

import Controller.*;
import Model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class OrderTab {
	private final OrderController orderController;
	private final CustomerController customerController;
	private final ProviderController providerController;
	private final WineController wineController;
	private ObservableList<Orders> orderData;
	private ObservableList<Wine> wineData;

	public OrderTab(OrderController orderController, CustomerController customerController, ProviderController providerController, WineController wineController) {
		this.orderController = orderController;
		this.customerController = customerController;
		this.providerController = providerController;
		this.wineController = wineController;
		this.orderData = FXCollections.observableArrayList();
		this.wineData = FXCollections.observableArrayList();
	}

	public Pane createOrderTablePaneWithDialog() {
		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(10));

		List<Orders> orders = orderController.getAllOrders();
		orderData = FXCollections.observableArrayList(orders);

		TableView<Orders> table = new TableView<>(orderData);

		// Column for customer name
		TableColumn<Orders, String> colCustomerName = new TableColumn<>("Client");
		colCustomerName.setCellValueFactory(cellData -> {
			Integer customerId = cellData.getValue().getCustomer_idCustomer();
			if (customerId != null && customerId > 0) {
				Customer customer = customerController.getCustomerById(customerId);
				return new SimpleStringProperty(customer != null ? customer.getCustomer_name() : "");
			} else {
				return new SimpleStringProperty("");
			}
		});

		// Column for provider name
		TableColumn<Orders, String> colProviderName = new TableColumn<>("Fournisseur");
		colProviderName.setCellValueFactory(cellData -> {
			Integer providerId = cellData.getValue().getProvider_idProvider();
			if (providerId != null && providerId > 0) {
				Provider provider = providerController.getProviderById(providerId);
				return new SimpleStringProperty(provider != null ? provider.getDomain_name() : "");
			} else {
				return new SimpleStringProperty("");
			}
		});

		// Column for order status
		TableColumn<Orders, String> colStatus = new TableColumn<>("Statut");
		colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

		// Column for list of wines in the order
		TableColumn<Orders, String> colWines = new TableColumn<>("Vins");
		colWines.setCellValueFactory(cellData -> {
			List<OrdersDetail> details = orderController.getOrderDetails(cellData.getValue().getIdOrders());
			return new SimpleStringProperty(details.stream()
					.map(detail -> wineController.getWineById(detail.getWine_idWine()).getCuvee_name())
					.collect(Collectors.joining(", ")));
		});

		table.getColumns().addAll(colCustomerName, colProviderName, colStatus, colWines);

		Button btnAddOrder = new Button("Ajouter commande");
		btnAddOrder.setOnAction(e -> showAddOrderDialog());

		Button btnShowDetails = new Button("Afficher détails");
		btnShowDetails.setOnAction(e -> {
			Orders selectedOrder = table.getSelectionModel().getSelectedItem();
			if (selectedOrder != null) {
				showOrderDetailsDialog(selectedOrder);
			} else {
				MainView.showAlert("Avertissement", "Veuillez sélectionner une commande à afficher.");
			}
		});

		Button btnDeleteOrder = new Button("Supprimer commande");
		btnDeleteOrder.setOnAction(e -> {
			Orders selectedOrder = table.getSelectionModel().getSelectedItem();
			if (selectedOrder != null) {
				boolean success = orderController.deleteOrder(selectedOrder.getIdOrders());
				if (success) {
					orderData.remove(selectedOrder);
					MainView.showAlert("Succès", "Commande supprimée avec succès.");
				} else {
					MainView.showAlert("Erreur", "La suppression de la commande a échoué.");
				}
			} else {
				MainView.showAlert("Avertissement", "Veuillez sélectionner une commande à supprimer.");
			}
		});

		Button btnValiderCommande = new Button("Valider commande");
		btnValiderCommande.setOnAction(e -> {
			Orders selectedOrder = table.getSelectionModel().getSelectedItem();
			if (selectedOrder != null) {
				boolean success = orderController.validateOrder(selectedOrder);
				if (success) {
					if (selectedOrder.getProvider_idProvider() != null) //test if order to provider or order from client
					{
						List<OrdersDetail> details = orderController.getOrderDetails(selectedOrder.getIdOrders());
						for (OrdersDetail detail : details) {
							int wine_id = detail.getWine_idWine();
							Wine wine = wineController.getWineById(wine_id);
							int newStock = wine.getStock_quantity() + detail.getQuantity();
							wineController.updateInventory(wine.getIdWine(), newStock);
						}
					}

					List<Orders> updatedOrders = orderController.getAllOrders();
					orderData.setAll(updatedOrders);

					List<Wine> updatedWines = wineController.getAllWines();
					wineData.setAll(updatedWines);

					MainView.showAlert("Succès", "Commande validée et stock mis à jour !");
				} else {
					MainView.showAlert("Erreur", "La validation de la commande a échoué.");
				}
			} else {
				MainView.showAlert("Erreur", "Veuillez sélectionner une commande.");
			}
		});

		HBox buttonBox = new HBox(10, btnAddOrder, btnShowDetails, btnDeleteOrder, btnValiderCommande);
		vbox.getChildren().addAll(table, buttonBox);
		return vbox;
	}

	private void showAddOrderDialog() {
		Dialog<Void> dialog = new Dialog<>();
		dialog.setTitle("Ajouter une commande");
		dialog.setHeaderText("Saisissez les informations de la commande");
		dialog.setResizable(true);

		ButtonType addButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(10));

		// Customer selection
		ComboBox<Customer> cbCustomer = new ComboBox<>(FXCollections.observableArrayList(customerController.getAllCustomers()));
		cbCustomer.setConverter(new StringConverter<Customer>() {
			@Override
			public String toString(Customer customer) {
				return customer != null ? customer.getCustomer_name() : "";
			}

			@Override
			public Customer fromString(String string) {
				return null;
			}
		});
		Button btnAddCustomer = new Button("+");
		btnAddCustomer.setOnAction(e -> CustomerTab.showAddCustomerDialog(cbCustomer));

		HBox customerBox = new HBox(10, new Label("Client:"), cbCustomer, btnAddCustomer);

		// List for order details
		ObservableList<OrdersDetail> detailsData = FXCollections.observableArrayList();
		TableView<OrdersDetail> detailsTable = new TableView<>(detailsData);

		TableColumn<OrdersDetail, String> colDomainName = new TableColumn<>("Domaine");
		colDomainName.setCellValueFactory(cellData -> {
			int wine_id = cellData.getValue().getWine_idWine();
			Wine wine = wineController.getWineById(wine_id);
			Provider provider = wine.getProvider();
			return new SimpleStringProperty(provider != null ? provider.getDomain_name() : "Unknown Provider");
		});

		TableColumn<OrdersDetail, String> colCuveeName = new TableColumn<>("Cuvée");
		colCuveeName.setCellValueFactory(cellData ->{
			int wine_id = cellData.getValue().getWine_idWine();
			Wine wine = wineController.getWineById(wine_id);
			return new SimpleStringProperty(wine.getCuvee_name());
		});

		TableColumn<OrdersDetail, Number> colQty = new TableColumn<>("Quantité");
		colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));

		detailsTable.getColumns().addAll(colDomainName, colCuveeName, colQty);

		Button btnAddDetail = new Button("Ajout produit");
		btnAddDetail.setOnAction(e -> showAddProductDialog(detailsData));

		Button btnModifyDetail = new Button("Modifier produit");
		btnModifyDetail.setOnAction(e -> {
			OrdersDetail selectedDetail = detailsTable.getSelectionModel().getSelectedItem();
			if (selectedDetail != null) {
				showModifyProductDialog(selectedDetail);
			} else {
				MainView.showAlert("Avertissement", "Veuillez sélectionner un produit à modifier.");
			}
		});

		Button btnDeleteDetail = new Button("Supprimer produit");
		btnDeleteDetail.setOnAction(e -> {
			OrdersDetail selectedDetail = detailsTable.getSelectionModel().getSelectedItem();
			if (selectedDetail != null) {
				detailsData.remove(selectedDetail);
			} else {
				MainView.showAlert("Avertissement", "Veuillez sélectionner un produit à supprimer.");
			}
		});

		HBox detailButtonsBox = new HBox(10, btnAddDetail, btnModifyDetail, btnDeleteDetail);

		vbox.getChildren().addAll(customerBox, new Label("Détails de la commande:"), detailsTable, detailButtonsBox);

		dialog.getDialogPane().setContent(vbox);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == addButtonType) {
				Customer selectedCustomer = cbCustomer.getValue();
				if (selectedCustomer != null && !detailsData.isEmpty()) {
					boolean success = orderController.createCustomerOrderWithDetails(selectedCustomer.getIdCustomer(), detailsData);
					if (success) {
						// Update stock quantities
						for (OrdersDetail detail : detailsData) {
							int wine_id = detail.getWine_idWine();
							Wine wine = wineController.getWineById(wine_id);
							int newStock = wine.getStock_quantity() - detail.getQuantity();
							wineController.updateInventory(wine.getIdWine(), newStock);

							// Check if stock goes below 12 and order more wine if necessary
							if (newStock < 24) {
								int quantityToOrder = 48 - newStock;
								Orders providerOrder = new Orders();
								providerOrder.setProvider_idProvider(wine.getProvider().getIdProvider());
								providerOrder.setStatus("processing");

								OrdersDetail orderDetail = new OrdersDetail();
								orderDetail.setWine_idWine(wine_id);
								orderDetail.setQuantity(quantityToOrder);
								orderDetail.setOrders_idOrders(providerOrder.getIdOrders());

								orderController.createProviderOrderWithDetails(wine.getProvider().getIdProvider(), List.of(orderDetail));
							}
						}

						// Refresh the order list
						List<Orders> updatedOrders = orderController.getAllOrders();
						orderData.setAll(updatedOrders);

						// Refresh the wine list
						List<Wine> updatedWines = wineController.getAllWines();
						wineData.setAll(updatedWines);

						MainView.showAlert("Succès", "Commande ajoutée avec succès !");
					} else {
						MainView.showAlert("Erreur", "L'ajout de la commande a échoué.");
					}
				} else {
					MainView.showAlert("Avertissement", "Veuillez sélectionner un client et ajouter des produits.");
				}
			}
			return null;
		});

		dialog.showAndWait();
	}

	private void showModifyProductDialog(OrdersDetail detail) {
		Dialog<OrdersDetail> detailDialog = new Dialog<>();
		detailDialog.setTitle("Modifier produit");
		detailDialog.setHeaderText("Modifier le produit");
		detailDialog.setResizable(true);

		ButtonType detailModifyButtonType = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
		detailDialog.getDialogPane().getButtonTypes().addAll(detailModifyButtonType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10));

		Wine wine = wineController.getWineById(detail.getWine_idWine());
		Label lblWine = new Label(wine != null ? wine.getCuvee_name() : "Unknown Wine");

		TextField tfQuantity = new TextField(String.valueOf(detail.getQuantity()));
		tfQuantity.setPromptText("Quantité");

		grid.add(new Label("Vin:"), 0, 0);
		grid.add(lblWine, 1, 0);
		grid.add(new Label("Quantité:"), 0, 1);
		grid.add(tfQuantity, 1, 1);

		detailDialog.getDialogPane().setContent(grid);
		detailDialog.setResultConverter(button -> {
			if (button == detailModifyButtonType) {
				try {
					int quantity = Integer.parseInt(tfQuantity.getText());
					detail.setQuantity(quantity);
					return detail;
				} catch (NumberFormatException ex) {
					MainView.showAlert("Erreur", "Veuillez saisir une quantité valide.");
				}
			}
			return null;
		});

		detailDialog.showAndWait();
	}

	private void showAddProductDialog(ObservableList<OrdersDetail> detailsData) {
		Dialog<OrdersDetail> detailDialog = new Dialog<>();
		detailDialog.setTitle("Ajouter produit");
		detailDialog.setHeaderText("Ajouter un produit");
		detailDialog.setResizable(true);

		ButtonType detailAddButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
		detailDialog.getDialogPane().getButtonTypes().addAll(detailAddButtonType, ButtonType.CANCEL);

		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(10));

		// Filter out already selected wines
		List<Wine> availableWines = wineController.getAllWines().stream()
				.filter(wine -> detailsData.stream().noneMatch(detail -> detail.getWine_idWine() == wine.getIdWine()))
				.collect(Collectors.toList());

		TableView<Wine> wineTable = new TableView<>(FXCollections.observableArrayList(availableWines));
		TableColumn<Wine, String> colDomainName = new TableColumn<>("Domaine");
		colDomainName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProvider().getDomain_name()));
		TableColumn<Wine, String> colWineName = new TableColumn<>("Cuvée");
		colWineName.setCellValueFactory(new PropertyValueFactory<>("cuvee_name"));
		TableColumn<Wine, Number> colWineStock = new TableColumn<>("Stock");
		colWineStock.setCellValueFactory(new PropertyValueFactory<>("stock_quantity"));
		wineTable.getColumns().addAll(colDomainName, colWineName, colWineStock);

		wineTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		TextField tfQuantity = new TextField();
		tfQuantity.setPromptText("Quantité");

		vbox.getChildren().addAll(new Label("Sélectionner un vin:"), wineTable, new Label("Quantité:"), tfQuantity);
		VBox.setVgrow(wineTable, Priority.ALWAYS);

		detailDialog.getDialogPane().setContent(vbox);

		detailDialog.setResultConverter(button -> {
			if (button == detailAddButtonType) {
				Wine selectedWine = wineTable.getSelectionModel().getSelectedItem();
				if (selectedWine != null) {
					try {
						int quantity = Integer.parseInt(tfQuantity.getText());
						OrdersDetail od = new OrdersDetail();
						od.setWine_idWine(selectedWine.getIdWine());
						od.setQuantity(quantity);
						return od;
					} catch (NumberFormatException ex) {
						MainView.showAlert("Erreur", "Quantité invalide.");
					}
				} else {
					MainView.showAlert("Erreur", "Veuillez sélectionner un vin.");
				}
			}
			return null;
		});

		Optional<OrdersDetail> detailResult = detailDialog.showAndWait();
		detailResult.ifPresent(detailsData::add);
	}

	private void showOrderDetailsDialog(Orders order) {
		Stage dialogStage = new Stage();
		dialogStage.setTitle("Détails de la commande " + order.getIdOrders());
		dialogStage.setResizable(true);

		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(10));

		List<OrdersDetail> details = orderController.getOrderDetails(order.getIdOrders());
		ObservableList<OrdersDetail> detailData = FXCollections.observableArrayList(details);

		TableView<OrdersDetail> detailTable = new TableView<>(detailData);
		TableColumn<OrdersDetail, Number> colQuantity = new TableColumn<>("Quantité");
		colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

		TableColumn<OrdersDetail, String> colProvider = new TableColumn<>("Fournisseur");
		colProvider.setCellValueFactory(cellData -> {
			Wine wine = wineController.getWineById(cellData.getValue().getWine_idWine());
			return new SimpleStringProperty(wine != null ? wine.getProvider().getDomain_name() : "Unknown");
		});

		TableColumn<OrdersDetail, String> colWineName = new TableColumn<>("Cuvée");
		colWineName.setCellValueFactory(cellData -> {
			Wine wine = wineController.getWineById(cellData.getValue().getWine_idWine());
			return new SimpleStringProperty(wine != null ? wine.getCuvee_name() : "Unknown");
		});

		detailTable.getColumns().addAll(colProvider, colWineName, colQuantity);

		Button btnAddDetail = new Button("Ajouter produit");
		Button btnRemoveDetail = new Button("Supprimer un produit");

		btnAddDetail.setOnAction(e -> {
			Dialog<OrdersDetail> detailDialog = new Dialog<>();
			detailDialog.setTitle("Ajouter produit");
			detailDialog.setHeaderText("Ajouter un produit à la commande " + order.getIdOrders());
			ButtonType addButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
			detailDialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(20, 150, 10, 10));

			// Filter out already selected wines
			List<Wine> availableWines = wineController.getAllWines().stream().collect(Collectors.toList());

			TableView<Wine> wineTable = new TableView<>(FXCollections.observableArrayList(availableWines));
			TableColumn<Wine, String> colDomainName = new TableColumn<>("Domaine");
			colDomainName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProvider().getDomain_name()));
			TableColumn<Wine, String> colcuvee = new TableColumn<>("Cuvée");
			colcuvee.setCellValueFactory(new PropertyValueFactory<>("cuvee_name"));
			TableColumn<Wine, Number> colWineStock = new TableColumn<>("Stock");
			colWineStock.setCellValueFactory(new PropertyValueFactory<>("stock_quantity"));
			wineTable.getColumns().addAll(colDomainName, colcuvee, colWineStock);

			TextField tfQuantityDetail = new TextField();
			tfQuantityDetail.setPromptText("Quantité");

			grid.add(new Label("Sélectionner un vin:"), 0, 0);
			grid.add(wineTable, 0, 1, 2, 1);
			grid.add(new Label("Quantité:"), 0, 2);
			grid.add(tfQuantityDetail, 1, 2);

			detailDialog.getDialogPane().setContent(grid);
			detailDialog.setResultConverter(dialogButton -> {
				if (dialogButton == addButtonType) {
					Wine selectedWine = wineTable.getSelectionModel().getSelectedItem();
					if (selectedWine != null) {
						try {
							int quantity = Integer.parseInt(tfQuantityDetail.getText());
							OrdersDetail od = new OrdersDetail();
							od.setOrders_idOrders(order.getIdOrders());
							od.setWine_idWine(selectedWine.getIdWine());
							od.setQuantity(quantity);
							return od;
						} catch (NumberFormatException ex) {
							MainView.showAlert("Erreur", "Quantité invalide.");
						}
					} else {
						MainView.showAlert("Erreur", "Veuillez sélectionner un vin.");
					}
				}
				return null;
			});

			Optional<OrdersDetail> detailResult = detailDialog.showAndWait();
			detailResult.ifPresent(od -> {
				boolean success = orderController.addOrderDetail(od.getOrders_idOrders(), od.getWine_idWine(), od.getQuantity());
				if (success) {
					//Update stock quantity
					Wine selectedWine = wineTable.getSelectionModel().getSelectedItem();
					if (selectedWine != null) {
						selectedWine.setStock_quantity(selectedWine.getStock_quantity() - od.getQuantity());

						wineTable.refresh();
					}
					List<OrdersDetail> updatedDetails = orderController.getOrderDetails(order.getIdOrders());
					detailData.setAll(updatedDetails);
				} else {
					MainView.showAlert("Erreur", "L'ajout du produit a échoué.");
				}
			});
		});

		btnRemoveDetail.setOnAction(e -> {
			OrdersDetail selectedDetail = detailTable.getSelectionModel().getSelectedItem();
			if (selectedDetail != null) {
				boolean success = orderController.removeOrderDetail(order.getIdOrders() ,selectedDetail.getIdOrdersDetail());
				if (success) {
					List<OrdersDetail> updatedDetails = orderController.getOrderDetails(order.getIdOrders());
					detailData.setAll(updatedDetails);
				} else {
					MainView.showAlert("Erreur", "La suppression du produit a échoué.");
				}
			} else {
				MainView.showAlert("Avertissement", "Veuillez sélectionner un produit à supprimer.");
			}
		});

		HBox buttonBox = new HBox(10, btnAddDetail, btnRemoveDetail);
		vbox.getChildren().addAll(detailTable, buttonBox);
		Scene scene = new Scene(vbox, 600, 400);
		dialogStage.setScene(scene);
		dialogStage.showAndWait();
	}

	public void refresh() {
		orderData.setAll(orderController.getAllOrders());
	}
}
