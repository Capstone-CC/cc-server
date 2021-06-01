package com.cau.cc.service;

import com.cau.cc.model.entity.Account;
import com.cau.cc.model.entity.Chatroom;
import com.cau.cc.model.entity.Report;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.ReportApiRequest;
import com.cau.cc.model.network.response.ReportApiResponse;
import com.cau.cc.model.repository.AccountRepository;
import com.cau.cc.model.repository.ChatRoomRepository;
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

    @Autowired
    private ChatRoomRepository chatRoomRepository;


    public Header<ReportApiResponse> createChat(Long id, ReportApiRequest request) {

        ReportApiRequest body = request;
        Optional<Account> report1 = accountRepository.findById(id);
        Account reporter = report1.get();
        if (reporter.getReporterCount() ==0) {
            return Header.ERROR("최대 신고 횟수를 초과하였습니다.");
        }
        reporter.setReporterCount(reporter.getReporterCount()-1);

        Optional<Chatroom> chatroom1 = chatRoomRepository.findById(request.getId());
        Chatroom chatroom = chatroom1.get();

        Account reported = null;
        if(chatroom.getManId().getId() == reporter.getId()) {
            Optional<Account> report2 = accountRepository.findById(chatroom.getWomanId().getId());
            reported = report2.get();
            reported.setReportedCount(reported.getReportedCount()+1);
        }
        else if (chatroom.getWomanId().getId() == reporter.getId()) {
            Optional<Account> report2 = accountRepository.findById(chatroom.getManId().getId());
            reported = report2.get();
            reported.setReportedCount(reported.getReportedCount()+1);
        }

        accountRepository.save(reporter);
        accountRepository.save(reported);

        Report report = Report.builder()
                .contents(body.getContents())
                .reportTime(LocalDateTime.now())
                .reporterId(reporter)
                .reportedId(reported)
                .build();

        Report newReport = reportRepository.save(report);
        return response(newReport);
    }

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
