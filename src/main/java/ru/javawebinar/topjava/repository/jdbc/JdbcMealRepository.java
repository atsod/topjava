package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JdbcMealRepository implements MealRepository {

    private static final BeanPropertyRowMapper<Meal> ROW_MAPPER = BeanPropertyRowMapper.newInstance(Meal.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcMealRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if(get(meal.getId(), userId) == null) {
            jdbcTemplate.update("insert into meals values (?, ?, ?, ?, ?)",
                    meal.getId(), meal.getDateTime(), meal.getDescription(), meal.getCalories(), userId);
        } else jdbcTemplate.update("update meals set id = ?1, date_time = ?2, description = ?3, calories = ?4, user_id = ?5 where user_id = ?6",
                meal.getId(), meal.getDateTime(), meal.getDescription(), meal.getCalories(), userId, userId);
        return get(meal.getId(), userId);
    }

    @Override
    public boolean delete(int id, int userId) {
        return jdbcTemplate.update("delete from meals where id = ?1 and user_id = ?2", id, userId) > 0;
    }

    @Override
    public Meal get(int id, int userId) {
        return jdbcTemplate
                    .query("select * from meals where id = ?1 and user_id = ?2", new Object[]{id, userId}, ROW_MAPPER)
                    .get(0);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return jdbcTemplate.query("select * from meals", ROW_MAPPER);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return jdbcTemplate
                .query("select * from meals where date_time > ?1 and date_time < ?2 and user_id = ?3",
                        new Object[]{startDateTime, endDateTime, userId}, ROW_MAPPER);
    }
}
