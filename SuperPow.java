import java.math.BigInteger;

public class SuperPow {
    public static long myPow1(long x, long n) {
        long v = 1;
        long n1 = n;
        while (n1 > 0) {
            if ((n1 & 1) != 0) {
                v *= x;
            }
            x =  x * x  % 1337;
            n1 >>= 1;
        }
        return v;
    }

    public static int superPow(int a, int[] b) {
        String s = "";
        for (int j : b) {
            s+= j;
        }
        long h = myPow1(a, Long.parseLong(s));
        return (int) h % 1337;
    }

    public static void main(String[] args) {
        int[] a = new int[]{2,0,0};
        long h = superPow(2147483647,a);
        System.out.println(h);
    }
}
