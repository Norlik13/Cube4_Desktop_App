package Model;

public class Provider {
	private int idProvider;
	private long phone_number;
	private String domain_name;

	public Provider() {}

	public Provider(int idProvider, long phone_number, String domain_name) {
		this.idProvider = idProvider;
		this.phone_number = phone_number;
		this.domain_name = domain_name;
	}

	public int getIdProvider() {
		return idProvider;
	}

	public void setIdProvider(int idProvider) {
		this.idProvider = idProvider;
	}

	public long getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(int phone_number) {
		this.phone_number = phone_number;
	}

	public String getDomain_name() {
		return domain_name;
	}

	public void setDomain_name(String domain_name) {
		this.domain_name = domain_name;
	}

	public void setProvider(String provider) {
		this.domain_name = provider;
	}

	public String getProvider() {
		return domain_name;
	}

	@Override
	public String toString() {
		return "Provider{" +
				"idProvider=" + idProvider +
				", phoneNumber=" + phone_number +
				", domainName='" + domain_name + '\'' +
				'}';
	}
}
