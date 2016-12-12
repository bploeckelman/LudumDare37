package lando.systems.ld37.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld37.utils.Config;
import lando.systems.ld37.utils.Dialogue;

/**
 * Created by dsgraham on 12/12/16.
 */
public class BaseLevel {
    public Dialogue dialogue;
    protected Rectangle dialogueRect = new Rectangle();
    protected LevelInfo.Stage currentStage;

    public GameInfo gameInfo;

    public BaseLevel(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
        dialogue = new Dialogue();
        dialogueRect = new Rectangle(10, (int) (3f / 4f * Config.gameHeight) - 10, Config.gameWidth - 20, Config.gameHeight / 4);
        Gdx.input.setInputProcessor(dialogue);
    }

    public void update(float dt, OrthographicCamera camera) {

    }

    public void draw(SpriteBatch batch, OrthographicCamera camera) {

    }

    protected void showDialogue(String... messages) {
        dialogue.show((int) dialogueRect.x, (int) dialogueRect.y, (int) dialogueRect.width, (int) dialogueRect.height, messages);
    }

    protected void showDialogue(Array<String> messages) {
        dialogue.show((int) dialogueRect.x, (int) dialogueRect.y, (int) dialogueRect.width, (int) dialogueRect.height, messages);
    }

    protected void showFinalDialogue(String msg){
        Array<String> messages = new Array<String>();
        messages.add(msg);
        dialogue.show((int) dialogueRect.x, (int) dialogueRect.y, (int) dialogueRect.width, (int) dialogueRect.height, messages, false, 20f, false);

    }

    public boolean isLevelComplete() {
        return false;
    }
}