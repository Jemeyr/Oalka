package graphics;

import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import skeleton.Bone;
import skeleton.Skeleton;

public class Model{

	protected Mesh mesh;
	
	private Model parent;
	private List<Model> children;
	
	public float[] col;
	private int colorUniform;
	
	private Matrix4f model;
	private int modelUniform;
	
	private Vector3f position;
	private Quaternion rotation;
	
	private float rotationAmount = 0.0f;
	
	private Skeleton skeleton;

	private int inverseBindUniform;
	private int skeletonUniform;
	
	protected Model(Mesh mesh, Shader shader)
	{	
		this.colorUniform = shader.getUniforms().get("color");
		this.modelUniform = shader.getUniforms().get("model");

		this.children = new ArrayList<Model>();
		
		this.mesh = mesh;
		
		this.col = new float[3];
		
		Random r = new Random();
		col[0] = r.nextFloat() * 0.33f + 0.66f;
		col[1] = r.nextFloat() * 0.33f + 0.66f;
		col[2] = r.nextFloat() * 0.33f + 0.66f;
		
		this.position = new Vector3f();
		this.rotation = new Quaternion(1.0f, 0.0f, 0.0f, rotationAmount);
		
		this.model = new Matrix4f();
		calculateModelMatrix();

		this.skeleton = new Skeleton(mesh.skeleton);
		
		this.inverseBindUniform = shader.getUniforms().get("jointInvBinds");
		this.skeletonUniform = shader.getUniforms().get("joints");
		
		FloatBuffer invBuff = GLOperations.generateInverseBindFloatBuffer(skeleton);
		glUniformMatrix4(inverseBindUniform, true, invBuff);
		
		FloatBuffer skelebuf = GLOperations.generatePoseFloatBuffer(skeleton);
		glUniformMatrix4(skeletonUniform, true, skelebuf);
		
	}
	
	
	public void draw(long time) {
		
		Map<String, Matrix4f> p = skeleton.getAnim().getPose(time);
		
		//recursively fill out the tree
		pose(skeleton.root, p, null);
		
		
		FloatBuffer skelebuf = GLOperations.generatePoseFloatBuffer(skeleton);
		glUniformMatrix4(skeletonUniform, true, skelebuf);
	
		
		glUniformMatrix4(modelUniform, false, GLOperations.generateFloatBuffer(model));		
		
		glUniform3f(colorUniform, col[0], col[1], col[2]);

		mesh.draw();
	}

	private void pose(Bone bone, Map<String, Matrix4f> pose, Matrix4f parent){
		
		for(Bone b : bone.children){
			b.transform.load(pose.get(b.name));
			if(parent != null){
				Matrix4f.mul(parent, b.transform, b.transform);
			}
			pose(b, pose, b.transform);
		}
	}

	
	
	public void addChild(Model model) {
		// This should add it to a bone in particular! YEAH
		this.children.add(model);
		
	}

	public void removeChild(Model model) {
		this.children.remove(model);
	}
	
	
	private void calculateModelMatrix(){
		this.model.setIdentity();
		//rotate
		
		Quaternion rotNorm = new Quaternion();
		this.rotation.normalise(rotNorm);
		
		Matrix4f rotationMat = new Matrix4f();
		rotationMat.m00 = 1.0f - 2.0f*(rotNorm.y*rotNorm.y + rotNorm.z*rotNorm.z);
		rotationMat.m01 = 2.0f*(rotNorm.x*rotNorm.y - rotNorm.z*rotNorm.w);
		rotationMat.m02 = 2.0f*(rotNorm.x*rotNorm.z + rotNorm.y*rotNorm.w);
		rotationMat.m03 = 0.0f;
		
		rotationMat.m10 = 2.0f*(rotNorm.x*rotNorm.y + rotNorm.z*rotNorm.w);
		rotationMat.m11 = 1.0f - 2.0f*(rotNorm.x*rotNorm.x + rotNorm.z*rotNorm.z);
		rotationMat.m12 = 2.0f*(rotNorm.y*rotNorm.z - rotNorm.x*rotNorm.w);
		rotationMat.m13 = 0.0f;
		
		rotationMat.m20 = 2.0f*(rotNorm.x*rotNorm.z - rotNorm.y*rotNorm.w);
		rotationMat.m21 = 2.0f*(rotNorm.y*rotNorm.z + rotNorm.x*rotNorm.w);
		rotationMat.m22 = 1.0f - 2.0f*(rotNorm.x*rotNorm.x + rotNorm.y*rotNorm.y);
		rotationMat.m23 = 0.0f;
		
		rotationMat.m30 = 0.0f;
		rotationMat.m31 = 0.0f;
		rotationMat.m32 = 0.0f;
		rotationMat.m33 = 1.0f;
	
		
		//translate
		this.model.translate(this.position);
	
		//rotate
		Matrix4f.mul(this.model, rotationMat, this.model);
		
		//apply on top of parent if non-null
		if(this.parent != null)
		{
			Matrix4f.mul(this.parent.model, this.model, this.model);
		}
	}
	
	public void setPosition(Vector3f newPosition){
		this.position = newPosition;
		calculateModelMatrix();
	}
	
	public void addPosition(Vector3f delta){
		this.position = Vector3f.add(this.position, delta, null);
		calculateModelMatrix();
	}
	
	public void setRotation(Quaternion rotation) {
		this.rotation = rotation;
		calculateModelMatrix();
	}

	public void addRotation(Quaternion delta) {
		Quaternion.mul(this.rotation, delta, this.rotation);	
		calculateModelMatrix();
	}
	
	

}
