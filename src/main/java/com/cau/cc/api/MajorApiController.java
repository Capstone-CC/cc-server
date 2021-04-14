package com.cau.cc.api;

import com.cau.cc.ifs.CrudInterface;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.MajorApiRequest;
import com.cau.cc.model.network.response.MajorApiResponse;
import com.cau.cc.service.MajorApiLogicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/major")
public class MajorApiController implements CrudInterface<MajorApiRequest, MajorApiResponse> {

    @Autowired
    private MajorApiLogicService majorApiLogicService;

    @Override
    @PostMapping("") // /api/major
    public Header<MajorApiResponse> create(@RequestBody MajorApiRequest request) {
        return majorApiLogicService.create(request);
    }

    @Override
    @GetMapping("{id}") // /api/major/id
    public Header<MajorApiResponse> read(@PathVariable Long id) {
        return majorApiLogicService.read(id);
    }

    @Override
    @PutMapping("") // /api/major
    public Header<MajorApiResponse> update(@RequestBody MajorApiRequest request) {
        return majorApiLogicService.update(request);
    }

    @Override
    @DeleteMapping("{id}") // /api/major/id
    public Header delete(@PathVariable Long id) {
        return majorApiLogicService.delete(id);
    }
}
