package in.globalit.rest;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.lowagie.text.DocumentException;

import in.globalit.binding.CorrespondenceBinding;
import in.globalit.response.HistoryResponse;
import in.globalit.response.PendingResponse;
import in.globalit.service.CorrespondenceNoticeService;

@RestController
public class CorrespondenceNoticeRest {

	@Autowired
	private CorrespondenceNoticeService service;

	@GetMapping("/generateNotice/{caseNum}")
	public CorrespondenceBinding generateNotice(@PathVariable("caseNum") Long caseNum)
			throws DocumentException, IOException {

		return service.generateNoticeWithPending(caseNum);
	}
	
	@GetMapping("/sendMail/{caseNum}")
	public String sendMailAndGeneratePdf(@PathVariable("caseNum") Long caseNum) throws IOException {
		
		String status = service.sendMailWithPdf(caseNum);
		if(status.contains("Success")) {
			return "Please check your mail";
		}else {
			return status;
		}
	}
	
	@GetMapping("/pendingRecords")
	public List<PendingResponse> viewAllPendingRecords(){
		return service.getAllPendingNotices();
	}
	
	@GetMapping("/historyRecords")
	public List<HistoryResponse> viewAllHistoryRecords(){
		return service.getAllHistoryNotices();
	}

}
