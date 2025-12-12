package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.entity.Brand;

import java.util.List;

public interface BrandService {

    List<Brand> getAll();

    Brand getById(Long id);
}
