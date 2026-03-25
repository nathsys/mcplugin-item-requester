package dev.nathsys.itemrequester;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/* Request command, usage: /request <item> <amount>. */
public class RequestCommand implements CommandExecutor {
    public final ItemRequester plugin;

    public RequestCommand(ItemRequester plugin) {
        this.plugin = plugin;
    }

    final int MAX_AMOUNT = 1028;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(commandSender instanceof Player player)) {
            commandSender.sendMessage("Players only.");
            return true;
        }

        if (args.length != 2) {
            player.sendMessage("Usage: /request <item> <amount>");
            return true;
        }

        Material material = Material.matchMaterial(args[0].toUpperCase());
        if (material == null) {
            player.sendMessage("Invalid item.");
            return true;
        }

        int amount = 0;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid amount.");
            return true;
        }

        if (amount < 1 || amount > MAX_AMOUNT) {
            player.sendMessage("Amount should be larger than 1 and not bigger than " + MAX_AMOUNT);
            return true;
        }

        // Make the request.
        Request request = new Request(plugin.getNextId(), player.getUniqueId(), material, amount);
        plugin.getRequests().put(request.getId(), request);

        // Expire the request after the configured amount of minutes.
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            plugin.fulfill(request.getId());
        }, 20L * 60 * plugin.getExpMinutes()); // 5 minutes.

        String niceName = request.getMaterial().name().toLowerCase().replace("_", " ");
        // Broadcast request.
        Component message = Component.text()
                // Player name in green + bold.
                .append(Component.text(player.getName(), NamedTextColor.GREEN)
                        .decorate(TextDecoration.BOLD))
                // The request text.
                .append(Component.text(" requests " + amount + " ", NamedTextColor.WHITE))
                // Item name in aqua.
                .append(Component.text(niceName, NamedTextColor.AQUA))
                .append(Component.text(" "))
                // Clickable "[CLICK TO FULFILL]" in gold + bold.
                .append(Component.text("[CLICK TO FULFILL]", NamedTextColor.GOLD)
                        .decorate(TextDecoration.BOLD)
                        .clickEvent(ClickEvent.runCommand("/fulfill " + request.getId()))
                        .hoverEvent(HoverEvent.showText(Component.text("Click to fulfill this request!", NamedTextColor.YELLOW))))
                .build();
        Bukkit.broadcast(message);
        return true;
    }
}
