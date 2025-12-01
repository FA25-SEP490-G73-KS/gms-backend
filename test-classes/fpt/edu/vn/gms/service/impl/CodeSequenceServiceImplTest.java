package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.entity.CodeSequence;
import fpt.edu.vn.gms.repository.CodeSequenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CodeSequenceServiceImplTest {

    @Mock
    CodeSequenceRepository codeSequenceRepository;

    @InjectMocks
    CodeSequenceServiceImpl service;

    private int currentYear;

    @BeforeEach
    void setUp() {
        currentYear = LocalDate.now().getYear();
    }

    @Test
    void generateCode_ShouldCreateNewSequence_WhenPrefixNotFound() {
        String prefix = "SR";
        when(codeSequenceRepository.findById(prefix)).thenReturn(Optional.empty());

        CodeSequence savedSeq = CodeSequence.builder()
                .prefix(prefix)
                .year(currentYear)
                .currentValue(1L)
                .build();
        when(codeSequenceRepository.save(any(CodeSequence.class))).thenReturn(savedSeq);

        String result = service.generateCode(prefix);

        assertNotNull(result);
        assertTrue(result.startsWith(prefix + "-" + currentYear + "-"));
        assertTrue(result.endsWith("000001"));
        verify(codeSequenceRepository).findById(prefix);
        verify(codeSequenceRepository).save(any(CodeSequence.class));
    }

    @Test
    void generateCode_ShouldIncrementExistingSequence_WhenPrefixFound() {
        String prefix = "SR";
        CodeSequence existing = CodeSequence.builder()
                .prefix(prefix)
                .year(currentYear)
                .currentValue(5L)
                .build();
        when(codeSequenceRepository.findById(prefix)).thenReturn(Optional.of(existing));

        CodeSequence savedSeq = CodeSequence.builder()
                .prefix(prefix)
                .year(currentYear)
                .currentValue(6L)
                .build();
        when(codeSequenceRepository.save(existing)).thenReturn(savedSeq);

        String result = service.generateCode(prefix);

        assertNotNull(result);
        assertTrue(result.startsWith(prefix + "-" + currentYear + "-"));
        assertTrue(result.endsWith("000006"));
        assertEquals(6L, existing.getCurrentValue());
        verify(codeSequenceRepository).findById(prefix);
        verify(codeSequenceRepository).save(existing);
    }

    @Test
    void generateCode_ShouldResetSequence_WhenYearChanged() {
        String prefix = "SR";
        int oldYear = currentYear - 1;
        CodeSequence existing = CodeSequence.builder()
                .prefix(prefix)
                .year(oldYear)
                .currentValue(100L)
                .build();
        when(codeSequenceRepository.findById(prefix)).thenReturn(Optional.of(existing));

        CodeSequence savedSeq = CodeSequence.builder()
                .prefix(prefix)
                .year(currentYear)
                .currentValue(1L)
                .build();
        when(codeSequenceRepository.save(existing)).thenReturn(savedSeq);

        String result = service.generateCode(prefix);

        assertNotNull(result);
        assertTrue(result.startsWith(prefix + "-" + currentYear + "-"));
        assertTrue(result.endsWith("000001"));
        assertEquals(currentYear, existing.getYear());
        assertEquals(1L, existing.getCurrentValue());
        verify(codeSequenceRepository).findById(prefix);
        verify(codeSequenceRepository).save(existing);
    }

    @Test
    void generateCode_ShouldFormatCodeCorrectly() {
        String prefix = "EXP";
        CodeSequence existing = CodeSequence.builder()
                .prefix(prefix)
                .year(currentYear)
                .currentValue(42L)
                .build();
        when(codeSequenceRepository.findById(prefix)).thenReturn(Optional.of(existing));

        CodeSequence savedSeq = CodeSequence.builder()
                .prefix(prefix)
                .year(currentYear)
                .currentValue(43L)
                .build();
        when(codeSequenceRepository.save(existing)).thenReturn(savedSeq);

        String result = service.generateCode(prefix);

        assertEquals(String.format("%s-%d-%06d", prefix, currentYear, 43L), result);
        verify(codeSequenceRepository).findById(prefix);
        verify(codeSequenceRepository).save(existing);
    }
}

