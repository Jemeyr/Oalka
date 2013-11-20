package skeleton;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Matrix4f;

public class Skeleton {
	public static final String ROOT = "root";

	private Animation anim;//TODO: come on, multiple animations
	public Map<String, Bone> bones;
	public Bone root;

	private Map<Bone, Integer> boneIndices;
	
	//TODO: go deeper, make copies of bones?
	public Skeleton(Skeleton prev){
		this.bones = new HashMap<String,Bone>(prev.bones);
		this.boneIndices = new HashMap<Bone,Integer>(prev.boneIndices);
		this.root = prev.root;
		
		this.anim = prev.anim;
		
	}
	
	public Skeleton(){
		this.bones = new HashMap<String,Bone>();
		
		Matrix4f identity=  new Matrix4f();
		identity.setIdentity();
		
		this.root = new Bone(ROOT, identity);
		this.bones.put(root.name, root);
	}
	
	public void setAnim(Animation anim){
		this.anim = anim;
	}
	
	public Animation getAnim(){
		return this.anim;
	}
	
	public void addRoot(String childName, Matrix4f offset){
		Bone bone = this.root.addChild(childName, offset);
		
		this.bones.put(childName, bone);
	}
	
	public Bone addChild(String parentName, String childName, Matrix4f offset){
		Bone bone = this.bones.get(parentName).addChild(childName, offset);
		this.bones.put(childName, bone);
		return bone;
	}

	public void addBoneIndexMap(Map<String, Integer> inputIndexMap) {
		this.boneIndices = new HashMap<Bone, Integer>();
		
		for(Entry<String, Integer> s : inputIndexMap.entrySet()){
			this.boneIndices.put(bones.get(s.getKey()), s.getValue());
		}
	}
	
	//Unsafe, returns actual map which can be edited, so don't do that.
	public Map<Bone, Integer> getBoneIndices(){
		return this.boneIndices;
	}
	
	
	

}
