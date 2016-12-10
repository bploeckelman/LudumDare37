package lando.systems.ld37.world;

/**
 * Created by dsgraham on 12/10/16.
 */
public class LevelInfo {
    public enum Stage {Infancy, Toddler, Primary, Secondary, College, Work, Marriage, Career, Family, Retirement, Death}
    public float crackTimer;
    public float crackSpeed;
    public float playerSpeed;
    public float playerFixSpeed;

    public LevelInfo(Stage stage){
        switch (stage){
            case Infancy:
                crackTimer = 3f;
                crackSpeed = 2f;
                playerFixSpeed = 10f;
                playerSpeed = 100;
                break;
            case Toddler:
                crackTimer = 2.5f;
                crackSpeed = 2.5f;
                playerFixSpeed = 8f;
                playerSpeed = 110;
                break;
            default:
                crackTimer = 1f;
                crackSpeed = 5f;
                playerFixSpeed = 10f;
                playerSpeed = 200;
        }
    }
}
