package Client;

import javafx.scene.paint.Color;
import java.util.HashMap;


public class ColorHelper {

  private static HashMap<String, Color> colorMap = new HashMap<String, Color>();
  static {
    colorMap.put("red", Color.ORANGERED);
    colorMap.put("cyan", Color.CYAN);
    colorMap.put("orange", Color.ORANGE);
    colorMap.put("green", Color.GREEN);
    colorMap.put("deeppink", Color.DEEPPINK);
    colorMap.put("lightgoldenrodyellow", Color.LIGHTGOLDENRODYELLOW);
    colorMap.put("yellow", Color.YELLOW);
    colorMap.put("turquoise", Color.TURQUOISE);
  }

  public static Color getColor(String colorString) {
    return colorMap.get(colorString);
  }

  public static String getRandomColorString() {
    Object[] keys = colorMap.keySet().toArray();
    int randomIndex = (int) Math.floor((Math.random() * keys.length));
    return (String) keys[randomIndex];
  }
}
