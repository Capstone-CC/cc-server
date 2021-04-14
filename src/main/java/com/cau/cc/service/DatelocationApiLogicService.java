package com.cau.cc.service;

import com.cau.cc.ifs.CrudInterface;
import com.cau.cc.model.entity.Datelocation;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.DatelocationApiRequest;
import com.cau.cc.model.network.response.DatelocationApiResponse;
import com.cau.cc.model.repository.DatelocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatelocationApiLogicService implements CrudInterface<DatelocationApiRequest, DatelocationApiResponse> {

    @Autowired
    private DatelocationRepository datelocationRepository;

    @Override
    public Header<DatelocationApiResponse> create(Header<DatelocationApiRequest> request) {
        DatelocationApiRequest body = request.getValue();

        Datelocation datelocation = Datelocation.builder()
                .location(body.getLocation())
                .name(body.getName())
                .img(body.getImg())
                .build();

        Datelocation newDatelocation = datelocationRepository.save(datelocation);
        return response(newDatelocation);
    }

    @Override
    public Header<DatelocationApiResponse> read(Long id) {
        return datelocationRepository.findById(id)
                .map(datelocation -> response(datelocation))
                .orElseGet(() -> Header.ERROR("데이터 없음"));
    }

    @Override
    public Header<DatelocationApiResponse> update(Header<DatelocationApiRequest> request) {
        DatelocationApiRequest body = request.getValue();

        return datelocationRepository.findById(body.getId())
                .map(datelocation -> {
                    datelocation
                            .setLocation(body.getLocation())
                            .setName(body.getName())
                            .setImg(body.getImg())
                            ;
                    return datelocation;
                })
                .map(newDatelocation -> datelocationRepository.save(newDatelocation))
                .map(entityDatelocation -> response(entityDatelocation))
                .orElseGet(() -> Header.ERROR("데이터 없음"));
    }

    @Override
    public Header delete(Long id) {
        return datelocationRepository.findById(id)
                .map(datelocation -> {
                    datelocationRepository.delete(datelocation);
                    return Header.OK();
                })
                .orElseGet(() -> Header.ERROR("데이터 없음"));
    }

    private Header<DatelocationApiResponse> response(Datelocation datelocation) {
        DatelocationApiResponse body = DatelocationApiResponse.builder()
                .id(datelocation.getId())
                .location(datelocation.getLocation())
                .name(datelocation.getName())
                .img(datelocation.getImg())
                .build();

        return Header.OK(body);
    }

}
