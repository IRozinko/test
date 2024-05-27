package fintech.marketing;

import net.agkn.hll.HLL;
import net.agkn.hll.util.NumberUtil;

public class HllUtils {

    public static HLL fromXex(String hex) {
        if (hex == null) {
            return new HLL(25, 5);
        }
        return HLL.fromBytes(NumberUtil.fromHex(hex, 0, hex.length()));
    }

    public static String toXex(HLL hll) {
        final byte[] bytes = hll.toBytes();
        return NumberUtil.toHex(bytes, 0, bytes.length);
    }
}
