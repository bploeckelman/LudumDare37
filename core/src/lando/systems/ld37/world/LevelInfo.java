package lando.systems.ld37.world;

/**
 * Created by dsgraham on 12/10/16.
 */
public class LevelInfo {
    public enum Stage {Infancy, Toddler, Primary, Secondary, College, Work, Marriage, Career, Family, Retirement, Death}
    public String mapName = "levels/level-test.tmx";
    public float crackTimer;
    public float crackSpeed;
    public float playerSpeed;
    public float playerFixSpeed;

    public LevelInfo(Stage stage){
        switch (stage){
            case Infancy:
                mapName = "levels/level-nursery.tmx";
                crackTimer = 4f;
                crackSpeed = 2.5f;
//                crackSpeed = 200f;
                playerFixSpeed = 20f;
                playerSpeed = 100;
                break;
            case Toddler:
                mapName = "levels/level-bathroom.tmx";
                crackTimer = 4f;
                crackSpeed = 4f;
//                crackSpeed = 200f;
                playerFixSpeed = 12f;
                playerSpeed = 110;
                break;
            case Primary:
                mapName = "levels/level-playground.tmx";
                crackTimer = 3f;
                crackSpeed = 5f;
//                crackSpeed = 200f;
                playerFixSpeed = 15f;
                playerSpeed = 120;
                break;
            case Secondary:
                mapName = "levels/level-hallway.tmx";
                crackTimer = 3f;
                crackSpeed = 6f;
//                crackSpeed = 200f;
                playerFixSpeed = 21f;
                playerSpeed = 130;
                break;
            case College:
                mapName = "levels/level-dorm.tmx";
                crackTimer = 3f;
                crackSpeed = 7f;
//                crackSpeed = 200f;
                playerFixSpeed = 22f;
                playerSpeed = 140;
                break;
            case Work:
                mapName = "levels/level-office.tmx";
                crackTimer = 3f;
                crackSpeed = 8f;
//                crackSpeed = 200f;
                playerFixSpeed = 25f;
                playerSpeed = 140;
                break;
            case Marriage:
                mapName = "levels/level-bedroom.tmx";
                crackTimer = 3f;
                crackSpeed = 9f;
//                crackSpeed = 200f;
                playerFixSpeed = 30f;
                playerSpeed = 145;
                break;
            case Career:
                mapName = "levels/level-exec-office.tmx";
                crackTimer = 3f;
                crackSpeed = 10f;
//                crackSpeed = 200f;
                playerFixSpeed = 35f;
                playerSpeed = 160;
                break;
            case Family:
                mapName = "levels/level-livingroom.tmx";
                crackTimer = 2.7f;
                crackSpeed = 9f;
//                crackSpeed = 200f;
                playerFixSpeed = 40f;
                playerSpeed = 160;
                break;
            case Retirement:
                mapName = "levels/level-hospital.tmx";
                crackTimer = 3f;
                crackSpeed = 8f;
//                crackSpeed = 200f;
                playerFixSpeed = 30f;
                playerSpeed = 100;
                break;
            default:
                crackTimer = 1f;
                crackSpeed = 5f;
                playerFixSpeed = 10f;
                playerSpeed = 200;
        }
    }
}
