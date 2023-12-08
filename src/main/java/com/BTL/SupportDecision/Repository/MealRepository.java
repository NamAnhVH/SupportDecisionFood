package com.BTL.SupportDecision.Repository;

import com.BTL.SupportDecision.Model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<Meal, Integer> {
    List<Meal> findByDishId(int id);
}
