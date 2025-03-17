package Controller;

import Model.Appellation;
import Util.ApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppellationController {
	private ApiClient apiClient = ApiClient.getInstance();
	private Gson gson = new Gson();

	public Appellation createAppellation(String name) {
		Map<String, Object> data = new HashMap<>();
		data.put("appellation", name);
		String json = gson.toJson(data);
		try {
			String response = apiClient.post("/appellations/", json);
			Map<String, Object> responseData = gson.fromJson(response, Map.class);
			int id = ((Double) responseData.get("id")).intValue();
			return new Appellation(id, name);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Appellation> getAllAppellations() {
		try {
			String response = apiClient.get("/appellations/");
			Type appellationListType = new TypeToken<List<Appellation>>(){}.getType();
			List<Appellation> appellations = gson.fromJson(response, appellationListType);
			return appellations;
		} catch(IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
