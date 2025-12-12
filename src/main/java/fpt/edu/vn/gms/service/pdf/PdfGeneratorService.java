package fpt.edu.vn.gms.service.pdf;

public interface PdfGeneratorService {

    byte[] generateQuotationPdf(String htmlContent);
}
