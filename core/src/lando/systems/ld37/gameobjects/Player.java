package lando.systems.ld37.gameobjects;

import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    public float accum;
    public int facing = 2;
    public boolean moving;
    public MutableFloat alpha;
    public float repairSoundAccum = 0f;

    public Player(LevelInfo levelInfo){
        pos = new Vector2(Config.gameWidth /2, Config.gameHeight/2);
        width = 25;
        wallFixSpeed = levelInfo.playerFixSpeed;
        center = new Vector2();
        accum = 0;
        moving = false;
        alpha = new MutableFloat(1);
    }

    public void update(float dt, Vector2 moveVec){
        accum += dt;
        if (moveVec.x > 0) facing = 3;
        else if (moveVec.x < 0) facing = 1;
        else if (moveVec.y > 0) facing = 0;
        else if (moveVec.y < 0) facing = 2;

        moving = !moveVec.epsilonEquals(0,0, .1f);

        center.set(pos.x + width/2, pos.y + width/2);
        if (wall != null){
            repairSoundAccum += dt;
            if (repairSoundAccum > 0.8f) {
                Assets.repairSound.play(1f);
                repairSoundAccum = 0f;
            }

            wall.health += wallFixSpeed * dt;
            if (wall.health >= 100){
                wall.health = 100;
                wall.cracking = false;
                wall.tutorialWall = false;
                wall = null;
            }
        } else {
            repairSoundAccum = 0f;
        }
        if (wall != null){
            if (center.dst(wall.center) > Level.clickDistance){
                wall = null;
            }
        }
    }

    public void render(SpriteBatch batch){
        TextureRegion tex = moving ? Assets.playerAnimations[facing].getKeyFrame(accum) : Assets.playerStanding[facing];
        batch.setColor(1,1,1,alpha.floatValue());
        batch.draw(tex, pos.x, pos.y, width, width*1.8f);
        batch.setColor(Color.WHITE);
    }
}
