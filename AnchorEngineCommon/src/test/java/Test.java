import top.alwaysready.anchorengine.common.client.ClientVarManager;

public class Test {

    public static void main(String[] args) {
        ClientVarManager cvm = new ClientVarManager();
        cvm.map("aa","wow");
        cvm.map("test1","aa");
        cvm.map("test2","bb");
        cvm.map("index","1");
        System.out.println(cvm.apply("%test{index}%"));
    }
}
