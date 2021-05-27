package com.cau.cc.service;

import com.cau.cc.ifs.CrudInterface;
import com.cau.cc.model.entity.Account;
import com.cau.cc.model.entity.GenderEnum;
import com.cau.cc.model.entity.Report;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.ChatroomApiRequest;
import com.cau.cc.model.network.request.ReportApiRequest;
import com.cau.cc.model.network.response.ReportApiResponse;
import com.cau.cc.model.repository.AccountRepository;
import com.cau.cc.model.repository.MatchingRepository;
import com.cau.cc.model.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ReportApiLogicService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ReportRepository reportRepository;


    public Header<ReportApiResponse> create(ReportApiRequest request) {

        ReportApiRequest body = request;
        Optional<Account> report1 = accountRepository.findById(request.getReporterId());
        Account reporter = report1.get();
        if (reporter.getReporterCount() ==0) {
            return Header.ERROR("최대 신고 횟수를 초과하였습니다.");
        }
        reporter.setReporterCount(reporter.getReporterCount()-1);

        Optional<Account> report2 = accountRepository.findById(request.getReportedId());
        Account reported = report2.get();
        reported.setReportedCount(reported.getReportedCount()+1);

        accountRepository.save(reporter);
        accountRepository.save(reported);

        Report report = Report.builder()
                .contents(body.getContents())
                .reportTime(LocalDateTime.now())
                .reporterId(accountRepository.getOne(body.getReporterId()))
                .reportedId(accountRepository.getOne(body.getReportedId()))
                .build();

        Report newReport = reportRepository.save(report);
        return response(newReport);
    }

    public Header<ReportApiResponse> read(Long id) {
        return reportRepository.findById(id)
                .map(report -> response(report))
                .orElseGet(() -> Header.ERROR("데이터 없음"));
    }

    public Header<ReportApiResponse> update(ReportApiRequest request) {
        ReportApiRequest body = request;

        return reportRepository.findById(body.getId())
                .map(report -> {
                    report
                            .setContents(body.getContents())
                            .setReportTime(body.getReportTime());
                    return report;
                })
                .map(newReport -> reportRepository.save(newReport))
                .map(entityReport -> response(entityReport))
                .orElseGet(()->Header.ERROR("데이터 없음"));
    }

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
                .contents(report.getContents())
                .reportTime(report.getReportTime())
                .reporterId(report.getReporterId().getId())
                .reportedId(report.getReportedId().getId())
                .build();

        return Header.OK(body);

    }
}
