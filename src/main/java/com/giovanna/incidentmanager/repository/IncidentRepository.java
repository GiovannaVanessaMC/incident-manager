package com.giovanna.incidentmanager.repository;

import com.giovanna.incidentmanager.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentRepository extends JpaRepository<Incident, Long> {

}