package top.alwaysready.anchorengine.common.string;

import top.alwaysready.anchorengine.common.util.AnchorUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class StringParser {
    public static final StringParser BYTE = new StringParser(Byte::parseByte);
    public static final StringParser SHORT = new StringParser(Short::parseShort);
    public static final StringParser INT = new StringParser(Integer::parseInt);
    public static final StringParser LONG = new StringParser(Long::parseLong);
    public static final StringParser FLOAT  = new StringParser(Float::parseFloat);
    public static final StringParser DOUBLE = new StringParser(Double::parseDouble);
    public static final StringParser BOOLEAN = new StringParser(str -> str==null?null: str.equalsIgnoreCase("true"));
    public static final StringParser STRING = new StringParser(str -> str);
    public static final StringParser CHARACTER = new StringParser(str -> str.charAt(0));
    public static final StringParser INVALID = new StringParser(any -> null);
    public static final StringParser HEX_INT = new StringParser(str -> Integer.parseInt(str,16));

    public static Optional<StringParser> ofType(Class<?> type){
        if(type.isEnum()){
            return Optional.of(new StringParser(str -> Enum.valueOf(type.asSubclass(Enum.class), str)));
        }
        return AnchorUtils.getService(StringParserManager.class).flatMap(man -> man.getParser(type));
    }

    private final Function<String,?> parser;

    public StringParser(Function<String, ?> parser) {
        this.parser = parser;
    }

    public Object parseString(String str){
        try {
            return this.parser.apply(str);
        } catch (Exception e){
            return null;
        }
    }

    public <T> Optional<T> parseString(String str,Class<T> type){
        return Optional.ofNullable(parseString(str))
                .filter(type::isInstance)
                .map(type::cast);
    }

    public <T> Collection<T> parseStringList(Collection<T> to,List<String> stringList) {
        stringList.forEach(string -> to.add((T) parseString(string)));
        return to;
    }
}
