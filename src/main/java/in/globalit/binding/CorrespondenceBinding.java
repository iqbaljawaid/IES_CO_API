package in.globalit.binding;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CorrespondenceBinding {
	
	private String citizenName;
	private String citizenSSN;
	private Long caseNum;
	private Integer edTraceId;
	private String planName;
	private String planStatus;
	private LocalDate startDate;
	private LocalDate endDate;
	private String benefitAmount;
	private String denialReason;

}
