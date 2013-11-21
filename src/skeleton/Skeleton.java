package skeleton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Matrix4f;

public class Skeleton {
	public static final String ROOT = "root";

	private List<Animation> anims;
	public Map<String, Bone> bones;
	public Bone root;

	private Map<Bone, Integer> boneIndices;
	
	//TODO: go deeper, make copies of bones?
	public Skeleton(Skeleton prev){
		this.bones = new HashMap<String,Bone>(prev.bones);
		this.boneIndices = new HashMap<Bone,Integer>(prev.boneIndices);
		this.root = prev.root;
		
		this.anims = prev.anims;
		
	}
	
	public Skeleton(){
		this.bones = new HashMap<String,Bone>();
		
		Matrix4f identity=  new Matrix4f();
		identity.setIdentity();
		
		this.root = new Bone(ROOT, identity);
		this.bones.put(root.name, root);
	}
	
	public void setAnims(List<Animation> anims){
		this.anims = anims;
	}
	
	public List<Animation> getAnims(){
		return this.anims;
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
