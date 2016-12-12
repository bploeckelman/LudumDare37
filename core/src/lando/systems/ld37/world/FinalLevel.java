package lando.systems.ld37.world;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quint;
import aurelienribon.tweenengine.primitives.MutableFloat;
import aurelienribon.tweenengine.primitives.MutableInteger;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import lando.systems.ld37.accessors.ColorAccessor;
import lando.systems.ld37.accessors.RectangleAccessor;
import lando.systems.ld37.gameobjects.GameObject;
import lando.systems.ld37.gameobjects.KeyItem;
import lando.systems.ld37.gameobjects.Player;
import lando.systems.ld37.utils.Assets;
import lando.systems.ld37.utils.Config;

/**
 * Created by dsgraham on 12/12/16.
 */
public class FinalLevel extends BaseLevel {
    Rectangle brainRect;
    MutableFloat brainAlpha;
    MutableFloat playerAlpha;
    MutableFloat momAlpha;
    int scriptSegment;
    boolean scriptRunning;
    Color overlayColor;
    Color backgroundColor;
    OrthographicCamera camera;
    Player player;
    float accum;
    Vector2 floatOffset;
    TextureRegion keyItemTex;
    MutableFloat keyItemAlpha;
    MutableFloat keyItemAngle;
    MutableInteger currentMapIndex;
    Rectangle keyItemRect;

    public FinalLevel(GameInfo gameInfo) {
        super(gameInfo);
        brainRect = new Rectangle(0, -60, Assets.brainDetail.getWidth(), Assets.brainDetail.getHeight());
        scriptSegment = 0;
        brainAlpha = new MutableFloat(0);
        playerAlpha = new MutableFloat(0);
        momAlpha = new MutableFloat(0);
        scriptRunning = false;
        backgroundColor = new Color(0,0,0,1);
        overlayColor = new Color(1,1,1,0);
        player = new Player(new LevelInfo(LevelInfo.Stage.Death));
        player.facing = 2;
        player.pos.x = Config.gameWidth/2 - player.width/2;
        player.pos.y = Config.gameHeight/2 - 10;
        keyItemAlpha = new MutableFloat(0);
        accum = 0;
        floatOffset = new Vector2();
        keyItemAngle = new MutableFloat(5f);
        Tween.to(keyItemAngle, -1, MathUtils.random(1f, 1.5f))
                .target(-1f * keyItemAngle.floatValue())
                .repeatYoyo(-1, 0f)
                .start(Assets.tween);
        setKeyItem(LevelInfo.Stage.Infancy);
        setStrings();
        loadMaps();
        currentMapIndex = new MutableInteger(-1);
    }

    public void update(float dt, OrthographicCamera camera) {
        runScript();
        dialogue.update(dt);

        accum += dt;
        floatOffset.set(MathUtils.cos(accum * 2f) * 1.5f,
                MathUtils.sin(accum * 5f) * 2.0f);

        if (keyItemAlpha.floatValue() > .5f){
            Assets.particleManager.addSparkles(keyItemRect.x + keyItemRect.width/2, keyItemRect.y + keyItemRect.height/2);
        }
    }

    public void draw(SpriteBatch batch, OrthographicCamera camera) {
        this.camera = camera;
        batch.setColor(backgroundColor);
        batch.draw(Assets.whitePixel, 0, 0, camera.viewportWidth, camera.viewportHeight);

        if (currentMapIndex.intValue() >= 0) {
            batch.end();
            {
                // push camera pos
                float prevX = camera.position.x;
                float prevY = camera.position.y;

                // move tiled map to play area
                camera.translate(-96, -96);
                camera.update();

                // draw the tiled map
                mapRenderers[currentMapIndex.intValue()].setView(camera);
                mapRenderers[currentMapIndex.intValue()].render();

                // pop the camera pos
                camera.position.set(prevX, prevY, 0f);
                camera.update();
            }
            batch.begin();

            for (GameObject obj : gameObjects[currentMapIndex.intValue()]) {
                if (obj != null) obj.render(batch);
            }

            KeyItem k = keyItems[currentMapIndex.intValue()];
            if (k != null){
                k.render(batch);
            }
        }


        batch.setColor(1,1,1, playerAlpha.floatValue());
        TextureRegion bed = Assets.gameObjectTextures.get("empty-bed");
        batch.draw(bed, camera.viewportWidth/2 - 50, camera.viewportHeight/2 - 50, 100, 100);
        player.render(batch);

        batch.setColor(1,1,1,momAlpha.floatValue());
        batch.draw(Assets.momStanding[2], 210, 230, 25, 25 * 1.8f);
        batch.draw(Assets.gameObjectTextures.get("baby"), 209, 235, 27, 15);

        batch.setColor(1f, 1f, 1f, brainAlpha.floatValue());
        batch.draw(Assets.brainDetail, brainRect.x, brainRect.y, brainRect.width, brainRect.height);
        batch.setColor(Color.WHITE);

        Assets.particleManager.render(batch);
        batch.setColor(1,1,1,keyItemAlpha.floatValue());
        batch.draw(keyItemTex,
                keyItemRect.x + floatOffset.x,
                keyItemRect.y + floatOffset.y,
                keyItemRect.width / 2f,
                keyItemRect.height / 2f,
                keyItemRect.width,
                keyItemRect.height,
                1f,
                1f,
                keyItemAngle.floatValue());

        batch.setColor(overlayColor);
        batch.draw(Assets.whitePixel, 0, 0, camera.viewportWidth, camera.viewportHeight);
        batch.setColor(Color.WHITE);
    }

    public void runScript(){
        if (scriptRunning) return;
        switch (scriptSegment){
            case 0:
                scriptRunning = true;
                Timeline.createSequence()
                        .push(Tween.to(backgroundColor, ColorAccessor.RGB, 1).target(1, 1, 1))
                        .push(Tween.to(brainAlpha, 1, .5f).target(.05f))
                        .push(Tween.to(brainAlpha, 1, .5f).target(0))
                        .push(Tween.to(brainAlpha, 1, .5f).target(.1f))
                        .push(Tween.to(brainAlpha, 1, .5f).target(0))
                        .push(Tween.to(brainAlpha, 1, .5f).target(.2f))
                        .push(Tween.to(brainAlpha, 1, .5f).target(0))
                        .push(Tween.to(brainAlpha, 1, .5f).target(.4f))
                        .push(Tween.to(brainAlpha, 1, .4f).target(0))
                        .push(Tween.to(brainAlpha, 1, .4f).target(.9f))
                        .push(Tween.to(brainAlpha, 1, .4f).target(0))
                        .push(Tween.to(brainAlpha, 1, 1f).target(1))
                        .push(Tween.to(backgroundColor, ColorAccessor.A, 1).target(0))
                        .push(Tween.to(brainRect, RectangleAccessor.XYWH, 1).target(player.pos.x + 5, player.pos.y + 25, 10, 8))
                        .beginParallel()
                            .push(Tween.to(brainAlpha, 1, .5f).target(0).ease(Quint.IN))
                            .push(Tween.to(playerAlpha, 1, .5f).target(1).ease(Quint.OUT))
                        .end()
                        .push(Tween.call(new TweenCallback() {
                            @Override
                            public void onEvent(int i, BaseTween<?> baseTween) {
                                scriptSegment++;
                                scriptRunning = false;
                                showDialogue("Ahh yes I remember my life fondly.",
                                        "Many things tried to break down the walls of my mind.",
                                        "Some I was able to deal with, others affected me throughout my life.");
                            }
                        }))
                        .start(Assets.tween);


                break;
            case 1:
                if(!dialogue.isActive()){
                    scriptRunning = true;
                    scriptSegment++;
                    final String contracted = contractedStrings.get(LevelInfo.Stage.Infancy);
                    final String notContracted = notContractedStrings.get(LevelInfo.Stage.Infancy);
                    final boolean c = gameInfo.neurosis.get(LevelInfo.Stage.Infancy);
                    Timeline.createSequence()
//                            .push(Tween.to(backgroundColor, ColorAccessor.RGB, 1).target(0,0,0))
                            .push(Tween.to(playerAlpha, 1, 1).target(0))
                            .pushPause(2f)
                            .push(Tween.to(keyItemAlpha, 1, 1).target(1))
                            .push(Tween.call(new TweenCallback() {
                                @Override
                                public void onEvent(int i, BaseTween<?> baseTween) {
                                    scriptRunning = false;
                                    showDialogue(c ? contracted : notContracted);
                                }
                            }))
                            .start(Assets.tween);
                }
                break;
            case 2:
                if(!dialogue.isActive()){
                    stageTransition(LevelInfo.Stage.Toddler);
                }
                break;
            case 3:
                if(!dialogue.isActive()){
                    stageTransition(LevelInfo.Stage.Primary);
                }
                break;
            case 4:
                if(!dialogue.isActive()){
                    stageTransition(LevelInfo.Stage.Secondary);
                }
                break;
            case 5:
                if(!dialogue.isActive()){
                    stageTransition(LevelInfo.Stage.College);
                }
                break;
            case 6:
                if(!dialogue.isActive()){
                    stageTransition(LevelInfo.Stage.Work);
                }
                break;
            case 7:
                if(!dialogue.isActive()){
                    stageTransition(LevelInfo.Stage.Marriage);
                }
                break;
            case 8:
                if(!dialogue.isActive()){
                    stageTransition(LevelInfo.Stage.Career);
                }
                break;
            case 9:
                if(!dialogue.isActive()){
                    stageTransition(LevelInfo.Stage.Family);
                }
                break;
            case 10:
                if(!dialogue.isActive()){
                    stageTransition(LevelInfo.Stage.Retirement);
                }
                break;
            case 11:
                if (!dialogue.isActive()){
                    scriptRunning = true;
                    scriptSegment++;
                    Timeline.createSequence()
                            .push(Tween.to(keyItemAlpha, 1, 1).target(0))
                            .pushPause(2f)
                            .push(Tween.set(currentMapIndex, 1).target(LevelInfo.Stage.values().length -1))
                            .push(Tween.to(currentMapIndex, 1, 4).target(0).ease(Linear.INOUT))
                            .pushPause(1)
                            .push(Tween.to(momAlpha, 1, 1).target(1))
                            .push(Tween.call(new TweenCallback() {
                                @Override
                                public void onEvent(int i, BaseTween<?> baseTween) {
                                    showFinalDialogue("{TITLE} made for LD37 by Brian Ploeckleman and Doug Graham.");
                                }
                            }))
                            .start(Assets.tween);
                }
        }

    }

    private void setKeyItem(LevelInfo.Stage stage){
        switch (stage){
            case Infancy:
                keyItemTex = Assets.keyInfant;
                break;
            case Toddler:
                keyItemTex = Assets.keyToddler;
                break;
            case Primary:
                keyItemTex = Assets.keyPrimary;
                break;
            case Secondary:
                keyItemTex = Assets.keySecondary;
                break;
            case College:
                keyItemTex = Assets.keyCollege;
                break;
            case Work:
                keyItemTex = Assets.keyWork;
                break;
            case Marriage:
                keyItemTex = Assets.keyMarriage;
                break;
            case Career:
                keyItemTex = Assets.keyCareer;
                break;
            case Family:
                keyItemTex = Assets.keyFamily;
                break;
            case Retirement:
                keyItemTex = Assets.keyRetirement;
                break;

        }
        float aspect = keyItemTex.getRegionWidth() / (float)keyItemTex.getRegionHeight();
        float width = 60 * aspect;
        keyItemRect = new Rectangle(Config.gameWidth/2 - (width/2), Config.gameHeight/2 - 80, width, 60);

    }

    private void stageTransition(final LevelInfo.Stage stage){
        scriptRunning = true;
        scriptSegment++;
        final String contracted = contractedStrings.get(stage);
        final String notContracted = notContractedStrings.get(stage);
        final boolean c = gameInfo.neurosis.get(stage);
        Timeline.createSequence()
                .push(Tween.to(keyItemAlpha, 1, 1).target(0))
                .push(Tween.call(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        setKeyItem(stage);
                    }
                }))
                .pushPause(2f)
                .push(Tween.to(keyItemAlpha, 1, 1).target(1))
                .push(Tween.call(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        scriptRunning = false;
                        showDialogue(c ? contracted : notContracted);
                    }
                }))
                .start(Assets.tween);
    }

    private ObjectMap<LevelInfo.Stage, String> contractedStrings;
    private ObjectMap<LevelInfo.Stage, String> notContractedStrings;
    private void setStrings(){
        contractedStrings = new ObjectMap<LevelInfo.Stage, String>();
        notContractedStrings = new ObjectMap<LevelInfo.Stage, String>();

        contractedStrings.put(LevelInfo.Stage.Infancy, "I picked up abandonment issues at an early age.");
        notContractedStrings.put(LevelInfo.Stage.Infancy, "I was confronted with abandonment and learned to cope with it.");

        contractedStrings.put(LevelInfo.Stage.Toddler, "The stress of potty training stayed with me.");
        notContractedStrings.put(LevelInfo.Stage.Toddler, "I overcame potty training");

        contractedStrings.put(LevelInfo.Stage.Primary, "The stress of potty training stayed with me.");
        notContractedStrings.put(LevelInfo.Stage.Primary, "I overcame potty training");

        contractedStrings.put(LevelInfo.Stage.Secondary, "The stress of potty training stayed with me.");
        notContractedStrings.put(LevelInfo.Stage.Secondary, "I overcame potty training");

        contractedStrings.put(LevelInfo.Stage.College, "The stress of potty training stayed with me.");
        notContractedStrings.put(LevelInfo.Stage.College, "I overcame potty training");

        contractedStrings.put(LevelInfo.Stage.Work, "The stress of potty training stayed with me.");
        notContractedStrings.put(LevelInfo.Stage.Work, "I overcame potty training");

        contractedStrings.put(LevelInfo.Stage.Marriage, "The stress of potty training stayed with me.");
        notContractedStrings.put(LevelInfo.Stage.Marriage, "I overcame potty training");

        contractedStrings.put(LevelInfo.Stage.Career, "The stress of potty training stayed with me.");
        notContractedStrings.put(LevelInfo.Stage.Career, "I overcame potty training");

        contractedStrings.put(LevelInfo.Stage.Family, "The stress of potty training stayed with me.");
        notContractedStrings.put(LevelInfo.Stage.Family, "I overcame potty training");

        contractedStrings.put(LevelInfo.Stage.Retirement, "The stress of potty training stayed with me.");
        notContractedStrings.put(LevelInfo.Stage.Retirement, "I overcame potty training");

    }

    TiledMapRenderer[] mapRenderers;
    GameObject[][] gameObjects;
    KeyItem[] keyItems;
    private void loadMaps(){
        mapRenderers = new TiledMapRenderer[LevelInfo.Stage.values().length];
        gameObjects = new GameObject[LevelInfo.Stage.values().length][];
        keyItems = new KeyItem[LevelInfo.Stage.values().length];
        int count = 0;
        for (LevelInfo.Stage stage : LevelInfo.Stage.values()){
            LevelInfo li = new LevelInfo(stage);
            TiledMap map = (new TmxMapLoader()).load(li.mapName);
            mapRenderers[count] = new OrthoCachedTiledMapRenderer(map);
            ((OrthoCachedTiledMapRenderer) mapRenderers[count]).setBlending(true);

            MapLayer objectLayer = map.getLayers().get("objects");

            gameObjects[count] = new  GameObject[objectLayer.getObjects().getCount()];


            for (int i = 0; i < objectLayer.getObjects().getCount(); i++) {
                MapObject object = objectLayer.getObjects().get(i);
                MapProperties props = object.getProperties();
                // Shift x,y by map position
                float x = (Float) props.get("x") + 96;
                float y = (Float) props.get("y") + 96;
                float w = (Float) props.get("width");
                float h = (Float) props.get("height");
                String name = object.getName();
                String type = (String) props.get("type");

                Rectangle bounds = new Rectangle(x, y, w, h);

                // Instantiate based on type
                if (type.equals("keyitem")) {
                    keyItems[count] = (new KeyItem(stage, true, bounds));
                } else if (type.equals("gameobject")) {
                    gameObjects[count][i] = (new GameObject(name, bounds));
                }
//            else if (type.equals("...")) {
//
//            }
            }

            count++;
        }

    }

}