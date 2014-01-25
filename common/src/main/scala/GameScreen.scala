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
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2

class GameScreen extends Screen {
  val batch = new SpriteBatch
  val stage = new Stage
  val moveDelta = 8f
  var elapsed = 0f
  var shootDelay = 0f
  var spawnDelay = 0f;
  val maxProjectiles = 4
  lazy val ship = new Ship(new Texture("art/ship3.png"))
  
  def getRainbow : Color = {
    def sinValue(e : Float):Float = (Math.sin(elapsed.toDouble + e).toFloat + 1f)/3f
    new Color(sinValue(-(Math.PI).toFloat * 2f / 3f), sinValue(Math.PI.toFloat * 2f / 3f), sinValue(0f), 1)
  }
  
  def checkBounds(ship: Ship) : Unit  = {
    if(ship.getX() < 30) ship.setX(30)
    if(ship.getX() > 1250) ship setX 1250
    if(ship.getY() > 690) ship setY 690
    if(ship.getY() < 30) ship setY 30
  }
  
  def checkScene(scene: Stage): Stage = {
    if(spawnDelay == 0 ) stage addActor(spawnBullet)

    val actors = scene.getActors();
    for(i <- 0 until actors.size){
      val actor = actors get i
      actor match{
        case p: Projectile => {
          if(actor.getActions().size == 0) actor.addAction(Actions.removeActor())}
        case s: Ship => checkBounds(s);
        case b: Bullet => {
          if(b contains(ship.getX(), ship.getY())) b.affectShip(ship)
          if(b.getActions().size == 0) b.addAction(Actions.removeActor())
        }
      }
    }
    
    scene
  }
  
  def spawnBullet: Bullet = {
    spawnDelay = 2 + Math.random.toFloat * 2
    val rand = Math.floor(Math.random() * 5)
    rand match{
      case 0 => spawnUnit(new Vector2(-1280,0), "art/friendlyProjectile3.png")
      case 1 => spawnUnit(new Vector2(-1280,0), "art/blackProjectile.png")
      case 2 => spawnUnit(new Vector2(-1280,0), "art/purpleProjectile.png")
      case 3 => spawnUnit(new Vector2(-1280,0), "art/yellowProjectile.png")
      case 4 => spawnUnit(new Vector2(-1280,0), "art/blueProjectile.png")
    }
  }
  
  def spawnUnit(target: Vector2, filePath: String): Bullet = {
    val bullet = new Bullet((ship:Ship) => ship.setScale(4), new Texture(filePath))
    bullet setPosition(640 + Math.random.toFloat * 640, Math.random.toFloat * 720)
    bullet addAction Actions.moveBy(target.x, target.y, 3, Interpolation.sineIn);
    bullet
  }
  
  def updateDelay(variable: Float, delta: Float): Float = {
    if(variable  > 0) variable - delta
    else 0 
  }

  def render(delta: Float) = {
    elapsed += delta
    shootDelay = updateDelay(shootDelay, delta);
    spawnDelay = updateDelay(spawnDelay, delta);
    val c = getRainbow
    Gdx.gl.glClearColor(c.r * ship.redModif, c.g * ship.greenModif, c.b * ship.blueModif , 1)
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
    if(Gdx.input.isKeyPressed(Keys.SPACE) && shootDelay == 0){
      shootDelay = 0.3f;
      stage addActor(ship.shoot)
    }
    

    stage.act(delta);
    stage.draw;
    checkScene(stage)
//    waves.draw(batch, stage, elapsed)
    
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
  def dispose() = { stage.dispose()
    batch.dispose}
}