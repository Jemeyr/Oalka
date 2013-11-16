package skeleton;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;

public class Bone {
	
	public String name;
	public Matrix4f transform;
	public Matrix4f invBind;
	protected Bone parent;
	public List<Bone> children;
	
	
	//constructor assumes that a root bone is made
	public Bone(String name, Matrix4f offset){	
		this.children = new ArrayList<Bone>();
		
		this.name = name;
		this.transform = offset;
	}
	
	private Bone(Bone parent, String name, Matrix4f offset){
		this(name, offset);
		this.parent = parent;
	}
	
	public Bone addChild(String name, Matrix4f offset){
		Bone bone = new Bone(this, name, offset);
		this.children.add(bone);
		
		return bone;
	}

	public void setInvBind(Matrix4f invBind){
		this.invBind = invBind;
	}
	
}
