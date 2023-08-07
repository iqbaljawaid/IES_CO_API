package in.globalit.service;

import java.io.IOException;
import java.util.List;

import com.lowagie.text.DocumentException;

import in.globalit.binding.CorrespondenceBinding;
import in.globalit.response.HistoryResponse;
import in.globalit.response.PendingResponse;

public interface CorrespondenceNoticeService {

	public CorrespondenceBinding generateNoticeWithPending(Long caseNum) throws DocumentException, IOException;

	public String sendMailWithPdf(Long caseNum) throws IOException;
	
	public List<PendingResponse> getAllPendingNotices();
	
	public List<HistoryResponse> getAllHistoryNotices();
	
	

}
