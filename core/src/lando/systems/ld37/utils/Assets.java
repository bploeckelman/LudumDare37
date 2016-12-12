package lando.systems.ld37.utils;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import lando.systems.ld37.accessors.*;
import lando.systems.ld37.utils.particles.ParticleManager;

/**
 * Created by Brian on 12/10/2016.
 */
public class Assets {

    public static AssetManager mgr;
    public static TweenManager tween;
    public static ParticleManager particleManager;
    public static SpriteBatch batch;
    public static ShapeRenderer shapes;
    public static GlyphLayout layout;
    public static BitmapFont font;
    public static BitmapFont font8pt;
    public static ShaderProgram fontShader;
    public static ShaderProgram shimmerShader;
    public static ShaderProgram featherShader;

    public static TextureRegion whitePixel;
    public static Texture whiteBox;
    public static Texture brainOutline;
    public static Texture brainDetail;
    public static TextureRegion[] walls;
    public static TextureRegion[] wallsDamaged;
    public static TextureRegion[] wallsCracked;
    public static Array<TextureAtlas.AtlasRegion> sparkles;
    public static TextureRegion keyInfant;
    public static TextureRegion keyToddler;
    public static TextureRegion keyPrimary;
    public static TextureRegion keySecondary;
    public static TextureRegion keyCollege;
    public static TextureRegion keyWork;
    public static TextureRegion keyMarriage;
    public static TextureRegion keyCareer;
    public static TextureRegion keyFamily;
    public static TextureRegion keyRetirement;
    public static TextureRegion clockFace;
    public static TextureRegion vignette;
    public static TextureRegion[] playerStanding;
    public static Animation[] playerAnimations;

    public static TextureRegion[] momStanding;
    public static Animation[] momAnimations;

    public static ObjectMap<String, TextureRegion> gameObjectTextures;

    public static TextureAtlas atlas;

    public static NinePatch speechBubble;
    public static NinePatch outline;

    public static boolean initialized;

    public static void load() {
        initialized = false;

        final TextureLoader.TextureParameter linearParams = new TextureLoader.TextureParameter();
        linearParams.minFilter = Texture.TextureFilter.Linear;
        linearParams.magFilter = Texture.TextureFilter.Linear;

        final TextureLoader.TextureParameter nearestParams = new TextureLoader.TextureParameter();
        nearestParams.minFilter = Texture.TextureFilter.Nearest;
        nearestParams.magFilter = Texture.TextureFilter.Nearest;

        mgr = new AssetManager();
        mgr.load("images/white-pixel.png", Texture.class, nearestParams);
        mgr.load("images/white-box.png", Texture.class, linearParams);
        mgr.load("images/brain-outline.png", Texture.class, linearParams);
        mgr.load("images/brain-detail.png", Texture.class, linearParams);
        mgr.load("images/speech-bubble.png", Texture.class, nearestParams);

        if (tween == null) {
            tween = new TweenManager();
            Tween.setCombinedAttributesLimit(4);
            Tween.setWaypointsLimit(2);
            Tween.registerAccessor(Color.class, new ColorAccessor());
            Tween.registerAccessor(Rectangle.class, new RectangleAccessor());
            Tween.registerAccessor(Vector2.class, new Vector2Accessor());
            Tween.registerAccessor(Vector3.class, new Vector3Accessor());
            Tween.registerAccessor(OrthographicCamera.class, new CameraAccessor());
        }

        if (particleManager == null){
            particleManager = new ParticleManager();
        }

        batch = new SpriteBatch();
        shapes = new ShapeRenderer();
        layout = new GlyphLayout();

        atlas = new TextureAtlas(Gdx.files.internal("images/sheets/sprites.atlas"));

    }

    public static float update() {
        if (!mgr.update()) return mgr.getProgress();
        if (initialized) return 1f;
        initialized = true;

        whiteBox = mgr.get("images/white-box.png", Texture.class);
        whitePixel = new TextureRegion(mgr.get("images/white-pixel.png", Texture.class));

        brainOutline = mgr.get("images/brain-outline.png", Texture.class);
        brainDetail = mgr.get("images/brain-detail.png", Texture.class);
        speechBubble = new NinePatch(mgr.get("images/speech-bubble.png", Texture.class), 3, 11, 3, 10);

        TextureRegion wallRegion = atlas.findRegion("walls");

        walls = new TextureRegion[16];
        walls[0] = atlas.findRegion("wall-top");  // BS
        walls[1] = new TextureRegion(wallRegion, 256, 0, 64, 64);  // TOP
        walls[2] = new TextureRegion(wallRegion, 192, 64, 64, 64); // LEFT
        walls[3] = new TextureRegion(wallRegion, 192, 0, 64, 64);  // TOP LEFT
        walls[4] = new TextureRegion(wallRegion, 256, 128, 64, 64); // BOTTOM
        walls[5] = atlas.findRegion("wall-top"); // BS can't happen
        walls[6] = new TextureRegion(wallRegion, 192, 128, 64, 64); // BOTTOM LEFT
        walls[7] = atlas.findRegion("wall-top"); // BS can't happen
        walls[8] = new TextureRegion(wallRegion, 320, 64, 64, 64); // RIGHT
        walls[9] = new TextureRegion(wallRegion, 320, 0, 64, 64); // TOP RIGHT
        walls[10] = atlas.findRegion("wall-top"); // can't happen
        walls[11] = atlas.findRegion("wall-top"); // BS
        walls[12] = new TextureRegion(wallRegion, 320, 128, 64, 64); // BOTTOM RIGHT
        walls[13] = atlas.findRegion("wall-top"); //BS
        walls[14] = atlas.findRegion("wall-top"); // BS
        walls[15] = atlas.findRegion("wall-top"); // BS

        wallsDamaged = new TextureRegion[16];
        wallsDamaged[0] = atlas.findRegion("wall-top");  // BS
        wallsDamaged[1] = new TextureRegion(wallRegion, 64, 0, 64, 64);  // TOP
        wallsDamaged[2] = new TextureRegion(wallRegion, 0, 64, 64, 64); // LEFT
        wallsDamaged[3] = new TextureRegion(wallRegion, 0, 0, 64, 64);  // TOP LEFT
        wallsDamaged[4] = new TextureRegion(wallRegion, 64, 128, 64, 64); // BOTTOM
        wallsDamaged[5] = atlas.findRegion("wall-top"); // BS can't happen
        wallsDamaged[6] = new TextureRegion(wallRegion, 0, 128, 64, 64); // BOTTOM LEFT
        wallsDamaged[7] = atlas.findRegion("wall-top"); // BS can't happen
        wallsDamaged[8] = new TextureRegion(wallRegion, 128, 64, 64, 64); // RIGHT
        wallsDamaged[9] = new TextureRegion(wallRegion, 128, 0, 64, 64); // TOP RIGHT
        wallsDamaged[10] = atlas.findRegion("wall-top"); // can't happen
        wallsDamaged[11] = atlas.findRegion("wall-top"); // BS
        wallsDamaged[12] = new TextureRegion(wallRegion, 128, 128, 64, 64); // BOTTOM RIGHT
        wallsDamaged[13] = atlas.findRegion("wall-top"); //BS
        wallsDamaged[14] = atlas.findRegion("wall-top"); // BS
        wallsDamaged[15] = atlas.findRegion("wall-top"); // BS

        wallsCracked = new TextureRegion[16];
        wallsCracked[0] = atlas.findRegion("wall-top");  // BS
        wallsCracked[1] = new TextureRegion(wallRegion, 448, 0, 64, 64);  // TOP
        wallsCracked[2] = new TextureRegion(wallRegion, 384, 64, 64, 64); // LEFT
        wallsCracked[3] = new TextureRegion(wallRegion, 384, 0, 64, 64);  // TOP LEFT
        wallsCracked[4] = new TextureRegion(wallRegion, 448, 128, 64, 64); // BOTTOM
        wallsCracked[5] = atlas.findRegion("wall-top"); // BS can't happen
        wallsCracked[6] = new TextureRegion(wallRegion, 384, 128, 64, 64); // BOTTOM LEFT
        wallsCracked[7] = atlas.findRegion("wall-top"); // BS can't happen
        wallsCracked[8] = new TextureRegion(wallRegion, 512, 64, 64, 64); // RIGHT
        wallsCracked[9] = new TextureRegion(wallRegion, 512, 0, 64, 64); // TOP RIGHT
        wallsCracked[10] = atlas.findRegion("wall-top"); // can't happen
        wallsCracked[11] = atlas.findRegion("wall-top"); // BS
        wallsCracked[12] = new TextureRegion(wallRegion, 512, 128, 64, 64); // BOTTOM RIGHT
        wallsCracked[13] = atlas.findRegion("wall-top"); //BS
        wallsCracked[14] = atlas.findRegion("wall-top"); // BS
        wallsCracked[15] = atlas.findRegion("wall-top"); // BS

        sparkles = atlas.findRegions("sparkle");
//        whitePixel = atlas.findRegion("white-pixel");
        outline = new NinePatch(atlas.findRegion("outline"), 5, 5, 5, 5);
        clockFace    = atlas.findRegion("clock-face");
        vignette     = atlas.findRegion("vignette");
        keyInfant    = atlas.findRegion("key-infancy");
        keyToddler   = atlas.findRegion("key-toddler");
        keyPrimary   = atlas.findRegion("key-primary");
        keySecondary = atlas.findRegion("key-secondary");
        keyCollege   = atlas.findRegion("key-college");
        keyWork      = atlas.findRegion("key-work");
        keyMarriage  = atlas.findRegion("key-marriage");
        keyCareer    = atlas.findRegion("key-career");
        keyFamily    = atlas.findRegion("key-family");
        keyRetirement= atlas.findRegion("key-retirement");

        TextureRegion chars = atlas.findRegion("chars");
        playerAnimations = new Animation[4];
        playerStanding = new TextureRegion[4];
        setAnimations(chars, 0, 0, playerAnimations, playerStanding);

        momAnimations = new Animation[4];
        momStanding = new TextureRegion[4];
        setAnimations(chars, 54, 0, momAnimations, momStanding);

        gameObjectTextures = new ObjectMap<String, TextureRegion>();
        gameObjectTextures.put("chair-brown", atlas.findRegion("chair-brown"));
        gameObjectTextures.put("lamp", atlas.findRegion("lamp"));
        gameObjectTextures.put("hospital-bed", atlas.findRegion("hospital-bed"));
        gameObjectTextures.put("toilet", atlas.findRegion("toilet"));
        gameObjectTextures.put("sink", atlas.findRegion("sink"));
        gameObjectTextures.put("table-flower", atlas.findRegion("table-flower"));
        gameObjectTextures.put("table-sink", atlas.findRegion("table-sink"));
        gameObjectTextures.put("locker", atlas.findRegion("locker"));
        gameObjectTextures.put("bed-bunk", atlas.findRegion("bed-bunk"));
        gameObjectTextures.put("desk-dorm", atlas.findRegion("desk-dorm"));
        gameObjectTextures.put("desk-office", atlas.findRegion("desk-office"));
        gameObjectTextures.put("chair-office", atlas.findRegion("chair-office"));
        gameObjectTextures.put("copymachine", atlas.findRegion("copymachine"));
        gameObjectTextures.put("bookshelf", atlas.findRegion("bookshelf"));
        gameObjectTextures.put("chair-office-nice-back", atlas.findRegion("chair-office-nice-back"));
        gameObjectTextures.put("chair-office-nice-front", atlas.findRegion("chair-office-nice-front"));
        gameObjectTextures.put("plant-fern", atlas.findRegion("plant-fern"));
        gameObjectTextures.put("table-end-lamp", atlas.findRegion("table-end-lamp"));
        gameObjectTextures.put("desk-office-nice", atlas.findRegion("desk-office-nice"));
        gameObjectTextures.put("table-end", atlas.findRegion("table-end"));
        gameObjectTextures.put("table-coffee", atlas.findRegion("table-coffee"));
        gameObjectTextures.put("chair-living-room-back", atlas.findRegion("chair-living-room-back"));
        gameObjectTextures.put("couch-living-room-back", atlas.findRegion("couch-living-room-back"));
        gameObjectTextures.put("tv", atlas.findRegion("tv"));
        gameObjectTextures.put("baby", atlas.findRegion("baby"));
        gameObjectTextures.put("baby-alt", atlas.findRegion("baby-alt"));

        final Texture distText = new Texture(Gdx.files.internal("fonts/ubuntu.png"), true);
        distText.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);
//        font = new BitmapFont(Gdx.files.internal("fonts/ubuntu.fnt"), new TextureRegion(distText), false);
        font = new BitmapFont(Gdx.files.internal("fonts/emulogic-16pt.fnt"));
        font8pt = new BitmapFont(Gdx.files.internal("fonts/emulogic-8pt.fnt"));

        fontShader = new ShaderProgram(Gdx.files.internal("shaders/dist.vert"),
                Gdx.files.internal("shaders/dist.frag"));
        if (!fontShader.isCompiled()) {
            Gdx.app.error("fontShader", "compilation failed:\n" + fontShader.getLog());
        }

//        ShaderProgram.pedantic = false;
        shimmerShader = new ShaderProgram(Gdx.files.internal("shaders/default.vert"),
                Gdx.files.internal("shaders/shimmer.frag"));
        if (!shimmerShader.isCompiled()) {
            Gdx.app.error("shimmerShader", "compilation failed:\n" + shimmerShader.getLog());
        }

        featherShader = new ShaderProgram(Gdx.files.internal("shaders/default.vert"),
                Gdx.files.internal("shaders/feather.frag"));
        if (!featherShader.isCompiled()) {
            Gdx.app.error("featherShader", "compilation failed:\n" + featherShader.getLog());
        }

        TextHelper.load();

        return 1f;
    }

    public static void dispose() {
        batch.dispose();
        shapes.dispose();
        font.dispose();
        font8pt.dispose();
        mgr.clear();
    }

    private static ShaderProgram compileShaderProgram(FileHandle vertSource, FileHandle fragSource) {
        ShaderProgram.pedantic = false;
        final ShaderProgram shader = new ShaderProgram(vertSource, fragSource);
        if (!shader.isCompiled()) {
            throw new GdxRuntimeException("Failed to compile shader program:\n" + shader.getLog());
        }
        else if (shader.getLog().length() > 0) {
            Gdx.app.debug("SHADER", "ShaderProgram compilation log:\n" + shader.getLog());
        }
        return shader;
    }

    public static void drawString(SpriteBatch batch, String text, float x, float y, Color c, float scale){
        batch.setShader(fontShader);
        float prevScale = font.getData().scaleX;
        font.getData().setScale(scale);
        font.setColor(c);
        font.draw(batch, text, x, y);
        font.getData().setScale(prevScale);
        batch.setShader(null);
    }

    public static TextureRegion getTextureRegionForGameObject(String name) {
        TextureRegion region = gameObjectTextures.get(name);
        return (region != null) ? region : new TextureRegion(Assets.whiteBox);
    }

    public static void setAnimations(TextureRegion chars, int xOffset, int yOffset, Animation[] animations, TextureRegion[] standings){
        for (int i = 0; i < 4; i++){
            Array<TextureRegion> anim = new Array<TextureRegion>();
            for (int j = 0; j < 3; j++){
                anim.add(new TextureRegion(chars, xOffset + 18 * j, yOffset +26 * i, 18, 26));
            }
            animations[i] = new Animation(.25f, anim, Animation.PlayMode.LOOP_PINGPONG);
            standings[i] = new TextureRegion(chars, xOffset + 18, yOffset + 26*i, 18, 26);
        }
    }

}
