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

public class Game {

	//hacks for demoing stuff to myself
	public static int frames;
	
	public static Model root;
	
	public static List<Matrix4f> bindPoses;
	public static List<String> joints;

	
	public static void main(String[] args)
	{

		RenderMaster renderMaster = new RenderMaster();

		Camera camera = renderMaster.getCamera();
		float rotation = 0.0f;
		float height = 0.0f;
		float lent = 15.0f;
		float fov = 90.0f;
		
		
		String[] filenames = {"temp/skelet_both.dae", "temp/batan.dae"};//"temp/skeletan.dae", "temp/werman.dae", "temp/tt.dae", "temp/two.dae"};
		renderMaster.loadMeshes(filenames);
		
		root = renderMaster.addModel("temp/skelet_both.dae");
		Quaternion q = new Quaternion();
		q.setFromAxisAngle(new Vector4f(1.0f, 0.0f, 0.0f, -(float)Math.PI/2.0f));
		
		root.addPosition(new Vector3f(0.0f, -5.0f, 0.0f));
		root.addRotation(q);
		
		Model baton = renderMaster.addModel("temp/batan.dae");
		q.setFromAxisAngle(new Vector4f(0.0f, 1.0f, 0.0f, -(float)Math.PI/2.0f));
		baton.addPosition(new Vector3f(0.0f, 3.0f, 0.0f));
		baton.addRotation(q);
		
		root.addChild(baton);
		

		while(!Display.isCloseRequested())
		{
			//close if escape is hit
			if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
				Display.destroy();
				break;
			}
			
			if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
			{
				rotation += 0.03f;	
			}else if(Keyboard.isKeyDown(Keyboard.KEY_LEFT))
			{
				rotation -= 0.03f;
			}
			
			if(Keyboard.isKeyDown(Keyboard.KEY_UP))
			{
				height += 0.5f;	
				
			}else if(Keyboard.isKeyDown(Keyboard.KEY_DOWN))
			{
				height -= 0.5f;
			}
			
			
			if(Keyboard.isKeyDown(Keyboard.KEY_W))
			{
				root.addPosition(new Vector3f(0.0f, 0.0f, 0.1f));				
			}
			else if(Keyboard.isKeyDown(Keyboard.KEY_S))
			{
				root.addPosition(new Vector3f(0.0f, 0.0f, -0.1f));	
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_D))
			{
				root.addPosition(new Vector3f(-0.1f, 0.0f, 0.0f));
			}
			else if(Keyboard.isKeyDown(Keyboard.KEY_A))
			{
				root.addPosition(new Vector3f(0.1f, 0.0f, 0.0f));
			}

			
			
			lent = 30f / (float)Math.atan(fov * 0.0349066);
			
			camera.setPosition(new Vector3f(lent * (float)Math.sin(rotation), 
											height, 
											lent * (float)Math.cos(rotation)));
			
			
			renderMaster.render();
			
		}

		
	}

}
