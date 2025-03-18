package View;

import Controller.*;
import Model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WineTab {

	private WineController wineController;
	private ColorController colorController;
	private CepageController cepageController;
	private AppellationController appellationControler;
	private ProviderController providerController;
	private OrderController orderController;
	private CustomerController customerController;
	private ObservableList<Provider> providerData;
	private ObservableList<Orders> orderData;
	private ObservableList<Customer> customerData;
	private ObservableList<Wine> wineData;

	public WineTab(OrderController orderController, CustomerController customerController, ProviderController providerController, WineController wineController, ColorController colorController, CepageController cepageController, AppellationController appellationController) {
		this.orderController = orderController;
		this.customerController = customerController;
		this.providerController = providerController;
		this.wineController = wineController;
		this.colorController = colorController;
		this.cepageController = cepageController;
		this.appellationControler = appellationController;
		this.wineData = FXCollections.observableArrayList(wineController.getAllWines());
		this.providerData = FXCollections.observableArrayList(providerController.getAllProviders());
		this.orderData = FXCollections.observableArrayList(orderController.getAllOrders());
		this.customerData = FXCollections.observableArrayList(customerController.getAllCustomers());
	}

	public Pane createWineTablePaneWithForm() {
		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(10));

		// Retrieve wines and set up filtering (as in your previous "Données" tab)
		List<Wine> wines = wineController.getAllWines();
		wineData = FXCollections.observableArrayList(wines);

		// Create filter ComboBoxes.
		ComboBox<String> cbCuvee = new ComboBox<>();
		ComboBox<Integer> cbVintage = new ComboBox<>();
		ComboBox<String> cbAppellation = new ComboBox<>();
		ComboBox<String> cbEffervescent = new ComboBox<>();
		ComboBox<String> cbColor = new ComboBox<>();

		List<String> distinctCuvees = wineData.stream()
				.map(Wine::getCuvee_name)
				.distinct()
				.sorted(String::compareToIgnoreCase)
				.collect(Collectors.toList());
		distinctCuvees.add(0, "Tous");
		cbCuvee.setItems(FXCollections.observableArrayList(distinctCuvees));
		cbCuvee.getSelectionModel().selectFirst();

		List<Integer> distinctVintages = wineData.stream()
				.map(Wine::getVintage)
				.distinct()
				.sorted()
				.collect(Collectors.toList());
		distinctVintages.add(0, 0);
		cbVintage.setItems(FXCollections.observableArrayList(distinctVintages));
		cbVintage.getSelectionModel().selectFirst();

		List<String> distinctAppellations = wineData.stream()
				.map(w -> {
					Appellation app = w.getAppellation();
					return app != null ? app.getAppellation() : "";
				})
				.filter(s -> !s.isEmpty())
				.distinct()
				.sorted(String::compareToIgnoreCase)
				.collect(Collectors.toList());
		distinctAppellations.add(0, "Tous");
		cbAppellation.setItems(FXCollections.observableArrayList(distinctAppellations));
		cbAppellation.getSelectionModel().selectFirst();

		cbEffervescent.setItems(FXCollections.observableArrayList("Tous", "Oui", "Non"));
		cbEffervescent.getSelectionModel().selectFirst();

		List<String> distinctColors = wineData.stream()
				.map(w -> {
					Color c = w.getColor();
					return c != null ? c.getColor() : "";
				})
				.filter(s -> !s.isEmpty())
				.distinct()
				.sorted(String::compareToIgnoreCase)
				.collect(Collectors.toList());
		distinctColors.add(0, "Tous");
		cbColor.setItems(FXCollections.observableArrayList(distinctColors));
		cbColor.getSelectionModel().selectFirst();

		HBox filterBox = new HBox(10);
		filterBox.getChildren().addAll(
				new Label("Cuvée:"), cbCuvee,
				new Label("Année:"), cbVintage,
				new Label("Appellation:"), cbAppellation,
				new Label("Effervescent:"), cbEffervescent,
				new Label("Couleur:"), cbColor
		);

		TableView<Wine> tableView = new TableView<>();

		TableColumn<Wine, String> domainColumn = new TableColumn<>("Domaine");
		domainColumn.setCellValueFactory(cellData -> {
			Provider c = cellData.getValue().getProvider();
			return new SimpleStringProperty(c != null ? c.getDomain_name() : "");
		});


		TableColumn<Wine, String> colCuvee = new TableColumn<>("Cuvée");
		colCuvee.setCellValueFactory(new PropertyValueFactory<>("cuvee_name"));

		TableColumn<Wine, Number> colVintage = new TableColumn<>("Année");
		colVintage.setCellValueFactory(new PropertyValueFactory<>("vintage"));

		TableColumn<Wine, String> colAppellation = new TableColumn<>("Appellation");
		colAppellation.setCellValueFactory(cellData -> {
			Appellation app = cellData.getValue().getAppellation();
			return new SimpleStringProperty(app != null ? app.getAppellation() : "");
		});

		TableColumn<Wine, String> colEffervescent = new TableColumn<>("Effervescent");
		colEffervescent.setCellValueFactory(cellData -> {
			boolean sparkling = cellData.getValue().isSparkling();
			return new SimpleStringProperty(sparkling ? "Oui" : "Non");
		});

		TableColumn<Wine, String> colColor = new TableColumn<>("Couleur");
		colColor.setCellValueFactory(cellData -> {
			Color c = cellData.getValue().getColor();
			return new SimpleStringProperty(c != null ? c.getColor() : "");
		});

		TableColumn<Wine, Number> colStock = new TableColumn<>("Stock");
		colStock.setCellValueFactory(new PropertyValueFactory<>("stock_quantity"));

		tableView.getColumns().addAll(domainColumn, colCuvee, colVintage, colAppellation, colEffervescent, colColor, colStock);

		FilteredList<Wine> filteredData = new FilteredList<>(wineData, p -> true);
		cbCuvee.valueProperty().addListener((obs, oldVal, newVal) ->
				updateWineFilter(filteredData, cbCuvee, cbVintage, cbAppellation, cbEffervescent, cbColor));
		cbVintage.valueProperty().addListener((obs, oldVal, newVal) ->
				updateWineFilter(filteredData, cbCuvee, cbVintage, cbAppellation, cbEffervescent, cbColor));
		cbAppellation.valueProperty().addListener((obs, oldVal, newVal) ->
				updateWineFilter(filteredData, cbCuvee, cbVintage, cbAppellation, cbEffervescent, cbColor));
		cbEffervescent.valueProperty().addListener((obs, oldVal, newVal) ->
				updateWineFilter(filteredData, cbCuvee, cbVintage, cbAppellation, cbEffervescent, cbColor));
		cbColor.valueProperty().addListener((obs, oldVal, newVal) ->
				updateWineFilter(filteredData, cbCuvee, cbVintage, cbAppellation, cbEffervescent, cbColor));

		SortedList<Wine> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(tableView.comparatorProperty());
		tableView.setItems(sortedData);

		// Modification buttons for wine.
		Button btnModifierStock = new Button("Modifier le stock");
		Button btnModifierVin = new Button("Modifier le vin");
		Button btnAddWine = new Button("Ajouter vin");
		Button btnDeleteWine = new Button("Supprimer vin");

		btnDeleteWine.setOnAction(e -> {
			Wine selectedWine = tableView.getSelectionModel().getSelectedItem();
			if (selectedWine != null) {
				boolean success = wineController.deleteWine(selectedWine.getIdWine());
				if (success) {
					wineData.remove(selectedWine);
					MainView.showAlert("Succès", "Vin supprimé !");
				} else {
					MainView.showAlert("Erreur", "La suppression du vin a échoué.");
				}
			} else {
				MainView.showAlert("Avertissement", "Veuillez sélectionner un vin à supprimer.");
			}
		});

		btnModifierStock.setOnAction(e -> {
			Wine selectedWine = tableView.getSelectionModel().getSelectedItem();
			if (selectedWine != null) {
				TextInputDialog dialog = new TextInputDialog(String.valueOf(selectedWine.getStock_quantity()));
				dialog.setTitle("Modifier le stock");
				dialog.setHeaderText("Modifier la quantité en stock pour le vin: " + selectedWine.getCuvee_name());
				dialog.setContentText("Nouvelle quantité:");
				Optional<String> result = dialog.showAndWait();
				if (result.isPresent()) {
					try {
						int newStock = Integer.parseInt(result.get());
						boolean success = wineController.updateInventory(selectedWine.getIdWine(), newStock);
						if (success) {
							selectedWine.setStock_quantity(newStock);
							tableView.refresh();
						} else {
							MainView.showAlert("Erreur", "La modification du stock a échoué.");
						}
					} catch (NumberFormatException ex) {
						MainView.showAlert("Erreur", "Veuillez entrer un nombre valide.");
					}
				}
			} else {
				MainView.showAlert("Avertissement", "Veuillez sélectionner un vin dans le tableau.");
			}
		});

		btnModifierVin.setOnAction(e -> {
			Wine selectedWine = tableView.getSelectionModel().getSelectedItem();
			if (selectedWine != null) {
				showWineDialog(selectedWine, "Modifier le vin", "Modifier les informations du vin: " + selectedWine.getCuvee_name());
				boolean success = wineController.updateWine(selectedWine);
				if (success) {
					wineData.setAll(wineController.getAllWines());
					tableView.refresh();
					MainView.showAlert("Succès", "Vin modifié avec succès !");
				} else {
					MainView.showAlert("Erreur", "La modification du vin a échoué.");
				}
			} else {
				MainView.showAlert("Avertissement", "Veuillez sélectionner un vin dans le tableau.");
			}
		});

		btnAddWine.setOnAction(e -> {
			Wine newWine = new Wine();
			newWine.setColor(new Color());
			newWine.setCepage(new Cepage());
			newWine.setAppellation(new Appellation());
			newWine.setProvider(new Provider());

			showWineDialog(newWine, "Ajouter vin", "Ajouter un nouveau vin");
			if (newWine.getCuvee_name() != null) {
				boolean success = wineController.createWine(
						newWine.getProvider_price(),
						newWine.getSelling_price(),
						newWine.getStock_quantity(),
						newWine.getVintage(),
						newWine.isSparkling(),
						newWine.getCuvee_name(),
						newWine.getColor().getIdColor(),
						newWine.getCepage().getIdCepage(),
						newWine.getAppellation().getIdAppellation(),
						newWine.getProvider().getIdProvider()
				);
				if (success) {
					// Refresh the wine list and filters
					List<Wine> updatedWines = wineController.getAllWines();
					wineData.setAll(updatedWines);

					// Refresh the filter ComboBoxes
					cbCuvee.setItems(FXCollections.observableArrayList(
							updatedWines.stream()
									.map(Wine::getCuvee_name)
									.distinct()
									.sorted(String::compareToIgnoreCase)
									.collect(Collectors.toList())
					));
					cbCuvee.getItems().add(0, "Tous");
					cbCuvee.getSelectionModel().selectFirst();

					cbVintage.setItems(FXCollections.observableArrayList(
							updatedWines.stream()
									.map(Wine::getVintage)
									.distinct()
									.sorted()
									.collect(Collectors.toList())
					));
					cbVintage.getItems().add(0, 0);
					cbVintage.getSelectionModel().selectFirst();

					cbAppellation.setItems(FXCollections.observableArrayList(
							updatedWines.stream()
									.map(w -> {
										Appellation app = w.getAppellation();
										return app != null ? app.getAppellation() : "";
									})
									.filter(s -> !s.isEmpty())
									.distinct()
									.sorted(String::compareToIgnoreCase)
									.collect(Collectors.toList())
					));
					cbAppellation.getItems().add(0, "Tous");
					cbAppellation.getSelectionModel().selectFirst();

					cbEffervescent.setItems(FXCollections.observableArrayList("Tous", "Oui", "Non"));
					cbEffervescent.getSelectionModel().selectFirst();

					cbColor.setItems(FXCollections.observableArrayList(
							updatedWines.stream()
									.map(w -> {
										Color c = w.getColor();
										return c != null ? c.getColor() : "";
									})
									.filter(s -> !s.isEmpty())
									.distinct()
									.sorted(String::compareToIgnoreCase)
									.collect(Collectors.toList())
					));
					cbColor.getItems().add(0, "Tous");
					cbColor.getSelectionModel().selectFirst();

					// Refresh the provider table
					List<Provider> updatedProviders = providerController.getAllProviders();
					providerData.setAll(updatedProviders);
				} else {
					MainView.showAlert("Erreur", "L'ajout du vin a échoué.");
				}
			}
		});

		HBox buttonBox = new HBox(10);
		buttonBox.getChildren().addAll(btnModifierStock, btnAddWine, btnModifierVin, btnDeleteWine);

		vbox.getChildren().addAll(filterBox, tableView, new Separator(), buttonBox);
		return vbox;
	}

	private void showWineDialog(Wine wine, String title, String headerText) {
		Dialog<Wine> dialog = new Dialog<>();
		dialog.setTitle(title);
		dialog.setHeaderText(headerText);

		ButtonType modifyButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(modifyButtonType, ButtonType.CANCEL);

		TextField cuveeNameField = new TextField(wine.getCuvee_name());
		TextField providerPriceField = new TextField(String.valueOf(wine.getProvider_price()));
		TextField sellingPriceField = new TextField(String.valueOf(wine.getSelling_price()));
		TextField stockQuantityField = new TextField(String.valueOf(wine.getStock_quantity()));
		TextField vintageField = new TextField(String.valueOf(wine.getVintage()));
		CheckBox sparklingCheckBox = new CheckBox();
		sparklingCheckBox.setSelected(wine.isSparkling());

		ComboBox<Color> colorComboBox = new ComboBox<>(FXCollections.observableArrayList(colorController.getAllColors()));
		colorComboBox.setConverter(new StringConverter<Color>() {
			@Override
			public String toString(Color color) {
				return color != null ? color.getColor() : "";
			}

			@Override
			public Color fromString(String string) {
				return null;
			}
		});
		if (wine.getColor() != null) {
			colorComboBox.setValue(wine.getColor());
		}
		Button addColorButton = new Button("+");
		addColorButton.setOnAction(e -> showAddColorDialog(colorComboBox));

		ComboBox<Cepage> cepageComboBox = new ComboBox<>(FXCollections.observableArrayList(cepageController.getAllCepages()));
		cepageComboBox.setConverter(new StringConverter<Cepage>() {
			@Override
			public String toString(Cepage cepage) {
				return cepage != null ? cepage.getCepage() : "";
			}

			@Override
			public Cepage fromString(String string) {
				return null;
			}
		});
		if (wine.getCepage() != null) {
			cepageComboBox.setValue(wine.getCepage());
		}
		Button addCepageButton = new Button("+");
		addCepageButton.setOnAction(e -> showAddCepageDialog(cepageComboBox));

		ComboBox<Appellation> appellationComboBox = new ComboBox<>(FXCollections.observableArrayList(appellationControler.getAllAppellations()));
		appellationComboBox.setConverter(new StringConverter<Appellation>() {
			@Override
			public String toString(Appellation appellation) {
				return appellation != null ? appellation.getAppellation() : "";
			}

			@Override
			public Appellation fromString(String string) {
				return null;
			}
		});
		if (wine.getAppellation() != null) {
			appellationComboBox.setValue(wine.getAppellation());
		}
		Button addAppellationButton = new Button("+");
		addAppellationButton.setOnAction(e -> showAddAppellationDialog(appellationComboBox));

		ComboBox<Provider> providerComboBox = new ComboBox<>(FXCollections.observableArrayList(providerController.getAllProviders()));
		providerComboBox.setConverter(new StringConverter<Provider>() {
			@Override
			public String toString(Provider provider) {
				return provider != null ? provider.getProvider() : "";
			}

			@Override
			public Provider fromString(String string) {
				return null;
			}
		});
		if (wine.getProvider() != null) {
			providerComboBox.setValue(wine.getProvider());
		}
		Button addProviderButton = new Button("+");
		addProviderButton.setOnAction(e -> ProviderTab.showAddProviderDialog(providerComboBox));

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		grid.add(new Label("Nom de la cuvée:"), 0, 0);
		grid.add(cuveeNameField, 1, 0);
		grid.add(new Label("Prix fournisseur:"), 0, 1);
		grid.add(providerPriceField, 1, 1);
		grid.add(new Label("Prix de vente:"), 0, 2);
		grid.add(sellingPriceField, 1, 2);
		grid.add(new Label("Quantité en stock:"), 0, 3);
		grid.add(stockQuantityField, 1, 3);
		grid.add(new Label("Année:"), 0, 4);
		grid.add(vintageField, 1, 4);
		grid.add(new Label("Effervescent:"), 0, 5);
		grid.add(sparklingCheckBox, 1, 5);
		grid.add(new Label("Couleur:"), 0, 6);
		grid.add(colorComboBox, 1, 6);
		grid.add(addColorButton, 2, 6);
		grid.add(new Label("Cépage:"), 0, 7);
		grid.add(cepageComboBox, 1, 7);
		grid.add(addCepageButton, 2, 7);
		grid.add(new Label("Appellation:"), 0, 8);
		grid.add(appellationComboBox, 1, 8);
		grid.add(addAppellationButton, 2, 8);
		grid.add(new Label("Fournisseur:"), 0, 9);
		grid.add(providerComboBox, 1, 9);
		grid.add(addProviderButton, 2, 9);

		dialog.getDialogPane().setContent(grid);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == modifyButtonType) {
				wine.setCuvee_name(cuveeNameField.getText());
				wine.setProvider_price(Float.parseFloat(providerPriceField.getText()));
				wine.setSelling_price(Float.parseFloat(sellingPriceField.getText()));
				wine.setStock_quantity(Integer.parseInt(stockQuantityField.getText()));
				wine.setVintage(Integer.parseInt(vintageField.getText()));
				wine.setSparkling(sparklingCheckBox.isSelected());
				wine.setColor(colorComboBox.getValue());
				wine.setCepage(cepageComboBox.getValue());
				wine.setAppellation(appellationComboBox.getValue());
				wine.setProvider(providerComboBox.getValue());
				return wine;
			}
			return null;
		});
		List<Provider> updatedProviders = providerController.getAllProviders();
		providerData.setAll(updatedProviders);

		List<Orders> updatedOrders = orderController.getAllOrders();
		orderData.setAll(updatedOrders);

		List<Customer> updatedCustomers = customerController.getAllCustomers();
		customerData.setAll(updatedCustomers);

		dialog.showAndWait();
	}

	private void showAddColorDialog(ComboBox<Color> colorComboBox) {
		Dialog<Color> dialog = new Dialog<>();
		dialog.setTitle("Add New Color");
		dialog.setHeaderText("Add a new color");

		ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField colorNameField = new TextField();
		colorNameField.setPromptText("Color Name");

		grid.add(new Label("Color Name:"), 0, 0);
		grid.add(colorNameField, 1, 0);

		dialog.getDialogPane().setContent(grid);
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == addButtonType) {
				return colorController.createColor(colorNameField.getText());
			}
			return null;
		});

		Optional<Color> result = dialog.showAndWait();
		result.ifPresent(color -> {
			colorComboBox.getItems().add(color);
			colorComboBox.setValue(color);
		});
	}


	private void showAddCepageDialog(ComboBox<Cepage> cepageComboBox) {
		Dialog<Cepage> dialog = new Dialog<>();
		dialog.setTitle("Add New Cepage");
		dialog.setHeaderText("Add a new cepage");

		ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField cepageNameField = new TextField();
		cepageNameField.setPromptText("Cepage Name");

		grid.add(new Label("Cepage Name:"), 0, 0);
		grid.add(cepageNameField, 1, 0);
		dialog.getDialogPane().setContent(grid);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == addButtonType) {
				return cepageController.createCepage(cepageNameField.getText());
			}
			return null;
		});

		Optional<Cepage> result = dialog.showAndWait();
		result.ifPresent(cepage -> {
			cepageComboBox.getItems().add(cepage);
			cepageComboBox.setValue(cepage);
		});
	}

	private void showAddAppellationDialog(ComboBox<Appellation> appellationComboBox) {
		Dialog<Appellation> dialog = new Dialog<>();
		dialog.setTitle("Add New Appellation");
		dialog.setHeaderText("Add a new appellation");

		ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField appellationNameField = new TextField();
		appellationNameField.setPromptText("Appellation Name");

		grid.add(new Label("Appellation Name:"), 0, 0);
		grid.add(appellationNameField, 1, 0);

		dialog.getDialogPane().setContent(grid);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == addButtonType) {
				return appellationControler.createAppellation(appellationNameField.getText());
			}
			return null;
		});

		Optional<Appellation> result = dialog.showAndWait();
		result.ifPresent(appellation -> {
			appellationComboBox.getItems().add(appellation);
			appellationComboBox.setValue(appellation);
		});
	}

	private void updateWineFilter(FilteredList<Wine> filteredData,
								  ComboBox<String> cbCuvee,
								  ComboBox<Integer> cbVintage,
								  ComboBox<String> cbAppellation,
								  ComboBox<String> cbEffervescent,
								  ComboBox<String> cbColor) {
		filteredData.setPredicate(wine -> {
			boolean matches = true;

			String selectedCuvee = cbCuvee.getValue();
			if (selectedCuvee != null && !"Tous".equals(selectedCuvee)) {
				matches &= wine.getCuvee_name().equalsIgnoreCase(selectedCuvee);
			}

			Integer selectedVintage = cbVintage.getValue();
			if (selectedVintage != null && selectedVintage != 0) {
				matches &= wine.getVintage() == selectedVintage;
			}
			String selectedAppellation = cbAppellation.getValue();
			if (selectedVintage != null && !"Tous".equals(selectedAppellation)) {
				Appellation app = wine.getAppellation();
				String appStr = (app != null ? app.getAppellation() : "");
				matches &= appStr.equalsIgnoreCase(selectedAppellation);
			}
			String selectedEffervescent = cbEffervescent.getValue();
			if (selectedVintage != null && !"Tous".equals(selectedEffervescent)) {
				boolean isEffervescent = "Oui".equalsIgnoreCase(selectedEffervescent);
				matches &= (wine.isSparkling() == isEffervescent);
			}
			String selectedColor = cbColor.getValue();
			if (selectedVintage != null && !"Tous".equals(selectedColor)) {
				String colorStr = wine.getColor().getColor();
				matches &= colorStr.equalsIgnoreCase(selectedColor);
			}
			return matches;
		});
	}

	public void refresh() {
		wineData.setAll(wineController.getAllWines());
	}
}
