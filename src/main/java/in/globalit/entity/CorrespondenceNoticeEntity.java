package in.globalit.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@EqualsAndHashCode
@Table(name = "CO_Notice_Table")
public class CorrespondenceNoticeEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer noticeId;
	private String pdfUrl;
	private LocalDate printDate;
	private String noticeStatus;
	@CreationTimestamp
	private LocalDate createdDate;
	
	@ManyToOne
	@JoinColumn(name = "case_num")
	private CitizenApplicationEntity application;
	
	@ManyToOne
	@JoinColumn(name = "ed_trace_id")
	private EligibilityDeterminationEntity eligibility;

}
