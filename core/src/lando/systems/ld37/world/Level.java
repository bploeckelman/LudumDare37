package lando.systems.ld37.world;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Linear;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld37.accessors.RectangleAccessor;
import lando.systems.ld37.gameobjects.Npc;
import lando.systems.ld37.gameobjects.Player;
import lando.systems.ld37.gameobjects.Wall;
import lando.systems.ld37.utils.Assets;
import lando.systems.ld37.utils.Config;

/**
 * Created by Brian on 12/10/2016.
 */
public class Level {

    private static int wallsWide = 16;
    private static int wallMargin = 2;
    private static float clickDistance = 50;

    public Rectangle gameBounds;
    public Vector2 lowerLeft;
    public Vector2 upperRight;
    public float crackTimer = 3;
    public float gameTimer = 60;

    LevelInfo levelInfo;
    Player player;
    TiledMap map;
    TiledMapRenderer mapRenderer;
    Array<Wall> walls;
    Array<Npc> npcs;

    private Vector2 movementVec = new Vector2();
    private Vector3 touchPoint = new Vector3();
    private boolean wallDestroyed;
    private boolean timeUp;
    private boolean inScript;


    public Level(LevelInfo.Stage stage) {
        levelInfo = new LevelInfo(stage);
        player = new Player(levelInfo);
        map = (new TmxMapLoader()).load(levelInfo.mapName);
        mapRenderer = new OrthoCachedTiledMapRenderer(map);
        ((OrthoCachedTiledMapRenderer) mapRenderer).setBlending(true);
        crackTimer = levelInfo.crackTimer;
        npcs = new Array<Npc>();

        buildWalls(levelInfo.crackSpeed);
        wallDestroyed = false;
        timeUp = false;

        inScript = true;
        initializeScript(stage);
    }

    public void update(float dt, OrthographicCamera camera) {
        for (Npc npc : npcs) {
            npc.update(dt);
        }
        if (inScript) {
            // DO SCRIPTED THINGS UNTIL DONE...
            return;
        }

        gameTimer -= dt;
        if (gameTimer < 0) {
            timeUp = true;
        }

        crackTimer -= dt;
        if (crackTimer < 0){
            crackTimer = levelInfo.crackTimer;
            walls.get(MathUtils.random(walls.size -1)).cracking = true;
        }

        for (Wall w : walls){
            w.update(dt);
            if (w.destroyed()){
                wallDestroyed = true;
            }
        }

        touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
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
    }

    public void draw(SpriteBatch batch, OrthographicCamera camera) {
        batch.end();
        {
            // push camera pos
            float prevX = camera.position.x;
            float prevY = camera.position.y;

            // move tiled map to play area
            camera.translate(-lowerLeft.x, -lowerLeft.y);
            camera.update();

            // draw the tiled map
            mapRenderer.setView(camera);
            mapRenderer.render();

            // pop the camera pos
            camera.position.set(prevX, prevY, 0f);
            camera.update();
        }
        batch.begin();

        for (Wall w : walls){
            w.render(batch, player);
        }

        for (Npc n : npcs) {
            n.draw(batch);
        }

        player.render(batch);
    }

    public boolean isWallDestroyed() {
        return wallDestroyed;
    }

    public boolean isTimeUp() {
        return timeUp;
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
                    int type = 0;
                    if (y == wallsHigh -1) type += 1;
                    if (x == 0) type += 2;
                    if (y == 0) type += 4;
                    if (x == wallsWide -1) type += 8;
                    walls.add(new Wall(type, rect, crackSpeed));
                }
            }
        }
    }

    private void movePlayer(float dt){
        movementVec.set(0,0);
        if (Gdx.input.isKeyPressed(Input.Keys.W)) movementVec.add(0, 1);
        if (Gdx.input.isKeyPressed(Input.Keys.S)) movementVec.add(0, -1);
        if (Gdx.input.isKeyPressed(Input.Keys.D)) movementVec.add(1, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.A)) movementVec.add(-1, 0);

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

        player.update(dt);
    }

    private void initializeScript(LevelInfo.Stage stage) {
        if (stage == LevelInfo.Stage.Infancy) {
            // spawn npcs
            final Npc mom = new Npc(
                    "Mom",
                    gameBounds.x + gameBounds.width / 2f,
                    gameBounds.y + gameBounds.height / 2f + 64f,
                    32f, 32f,
                    new TextureRegion(Assets.whiteBox)
            );
            npcs.add(mom);

            // start dialog sequences
            mom.say("Fuck this shit", 10f);

            // trigger movements
            float doorPosY = gameBounds.y + gameBounds.height;
            Timeline.createSequence()
                    .push(
                Tween.to(mom.bounds, RectangleAccessor.Y, 10f)
                        .target(doorPosY)
                        .ease(Linear.INOUT)
                    )
                    .push(
                Tween.call(new TweenCallback() {
                               @Override
                               public void onEvent(int type, BaseTween<?> source) {
                                   npcs.removeValue(mom, true);
                                   inScript = false;
                               }
                           }
                    ))
                    .start(Assets.tween);
        }
        // else if (stage == ...) {}
    }

}