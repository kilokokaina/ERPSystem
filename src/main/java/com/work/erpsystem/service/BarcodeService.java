package com.work.erpsystem.service;

import com.work.erpsystem.exception.DuplicateDBRecord;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.BarcodeModel;

public interface BarcodeService {

    BarcodeModel save(BarcodeModel barcode) throws DuplicateDBRecord;
    BarcodeModel findById(Long codeId) throws NoDBRecord;
    BarcodeModel findByCode(String codeValue) throws NoDBRecord;
    void delete(BarcodeModel barcode);
    void deleteById(Long codeId);

}
