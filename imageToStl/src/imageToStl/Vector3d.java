package imageToStl;

public class Vector3d {
	public double x, y, z;

	public Vector3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void normalize() {
		double length = Math.sqrt(x * x + y * y + z * z);

		x /= length;
		y /= length;
		z /= length;
	}

	public String toString() {
		return "" + x + " " + y + " " + z;
	}
}
