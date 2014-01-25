package us.zhaar.ggj

import com.badlogic.gdx.Game



class Azurey extends Game {

	override def create() {
		Azurey.this.setScreen(new GameScreen(this))
	}
}
