package graphics;

import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import skeleton.Animation;
import skeleton.Bone;
import skeleton.Skeleton;

public class Model{

	private RenderMaster renderMaster;
	
	protected Mesh mesh;
	
	private Model parent;
	private Map<Model, Bone> children;
	
	private Map<Animation, Float> animWeights;
	
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
	
	protected Model(Mesh mesh, Shader shader, RenderMaster renderMaster)
	{	
		this.renderMaster = renderMaster;
		
		
		this.colorUniform = shader.getUniforms().get("color");
		this.modelUniform = shader.getUniforms().get("model");

		this.children = new HashMap<Model,Bone>();
		
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
		

		this.animWeights = new HashMap<Animation, Float>();
		//fill out animWeights
		
		for(Animation a : skeleton.getAnims())
		{
			this.animWeights.put(a, 1.0f / skeleton.getAnims().size());			
		}
		
		
		
	}
	
	private void setWeights(float[] weights){
		int i = 0;
		Map<Animation, Float> aWeights = new HashMap<Animation, Float>();
		float left = 1.0f;
		Animation last = null;
		
		for(Entry<Animation, Float> e : animWeights.entrySet()){
			last = e.getKey();
			left -= weights[i];
			aWeights.put(e.getKey(), weights[i++]);
		}
		
		//add any remaining weight to last one.
		aWeights.put(last, aWeights.get(last) + left);
		
		this.animWeights = aWeights;
	}
	
	private float[] aw = {1.0f, 0.0f, 0.0f, 0.0f};
		
	public void draw(long time) {
	
		if(Keyboard.isKeyDown(Keyboard.KEY_T)){
			aw[0] = 1.0f;
			aw[1] = 0.0f;
			aw[2] = 0.0f;
			aw[3] = 0.0f;
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_Y)){
			aw[0] = 0.0f;
			aw[1] = 1.0f;
			aw[2] = 0.0f;
			aw[3] = 0.0f;
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_U)){
			aw[0] = 0.0f;
			aw[1] = 0.0f;
			aw[2] = 1.0f;
			aw[3] = 0.0f;
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_I)){
			aw[0] = 0.0f;
			aw[1] = 0.0f;
			aw[2] = 0.0f;
			aw[3] = 1.0f;
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_G) && aw[0] < 1.0f){
			float s = 1.0f - aw[0];
			aw[0] += 0.01f;
			aw[1] -= 0.01f * aw[1]/s;
			aw[2] -= 0.01f * aw[2]/s;
			aw[3] -= 0.01f * aw[3]/s;
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_H) && aw[1] < 1.0f){
			float s = 1.0f - aw[1];
			aw[0] -= 0.01f * aw[0]/s;
			aw[1] += 0.01f;
			aw[2] -= 0.01f * aw[2]/s;
			aw[3] -= 0.01f * aw[3]/s;
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_J) && aw[2] < 1.0f){
			float s = 1.0f - aw[2];
			aw[0] -= 0.01f * aw[0]/s;
			aw[1] -= 0.01f * aw[1]/s;		
			aw[2] += 0.01f;
			aw[3] -= 0.01f * aw[3]/s;
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_K) && aw[3] < 1.0f){
			float s = 1.0f - aw[3];
			aw[0] -= 0.01f * aw[0]/s;
			aw[1] -= 0.01f * aw[1]/s;
			aw[2] -= 0.01f * aw[2]/s;			
			aw[3] += 0.01f;
		}
		
		aw[0] = aw[0] < 0.001f ? 0.0f : aw[0] > 1.0f ? 1.0f : aw[0];
		aw[1] = aw[1] < 0.001f ? 0.0f : aw[1] > 1.0f ? 1.0f : aw[1];
		aw[2] = aw[2] < 0.001f ? 0.0f : aw[2] > 1.0f ? 1.0f : aw[2];
		aw[3] = aw[3] < 0.001f ? 0.0f : aw[3] > 1.0f ? 1.0f : aw[3];
		
		System.out.println((int)(100*aw[0]) + " " + (int)(100*aw[1]) + " " + (int)(100*aw[2]) + " " + (int)(100*aw[3]));
		setWeights(aw);
		
		
		
		
		Map<String, Matrix4f> r = blend(animWeights, time);
		
		//recursively fill out the tree
		pose(skeleton.root, r, null);
		
		
		FloatBuffer skelebuf = GLOperations.generatePoseFloatBuffer(skeleton);
		glUniformMatrix4(skeletonUniform, true, skelebuf);
		
		
		glUniformMatrix4(modelUniform, false, GLOperations.generateFloatBuffer(this.model));		
		
		glUniform3f(colorUniform, col[0], col[1], col[2]);

		mesh.draw();
		
		//draw all children after to ensure update has occurred on parent first	
		for(Entry<Model, Bone> child : this.children.entrySet()){
			Matrix4f prt = new Matrix4f();
			Matrix4f.mul(this.model, skeleton.bones.get(child.getValue().name).transform, prt);
			
			child.getKey().draw(time, prt);
		}
		
	}
	

	public void draw(long time, Matrix4f parent) {
		

		Map<String, Matrix4f> r = blend(animWeights, time);
		
		//recursively fill out the tree
		pose(skeleton.root, r, null);
		
		//recursively fill out the tree
		pose(skeleton.root, r, null);
		
		
		FloatBuffer skelebuf = GLOperations.generatePoseFloatBuffer(skeleton);
		glUniformMatrix4(skeletonUniform, true, skelebuf);
	
		Matrix4f modelTransform = new Matrix4f();
		Matrix4f.mul(parent, this.model, modelTransform);
		
		glUniformMatrix4(modelUniform, false, GLOperations.generateFloatBuffer(modelTransform));		
		
		glUniform3f(colorUniform, col[0], col[1], col[2]);

		mesh.draw();
	}

	private Map<String, Matrix4f> blend(Map<Animation, Float> animWeights, long time){
		Map<String, Matrix4f> c = new HashMap<String, Matrix4f>();

		
		for(Entry<Animation,Float> anim : animWeights.entrySet()){
			for(Entry <String, Matrix4f> e : anim.getKey().getPose(time).entrySet()) {
				
				if(!c.containsKey(e.getKey())){
					Matrix4f m = new Matrix4f(); m.setZero();
					Animation.matrixAccumulate(m, e.getValue(), anim.getValue());
					c.put(e.getKey(), m);
				}
				else{
					Animation.matrixAccumulate(c.get(e.getKey()), e.getValue(), anim.getValue());
				}
			}
		}
		
		for(Entry<String, Matrix4f> e : c.entrySet()){
			Animation.matrixFinalize(e.getValue());
		}
		
		return c;
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

	
	
	public void addChild(Model model, String boneName) {
		this.children.put(model, this.skeleton.bones.get(boneName));
		
		//removes from toplevel models
		renderMaster.models.remove(model);
		
	}

	public void removeChild(Model model) {
		renderMaster.models.add(model);
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
	
	public Vector3f getPosition(){
		return new Vector3f(this.position);
	}

}
