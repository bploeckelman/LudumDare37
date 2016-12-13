package lando.systems.ld37.screens;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld37.LudumDare37;
import lando.systems.ld37.utils.Assets;

/**
 * Created by Brian on 12/12/2016.
 */
public class TitleScreen extends BaseScreen {

    String clickText;
    MutableFloat alpha;
    Color color;

    public TitleScreen() {
        super();
        clickText = "click to begin";
        alpha = new MutableFloat(0.1f);
        color = new Color(1f, 1f, 1f, alpha.floatValue());

        Tween.to(alpha, -1, 0.5f)
                .target(1f)
                .repeatYoyo(-1, 0f)
                .start(Assets.tween);
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            LudumDare37.game.setScreen(new GameScreen());
        }

        color.set(1f, 1f, 1f, alpha.floatValue());
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(Assets.titleTexture, 0, 0, camera.viewportWidth, camera.viewportHeight);

        Assets.font8pt.getData().setScale(1.75f);
        Assets.layout.setText(Assets.font8pt, clickText);
        float x = 200f - Assets.layout.width / 2f;
        float y = camera.viewportHeight - 60f;
        batch.setColor(0f, 0f, 0f, 0.6f);
        batch.draw(Assets.whitePixel, x - 20, y - Assets.layout.height - 10, Assets.layout.width + 40, Assets.layout.height + 20);
        batch.setColor(Color.WHITE);

        Assets.font8pt.setColor(color);
        Assets.font8pt.draw(batch, clickText, x, y);
        Assets.font8pt.setColor(Color.WHITE);
        Assets.font8pt.getData().setScale(1f);
        batch.end();
    }

}
