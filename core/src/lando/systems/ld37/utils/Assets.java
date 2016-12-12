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
    public static Array<TextureAtlas.AtlasRegion> sparkles;
    public static TextureRegion keyInfant;
    public static TextureRegion keyToddler;
    public static TextureRegion clockFace;
    public static TextureRegion vignette;

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

        sparkles = atlas.findRegions("sparkle");
        whitePixel = atlas.findRegion("white-pixel");
        outline = new NinePatch(atlas.findRegion("outline"), 5, 5, 5, 5);
        clockFace = atlas.findRegion("clock-face");
        vignette = atlas.findRegion("vignette");
        keyInfant = atlas.findRegion("key-infancy");
        keyToddler = atlas.findRegion("key-toddler");

        gameObjectTextures = new ObjectMap<String, TextureRegion>();
        gameObjectTextures.put("chair-brown", atlas.findRegion("chair-brown"));
        gameObjectTextures.put("lamp", atlas.findRegion("lamp"));
        gameObjectTextures.put("hospital-bed", atlas.findRegion("hospital-bed"));
        gameObjectTextures.put("hospital-iv", atlas.findRegion("hospital-iv"));
        gameObjectTextures.put("toilet", atlas.findRegion("toilet"));
        gameObjectTextures.put("sink", atlas.findRegion("sink"));
        gameObjectTextures.put("table-flower", atlas.findRegion("table-flower"));
        gameObjectTextures.put("table-sink", atlas.findRegion("table-sink"));

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

}
