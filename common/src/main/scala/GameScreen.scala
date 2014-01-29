package us.zhaar.ggj

import org.dischan.randomBS.CustomAssetManager
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.Screen
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL10
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.scenes.scene2d.utils.Align

class GameScreen(game: Azurey) extends Screen {
  val batch = new SpriteBatch
      val stage = new Stage
      val maxProjectiles = 4
      val maxTime = 60;
  var moveDelta = 8f
      val manager = new CustomAssetManager(getParameters)
  /*
   * lol variables
   */
  var elapsed = 0f
  var shootDelay = 0f
  var spawnDelay = 0f
  var bulletSpeed = 2f
  var pauseGame = false
  var accelerated = false
  var level = 0
  def currentLevel = MathUtils.floor(elapsed/10)

  def getParameters()= {
    val param = new TextureParameter();
    param.minFilter = TextureFilter.Linear;
    param.genMipMaps = true;
    param
  }

  val timer = new Label("", new LabelStyle(font, Color.WHITE))
  lazy val font = new BitmapFont(
      Gdx.files.internal("ui/gameFont.fnt"),
      new TextureRegion(manager.buildTexture("ui/gameFont.png")), false);
  lazy val ship = new Ship(manager.buildTexture("art/ship3.png"))

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
                if(actor.getActions().size == 0 ) actor.addAction(Actions.removeActor())}
              case s: Ship => checkBounds(s);
              case b: Bullet => {
                if(b.contains(ship.getX(), ship.getY()) && !pauseGame) b.affectShip(ship)
                b.setScale(1+getWave(1, 2.1f, -.5f, elapsed))
                if(b.getActions().size == 0 && !pauseGame) b.addAction(Actions.removeActor())
              }
              case _ => Nil
          }
        }
        if(!pauseGame)timer setText((((elapsed * 100 ).toInt)/100f).toString)
        if(level != currentLevel){
          level = currentLevel
          nextLevel(level)
        }

        scene
    }

    def nextLevel(level: Int){
      val array = new Array(level)
      bulletSpeed = bulletSpeed *4/5f
      Gdx.audio.newSound(Gdx.files.internal("art/sound/Next_level2.wav")).play()

      def selectArrow():String = {
          val rand = Math.floor(Math.random() * 7)
              rand match{
              case 0 => "arrBlue.png"
              case 1 => "arrOrange.png"
              case 2 => "arrYellow.png"
              case 3 => "arrLime.png"
              case 4 => "arrLightBlue.png"
              case 5 => "arrPurple.png"
              case 6 => "arrGreen.png"
          }
      }

      for(i <- 0 until level){
        val arrow = new Image(manager.buildTexture("art/" + selectArrow))
        arrow.setPosition(1280, 0)
        arrow.addAction(Actions.sequence(Actions.delay(i*0.3f, Actions.moveBy(-1500, 0,bulletSpeed)), Actions.removeActor()))
        stage.addActor(arrow)
      }
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
      pauseGame = true;
      Gdx.audio.newSound(Gdx.files.internal("art/sound/Done2.wav")).play()

      val actors = stage.getActors();
      for(a <- 0 until actors.size){
        actors.get(a).clearActions()
      }
      val score = elapsed / bulletSpeed
          val scoreLabel = new Label("time :\n " +elapsed + "\n press space to restart", new LabelStyle(font, Color.BLACK))
      scoreLabel.setAlignment(Align.center)
      scoreLabel.setPosition(1280/2 - scoreLabel.getWidth()/2, 720/2 - scoreLabel.getHeight()/2)
      stage addActor(scoreLabel)
    }

    def spawnBullet: Bullet = {
        val rand = Math.floor(Math.random() * 5)
            val initialPosition = new Vector2(640 + Math.random.toFloat * 640, Math.random.toFloat * 720)
        rand match{
        case 0 => spawnUnit(initialPosition, new Vector2(-1280,0), "art/friendlyProjectile3.png", (ship:Ship) => {
          Gdx.audio.newSound(Gdx.files.internal("art/sound/Teleport2.wav")).play
          ship.addAction(Actions.moveTo(640, 360, 0.2f, Interpolation.exp10))
          ship.blueModif = 1f
          ship.greenModif = 0.8f
          ship.redModif = 0.1f
        })
        case 1 => spawnUnit(initialPosition, new Vector2(-1280,1280 * (ship.getY() - initialPosition.y)/(initialPosition.x- ship.getX())),  "art/blackProjectile.png", (ship:Ship) => {
          ship.blueModif *= 0.1f
          ship.greenModif *= 0.1f
          ship.redModif *= 0.1f
          end(stage)
        })
        case 2 => {
          val randomVect = new Vector2(1280,0);
          randomVect.setAngle(MathUtils.random() * 360)
          spawnUnit(new Vector2(640, 360), randomVect, "art/purpleProjectile.png", (ship:Ship) => { 
            ship.blueModif = 0.2f
            spawnMultipleBullets((level+2)^2);
          })}
        case 3 => spawnUnit(initialPosition, new Vector2(-1280,0), "art/yellowProjectile.png", (ship:Ship) =>{
          ship.blueModif = 1f
          ship.greenModif = 0.5f
          ship.redModif = 0.5f
          Gdx.audio.newSound(Gdx.files.internal("art/sound/Speed_down2.wav")).play()
          ship.decreseSpeed
        })
        case 4 => spawnUnit(initialPosition, new Vector2(-1280,0), "art/blueProjectile.png", (ship:Ship) => {
          Gdx.audio.newSound(Gdx.files.internal("art/sound/Speed_up2.wav")).play()
          ship.increaseSpeed
        })
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

      /*
       * lol fuck my life
       */
      def restartGame() = {
          Gdx.audio.newSound(Gdx.files.internal("art/sound/Laser_2.wav")).play()

          stage.clear()
          stage.addActor(ship)
          stage addActor timer
          timer.setPosition( 10, 680)
          timer.setHeight(20)
          elapsed = 0f
          shootDelay = 0f
          spawnDelay = 0f
          bulletSpeed = 2f
          level = 0;
          ship.resetSpeed
      }

      def render(delta: Float) = {
          if(!pauseGame)elapsed += delta
              shootDelay = updateDelay(shootDelay, delta);
          spawnDelay = updateDelay(spawnDelay, delta);
          val c = getRainbow
              Gdx.gl.glClearColor(1 - c.r * ship.redModif, 1 - c.g * ship.greenModif, 1 - c.b * ship.blueModif , 1)
              Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT)

              batch.begin()
              if(!pauseGame){
                if(Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) 
                  ship addAction(Actions.moveBy(-ship.speed, 0, 0.1f))
                if(Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) 
                  ship addAction(Actions.moveBy(ship.speed, 0, 0.1f))
                if(Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)) 
                  ship addAction(Actions.moveBy(0, ship.speed, 0.2f))
                if(Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S)) 
                  ship addAction(Actions.moveBy(0, -ship.speed, 0.2f))
              }
          if(Gdx.input.isKeyPressed(Keys.SPACE)){
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
          val mainMusic = Gdx.audio.newMusic(Gdx.files.internal("art/16 - Anna.mp3"))
              mainMusic.setVolume(0.3f)
              mainMusic.setLooping(true)
              mainMusic.play()
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