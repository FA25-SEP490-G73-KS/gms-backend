package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.entity.Market;
import fpt.edu.vn.gms.repository.MarketRepository;
import fpt.edu.vn.gms.service.MarketService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
@Slf4j
public class MarketServiceImpl implements MarketService {

    MarketRepository marketRepo;

    @Override
    public List<Market> getAll() {
        log.info("Fetching all markets");
        return marketRepo.findAll();
    }
}
