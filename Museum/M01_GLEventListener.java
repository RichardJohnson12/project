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
  private Model tt1, cube, sphere, lightBase, lightPole, lightSupport, lightSocket, mobBase, mob, wallBack, wallLeft;
  private Light light;
  private SGNode robotRoot;
  private float xPosition = 0;
  private TransformNode translateX, robotMoveTranslate, footRotate;

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
    int[] phoneGraphic = TextureLibrary.loadTexture(gl, "textures/phoneGraphic.jpg");
    int[] granite = TextureLibrary.loadTexture(gl, "textures/granite.jpg");
    int[] backWall = TextureLibrary.loadTexture(gl, "textures/backWall.jpg");
    light = new Light(gl);
    light.setCamera(camera);
    
    Mesh m = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    Material material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    Mat4 modelMatrix = Mat4Transform.scale(32,1f,32);
    tt1 = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId0);

    m = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
     material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(32,16f,1),Mat4Transform.rotateAroundX(90));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.translate(0,-16,-0.5f));

    wallBack = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId0);

    m = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90),Mat4Transform.scale(32,16f,1) );
//    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.translate(0,0,0.5f));
//    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(32,1f,16));
  modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.rotateAroundZ(-90));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.translate(0, -0.5f, 0));
    wallLeft = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId0);

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
    shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,2,4), Mat4Transform.translate(3.5f,0.5f,0));
    lightBase = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId1);

    m = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
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
    shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(6,1,6), Mat4Transform.translate(1,0.5f,-2.25f));
    mobBase = new Model(gl, camera, light, shader, material, modelMatrix, m, granite);

    m = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,6,1), Mat4Transform.translate(1.5f,0.6f,-12f));
    mob = new Model(gl, camera, light, shader, material, modelMatrix, m, phoneGraphic);

    // robot

    float bodyHeight = 2f;
    float bodyWidth = 2f;
    float bodyDepth = 1f;

    float headScale = 2f;
    float headHeight = bodyHeight + 2;
    float earScale = 1;
    float earSize = 0.5f;
    float earMove = 0.9f;

    float noseScale = 0.5f;
    float noseHeight = headHeight;



    float footSize = 1f;
    float footScale = 1f;

    robotRoot = new NameNode("root");
    robotMoveTranslate = new TransformNode("robot transform",Mat4Transform.translate(xPosition - 10,0,0));

    TransformNode robotTranslate = new TransformNode("robot transform",Mat4Transform.translate(0, footSize,0));

    NameNode body = new NameNode("body");
    Mat4 mat = Mat4Transform.scale(bodyWidth,bodyHeight,bodyDepth);
    mat = Mat4.multiply(mat, Mat4Transform.translate(0,0.5f,0));
    TransformNode bodyTransform = new TransformNode("body transform", mat);
    ModelNode bodyShape = new ModelNode("Cube(body)", cube);

    NameNode head = new NameNode("head");
    mat = new Mat4(1);
    mat = Mat4.multiply(mat, Mat4Transform.translate(0,bodyHeight,0));
    mat = Mat4.multiply(mat, Mat4Transform.scale(headScale,headScale,headScale));
    mat = Mat4.multiply(mat, Mat4Transform.translate(0,0.5f,0));
    TransformNode headTransform = new TransformNode("head transform", mat);
    ModelNode headShape = new ModelNode("Sphere(head)", sphere);

    NameNode leftEar = new NameNode("left ear");
    mat = new Mat4(1);
    mat = Mat4.multiply(mat, Mat4Transform.translate(0,headHeight,0));
    mat = Mat4.multiply(mat, Mat4Transform.scale(earScale,earScale,earScale));
    mat = Mat4.multiply(mat, Mat4Transform.translate(-earMove,0f,0));
    TransformNode leftEarTransform = new TransformNode("ear transform", mat);
    ModelNode leftEarShape = new ModelNode("Sphere(ear)", sphere);

    NameNode rightEar = new NameNode("right ear");
    mat = new Mat4(1);
    mat = Mat4.multiply(mat, Mat4Transform.translate(0,headHeight,0));
    mat = Mat4.multiply(mat, Mat4Transform.scale(earScale,earScale,earScale));
    mat = Mat4.multiply(mat, Mat4Transform.translate(earMove,0f,0));
    TransformNode rightEarTransform = new TransformNode("ear transform", mat);
    ModelNode rightEarShape = new ModelNode("Sphere(ear)", sphere);

    NameNode nose = new NameNode("nose");
    mat = new Mat4(1);
    mat = Mat4.multiply(mat, Mat4Transform.translate(0,noseHeight,0));
    mat = Mat4.multiply(mat, Mat4Transform.scale(noseScale,noseScale,noseScale));
    mat = Mat4.multiply(mat, Mat4Transform.translate(0,-headScale*0.9f,1.7f));
    TransformNode noseTransform = new TransformNode("nose transform", mat);
    ModelNode noseShape = new ModelNode("Sphere(ear)", sphere);




    NameNode foot = new NameNode("foot");
    mat = new Mat4(1);
    mat = Mat4.multiply(mat, Mat4Transform.translate(bodyWidth-0.5f,0,0));
    mat = Mat4.multiply(mat, Mat4Transform.rotateAroundX(180));
    mat = Mat4.multiply(mat, Mat4Transform.scale(footScale,footSize,footScale));
    mat = Mat4.multiply(mat, Mat4Transform.translate(-1.5f,0.5f,0));
    TransformNode footTransform = new TransformNode("foot transform", mat);
    ModelNode footShape = new ModelNode("sphere", sphere);

    robotRoot.addChild(robotMoveTranslate);
    robotMoveTranslate.addChild(robotTranslate);
    robotTranslate.addChild(body);
    body.addChild(bodyTransform);
    bodyTransform.addChild(bodyShape);
    body.addChild(head);
    head.addChild(headTransform);
    headTransform.addChild(headShape);
    head.addChild(leftEar);
    leftEar.addChild(leftEarTransform);
    leftEarTransform.addChild(leftEarShape);
    head.addChild(rightEar);
    rightEar.addChild(rightEarTransform);
    rightEarTransform.addChild(rightEarShape);
    head.addChild(nose);
    nose.addChild(noseTransform);
    noseTransform.addChild(noseShape);
    body.addChild(foot);
    foot.addChild(footTransform);
    footTransform.addChild(footShape);


    robotRoot.update();  // IMPORTANT - don't forget this
    //robotRoot.print(0, false);
    //System.exit(0);



  }
 
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    
    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);

    tt1.render(gl);
    wallBack.render(gl);
    wallLeft.render(gl);
    cube.render(gl);
    sphere.render(gl);
    lightBase.render(gl);
    lightPole.render(gl);
    lightSupport.render(gl);
    lightSocket.render(gl);
    mobBase.render(gl);
    mob.render(gl);


    robotRoot.draw(gl);
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