package lando.systems.ld37.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import lando.systems.ld37.utils.Config;
import lando.systems.ld37.LudumDare37;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Config.gameWidth;
		config.height = Config.gameHeight;
		new LwjglApplication(new LudumDare37(), config);
	}
}
