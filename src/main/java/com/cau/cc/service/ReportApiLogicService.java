package com.cau.cc.service;

import com.cau.cc.ifs.CrudInterface;
import com.cau.cc.model.entity.Report;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.ReportApiRequest;
import com.cau.cc.model.network.response.ReportApiResponse;
import com.cau.cc.model.repository.AccountRepository;
import com.cau.cc.model.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReportApiLogicService implements CrudInterface<ReportApiRequest, ReportApiResponse> {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Override
    public Header<ReportApiResponse> create(Header<ReportApiRequest> request) {
        ReportApiRequest body = request.getData();

        Report report = Report.builder()
                .content(body.getContent())
                .reportTime(LocalDateTime.now())
                .reporterId(accountRepository.getOne(body.getReporterId()))
                .reportedId(accountRepository.getOne(body.getReportedId()))
                .build();

        Report newReport = reportRepository.save(report);
        return response(newReport);
    }

    @Override
    public Header<ReportApiResponse> read(Long id) {
        return reportRepository.findById(id)
                .map(report -> response(report))
                .orElseGet(() -> Header.ERROR("데이터 없음"));
    }

    @Override
    public Header<ReportApiResponse> update(Header<ReportApiRequest> request) {
        ReportApiRequest body = request.getData();

        return reportRepository.findById(body.getId())
                .map(report -> {
                    report
                            .setContent(body.getContent())
                            .setReportTime(body.getReportTime());
                    return report;
                })
                .map(newReport -> reportRepository.save(newReport))
                .map(entityReport -> response(entityReport))
                .orElseGet(()->Header.ERROR("데이터 없음"));
    }

    @Override
    public Header delete(Long id) {
        return reportRepository.findById(id)
                .map(report -> {
                    reportRepository.delete(report);
                    return Header.OK();
                })
                .orElseGet(() -> Header.ERROR("데이터 없음"));
    }

    public Header<ReportApiResponse> response(Report report) {

        ReportApiResponse body = ReportApiResponse.builder()
                .id(report.getId())
                .content(report.getContent())
                .reportTime(report.getReportTime())
                .reporterId(report.getReporterId().getId())
                .reportedId(report.getReportedId().getId())
                .build();

        return Header.OK(body);

    }
}
