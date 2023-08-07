package in.globalit.response;

import java.time.LocalDate;

import lombok.Data;

@Data
public class HistoryResponse {

	private Integer edTraceId;
	private String planName;
	private String planStatus;
	private LocalDate eligStartDate;
	private LocalDate eligEndDate;
	private String benefitAmount;
	private LocalDate generatedDate;

}
