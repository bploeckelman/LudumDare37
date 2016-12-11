package lando.systems.ld37.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld37.utils.Assets;
import lando.systems.ld37.utils.Config;
import lando.systems.ld37.world.Level;
import lando.systems.ld37.world.LevelInfo;

/**
 * Created by dsgraham on 12/10/16.
 */
public class Player {
    public Vector2 pos;
    public float width;
    public float wallFixSpeed;
    public Wall wall;
    public Vector2 center;

    public Player(LevelInfo levelInfo){
        pos = new Vector2(Config.gameWidth /2, Config.gameHeight/2);
        width = 20;
        wallFixSpeed = levelInfo.playerFixSpeed;
        center = new Vector2();
    }

    public void update(float dt){
        center.set(pos.x + width/2, pos.y + width/2);
        if (wall != null){
            wall.health += wallFixSpeed * dt;
            if (wall.health >= 100){
                wall.health = 100;
                wall.cracking = false;
                wall.tutorialWall = false;
                wall = null;
            }
        }
        if (wall != null){
            if (center.dst(wall.center) > Level.clickDistance){
                wall = null;
            }
        }
    }

    public void render(SpriteBatch batch){
        batch.draw(Assets.whiteBox, pos.x, pos.y, width, width);
    }
}
