package in.globalit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.globalit.entity.PlanEntity;

public interface PlanRepo extends JpaRepository<PlanEntity, Integer> {
	
	public List<PlanEntity> findByPlanName(String planName);

}
