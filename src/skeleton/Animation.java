package skeleton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Animation {
	//First idea: each bone maps to a list of poses, each pose has a keyframe, so it can pick the best one
	// at this point keyframes are handled per bone, which isn't the case in the animations I'm doing, but might
	// be better for blending different animations later
	
	//Also,bones are kept as string names, so an animation is not bound to one skeleton? Maybe this is a horrible idea, maybe not.
	// It lets us load an animation without knowing about the skeleton though.
	private String id;
	private Map<String, List<Pose>> poses;
	
	public Animation(String id){
		this.id = id;
		this.poses = new HashMap<String, List<Pose>>();
	}
	
	public String getId(){
		return id;
	}
	
	public void addBone(String bonename, List<Pose> poses){
		this.poses.put(bonename, poses);
	}
	
	public List<Pose> getPoses(String bonename){
		return this.poses.get(bonename);
	}
	
	
	
	
}
