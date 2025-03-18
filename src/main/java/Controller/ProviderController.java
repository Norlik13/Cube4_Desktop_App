package Controller;

import Model.Provider;
import Util.ApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProviderController {
	private static final ApiClient apiClient = ApiClient.getInstance();
	private static final Gson gson = new Gson();

	// Get all providers
	public List<Provider> getAllProviders() {
		try {
			String response = apiClient.get("/providers/");
			Type providerListType = new TypeToken<List<Provider>>(){}.getType();
			return gson.fromJson(response, providerListType);
		} catch(IOException | InterruptedException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	// Create a new provider
	public Provider createProvider(String name, long phoneNumber) {
		Map<String, Object> data = new HashMap<>();
		data.put("domain_name", name);
		data.put("phone_number", phoneNumber);
		String json = gson.toJson(data);
		try {
			String response = apiClient.post("/providers/", json);
			Map<String, Object> responseData = gson.fromJson(response, Map.class);
			int id = ((Double) responseData.get("id")).intValue();
			return new Provider(id, phoneNumber, name);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	// Update a provider
	public boolean updateProvider(Provider provider) {
		String json = gson.toJson(provider);
		try {
			apiClient.put("/providers/" + provider.getIdProvider(), json);
			return true;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteProvider(int idProvider) {
		try {
			apiClient.delete("/providers/" + idProvider);
			return true;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	public Provider getProviderById(Integer providerId) {
        try {
            String response = apiClient.get("/providers/" + providerId);
            return gson.fromJson(response, Provider.class);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
