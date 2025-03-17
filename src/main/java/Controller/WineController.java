package Controller;

import Model.Wine;
import Model.WineFromJson;
import Util.ApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WineController {
	private ApiClient apiClient = ApiClient.getInstance();
	private Gson gson = new Gson();
	private static List<Wine> wineList;

	public WineController() {
		this.apiClient = ApiClient.getInstance();
		this.gson = new GsonBuilder()
				.registerTypeAdapter(Wine.class, new WineFromJson())
				.create();
		wineList = getAllWines();
	}

	// Get all wines
	public List<Wine> getAllWines() {
		try {
			String response = apiClient.get("/wines/");
			Type wineListType = new TypeToken<List<Wine>>(){}.getType();
			return gson.fromJson(response, wineListType);
		} catch(IOException | InterruptedException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	// Create a new wine
	public boolean createWine(float providerPrice, float sellingPrice, int stockQuantity, int vintage,
							  boolean sparkling, String cuveeName, int colorId, int cepageId,
							  int appellationId, int providerId) {
		Map<String, Object> data = new HashMap<>();
		data.put("provider_price", providerPrice);
		data.put("selling_price", sellingPrice);
		data.put("stock_quantity", stockQuantity);
		data.put("Vintage", vintage);
		data.put("Sparkling", sparkling);
		data.put("cuvee_name", cuveeName);
		data.put("Color_idColor", colorId);
		data.put("Cepage_idCepage", cepageId);
		data.put("Appellation_idAppellation", appellationId);
		data.put("Provider_idProvider", providerId);
		String json = gson.toJson(data);
		try {
			apiClient.post("/wines/", json);
			return true;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Update a wine
	public boolean updateWine(Wine wine) {
		String json = gson.toJson(wine);
		try {
			apiClient.put("/wines/" + wine.getIdWine(), json);
			return true;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Delete a wine
	public boolean deleteWine(int wineId) {
		try {
			apiClient.delete("/wines/" + wineId);
			return true;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Get wine by ID
	public Wine getWineById(int wineId) {
		try {
			String response = apiClient.get("/wines/" + wineId);
			return gson.fromJson(response, Wine.class);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean updateInventory(int articleId, int newStock) {
        Map<String, Object> data = new HashMap<>();
        data.put("stock_quantity", newStock);
        String json = gson.toJson(data);
        try {
            apiClient.put("/wines/inventory/" + articleId, json);
            return true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
