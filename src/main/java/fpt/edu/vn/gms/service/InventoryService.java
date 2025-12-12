package fpt.edu.vn.gms.service;

public interface InventoryService {

    double getAvailableQuantity(Long partId);

    double getReservedQuantity(Long partId);
}

