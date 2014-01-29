package org.dischan.randomBS;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture;

class CustomAssetManager(param: TextureParameter) extends AssetManager{
  
   def buildTexture(path: String):Texture = {
    if(!isLoaded(path)) {
      load(path, classOf[Texture], param);
      finishLoading();
    }
    get(path, classOf[Texture]);
  }
}
