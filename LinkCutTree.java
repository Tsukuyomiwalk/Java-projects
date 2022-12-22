public class LinkCutTree {
    static void rotateRight(Node p) {
        Node q = p.parent;
        Node r = q.parent;
        q.normalize();
        p.normalize();
        if ((q.left = p.right) != null) {
            q.left.parent = q;
        }
        p.right = q;
        q.parent = p;
        if ((p.parent = r) != null) {
            if (r.left == q) {
                r.left = p;
            } else if (r.right == q) {
                r.right = p;
            }
        }
        q.update();
    }

    static void rotateLeft(Node p) {
        Node q = p.parent;
        Node r = q.parent;
        q.normalize();
        p.normalize();
        if ((q.right = p.left) != null) q.right.parent = q;
        p.left = q;
        q.parent = p;
        if ((p.parent = r) != null) {
            if (r.left == q) {
                r.left = p;
            } else {
                if (r.right == q) {
                    r.right = p;
                }
            }
        }
        q.update();
    }

    static void splay(Node p) {
        while (!p.isRoot()) {
            Node q = p.parent;
            if (q.isRoot()) {
                if (q.left == p) {
                    rotateRight(p);
                } else {
                    rotateLeft(p);
                }
            } else {
                Node r = q.parent;
                if (r.left == q) {
                    if (q.left == p) {
                        rotateRight(q);
                    } else {
                        rotateLeft(p);
                    }
                    rotateRight(p);
                } else {
                    if (q.right == p) {
                        rotateLeft(q);
                    } else {
                        rotateRight(p);
                    }
                    rotateLeft(p);
                }
            }
        }
        p.update();
    }

    static void expose(Node q) {
        Node root;
        Node p;
        p = q;
        splay(q);
        root = q;
        while (p != null) {
            splay(p);
            p.left = root;
            p.update();
            root = p;
            p = p.parent;
        }
        splay(q);
    }

    static void link(Node p, Node q) {
        expose(p);
        if (p.right != null)
            p.parent = q;
    }

    static int toggle(Node p) {
        expose(p);
        int before = p.on;
        p.flip = !p.flip;
        p.normalize();
        int after = p.on;
        return after - before;
    }

    static int rootId(Node p) {
        expose(p);
        while (p.right != null) p = p.right;
        splay(p);
        return p.id;
    }


    static LinkCutTree.Node lca(Node u, Node v) {
        expose(u);
        expose(v);
        splay(u);
        u.update();
        return u;
    }

    static class Node {
        int son, my_son, on, id;
        boolean flip, my_flip;
        Node left, right, parent;

        Node(int c, int i) {
            id = i;
            son = my_son = c;
            on = 0;
            left = right = parent = null;
            flip = my_flip = false;
        }

        boolean isRoot() {
            return parent == null || (parent.left != this && parent.right != this);
        }


        void normalize() {
            if (flip) {
                flip = false;
                on = son - on;
                my_flip = !my_flip;
                if (left != null) left.flip = !left.flip;
                if (right != null) right.flip = !right.flip;
            }
        }

        void update() {
            son = my_son;
            on = my_flip ? my_son : 0;
            if (left != null) {
                son += left.son;
                on += left.flip ? left.son - left.on : left.on;
            }
            if (right != null) {
                son += right.son;
                on += right.flip ? right.son - right.on : right.on;
            }
        }
    }
}

//        Node r = null;
//        Node p = q;
//        while (p != null) {
//            splay(p);
//            p.left = r;
//            p.update();
//            r = p;
//            p = p.parent;
//        }
//        assert q != null;
//        splay(q);