package in.globalit.util;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Component;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import in.globalit.binding.CorrespondenceBinding;

@Component
public class PdfGenerator {

	public byte[] generate(CorrespondenceBinding binding)
			throws DocumentException, IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Document document = new Document(PageSize.A4);
		// Getting instance of PdfWriter
		//PdfWriter.getInstance(document, response.getOutputStream());
		//PdfWriter.getInstance(document, new FileOutputStream(file));
		PdfWriter.getInstance(document, baos);
		// Opening the created document to modify it
		document.open();
		// Creating font && setting font size and style
		Font fontTitle = FontFactory.getFont(FontFactory.COURIER_BOLDOBLIQUE);
		fontTitle.setSize(20);
		// Creating paragraph
		Paragraph p1 = new Paragraph("IES Notice", fontTitle);
		// aligning the paragraph
		p1.setAlignment(Element.ALIGN_CENTER);
		// adding the created paragraph in the document obj
		document.add(p1);
		// Creating a table of 10 columns
		PdfPTable table = new PdfPTable(10);
		// Setting width of the table and its columns and spacing
		table.setWidthPercentage(100f);
		table.setWidths(new float[] {2.5f,1.5f,1.0f,2.0f,2.0f,1.5f,1.5f,1.5f,2.0f,2.0f});
		table.setSpacingBefore(3);
		// Creating table cells for the table header
		PdfPCell cell = new PdfPCell();
		// setting background color and padding of the table cells
		cell.setBackgroundColor(Color.MAGENTA);
		cell.setPadding(5);
		// Creating font and Setting font style and size
		Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN);
		font.setColor(Color.WHITE);
		font.setStyle(Font.BOLD);
		font.setSize(10);

		// Adding headings in the created table cell or header
		// Adding Cell to table
		cell.setPhrase(new Phrase("Citizen Name", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("SSN", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Case Number", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Eligibility Id", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Plan Name", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Plan Status", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Start Date", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("End Date", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Benefit Amount", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Denial Reason", font));
		table.addCell(cell);
		
		
		table.addCell(binding.getCitizenName());
		table.addCell(binding.getCitizenSSN());
		table.addCell(binding.getCaseNum() +"");
		table.addCell(binding.getEdTraceId()+"");
		table.addCell(binding.getPlanName());
		table.addCell(binding.getPlanStatus());
		table.addCell(binding.getStartDate()==null?"N/A":binding.getStartDate().toString());
		table.addCell(binding.getEndDate()==null?"N/A":binding.getEndDate().toString());
		table.addCell(binding.getBenefitAmount()==null?"N/A":binding.getBenefitAmount().toString());
		table.addCell(binding.getDenialReason());
		
		// Adding the created table to the document
		document.add(table);

		// Closing the document
		document.close();
		return baos.toByteArray();
	}

}
