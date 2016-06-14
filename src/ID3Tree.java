/**
 * Created with IntelliJ IDEA.
 * User: dreamer
 * Date: 25.04.13
 * Time: 16:21
 * To change this template use File | Settings | File Templates.
 */
public class ID3Tree {
    ID3Tree left; //1
    ID3Tree right; //0
    String value;
    int level;
    String prefix;
    boolean isActive;


    public ID3Tree(String value, String prefix) {
        left = null;
        right = null;
        this.value = value;
        this.level = 0;
        this.prefix = prefix;
        this.isActive = false;
    }

    public void addRight(ID3Tree tree) {
        tree.level = this.level + 1;
        this.right = tree;
    }

    public void addLeft(ID3Tree tree) {
        tree.level = this.level + 1;
        this.left = tree;
    }

    public int getHeight(ID3Tree pointer) {
        int h = 0;
        if(pointer.right == null && pointer.left == null) return 1;
        if(pointer.right != null) {
            h = getHeight(pointer.right);
        }
        if(pointer.left != null) {
            int hh = getHeight(pointer.left);
            if(hh > h) h = hh;
        }
        return h;
    }

    public String printTree() {
        String result = "";
        String tabs = "";
        for(int i = 0; i < this.level; i++) tabs += "\t";
        if(this.left != null ) result += tabs + this.left.printTree() + "\n";
        if(this.isActive) result += tabs + "< if " + prefix + " : " + value + ">\n";
        else result += tabs + " if " + prefix + " : " + value + "\n";
        if(this.right != null) result += tabs + this.right.printTree();
        return result;
    }

}
