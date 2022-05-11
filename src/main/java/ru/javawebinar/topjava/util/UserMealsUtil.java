package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.chrono.ChronoLocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals,
                                                            LocalTime startTime,
                                                            LocalTime endTime,
                                                            int caloriesPerDay) {
        List<UserMealWithExcess> resultList = new ArrayList<>();
        //<dayOfMonth, calories>
        Map<Integer, Integer> summaryDailyCalories = new HashMap<>();
        List<UserMeal> mealsFilteredList = new ArrayList<>();

        for(UserMeal meal : meals) {
            int day = meal.getDateTime().getDayOfMonth();
            //кладем в мапу новое значение калорий, если их нет. Иначе прибавляем к уже существующему количеству.
            summaryDailyCalories.put(day,
                    summaryDailyCalories.getOrDefault(day, 0) + meal.getCalories());

            if(meal.getDateTime().toLocalTime().isAfter(startTime) &&
                    meal.getDateTime().toLocalTime().isBefore(endTime)) {
                mealsFilteredList.add(meal);
            }
        }

        for(UserMeal meal : mealsFilteredList) {
            resultList.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(),
                    summaryDailyCalories.get(meal.getDateTime().getDayOfMonth()) > caloriesPerDay));
        }

        return resultList;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals,
                                                             LocalTime startTime,
                                                             LocalTime endTime,
                                                             int caloriesPerDay) {
        //<Day, Calories>
        Map<Integer, Integer> dayCaloriesMap = new HashMap<>();

        meals = meals.stream()
                .peek(m -> dayCaloriesMap.put(m.getDateTime().getDayOfMonth(),
                        dayCaloriesMap.getOrDefault(m.getDateTime().getDayOfMonth(), 0) + m.getCalories()))
                .collect(Collectors.toList());

        return meals.stream().filter(m -> m.getDateTime().toLocalTime().isAfter(startTime)
                        && m.getDateTime().toLocalTime().isBefore(endTime))
                .map(m -> new UserMealWithExcess(m.getDateTime(), m.getDescription(), m.getCalories(),
                        dayCaloriesMap.get(m.getDateTime().getDayOfMonth()) > caloriesPerDay))
                .collect(Collectors.toList());
    }
}
