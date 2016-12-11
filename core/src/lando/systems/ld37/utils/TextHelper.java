package lando.systems.ld37.utils;

import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by Brian on 12/11/2016.
 */
public class TextHelper {

    private static ObjectMap<String, String> strings;

    public static void load() {
        strings = new ObjectMap<String, String>();

        strings.put("test", "This is a very long message, you'd be surprised just how long it is. No really, I'm not joking here...");
    }

    public static String get(String key) {
        String value = strings.get(key);
        return (value != null) ? value : "Uninitialized text for key (" + key + ")";
    }

}
