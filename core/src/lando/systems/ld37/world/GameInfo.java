package lando.systems.ld37.world;

import com.badlogic.gdx.utils.ObjectMap;
import lando.systems.ld37.screens.GameScreen;

/**
 * Created by dsgraham on 12/10/16.
 */
public class GameInfo {
    public LevelInfo.Stage currentStage;
    public ObjectMap<LevelInfo.Stage, Boolean> neurosis;
    public GameScreen gameScreen;

    public GameInfo(GameScreen gameScreen){
        this.gameScreen = gameScreen;
        currentStage = LevelInfo.Stage.Infancy;
        neurosis = new ObjectMap<LevelInfo.Stage, Boolean>();
    }

    public void addStageComplete(LevelInfo.Stage stage, boolean contracted){
        neurosis.put(stage, contracted);
    }

    public void nextStage(){
        switch(currentStage){
            case Infancy:
                currentStage = LevelInfo.Stage.Toddler;
                break;
            case Toddler:
                currentStage = LevelInfo.Stage.Primary;
                break;
            case Primary:
                currentStage = LevelInfo.Stage.Secondary;
                break;
            case Secondary:
                currentStage = LevelInfo.Stage.College;
                break;
            case College:
                currentStage = LevelInfo.Stage.Work;
                break;
            case Work:
                currentStage = LevelInfo.Stage.Marriage;
                break;
            case Marriage:
                currentStage = LevelInfo.Stage.Career;
                break;
            case Career:
                currentStage = LevelInfo.Stage.Family;
                break;
            case Family:
                currentStage = LevelInfo.Stage.Retirement;
                break;
            case Retirement:
                currentStage = LevelInfo.Stage.Death;
                break;
            default:
                currentStage = LevelInfo.Stage.Death;
        }
    }
}
