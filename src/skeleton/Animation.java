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
	}

	public Map<String, Matrix4f> getPose(long time){

		interpolationStep += (float)(time - lastTime) / 1000.0f;
		//TODO: scale by fps?
		
		if(interpolationStep > 1.0f){
			
			if(!cyclic){
				lastIndex = 0;
				nextIndex = 1;
			}
			else
			{
				lastIndex = nextIndex;
				nextIndex = (nextIndex + 1) % keyframeCount;
			}
			
			interpolationStep = 0.0f;
			//System.out.println("interpolating from " + lastIndex + " to " + nextIndex);
			
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
	
	
	private static float interpolate(float a, float as, float b, float bs){
		double aca = Math.acos(a);
		double acb = Math.acos(b);
		
		return (float)Math.cos(aca*as+acb*bs);
	}
	
	//no news every pose, ugh
	private static Matrix4f m = new Matrix4f();
	public static Matrix4f matrixInterpolate(Matrix4f a, Matrix4f b, float interpolationStep){

		m.setZero();
		
		float inverseStep = 1.00f - interpolationStep;

		//LERPING MATRICES COMPONENT-WISE IS GREAT! AW YEAH
		m.m00 = interpolate(a.m00, inverseStep, b.m00, interpolationStep);
		m.m01 = interpolate(a.m01, inverseStep, b.m01, interpolationStep);
		m.m02 = interpolate(a.m02, inverseStep, b.m02, interpolationStep);
		m.m03 = interpolate(a.m03, inverseStep, b.m03, interpolationStep);

		m.m10 = interpolate(a.m10, inverseStep, b.m10, interpolationStep);
		m.m11 = interpolate(a.m11, inverseStep, b.m11, interpolationStep);
		m.m12 = interpolate(a.m12, inverseStep, b.m12, interpolationStep);
		m.m13 = interpolate(a.m13, inverseStep, b.m13, interpolationStep);

		m.m20 = interpolate(a.m20, inverseStep, b.m20, interpolationStep);
		m.m21 = interpolate(a.m21, inverseStep, b.m21, interpolationStep);
		m.m22 = interpolate(a.m22, inverseStep, b.m22, interpolationStep);
		m.m23 = interpolate(a.m23, inverseStep, b.m23, interpolationStep);

		m.m33 = interpolate(a.m33, inverseStep, b.m33, interpolationStep);
		
		//interpolate angularly
		double api = Math.atan2(a.m30,a.m31);
		double atheta = Math.atan2(a.m32,api);
		double ar2 = (double)(a.m30*a.m30 + a.m31*a.m31 + a.m32*a.m32);
		
		double bpi = Math.atan2(b.m30,b.m31);
		double btheta = Math.atan2(b.m32,bpi);
		double br2 = (double)(b.m30*b.m30 + b.m31*b.m31 + b.m32*b.m32);
		
		
		double mpi = api * inverseStep + bpi * interpolationStep;
		double mtheta = atheta * inverseStep + btheta * interpolationStep;
		double mr = Math.sqrt(ar2) * inverseStep + Math.sqrt(br2) * interpolationStep;
		
		m.m32 = (float)(mpi * Math.tan(mtheta));
		m.m31 = (float)(mr * Math.cos(mpi));
		m.m30 = (float)(mr * Math.sin(mpi));

		return m;
	}

	
	public static void matrixFinalize(Matrix4f m){
		m.m00 = (float)Math.sin(m.m00);
		m.m01 = (float)Math.sin(m.m01);
		m.m02 = (float)Math.sin(m.m02);
		m.m03 = (float)Math.sin(m.m03);
		
		m.m10 = (float)Math.sin(m.m10);
		m.m11 = (float)Math.sin(m.m11);
		m.m12 = (float)Math.sin(m.m12);
		m.m13 = (float)Math.sin(m.m13);
		
		m.m20 = (float)Math.sin(m.m20);
		m.m21 = (float)Math.sin(m.m21);
		m.m22 = (float)Math.sin(m.m22);
		m.m23 = (float)Math.sin(m.m23);
		
		m.m33 = (float)Math.sin(m.m33);
		
	}

	public static void matrixAccumulate(Matrix4f sum, Matrix4f value, Float weight) {
		//added asin, later must be sined
		sum.m00 += Math.asin(value.m00) * weight;
		sum.m01 += Math.asin(value.m01) * weight;
		sum.m02 += Math.asin(value.m02) * weight;
		sum.m03 += Math.asin(value.m03) * weight;
		
		sum.m10 += Math.asin(value.m10) * weight;
		sum.m11 += Math.asin(value.m11) * weight;
		sum.m12 += Math.asin(value.m12) * weight;
		sum.m13 += Math.asin(value.m13) * weight;
		
		sum.m20 += Math.asin(value.m20) * weight;
		sum.m21 += Math.asin(value.m21) * weight;
		sum.m22 += Math.asin(value.m22) * weight;
		sum.m23 += Math.asin(value.m23) * weight;
		
		sum.m33 += Math.asin(value.m33) * weight;
		
		//added normally
		sum.m30 += value.m30 * weight;
		sum.m31 += value.m31 * weight;
		sum.m32 += value.m32 * weight;
	}
	
	

}
