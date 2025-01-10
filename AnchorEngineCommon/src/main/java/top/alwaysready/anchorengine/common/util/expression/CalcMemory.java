package top.alwaysready.anchorengine.common.util.expression;

import top.alwaysready.anchorengine.common.string.StringReplacer;

import java.util.Optional;
import java.util.Stack;

public class CalcMemory{
    private final Stack<Double> stack;
    private final StringReplacer replacer;

    public CalcMemory(StringReplacer replacer) {
        stack = new Stack<>();
        this.replacer = replacer;
    }

    public void push(double d) {
        stack.push(d);
    }

    public double pop() {
        return stack.pop();
    }

    public Optional<Double> getValue(String key) {
        return replacer.getAsDouble(key);
    }
}
