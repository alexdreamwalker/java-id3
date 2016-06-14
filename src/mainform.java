import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created with IntelliJ IDEA.
 * User: dreamer
 * Date: 25.04.13
 * Time: 16:18
 * To change this template use File | Settings | File Templates.
 */
public class mainform {
    private JTextArea textArea1;
    private JButton makeItDirtyButton;
    private JTextArea textArea2;
    private JPanel panel;
    private JButton makeMeRuleItButton;
    ID3Parser parser;

    public static void main(String[] args) {
        JFrame frame = new JFrame("mainform");
        frame.setContentPane(new mainform().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public mainform() {
        makeItDirtyButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String input = textArea1.getText();
                parser = new ID3Parser(input, textArea2);
                String output = parser.id3tree.printTree();
                textArea2.setText(output);
            }
        });
        makeMeRuleItButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parser.passTree(parser.id3tree);
            }
        });
    }
}
