package lando.systems.ld37.screens;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Quint;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld37.utils.Assets;
import lando.systems.ld37.utils.Config;
import lando.systems.ld37.utils.Dialogue;
import lando.systems.ld37.utils.TextHelper;
import lando.systems.ld37.world.GameInfo;
import lando.systems.ld37.world.Level;

/**
 * Created by dsgraham on 12/10/16.
 */
public class GameScreen extends BaseScreen {

    private float runningTime;
    private MutableFloat detailAlpha = new MutableFloat(0f);
    private GameInfo gameInfo;
    private Array<String> messages;
    private Level level;

    public GameScreen(){
        super();
        gameInfo = new GameInfo();
        messages = new Array<String>();
        level = new Level(gameInfo.currentStage);
        runningTime = 0;

        Gdx.input.setInputProcessor(
                new InputMultiplexer(level.dialogue, this)
        );
    }

    @Override
    public void update(float dt) {
        runningTime += dt;

        level.update(dt, camera);
        if (level.isTimeUp() || level.isWallDestroyed()){
            stageCompleted(false);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && detailAlpha.floatValue() == 0f) {
            detailAlpha.setValue(0.05f);
            Tween.to(detailAlpha, -1, 1.0f).target(0f).ease(Quint.OUT).start(Assets.tween);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(Config.bgColor.r, Config.bgColor.g, Config.bgColor.b, Config.bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setShader(Assets.shimmerShader);
        Assets.shimmerShader.begin();
        Assets.shimmerShader.setUniformf("u_time", runningTime);
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        batch.draw(Assets.whitePixel, 0, 0, camera.viewportWidth, camera.viewportHeight);
        batch.end();
        Assets.shimmerShader.end();

        batch.setShader(Assets.featherShader);
        Assets.featherShader.begin();
        Assets.featherShader.setUniformf("u_time", runningTime);
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        batch.draw(Assets.brainOutline, 0, -60f);
        batch.end();
        Assets.featherShader.end();
        batch.setShader(null);

        batch.begin();
        level.draw(batch, camera);

        batch.setColor(1f, 1f, 1f, detailAlpha.floatValue());
        batch.draw(Assets.brainDetail, 0, -60f);
        batch.setColor(1f, 1f, 1f, 1f);

        level.dialogue.render(batch);
        batch.end();
    }

    private void stageCompleted(boolean contracted){
        gameInfo.addStageComplete(gameInfo.currentStage, contracted);
        gameInfo.nextStage();
        level = new Level(gameInfo.currentStage);
        // TODO show things on end, etc.
    }
}
