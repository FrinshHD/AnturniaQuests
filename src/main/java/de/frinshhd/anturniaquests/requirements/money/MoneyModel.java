package de.frinshhd.anturniaquests.requirements.money;

import com.google.gson.annotations.SerializedName;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import org.bukkit.Bukkit;

import java.util.LinkedHashMap;
import java.util.UUID;

public class MoneyModel extends BasicRequirementModel {

    @SerializedName("amount")
    private double amount = 0.0;

    public MoneyModel(LinkedHashMap<String, Object> map) {
        super(map, "money");

        if (map.containsKey("amount")) {
            this.amount = Double.parseDouble(map.get("amount").toString());
        }
    }

    public double getAmount() {
        return this.amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void removePlayerMoney(UUID uuid) {
        if (Main.getEconomy() != null) {
            Main.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(uuid), amount);
        }
    }
}