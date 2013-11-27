package game;


import graphics.Camera;
import graphics.Model;
import graphics.RenderMaster;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Game {

	//hacks for demoing stuff to myself
	public static int frames;
	
	public static Model root;
	
	public static void main(String[] args)
	{

		RenderMaster renderMaster = new RenderMaster();

		Camera camera = renderMaster.getCamera();
		float rotation = 0.0f;
		float height = 0.0f;
		float lent = 15.0f;
		
		
		String[] filenames = {"temp/batan.dae", "temp/dude.dae"};//"temp/skeletan.dae", "temp/werman.dae", "temp/tt.dae", "temp/two.dae"};
		renderMaster.loadMeshes(filenames);
		
		Quaternion q = new Quaternion();
		
		Model baton = renderMaster.addModel("temp/batan.dae");
		q.setFromAxisAngle(new Vector4f(0.0f, 1.0f, 0.0f, -(float)Math.PI/2.0f));
		baton.addPosition(new Vector3f(-8.0f, 2.0f, 0.0f));
		baton.addRotation(q);
		
		q = new Quaternion();
		root = renderMaster.addModel("temp/dude.dae");
		q.setFromAxisAngle(new Vector4f(1.0f, 0.0f, 0.0f, -(float)Math.PI/2.0f));
		
		root.addPosition(new Vector3f(0.0f, -5.0f, 0.0f));
		root.addRotation(q);
		
		float tscale = 1.0f;

		float speed = 0.05f;
		
		boolean holdBaton = false;

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
			}else if(Keyboard.isKeyDown(Keyboard.KEY_UP))
			{
				height += 0.5f;	
				
			}else if(Keyboard.isKeyDown(Keyboard.KEY_DOWN))
			{
				height -= 0.5f;
			}else if(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
			{
				lent = lent > 0 ? lent - 0.5f : 0.0f;
			}else if(Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
			{
				lent += 0.5f;
			}
			
			
			if(Keyboard.isKeyDown(Keyboard.KEY_W))
			{
				root.addPosition(new Vector3f(0.0f, 0.0f, speed));				
			}
			else if(Keyboard.isKeyDown(Keyboard.KEY_S))
			{
				root.addPosition(new Vector3f(0.0f, 0.0f, -speed));	
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_D))
			{
				root.addPosition(new Vector3f(-speed, 0.0f, 0.0f));
			}
			else if(Keyboard.isKeyDown(Keyboard.KEY_A))
			{
				root.addPosition(new Vector3f(speed, 0.0f, 0.0f));
			}
			


			if(Keyboard.isKeyDown(Keyboard.KEY_Z) && !holdBaton){
				root.addChild(baton, "ForeArm.L");
				holdBaton = true;
			}
			
			if(Keyboard.isKeyDown(Keyboard.KEY_R)){
				tscale *= 1.01f;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_F)){
				tscale *= 0.99f;
			}
			
		
			camera.setPosition(new Vector3f(lent * (float)Math.sin(rotation), 
											height, 
											lent * (float)Math.cos(rotation)));
			
			if(Keyboard.isKeyDown(Keyboard.KEY_X)){
				camera.setTarget(root.getPosition());
			}
			
			renderMaster.render(tscale);
			
		}

		
	}

}
