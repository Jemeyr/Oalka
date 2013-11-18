package graphics;

import game.Game;

import java.util.HashMap;

import loader.ColladaLoader;
import skeleton.Animation;
import skeleton.Skeleton;

public class ModelFactory {
	
	private Shader skinnedShader;
	
	private Skeleton hackySkeleton;
	
	private HashMap<String, Mesh> loadedMeshes;
	
	public ModelFactory(Shader skinnedShader)
	{
		this.skinnedShader = skinnedShader;
		this.loadedMeshes = new HashMap<String, Mesh>();
	}
	
	
	public Model getModel(String filename)
	{
		if(!loadedMeshes.containsKey(filename))
		{
			loadModel(filename);	
		}
		
		Mesh m = loadedMeshes.get(filename);

		return new Model(m, this.skinnedShader, hackySkeleton);
	}
	
	public void loadModel(String filename)
	{
		if(!loadedMeshes.containsKey(filename))
		{

			HashMap<String, Object> modelData = ColladaLoader.load(filename);
			
			//HACK
			if(filename.equals("temp/skeletan.dae")){
				Game.skeleton = (Skeleton)modelData.get("skeleton");
				Game.animation = (Animation)modelData.get("animation");
			}
			
			
			//only set it if it's true, otherwise we overwrite with null, durr
			if(modelData.containsKey("skeleton")){
				this.hackySkeleton = (Skeleton)modelData.get("skeleton");
			}
			
			Mesh mesh = new Mesh(filename, skinnedShader, modelData);
			
			loadedMeshes.put(filename, mesh);
	
		}
	}
	
	public void unloadModel(String filename)
	{
		loadedMeshes.get(filename).delete();
		loadedMeshes.remove(filename);
		
	}
	
	
	
}
