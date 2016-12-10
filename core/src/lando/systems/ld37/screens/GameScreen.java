package lando.systems.ld37.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld37.gameobjects.Wall;
import lando.systems.ld37.utils.Assets;
import lando.systems.ld37.utils.Config;

/**
 * Created by dsgraham on 12/10/16.
 */
public class GameScreen extends BaseScreen {

    public Texture debugTex;
    Array<Wall> walls;
    private static int wallsWide = 15;
    private static int wallMargin = 2;
    private boolean showDetail = false;

    public GameScreen(){
        super();
        buildWalls();
        debugTex = Assets.whitePixel;
    }

    @Override
    public void update(float dt) {
        for (Wall w : walls){
            w.update(dt);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            showDetail = !showDetail;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(Config.bgColor.r, Config.bgColor.g, Config.bgColor.b, Config.bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(Assets.brainOutline, 0, -60f);
        if (showDetail) {
            batch.draw(Assets.brainDetail, 0, -60f);
        }
        for (Wall w : walls){
            w.render(batch);
        }
        batch.end();
    }

    private void buildWalls(){
        walls = new Array<Wall>();
        float wallWidth = Config.gameWidth / (wallsWide + (2 * wallMargin));
        int wallsHigh = ((int)(Config.gameHeight / wallWidth)) - (2 * wallMargin);
        for (int y = 0; y < wallsHigh; y++){
            for (int x = 0; x < wallsWide; x++){
                if (x == 0 || x == wallsWide -1 || y == 0 || y == wallsHigh -1) {
                    Rectangle rect = new Rectangle((x + wallMargin) * wallWidth, (y + wallMargin) * wallWidth, wallWidth, wallWidth);
                    walls.add(new Wall(rect, 2));
                }
            }
        }
    }
}
