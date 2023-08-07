package in.globalit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.globalit.entity.CorrespondenceNoticeEntity;

public interface CorrespondenceNoticeRepo extends JpaRepository<CorrespondenceNoticeEntity, Integer> {
	
	@Query(value = "select * from co_notice_table where case_num= :caseNum ",nativeQuery = true)
	public CorrespondenceNoticeEntity findByCaseNum(@Param("caseNum") Long caseNum);

}
