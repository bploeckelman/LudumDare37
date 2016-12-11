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
    int type;
    public Vector2 center;

    public Wall(int type, Rectangle bounds, float crackSpeed){
        this.type = type;
        this.bounds = bounds;
        this.health = 100;
        this.cracking = false;
        this.crackSpeed = crackSpeed;
        healthColor = new Color();
        hovered = false;
        center = new Vector2(bounds.x + bounds.width/2, bounds.y + bounds.height/2);
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
            batch.setColor(Color.ORANGE);
        } else {
            batch.setColor(Color.WHITE);
        }
        batch.draw(Assets.walls[type], bounds.x, bounds.y, bounds.width, bounds.height);
        if (health < 100){
            if ((type & 1) == 1 || (type & 4) == 4) {
                float yOffest = 0;
                if ((type & 1) == 1) yOffest = bounds.height - 5;
                batch.setColor(Color.BLACK);
                batch.draw(
                        Assets.whitePixel,
                        bounds.x + 4,
                        bounds.y + yOffest - 1,
                        bounds.width - 8, // Full health
                        7
                );
                float n = health / 100;
                healthColor = Utils.hsvToRgb(((n * 120f) - 20) / 365f, 1.0f, 1.0f, healthColor);
                batch.setColor(healthColor);
                batch.draw(
                        Assets.whitePixel,
                        bounds.x + 5,
                        bounds.y + yOffest,
                        n * (bounds.width - 10),
                        5
                );
            } else {
                float xOffest = 0;
                if ((type & 0x8) == 0x8) xOffest = bounds.width - 5;
                batch.setColor(Color.BLACK);
                batch.draw(
                        Assets.whitePixel,
                        bounds.x + xOffest -1,
                        bounds.y + 4,
                        7,
                        bounds.height - 8 // Full health
                );
                float n = health / 100;
                healthColor = Utils.hsvToRgb(((n * 120f) - 20) / 365f, 1.0f, 1.0f, healthColor);
                batch.setColor(healthColor);
                batch.draw(
                        Assets.whitePixel,
                        bounds.x + xOffest,
                        bounds.y + 5,
                        5,
                        n * (bounds.height - 10)
                );
            }
        }
        batch.setColor(Color.WHITE);
    }

    public boolean destroyed(){
        return health <= 0;
    }
}
