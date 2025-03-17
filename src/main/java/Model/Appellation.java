package Model;


public class Appellation {
	private int idAppellation;
	private String appellation;

	public Appellation() {}

	public Appellation(int idAppellation, String appellation) {
		this.idAppellation = idAppellation;
		this.appellation = appellation;
	}

	public int getIdAppellation() {
		return idAppellation;
	}

	public void setIdAppellation(int idAppellation) {
		this.idAppellation = idAppellation;
	}

	public String getAppellation() {
		return appellation;
	}

	public void setAppellation(String appellation) {
		this.appellation = appellation;
	}

	@Override
	public String toString() {
		return "Appellation{" +
				"idAppellation=" + idAppellation +
				", appellation='" + appellation + '\'' +
				'}';
	}
}

