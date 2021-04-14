package com.cau.cc.api;

import com.cau.cc.ifs.CrudInterface;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.MatchingApiRequest;
import com.cau.cc.model.network.response.MatchingApiResponse;
import com.cau.cc.service.MatchingApiLogicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matching")
public class MatchingApiController implements CrudInterface<MatchingApiRequest, MatchingApiResponse> {

    @Autowired
    private MatchingApiLogicService matchingApiLogicService;

    @Override
    @PostMapping("") // /api/matching
    public Header<MatchingApiResponse> create(@RequestBody MatchingApiRequest request) {
        return matchingApiLogicService.create(request);
    }

    @Override
    @GetMapping("{id}")
    public Header<MatchingApiResponse> read(@PathVariable Long id) {
        return matchingApiLogicService.read(id);
    }

    @Override
    @PutMapping("")
    public Header<MatchingApiResponse> update(@RequestBody MatchingApiRequest request) {
        return matchingApiLogicService.update(request);
    }

    @Override
    @DeleteMapping("{id}")
    public Header delete(@PathVariable Long id) {
        return matchingApiLogicService.delete(id);
    }

}
