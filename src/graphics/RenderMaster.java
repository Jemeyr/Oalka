package graphics;


import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glEnable;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class RenderMaster{
	
	ModelFactory modelFactory;
	
	List<Model> models;
	Camera camera;
	Shader skinnedShader;
	
	public RenderMaster()
	{
		try{
			Display.setDisplayMode(new DisplayMode(800, 600));
			Display.create();
		}
		catch (Exception e)
		{
			System.out.println("RenderMaster Failing hardcore " + e);
			System.exit(-1);
		}
		
		
        glEnable(GL_DEPTH_TEST);
		
		String skinnedVertexShader = "temp/skinVertShader.txt";
		String skinnedFragShader = "temp/skinFragShader.txt";
		
		skinnedShader = new Shader(skinnedFragShader, skinnedVertexShader);
		
		modelFactory = new ModelFactory(skinnedShader);
		models = new ArrayList<Model>();
		
		List<Shader> shaders = new ArrayList<Shader>();
		shaders.add(skinnedShader);
		camera = new Camera(shaders);
	}

	
	public void render() {
		long time = System.currentTimeMillis();
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		skinnedShader.use();
		camera.setActive(skinnedShader);
		for(Model model : models)
		{
			model.draw(time);
		}
	
        Display.update();
    }

	public Camera getCamera() {
		return this.camera;
	}

	public void loadMeshes(String[] filenames) {
		for(String file : filenames){
			modelFactory.loadModel(file);
		}
	}

	public void unloadMeshes(String[] filenames) {
		for(String file : filenames){
			modelFactory.unloadModel(file);
		}
	}

	public Model addModel(String filename) {
		Model m = modelFactory.getModel(filename);
		models.add(m);	
		
		return (Model)m;
	}

}
