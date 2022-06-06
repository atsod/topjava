package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class MealsUtil {
    public static void main(String[] args) {
        List<Meal> meals = Arrays.asList(
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<MealTo> mealsTo = filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);
        List<MealTo> mealsCycle = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsCycle.forEach(System.out::println);
    }

    public static List<MealTo> filteredByStreams(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesSumByDate = meals.stream()
                .collect(
                        Collectors.groupingBy(Meal::getDate, Collectors.summingInt(Meal::getCalories))
//                      Collectors.toMap(Meal::getDate, Meal::getCalories, Integer::sum)
                );

        return meals.stream()
                .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime))
                .map(meal -> createTo(meal, caloriesSumByDate.get(meal.getDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    private static MealTo createTo(Meal meal, boolean excess) {
        return new MealTo(meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess);
    }

    public static List<MealTo> filteredByCycles(List<Meal> meals,
                                                            LocalTime startTime,
                                                            LocalTime endTime,
                                                            int caloriesPerDay) {
        List<MealTo> resultList = new ArrayList<>();
        //<dayOfMonth, calories>
        Map<LocalDate, Integer> tempMap = new HashMap<>();

        for(Meal meal : meals) {
            LocalDate mealDate = meal.getDateTime().toLocalDate();
            //кладем в мапу новое значение калорий, если их нет. Иначе прибавляем к уже существующему количеству.
            tempMap.put(mealDate,
                    tempMap.getOrDefault(mealDate, 0) + meal.getCalories());
        }

        for(Meal meal : meals) {
            LocalDateTime dateTime = meal.getDateTime();
            if(TimeUtil.isBetweenHalfOpen(dateTime.toLocalTime(), startTime, endTime)) {
                resultList.add(new MealTo(dateTime, meal.getDescription(), meal.getCalories(),
                        tempMap.get(dateTime.toLocalDate()) > caloriesPerDay));
            }
        }
        return resultList;
    }
}
