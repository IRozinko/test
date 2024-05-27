package fintech;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;

public abstract class ClasspathUtils {

    public static String resourceToString(String path) {
        try {
            URL url = Resources.getResource(path);
            String text = Resources.toString(url, Charsets.UTF_8);
            return text;
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to load classpath resource " + path);
        }
    }

    public static byte[] resourceToBytes(String path) {
        try {
            URL url = Resources.getResource(path);
            byte[] bytes = Resources.toByteArray(url);
            return bytes;
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to load classpath resource " + path);
        }
    }
}
