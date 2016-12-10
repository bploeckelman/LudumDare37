package lando.systems.ld37.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld37.utils.Assets;
import lando.systems.ld37.utils.Config;

/**
 * Created by dsgraham on 12/10/16.
 */
public class Player {
    public Vector2 pos;
    public float width;
    public Wall wall;

    public Player(){
        pos = new Vector2(Config.gameWidth /2, Config.gameHeight/2);
        width = 20;
    }

    public void update(float dt){

    }

    public void render(SpriteBatch batch){
        batch.draw(Assets.whiteBox, pos.x, pos.y, width, width);
    }
}
