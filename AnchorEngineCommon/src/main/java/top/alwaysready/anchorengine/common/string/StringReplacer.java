package top.alwaysready.anchorengine.common.string;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class StringReplacer {
    public static String colorize(String plain){
        return plain.replaceAll("&", "§").replaceAll("§§", "&");
    }

    public static List<String> colorize(List<String> lines) {
        return lines.stream().map(StringReplacer::colorize).collect(Collectors.toList());
    }

    private StringReplacer parent;
    private final Map<String, Supplier<String>> remap = new Hashtable<>();

    public void setParent(StringReplacer parent) {
        this.parent = parent;
    }

    public void clear(){
        remap.clear();
    }

    public void remove(String entry){
        remap.remove(entry);
    }

    public StringReplacer map(String from,String to){
        return map(from,()->to);
    }

    public StringReplacer map(String from,Supplier<String> calc){
        remap.put(from,calc);
        return this;
    }

    protected Optional<String> getMapped(String from){
        Supplier<String> calc = remap.get(from);
        String to = calc ==null? null: calc.get();
        if(to!=null) return Optional.of(to);
        return parent == null? Optional.empty():parent.getMapped(from);
    }

    public Optional<String> getReplaced(String text){
        if(text == null) return Optional.empty();
        StringBuilder builder = new StringBuilder(text);
        Deque<Integer> stack = new ConcurrentLinkedDeque<>();
        int end = builder.indexOf("}");
        int start = builder.indexOf("{");
        while(end >= 0) {
            while(start>=0 && start<end){
                stack.push(start);
                start = builder.indexOf("{",start+1);
            }
            if(stack.isEmpty()) {
                end = builder.indexOf("}",end+1);
                continue;
            }
            int matched = stack.pop();
            String var = getMapped(builder.substring(matched + 1, end)).orElse(null);
            if(var!=null) {
                builder.replace(matched,end+1,var);
                end = builder.indexOf("}", start+var.length());
                continue;
            }
            end = builder.indexOf("}",end+1);
        }
        return getMapped(builder.toString());
    }

    public String apply(Object text){
        StringBuilder builder = new StringBuilder(String.valueOf(text));
        int start = 0;
        while (start < builder.length()) {
            start = builder.indexOf("%", start);
            if (start < 0) break;
            int end = builder.indexOf("%", start + 1);
            if (end < 0) break;
            if (end < start + 2) {
                builder.replace(start,end+1,"%");
                continue;
            }
            String from = builder.substring(start + 1, end);
            Optional<String> opt = getReplaced(from);
            if(opt.isEmpty()){
                start = end+1;
            } else {
                builder.replace(start,end+1,opt.get());
            }
        }
        return builder.toString();
    }

    public Optional<Boolean> getAsBoolean(String text){
        return Optional.ofNullable(text).map(t -> t.equalsIgnoreCase("true"));
    }

    public Optional<Integer> getAsHexInt(String text) {
        return StringParser.HEX_INT.parseString(apply(text), Integer.class);
    }

    public Optional<Integer> getAsInt(String text){
        return getAsDouble(text).map(Double::intValue);
    }

    public Optional<Double> getAsDouble(String text){
        if(text == null) return Optional.empty();
        return StringParser.DOUBLE.parseString(apply(text),Double.class);
    }

    public String apply(Object text,Object... args){
        StringBuilder builder = new StringBuilder(String.valueOf(text));
        int start = 0;
        while (start < builder.length()) {
            start = builder.indexOf("%", start);
            if (start < 0) break;
            int end = builder.indexOf("%", start + 1);
            if (end < 0) break;
            if (end < start + 2) {
                start = end + 1;
                continue;
            }
            String from = builder.substring(start + 1, end);
            try {
                int index = Integer.parseInt(from);
                if (index >= 0 && index < args.length) {
                    builder.replace(start, end + 1, String.valueOf(args[index]));
                } else {
                    start = end + 1;
                }
            } catch (NumberFormatException e) {
                Optional<String> opt = getReplaced(from);
                if(opt.isEmpty()){
                    start = end+1;
                } else {
                    builder.replace(start,end+1,opt.get());
                }
            }
        }
        return builder.toString();
    }

    public String applyColored(Object text,Object... args) {
        return colorize(apply(text,args));
    }

    public List<String> applyList(List<?> lines){
        return lines.stream().map(this::apply).collect(Collectors.toList());
    }

    public List<String> applyColoredList(List<String> lines) {
        return lines.stream().map(this::apply).map(StringReplacer::colorize).collect(Collectors.toList());
    }

    public StringReplacer createChild() {
        StringReplacer replacer = new StringReplacer();
        replacer.setParent(this);
        return replacer;
    }
}
