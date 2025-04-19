package org.site.BoU.Repositories;

import org.site.BoU.Entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRep extends JpaRepository<Course, Long> {
    Optional<Course> findByFromCurrencyAndToCurrency(String fromCurrency, String toCurrency);
}

