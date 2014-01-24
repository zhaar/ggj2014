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

class GameScreen extends Screen {
  val batch = new SpriteBatch
  val stage = new Stage

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

  def render(delta: Float) = {
    Gdx.gl.glClearColor(0, 0, 0, 1)
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT)

    batch.begin()

    stage.act(delta);
    stage.draw;
    
    batch.end()
  }

  def resize(width: Int, height: Int) = {}
  def show() = {
    val ship = new Ship(new Texture("art/ship.png"))
    stage addActor ship
    ship.setX(640);
    ship.setY(360)
    ship setScale 0.5f
    ship.setOrigin(ship.getWidth()/2, ship.getHeight()/2)
  }
  def hide() = { dispose }
  def pause() = {}
  def resume() = {}
  def dispose() = {}
}