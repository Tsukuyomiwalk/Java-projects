import java.util.Objects;
import java.util.Scanner;

public class AVL_tree {

    public static class Node {
        int key;
        int height;
        Node left;
        Node right;
        Node(int key) {
            this.key = key;
        }
    }

    public static Node root;

    public static void RootInsert(int key) {
        root = insert(root, key);
    }

    public static void RootDelete(int key) {
        root = delete(root, key);
    }

    public static int h(Node a) {
        if (a == null) {
            return -1;
        } else {
            return a.height;
        }
    }

    public static void UPDHeight(Node Avl) {
        Avl.height = 1 + checker(Avl.left, Avl.right);
    }

    public static int checker(Node a, Node b) {
        return Math.max(h(a), h(b));
    }

    public static int AvlBalance(Node a) {
        if (a == null) {
            return 0;
        } else {
            return h(a.right) - h(a.left);
        }
    }

    public static boolean Exist(int x) {
        Node y = root;
        while (y != null) {
            if (y.key == x) {
                return true;
            }
            y = y.key < x ? y.right : y.left;
        }
        return false;
    }

    public static String next(int x) {
        Node cur = root;
        Node suc = null;
        while (cur != null) {
            if (cur.key > x) {
                suc = cur;
                cur = cur.left;
            } else {
                cur = cur.right;
            }
        }
        return suc != null ? String.valueOf(suc.key) : "none";
    }

    public static String prev(int x) {
        Node cur = root;
        Node suc = null;
        while (cur != null) {
            if (cur.key < x) {
                suc = cur;
                cur = cur.right;
            } else {
                cur = cur.left;
            }
        }
        return suc != null ? String.valueOf(suc.key) : "none";
    }

    public static Node rotate(Node a, String mode) {
        Node x;
        if (Objects.equals(mode, "r")) {
            x = a.left;
            Node z = x.right;
            x.right = a;
            a.left = z;
        } else {
            x = a.right;
            Node z = x.left;
            x.left = a;
            a.right = z;
        }
        UPDHeight(a);
        UPDHeight(x);
        return x;
    }

    public static Node rebalance(Node a) {
        UPDHeight(a);
        int b = AvlBalance(a);
        if (b <= 1) {
            if (b < -1) {
                if (h(a.left.left) <= h(a.left.right)) {
                    a.left = rotate(a.left, "l");
                }
                a = rotate(a, "r");
            }
        } else {
            if (h(a.right.right) <= h(a.right.left)) {
                a.right = rotate(a.right, "r");
            }
            a = rotate(a, "l");
        }
        return a;
    }

    public static Node insert(Node a, int key) throws IllegalArgumentException {
        if (a == null) return new Node(key);
        else if (a.key > key) {
            a.left = insert(a.left, key);
        } else {
            if (a.key < key) {
                a.right = insert(a.right, key);
            }
        }
        return rebalance(a);
    }

    public static Node delete(Node a, int key) {
        if (a == null) {
            return null;
        }
        else if (a.key > key) {
            a.left = delete(a.left, key);
        } else if (a.key < key) {
            a.right = delete(a.right, key);
        } else {
            if (a.left == null || a.right == null) {
                if (a.left == null) {
                    a = a.right;
                } else {
                    a = a.left;
                }
            } else {
                Node l = a.right;
                while (l.left != null) {
                    l = l.left;
                }
                a.key = l.key;
                a.right = delete(a.right, a.key);
            }
        }
        if (a != null) {
            a = rebalance(a);
        }
        return a;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String operation;
        int key;
        Node Avl = null;
        while (sc.hasNext()) {
            operation = sc.next();
            key = sc.nextInt();
            if (Objects.equals(operation, "insert")) {
                Avl = insert(Avl, key);
                RootInsert(key);
            } else if (Objects.equals(operation, "prev")) {
                String out = prev(key);
                System.out.println(Objects.requireNonNullElse(out, "none"));
            } else if (Objects.equals(operation, "next")) {
                String out = next(key);
                System.out.println(Objects.requireNonNullElse(out, "none"));
            } else if (Objects.equals(operation, "exists")) {
                boolean out = Exist(key);
                if (out) {
                    System.out.println("true");
                } else {
                    System.out.println("false");
                }
            } else if (Objects.equals(operation, "delete")) {
                Avl = delete(Avl, key);
                RootDelete(key);
            }
        }
    }
}
