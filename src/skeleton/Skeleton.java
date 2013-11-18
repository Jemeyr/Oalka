package skeleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Matrix4f;

public class Skeleton {
	public static final String ROOT = "root";
	
	public List<Animation> animations;
	public Map<String, Bone> bones;
	public Bone root;

	private Map<Bone, Integer> boneIndices;
	
	public Skeleton(Skeleton prev){
		this.bones = new HashMap<String,Bone>();
		this.animations = new ArrayList<Animation>();
		
		//copy initial bones and animations
		for(Entry<String,Bone> e : prev.bones.entrySet()){
			this.bones.put(e.getKey(), e.getValue());
		}
		
		for(Animation a : prev.animations){
			this.animations.add(a);
		}
		
		
		Matrix4f identity=  new Matrix4f();
		identity.setIdentity();
		
		this.root = new Bone(ROOT, identity);
		this.bones.put(root.name, root);
	}
	
	public Skeleton(){
		this.bones = new HashMap<String,Bone>();
		this.animations = new ArrayList<Animation>();
		
		Matrix4f identity=  new Matrix4f();
		identity.setIdentity();
		
		this.root = new Bone(ROOT, identity);
		this.bones.put(root.name, root);
	}
	
	public void addAnimation(Animation animation){
		this.animations.add(animation);
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
