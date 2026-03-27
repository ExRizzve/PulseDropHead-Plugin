package ex.rizzve.pulseDropHead.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TabCompleter implements org.bukkit.command.TabCompleter {
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            completions.addAll(Arrays.asList("givehead", "lang", "reload"));
            return filterCompletions(completions, args[0]);
        }
        
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("givehead")) {
                return getOnlinePlayerNames(args[1]);
            }
            
            if (args[0].equalsIgnoreCase("lang")) {
                completions.addAll(Arrays.asList("ru", "eng"));
                return filterCompletions(completions, args[1]);
            }
        }
        
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("givehead")) {
                return getOnlinePlayerNames(args[2]);
            }
        }
        
        return completions;
    }
    
    private List<String> getOnlinePlayerNames(String prefix) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(prefix.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    private List<String> filterCompletions(List<String> completions, String prefix) {
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(prefix.toLowerCase()))
                .collect(Collectors.toList());
    }
}
