package com.BTL.SupportDecision.Controller;

import com.BTL.SupportDecision.FormData.Option;
import com.BTL.SupportDecision.FormData.UserSelectionDto;
import com.BTL.SupportDecision.Model.Dish;
import com.BTL.SupportDecision.Service.DishService;
import com.BTL.SupportDecision.Service.SupportDecisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/")
public class MainController {

    @Autowired
    private SupportDecisionService supportDecisionService;
    @Autowired
    private DishService dishService;

    @GetMapping()
    public String mainPage(Model model){
        List<Option> options = Arrays.asList(
                new Option("Loại món ăn", false),
                new Option("Ngân sách", false),
                new Option("Thời gian chuẩn bị", false),
                new Option("Lượng dinh dưỡng", false),
                new Option("Chế độ ăn uống", false),
                new Option("Bữa ăn", false)
        );
        model.addAttribute("test", new UserSelectionDto(options));
        model.addAttribute("dishes", dishService.getAllDish());
        return "mainPage";
    }
    @PostMapping("/check")
    public String supportDecision(@ModelAttribute UserSelectionDto userSelection, Model model){
        model.addAttribute("suggestDishes", supportDecisionService.getSuggestedDish(userSelection));
        return "suggestPage";
    }
}
