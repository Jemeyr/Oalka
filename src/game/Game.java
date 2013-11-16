package game;


import graphics.Camera;
import graphics.Model;
import graphics.RenderMaster;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import skeleton.Animation;
import skeleton.Bone;
import skeleton.Pose;
import skeleton.Skeleton;

public class Game {

	//hacks for demoing stuff to myself
	public static Skeleton skeleton;
	public static Animation animation;
	public static int frames;
	
	public static Model root;
	
	public static List<Matrix4f> bindPoses;
	public static List<String> joints;
	
	public static void pose(Bone bone, int alpha, int beta, float amount, Matrix4f parent){
		
		//Only body seems to do anything right now
		for(Bone b : bone.children){
			
			
			List<Pose> poses = animation.getPoses(b.name);
			Matrix4f am = poses.get(alpha).getTransform();
			Matrix4f bm = poses.get(beta).getTransform();
			
			
			
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
	
	public static void main(String[] args)
	{

		RenderMaster renderMaster = new RenderMaster();

		Camera camera = renderMaster.getCamera();
		float rotation = 0.0f;
		float height = 0.0f;
		float lent = 15.0f;
		float fov = 90.0f;
		
		
		String[] filenames = {"temp/skeletan.dae", "temp/werman.dae", "temp/tt.dae", "temp/two.dae"};
		renderMaster.loadMeshes(filenames);
		
		root = renderMaster.addModel("temp/werman.dae");
		Quaternion q = new Quaternion();
		q.setFromAxisAngle(new Vector4f(1.0f, 0.0f, 0.0f, -(float)Math.PI/2.0f));
		
		root.addPosition(new Vector3f(0.0f, -5.0f, 0.0f));
		root.addRotation(q);
		
		Game.frames = Game.animation.getPoses("Body").size();		//Hacky static use of bone name, but the whole thing is hacky so deal with it.
		
		float someamount = 0.0f;
		int curr = 0;
		int next = 1;
		

		pose(skeleton.root, curr, next, someamount, null);
		while(!Display.isCloseRequested())
		{
			//close if escape is hit
			if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
				Display.destroy();
				break;
			}
			
			if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
			{
				rotation += 0.015f;	
			}else if(Keyboard.isKeyDown(Keyboard.KEY_LEFT))
			{
				rotation -= 0.015f;
			}
			
			if(Keyboard.isKeyDown(Keyboard.KEY_UP))
			{
				height += 0.05f;	
				
			}else if(Keyboard.isKeyDown(Keyboard.KEY_DOWN))
			{
				height -= 0.05f;
			}
			
			if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
			{
				fov *= 1.01f;
			}
			else if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			{
				fov *= 0.99f;
			}
			
			if(Keyboard.isKeyDown(Keyboard.KEY_D))
			{
				root.addPosition(new Vector3f(0.0f, 0.0f, 0.05f));				
			}
			else if(Keyboard.isKeyDown(Keyboard.KEY_A))
			{
				root.addPosition(new Vector3f(0.0f, 0.0f, -0.05f));	
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_W))
			{
				root.addPosition(new Vector3f(-0.05f, 0.0f, 0.0f));
			}
			else if(Keyboard.isKeyDown(Keyboard.KEY_S))
			{
				root.addPosition(new Vector3f(0.05f, 0.0f, 0.0f));
			}

			someamount += 0.01f;
			
			if(someamount > 1.0f){
				curr = next;
				next = next == frames - 1 ? 0 : next + 1;
				someamount = 0.0f;
				System.out.println("curr/next: " + curr + "/" + next);
			}
			
			//pose!
			//pose(skeleton.root, curr, next, someamount, null);
			
			
			
			fov = fov > 180.0f ? 180.0f : fov <= 0.0f ? 0.0f : fov;
			
			lent = 15f / (float)Math.atan(fov * 0.0349066);
			
			camera.setPosition(new Vector3f(lent * (float)Math.sin(rotation), 
											height, 
											lent * (float)Math.cos(rotation)));
			
			camera.setFOV(fov);
			
			
			
			renderMaster.render();
			
		}

		
	}

}
