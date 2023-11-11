package com.BTL.SupportDecision.Controller;

import com.BTL.SupportDecision.FormData.UserSelectionDto;
import com.BTL.SupportDecision.Model.Dish;
import com.BTL.SupportDecision.Service.SupportDecisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    @Autowired
    private SupportDecisionService supportDecisionService;

    @PostMapping("/check")
    public List<Dish> supportDecision(Model model, @ModelAttribute UserSelectionDto userSelection){
//        model.addAttribute("test", supportDecisionService.getSuggestedDish(userSelection));
        return supportDecisionService.getSuggestedDish(userSelection);
    }
}
