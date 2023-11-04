package com.BTL.SupportDecision.FormData;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserSelectionDto {
    private String desiredType;
    private int desiredTimeMinute;
    private int desiredTimeSecond;
    private int budget;
    private String desiredNutrition;
    private String desiredFat;

    private List<Option> options;

    public UserSelectionDto(List<Option> options){
        this.options = options;
    }

    private List<Option> selectedOptions;
}
