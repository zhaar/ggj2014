package us.zhaar.ggj

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils

class Bullet(transform : Ship => Unit, texture:Texture) extends Image(texture){
  
  def affectShip(ship: Ship): Unit = transform(ship)
  
  def contains(x: Float, y: Float): Boolean = {
    val yDiff = y - this.getY
    val xDiff = x - getX
    val distance = Math.sqrt(xDiff * xDiff + yDiff * yDiff).toFloat
    distance < 20*getScaleX()/2
  }
    
  
}