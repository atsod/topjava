package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;

import java.util.Collection;

import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

public class MealRestController {

    protected final Logger logger = LoggerFactory.getLogger(MealRestController.class);

    @Autowired
    private MealService service;

    public Meal save(Meal meal) {
        return service.save(meal);
    }

    public boolean delete(int id) {
        return service.delete(authUserId());
    }

    public Meal get(int id) {
        return service.get(authUserId());
    }

    public Collection<Meal> getAll() {
        return service.getAll();
    }

}