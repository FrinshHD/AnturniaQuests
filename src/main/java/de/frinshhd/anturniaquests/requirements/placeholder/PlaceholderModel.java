package de.frinshhd.anturniaquests.requirements.placeholder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;

import java.util.LinkedHashMap;

public class PlaceholderModel extends BasicRequirementModel {

    @JsonProperty
    private String comparisonType = "";
    @JsonProperty
    private String comparisonOperator = "";
    @JsonProperty
    private String comparison = "";
    @JsonProperty
    private String placeholder = "";
    @JsonProperty
    private String name = "";

    @JsonIgnore
    public PlaceholderModel(LinkedHashMap<String, Object> map) {
        super(map, "placeholders");

        if (map.containsKey("comparisonType")) {
            this.comparisonType = map.get("comparisonType").toString();
        }

        if (map.containsKey("comparisonOperator")) {
            this.comparisonOperator = map.get("comparisonOperator").toString();
        }

        if (map.containsKey("comparison")) {
            this.comparison = map.get("comparison").toString();
        }

        if (map.containsKey("placeholder")) {
            this.placeholder = map.get("placeholder").toString();
        }

        if (map.containsKey("name")) {
            this.name = map.get("name").toString();
        }
    }


    @JsonIgnore
    public void setComparisonType(ComparisonType comparisonType) {
        this.comparisonType = comparisonType.toString();
    }

    @JsonIgnore
    public ComparisonType getComparisonType() {
        try {
            return ComparisonType.valueOf(comparisonType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ComparisonType.INT;
        }
    }

    @JsonIgnore
    public void setComparison(String comparison) {
        this.comparison = comparison;
    }

    @JsonIgnore
    public String getComparison() {
        return comparison;
    }

    @JsonIgnore
    public void setComparisonOperator(ComparionOperator comparisonOperator) {
        this.comparisonOperator = comparisonOperator.toString();
    }

    @JsonIgnore
    public ComparionOperator getComparisonOperator() {
        try {
            return ComparionOperator.valueOf(comparisonOperator.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ComparionOperator.EQUAL;
        }
    }

    @JsonIgnore
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    @JsonIgnore
    public String getPlaceholder() {
        return placeholder;
    }

    @JsonIgnore
    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public String getName() {
        if (name == null || name.isEmpty()) return getPlaceholder();
        return name;
    }

}

