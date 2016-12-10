package lando.systems.ld37.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld37.gameobjects.Player;
import lando.systems.ld37.gameobjects.Wall;
import lando.systems.ld37.utils.Assets;
import lando.systems.ld37.utils.Config;
import lando.systems.ld37.world.LevelInfo;

/**
 * Created by dsgraham on 12/10/16.
 */
public class GameScreen extends BaseScreen {

    private static int wallsWide = 16;
    private static int wallMargin = 2;
    private boolean showDetail = false;
    private LevelInfo.Stage stage;

    public Rectangle gameBounds;
    public Vector2 lowerLeft;
    public Vector2 upperRight;

    public float gameTimer;
    public LevelInfo levelInfo;
    public Texture debugTex;
    Array<Wall> walls;
    float crackTimer = 3;
    Vector2 movementVec;
    Vector2 tempVec2;
    Rectangle tempRec;
    Player player;

    public GameScreen(LevelInfo.Stage stage){
        super();
        levelInfo = new LevelInfo(stage);
        buildWalls(levelInfo.crackSpeed);
        debugTex = Assets.whitePixel;
        gameTimer = 60;
        movementVec = new Vector2();
        tempVec2 = new Vector2();
        tempRec = new Rectangle();
        player = new Player();
    }

    @Override
    public void update(float dt) {
        gameTimer -= dt;
        if (gameTimer < 0){
            // completed
        }

        crackTimer -= dt;
        if (crackTimer < 0){
            crackTimer = levelInfo.crackTimer;
            walls.get(MathUtils.random(walls.size -1)).cracking = true;
        }
        for (Wall w : walls){
            w.update(dt);
            if (w.destroyed()){
                // you lost this stage
            }
        }

        movePlayer(dt);
        player.update(dt);

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
//        batch.draw(Assets.whiteBox, gameBounds.x, gameBounds.y, gameBounds.width, gameBounds.height);
        for (Wall w : walls){
            w.render(batch);
        }

        player.render(batch);
        batch.end();
    }

    private void movePlayer(float dt){
        movementVec.set(0,0);
        if (Gdx.input.isKeyPressed(Input.Keys.W)){
            movementVec.add(0, 1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)){
            movementVec.add(0, -1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)){
            movementVec.add(1, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            movementVec.add(-1, 0);
        }

        movementVec.scl(levelInfo.playerSpeed * dt);
        if (player.pos.y + player.width + movementVec.y > upperRight.y){
            movementVec.y = upperRight.y - (player.pos.y + player.width);
        }

        if (player.pos.x + player.width + movementVec.x > upperRight.x){
            movementVec.x = upperRight.x - (player.pos.x + player.width);
        }

        if (player.pos.y + movementVec.y < lowerLeft.y){
            movementVec.y = lowerLeft.y - player.pos.y;
        }

        if (player.pos.x + movementVec.x < lowerLeft.x){
            movementVec.x = lowerLeft.x - player.pos.x;
        }

        if (!movementVec.epsilonEquals(0,0,.1f)) {
            player.wall = null;
            player.pos.add(movementVec);
        }

    }

    private void buildWalls(float crackSpeed){
        walls = new Array<Wall>();

        float wallWidth = Config.gameWidth / (wallsWide + (2 * wallMargin));
        int wallsHigh = ((int)(Config.gameHeight / wallWidth)) - (2 * wallMargin);
        gameBounds = new Rectangle((wallMargin +1) * wallWidth, (wallMargin+1) * wallWidth,
                (wallsWide-2)*wallWidth, (wallsHigh-2)*wallWidth);
        lowerLeft = new Vector2(gameBounds.x, gameBounds.y);
        upperRight = new Vector2(gameBounds.x + gameBounds.width, gameBounds.y + gameBounds.height);
        for (int y = 0; y < wallsHigh; y++){
            for (int x = 0; x < wallsWide; x++){
                if (x == 0 || x == wallsWide -1 || y == 0 || y == wallsHigh -1) {
                    Rectangle rect = new Rectangle((x + wallMargin) * wallWidth, (y + wallMargin) * wallWidth, wallWidth, wallWidth);
                    walls.add(new Wall(rect, crackSpeed));
                }
            }
        }
    }
}
