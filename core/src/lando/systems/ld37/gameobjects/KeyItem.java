package lando.systems.ld37.gameobjects;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld37.accessors.RectangleAccessor;
import lando.systems.ld37.utils.Assets;
import lando.systems.ld37.utils.Config;
import lando.systems.ld37.world.LevelInfo;

/**
 * Created by dsgraham on 12/11/16.
 */
public class KeyItem {
    LevelInfo.Stage stage;
    boolean active;
    public Rectangle bounds;
    public TextureRegion tex;
    public MutableFloat angle;
    public Vector2 accum;
    public Vector2 floatOffset;

    public KeyItem(LevelInfo.Stage stage, boolean active){
        this.stage = stage;
        this.active = active;
        angle = new MutableFloat(0);
        floatOffset = new Vector2();
        this.accum = new Vector2(0,0);

        switch(stage){
            case Infancy:
                tex = Assets.keyInfant;
                bounds = new Rectangle(Config.gameWidth/2, Config.gameHeight - 70, 40, 40);
                break;
            default:
                tex = Assets.keyInfant;
                bounds = new Rectangle(30, 30, 30, 30);

        }

        if (active){
            bounds = new Rectangle(Config.gameWidth /2 - 20, Config.gameHeight/2, 20, 20);
        } else {
            this.accum = new Vector2(MathUtils.random(0.1f, 1f),
                    MathUtils.random(0.1f, 1f));
            angle = new MutableFloat(MathUtils.random(-5f, 5f));
            Tween.to(angle, -1, MathUtils.random(1f, 1.5f))
                    .target(-1f * angle.floatValue())
                    .repeatYoyo(-1, 0f)
                    .start(Assets.tween);
        }
    }

    public void update(float dt){
        if (!active){
            accum.add(dt, dt);
            floatOffset.set(MathUtils.cos(accum.x * 2f) * 1.5f,
                    MathUtils.sin(accum.y * 5f) * 2.0f);
        }
    }

    public void render(SpriteBatch batch){
        batch.draw(tex,
                bounds.x + floatOffset.x,
                bounds.y + floatOffset.y,
                bounds.width / 2f,
                bounds.height / 2f,
                bounds.width,
                bounds.height,
                1f,
                1f,
                angle.floatValue());
    }
}
