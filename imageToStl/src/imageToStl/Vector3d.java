package imageToStl;

public class Vector3d {
	public double x, y, z;

	public Vector3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3d add(Vector3d other){
		return new Vector3d(x+other.x, y+other.y, z+other.z);
	}
	
	public Vector3d normal() {
		double length = Math.sqrt(x * x + y * y + z * z);
		return new Vector3d(x / length, y / length, z / length);
	}

	public String toString() {
		return "" + x + " " + y + " " + z;
	}
}
