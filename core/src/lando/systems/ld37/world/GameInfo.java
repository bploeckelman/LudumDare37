package lando.systems.ld37.world;

import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by dsgraham on 12/10/16.
 */
public class GameInfo {
    public LevelInfo.Stage currentStage;
    public ObjectMap<LevelInfo.Stage, Boolean> neurosis;


    public GameInfo(){
        currentStage = null;
        neurosis = new ObjectMap<LevelInfo.Stage, Boolean>();
    }

    public void addStageComplete(LevelInfo.Stage stage, boolean contracted){
        neurosis.put(stage, contracted);
    }

    public LevelInfo.Stage nextStage(){
        return LevelInfo.Stage.Toddler; // TODO make this real.
    }
}
