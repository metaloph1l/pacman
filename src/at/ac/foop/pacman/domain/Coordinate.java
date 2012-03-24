package at.ac.foop.pacman.domain;

/**
 *
 * @author Phil
 */
public class Coordinate {

	private Integer x;
	private Integer y;

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Coordinate other = (Coordinate) obj;
		if (this.x != other.x && (this.x == null || !this.x.equals(other.x))) {
			return false;
		}
		if (this.y != other.y && (this.y == null || !this.y.equals(other.y))) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 31 * hash + (this.x != null ? this.x.hashCode() : 0);
		hash = 31 * hash + (this.y != null ? this.y.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		return "Coordinates{" + "x=" + x + ", y=" + y + '}';
	}
}
