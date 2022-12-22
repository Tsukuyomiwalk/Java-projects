public class Pow {
    public static double myPow1(double x, long n) {
        double v = 1d;
        long n1 = Math.abs(n);
        while(n1 > 0) {
            if((n1 & 1) != 0) {
                v *= x;
            }
            x *= x;
            n1 >>= 1;
        }
        if (n >= 0) {
            return v;
        } else {
            return 1d / v;
        }
    }

    public static void main(String[] args) {
        System.out.println(myPow1(2.00000, -2147483648));
    }
}
