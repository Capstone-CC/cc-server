package com.cau.cc.api;

import com.cau.cc.ifs.CrudInterface;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.DatelocationApiRequest;
import com.cau.cc.model.network.response.DatelocationApiResponse;
import com.cau.cc.service.DatelocationApiLogicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/datelocation")
public class DatelocationApiController implements CrudInterface<DatelocationApiRequest, DatelocationApiResponse> {

    @Autowired
    private DatelocationApiLogicService datelocationApiLogicService;

    @Override
    @PostMapping("")
    public Header<DatelocationApiResponse> create(@RequestBody DatelocationApiRequest request) {
        return datelocationApiLogicService.create(request);
    }

    @Override
    @GetMapping("{id}")
    public Header<DatelocationApiResponse> read(@PathVariable Long id) {
        return datelocationApiLogicService.read(id);
    }

    @Override
    @PutMapping("")
    public Header<DatelocationApiResponse> update(@RequestBody DatelocationApiRequest request) {
        return datelocationApiLogicService.update(request);
    }

    @Override
    @DeleteMapping("{id}")
    public Header delete(@PathVariable Long id) {
        return datelocationApiLogicService.delete(id);
    }
}
