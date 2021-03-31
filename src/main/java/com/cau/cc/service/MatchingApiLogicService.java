package com.cau.cc.service;

import com.cau.cc.ifs.CrudInterface;
import com.cau.cc.model.entity.Matching;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.MatchingApiRequest;
import com.cau.cc.model.network.response.MatchingApiResponse;
import com.cau.cc.model.repository.AccountRepository;
import com.cau.cc.model.repository.MatchingRepository;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MatchingApiLogicService implements CrudInterface<MatchingApiRequest, MatchingApiResponse> {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private MatchingRepository matchingRepository;

    @Override
    public Header<MatchingApiResponse> create(Header<MatchingApiRequest> request) {

        MatchingApiRequest body = request.getData();

        Matching matching = Matching.builder()
                .manUserState(body.getManUserState())
                .womanUserState(body.getWomanUserState())
                .time(LocalDateTime.now())
                .manId(accountRepository.getOne(body.getManId()))
                .womanId(accountRepository.getOne(body.getWomanId()))
                .build();

        Matching newMatching = matchingRepository.save(matching);

        return response(newMatching);

    }

    @Override
    public Header<MatchingApiResponse> read(Long id) {
        return matchingRepository.findById(id)
                .map(this::response)
                .orElseGet(()-> Header.ERROR("데이터 없음"));
    }

    @Override
    public Header<MatchingApiResponse> update(Header<MatchingApiRequest> request) {
        MatchingApiRequest body = request.getData();

        return matchingRepository.findById(body.getId())
                .map(entityMatching -> {
                    entityMatching.setManUserState(body.getManUserState())
                    .setWomanUserState(body.getWomanUserState())
                    .setTime(LocalDateTime.now())
                    ;

                    return entityMatching;
                })
                .map(newEntityMatching -> matchingRepository.save(newEntityMatching))
                .map(matching -> response(matching))
                .orElseGet(()->Header.ERROR("데이터 없음"));
    }

    @Override
    public Header delete(Long id) {
        return matchingRepository.findById(id)
                .map(matching -> {
                    matchingRepository.delete(matching);
                    return Header.OK();
                })
                .orElseGet(()->Header.ERROR("데이터 없음"));
    }

    private Header<MatchingApiResponse> response(Matching matching) {
        MatchingApiResponse body = MatchingApiResponse.builder()
                .id(matching.getId())
                .manUserState(matching.getManUserState())
                .womanUserState(matching.getWomanUserState())
                .manId(matching.getManId().getId())
                .womanId(matching.getWomanId().getId())
                .build();

        return Header.OK(body);
    }
}
