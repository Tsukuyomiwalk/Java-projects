import java.util.*;

public class E {
    static int[] nums = new int[]{3, 2, 4};

    public static void main(String[] args) {
        System.out.println(frequencySort("tree"));
    }

    public static int[] runningSum(int[] nums) {
        int[] nums1 = new int[nums.length];
        nums1[0] = nums[0];
        for (int i = 0; i < nums.length - 1; i++) {
            nums1[i + 1] = nums1[i] + nums[i + 1];
        }
        return nums1;
    }

    public static List<String> fizzBuzz(int n) {
        List<String> a = new ArrayList<String>();
        for (int i = 1; i <= n; i++) {
            String str = "";
            if (i % 3 == 0 && i % 5 == 0) {
                str += "FizzBuzz";
            } else if (i % 3 == 0) {
                str += "Fizz";
            } else if (i % 5 == 0) {
                str += "Buzz";
            } else {
                str += i;
            }
            a.add(str);
        }
        return a;
    }

    public static int numberOfSteps(int num) {
        int counter = 0;
        while (num != 0) {
            if (num % 2 == 0) {
                num /= 2;
            } else {
                num -= 1;
            }
            counter++;
        }
        return counter;
    }

    public static ListNode middleNode(ListNode head) {
        int cnt = 0;
        ArrayList<ListNode> a = new ArrayList<>();
        while (head != null) {
            a.add(head);
            cnt++;
            head = head.next;
        }
        return a.get(cnt / 2);
    }

    public static class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }

    public static boolean canConstruct(String ransomNote, String magazine) {
        HashMap<Character, Integer> abc = new HashMap<>();
        for (int i = 0; i < magazine.length(); i++) {
            int isEmpty = abc.getOrDefault(magazine.charAt(i), 0);
            abc.put(magazine.charAt(i), isEmpty + 1);
        }

        for (int i = 0; i < ransomNote.length(); i++) {
            int isEmpty1 = abc.getOrDefault(ransomNote.charAt(i), 0);
            if (isEmpty1 == 0) {
                return false;
            }
            abc.put(ransomNote.charAt(i), isEmpty1 - 1);
        }
        return true;
    }

    public static int[] twoSum(int[] nums, int target) {
        int second = 1;
        for (int first = 0; first < nums.length; ++first) {
            while (second != nums.length -1 && nums[second] + nums[first] != target) {
                second++;
            }
            return new int[]{first, second};
        }
        return new int[]{0, 0};
    }


    public static String frequencySort(String s) {
        TreeMap<Character, Integer> set= new TreeMap<>();
        int max = -1;
        for (int i = 0; i < s.length(); i++) {
            int cnt = set.getOrDefault(s.charAt(i), 0);
            cnt++;
            if (cnt > max) max = cnt;
            set.put(s.charAt(i),cnt);
        }
        StringBuilder out = new StringBuilder();
        while (!set.isEmpty()) {
            char c = set.firstKey();
            int i = set.get(c);
            System.out.println(set);
            out.append(String.valueOf(c).repeat(Math.max(0, i)));
            set.remove(c);
        }
        return String.valueOf(out);
    }
}