import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class A {
    static int n, m;
    static ArrayList<ArrayList<Integer>> gr = new ArrayList<>();
    static ArrayList<Integer> cycle = new ArrayList<>();
    static ArrayList<Integer> p = new ArrayList<>();
    static ArrayList<Boolean> used = new ArrayList<>();
    static ArrayList<Integer> ans = new ArrayList<>();
    static ArrayList<Integer> o = new ArrayList<>();
    static int cycle_st;
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        o.add(0);
        LinkedList<Integer> deque = new LinkedList<>();
        n = sc.nextInt();
        m = sc.nextInt();
        for (int i = 0; i < n; i++) {
            used.add(false);
            p.add(-1);
            cycle.add(0);
            gr.add(o);
        }
        for (int i = 0; i < m; ++i) {
            int a, b;
            a = sc.nextInt();
            b = sc.nextInt();
            gr.get(a - 1).add(b - 1);
        }
        cycle_st = -1;
        for (int i = 0; i < n; ++i)
            if (dfsacycling(i))
                break;
        if (cycle_st != -1)
            System.out.println(-1);
        else {
            topological_sort();
            for (int an : ans) {
                System.out.print(an + 1 + " ");
            }
        }
    }


    public static void topological_sort() {
        for (int i = 0; i < n; ++i)
            used.set(i, false);
        ans.clear();
        for (int i = 0; i < n; ++i)
            if (!used.get(i))
                dfs(i);
        for (int j = 0; j < ans.size() / 2; j++) {
            int tmp = ans.get(j);
            ans.set(j, ans.get(ans.size() - 1 - j));
            ans.set(ans.size() - 1 - j, tmp);
        }
    }

    public static void dfs(int v) {
        used.set(v, true);
        for (int to : gr.get(v)) {
            if (!used.get(to))
                dfs(to);
        }
        ans.add(v);
    }

    public static boolean dfsacycling(int v) {
        cycle.set(v, 1);
        for (int i = 0; i < gr.get(v).size(); ++i) {
            int to = gr.get(v).get(i);
            if (cycle.get(to) != 0) {
                if (cycle.get(to) == 1) {
                    cycle_st = to;
                    return true;
                }
            } else {
                p.set(to, v);
                if (!dfsacycling(to)) continue;
                return true;
            }
        }
        cycle.set(v, 2);
        return false;
    }
}