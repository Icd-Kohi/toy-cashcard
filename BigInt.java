import java.math.*;
import java.math.BigInteger;
class BigInt{
    public static String BIG_NUMBER = "1231213242342525";
    public static int MAX_VALUE = 2147483646;
    public static byte ZERO = 0;

    public static void main(String[] args) {
        BigInteger num = new BigInteger(BIG_NUMBER);
        // Check if num is between Integer.MIN_VALUE and 2147483646 (exclusive)
        if (num.compareTo(BigInteger.valueOf(MAX_VALUE)) < ZERO) {
            int theta = num.intValueExact(); // Safe conversion
            System.out.println(theta);
        } else {
            System.out.println(num);
        }
    }
}
