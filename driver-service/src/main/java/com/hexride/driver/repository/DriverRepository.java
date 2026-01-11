package com.hexride.driver.repository;

import com.hexride.common.enums.DriverStatus;
import com.hexride.driver.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, String> {
    
    List<Driver> findByStatus(DriverStatus status);
    
    @Query("SELECT d FROM Driver d WHERE d.id IN :ids AND d.status = :status")
    List<Driver> findByIdsAndStatus(@Param("ids") List<String> ids, @Param("status") DriverStatus status);
    
    boolean existsByLicenseNumber(String licenseNumber);
}
