package in.globalit.service.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.lowagie.text.DocumentException;

import in.globalit.binding.CorrespondenceBinding;
import in.globalit.entity.CitizenApplicationEntity;
import in.globalit.entity.CorrespondenceNoticeEntity;
import in.globalit.entity.EligibilityDeterminationEntity;
import in.globalit.repository.CitizenRepo;
import in.globalit.repository.CorrespondenceNoticeRepo;
import in.globalit.repository.EligibilityRepo;
import in.globalit.response.HistoryResponse;
import in.globalit.response.PendingResponse;
import in.globalit.service.CorrespondenceNoticeService;
import in.globalit.util.EmailSender;
import in.globalit.util.PdfGenerator;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class CorrespondenceNoticeServiceImpl implements CorrespondenceNoticeService {

	@Autowired
	private CitizenRepo citizenRepo;

	@Autowired
	private EligibilityRepo eligibilityRepo;

	@Autowired
	private CorrespondenceNoticeRepo correspondenceRepo;

	@Autowired
	private PdfGenerator pdfGenerator;

	@Autowired
	private EmailSender mailSender;

	@Autowired
	private Environment env;

	@Override
	public CorrespondenceBinding generateNoticeWithPending(Long caseNum) throws DocumentException, IOException {

		CorrespondenceBinding binding = new CorrespondenceBinding();
		CitizenApplicationEntity applicationEntity = citizenRepo.findByCaseNum(caseNum);
		EligibilityDeterminationEntity determinationEntity = eligibilityRepo.findByCaseNum(caseNum);
		if (applicationEntity != null && determinationEntity != null) {
			binding.setCitizenName(applicationEntity.getFullname());
			binding.setCitizenSSN(applicationEntity.getSsn());
			binding.setCaseNum(caseNum);
			binding.setBenefitAmount(determinationEntity.getBenefitAmount());
			binding.setEdTraceId(determinationEntity.getEdTraceId());
			binding.setEndDate(determinationEntity.getEligEndDate());
			binding.setPlanName(determinationEntity.getPlanName());
			binding.setPlanStatus(determinationEntity.getPlanStatus());
			binding.setStartDate(determinationEntity.getEligStartDate());
			binding.setDenialReason(determinationEntity.getDenialReason());

			// File file = new
			// File("src\\main\\resources\\static\\pdf"+"\\"+applicationEntity.getCaseNum()+".pdf");

			byte[] pdfContent = pdfGenerator.generate(binding);

			CorrespondenceNoticeEntity correspondenceEntity = correspondenceRepo.findByCaseNum(caseNum);
			if (correspondenceEntity == null) {
				CorrespondenceNoticeEntity coEntity = new CorrespondenceNoticeEntity();

				// Getting AWS credentials and bucket name from the environment
				String accessKey = env.getProperty("aws.accessKey");
				String secretKey = env.getProperty("aws.secretKey");
				String bucketName = env.getProperty("aws.bucketName");

				// Upload the PDF content to AWS S3
				//go to s3 console and create inline policy and give putObjectRequest permission in json tab
				String s3Key = "IES-CaseNum-" + caseNum + ".pdf";
				uploadPdfToS3(pdfContent, bucketName, s3Key, accessKey, secretKey);

				// Save the S3 URL in your database
				String s3Url = generateS3Url(bucketName, s3Key);
				coEntity.setPdfUrl(s3Url);

				coEntity.setApplication(applicationEntity);
				coEntity.setEligibility(determinationEntity);
				coEntity.setNoticeStatus("PENDING");
				// coEntity.setCoPdf(pdfContent);
				correspondenceRepo.save(coEntity);
			}
		}

		return binding;
	}

	private void uploadPdfToS3(byte[] pdfContent, String bucketName, String s3Key, String accessKey, String secretKey) {
		S3Client s3Client = S3Client.builder().region(Region.AP_SOUTH_1)
				.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
				.build();

		PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(s3Key).acl("public-read").build();

		s3Client.putObject(putObjectRequest, RequestBody.fromBytes(pdfContent));
	}

	private String generateS3Url(String bucketName, String s3Key) {
		return "https://" + bucketName + ".s3.ap-south-1.amazonaws.com/" + s3Key;
	}

	@Override
	public String sendMailWithPdf(Long caseNum) throws IOException {

		CitizenApplicationEntity applicationEntity = citizenRepo.findByCaseNum(caseNum);
		CorrespondenceNoticeEntity coEntity = correspondenceRepo.findByCaseNum(caseNum);
		if ("PENDING".equals(coEntity.getNoticeStatus())) {

			String pdfUrl = coEntity.getPdfUrl();
			//File pdfFile = savePdfToFile(pdfUrl, applicationEntity.getCaseNum() + "");

			String to = applicationEntity.getEmail();
			String subject = "Mail regarding your plan status";
			String body = "<body style='font-weight: bold;font-size: 15px'>"
					+ "<h2 style='font-family: 'Trocchi', serif; font-size: 45px; font-weight: normal; line-height: 48px; margin: 0;'>Hi this mail is from DHS office please download your pdf from the attached link to see the eligibility and status of your application "
					+ "</h2>" + "<br>"
					+ "<h4 style='font-family: 'Trocchi', serif; font-size: 15px; font-weight: normal; line-height: 18px; margin: 0;'>If you have any problem go to your nearest DHS office"
					+ "</h4" + "<br>" + "</body>";

			mailSender.sendEmailWithAttachment(to, subject, body, pdfUrl);
			coEntity.setPrintDate(LocalDate.now());
			coEntity.setNoticeStatus("HISTORY");
			correspondenceRepo.save(coEntity);
			return "Success";
		}
		return "You have already got the mail..check your mail";
	}

	/*
	 * private File savePdfToFile(byte[] pdfContent, String caseNum) throws
	 * IOException { File pdfFile = new File("src\\main\\resources\\static\\pdf" +
	 * caseNum + ".pdf"); try (FileOutputStream fos = new FileOutputStream(pdfFile))
	 * { fos.write(pdfContent); } return pdfFile; }
	 */
	
	@Override
	public List<PendingResponse> getAllPendingNotices() {
		List<CorrespondenceNoticeEntity> findAll = correspondenceRepo.findAll();
		List<CorrespondenceNoticeEntity> pendingRecords = findAll.stream().filter(e -> "PENDING".equals(e.getNoticeStatus())).collect(Collectors.toList());
		
		List<PendingResponse> pendingsResponses = new ArrayList<>();
		pendingRecords.forEach(record -> {
			PendingResponse pendingResponse = new PendingResponse();
			
			pendingResponse.setEdTraceId(record.getEligibility().getEdTraceId());
			pendingResponse.setPlanName(record.getEligibility().getPlanName());
			pendingResponse.setPlanStatus(record.getEligibility().getPlanStatus());
			pendingResponse.setEligStartDate(record.getEligibility().getEligStartDate());
			pendingResponse.setEligEndDate(record.getEligibility().getEligEndDate());
			pendingResponse.setBenefitAmount(record.getEligibility().getBenefitAmount());
			
			pendingsResponses.add(pendingResponse);
		});
		
		return pendingsResponses;
	}
	
	@Override
	public List<HistoryResponse> getAllHistoryNotices() {
		List<CorrespondenceNoticeEntity> findAll = correspondenceRepo.findAll();
		List<CorrespondenceNoticeEntity> historyRecords = findAll.stream().filter(e -> "HISTORY".equals(e.getNoticeStatus())).collect(Collectors.toList());
		
		List<HistoryResponse> historyResponses = new ArrayList<>();
		historyRecords.forEach(record -> {
			HistoryResponse historyResponse = new HistoryResponse();
			
			historyResponse.setEdTraceId(record.getEligibility().getEdTraceId());
			historyResponse.setPlanName(record.getEligibility().getPlanName());
			historyResponse.setPlanStatus(record.getEligibility().getPlanStatus());
			historyResponse.setEligStartDate(record.getEligibility().getEligStartDate());
			historyResponse.setEligEndDate(record.getEligibility().getEligEndDate());
			historyResponse.setBenefitAmount(record.getEligibility().getBenefitAmount());
			historyResponse.setGeneratedDate(record.getPrintDate());
			
			historyResponses.add(historyResponse);
		});
		
		return historyResponses;
	}
	

}
