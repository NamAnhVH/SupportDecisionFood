package com.BTL.SupportDecision.FormData;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Option {
    private String name;
    private boolean checked;
    private int rank;

    public Option(String name, boolean checked, int rank){
        this.name = name;
        this.checked = checked;
        this.rank = rank;
    }

    public Option(){

    }
}
