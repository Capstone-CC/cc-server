package com.cau.cc.api;

import com.cau.cc.ifs.CrudInterface;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.ReportApiRequest;
import com.cau.cc.model.network.response.ReportApiResponse;
import com.cau.cc.service.ReportApiLogicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
public class ReportApiController implements CrudInterface<ReportApiRequest, ReportApiResponse> {

    @Autowired
    ReportApiLogicService reportApiLogicService;

    @Override
    @PostMapping("") // /api/report
    public Header<ReportApiResponse> create(@RequestBody ReportApiRequest request) {
        return reportApiLogicService.create(request);
    }

    @Override
    @GetMapping("{id}") // /api/report/id
    public Header<ReportApiResponse> read(@PathVariable Long id) {
        return reportApiLogicService.read(id);
    }

    @Override
    @PutMapping("") // /api/report
    public Header<ReportApiResponse> update(@RequestBody ReportApiRequest request) {
        return reportApiLogicService.update(request);
    }

    @Override
    @DeleteMapping("{id}") // /api/report/id
    public Header delete(@PathVariable Long id) {
        return reportApiLogicService.delete(id);
    }

}
