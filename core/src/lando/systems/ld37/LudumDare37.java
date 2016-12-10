package lando.systems.ld37;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld37.screens.BaseScreen;
import lando.systems.ld37.screens.GameScreen;
import lando.systems.ld37.utils.Assets;
import lando.systems.ld37.world.LevelInfo;

public class LudumDare37 extends ApplicationAdapter {

	public static LudumDare37 game;

	SpriteBatch batch;
	BaseScreen screen;

	@Override
	public void create () {
	    Assets.load();
	    float progress = 0f;
	    do {
	    	progress = Assets.update();
		} while (progress != 1f);
		game = this;

		batch = Assets.batch;
        setScreen(new GameScreen(LevelInfo.Stage.Infancy));
	}

	@Override
	public void render () {
		float dt = Math.min(Gdx.graphics.getDeltaTime(), 1f / 30f);
		screen.update(dt);
		screen.render(batch);
	}
	
	@Override
	public void dispose () {
		Assets.dispose();
	}

	public void setScreen(BaseScreen newScreen){
		screen = newScreen;
	}

}
