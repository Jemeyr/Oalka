package skeleton;

import org.lwjgl.util.vector.Matrix4f;

//BEST CLASS EVER
public class Pose {
	private float frame;
	private Matrix4f transform;
	
	public Pose(float frame, Matrix4f transform){
		this.frame = frame;
		this.transform = transform;
	}
	
	public float getFrame(){
		return this.frame;
	}
	
	public Matrix4f getTransform(){
		return this.transform;
	}
	
	
}

