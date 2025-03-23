package hu.opau.voltvault.logic;

public class Condition {
    public String leftOperand;
    public Operator operator;
    public Object rightOperand;

    public enum Operator {
        EQ, NE, LT, GT, LE, GE, AC
    }

    public Condition(String leftOperand, Operator operator, Object rightOperand) {
        this.leftOperand = leftOperand;
        this.operator = operator;
        this.rightOperand = rightOperand;
    }
}
