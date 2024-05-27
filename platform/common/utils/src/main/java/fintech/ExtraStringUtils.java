package fintech;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class ExtraStringUtils {

    public static boolean commaSeparatedListContainsValueIgnoringCaseAndWhitespaces(String list, String valueToFind) {
        if (isBlank(list) || isBlank(valueToFind)) {
            return false;
        }
        List<String> values = Splitter.on(",").trimResults().splitToList(list.replaceAll("\\s", "").toLowerCase());
        return values.contains(valueToFind.replaceAll("\\s", "").toLowerCase());
    }

    public static List<String> splitCommaSeparatedList(String list) {
        if (StringUtils.isBlank(list)) {
            return new ArrayList<>();
        }
        List<String> values = Splitter.on(",").trimResults().splitToList(list);
        return values;
    }

    // https://stackoverflow.com/questions/955110/similarity-string-comparison-in-java/16018452#16018452
    public static double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0; /* both strings are zero length */
        }
        return (longerLength - StringUtils.getLevenshteinDistance(longer, shorter)) / (double) longerLength;
    }
}
