package us.zhaar.ggj

import com.badlogic.gdx.backends.lwjgl._

object Main extends App {
    val cfg = new LwjglApplicationConfiguration
    cfg.title = "abstractSideScroller"
    cfg.height = 720
    cfg.width = 1280
    cfg.useGL20 = true
    cfg.forceExit = false
    new LwjglApplication(new Azurey, cfg)
}
