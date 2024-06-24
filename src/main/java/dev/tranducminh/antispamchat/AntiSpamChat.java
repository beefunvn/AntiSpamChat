package dev.tranducminh.antispamchat;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class AntiSpamChat extends JavaPlugin implements Listener {

    private Map<String, Long> lastMessageTimes = new HashMap<>();
    private Map<String, String> lastMessages = new HashMap<>();
    private int delaySeconds;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        loadConfig();
    }

    private void loadConfig() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();

        delaySeconds = config.getInt("delaySeconds", 5);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (isSpamming(player, message)) {
            event.setCancelled(true);
        }
    }

    private boolean isSpamming(Player player, String message) {
        long currentTime = System.currentTimeMillis();
        if (lastMessageTimes.containsKey(player.getName())) {
            long lastMessageTime = lastMessageTimes.get(player.getName());
            if (currentTime - lastMessageTime < delaySeconds * 1000) {
                return true;
            }
        }

        if (lastMessages.containsKey(player.getName())) {
            String lastMessage = lastMessages.get(player.getName());
            if (lastMessage.equals(player.getDisplayName() + message)) {
                return true;
            }
        }

        lastMessageTimes.put(player.getName(), currentTime);
        lastMessages.put(player.getName(), player.getDisplayName() + message);
        return false;
    }
}