package lando.systems.ld37.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld37.gameobjects.Player;
import lando.systems.ld37.gameobjects.Wall;
import lando.systems.ld37.utils.Assets;
import lando.systems.ld37.utils.Config;
import lando.systems.ld37.world.GameInfo;
import lando.systems.ld37.world.LevelInfo;

/**
 * Created by dsgraham on 12/10/16.
 */
public class GameScreen extends BaseScreen {

    private static int wallsWide = 16;
    private static int wallMargin = 2;
    private static float clickDistance = 50;

    private float runningTime;
    private boolean showDetail = false;
    private GameInfo gameInfo;

    public Rectangle gameBounds;
    public Vector2 lowerLeft;
    public Vector2 upperRight;

    public float gameTimer;
    public LevelInfo levelInfo;
    public TiledMap map;
    TiledMapRenderer mapRenderer;
    Array<Wall> walls;
    float crackTimer = 3;
    Vector2 movementVec;
    Vector2 tempVec2;
    Rectangle tempRec;
    Player player;

    public GameScreen(){
        super();
        gameInfo = new GameInfo();
        startLevel();
        runningTime = 0;
    }

    @Override
    public void update(float dt) {
        runningTime += dt;

        gameTimer -= dt;
        if (gameTimer < 0){
            stageCompleted(false);
        }

        crackTimer -= dt;
        if (crackTimer < 0){
            crackTimer = levelInfo.crackTimer;
            walls.get(MathUtils.random(walls.size -1)).cracking = true;
        }
        for (Wall w : walls){
            w.update(dt);
            if (w.destroyed()){
                stageCompleted(true);
            }
        }

        Vector3 touchPoint = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPoint);
        if (player.center.dst(touchPoint.x, touchPoint.y) < clickDistance){
            for (Wall w : walls){
                if (w.bounds.contains(touchPoint.x, touchPoint.y)){
                    w.hovered = true;
                    if (Gdx.input.justTouched()) {
                        player.wall = w;
                    }
                    break;
                }
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

        batch.setShader(Assets.shimmerShader);
        Assets.shimmerShader.begin();
        Assets.shimmerShader.setUniformf("u_time", runningTime);
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        batch.draw(Assets.whitePixel, 0, 0, camera.viewportWidth, camera.viewportHeight);
        batch.end();
        Assets.shimmerShader.end();
        batch.setShader(null);

        batch.begin();
        batch.draw(Assets.brainOutline, 0, -60f);
        if (showDetail) {
            batch.draw(Assets.brainDetail, 0, -60f);
        }

        batch.end();
        float prevX = camera.position.x;
        float prevY = camera.position.y;
        camera.translate(-lowerLeft.x, -lowerLeft.y);
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();
        camera.position.set(prevX, prevY, 0f);
        camera.update();
        batch.begin();

        for (Wall w : walls){
            w.render(batch, player);
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

    private void startLevel(){
        levelInfo = new LevelInfo(gameInfo.currentStage);
        buildWalls(levelInfo.crackSpeed);
        map = (new TmxMapLoader()).load(levelInfo.mapName);
        mapRenderer = new OrthoCachedTiledMapRenderer(map);
        ((OrthoCachedTiledMapRenderer) mapRenderer).setBlending(true);
        gameTimer = 60;
        movementVec = new Vector2();
        tempVec2 = new Vector2();
        tempRec = new Rectangle();
        player = new Player(levelInfo);
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

    private void stageCompleted(boolean contracted){
        gameInfo.addStageComplete(gameInfo.currentStage, contracted);

        gameInfo.nextStage();
        startLevel();
        // TODO show things on end, etc.
    }
}
