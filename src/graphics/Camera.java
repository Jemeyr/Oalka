package graphics;

import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	private Vector3f pos;
	private Vector3f target;
	
	private Matrix4f view;
	private Matrix4f perspective;
	
	private FloatBuffer viewPerspective;
	
	private boolean dirty;
	
	private Map<Shader, Integer> posUniforms;
	private Map<Shader, Integer> matUniforms;
	
	private float fov = 90.f;
	private float aspectRatio = 1.333f;
	private float near = 0.01f;
	private float far = 1000.0f;
	
	
	public Camera(List<Shader> shaders)
	{
		this.pos = new Vector3f(0.0f, 0.0f, 0.0f);
		this.target = new Vector3f(0.0f, 0.0f, 0.0f);
		
		this.dirty = true;
		
		this.view = GLOperations.buildViewMatrix(pos, target);
		this.perspective = GLOperations.buildPerspectiveMatrix(fov, aspectRatio, near, far);
		
		this.viewPerspective = GLOperations.generateFloatBuffer(Matrix4f.mul(perspective, view, null)); 
	
		this.posUniforms = new HashMap<Shader, Integer>();
		this.matUniforms = new HashMap<Shader, Integer>();

		for(Shader shader : shaders){
			shader.use();
			posUniforms.put(shader, shader.getUniforms().get("cameraPosition"));
			matUniforms.put(shader, shader.getUniforms().get("viewPerspective"));
		}
		
	}
	
	public void setActive(Shader shader)
	{
		dirty=true;
		update(shader);
	}
	
	public void setTarget(Vector3f newTar)
	{
		dirty = true;
		target = newTar;
	}
	
	public void setPosition(Vector3f newPos)
	{
		dirty = true;
		pos = newPos;
	}
	
	public void addPosition(Vector3f delta)
	{
		dirty = true;
		Vector3f.add(pos, delta, pos);
	}
	
	public void update(Shader shader)
	{
		if(dirty)
		{
			dirty = false;
			
			this.view = GLOperations.buildViewMatrix(pos, target);
			this.perspective = GLOperations.buildPerspectiveMatrix(fov, aspectRatio, near, far);
			
			this.viewPerspective = GLOperations.generateFloatBuffer(Matrix4f.mul(perspective, view, null));
			
			glUniformMatrix4(matUniforms.get(shader), false, viewPerspective);	
			
			glUniform3f(posUniforms.get(shader), pos.x, pos.y, pos.z);
			
		}
	}
	
	
	public void setFOV(float fov){
		this.fov = fov;
		dirty = true;
	}
	
}
