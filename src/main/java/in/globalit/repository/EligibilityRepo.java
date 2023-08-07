package in.globalit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.globalit.entity.EligibilityDeterminationEntity;

public interface EligibilityRepo extends JpaRepository<EligibilityDeterminationEntity, Integer> {

	@Query(value = "select * from ed_eligibility_details where case_num= :caseNum ",nativeQuery = true)
	public EligibilityDeterminationEntity findByCaseNum(@Param("caseNum") Long caseNum);
}
