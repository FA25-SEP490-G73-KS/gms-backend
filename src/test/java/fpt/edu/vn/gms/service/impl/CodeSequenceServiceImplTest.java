package fpt.edu.vn.gms.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fpt.edu.vn.gms.base.BaseServiceTest;
import fpt.edu.vn.gms.entity.CodeSequence;
import fpt.edu.vn.gms.repository.CodeSequenceRepository;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.Optional;

public class CodeSequenceServiceImplTest extends BaseServiceTest {

  @Mock
  private CodeSequenceRepository codeSequenceRepository;

  @InjectMocks
  private CodeSequenceServiceImpl codeSequenceServiceImpl;

  @Test
  void generateCode_WhenNoExistingSequence_ShouldCreateNewSequenceAndReturnFormattedCode() {
    String prefix = "INV";
    int year = LocalDate.now().getYear();

    when(codeSequenceRepository.findById(prefix)).thenReturn(Optional.empty());
    ArgumentCaptor<CodeSequence> captor = ArgumentCaptor.forClass(CodeSequence.class);

    String code = codeSequenceServiceImpl.generateCode(prefix);

    verify(codeSequenceRepository).save(captor.capture());
    CodeSequence savedSeq = captor.getValue();
    assertEquals(prefix, savedSeq.getPrefix());
    assertEquals(year, savedSeq.getYear());
    assertEquals(1L, savedSeq.getCurrentValue());
    assertEquals(String.format("%s-%d-%06d", prefix, year, 1L), code);
  }

  @Test
  void generateCode_WhenExistingSequenceSameYear_ShouldIncrementAndReturnFormattedCode() {
    String prefix = "ORD";
    int year = LocalDate.now().getYear();
    CodeSequence existingSeq = CodeSequence.builder()
        .prefix(prefix)
        .year(year)
        .currentValue(5L)
        .build();

    when(codeSequenceRepository.findById(prefix)).thenReturn(Optional.of(existingSeq));
    ArgumentCaptor<CodeSequence> captor = ArgumentCaptor.forClass(CodeSequence.class);

    String code = codeSequenceServiceImpl.generateCode(prefix);

    verify(codeSequenceRepository).save(captor.capture());
    CodeSequence savedSeq = captor.getValue();
    assertEquals(6L, savedSeq.getCurrentValue());
    assertEquals(String.format("%s-%d-%06d", prefix, year, 6L), code);
  }

  @Test
  void generateCode_WhenExistingSequenceDifferentYear_ShouldResetAndReturnFormattedCode() {
    String prefix = "PAY";
    int currentYear = LocalDate.now().getYear();
    int lastYear = currentYear - 1;
    CodeSequence oldSeq = CodeSequence.builder()
        .prefix(prefix)
        .year(lastYear)
        .currentValue(99L)
        .build();

    when(codeSequenceRepository.findById(prefix)).thenReturn(Optional.of(oldSeq));
    ArgumentCaptor<CodeSequence> captor = ArgumentCaptor.forClass(CodeSequence.class);

    String code = codeSequenceServiceImpl.generateCode(prefix);

    verify(codeSequenceRepository).save(captor.capture());
    CodeSequence savedSeq = captor.getValue();
    assertEquals(currentYear, savedSeq.getYear());
    assertEquals(1L, savedSeq.getCurrentValue());
    assertEquals(String.format("%s-%d-%06d", prefix, currentYear, 1L), code);
  }
}
