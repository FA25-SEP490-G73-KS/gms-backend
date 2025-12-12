package fpt.edu.vn.gms.service.pdf;

import com.lowagie.text.pdf.BaseFont;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    public byte[] generateQuotationPdf(String htmlContent) {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            ITextRenderer renderer = new ITextRenderer();

            // Đăng ký font để render tiếng Việt
            renderer.getFontResolver().addFont(
                    "src/main/resources/fonts/DejaVuSans.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
            );

            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(out);

            return out.toByteArray();
        }
        catch (Exception e) {
            log.error("PDF generation failed", e);
            throw new RuntimeException("Lỗi tạo PDF");
        }
    }
}
