//package com.cau.cc.service;
//
//import com.cau.cc.ifs.CrudInterface;
//import com.cau.cc.model.entity.Account;
//import com.cau.cc.model.entity.GenderEnum;
//import com.cau.cc.model.entity.Report;
//import com.cau.cc.model.network.Header;
//import com.cau.cc.model.network.request.ReportApiRequest;
//import com.cau.cc.model.network.response.ReportApiResponse;
//import com.cau.cc.model.repository.AccountRepository;
//import com.cau.cc.model.repository.MatchingRepository;
//import com.cau.cc.model.repository.ReportRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//
//@Service
//public class ReportApiLogicService {
//
//    @Autowired
//    private AccountRepository accountRepository;
//
//    @Autowired
//    private ReportRepository reportRepository;
//
//    @Autowired
//    private MatchingRepository matchingRepository;
//
//
//    public Header<ReportApiResponse> MatchingReport(String email) {
//        Account account = accountRepository.findByEmail(email);
//        if(account.getGender() == GenderEnum.남) {
//            matchingRepository.findbyManId(account.getId());
//        }
//        else {
//            matchingRepository.findbyWomanId(account.getId());
//        }
//
//        Report report = Report.builder()
//                .contents(body.getContents())
//                .reportTime(LocalDateTime.now())
//                .reporterId(accountRepository.getOne(body.getReporterId()))
//                .reportedId(accountRepository.getOne(body.getReportedId()))
//                .build();
//
//        Report newReport = reportRepository.save(report);
//        return response(newReport);
//    }
//
//    public Header<ReportApiResponse> read(Long id) {
//        return reportRepository.findById(id)
//                .map(report -> response(report))
//                .orElseGet(() -> Header.ERROR("데이터 없음"));
//    }
//
//    public Header<ReportApiResponse> update(ReportApiRequest request) {
//        ReportApiRequest body = request;
//
//        return reportRepository.findById(body.getId())
//                .map(report -> {
//                    report
//                            .setContents(body.getContents())
//                            .setReportTime(body.getReportTime());
//                    return report;
//                })
//                .map(newReport -> reportRepository.save(newReport))
//                .map(entityReport -> response(entityReport))
//                .orElseGet(()->Header.ERROR("데이터 없음"));
//    }
//
//    public Header delete(Long id) {
//        return reportRepository.findById(id)
//                .map(report -> {
//                    reportRepository.delete(report);
//                    return Header.OK();
//                })
//                .orElseGet(() -> Header.ERROR("데이터 없음"));
//    }
//
//    public Header<ReportApiResponse> response(Report report) {
//
//        ReportApiResponse body = ReportApiResponse.builder()
//                .id(report.getId())
//                .contents(report.getContents())
//                .reportTime(report.getReportTime())
//                .reporterId(report.getReporterId().getId())
//                .reportedId(report.getReportedId().getId())
//                .build();
//
//        return Header.OK(body);
//
//    }
//}
