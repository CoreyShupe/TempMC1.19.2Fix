package io.github.coreyshupe.tempfix1192;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Listener {
    private final static Field MC_CONN_FIELD;
    private final static Method WRITE_METHOD;
    private final static Constructor<?> SD_PACKET_CONTRUCTOR;

    static {
        try {
            Class<?> connectedPlayerClass = Class.forName("com.velocitypowered.proxy.connection.client.ConnectedPlayer");
            MC_CONN_FIELD = connectedPlayerClass.getDeclaredField("connection");
            WRITE_METHOD = MC_CONN_FIELD.getType().getDeclaredMethod("write", Object.class);
            Class<?> sdClass = Class.forName("com.velocitypowered.proxy.protocol.packet.ServerData");
            SD_PACKET_CONTRUCTOR = sdClass.getConstructor();
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Subscribe
    public void onPostConnect(PostLoginEvent event) {
        try {
            var connectedPlayer = event.getPlayer();
            var connection = MC_CONN_FIELD.get(connectedPlayer);
            var packet = SD_PACKET_CONTRUCTOR.newInstance();
            WRITE_METHOD.invoke(connection, packet);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }
}