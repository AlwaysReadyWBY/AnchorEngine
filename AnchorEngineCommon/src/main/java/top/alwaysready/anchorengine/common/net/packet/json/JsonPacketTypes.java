package top.alwaysready.anchorengine.common.net.packet.json;

import static top.alwaysready.anchorengine.common.util.AnchorUtils.toKey;
public interface JsonPacketTypes {
    interface C2S{
        String DEBUG = toKey("debug");
        String ACTION = toKey("action");
        String QUERY = toKey("query");
        String HANDSHAKE = toKey("handshake");
    }

    interface S2C{
        String DEBUG = toKey("debug");
        String ACTION = toKey("action");
        String PUSH = toKey("push");
        String SET_SCREEN = toKey("set_screen");
        String CLOSE_SCREEN = toKey("close_screen");
        String CALL_EVENT = toKey("call_event");
    }
}
