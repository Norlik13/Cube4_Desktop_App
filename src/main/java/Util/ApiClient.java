package Util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class ApiClient {
	private static final String BASE_URL = "http://localhost:5000";
	private static ApiClient instance;
	private final HttpClient httpClient;

	private ApiClient() {
		httpClient = HttpClient.newHttpClient();
	}

	public static ApiClient getInstance() {
		if(instance == null) {
			instance = new ApiClient();
		}
		return instance;
	}

	public String get(String endpoint) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(BASE_URL + endpoint))
				.header("Content-Type", "application/json")
				.GET()
				.build();
		HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
		return response.body();
	}

	public String post(String endpoint, String jsonBody) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(BASE_URL + endpoint))
				.header("Content-Type", "application/json")
				.POST(BodyPublishers.ofString(jsonBody))
				.build();
		HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
		return response.body();
	}

	public String put(String endpoint, String jsonBody) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(BASE_URL + endpoint))
				.header("Content-Type", "application/json")
				.PUT(BodyPublishers.ofString(jsonBody))
				.build();
		HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
		return response.body();
	}

	public void delete(String endpoint) throws IOException, InterruptedException {
		URL url = new URL(BASE_URL + endpoint);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("DELETE");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setDoOutput(true);

		int responseCode = connection.getResponseCode();
		if (responseCode != 200) {
			throw new IOException("Failed to delete resource: " + responseCode);
		}
	}
}

