package us.zhaar.ggj

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.graphics.Texture

class Wall(texture:Texture, projectiles : Array[Projectile]) extends Image(texture){
  
  def contains(x: Float, y: Float): Boolean = 
    getX() < x && x < getX() + getWidth() && getY() < y && y < getY() + getWidth()
    
  
}