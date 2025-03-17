package Model;

public class Cepage {
	private int idCepage;
	private String cepage;

	public Cepage() {}

	public Cepage(int idCepage, String cepage) {
		this.idCepage = idCepage;
		this.cepage = cepage;
	}

	public int getIdCepage() {
		return idCepage;
	}

	public void setIdCepage(int idCepage) {
		this.idCepage = idCepage;
	}

	public String getCepage() {
		return cepage;
	}

	public void setCepage(String cepage) {
		this.cepage = cepage;
	}

	@Override
	public String toString() {
		return "Cepage{" +
				"idCepage=" + idCepage +
				", cepage='" + cepage + '\'' +
				'}';
	}
}

