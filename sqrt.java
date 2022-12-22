public class sqrt {
    public static int mySqrt(int x) {
        long l = 0;
        long r = x;
        long root = 0;
        while (l <= r)
        {
            long m = (l + r) / 2;
            if (m * m <= x)
            {
                root = m;
                l = m + 1 ;
            }
            else
            {
                r = m - 1;
            }
        }
        return (int)root;
    }
    public static void main(String[] args) {
        System.out.println(mySqrt(2147395599));
    }
}
