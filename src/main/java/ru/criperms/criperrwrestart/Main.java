package ru.criperms.criperrwrestart;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.criperms.criperrwrestart.listeners.RestartReplaceCMD;

public class Main extends JavaPlugin implements CommandExecutor {
    private BossBar bossBar;

    private int countdownTime = 300;

    private BukkitRunnable countdownTask;

    public void onEnable() {
        getLogger().info(" ");
        getLogger().info(" ");
        getLogger().info(" ");
        getLogger().info("§e░█████╗░██████╗░██╗██████╗░███████╗██████╗░███╗░░░███╗░██████╗");
        getLogger().info("§e██╔══██╗██╔══██╗██║██╔══██╗██╔════╝██╔══██╗████╗░████║██╔════╝");
        getLogger().info("§e██║░░╚═╝██████╔╝██║██████╔╝█████╗░░██████╔╝██╔████╔██║╚█████╗░");
        getLogger().info("§e██║░░██╗██╔══██╗██║██╔═══╝░██╔══╝░░██╔══██╗██║╚██╔╝██║░╚═══██╗");
        getLogger().info("§e╚█████╔╝██║░░██║██║██║░░░░░███████╗██║░░██║██║░╚═╝░██║██████╔╝");
        getLogger().info("§e░╚════╝░╚═╝░░╚═╝╚═╝╚═╝░░░░░╚══════╝╚═╝░░╚═╝╚═╝░░░░░╚═╝╚═════╝░");
        getLogger().info(" ");
        getLogger().info(" §bПлагин написан кодером: Swozz");
        getLogger().info(" §bПлагин был переписан кодером: CriperMS");
        getLogger().info(" §aБлагодарю за использование плагина!");
        getLogger().info(" §aСвязаться со мной можно в Телеграм: @agentdoubbl");
        getLogger().info(" ");
        getLogger().info(" ");
        getLogger().info(" ");
        getLogger().info(" ");
        saveDefaultConfig();
        PluginCommand restartCommand = getCommand("crestart");
        assert restartCommand != null;
        restartCommand.setExecutor(this);
        Bukkit.getPluginManager().registerEvents(new RestartReplaceCMD(), this);
        this.bossBar = Bukkit.createBossBar(getConfigMessage("bossBarTitle", this.countdownTime), BarColor.RED, BarStyle.SOLID, new org.bukkit.boss.BarFlag[0]);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("crestart")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("stop")) {
                stopCountdown(sender);
            } else if (sender.hasPermission("rwrestart.use")) {
                if (args.length > 0) {
                    try {
                        int time = Integer.parseInt(args[0]);
                        if (time < 1 || time > 300) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',  getConfig().getString("messages.invalidTime")));
                            return true;
                        }
                        this.countdownTime = time;
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.invalidTime")));
                        return true;
                    }
                }
                startCountdown();
                for (Player player : Bukkit.getOnlinePlayers())
                    this.bossBar.addPlayer(player);
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.noPermission")));
            }
            return true;
        }
        return false;
    }

    private void startCountdown() {
        if (this.countdownTask != null && !this.countdownTask.isCancelled())
            this.countdownTask.cancel();
        this.countdownTask = new BukkitRunnable() {
            private int timeLeft = Main.this.countdownTime;

            public void run() {
                if (this.timeLeft == 300 || this.timeLeft == 60 || this.timeLeft <= 30) {
                    String message = Main.this.getConfigMessage("restartCountdown", this.timeLeft);
                    Bukkit.broadcastMessage(message);
                    String title = Main.this.getConfigMessage("restartTitle", this.timeLeft);
                    String subtitle = Main.this.getConfigMessage("restartSubtitle", this.timeLeft);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendTitle(title, subtitle, 10, 70, 20);
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                    }
                }
                Main.this.bossBar.setTitle(Main.this.getConfigMessage("bossBarTitle", this.timeLeft));
                Main.this.bossBar.setProgress(this.timeLeft / 300.0D);
                this.timeLeft--;
                if (this.timeLeft < 0) {
                    cancel();
                    for (Player player : Bukkit.getOnlinePlayers())
                        Main.this.bossBar.removePlayer(player);
                    Bukkit.shutdown();
                }
            }
        };
        this.countdownTask.runTaskTimer((Plugin)this, 0L, 20L);
    }

    private void stopCountdown(CommandSender sender) {
        if (this.countdownTask != null && !this.countdownTask.isCancelled()) {
            this.countdownTask.cancel();
            for (Player player : Bukkit.getOnlinePlayers())
                this.bossBar.removePlayer(player);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',  getConfig().getString("messages.countdownStop")));
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.noActiveRestart")));
        }
    }

    private String getConfigMessage(String path, int time) {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages." + path)).replace("%time%", String.valueOf(time));
    }
}