package graphics;

import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import skeleton.Bone;
import skeleton.Pose;
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

	
	private long lastTime;
	
	private int inverseBindUniform;
	private int skeletonUniform;

	private int lastIndex = 0;
	private int nextIndex = 1;
	private float interpolationStep = 0.0f;
	
	protected Model(Mesh mesh, Shader shader, Skeleton skeleton)
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
		
		this.inverseBindUniform = shader.getUniforms().get("jointInvBinds");
		this.skeletonUniform = shader.getUniforms().get("joints");
		
		FloatBuffer invBuff = GLOperations.generateInverseBindFloatBuffer(skeleton);
		glUniformMatrix4(inverseBindUniform, true, invBuff);
		
		FloatBuffer skelebuf = GLOperations.generatePoseFloatBuffer(skeleton);
		glUniformMatrix4(skeletonUniform, true, skelebuf);
		
		
		this.skeleton = skeleton;
		lastTime = System.currentTimeMillis();
	}
	
	
	public void draw(long time) {
		
		interpolationStep += (float)(time - lastTime) / 1000.0f;
		
		if(interpolationStep > 1.0f){
			interpolationStep = 0.0f;
			lastIndex = nextIndex;
			nextIndex = (nextIndex + 1) % skeleton.animations.get(0).getPoses("Body").size();
		}
		
		
		//pose here
		pose(skeleton.root, lastIndex, nextIndex, interpolationStep, null);
		
		
		FloatBuffer skelebuf = GLOperations.generatePoseFloatBuffer(skeleton);
		glUniformMatrix4(skeletonUniform, true, skelebuf);
	
		
		glUniformMatrix4(modelUniform, false, GLOperations.generateFloatBuffer(model));		
		
		glUniform3f(colorUniform, col[0], col[1], col[2]);

		mesh.draw();
		
		lastTime = time;
	}

	

	public void pose(Bone bone, int alpha, int beta, float amount, Matrix4f parent){
		
		//Only body seems to do anything right now
		for(Bone b : bone.children){
			
			
			Matrix4f am = this.skeleton.animations.get(0).getPoses(b.name).get(alpha).getTransform();
			Matrix4f bm = this.skeleton.animations.get(0).getPoses(b.name).get(beta).getTransform();
			
			
			
			Matrix4f m = new Matrix4f();
			//LERPING MATRICES COMPONENT-WISE IS GREAT! AW YEAH
			m.m00 = am.m00 * (1.0f - amount) + bm.m00 * amount;	m.m01 = am.m01 * (1.0f - amount) + bm.m01 * amount;	m.m02 = am.m02 * (1.0f - amount) + bm.m02 * amount;	m.m03 = am.m03 * (1.0f - amount) + bm.m03 * amount;
			m.m10 = am.m10 * (1.0f - amount) + bm.m10 * amount;	m.m11 = am.m11 * (1.0f - amount) + bm.m11 * amount;	m.m12 = am.m12 * (1.0f - amount) + bm.m12 * amount;	m.m13 = am.m13 * (1.0f - amount) + bm.m13 * amount;
			m.m20 = am.m20 * (1.0f - amount) + bm.m20 * amount;	m.m21 = am.m21 * (1.0f - amount) + bm.m21 * amount;	m.m22 = am.m22 * (1.0f - amount)+ bm.m22 * amount;	m.m23 = am.m23 * (1.0f - amount) + bm.m23 * amount;
			m.m30 = am.m30 * (1.0f - amount) + bm.m30 * amount;	m.m31 = am.m31 * (1.0f - amount) + bm.m31 * amount;	m.m32 = am.m32 * (1.0f - amount) + bm.m32 * amount;	m.m33 = am.m33 * (1.0f - amount) + bm.m33 * amount;

			//set transform of our bone
			b.transform.load(m);
			
			if(parent != null){
				Matrix4f.mul(parent, b.transform, b.transform);
			}
			pose(b, alpha, beta, amount, b.transform);
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
