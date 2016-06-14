/**
 * Created with IntelliJ IDEA.
 * User: dreamer
 * Date: 28.04.13
 * Time: 5:47
 * To change this template use File | Settings | File Templates.
 */
public class ID3Attr {
    String name;
    String leftValue;  //0
    String rightValue;    //1
    int id;

    public ID3Attr(String name, String leftValue, String rightValue, int id) {
        this.name = name;
        this.leftValue = leftValue;
        this.rightValue = rightValue;
        this.id = id;
    }
}
