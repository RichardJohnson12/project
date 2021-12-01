import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
  
public class M01_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
    
  public M01_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f,6f,15f));
    this.camera.setTarget(new Vec3(0f,5f,0f));
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {   
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    initialise(gl);
    startTime = getSeconds();
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    disposeModels(gl);
  }

  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */
   
  private Camera camera;
  private Model tt1, cube, sphere, lightBase, lightPole, lightSupport, lightSocket, mobBase, mob;
  private Light light;

  private void disposeModels(GL3 gl) {
    tt1.dispose(gl);
    cube.dispose(gl);
    sphere.dispose(gl);
    light.dispose(gl);
  }
  



  public void initialise(GL3 gl) {
    createRandomNumbers();
    int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/chequerboard.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/container2.jpg");
    int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
    int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/jade.jpg");
    int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/jade_specular.jpg");
    
    light = new Light(gl);
    light.setCamera(camera);
    
    Mesh m = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    Material material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    Mat4 modelMatrix = Mat4Transform.scale(32,1f,32);
    tt1 = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId0);

    m = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(6,2,6), Mat4Transform.translate(0,0.5f,0));
    cube = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId1, textureId2);

    m = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    shader = new Shader(gl, "vs_sphere_04.txt", "fs_sphere_04.txt");

    // no texture version
    // shader = new Shader(gl, "vs_sphere_04.txt", "fs_sphere_04_notex.txt");

    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(3,6,3), Mat4Transform.translate(0,0.5f,0));
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0,2,0), modelMatrix);

    sphere = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId3, textureId4);

    // no texture version
    // sphere = new Model(gl, camera, light, shader, material, modelMatrix, m);


    //LIGHTBASE FROM HERE
    m = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
//    shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
//    material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,2,4), Mat4Transform.translate(3.5f,0.5f,0));
    lightBase = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId1);

    m = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
//    shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
//    material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(0.5f,15,0.5f), Mat4Transform.translate(28,0.6f,0));
    lightPole = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId1);

    m = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(5f,0.5f,0.5f), Mat4Transform.translate(2.25f,32.5f,0));
    lightSupport = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId1);

    m = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(1,1,1), Mat4Transform.translate(9,15.5f,0));
    lightSocket = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId1);

   //Lightbulb needs doing

    //Mobile phone
    m = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
//    shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
//    material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(6,1,6), Mat4Transform.translate(1,0.5f,-2.25f));
    mobBase = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId1);

    m = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
//    shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
//    material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,6,1), Mat4Transform.translate(1.5f,0.6f,-12f));
    mob = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId1);



  }
 
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    
    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);

    tt1.render(gl);
    cube.render(gl);
    sphere.render(gl);
    lightBase.render(gl);
    lightPole.render(gl);
    lightSupport.render(gl);
    lightSocket.render(gl);
    mobBase.render(gl);
    mob.render(gl);
  }

  // The light's postion is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 2.7f;
    float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);
    
    //return new Vec3(5f,3.4f,5f);  // use to set in a specific position for testing
  }
  
    // ***************************************************
  /* TIME
   */ 
  
  private double startTime;
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

  // ***************************************************
  /* An array of random numbers
   */ 
  
  private int NUM_RANDOMS = 1000;
  private float[] randoms;
  
  private void createRandomNumbers() {
    randoms = new float[NUM_RANDOMS];
    for (int i=0; i<NUM_RANDOMS; ++i) {
      randoms[i] = (float)Math.random();
    }
  }
  
  
}