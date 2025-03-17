package Controller;

import Model.Cepage;
import Util.ApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CepageController {
	private ApiClient apiClient = ApiClient.getInstance();
	private Gson gson = new Gson();

	public Cepage createCepage(String name) {
		Map<String, Object> data = new HashMap<>();
		data.put("cepage", name);
		String json = gson.toJson(data);
		try {
			String response = apiClient.post("/cepages/", json);
			Map<String, Object> responseData = gson.fromJson(response, Map.class);
			int id = ((Double) responseData.get("id")).intValue();
			return new Cepage(id, name);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Cepage> getAllCepages() {
		try {
			String response = apiClient.get("/cepages/");
			Type cepageListType = new TypeToken<List<Cepage>>(){}.getType();
			List<Cepage> cepages = gson.fromJson(response, cepageListType);
			return cepages;
		} catch(IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
