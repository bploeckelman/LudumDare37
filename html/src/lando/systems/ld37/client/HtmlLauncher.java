package lando.systems.ld37.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import lando.systems.ld37.utils.Config;
import lando.systems.ld37.LudumDare37;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(Config.GameWidth, Config.GameHeight);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new LudumDare37();
        }
}