package us.zhaar.ggj

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.scenes.scene2d.actions.Actions

class Ship(text: Texture) extends Image(text){
  
  var redModif = 1f
  var greenModif = 1f
  var blueModif = 1f
  
  def radius = getWidth()/2
  
    def shoot : Projectile = {
      val proj = new Projectile(this.getProjectileTexture)
      proj setPosition(getX() - getOriginX(), getY() - getOriginY())
      proj addAction(Actions.moveBy(1000, 0, 2))
      proj
    }
    
    def getProjectileTexture : Texture = new Texture("art/projectile2.png")
    
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