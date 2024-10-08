package top.alwaysready.anchorengine.common.string;

import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

public class StringParserManager {
    private final Map<Class<?>,StringParser> parserMap = new Hashtable<>();

    public StringParserManager(){
        registerParser(byte.class,StringParser.BYTE);
        registerParser(Byte.class,StringParser.BYTE);
        registerParser(short.class,StringParser.SHORT);
        registerParser(Short.class,StringParser.SHORT);
        registerParser(int.class,StringParser.INT);
        registerParser(Integer.class,StringParser.INT);
        registerParser(long.class,StringParser.LONG);
        registerParser(Long.class,StringParser.LONG);
        registerParser(float.class,StringParser.FLOAT);
        registerParser(Float.class,StringParser.FLOAT);
        registerParser(double.class,StringParser.DOUBLE);
        registerParser(Double.class,StringParser.DOUBLE);
        registerParser(boolean.class,StringParser.BOOLEAN);
        registerParser(Boolean.class,StringParser.BOOLEAN);
        registerParser(String.class,StringParser.STRING);
        registerParser(char.class,StringParser.CHARACTER);
        registerParser(Character.class,StringParser.CHARACTER);
    }

    public void registerParser(Class<?> clazz,StringParser parser){
        parserMap.put(clazz,parser);
    }

    public Optional<StringParser> getParser(Class<?> clazz){
        return Optional.ofNullable(parserMap.get(clazz));
    }
}
