package lando.systems.ld37.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld37.utils.Assets;

/**
 * Created by Brian on 12/10/2016.
 */
public class Npc {

    public String name;
    public Vector2 centerPos;
    public Rectangle bounds;
    public TextureRegion keyframe;

    private float speechTimer = 0f;
    private String speechText = "";

    public Npc() {}

    public Npc(String name, Rectangle bounds, TextureRegion keyframe) {
        this.name = name;
        this.bounds = bounds;
        this.keyframe = keyframe;
        this.centerPos = new Vector2();
        bounds.getCenter(this.centerPos);
    }

    public Npc(String name, float x, float y, float w, float h, TextureRegion keyframe) {
        this(name, new Rectangle(x, y, w, h), keyframe);
    }

    public void update(float dt) {
        bounds.getCenter(centerPos);
        if (speechTimer > 0f) {
            speechTimer -= dt;
            if (speechTimer < 0f) {
                speechTimer = 0f;
            }
        }
        Gdx.app.log("SpeechTimer/Bounds.Y", name + ": " + Float.toString(speechTimer) + ", " + bounds.y);
    }

    public void draw(SpriteBatch batch) {
        if (keyframe != null) {
            batch.draw(keyframe, bounds.x, bounds.y, bounds.width, bounds.height);
        }
        if (speechTimer > 0f) {
            drawSpeechBubble(batch);
        }
    }

    public void say(String text, float duration) {
        speechTimer = duration;
        speechText = text;
    }

    private void drawSpeechBubble(SpriteBatch batch) {
        float scale = 0.4f;
        float prevScale = Assets.font.getData().scaleX;
        Assets.font.getData().setScale(scale);
        Assets.layout.setText(Assets.font, speechText);
        Assets.font.getData().setScale(prevScale);

        Assets.speechBubble.draw(batch,
                centerPos.x - Assets.layout.width - 20,
                centerPos.y + 10,
                Assets.layout.width + 20,
                Assets.layout.height + 20
        );
        Assets.drawString(batch, speechText,
                centerPos.x - Assets.layout.width - 10,
                centerPos.y + 20 + Assets.layout.height,
                Color.BLACK,
                scale
        );
    }

}
