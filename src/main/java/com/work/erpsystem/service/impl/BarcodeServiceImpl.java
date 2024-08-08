package com.work.erpsystem.service.impl;

import com.work.erpsystem.exception.DuplicateDBRecord;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.BarcodeModel;
import com.work.erpsystem.repository.BarcodeRepository;
import com.work.erpsystem.service.BarcodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class BarcodeServiceImpl implements BarcodeService {

    private final BarcodeRepository barcodeRepository;

    @Autowired
    public BarcodeServiceImpl(BarcodeRepository barcodeRepository) {
        this.barcodeRepository = barcodeRepository;
    }

    @Override
    public BarcodeModel save(BarcodeModel barcode) throws DuplicateDBRecord {
        if (barcodeRepository.findByCodeValue(barcode.getCodeValue()) != null) {
            throw new DuplicateDBRecord("This barcode already exists in DB");
        }

        return barcodeRepository.save(barcode);
    }

    @Override
    public BarcodeModel findById(Long codeId) throws NoDBRecord {
        BarcodeModel barcode = barcodeRepository.findById(codeId).orElse(null);

        if (Objects.isNull(barcode)) {
            throw new NoDBRecord("No such barcode in DB");
        }

        return barcode;
    }

    @Override
    public BarcodeModel findByCode(String codeValue) throws NoDBRecord {
        BarcodeModel barcode = barcodeRepository.findByCodeValue(codeValue);

        if (Objects.isNull(barcode)) {
            throw new NoDBRecord("No such barcode in DB");
        }

        return barcode;
    }

    @Override
    public void delete(BarcodeModel barcode) {
        barcodeRepository.delete(barcode);
    }

    @Override
    public void deleteById(Long codeId) {
        barcodeRepository.deleteById(codeId);
    }
}
