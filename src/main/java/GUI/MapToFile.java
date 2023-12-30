package GUI;

import simulation.statistics.SimSettings;
import java.io.*;
import java.util.Map;

public class MapToFile {

    public static void serializeMapToFile(Map<String, SimSettings> dataMap, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(dataMap);
        } catch (IOException e) {
            System.out.printf("Serializing error: %s\n", e);
        }
    }

    public static Map<String, SimSettings> deserializeMapFromFile(String filename) {
        Map<String, SimSettings> map;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            Object object = ois.readObject();
            map = (Map<String, SimSettings>) object;
        } catch (IOException | ClassNotFoundException e) {
            System.out.printf("Deserializing error: %s\n", e);
            throw new RuntimeException(e);
        }
        return map;
    }

}
