package ex.rizzve.pulseDropHead.commands;

import ex.rizzve.pulseDropHead.PulseDropHead;
import ex.rizzve.pulseDropHead.managers.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class Cmd implements CommandExecutor {
    
    private final PulseDropHead plugin;
    
    public Cmd(PulseDropHead plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(getLang().getMessage("no-permission"));
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage(getLang().getMessage("usage-givehead"));
            sender.sendMessage(getLang().getMessage("usage-lang"));
            return true;
        }
        
        if (args[0].equalsIgnoreCase("lang")) {
            return handleLangCommand(sender, args);
        }
        
        if (args[0].equalsIgnoreCase("reload")) {
            plugin.getConfigManager().loadConfig();
            plugin.getLanguageManager().loadLanguage();
            sender.sendMessage(getLang().getMessage("reload-success"));
            return true;
        }
        
        if (args[0].equalsIgnoreCase("givehead")) {
            return handleGiveHeadCommand(sender, args);
        }
        
        sender.sendMessage(getLang().getMessage("usage-givehead"));
        sender.sendMessage(getLang().getMessage("usage-lang"));
        return true;
    }
    
    private boolean handleLangCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(getLang().getMessage("usage-lang"));
            return true;
        }
        
        String lang = args[1].toLowerCase();
        if (!lang.equals("ru") && !lang.equals("eng")) {
            sender.sendMessage(getLang().getMessage("invalid-language"));
            return true;
        }
        
        plugin.getLanguageManager().setLanguage(lang);
        sender.sendMessage(getLang().getMessage("language-changed").replace("%lang%", lang));
        return true;
    }
    
    private boolean handleGiveHeadCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(getLang().getMessage("usage-givehead"));
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(getLang().getMessage("player-not-found").replace("%player%", args[1]));
            return true;
        }
        
        Player headOwner = Bukkit.getPlayer(args[2]);
        if (headOwner == null) {
            sender.sendMessage(getLang().getMessage("player-not-found").replace("%player%", args[2]));
            return true;
        }
        
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwningPlayer(headOwner);
        
        String displayName = plugin.getConfigManager().getHeadDisplayName()
                .replace("%player%", headOwner.getName());
        meta.setDisplayName(displayName);
        
        skull.setItemMeta(meta);
        
        plugin.getPacketOptimizer().queueItemGive(target, skull);
        
        sender.sendMessage(getLang().getMessage("head-given")
                .replace("%owner%", headOwner.getName())
                .replace("%target%", target.getName()));
        
        return true;
    }
    
    private LanguageManager getLang() {
        return plugin.getLanguageManager();
    }
}
