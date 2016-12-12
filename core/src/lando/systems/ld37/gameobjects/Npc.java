package lando.systems.ld37.gameobjects;

import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld37.utils.Assets;

/**
 * Created by Brian on 12/10/2016.
 */
public class Npc {
    public enum npcType { MOM }

    public String name;
    public Vector2 centerPos;
    public Rectangle bounds;
    public TextureRegion keyframe;
    public MutableFloat alpha;
    private float speechTimer = 0f;
    private String speechText = "";
    private Color textColor;
    public boolean moving;
    public int facing;
    float accum;
    npcType type;
    Animation[] animations;
    TextureRegion[] standingTex;

    public Npc() {}

    public Npc(String name, Rectangle bounds, npcType type) {
        this.type = type;
        this.name = name;
        this.bounds = bounds;
        this.keyframe = keyframe;
        this.centerPos = new Vector2();
        alpha = new MutableFloat(1);
        bounds.getCenter(this.centerPos);
        textColor = new Color(0,0,0,1);
        moving = false;
        facing = 0;
        accum = 0;
        setAnimations();
    }

    public Npc(String name, float x, float y, float w, float h, npcType type) {
        this(name, new Rectangle(x, y, w, h), type);
    }

    public void update(float dt) {
        accum += dt;
        bounds.getCenter(centerPos);
        if (speechTimer > 0f) {
            speechTimer -= dt;
            if (speechTimer < 0f) {
                speechTimer = 0f;
            }
        }
    }

    public void draw(SpriteBatch batch) {
        batch.setColor(1,1,1,alpha.floatValue());
        keyframe  = moving ? animations[facing].getKeyFrame(accum) : standingTex[facing];

        if (keyframe != null) {
            batch.draw(keyframe, bounds.x, bounds.y, bounds.width, bounds.height);
        }
        if (speechTimer > 0f) {
            drawSpeechBubble(batch);
        }
        batch.setColor(Color.WHITE);
    }

    public void say(String text, float duration) {
        speechTimer = duration;
        speechText = text;
    }

    private void drawSpeechBubble(SpriteBatch batch) {
        float scale = 1.0f;
        float prevScale = Assets.font.getData().scaleX;
        Assets.font.getData().setScale(scale);
        Assets.layout.setText(Assets.font, speechText);
        Assets.font.getData().setScale(prevScale);
        textColor.set(0,0,0,alpha.floatValue());
        Assets.speechBubble.draw(batch,
                centerPos.x - Assets.layout.width - 20,
                centerPos.y + 5,
                Assets.layout.width + 20,
                Assets.layout.height + 20
        );
        Assets.drawString(batch, speechText,
                centerPos.x - Assets.layout.width - 10,
                centerPos.y + 20 + Assets.layout.height,
                textColor,
                scale
        );
    }

    private void setAnimations(){
        switch(type){
            case MOM:
                animations = Assets.momAnimations;
                standingTex = Assets.momStanding;
                break;
        }
    }

}
