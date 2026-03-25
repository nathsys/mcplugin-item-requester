package dev.nathsys.itemrequester;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/* The fulfil command, gets executed by click on request. Usage: /fulfil <request_id> */
public record FulfillCommand(ItemRequester plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player fulfiller)) return true;

        if (args.length != 1) return true;

        int id = Integer.parseInt(args[0]);

        Request request = plugin.getRequests().get(id);

        if (request == null) {
            fulfiller.sendMessage("Request not found, it has likely been fulfilled or was expired.");
            return true;
        }

        Player requester = Bukkit.getPlayer(request.getRequester());

        if (requester == null) {
            fulfiller.sendMessage("Player not found.");
            return true;
        }

        if (fulfiller.getName().equals(requester.getName())) {
            fulfiller.sendMessage("Cannot fulfill your own request.");
            return true;
        }

        ItemStack stack = new ItemStack(request.getMaterial(), request.getAmount());

        if (!fulfiller.getInventory().containsAtLeast(stack, request.getAmount())) {
            fulfiller.sendMessage("You don't have enough items.");
            return true;
        }

        synchronized (request) {
            if (request.isFulfilled()) {
                fulfiller.sendMessage("Already fulfilled.");
                return true;
            }

            plugin.fulfill(id);
        }

        fulfiller.getInventory().removeItem(stack);
        requester.getInventory().addItem(stack);

        fulfiller.sendMessage("Request fulfilled!");
        requester.sendMessage("Your request was fulfilled by " + fulfiller.getName());


        String niceName = request.getMaterial().name().toLowerCase().replace("_", " ");

        Component message = Component.text()
                .append(Component.text(fulfiller.getName(), NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                .append(Component.text(" fulfilled request #" + request.getId() + " for "))
                .append(Component.text(requester.getName(), NamedTextColor.AQUA))
                .append(Component.text(" (" + request.getAmount() + " " + niceName + ")"))
                .build();

        Bukkit.broadcast(message);

        return true;
    }
}
