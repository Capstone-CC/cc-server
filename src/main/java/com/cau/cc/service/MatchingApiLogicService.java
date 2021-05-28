package com.cau.cc.service;

import com.cau.cc.ifs.CrudInterface;
import com.cau.cc.model.entity.GenderEnum;
import com.cau.cc.model.entity.Matching;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.MatchingApiRequest;
import com.cau.cc.model.network.response.MatchingApiResponse;
import com.cau.cc.model.repository.AccountRepository;
import com.cau.cc.model.repository.MatchingRepository;
import com.cau.cc.webrtc.model.DelayObject;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class MatchingApiLogicService implements CrudInterface<MatchingApiRequest, MatchingApiResponse> {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private MatchingRepository matchingRepository;

    @Override
    public Header<MatchingApiResponse> create(MatchingApiRequest request) {

        MatchingApiRequest body = request;

        Matching matching = Matching.builder()
                .manUserState(body.getManUserState())
                .womanUserState(body.getWomanUserState())
                .time(body.getTime())
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
    public Header<MatchingApiResponse> update(MatchingApiRequest request) {
        MatchingApiRequest body = request;

        return matchingRepository.findById(body.getId())
                .map(entityMatching -> {
                    entityMatching.setManUserState(body.getManUserState())
                    .setWomanUserState(body.getWomanUserState())
                    .setTime(body.getTime()) // 현재 시간으로 하면 x
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
                .time(matching.getTime())
                .manId(matching.getManId().getId())
                .womanId(matching.getWomanId().getId())
                .build();

        return Header.OK(body);
    }

    /**매칭 남자id,매칭 여자id, 시간으로 찾기**/
    public Header<MatchingApiResponse> findByManIdAndWomanIdAndTime(MatchingApiRequest request){
        Matching matching = matchingRepository.findByManIdAndWomanIdAndTime(request.getManId(),request.getWomanId(),request.getTime());
        return response(matching);
    }



    /**id와 매칭된 매칭 테이블 모두 찾기**/
    public List<DelayObject> findById(GenderEnum gender, Long id){
        List<Matching> matchings = null;
        if(gender == GenderEnum.남){
            matchings = matchingRepository.findByManId(id);
        } else{
            matchings = matchingRepository.findByWomanId(id);
        }
        return delayObjects(matchings,gender);
    }
    
    //TODO 리스트로 반환하기
    private List<DelayObject> delayObjects (List<Matching> matching,GenderEnum gender){
        if (matching == null){
            return null;
        }
        Set<DelayObject> set = new HashSet<>();

        for(Matching o : matching){
            if(gender == GenderEnum.남){
                set.add(new DelayObject(o.getWomanId().getId(),0));
            } else{
                set.add(new DelayObject(o.getManId().getId(),0));
            }
        }
        //중복제거된 DelayObject를 list로 저장
        List<DelayObject> delayObjects = new ArrayList<>(set);
        return delayObjects;
    }
}
