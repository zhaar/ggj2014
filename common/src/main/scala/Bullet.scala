package us.zhaar.ggj

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.graphics.Texture

class Bullet(transform : Ship => Ship, texture:Texture) extends Image(texture){

  def contains(x: Float, y: Float): Boolean = 
    getX() < x && x < getX() + getWidth() && getY() < y && y < getY() + getWidth()
    
  
}