package hu.opau.voltvault.logic;

public class Condition {
    public String leftOperand;
    public Operator operator;
    public Object rightOperand;

    public enum Operator {
        EQ, NE, LT, GT, LE, GE, AC, LIKE, UNDEFINED
    }

    public Condition(String leftOperand, Operator operator, Object rightOperand) {
        this.leftOperand = leftOperand;
        this.operator = operator;
        this.rightOperand = rightOperand;
    }

    public static Condition fromString(String str) {

        if (str == null) {
            throw new IllegalArgumentException();
        }
        String[] split = str.split(" ");
        if (split.length != 3) {
            throw new IllegalArgumentException();
        }

        Operator operator = Operator.UNDEFINED;
        switch (split[1]) {
            case "==":
                operator = Operator.EQ;
                break;
            case "!=":
                operator = Operator.NE;
                break;
        }

        return new Condition(split[0], operator, split[2]);
    }
}
