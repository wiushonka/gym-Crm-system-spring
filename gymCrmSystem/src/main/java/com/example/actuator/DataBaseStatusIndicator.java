package com.example.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DataBaseStatusIndicator implements HealthIndicator {

    private final DataSource dataSource;

    public DataBaseStatusIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        try(Connection connection = dataSource.getConnection()){
            if(connection.isValid(2)){
                return Health.up().withDetail("dataBaseStatus", "Up").build();
            }else{
                return Health.down().withDetail("dataBaseStatus", "Down").build();
            }
        }catch (SQLException ex){
            return Health.down(ex).withDetail("database","Connection error").build();
        }
    }
}
