package us.zhaar.ggj

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.graphics.g2d.Sprite


class Wave (amplitude: Float, waveLength: Float){
  
    val vertexShader = """
      attribute vec4 a_position;    \n
      attribute vec2 a_texCoord0;\n
      uniform mat4 u_worldView;\n
      varying vec4 v_color;
      varying vec2 v_texCoords;
      void main()                  \n
      {                            \n
         v_color = vec4(1, 1, 1, 1); \n
         v_texCoords = a_texCoord0; \n
         gl_Position =  u_worldView * a_position;  \n
      }                            \n"""
  
  val fragmentShader = """#ifdef GL_ES\n
    precision mediump float;\n
    #endif\n"
    varying vec4 v_color;\n
      varying vec2 v_texCoords;\n
      uniform sampler2D u_texture;\n
      uniform sampler2D u_texture2;\n
      uniform float timedelta;\n
      void main()                                  \n
      {                                            \n
        vec2 displacement = texture2D(u_texture2, v_texCoords/6.0).xy;\n
        float t=v_texCoords.y +displacement.y*0.1-0.15+  (sin(v_texCoords.x * 60.0+timedelta) * 0.005); \n
        gl_FragColor = v_color * texture2D(u_texture, vec2(v_texCoords.x,t));\n
      }"""

  val fragmentShader2 = """#ifdef GL_ES\n
      precision mediump float;\n
      #endif\n
      varying vec4 v_color;\n
      varying vec2 v_texCoords;\n
      uniform sampler2D u_texture;\n
      void main()                                  \n
      {                                            \n
        gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n
      }"""
    
  val w = 1280
  val h = 720
  val matrix = new Matrix4()
  
  val shader = new ShaderProgram(vertexShader, fragmentShader)
  lazy val waterShader = new ShaderProgram(vertexShader, fragmentShader2)
  
  lazy val waterMesh = createQuad(-1, -1, 1, -1, 1, -0.3f, -1, -0.3f);
  
  lazy val coloredTexture: Texture = buildTexture("art/water.png");
  lazy val edgeTexture: Texture = buildTexture("art/waterdisplacement.png")
    
  def buildTexture(filePath: String) : Texture = {
    val texture = new Texture(filePath)
    texture.setFilter(TextureFilter.Linear, TextureFilter.Linear)
    texture
  }
  
  def init() : Wave = {
    edgeTexture.bind()
    ShaderProgram.pedantic=false;
    this
  }
  
  def angle(time: Float) : Float = {
    val angle = time * (2 * MathUtils.PI);
    if(angle > (2 * MathUtils.PI)) angle - (2 * MathUtils.PI) else angle;
  }
    
  def draw(batch: SpriteBatch, stage: Stage, elapsedTime: Float) : Unit = {
    batch.setShader(waterShader);
    Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    Gdx.gl20.glEnable(GL20.GL_BLEND);
    coloredTexture.bind(1);
    edgeTexture.bind(2);

    shader.begin();
    shader.setUniformMatrix("u_worldView",  stage.getCamera().combined);
    shader.setUniformi("u_texture", 1);
    shader.setUniformi("u_texture2", 2);
    shader.setUniformf("timedelta", -angle(elapsedTime));
    waterMesh.render(shader, GL20.GL_TRIANGLE_FAN);
    shader.end();
  }
  def createQuad(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float,
      y3: Float, x4: Float, y4: Float): Mesh = {
    val verts = Array(y1, 0, 1f, 1f,
        x2, y2, 0, 0f, 1f, 
        x3, y3, 0, 0f, 0f,
        x4, y4, 0, 1f, 0f)

    val mesh = new Mesh(true, 4, 0,
        new VertexAttribute(Usage.Position, 3,
            ShaderProgram.POSITION_ATTRIBUTE), new VertexAttribute(
            Usage.TextureCoordinates, 2,
            ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

    mesh.setVertices(verts);
    return mesh;
  }
}