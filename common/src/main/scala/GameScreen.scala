package us.zhaar.ggj

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL10
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.Input.Keys

class GameScreen extends Screen {
  val batch = new SpriteBatch
  val stage = new Stage
  val moveDelta = 8
  var elapsed = 0f;
  lazy val ship = new Ship(new Texture("art/ship3.png"))
  
  class Ship(text: Texture) extends Image(text){
    override def draw(batch: SpriteBatch, alpha: Float): Unit = {
      validate();

      val color = getColor();
      batch.setColor(color.r, color.g, color.b, color.a * alpha);

      val x = getX
      val y = getY
      val originX = getOriginX
      val originY = getOriginY
      val xx = x - originX
      val yy = y - originY
      val imageWidth = getWidth
      val imageHeight = getHeight
      val scaleX = getScaleX
      val scaleY = getScaleY
      val drawable = getDrawable

      val region = drawable.asInstanceOf[TextureRegionDrawable].getRegion();
      val rotation = getRotation();
      batch.draw(region,
          xx,
          yy,
          originX,
          originY,
          imageWidth,
          imageHeight,
          scaleX,
          scaleY, rotation);
    }
  }
  
  class obstacle{
    
  }
  
  def getRainbow : Color = {
    def sinValue(e : Float):Float = (Math.sin(elapsed + e).toFloat + 1f)/2f
    new Color(sinValue(-(Math.PI).toFloat * 2f / 3f), sinValue(Math.PI.toFloat * 2f / 3f), sinValue(0f), 1)
  }

  def render(delta: Float) = {
    elapsed += delta
    val c = getRainbow
    Gdx.gl.glClearColor(c.r, c.g, c.b , 0.8f)
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT)

    batch.begin()

    if(Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) 
      ship addAction(Actions.moveBy(-moveDelta, 0, 0.1f))
    if(Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) 
      ship addAction(Actions.moveBy(moveDelta, 0, 0.1f))
    if(Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)) 
      ship addAction(Actions.moveBy(0, moveDelta, 0.2f))
    if(Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S)) 
      ship addAction(Actions.moveBy(0, -moveDelta, 0.2f))
    stage.act(delta);
    stage.draw;
    
    batch.end()
  }

  def resize(width: Int, height: Int) = {}
  
  def show() = {
    stage addActor ship
    ship.setX(640);
    ship.setY(360)
    ship.setOrigin(ship.getWidth()/2, ship.getHeight()/2)
  }
  
  def hide() = { dispose }
  def pause() = {}
  def resume() = {}
  def dispose() = {}
}