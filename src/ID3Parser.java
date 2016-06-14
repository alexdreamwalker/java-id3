import javax.swing.*;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: dreamer
 * Date: 25.04.13
 * Time: 16:48
 * To change this template use File | Settings | File Templates.
 */
public class ID3Parser {
    String input;
    ID3Tree id3tree;
    private JTextArea textArea2;

    int numAttr;
    int numCases;

    ArrayList<ID3Attr> attrs;
    ID3Case[][] cases;

    int goal;

    public ID3Parser(String input, JTextArea area) {
        this.input = input;
        this.textArea2 = area;
        parseInput();
        //printCases();
        makeTree(goal, 1);
    }

    public void printCases(ID3Case[][] cases) {
        for(int i = 0; i < cases.length; i++) {
            System.out.println(i + " : " );
            for(int j = 0; j < numAttr; j++) {
                System.out.print("\t" + j + " : " +cases[i][j].value);
            }
            System.out.println();
        }
    }

    public int getAttrId(String attr) {
        int result = 0;
        for(int i = 0; i < numAttr; i++) {
            if(attrs.get(i).name.equals(attr)) return i;
        }
        return result;
    }

    public int getAttrValue(int idAttr, String attrValue) {
        ID3Attr attr = attrs.get(idAttr);
        if(attr.rightValue.equals(attrValue)) return 1;
        else return 0;
    }

    public void parseInput() {
        int index = 0;
        String[] data = input.split("\n");
        numAttr = Integer.valueOf(data[index]);
        index++;
        attrs = new ArrayList<ID3Attr>();
        for(int j = 0; j < numAttr; j++) {
            String attrSting = data[index];
            String[] attrData = attrSting.split("\t");
            ID3Attr attr = new ID3Attr(attrData[0], attrData[2], attrData[1], j);
            attrs.add(attr);
            index++;
        }
        String goalString = data[index];
        index++;
        goal = getAttrId(goalString);
        numCases = Integer.valueOf(data[index]);
        index++;
        cases = new ID3Case[numCases][numAttr];
        for(int i = 0; i < numCases; i++) {
            String caseString =  data[index];
            String[] caseAttrs = caseString.split("\t");
            for(int j = 0; j < numAttr; j++) {
                String attrData =  caseAttrs[j];
                String[] attrValues = attrData.split("=");
                int attrId = getAttrId(attrValues[0]);
                int attrValue = getAttrValue(attrId, attrValues[1]);

                ID3Case mycase = new ID3Case(attrId, attrValue);
                cases[i][j] = mycase;
            }
            index++;
        }
    }

    public double log2(double value) {
        return Math.log(value)/Math.log(2);
    }

    public double calculateH(ID3Case[][] cases, int attr, int value) {
        double n = cases.length;
        if(n == 0) return 0;
        double m = 0;
        for(int i = 0; i < n; i++) {
            if(cases[i][attr].value == value) m++;
        }
        if(m == n || m == 0) return 0;
        double result = -(m / n) * log2(m / n)  - ((n - m) / n) * log2((n - m)/n);
        return result;
    }

    public ID3Case[][] sortedCases(ID3Case[][] mycase, int attr, int value) {
        ArrayList<ID3Case[]> list = new ArrayList<ID3Case[]>();
        for(int i = 0; i < mycase.length; i++) {
            ID3Case[] mycases = mycase[i];
            if(mycases[attr].value == value) list.add(mycases);
        }
        ID3Case[][] result = new ID3Case[list.size()][numAttr];
        for(int i = 0; i < list.size(); i++)
            for(int j = 0; j < numAttr; j++)
                result[i][j] = list.get(i)[j];
        return result;
    }

    public double calculateGain(ID3Case[][] cases, int attr, int goal, int goalValue) {
        double n = cases.length;
        double m = 0;
        for(int i = 0; i < n ; i++) {
            if(cases[i][attr].value == 1) m++;
        }
        ID3Case[][] leftCases = sortedCases(cases, attr, 0);
        ID3Case[][] rightCases = sortedCases(cases, attr, 1);

        if(attr == 0) {
            System.out.println(calculateH(cases, goal, goalValue) + " - (" + m + "/" + n + ") * ");
        }

        return calculateH(cases, goal, goalValue) - (m / n) * calculateH(rightCases, goal, goalValue) - ((n - m) / n) * calculateH(leftCases, goal, goalValue);
    }

    public ID3Tree ID3(ID3Case[][] a, String prefix, int s, ArrayList<ID3Attr> q, int value) {
        ID3Tree tree = new ID3Tree("start", prefix);

        if(sortedCases(a, s, 1).length == a.length) {
            tree = new ID3Tree("1", prefix);
            return tree;
        }
        else if(sortedCases(a, s, 1).length == 0) {
            tree = new ID3Tree("0", prefix);
            return tree;
        }

        if(q.size() == 0) {
            if(sortedCases(a, s, 1).length >= a.length / 2) {
                tree = new ID3Tree("1", prefix);
                return tree;
            }  else {
                tree = new ID3Tree("0", prefix);
                return tree;
            }
        }

        double maxGain = 0;
        int selectedAttr = 0;
        int selectedNumber = 0;
        for(int i = 0; i < q.size(); i++) {
            ID3Attr attr = q.get(i);
            double gain = calculateGain(a, attr.id, s, value);
            System.out.println("Calculated gain for " + attr.name + " = " + gain);
            if(gain > maxGain) {
                maxGain = gain;
                selectedAttr = attr.id;
                selectedNumber = i;
            }
        }

        tree = new ID3Tree(attrs.get(selectedAttr).name, prefix);

        //left part
        ID3Tree leftTree = new ID3Tree(attrs.get(selectedAttr).leftValue, prefix);
        if(sortedCases(a, attrs.get(selectedAttr).id, 0).length == 0) {
            if(sortedCases(a, attrs.get(selectedAttr).id, 0).length >= a.length / 2) {
                leftTree = new ID3Tree("1", attrs.get(selectedAttr).leftValue);
            }  else {
                leftTree = new ID3Tree("0", attrs.get(selectedAttr).leftValue);
            }
        } else {
            ArrayList<ID3Attr> newAttrs = new ArrayList<ID3Attr>(q);
            newAttrs.remove(selectedNumber);
            leftTree = ID3(sortedCases(a, attrs.get(selectedAttr).id, 0), attrs.get(selectedAttr).leftValue, s, newAttrs, value);
        }
        tree.addLeft(leftTree);

        //right part
        ID3Tree rightTree = new ID3Tree(attrs.get(selectedAttr).rightValue, prefix);
        if(sortedCases(a, attrs.get(selectedAttr).id, 1).length == 0) {
            if(sortedCases(a, attrs.get(selectedAttr).id, 1).length >= a.length / 2) {
                rightTree = new ID3Tree("1", attrs.get(selectedAttr).rightValue);
            }  else {
                rightTree = new ID3Tree("0", attrs.get(selectedAttr).rightValue);
            }
        } else {
            ArrayList<ID3Attr> newAttrs = new ArrayList<ID3Attr>(q);
            newAttrs.remove(selectedNumber);
            rightTree = ID3(sortedCases(a, attrs.get(selectedAttr).id, 1), attrs.get(selectedAttr).rightValue, s, newAttrs, value);
        }
        tree.addRight(rightTree);

        return tree;
    }

    public void makeTree(int attrId, int attrValue) {
        ArrayList<ID3Attr> newAttrs = new ArrayList<ID3Attr>(attrs);
        newAttrs.remove(attrId);
        id3tree = ID3(cases, "start", attrId, newAttrs, attrValue);
        id3tree.isActive = true;
    }

    public void passTree(ID3Tree tree) {
        JFrame frame = new JFrame();
        tree.isActive = true;
        textArea2.setText(id3tree.printTree());
        if(tree.value.equals("0") || tree.value.equals("1")) {
            tree.isActive = false;
            String answer = attrs.get(goal).name + " : ";
            if (tree.value.equals("0")) answer += attrs.get(goal).leftValue;
            else answer += attrs.get(goal).rightValue;
            JOptionPane.showMessageDialog(frame,
                    answer);
        } else {
            String question = tree.value;
            String leftVar = attrs.get(getAttrId(question)).leftValue;
            String rightVar = attrs.get(getAttrId(question)).rightValue;
            Object[] options = {leftVar, rightVar};
            int n = JOptionPane.showOptionDialog(frame,
                    question,
                    "A Silly Question",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[1]);
            switch (n) {
                case 0 : tree.isActive = false; passTree(tree.left); break;
                case 1 : tree.isActive = false; passTree(tree.right); break;
            }
        }
    }
}
