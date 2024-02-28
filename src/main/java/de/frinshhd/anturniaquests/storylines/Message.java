package de.frinshhd.anturniaquests.storylines;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {

    @JsonProperty
    private String message = null;

    @JsonProperty
    private long delay = -1;

}
