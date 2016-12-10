package lando.systems.ld37.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld37.utils.Assets;

/**
 * Created by dsgraham on 12/10/16.
 */
public class Wall {
    Rectangle bounds;
    float health;
    float crackSpeed;
    boolean cracking;

    public Wall(Rectangle bounds, float crackSpeed){
        this.bounds = bounds;
        this.health = 100;
        this.cracking = false;
        this.crackSpeed = crackSpeed;
    }

    public void update(float dt){
        if (cracking){
            health -= crackSpeed * dt;
        }
    }

    public void render(SpriteBatch batch){
        batch.draw(Assets.whiteBox, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public boolean destroyed(){
        return health <= 0;
    }
}
