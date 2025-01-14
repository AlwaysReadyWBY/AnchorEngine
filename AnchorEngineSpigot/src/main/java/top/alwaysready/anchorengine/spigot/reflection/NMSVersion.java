package top.alwaysready.anchorengine.spigot.reflection;

import org.bukkit.Server;

public class NMSVersion {

    public static NMSVersion auto(Server server){
        String craftPackage = server.getClass().getPackage().getName();
        if(!craftPackage.contains("v1_")){
            return new NMSVersion().resolve(server.getBukkitVersion());
        }
        return new NMSVersion().resolveLegacy(craftPackage.substring(craftPackage.lastIndexOf('.')+1));
    }

    private boolean def = true;
    private boolean legacy;
    private int[] numbers;

    public NMSVersion resolve(String ver) {
        if(ver.equals("default")) return this;
        def = false;
        if (ver.startsWith("v")) {
            resolveLegacy(ver);
            return this;
        }
        //TODO resolve version
        setLegacy(false);
        setNumbers(new int[0]);
        return this;
    }

    public NMSVersion resolveLegacy(String ver) {
        def = false;
        setLegacy(true);
        String[] strings = ver.split("_");
        setNumbers(new int[]{
                Integer.parseInt(strings[0].substring(1)),
                Integer.parseInt(strings[1]),
                Integer.parseInt(strings[2].substring(1))
        });
        return this;
    }

    public boolean isLegacy() {
        return legacy;
    }

    public void setLegacy(boolean legacy) {
        this.legacy = legacy;
    }

    public void setNumbers(int[] numbers) {
        this.numbers = numbers;
    }

    public int[] getNumbers() {
        return numbers;
    }

    public boolean isDefault() {
        return def;
    }

    @Override
    public String toString() {
        return "v"+numbers[0]+"_"+numbers[1]+"_R"+numbers[2];
    }

    public boolean isOver(NMSVersion another) {
        if (another == null || another.isDefault()) return true;
        if (isLegacy() && !another.isLegacy()) return false;
        if (!isLegacy() && another.isLegacy()) return true;
        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i] > another.numbers[i]) {
                return true;
            } else if (numbers[i] < another.numbers[i]) {
                return false;
            }
        }
        return true;
    }
}
