package Controller;

import Model.Color;
import Util.ApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColorController {
	private ApiClient apiClient = ApiClient.getInstance();
	private Gson gson = new Gson();

	public Color createColor(String name) {
		Map<String, Object> data = new HashMap<>();
		data.put("color", name);
		String json = gson.toJson(data);
		try {
			String response = apiClient.post("/colors/", json);
			Map<String, Object> responseData = gson.fromJson(response, Map.class);
			int id = ((Double) responseData.get("id")).intValue();
			return new Color(id, name);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Color> getAllColors() {
		try {
			String response = apiClient.get("/colors/");
			Type colorListType = new TypeToken<List<Color>>(){}.getType();
			List<Color> colors = gson.fromJson(response, colorListType);
			return colors;
		} catch(IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
