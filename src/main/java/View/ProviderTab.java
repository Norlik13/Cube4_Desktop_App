package View;

import Controller.ProviderController;
import Model.Provider;
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

public class ProviderTab {

	private static ProviderController providerController;
	private ObservableList<Provider> providerData;

	public ProviderTab(ProviderController providerController) {
		this.providerController = providerController;
		this.providerData = FXCollections.observableArrayList(providerController.getAllProviders());
	}

	public Pane createProviderTablePaneWithButton() {
		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(10));

		// Retrieve providers from the API.
		List<Provider> providers = providerController.getAllProviders();
		providerData = FXCollections.observableArrayList(providers);

		// Create TableView for providers.
		TableView<Provider> table = new TableView<>(providerData);
		TableColumn<Provider, String> colDomain = new TableColumn<>("Domaine");
		colDomain.setCellValueFactory(new PropertyValueFactory<>("domain_name"));
		TableColumn<Provider, Number> colPhone = new TableColumn<>("Téléphone");
		colPhone.setCellValueFactory(new PropertyValueFactory<>("phone_number"));
		table.getColumns().addAll(colDomain, colPhone);

		// Create a button to open the add-provider dialog.
		Button btnAddProvider = new Button("Ajouter fournisseur");
		btnAddProvider.setOnAction(e -> {
			Dialog<Provider> dialog = new Dialog<>();
			dialog.setTitle("Ajouter un fournisseur");
			dialog.setHeaderText("Ajouter un nouveau fournisseur");

			ButtonType addButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
			dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(20, 150, 10, 10));

			TextField tfDomain = new TextField();
			tfDomain.setPromptText("Nom de domaine");
			TextField tfPhone = new TextField();
			tfPhone.setPromptText("Téléphone");

			grid.add(new Label("Domaine:"), 0, 0);
			grid.add(tfDomain, 1, 0);
			grid.add(new Label("Téléphone:"), 0, 1);
			grid.add(tfPhone, 1, 1);

			dialog.getDialogPane().setContent(grid);

			dialog.setResultConverter(dialogButton -> {
				if (dialogButton == addButtonType) {
					try {
						int phoneNumber = Integer.parseInt(tfPhone.getText());
						Provider newProvider = providerController.createProvider(tfDomain.getText(), phoneNumber);
						if (newProvider != null) {
							List<Provider> updatedProviders = providerController.getAllProviders();
							providerData.setAll(updatedProviders);
							//return new Provider(0, Integer.parseInt(tfPhone.getText()), tfDomain.getText());
							MainView.showAlert("Succès", "Fournisseur ajouté !");
						} else {
							MainView.showAlert("Erreur", "L'ajout du fournisseur a échoué.");
						}
					} catch (NumberFormatException ex) {
						return null;
					}
				}
				return null;
			});

			Optional<Provider> result = dialog.showAndWait();
			result.ifPresent(provider -> {
				MainView.showAlert("Succès", "Fournisseur ajouté !");
			});
		});

		Button btnModifyProvider = new Button("Modifier fournisseur");
		btnModifyProvider.setOnAction(e -> {
			Provider selectedProvider = table.getSelectionModel().getSelectedItem();
			if (selectedProvider != null) {
				Dialog<Provider> dialog = new Dialog<>();
				dialog.setTitle("Modifier un fournisseur");
				dialog.setHeaderText("Modifier le fournisseur " + selectedProvider.getDomain_name());

				ButtonType modifyButtonType = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
				dialog.getDialogPane().getButtonTypes().addAll(modifyButtonType, ButtonType.CANCEL);

				GridPane grid = new GridPane();
				grid.setHgap(10);
				grid.setVgap(10);
				grid.setPadding(new Insets(20, 150, 10, 10));

				TextField tfDomain = new TextField(selectedProvider.getDomain_name());
				TextField tfPhone = new TextField(String.valueOf(selectedProvider.getPhone_number()));

				grid.add(new Label("Domaine:"), 0, 0);
				grid.add(tfDomain, 1, 0);
				grid.add(new Label("Téléphone:"), 0, 1);
				grid.add(tfPhone, 1, 1);

				dialog.getDialogPane().setContent(grid);

				dialog.setResultConverter(dialogButton -> {
					if (dialogButton == modifyButtonType) {
						try {
							int phone = Integer.parseInt(tfPhone.getText());
							selectedProvider.setDomain_name(tfDomain.getText());
							selectedProvider.setPhone_number(phone);
							boolean success = providerController.updateProvider(selectedProvider);
							return success ? selectedProvider : null;
						} catch (NumberFormatException ex) {
							return null;
						}
					}
					return null;
				});

				Optional<Provider> result = dialog.showAndWait();
				result.ifPresent(updatedProvider -> {
					List<Provider> updatedProviders = providerController.getAllProviders();
					providerData.setAll(updatedProviders);
					MainView.showAlert("Succès", "Fournisseur modifié !");
				});
			} else {
				MainView.showAlert("Avertissement", "Veuillez sélectionner un fournisseur à modifier.");
			}
		});

		Button btnDeleteProvider = new Button("Supprimer fournisseur");
		btnDeleteProvider.setOnAction(e -> {
			Provider selectedProvider = table.getSelectionModel().getSelectedItem();
			if (selectedProvider != null) {
				boolean success = providerController.deleteProvider(selectedProvider.getIdProvider());
				if (success) {
					providerData.remove(selectedProvider);
					MainView.showAlert("Succès", "Fournisseur supprimé !");
				} else {
					MainView.showAlert("Erreur", "La suppression du fournisseur a échoué.");
				}
			} else {
				MainView.showAlert("Avertissement", "Veuillez sélectionner un fournisseur à supprimer.");
			}
		});

		vbox.getChildren().addAll(table, btnAddProvider, btnModifyProvider, btnDeleteProvider);
		return vbox;
	}

	public static void showAddProviderDialog(ComboBox<Provider> providerComboBox) {
		Dialog<Provider> dialog = new Dialog<>();
		dialog.setTitle("Add New Provider");
		dialog.setHeaderText("Add a new provider");

		ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField providerNameField = new TextField();
		providerNameField.setPromptText("Provider Name");

		TextField providerPhoneField = new TextField();
		grid.add(new Label("Provider Name:"), 0, 0);
		grid.add(providerNameField, 1, 0);
		grid.add(new Label("Phone Number:"), 0, 1);
		grid.add(providerPhoneField, 1, 1);

		dialog.getDialogPane().setContent(grid);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == addButtonType) {
				long phoneNumber = Long.parseLong(providerPhoneField.getText());
				return providerController.createProvider(providerNameField.getText(), phoneNumber);
			}
			return null;
		});

		Optional<Provider> result = dialog.showAndWait();
		result.ifPresent(provider -> {
			providerComboBox.getItems().add(provider);
			providerComboBox.setValue(provider);
		});
	}

	public void refresh() {
		providerData.setAll(providerController.getAllProviders());
	}
}
