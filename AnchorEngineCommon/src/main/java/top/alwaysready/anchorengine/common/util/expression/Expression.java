package top.alwaysready.anchorengine.common.util.expression;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import top.alwaysready.anchorengine.common.string.StringReplacer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@JsonAdapter(Expression.Adapter.class)
public class Expression {
    public static final Expression ZERO = new Expression();

    private String exp;
    private transient boolean isCompiled;
    private transient final List<Mark> operator = new ArrayList<>();

    public Expression() {
        this("0");
    }

    public Expression(String exp) {
        this.exp = exp;
        isCompiled = false;
    }

    public void setExp(String exp) {
        this.exp = exp;
        isCompiled = false;
    }

    private void drain(Stack<Mark> op, int tier) {
        Mark mark;
        while(!op.isEmpty()) {
            mark = op.peek();
            if (mark.getTier() < tier) {
                if(tier==0) op.pop();
                break;
            }
            op.pop();
            operator.add(mark);
        }
    }

    public double calc(StringReplacer replacer) {
        if(!isCompiled()) compile();
        CalcMemory mem=new CalcMemory(replacer);
        for (Mark m : operator) {
            m.calc(mem);
        }
        return mem.pop();
    }

    public boolean isCompiled(){
        return isCompiled;
    }

    public void compile() {
        boolean isNum = false;
        boolean isVar = false;
        String exp = getExpString();
        int l = exp.length();
        int s = 0;
        Stack<Mark> op = new Stack<>();
        for(int i = 0; i < l; ++i) {
            char ch = exp.charAt(i);
            if(isVar){
                if(ch!='>') continue;
                operator.add(Mark.var(exp.substring(s,i)));
                isVar = false;
                isNum = false;
                continue;
            }
            if(!isNum && ch == '-'){
                drain(op,Mark.REV.getTier());
                op.push(Mark.REV);
                continue;
            }
            if(isNum) {
                if(ch == '<') {
                    s = i+1;
                    isVar = true;
                    continue;
                }
                if(ch == '.' || (ch>='0' && ch<='9')) continue;
                String str = exp.substring(s, i);
                try {
                    operator.add(Mark.number(Double.parseDouble(str)));
                    isNum = false;
                } catch (NumberFormatException e){
                    throw(new IllegalArgumentException("Illegal Number \""+str+"\" in expression "+exp,e));
                }
            }
            if(ch >= '0' && ch <= '9'){
                s = i;
                isNum = true;
                continue;
            }
            if(ch == '<'){
                s = i+1;
                isNum = true;
                isVar = true;
                continue;
            }
            Mark m = Mark.of(ch);
            if(m == Mark.UNKNOWN){
                throw(new IllegalArgumentException("Illegal Expression: "+getExpString()));
            }
            if(m != Mark.LBR) drain(op,m.getTier());
            if(m != Mark.RBR) op.push(m);
        }
        if (isNum) {
            try {
                operator.add(Mark.number(Double.parseDouble(exp.substring(s, l))));
            } catch (NumberFormatException e){
                throw(new IllegalArgumentException("Illegal Expression: "+getExpString(),e));
            }
        }
        drain(op, 0);
        isCompiled = true;
    }

    public String getExpString() {
        return exp;
    }

    @Override
    public String toString() {
        return getExpString();
    }

    public static class Adapter implements JsonSerializer<Expression>, JsonDeserializer<Expression>{
        @Override
        public Expression deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if(json==null || !json.isJsonPrimitive()) return ZERO;
            JsonPrimitive prim = json.getAsJsonPrimitive();
            if(!prim.isString()) return ZERO;
            return new Expression(prim.getAsString());
        }

        @Override
        public JsonElement serialize(Expression src, Type typeOfSrc, JsonSerializationContext context) {
            if(src==null) return JsonNull.INSTANCE;
            return new JsonPrimitive(src.getExpString());
        }
    }
}