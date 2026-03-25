package dev.nathsys.itemrequester;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ItemRequester extends JavaPlugin {

    private final Map<Integer, Request> requests = new HashMap<>();
    private int nextId = 1;

    private int expMinutes = getConfig().getInt("request-expiration-minutes");

    public Map<Integer, Request> getRequests() {
        return requests;
    }

    public void fulfill(int id) {
        requests.get(id).setFulfilled(true);
        requests.remove(id);
    }

    public int getNextId() {
        return nextId++;
    }

    @Override
    public void onEnable() {
        getLogger().info("Plugin enabled!");
        saveDefaultConfig();
        Objects.requireNonNull(getCommand("request")).setExecutor(new RequestCommand(this));
        Objects.requireNonNull(getCommand("fulfill")).setExecutor(new FulfillCommand(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled!");
    }

    public int getExpMinutes() {
        return expMinutes;
    }
}