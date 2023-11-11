package com.BTL.SupportDecision.FormData;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Option {
    private String name;
    private boolean checked;
    private int position;
    private int rank;

    public Option(String name, boolean checked){
        this.name = name;
        this.checked = checked;
    }

    public Option(){

    }
}
