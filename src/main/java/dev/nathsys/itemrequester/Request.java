package dev.nathsys.itemrequester;

import org.bukkit.Material;
import java.util.UUID;

public class Request {
    private final int id;
    private final UUID requester;
    private final Material material;
    private final int amount;
    private boolean fulfilled = false;

    public Request(int id, UUID requester, Material material, int amount) {
        this.id = id;
        this.requester = requester;
        this.material = material;
        this.amount = amount;
    }

    public int getId() { return id; }
    public UUID getRequester() { return requester; }
    public Material getMaterial() { return material; }
    public int getAmount() { return amount; }

    public boolean isFulfilled() { return fulfilled; }
    public void setFulfilled(boolean fulfilled) { this.fulfilled = fulfilled; }
}