package fintech;

import java.util.Random;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

public class RandomUtils {

    private static final Random random = new Random();

    public static Long randomId() {
        return System.currentTimeMillis();
    }

    public static String randomDocNumber() {
        return randomNumeric(8);
    }

    public static boolean randomBoolean(float probability) {
        return random.nextFloat() < probability;
    }

}
