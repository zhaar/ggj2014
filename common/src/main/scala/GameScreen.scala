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
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Action

class GameScreen(game: Azurey) extends Screen {
  val batch = new SpriteBatch
  val stage = new Stage
  val maxProjectiles = 4
  val maxTime = 60;
  val moveDelta = 8f
  /*
   * lol variables
   */
  var elapsed = 0f
  var shootDelay = 0f
  var spawnDelay = 0f
  var bulletSpeed = 2f
  var pauseGame = false
  var accelerated = false
  
  val timer = new Label("", new LabelStyle(font, Color.WHITE))
  lazy val font = new BitmapFont(
        Gdx.files.internal("ui/gameFont.fnt"),
        new TextureRegion(new Texture("ui/gameFont.png")), false);
  lazy val ship = new Ship(new Texture("art/ship3.png"))
    
  def getSpeed = {
    if(accelerated)moveDelta *2
    else moveDelta
  }
  
  def getRainbow : Color = {
    def sinValue(e : Float):Float = (Math.sin(elapsed.toDouble + e).toFloat + 1f)/3f
    new Color(sinValue(-(Math.PI).toFloat * 2f / 3f), sinValue(Math.PI.toFloat * 2f / 3f), sinValue(0f), 1)
  }
  
  /*
   * checks if the ship isn't out of bounds
   */
  def checkBounds(ship: Ship) : Unit  = {
    if(ship.getX() < 30) ship.setX(30)
    if(ship.getX() > 1250) ship setX 1250
    if(ship.getY() > 690) ship setY 690
    if(ship.getY() < 30) ship setY 30
  }
  
  /*
   * desperate attempt to control side effects
   */
  def checkScene(scene: Stage): Stage = {
    if(spawnDelay == 0 && !pauseGame){
      spawnDelay = 1/(0.2f*elapsed + 2.1f) * 4.41f 
      spawnMultipleBullets(MathUtils.ceil((0.2f*elapsed + 4)).toInt)
    }

    val actors = scene.getActors();
    for(i <- 0 until actors.size){
      val actor = actors get i
      actor match{
        case p: Projectile => {
          if(actor.getActions().size == 0) actor.addAction(Actions.removeActor())}
        case s: Ship => checkBounds(s);
        case b: Bullet => {
          if(b contains(ship.getX(), ship.getY())) b.affectShip(ship)
          b.setScale(1+getWave(1, 2.1f, -.5f, elapsed))
          if(b.getActions().size == 0) b.addAction(Actions.removeActor())
        }
        case _ => Nil
      }
    }
    timer setText((((elapsed * 100 ).toInt)/100f).toString)
    
    scene
  }
  
  def getWave(amplitude: Float, waveLength: Float, phase: Float, x: Float): Float = (MathUtils.sin(x * 2f * MathUtils.PI * waveLength + phase) + 1 ) * amplitude
  
  def spawnMultipleBullets(amount :Int ) = {
    for(i <- 0 until amount)
      stage addActor(spawnBullet)
  }
  
  
  /*
   * ALL THOSE SIDE EFFECTS FKKK
   */
  def end(stage:Stage) : Unit = {
    val actors = stage.getActors();
    for(a <- 0 until actors.size){
      actors.get(a).clearActions()
    }
    val score = elapsed * bulletSpeed
    val scoreLabel = new Label("score ( time * speed) :\n " + score, new LabelStyle(font, Color.BLACK))
    scoreLabel.setPosition(1280/2 - scoreLabel.getWidth()/2, 720/2 - scoreLabel.getHeight()/2)
    stage addActor(scoreLabel)
    pauseGame = true;
  }
  
  def spawnBullet: Bullet = {
    val rand = Math.floor(Math.random() * 5)
    val initialPosition = new Vector2(640 + Math.random.toFloat * 640, Math.random.toFloat * 720)
    rand match{
      case 0 => spawnUnit(initialPosition, new Vector2(-1280,0), "art/friendlyProjectile3.png", (ship:Ship) => ship.addAction(Actions.moveTo(640, 360, 0.2f, Interpolation.exp10)))
      case 1 => spawnUnit(initialPosition, new Vector2(-1280,1280 * (ship.getY() - initialPosition.y)/(initialPosition.x- ship.getX())),  "art/blackProjectile.png", (ship:Ship) => end(stage))
      case 2 => {
        val randomVect = new Vector2(1280,0);
        randomVect.setAngle(MathUtils.random() * 360)
        spawnUnit(new Vector2(640, 360), randomVect, "art/purpleProjectile.png", (ship:Ship) => ship.blueModif = 0.2f)
      }
      case 3 => spawnUnit(initialPosition, new Vector2(-1280,0), "art/yellowProjectile.png", (ship:Ship) => bulletSpeed *= 45/50f)
      case 4 => spawnUnit(initialPosition, new Vector2(-1280,0), "art/blueProjectile.png", (ship:Ship) => bulletSpeed *= 50/45f)
    }
  }
  
  
  def spawnUnit(initialPosition: Vector2, target: Vector2, filePath: String, f:Ship => Unit): Bullet = {
    val bullet = new Bullet(f, new Texture(filePath))
    bullet setPosition(initialPosition.x, initialPosition.y)
    bullet setOrigin(20, 20)
    bullet addAction Actions.moveBy(target.x, target.y, bulletSpeed, Interpolation.linear);
    bullet
  }
  
  def updateDelay(variable: Float, delta: Float): Float = {
    if(variable  > 0) variable - delta
    else 0 
  }
  
  def restartGame() = {
    stage.clear()
    stage.addActor(ship)
    stage addActor timer
    timer.setPosition( 10, 680)
    timer.setHeight(20)
    elapsed = 0f
    shootDelay = 0f
    spawnDelay = 0f
    bulletSpeed = 2f
  }

  def render(delta: Float) = {
    elapsed += delta
    shootDelay = updateDelay(shootDelay, delta);
    spawnDelay = updateDelay(spawnDelay, delta);
    val c = getRainbow
    Gdx.gl.glClearColor(1 - c.r * ship.redModif, 1 - c.g * ship.greenModif, 1 - c.b * ship.blueModif , 1)
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT)

    batch.begin()
    
    if(Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) 
      ship addAction(Actions.moveBy(-getSpeed, 0, 0.1f))
    if(Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) 
      ship addAction(Actions.moveBy(getSpeed, 0, 0.1f))
    if(Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)) 
      ship addAction(Actions.moveBy(0, getSpeed, 0.2f))
    if(Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S)) 
      ship addAction(Actions.moveBy(0, -getSpeed, 0.2f))
    if(Gdx.input.isKeyPressed(Keys.SPACE) && shootDelay == 0){
      shootDelay = 0.3f;
      if(pauseGame){
        restartGame
        pauseGame = false;
      }
      if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) accelerated = true else accelerated = false
//      stage addActor(ship.shoot)
    }

    stage.act(delta);
    stage.draw;
    checkScene(stage)
//    waves.draw(batch, stage, elapsed)
    
    batch.end()
  }

  def resize(width: Int, height: Int) = {}
  
  def show() = {
    Gdx.audio.newMusic(Gdx.files.internal("art/16 - Anna.mp3")).play()
    stage addActor ship
    
    stage addActor timer
    timer.setPosition( 10, 680)
    timer.setHeight(20)
  }
  
  def hide() = { dispose }
  def pause() = {}
  def resume() = {}
  def dispose() = { stage.dispose()
    batch.dispose}
}