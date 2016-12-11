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
import com.badlogic.gdx.utils.GdxRuntimeException;
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
    public static ShaderProgram fontShader;
    public static ShaderProgram shimmerShader;
    public static ShaderProgram featherShader;

    public static Texture whitePixel;
    public static Texture whiteBox;
    public static Texture brainOutline;
    public static Texture brainDetail;
    public static TextureRegion[] walls;

    public static TextureAtlas atlas;

    public static NinePatch speechBubble;

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

        whitePixel = mgr.get("images/white-pixel.png", Texture.class);
        whiteBox = mgr.get("images/white-box.png", Texture.class);
        brainOutline = mgr.get("images/brain-outline.png", Texture.class);
        brainDetail = mgr.get("images/brain-detail.png", Texture.class);
        speechBubble = new NinePatch(mgr.get("images/speech-bubble.png", Texture.class), 11, 3, 3, 10);

        walls = new TextureRegion[16];
        walls[0] = atlas.findRegion("wall-top");  // BS
        walls[1] = atlas.findRegion("wall-top");
        walls[2] = atlas.findRegion("wall-left");
        walls[3] = atlas.findRegion("wall-top-left");
        walls[4] = atlas.findRegion("wall-bottom");
        walls[5] = atlas.findRegion("wall-top"); // BS can't happen
        walls[6] = atlas.findRegion("wall-bottom-left");
        walls[7] = atlas.findRegion("wall-top"); // BS can't happen
        walls[8] = atlas.findRegion("wall-right");
        walls[9] = atlas.findRegion("wall-top-right");
        walls[10] = atlas.findRegion("wall-top"); // can't happen
        walls[11] = atlas.findRegion("wall-top"); // BS
        walls[12] = atlas.findRegion("wall-bottom-right");
        walls[13] = atlas.findRegion("wall-top"); //BS
        walls[14] = atlas.findRegion("wall-top"); // BS
        walls[15] = atlas.findRegion("wall-top");


        final Texture distText = new Texture(Gdx.files.internal("fonts/ubuntu.png"), true);
        distText.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);
        font = new BitmapFont(Gdx.files.internal("fonts/ubuntu.fnt"), new TextureRegion(distText), false);

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

        return 1f;
    }

    public static void dispose() {
        batch.dispose();
        shapes.dispose();
        font.dispose();
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
        fontShader.setUniformf("u_scale", scale);
        font.getData().setScale(scale);
        font.setColor(c);
        font.draw(batch, text, x, y);
        font.getData().setScale(1f);
        fontShader.setUniformf("u_scale", 1f);
        font.getData().setScale(scale);
        batch.setShader(null);
    }

}
