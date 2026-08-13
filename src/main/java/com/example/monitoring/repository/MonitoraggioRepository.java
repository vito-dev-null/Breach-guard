package com.example.monitoring.repository;

import com.example.monitoring.model.SystemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonitoraggioRepository extends JpaRepository<SystemStatus, Long> {
}
