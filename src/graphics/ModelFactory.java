package graphics;

import java.util.HashMap;

import loader.ColladaLoader;

public class ModelFactory {
	
	private Shader skinnedShader;
	private RenderMaster renderMaster;
	
	private HashMap<String, Mesh> loadedMeshes;
	
	public ModelFactory(Shader skinnedShader, RenderMaster renderMaster)
	{
		this.renderMaster = renderMaster;
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

		return new Model(m, this.skinnedShader, this.renderMaster);
	}
	
	public void loadModel(String filename)
	{
		if(!loadedMeshes.containsKey(filename))
		{
			HashMap<String, Object> modelData = ColladaLoader.load(filename);
			
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
