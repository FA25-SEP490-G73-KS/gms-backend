package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.entity.CodeSequence;
import fpt.edu.vn.gms.repository.CodeSequenceRepository;
import fpt.edu.vn.gms.service.CodeSequenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CodeSequenceServiceImpl implements CodeSequenceService {

    private final CodeSequenceRepository codeSequenceRepository;

    @Override
    public synchronized String generateCode(String prefix) {
        int year = LocalDate.now().getYear();

        // Lấy hoặc tạo mới sequence cho prefix
        CodeSequence seq = codeSequenceRepository.findById(prefix)
                .orElseGet(() -> CodeSequence.builder()
                        .prefix(prefix)
                        .year(year)
                        .currentValue(0L)
                        .build());

        // Reset khi qua năm mới
        if (seq.getYear() != year) {
            seq.setYear(year);
            seq.setCurrentValue(0L);
        }

        // Tăng số thứ tự
        seq.setCurrentValue(seq.getCurrentValue() + 1);
        codeSequenceRepository.save(seq);

        // Format: PREFIX-YYYY-000001
        return String.format("%s-%d-%06d", prefix, year, seq.getCurrentValue());
    }
}
