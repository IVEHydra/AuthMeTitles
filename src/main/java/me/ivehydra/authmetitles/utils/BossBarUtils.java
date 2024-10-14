package me.ivehydra.authmetitles.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BossBarUtils {

    private static final Map<String, Object> dragons = new HashMap<>();

    public static void addBossBar(Player p, String text, float health) throws Exception {
        Object dragon = createDragon(p, text, health);
        dragons.put(p.getName(), dragon);
        sendPacket(p, createSpawnPacket(dragon));
    }

    public static void removeBossBar(Player p) throws Exception {
        String name = p.getName();
        if(!dragons.containsKey(name)) return;

        Class<?> destroyer = Class.forName(getNMSClass("PacketPlayOutEntityDestroy"));
        Object dragon = dragons.get(p.getName());
        int id = (int) dragon.getClass().getMethod("getId").invoke(dragon);

        Constructor<?> constructor = destroyer.getConstructor(int[].class);
        Object packet = constructor.newInstance(new int[]{id});

        sendPacket(p, packet);
        dragons.remove(name);
    }

    public static void teleportBossBar(Player p) throws Exception {
        String name = p.getName();
        if(!dragons.containsKey(name)) return;

        Object dragon = dragons.get(name);
        Method setLocation = dragon.getClass().getMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
        setLocation.invoke(dragon, p.getLocation().getX(), - 100, p.getLocation().getZ(), 0F, 0F);

        sendPacket(p, createSpawnPacket(dragon));
    }

    public static void updateText(Player p, String text) throws Exception {
        String name = p.getName();
        if(!dragons.containsKey(name)) return;

        Object dragon = dragons.get(name);
        Method setCustomName = dragon.getClass().getMethod("setCustomName", String.class);
        setCustomName.invoke(dragon, text);

        sendPacket(p, createSpawnPacket(dragon));
    }

    public static void updateHealth(Player p, float health) throws Exception {
        String name = p.getName();
        if(!dragons.containsKey(name)) return;

        Object dragon = dragons.get(name);
        Method setHealth = dragon.getClass().getMethod("setHealth", float.class);
        setHealth.invoke(dragon, health);

        sendPacket(p, createSpawnPacket(dragon));
    }

    public static void updateBossBar(Player p, String text, float health) throws Exception {
        updateText(p, text);
        updateHealth(p, health);
    }

    public static Object createDragon(Player p, String text, float health) throws Exception {
        Class<?> dragonClass = Class.forName(getNMSClass("EntityEnderDragon"));
        Class<?> worldClass = Class.forName(getNMSClass("World"));
        Object world = p.getWorld().getClass().getMethod("getHandle").invoke(p.getWorld());

        Constructor<?> constructor = dragonClass.getConstructor(worldClass);
        Object dragon = constructor.newInstance(world);

        Method setLocation = dragon.getClass().getMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
        setLocation.invoke(dragon, p.getLocation().getX(), -100, p.getLocation().getZ(), 0F, 0F);

        Method setCustomName = dragon.getClass().getMethod("setCustomName", String.class);
        setCustomName.invoke(dragon, text);

        Method setHealth = dragon.getClass().getMethod("setHealth", float.class);
        setHealth.invoke(dragon, health);

        return dragon;
    }

    public static Set<String> getPlayers() {
        Set<String> players = new HashSet<>();

        for(String name : dragons.keySet()) {
            Player p = Bukkit.getPlayer(name);
            if(p != null && p.isOnline()) players.add(p.getName());
        }

        return players;
    }

    private static Object createSpawnPacket(Object dragon) throws Exception {
        Class<?> packet = Class.forName(getNMSClass("PacketPlayOutSpawnEntityLiving"));
        Constructor<?> constructor = packet.getConstructor(dragon.getClass());
        return constructor.newInstance(dragon);
    }

    private static void sendPacket(Player p, Object packet) throws Exception {
        Object handle = p.getClass().getMethod("getHandle").invoke(p);
        Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
        playerConnection.getClass().getMethod("sendPacket", Class.forName(getNMSClass("Packet"))).invoke(playerConnection, packet);
    }

    private static String getNMSClass(String className) { return "net.minecraft.server.v1_8_R3." + className; }

}
