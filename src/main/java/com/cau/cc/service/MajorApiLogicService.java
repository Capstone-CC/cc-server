package com.cau.cc.service;

import com.cau.cc.ifs.CrudInterface;
import com.cau.cc.model.entity.Major;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.MajorApiRequest;
import com.cau.cc.model.network.response.MajorApiResponse;
import com.cau.cc.model.repository.AccountRepository;
import com.cau.cc.model.repository.MajorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MajorApiLogicService implements CrudInterface<MajorApiRequest, MajorApiResponse> {

    @Autowired
    private MajorRepository majorRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Header<MajorApiResponse> create(Header<MajorApiRequest> request) {

        MajorApiRequest body = request.getValue();

        Major major = Major.builder()
                .majorName(body.getMajorName())
                .build();

        Major newMajor = majorRepository.save(major);
        return response(newMajor);
    }

    @Override
    public Header<MajorApiResponse> read(Long id) {
        return majorRepository.findById(id)
                .map(major -> response(major))
                .orElseGet(() -> Header.ERROR("데이터 없음"));
    }

    @Override
    public Header<MajorApiResponse> update(Header<MajorApiRequest> request) {
        MajorApiRequest body = request.getValue();

        return majorRepository.findById(body.getId())
                .map(major -> {
                    major
                            .setMajorName(body.getMajorName());
                    return major;
                })
                .map(newMajor -> majorRepository.save(newMajor))
                .map(entityMajor -> response(entityMajor))
                .orElseGet(()-> Header.ERROR("데이터 없음"));
    }

    @Override
    public Header delete(Long id) {
        return majorRepository.findById(id)
                .map(major -> {
                    majorRepository.delete(major);
                    return Header.OK();
                })
                .orElseGet(() -> Header.ERROR("데이터 없음"));
    }

    private Header<MajorApiResponse> response(Major major) {
        MajorApiResponse body = MajorApiResponse.builder()
                .id(major.getId())
                .majorName(major.getMajorName())
                .build()
                ;

        return Header.OK(body);
    }
}
