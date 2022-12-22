import java.util.Arrays;

public class TwoSum {
    public static int[] twoSum(int[] nums, int target) {
        int l = 0;
        int r = nums.length - 1;
        while (l < r) {
            int sum = nums[l] + nums[r];
            if (target == sum) {
                return new int[]{l, r};
            }
            if (sum < target) {
                l++;
            } else {
                r--;
            }
        }
        return new int[0];
    }

    public static void main(String[] args) {
      //  System.out.println(Arrays.toString(twoSum(new int[]{2, 7, 11, 15}, 9)));
        System.out.println(Arrays.toString(twoSum(new int[]{2, 7 ,11 ,15}, 9)));
    }
}
