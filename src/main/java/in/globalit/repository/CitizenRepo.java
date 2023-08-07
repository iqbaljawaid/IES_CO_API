package in.globalit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.globalit.entity.CitizenApplicationEntity;

public interface CitizenRepo extends JpaRepository<CitizenApplicationEntity, Long> {
	
	public CitizenApplicationEntity findBySsn(String ssn);
	
	public CitizenApplicationEntity findByCaseNum(Long caseNum);

}
