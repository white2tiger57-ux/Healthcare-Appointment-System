package com.healthcare.appointmentsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class HealthcareAppointmentSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthcareAppointmentSystemApplication.class, args);
    }
}
