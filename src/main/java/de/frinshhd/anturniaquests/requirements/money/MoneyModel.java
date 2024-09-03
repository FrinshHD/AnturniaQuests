package de.frinshhd.anturniaquests.requirements.money;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import org.bukkit.Bukkit;

import java.util.LinkedHashMap;
import java.util.UUID;

public class MoneyModel extends BasicRequirementModel {

    @JsonProperty
    private double amount = 0.0;

    @JsonIgnore
    public MoneyModel(LinkedHashMap<String, Object> map) {
        super(map, "money");

        if (map.containsKey("amount")) {
            this.amount = Double.parseDouble(map.get("amount").toString());
        }
    }

    @JsonIgnore
    public double getAmount() {
        return this.amount;
    }

    @JsonIgnore
    public void setAmount(double amount) {
        this.amount = amount;
    }

    @JsonIgnore
    public void removePlayerMoney(UUID uuid) {
        if (Main.getEconomy() != null) {
            Main.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(uuid), amount);
        }
    }
}
