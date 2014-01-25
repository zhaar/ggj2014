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
import com.badlogic.gdx.utils.Pool.Poolable

class GameScreen extends Screen {
  val batch = new SpriteBatch
  val stage = new Stage
  val moveDelta = 8
  var elapsed = 0f
  var delay = 0f
  val maxProjectiles = 4
  lazy val ship = new Ship(new Texture("art/ship3.png"))
  val waves = new Wave(1,1).init
  
  def getRainbow : Color = {
    def sinValue(e : Float):Float = (Math.sin(elapsed + e).toFloat + 1f)/3f
    new Color(sinValue(-(Math.PI).toFloat * 2f / 3f), sinValue(Math.PI.toFloat * 2f / 3f), sinValue(0f), 1)
  }
  
  def checkBounds(ship: Ship) : Unit  = {
    if(ship.getX() < 30) ship.setX(30)
    if(ship.getX() > 1250) ship setX 1350
    if(ship.getY() > 690) ship setY 690
    if(ship.getY() < 30) ship setY 30
  }
  
  def checkScene(scene: Stage): Stage = {
    val actors = scene.getActors();
    for(i <- 0 until actors.size){
      val actor = actors get i
      actor match{
        case proj: Projectile => if(actor.getActions().size == 0) actor.remove();
        case ship: Ship => checkBounds(ship);
      }
    }
    
    scene
  }

  def render(delta: Float) = {
    elapsed += delta
    if(delay  > 0) delay -= delta
    else delay = 0
    val c = getRainbow
    Gdx.gl.glClearColor(c.r, c.g, c.b , 1)
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
    if(Gdx.input.isKeyPressed(Keys.SPACE) && delay == 0){
      delay = 0.3f;
      stage addActor(ship.shoot)
    }
    

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