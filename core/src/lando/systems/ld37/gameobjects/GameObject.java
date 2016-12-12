package lando.systems.ld37.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld37.utils.Assets;

/**
 * Brian Ploeckelman created on 8/22/2015.
 */
public class GameObject {

    public String name;
    public Rectangle bounds;
    public TextureRegion keyframe;

    public GameObject(String name, Rectangle bounds) {
        this.name = name;
        this.bounds = bounds;
        this.keyframe = Assets.getTextureRegionForGameObject(name);
    }

    public void update(float delta) {

    }

    public void render(SpriteBatch batch) {
        batch.draw(keyframe, bounds.x, bounds.y, bounds.width, bounds.height);
    }

}
