package Model;

public class Color {
	private int idColor;
	private String color;

	public Color() {
	}

	public Color(int idColor, String color) {
		this.idColor = idColor;
		this.color = color;
	}

	public int getIdColor() {
		return idColor;
	}

	public void setIdColor(int idColor) {
		this.idColor = idColor;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return "Color{" +
				"idColor=" + idColor +
				", color='" + color + '\'' +
				'}';
	}
}
