package com.cau.cc.api;

import com.cau.cc.ifs.CrudInterface;
import com.cau.cc.model.entity.Account;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.DatelocationApiRequest;
import com.cau.cc.model.network.request.ReportApiRequest;
import com.cau.cc.model.network.response.ReportApiResponse;
import com.cau.cc.service.ReportApiLogicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "Report API")
@RequestMapping("/report")
public class ReportApiController{

    @Autowired
    ReportApiLogicService reportApiLogicService;

    @ApiOperation(value = "신고 시 카운트 증가",notes = "필수 정보 : report request")
    @PostMapping("") // /api/report
    public Header<ReportApiResponse> create(@RequestBody ReportApiRequest request) {
        return reportApiLogicService.create(request);
    }


    @GetMapping("{id}") // /api/report/id
    public Header<ReportApiResponse> read(@PathVariable Long id) {
        return reportApiLogicService.read(id);
    }


    @PutMapping("") // /api/report
    public Header<ReportApiResponse> update(@RequestBody ReportApiRequest request) {
        return reportApiLogicService.update(request);
    }


    @DeleteMapping("{id}") // /api/report/id
    public Header delete(@PathVariable Long id) {
        return reportApiLogicService.delete(id);
    }

}
