package fpt.edu.vn.gms.service.pdf;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HtmlTemplateService {

    public String loadAndFillTemplate(String templateName, Map<String, Object> data) {

        try {
            MustacheFactory mf = new DefaultMustacheFactory();
            Reader reader = new InputStreamReader(
                    getClass().getResourceAsStream("/" + templateName),
                    StandardCharsets.UTF_8
            );

            Mustache mustache = mf.compile(reader, templateName);

            StringWriter writer = new StringWriter();
            mustache.execute(writer, data).flush();

            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Không thể load template HTML");
        }
    }
}

