package skeleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Matrix4f;


public class Animation {
	//list of (frame, matrix) pairs
	//skeleton has a list of animations with weights
	
	//animation has a start time, and can get the weights for each bone.
	//should have a method to return the weight for a bone
	
	//anim should hold all of the keyframes in an animation
	//anim should be used to get the current pose
	//anim should hold a start time
	//anim should be cyclic or not
	
	//anim should have start/stop methods
	//anim should be able to return something relating bone STRING IDs to their transforms
	
	//anim should NOT hold weights
	
	//anim should never harm or through inaction allow a human being to come to harm
	
	
	private List<Map<String,Matrix4f>> keyframes;
	private float[] keyframeTimes;
	private Map<String,Matrix4f> currentPose;
	private long lastTime;
	private int keyframeCount = 0;
	
	private float interpolationStep = 0.0f;
	
	private boolean running = false;
	private boolean cyclic = true;
	
	private int lastIndex = 0;
	private int nextIndex = 1;
	

	public Animation(boolean isCyclic, Map<String,List<Pose>> poseList, float[] keyframeTimes){
		this.cyclic = isCyclic; //true assumes first pose and last are identical
		
		
		this.keyframes = new ArrayList<Map<String,Matrix4f>>();
		this.keyframeTimes = keyframeTimes;
		this.keyframeCount = this.keyframeTimes.length;
		
		for(int i = 0; i < keyframeCount; i++){
			Map<String, Matrix4f> keyframeMap = new HashMap<String, Matrix4f>();
			
			for(Entry<String,List<Pose>> e : poseList.entrySet()){
				keyframeMap.put(e.getKey(), e.getValue().get(i).getTransform());
			}
			this.keyframes.add(keyframeMap);
		}
		

		this.currentPose = new HashMap<String, Matrix4f>();	
		this.lastTime = 0l;
		setupCurrentPose();
		
	}
	
	
	public void start(long time){
		this.lastTime = time;
		this.interpolationStep = 0.0f;
		this.running = true;
	}

	public Map<String, Matrix4f> getPose(long time){

		interpolationStep += (float)(time - lastTime) / 1000.0f;
		//TODO: scale by fps?
		
		if(interpolationStep > 1.0f){
			
			if(!cyclic){
				running = false;
				lastIndex = 0;
				nextIndex = 1;
			}
			else
			{
				lastIndex = nextIndex;
				nextIndex = (nextIndex + 1) % keyframeCount;
			}
			
			interpolationStep = 0.0f;
			System.out.println("interpolating from " + lastIndex + " to " + nextIndex);
			
		}
		
		
		//set current pose from interpolating members between the two keyframes
		Map<String, Matrix4f> a = keyframes.get(lastIndex);
		Map<String, Matrix4f> b = keyframes.get(nextIndex);
		
		for(Entry<String, Matrix4f> e : this.currentPose.entrySet()){
			String id = e.getKey();
			Matrix4f result = new Matrix4f(matrixInterpolate(a.get(id),b.get(id),interpolationStep));
			
			this.currentPose.put(id, result);
		}
		
		
		lastTime = time;		
		return this.currentPose;
	}
	
	private void setupCurrentPose(){
		//sets the current pose to the first frame, this makes sure that the other current poses know which bones to apply to
		for(Entry<String, Matrix4f> e : keyframes.get(0).entrySet())
		{
			this.currentPose.put(e.getKey(), e.getValue());
		}
	}
	
	
	
	//no news every pose, ugh
	private static Matrix4f m = new Matrix4f();
	public static Matrix4f matrixInterpolate(Matrix4f a, Matrix4f b, float interpolationStep){

		m.setZero();
		
		float inverseStep = 1.00f - interpolationStep;

		//LERPING MATRICES COMPONENT-WISE IS GREAT! AW YEAH
		m.m00 = a.m00 * inverseStep + b.m00 * interpolationStep;	m.m01 = a.m01 * inverseStep + b.m01 * interpolationStep;	m.m02 = a.m02 * inverseStep + b.m02 * interpolationStep;	m.m03 = a.m03 * inverseStep + b.m03 * interpolationStep;
		m.m10 = a.m10 * inverseStep + b.m10 * interpolationStep;	m.m11 = a.m11 * inverseStep + b.m11 * interpolationStep;	m.m12 = a.m12 * inverseStep + b.m12 * interpolationStep;	m.m13 = a.m13 * inverseStep + b.m13 * interpolationStep;
		m.m20 = a.m20 * inverseStep + b.m20 * interpolationStep;	m.m21 = a.m21 * inverseStep + b.m21 * interpolationStep;	m.m22 = a.m22 * inverseStep+ b.m22 * interpolationStep;	m.m23 = a.m23 * inverseStep + b.m23 * interpolationStep;
		m.m30 = a.m30 * inverseStep + b.m30 * interpolationStep;	m.m31 = a.m31 * inverseStep + b.m31 * interpolationStep;	m.m32 = a.m32 * inverseStep + b.m32 * interpolationStep;	m.m33 = a.m33 * inverseStep + b.m33 * interpolationStep;

		return m;
	}
	
	

}
