package lando.systems.ld37;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld37.screens.BaseScreen;
import lando.systems.ld37.screens.GameScreen;

public class LudumDare37 extends ApplicationAdapter {
	SpriteBatch batch;
	BaseScreen screen;
	public static LudumDare37 game;


	@Override
	public void create () {
		game = this;
		batch = new SpriteBatch();
		setScreen(new GameScreen());
	}

	@Override
	public void render () {
		float dt = Math.min(Gdx.graphics.getDeltaTime(), 1f / 30f);

		screen.update(dt);

		screen.render(batch);


	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}

	public void setScreen(BaseScreen newScreen){
		screen = newScreen;
	}
}
