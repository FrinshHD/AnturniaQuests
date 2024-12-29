package de.frinshhd.anturniaquests.storylines;

import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("message")
    private String message = null;

    @SerializedName("delay")
    private long delay = -1;

}