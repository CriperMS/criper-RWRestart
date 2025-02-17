package ru.criperms.criperrwrestart.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class RestartReplaceCMD implements Listener{

    @EventHandler
    public void onPlayercmdPreProcess(PlayerCommandPreprocessEvent e){
        String message = e.getMessage();
        String[] parts = message.split(" ");

        if (parts.length >= 1 && parts[0].equalsIgnoreCase("restart")){
            String newMessage = message.replaceFirst("restart", "crestart");
            e.setMessage(message);
        }
    }
}
