package me.ivehydra.authmetitles.utils;

import me.ivehydra.authmetitles.AuthMeTitles;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BossBarUtils {

    private static final AuthMeTitles instance = AuthMeTitles.getInstance();
    private static final Map<String, Object> withers = new HashMap<>();
    private static Constructor<?> packetPlayOutSpawnEntityLiving;
    private static Constructor<?> packetPlayOutEntityDestroy;
    private static Constructor<?> entityWither;
    private static Method getId;
    private static Method setLocation;
    private static Method setCustomName;
    private static Method setHealth;
    private static Method setInvisible;
    private static Method getWorldHandle;
    private static Method getPlayerHandle;
    private static Field playerConnection;
    private static Method sendPacket;
    private static Method getDataWatcher;
    private static Constructor<?> packetPlayOutEntityTeleport;
    private static Constructor<?> packetPlayOutEntityMetadata;

    static {
        try {
            packetPlayOutSpawnEntityLiving = getNMSClass("PacketPlayOutSpawnEntityLiving").getConstructor(getNMSClass("EntityLiving"));
            packetPlayOutEntityDestroy = getNMSClass("PacketPlayOutEntityDestroy").getDeclaredConstructor(int[].class);
            entityWither = getNMSClass("EntityWither").getConstructor(getNMSClass("World"));
            getId = getNMSClass("Entity").getMethod("getId");
            setLocation = getNMSClass("EntityWither").getMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
            setCustomName = getNMSClass("EntityWither").getMethod("setCustomName", String.class);
            setHealth = getNMSClass("EntityWither").getMethod("setHealth", float.class);
            setInvisible = getNMSClass("EntityWither").getMethod("setInvisible", boolean.class);
            getWorldHandle = getBukkitClass("CraftWorld").getMethod("getHandle");
            getPlayerHandle = getBukkitClass("entity.CraftPlayer").getMethod("getHandle");
            playerConnection = getNMSClass("EntityPlayer").getDeclaredField("playerConnection");
            sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet"));
            getDataWatcher = getNMSClass("EntityWither").getMethod("getDataWatcher");
            packetPlayOutEntityTeleport = getNMSClass("PacketPlayOutEntityTeleport").getConstructor(getNMSClass("Entity"));
            packetPlayOutEntityMetadata = getNMSClass("PacketPlayOutEntityMetadata").getConstructor(int.class, getNMSClass("DataWatcher"), boolean.class);
        } catch(Exception e) {
            instance.sendLog("[AuthMeTitles]" + ChatColor.RED + " An error occurred while sending packets: " + e.getMessage());
        }
    }

    public static void addWither(Player p, String text, float health) {
        try {
            String name = p.getName();
            if(!withers.containsKey(name)) {
                Object world = getWorldHandle.invoke(p.getWorld());
                Object wither = entityWither.newInstance(world);
                Location loc = p.getLocation().clone();
                setLocation.invoke(wither, loc.getX(), loc.getY(), loc.getZ(), 0F, 0F);
                setCustomName.invoke(wither, text);
                setHealth.invoke(wither, health);
                setInvisible.invoke(wither, true);
                withers.put(name, wither);
                sendPacketToPlayer(p, packetPlayOutSpawnEntityLiving.newInstance(wither));
            }
        } catch(Exception e) {
            instance.sendLog("[AuthMeTitles]" + ChatColor.RED + " An error occurred while trying to spawn a Wither: " + e.getMessage());
        }
    }

    public static void removeWither(Player p) {
        try {
            String name = p.getName();
            if(withers.containsKey(name)) {
                Object wither = withers.remove(name);
                int id = (int) getId.invoke(wither);
                Object destroyer = packetPlayOutEntityDestroy.newInstance(new int[]{id});
                sendPacketToPlayer(p, destroyer);
            }
        } catch(Exception e) {
            instance.sendLog("[AuthMeTitles]" + ChatColor.RED + " An error occurred while trying to destroy a Wither: " + e.getMessage());
        }
    }

    public static void updateText(Player p, String text) {
        try {
            String name = p.getName();
            if(withers.containsKey(name)) {
                Object wither = withers.get(name);
                setCustomName.invoke(wither, text);
                int id = (int) getId.invoke(wither);
                Object packet = packetPlayOutEntityMetadata.newInstance(id, getDataWatcher.invoke(wither), true);
                sendPacketToPlayer(p, packet);
            }
        } catch(Exception e) {
            instance.sendLog("[AuthMeTitles]" + ChatColor.RED + " An error occurred while trying to update the text of a Wither: " + e.getMessage());
        }
    }

    public static void updateHealth(Player p, float health) {
        try {
            String name = p.getName();
            if(withers.containsKey(name)) {
                Object wither = withers.get(name);
                setHealth.invoke(wither, health);
                int id = (int) getId.invoke(wither);
                Object packet = packetPlayOutEntityMetadata.newInstance(id, getDataWatcher.invoke(wither), true);
                sendPacketToPlayer(p, packet);
            }
        } catch(Exception e) {
            instance.sendLog("[AuthMeTitles]" + ChatColor.RED + " An error occurred while trying to update the health of a Wither: " + e.getMessage());
        }
    }

    public static void teleport(Player p) {
        try {
            String name = p.getName();
            if(withers.containsKey(name)) {
                Object wither = withers.get(name);
                Location loc = p.getLocation().clone();
                setLocation.invoke(wither, loc.getX(), loc.getY(), loc.getZ(), 0F, 0F);
                Object packet = packetPlayOutEntityTeleport.newInstance(wither);
                sendPacketToPlayer(p, packet);
            }
        } catch(Exception e) {
            instance.sendLog("[AuthMeTitles]" + ChatColor.RED + " An error occurred while trying to teleport a Wither : " + e.getMessage());
        }
    }

    public static boolean contains(Player p) { return withers.containsKey(p.getName()); }

    private static void sendPacketToPlayer(Player p, Object packet) throws Exception {
        Object playerHandle = getPlayerHandle.invoke(p);
        Object connection = playerConnection.get(playerHandle);
        sendPacket.invoke(connection, packet);
    }

    private static Class<?> getNMSClass(String name) throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        return Class.forName("net.minecraft.server." + version + "." + name);
    }

    private static Class<?> getBukkitClass(String name) throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
    }

}
