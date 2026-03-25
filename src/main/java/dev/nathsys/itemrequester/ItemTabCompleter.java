package dev.nathsys.itemrequester;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemTabCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) { // first argument = item
            String prefix = args[0].toUpperCase();

            for (Material mat : Material.values()) {
                if (mat.isItem() && !mat.isAir() && mat.name().startsWith(prefix)) {
                    completions.add(mat.name());
                }
            }
        }

        return completions;
    }
}
