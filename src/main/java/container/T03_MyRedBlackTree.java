package container;


/***
 * 重写红黑树
 */
public class T03_MyRedBlackTree {
    TreeNode root;

    static class TreeNode {
        String key;
        String value;
        TreeNode left;
        TreeNode right;
        TreeNode parent;
        boolean red;

    }

    /***
     * 左旋
     * @param root
     * @param p
     * @return
     */
    static TreeNode spinLeft(TreeNode root, TreeNode p) {

        TreeNode pp, r, rl;//分别为p点父节点，p点右节点，p点右节点的左节点
        if (p != null && (r = p.right) != null) {
            //p点的右节点指向rl，并且当rl节点不为空时，将它的父节点指向p
            if ((rl = p.right = r.left) != null) {
                rl.parent = p;
            }
            //右节点的父节点指向p点父节点
            //当此时父节点为空时，表示当前为根节点,根节点颜色为黑色
            if ((pp = r.parent = p.parent) == null) {
                (root = r).red = false;
            } else if (pp.left == p) {
                pp.left = r;
            } else {
                pp.right = r;
            }
            //r的左节点指向p，p的父节点指向r
            r.left = p;
            p.parent = r;
        }
        return root;
    }

    /***
     * 右旋
     * @param root
     * @param p
     * @return
     */
    static TreeNode spinRight(TreeNode root, TreeNode p) {
        //分别为p点父节点，p点左节点，p点左节点的右节点
        TreeNode pp, l, lr;
        if (p != null && (l = p.left) != null) {
            //p点的左节点指向rl，并且当rl节点不为空时，将它的父节点指向p
            if ((lr = p.left = l.right) != null) {
                lr.parent = p;
            }
            //右节点的父节点指向p点父节点
            //当此时父节点为空时，表示当前为根节点,根节点颜色为黑色
            if ((pp = l.parent = p.parent) == null) {
                (root = l).red = false;
            } else if (pp.left == p) {
                pp.left = l;
            } else {
                pp.right = l;
            }
            //r的左节点指向p，p的父节点指向r
            l.right = p;
            p.parent = l;
        }
        return root;
    }

    /***
     * 插入新节点
     * @param root 树的根节点
     * @param i 插入节点
     * @return
     */
    static TreeNode insert(TreeNode root, TreeNode i) {
        TreeNode c = root;//当前节点
        TreeNode p;
        if (root == null) {
            (root = i).red = false;
            return root;
        }
        //查找到插入位置
        do {
            p = c;
            if (c.key.hashCode() == i.key.hashCode()) {
                c.value = i.value;
                break;
            }
            if (i.key.hashCode() < c.key.hashCode()) {
                if ((c = c.left) == null) {
                    p.left = i;
                    i.parent = p;
                    break;
                }
            } else if (i.key.hashCode() > c.key.hashCode()) {
                if ((c = c.right) == null) {
                    p.right = i;
                    i.parent = p;
                    break;
                }
            }

        } while (true);
        insertBalance(root, i);
        return root;
    }

    /***
     * 插入自平衡
     * @param root
     * @param i
     * @return
     */
    static TreeNode insertBalance(TreeNode root, TreeNode i) {
        TreeNode p, pp, u;
        i.red = true;
        //当前为空树，直接返回
        if ((p = i.parent) == null) {
            i.red = false;
            return i;
        }
        //父节点为黑色，直接返回,或者没有祖父节点
        if (p.red == false || (pp = p.parent) == null) {
            return root;
        }
        // 父节点为红色
        // 若存在叔叔节点，且为红色
        if ((u = (pp.left == p) ? pp.right : pp.left).red) {
            u.red = p.red = false;
            pp.red = true;
            i = pp;
            root = insertBalance(root, i);
        }
        //存在叔叔节点，且为黑色或者空
        else if ((u = (pp.left == p) ? pp.right : pp.left).red == false || u == null) {
            //父节点是祖父节点的左节点
            if (p == pp.left) {
                if (i == p.left) {
                    pp.red = true;
                    p.red = false;
                    spinRight(root,pp);
                }
                else {
                    spinLeft(root,p);
                    i=p;
                    root=insertBalance(root,i);
                }
            }else {
                //父节点是祖父节点的额右节点
                if(i==p.right){
                    pp.red=true;
                    p.red=false;
                    root=spinLeft(root,pp);
                }else {
                    spinRight(root,p);
                    i=p;
                    root=insertBalance(root,i);
                }
            }
        }
        return root;
    }


    /**
     * 删除节点
     * @param root
     * @param key
     * @returnr
     */
    static void remove(TreeNode root,String key){

        TreeNode currentNode=root;
        TreeNode deleteNode, removeNode=null;
        //查找删除节点
        while (true){
            if(currentNode==null)
                break;
            if(currentNode.key==key){
                removeNode=root;
            }else if(currentNode.key.hashCode()>key.hashCode()){
                currentNode=currentNode.left;
            }else {
                currentNode=currentNode.right;
            }
        }
        //未找到删除节点
        if(removeNode==null)
            return;

        //情形一：无子节点
        if(removeNode.left==null&&removeNode.right==null)
            deleteNode=removeNode;
        //情形二：只有一个子节点,用子节点替换当前节点，转为删除子节点
        else if(removeNode.left==null||removeNode.right==null){
            if(removeNode.left!=null){
                removeNode.key=removeNode.left.key;
                removeNode.value=removeNode.left.value;
                remove(root,removeNode.left.key);
                deleteNode=removeNode.left;
            }else {
                removeNode.key=removeNode.right.key;
                removeNode.value=removeNode.right.value;
                remove(root,removeNode.right.key);
                deleteNode=removeNode.right;
            }
        }
        //情形三：有两个子节点，找到当前节点后继，用后继节点替换，删除后继节点
        else {
            TreeNode successNode=removeNode.right;
            while (successNode.left!=null){
                    successNode=successNode.left;
            }
            removeNode.key=successNode.key;
            removeNode.value=successNode.value;
            remove(root,successNode.key);
            deleteNode=successNode;
        }
        removeBalance(root,deleteNode);

    }

    /**
     * 删除自平衡
     * @param root
     * @param r
     * @return
     */
    static  TreeNode removeBalance(TreeNode root,TreeNode r){

        return root;
    }

}
