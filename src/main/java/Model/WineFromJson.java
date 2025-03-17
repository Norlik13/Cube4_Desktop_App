package Model;

import com.google.gson.*;

import java.lang.reflect.Type;

public class WineFromJson implements JsonDeserializer<Wine> {
	@Override
	public Wine deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();

		Wine wine = new Wine();
		wine.setIdWine(jsonObject.get("idWine").getAsInt());
		wine.setCuvee_name(jsonObject.get("cuvee_name").getAsString());
		wine.setProvider_price(jsonObject.get("provider_price").getAsFloat());
		wine.setSelling_price(jsonObject.get("selling_price").getAsFloat());
		wine.setStock_quantity(jsonObject.get("stock_quantity").getAsInt());
		wine.setVintage(jsonObject.get("Vintage").getAsInt());
		wine.setSparkling(jsonObject.get("Sparkling").getAsBoolean());

		JsonObject colorObject = jsonObject.getAsJsonObject("Color");
		int color_id = colorObject.get("Color_id").getAsInt();
		String color_name = colorObject.get("Color_color").getAsString();
		Color color = new Color(color_id, color_name);
		wine.setColor(color);

		JsonObject cepageObject = jsonObject.getAsJsonObject("Cepage");
		int cepage_id = cepageObject.get("Cepage_id").getAsInt();
		String cepage_name = cepageObject.get("Cepage_cepage").getAsString();
		Cepage cepage = new Cepage(cepage_id, cepage_name);
		wine.setCepage(cepage);

		JsonObject providerObject = jsonObject.getAsJsonObject("Provider");
		int provider_id = providerObject.get("Provider_id").getAsInt();
		String provider_name = providerObject.get("Provider_domain_name").getAsString();
		Long provider_phone = providerObject.get("Provider_phone_number").getAsLong();
		Provider provider = new Provider(provider_id, provider_phone,  provider_name);
		wine.setProvider(provider);

		JsonObject appellationObject = jsonObject.getAsJsonObject("Appellation");
		int appellation_id = appellationObject.get("Appellation_id").getAsInt();
		String appellation_name = appellationObject.get("Appellation_name").getAsString();
		Appellation appellation = new Appellation(appellation_id, appellation_name);
		wine.setAppellation(appellation);

		return wine;
	}
}