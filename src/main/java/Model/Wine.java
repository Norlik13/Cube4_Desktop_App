package Model;

import com.google.gson.annotations.SerializedName;

public class Wine {
	@SerializedName("idWine")
	private int idWine;

	@SerializedName("cuvee_name")
	private String cuvee_name;

	@SerializedName("provider_price")
	private float provider_price;

	@SerializedName("selling_price")
	private float selling_price;

	@SerializedName("stock_quantity")
	private int stock_quantity;

	@SerializedName("Vintage")
	private int vintage;

	@SerializedName("Sparkling")
	private boolean sparkling;

	@SerializedName("Color")
	private Color color;

	@SerializedName("Cepage")
	private Cepage cepage;

	@SerializedName("Appellation")
	private Appellation appellation;

	@SerializedName("Provider")
	private Provider provider;

	public Wine() {}

	public int getIdWine() {
		return idWine;
	}

	public void setIdWine(int idWine) {
		this.idWine = idWine;
	}

	public String getCuvee_name() {
		return cuvee_name;
	}

	public void setCuvee_name(String cuvee_name) {
		this.cuvee_name = cuvee_name;
	}

	public float getProvider_price() {
		return provider_price;
	}

	public void setProvider_price(float provider_price) {
		this.provider_price = provider_price;
	}

	public float getSelling_price() {
		return selling_price;
	}

	public void setSelling_price(float selling_price) {
		this.selling_price = selling_price;
	}

	public int getStock_quantity() {
		return stock_quantity;
	}

	public void setStock_quantity(int stock_quantity) {
		this.stock_quantity = stock_quantity;
	}

	public int getVintage() {
		return vintage;
	}

	public void setVintage(int vintage) {
		this.vintage = vintage;
	}

	public boolean isSparkling() {
		return sparkling;
	}

	public void setSparkling(boolean sparkling) {
		this.sparkling = sparkling;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Cepage getCepage() {
		return cepage;
	}

	public void setCepage(Cepage cepage) {
		this.cepage = cepage;
	}

	public Appellation getAppellation() {
		return appellation;
	}

	public void setAppellation(Appellation appellation) {
		this.appellation = appellation;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	@Override
	public String toString() {
		return "Wine{" +
				"idWine=" + idWine +
				", cuvee_name='" + cuvee_name + '\'' +
				", provider_price=" + provider_price +
				", selling_price=" + selling_price +
				", stock_quantity=" + stock_quantity +
				", vintage=" + vintage +
				", sparkling=" + sparkling +
				", color=" + color +
				", cepage=" + cepage +
				", appellation=" + appellation +
				", provider=" + provider +
				'}';
	}
}