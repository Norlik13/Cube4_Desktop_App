package View;

import Controller.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class MainView extends Application {

	private OrderTab orderTab;
	private WineTab wineTab;
	private CustomerTab customerTab;
	private ProviderTab providerTab;

	@Override
	public void start(Stage primaryStage) {
		OrderController orderController = new OrderController();
		CustomerController customerController = new CustomerController();
		ProviderController providerController = new ProviderController();
		WineController wineController = new WineController();
		ColorController colorController = new ColorController();
		CepageController cepageController = new CepageController();
		AppellationController appellationController = new AppellationController();


		OrderTab orderTab = new OrderTab(orderController, customerController, providerController, wineController);
		WineTab wineTab = new WineTab(orderController, customerController, providerController, wineController, colorController, cepageController, appellationController);
		CustomerTab customerTab = new CustomerTab(customerController);
		ProviderTab providerTab = new ProviderTab(providerController);

		TabPane tabPane = new TabPane();

		Tab wineTabPane = new Tab("Liste Vins", wineTab.createWineTablePaneWithForm());
		wineTabPane.setClosable(false);

		Tab customerTabPane = new Tab("Liste Clients", customerTab.createCustomerTablePaneWithButton());
		customerTabPane.setClosable(false);

		Tab providerTabPane = new Tab("Liste Fournisseurs", providerTab.createProviderTablePaneWithButton());
		providerTabPane.setClosable(false);

		Tab orderTabPane = new Tab("Liste Commandes", orderTab.createOrderTablePaneWithDialog());
		orderTabPane.setClosable(false);

		tabPane.getTabs().addAll(wineTabPane, customerTabPane, providerTabPane, orderTabPane);

		Scene scene = new Scene(tabPane, 900, 600);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Application d'entrepôt");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	public static void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
