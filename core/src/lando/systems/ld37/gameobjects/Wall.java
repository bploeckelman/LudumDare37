package lando.systems.ld37.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld37.utils.Assets;
import lando.systems.ld37.utils.Utils;

/**
 * Created by dsgraham on 12/10/16.
 */
public class Wall {
    public Rectangle bounds;
    float health;
    float crackSpeed;
    public boolean cracking;
    Color healthColor;
    public boolean hovered;

    public Wall(Rectangle bounds, float crackSpeed){
        this.bounds = bounds;
        this.health = 100;
        this.cracking = false;
        this.crackSpeed = crackSpeed;
        healthColor = new Color();
        hovered = false;
    }

    public void update(float dt){
        if (cracking){
            health -= crackSpeed * dt;
        }
        if (health < 0){
            health = 0;
        }
        hovered = false;
    }

    public void render(SpriteBatch batch, Player player){
        if (this == player.wall){
            batch.setColor(Color.GREEN);
        }
        else if (hovered){
            batch.setColor(Color.YELLOW);
        } else {
            batch.setColor(Color.WHITE);
        }
        batch.draw(Assets.whiteBox, bounds.x, bounds.y, bounds.width, bounds.height);
        if (health < 100){
            batch.setColor(Color.BLACK);
            batch.draw(
                    Assets.whitePixel,
                    bounds.x + 5,
                    bounds.y + bounds.height/2,
                    bounds.width - 10, // Full health
                    5
            );
            float n = health / 100;
            healthColor = Utils.hsvToRgb(((n * 120f) - 20) / 365f, 1.0f, 1.0f, healthColor);
            batch.setColor(healthColor);
            batch.draw(
                    Assets.whitePixel,
                    bounds.x + 5,
                    bounds.y + bounds.height/2,
                    n * (bounds.width - 10),
                    5
            );
        }
        batch.setColor(Color.WHITE);
    }

    public boolean destroyed(){
        return health <= 0;
    }
}
