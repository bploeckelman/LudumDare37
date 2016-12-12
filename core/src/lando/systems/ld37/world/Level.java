package lando.systems.ld37.world;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quint;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld37.accessors.RectangleAccessor;
import lando.systems.ld37.gameobjects.*;
import lando.systems.ld37.utils.Assets;
import lando.systems.ld37.utils.Config;
import lando.systems.ld37.utils.Dialogue;

/**
 * Created by Brian on 12/10/2016.
 */
public class Level {

    private static int wallsWide = 16;
    private static int wallMargin = 2;
    public static float clickDistance = 70;

    public Rectangle gameBounds;
    public Vector2 lowerLeft;
    public Vector2 upperRight;
    public float crackTimer = .5f;
    public float gameTimer = 30;
    public Dialogue dialogue;

    LevelInfo levelInfo;
    LevelInfo.Stage currentStage;
    Player player;
    TiledMap map;
    TiledMapRenderer mapRenderer;
    Array<Wall> walls;
    Array<Npc> npcs;

    private Rectangle dialogueRect = new Rectangle();
    private Vector2 movementVec = new Vector2();
    private Vector3 touchPoint = new Vector3();
    private int scriptSegment;
    private boolean levelCompleted;
    private boolean levelStarted;
    private boolean inScript;
    private boolean scriptReady;

    private Array<KeyItem> keyItems;
    private Array<GameObject> gameObjects;
    private GameInfo gameInfo;
    private MutableFloat overlayAlpha;


    public Level(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
        LevelInfo.Stage stage = gameInfo.currentStage;
        levelInfo = new LevelInfo(stage);
        currentStage = stage;
        player = new Player(levelInfo);
        map = (new TmxMapLoader()).load(levelInfo.mapName);
        mapRenderer = new OrthoCachedTiledMapRenderer(map);
        ((OrthoCachedTiledMapRenderer) mapRenderer).setBlending(true);
        crackTimer = .5f;
        npcs = new Array<Npc>();

        buildWalls(levelInfo.crackSpeed);

        keyItems = new Array<KeyItem>();
        for (LevelInfo.Stage s :gameInfo.neurosis.keys()){
            if (gameInfo.neurosis.get(s)){
                keyItems.add(new KeyItem(s, false, null));
            }
        }

        loadMapObjects();

        dialogue = new Dialogue();
        dialogueRect = new Rectangle(10, (int) (3f / 4f * Config.gameHeight) - 10, Config.gameWidth - 20, Config.gameHeight / 4);
        initializeLevel();
    }

    public void update(float dt, OrthographicCamera camera) {
        if (!levelStarted) return;
        updateScript(dt);
        dialogue.update(dt);

        for (KeyItem k : keyItems){
            k.update(dt);
        }

        for (Npc npc : npcs) {
            npc.update(dt);
        }

        if (inScript) {
            return;
        }

        gameTimer -= dt;
        if (gameTimer < 0) {
            finishLevel(false);
        }

        crackTimer -= dt;
        if (crackTimer < 0){
            crackTimer = levelInfo.crackTimer;
            walls.get(MathUtils.random(walls.size -1)).cracking = true;
        }

        for (Wall w : walls){
            w.update(dt);
            if (w.destroyed()){
                finishLevel(true);
            }
        }

        touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPoint);
        for (Wall w: walls){
            if (w.bounds.contains(touchPoint.x, touchPoint.y)){
                if (player.center.dst(w.center) < clickDistance) {
                    w.hovered = true;
                    if (Gdx.input.justTouched()){
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

        batch.draw(Assets.vignette, gameBounds.x, gameBounds.y, gameBounds.width, gameBounds.height);

        for (Wall w : walls){
            w.renderOutline(batch);
        }

        player.render(batch);

        for (GameObject obj : gameObjects) {
            obj.render(batch);
        }

        Assets.particleManager.render(batch);

        for (KeyItem k : keyItems){
            k.render(batch);
        }

        for (Npc n : npcs) {
            n.draw(batch);
        }

        batch.draw(Assets.clockFace, camera.viewportWidth - 60, 10, 50, 50);
        batch.setColor(Color.RED);
        batch.draw(Assets.whitePixel, camera.viewportWidth - 36, 35, 1, 0, 2, 17, 1, 1, gameTimer * 6f);
        batch.setColor(Color.WHITE);

        batch.setColor(0,0,0,overlayAlpha.floatValue());
        batch.draw(Assets.whitePixel, 0, 0, camera.viewportWidth, camera.viewportHeight);
        batch.setColor(Color.WHITE);
    }

    public boolean isLevelComplete(){
        return levelCompleted;
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
        for (KeyItem k : keyItems){
            movementWithRect(movementVec, k.bounds);
        }
        for (GameObject obj : gameObjects) {
            movementWithRect(movementVec, obj.bounds);
        }

        player.pos.add(movementVec);

        player.update(dt);
    }

    Vector2 tmpUL = new Vector2();
    Vector2 tmpUR = new Vector2();
    Vector2 tmpLL = new Vector2();
    Vector2 tmpLR = new Vector2();
    Vector2 tmpInt = new Vector2();
    Vector2 nor = new Vector2();
    Vector2 undesired = new Vector2();
    private void movementWithRect(Vector2 mVec, Rectangle bounds){
        if (mVec.epsilonEquals(0,0,.1f)) return; // Don't waste time when movement is 0
        tmpUL.set(player.pos.x, player.pos.y + player.width);
        tmpUR.set(player.pos.x + player.width, player.pos.y + player.width);
        tmpLL.set(player.pos.x, player.pos.y);
        tmpLR.set(player.pos.x + player.width, player.pos.y);

        // up
        if (mVec.y > 0) {
            if (Intersector.intersectSegments(tmpUL.x, tmpUL.y, tmpUL.x + mVec.x, tmpUL.y + mVec.y,
                    bounds.x, bounds.y, bounds.x + bounds.width, bounds.y, tmpInt) ||
                    Intersector.intersectSegments(tmpUR.x, tmpUR.y, tmpUR.x + mVec.x, tmpUR.y + mVec.y,
                            bounds.x, bounds.y, bounds.x + bounds.width, bounds.y, tmpInt)) {
                nor.set(0, -1);
                float dot = nor.dot(mVec);
                undesired.set(nor.scl(dot));
                mVec.sub(undesired);
            }
        }

        // down
        if (mVec.y < 0) {
            if (Intersector.intersectSegments(tmpLL.x, tmpLL.y, tmpLL.x + mVec.x, tmpLL.y + mVec.y,
                    bounds.x, bounds.y + bounds.height, bounds.x + bounds.width, bounds.y + bounds.height, tmpInt) ||
                    Intersector.intersectSegments(tmpLR.x, tmpLR.y, tmpLR.x + mVec.x, tmpLR.y + mVec.y,
                            bounds.x, bounds.y + bounds.height, bounds.x + bounds.width, bounds.y + bounds.height, tmpInt)) {
                nor.set(0, 1);
                float dot = nor.dot(mVec);
                undesired.set(nor.scl(dot));
                mVec.sub(undesired);
            }
        }

        // right
        if (mVec.x > 0) {
            if (Intersector.intersectSegments(tmpUR.x, tmpUR.y, tmpUR.x + mVec.x, tmpUR.y + mVec.y,
                    bounds.x, bounds.y, bounds.x, bounds.y + bounds.height, tmpInt) ||
                    Intersector.intersectSegments(tmpLR.x, tmpLR.y, tmpLR.x + mVec.x, tmpLR.y + mVec.y,
                            bounds.x, bounds.y, bounds.x, bounds.y + bounds.height, tmpInt)) {
                nor.set(-1, 0);
                float dot = nor.dot(mVec);
                undesired.set(nor.scl(dot));
                mVec.sub(undesired);
            }
        }

        // left
        if (mVec.x < 0) {
            if (Intersector.intersectSegments(tmpUL.x, tmpUL.y, tmpUL.x + mVec.x, tmpUL.y + mVec.y,
                    bounds.x + bounds.width, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, tmpInt) ||
                    Intersector.intersectSegments(tmpLL.x, tmpLL.y, tmpLL.x + mVec.x, tmpLL.y + mVec.y,
                            bounds.x + bounds.width, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, tmpInt)) {
                nor.set(1, 0);
                float dot = nor.dot(mVec);
                undesired.set(nor.scl(dot));
                mVec.sub(undesired);
            }
        }

    }

    private void initializeScript() {
        inScript = true;
        scriptReady = false;
        scriptSegment = 0;

        switch (currentStage) {
            case Infancy: {
                final Npc mom = new Npc(
                        "Mom",
                        gameBounds.x + gameBounds.width / 2f,
                        gameBounds.y + gameBounds.height / 2f + 64f,
                        32f, 32f,
                        new TextureRegion(Assets.whiteBox)
                );
                npcs.add(mom);

                float duration = 4f;
                mom.say("$%#@ $# )#*@#", duration);

                float doorPosY = gameBounds.y + gameBounds.height - mom.bounds.height;
                Timeline.createSequence()
                        .beginParallel()
                        .push(Tween.to(mom.bounds, RectangleAccessor.Y, duration)
                                .target(doorPosY)
                                .ease(Linear.INOUT))
                        .push(Tween.to(mom.alpha, 1, 2)
                                .target(0)
                                .delay(duration - 2))
                        .end()
                        .push(Tween.call(new TweenCallback() {
                            @Override
                            public void onEvent(int type, BaseTween<?> source) {
                                npcs.removeValue(mom, true);
                                scriptReady = true;
                            }
                        }))
                        .start(Assets.tween);
            }
            break;
//        case FOO: {}
            default: {
                inScript = false;
            }
        }
    }

    private void updateScript(float dt) {
        switch (currentStage) {
            case Infancy:
            {
                switch (scriptSegment) {
                    case 0:
                    {
                        if (scriptReady) {
                            scriptSegment++;
                            showDialogue("Record scratch... freeze frame...",
                                         "I bet you're wondering how I got here.");
                        }
                    }
                    break;
                    case 1:{
                        if (!dialogue.isActive()){
                            inScript = false;
                        }
                        for (Wall w : walls){
                            if (w.cracking) {
                                w.tutorialWall = true;
                                inScript = true;
                                showDialogue("There is a crack forming in your walls.",
                                            "Move(WASD) next to it and click on it to repair it.");
                                scriptSegment++;
                            }
                        }
                    }
                    break;
                    case 2:
                        if (!dialogue.isActive()){
                            inScript = false;
                            scriptSegment++;
                        }
                        break;

                    // case N: {} break;
                }
            }
            break;
            // case FOO: {} break;
        }
    }

    private void showDialogue(String... messages) {
        dialogue.show((int) dialogueRect.x, (int) dialogueRect.y, (int) dialogueRect.width, (int) dialogueRect.height, messages);
    }

    private void initializeLevel(){
        levelStarted = false;
        overlayAlpha = new MutableFloat(1);
        Tween.to(overlayAlpha, 1, 2)
                .target(0)
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        initializeScript();
                        levelStarted = true;
                    }
                })
                .start(Assets.tween);

    }

    private void finishLevel(boolean contracted){
        gameInfo.addStageComplete(gameInfo.currentStage, contracted);
        inScript = true;
        KeyItem keyItem = null;
        for (KeyItem k : keyItems){
            if (k.active) keyItem = k;
        }
        if (keyItem == null) return; // Shouldn't happen
        keyItem.sparkle = true;
        Tween outcomeTween;
        if (contracted){
            Rectangle b = keyItem.getInactiveBounds();
            outcomeTween = Tween.to(keyItem.bounds, RectangleAccessor.XYWH, 3)
                    .target(b.x, b.y, b.width, b.height)
                    .waypoint(Config.gameWidth / 2, 350, 50, 50);
        }   else {
            Rectangle b = keyItem.bounds;
            outcomeTween = Tween.to(keyItem.bounds, RectangleAccessor.XYWH, 2)
                    .target(b.x + b.width/2,b.y + b.height/2, 0, 0)
                    .ease(Elastic.IN);
        }
        gameInfo.gameScreen.detailAlpha.setValue(0.05f * keyItems.size);

        Timeline.createSequence()
                .push(Tween.to(gameInfo.gameScreen.detailAlpha, -1, 1.0f).target(0f).ease(Quint.OUT))
                .push(outcomeTween)
                .push(Tween.to(overlayAlpha, 1, 2)
                           .target(1))
                .push(Tween.call(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        levelCompleted = true;
                        Assets.particleManager.clearParticles();
                    }
                })).start(Assets.tween);
    }

    private void loadMapObjects() {
        if (map == null) return;
        if (gameObjects == null) {
            gameObjects = new Array<GameObject>();
        }

        MapLayer objectLayer = map.getLayers().get("objects");
        for (MapObject object : objectLayer.getObjects()) {
            MapProperties props = object.getProperties();
            // Shift x,y by map position
            float x = (Float) props.get("x") + lowerLeft.x;
            float y = (Float) props.get("y") + lowerLeft.y;
            float w = (Float) props.get("width");
            float h = (Float) props.get("height");
            String type = (String) props.get("type");
            String name = object.getName();
            Rectangle bounds = new Rectangle(x, y, w, h);

            // Instantiate based on type
            if (type.equals("keyitem")) {
                keyItems.add(new KeyItem(currentStage, true, bounds));
            } else if (type.equals("gameobject")) {
                gameObjects.add(new GameObject(name, bounds));
            }
//            else if (type.equals("...")) {
//
//            }
        }
    }

}
