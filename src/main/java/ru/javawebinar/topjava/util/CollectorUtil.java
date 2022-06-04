package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class CollectorUtil implements Collector<Meal, ArrayList<Meal>, ArrayList<MealTo>> {
    private final Map<Integer, Integer> dailyCalories = new HashMap<>();
    private final Integer maxCalories;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public CollectorUtil(Integer maxCalories, LocalTime startTime, LocalTime endTime) {
        this.maxCalories = maxCalories;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public Supplier<ArrayList<Meal>> supplier() {
        return ArrayList::new;
    }

    @Override
    public BiConsumer<ArrayList<Meal>, Meal> accumulator() {
        return ArrayList::add;
    }

    @Override
    public BinaryOperator<ArrayList<Meal>> combiner() {
        return (l, r) -> {l.addAll(r); return l;};
    }

    @Override
    public Function<ArrayList<Meal>, ArrayList<MealTo>> finisher() {
        return u -> u.stream()
                .peek(m -> dailyCalories.put(m.getDateTime().getDayOfMonth(),
                        dailyCalories.getOrDefault(m.getDateTime().getDayOfMonth(), 0) + m.getCalories()))
                .collect(Collectors.toList()).stream()
                .filter(m -> TimeUtil.isBetweenHalfOpen(m.getDateTime().toLocalTime(), startTime, endTime))
                .map(m -> new MealTo(m.getDateTime(), m.getDescription(), m.getCalories(),
                        dailyCalories.get(m.getDateTime().getDayOfMonth()) > maxCalories))
                .collect(Collectors.toCollection(ArrayList::new));
    }
    /*У меня в финишере за один день аггрегируются.
    Разделяю на дни, потом склеиваю... под условие подходит*/

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.CONCURRENT);
    }
}
