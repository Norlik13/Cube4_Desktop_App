package View;

import Controller.CustomerController;
import Controller.OrderController;
import Controller.ProviderController;
import Controller.WineController;
import Model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

public class CustomerTab {

	private static CustomerController customerController;
	private ObservableList<Customer> customerData;

	public CustomerTab(CustomerController customerController) {
		this.customerController = customerController;
		this.customerData = FXCollections.observableArrayList(customerController.getAllCustomers());
	}



	public Pane createCustomerTablePaneWithButton() {
		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(10));

		// Retrieve current list of customers from the API.
		List<Customer> customers = customerController.getAllCustomers();
		customerData = FXCollections.observableArrayList(customers);

		// Create TableView to display customers.
		TableView<Customer> table = new TableView<>(customerData);
		TableColumn<Customer, String> colName = new TableColumn<>("Nom");
		colName.setCellValueFactory(new PropertyValueFactory<>("customer_name"));
		TableColumn<Customer, String> colMail = new TableColumn<>("Mail");
		colMail.setCellValueFactory(new PropertyValueFactory<>("mail_address"));
		table.getColumns().addAll(colName, colMail);

		// Create a button that opens a dialog to add a new client.
		Button btnAddClient = new Button("Ajouter client");
		btnAddClient.setOnAction(e -> {
			Dialog<Customer> dialog = new Dialog<>();
			dialog.setTitle("Ajouter un client");
			dialog.setHeaderText("Ajouter un nouveau client");

			ButtonType addButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
			dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(20, 150, 10, 10));

			TextField tfName = new TextField();
			tfName.setPromptText("Nom du client");
			TextField tfMail = new TextField();
			tfMail.setPromptText("Mail");
			PasswordField pfPassword = new PasswordField();
			pfPassword.setPromptText("Mot de passe");

			grid.add(new Label("Nom:"), 0, 0);
			grid.add(tfName, 1, 0);
			grid.add(new Label("Mail:"), 0, 1);
			grid.add(tfMail, 1, 1);
			grid.add(new Label("Mot de passe:"), 0, 2);
			grid.add(pfPassword, 1, 2);

			dialog.getDialogPane().setContent(grid);

			// Convert the result when the "Ajouter" button is clicked.
			dialog.setResultConverter(dialogButton -> {
				if (dialogButton == addButtonType) {
					boolean success = customerController.createCustomer(tfName.getText(), tfMail.getText(), pfPassword.getText());
					if (success) {
						// Create a new Customer object (ID can be 0 as it will be refreshed)
						return new Customer(0, tfName.getText(), tfMail.getText(), pfPassword.getText());
					}
				}
				return null;
			});

			Optional<Customer> result = dialog.showAndWait();
			result.ifPresent(newCustomer -> {
				// Refresh the TableView with the updated list.
				List<Customer> updatedCustomers = customerController.getAllCustomers();
				customerData.setAll(updatedCustomers);
				MainView.showAlert("Succès", "Client ajouté !");
			});
		});

		Button btnModifyClient = new Button("Modifier client");
		btnModifyClient.setOnAction(e -> {
			Customer selectedClient = table.getSelectionModel().getSelectedItem();
			if (selectedClient != null) {
				Dialog<Customer> dialog = new Dialog<>();
				dialog.setTitle("Modifier un client");
				dialog.setHeaderText("Modifier le client " + selectedClient.getCustomer_name());

				ButtonType modifyButtonType = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
				dialog.getDialogPane().getButtonTypes().addAll(modifyButtonType, ButtonType.CANCEL);

				GridPane grid = new GridPane();
				grid.setHgap(10);
				grid.setVgap(10);
				grid.setPadding(new Insets(20, 150, 10, 10));

				TextField tfName = new TextField(selectedClient.getCustomer_name());
				TextField tfMail = new TextField(selectedClient.getMail_address());
				PasswordField pfPassword = new PasswordField();
				pfPassword.setPromptText("laisser vide si inchangé)");

				grid.add(new Label("Nom:"), 0, 0);
				grid.add(tfName, 1, 0);
				grid.add(new Label("Mail:"), 0, 1);
				grid.add(tfMail, 1, 1);
				grid.add(new Label("Mot de passe:"), 0, 2);
				grid.add(pfPassword, 1, 2);

				dialog.getDialogPane().setContent(grid);

				dialog.setResultConverter(dialogButton -> {
					if (dialogButton == modifyButtonType) {
						selectedClient.setCustomer_name(tfName.getText());
						selectedClient.setMail_address(tfMail.getText());
						if (!pfPassword.getText().isEmpty()) {
							selectedClient.setPassword(pfPassword.getText());
						}
						boolean success = customerController.updateCustomer(selectedClient);
						return success ? selectedClient : null;
					}
					return null;
				});

				Optional<Customer> result = dialog.showAndWait();
				result.ifPresent(updatedClient -> {
					List<Customer> updatedCustomers = customerController.getAllCustomers();
					customerData.setAll(updatedCustomers);
					MainView.showAlert("Succès", "Client modifié !");
				});
			} else {
				MainView.showAlert("Avertissement", "Veuillez sélectionner un client à modifier.");
			}
		});

		Button btnDeleteClient = new Button("Supprimer client");
		btnDeleteClient.setOnAction(e -> {
			Customer selectedClient = table.getSelectionModel().getSelectedItem();
			if (selectedClient != null) {
				boolean success = customerController.deleteCustomer(selectedClient.getIdCustomer());
				if (success) {
					table.getItems().remove(selectedClient);
					MainView.showAlert("Succès", "Client supprimé !");
				} else {
					MainView.showAlert("Erreur", "La suppression du client a échoué.");
				}
			} else {
				MainView.showAlert("Avertissement", "Veuillez sélectionner un client à supprimer.");
			}
		});

		vbox.getChildren().addAll(table, btnAddClient, btnModifyClient, btnDeleteClient);
		return vbox;
	}

	public static void showAddCustomerDialog(ComboBox<Customer> cbCustomer) {
		Dialog<Customer> dialog = new Dialog<>();
		dialog.setTitle("Ajouter un client");
		dialog.setHeaderText("Ajouter un nouveau client");
		dialog.setResizable(true);

		ButtonType addButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField tfName = new TextField();
		tfName.setPromptText("Nom du client");
		TextField tfMail = new TextField();
		PasswordField pfPassword = new PasswordField();
		pfPassword.setPromptText("Mot de passe");

		grid.add(new Label("Nom:"), 0, 0);
		grid.add(tfName, 1, 0);
		grid.add(new Label("Mail:"), 0, 1);
		grid.add(tfMail, 1, 1);
		grid.add(new Label("Mot de passe:"), 0, 2);
		grid.add(pfPassword, 1, 2);

		dialog.getDialogPane().setContent(grid);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == addButtonType) {
				boolean success = customerController.createCustomer(tfName.getText(), tfMail.getText(), pfPassword.getText());
				if (success) {
					List<Customer> updatedCustomers = customerController.getAllCustomers();
					for (Customer customer : updatedCustomers) {
						if (customer.getCustomer_name().equals(tfName.getText()) && customer.getMail_address().equals(tfMail.getText())) {
							return customer;
						}
					}
				}
			}
			return null;
		});

		Optional<Customer> result = dialog.showAndWait();
		result.ifPresent(newCustomer -> {
			cbCustomer.getItems().add(newCustomer);
			cbCustomer.setValue(newCustomer);
		});
	}

	public void refresh() {
		customerData.setAll(customerController.getAllCustomers());
	}
}
