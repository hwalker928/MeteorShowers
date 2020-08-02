package com.belmu.meteorshowers.commands;

import com.belmu.meteorshowers.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class setAlert implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

        if (sender instanceof Player) {

            Player player = (Player) sender;
            FileConfiguration cfg = Main.getInstance().getConfig();

            if (cmd.getName().equalsIgnoreCase("setalert")) {

                if(args.length == 0) {

                    player.sendMessage(Main.prefix + " §cWrong Usage ! Try </setalert (alert)>");

                } else {

                    StringBuilder a = new StringBuilder();

                    for (int i = 1; i < args.length; i++) {

                        a.append(args[i].replace("&", "§") + " ");

                    }

                    String alert = a.toString().trim();

                    cfg.set("alert-message", alert);
                    player.sendMessage(Main.prefix + " §7Set the alert to §f'" + alert + "§f'");

                    Main.getInstance().saveConfig();

                }

            }

        }

        return false;
    }

}
