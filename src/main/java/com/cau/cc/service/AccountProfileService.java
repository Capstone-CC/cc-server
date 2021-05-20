package com.cau.cc.service;

import com.cau.cc.model.entity.Account;
import com.cau.cc.model.entity.ChatMessage;
import com.cau.cc.model.entity.Chatroom;
import com.cau.cc.model.entity.GenderEnum;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.AccountProfileApiRequest;
import com.cau.cc.model.network.response.AccountChatListApiResponse;
import com.cau.cc.model.network.response.AccountProfileApiResponse;
import com.cau.cc.model.network.response.ChatMessageApiResponse;
import com.cau.cc.model.network.response.ChatroomApiResponse;
import com.cau.cc.model.repository.AccountRepository;
import com.cau.cc.model.repository.ChatMessageRepository;
import com.cau.cc.page.Pagination;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AccountProfileService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ChatroomApiLogicService chatroomApiLogicService;
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public Header<AccountProfileApiResponse> create(AccountProfileApiRequest request) {
        return null;
    }

    public Header<AccountProfileApiResponse> read(String email) {
        Account account = accountRepository.findByEmail(email);
        if(account!=null) {
            AccountProfileApiResponse accountApiResponse = response(account);
            return Header.OK(accountApiResponse);
        }
        else return Header.ERROR("데이터 없음");

    }
// 이멜, 닉네임, 성별, 학과, 학년, 자기소개
    private AccountProfileApiResponse response(Account account) {
        AccountProfileApiResponse accountProfileApiResponse = AccountProfileApiResponse.builder()
                .image(account.getImage())
                .email(account.getEmail())
                .gender(account.getGender())
                .grade(account.getGrade())
                .majorName(account.getMajorName())
                .nickName(account.getNickName())
                .content(account.getContent())
                .build();
//
//        if(account.getMajorName() != null){
//            accountApiResponse.setMajor(account.getMajorName());
//        }
//

        return accountProfileApiResponse;
    }

//    @Autowired
//    MajorRepository majorRepository;

    public Header<AccountProfileApiResponse> update(AccountProfileApiRequest request) {

        Account account = accountRepository.findByEmail(request.getEmail());

        //major 테이블에서 majorname을 통해서 majorId 값 받아오기
        //Major major =  majorRepository.findByMajorName(request.getMajorId().getMajorName());

        if(account != null) {
            account.setNickName(request.getNickName())
                    .setEmail(request.getEmail())
                    .setGender(request.getGender())
                    .setGrade(request.getGrade())
                    .setMajorName(request.getMajorName())
                    .setContent(request.getContent())
                    .setImage(request.getImage());

            Account updateAccount = accountRepository.save(account);
            AccountProfileApiResponse accountApiResponse = response(updateAccount);
            return Header.OK(accountApiResponse);
        }
        else return Header.ERROR("데이터 없음");


    }

    public Header delete(Long id) {
        return null;
    }

    public Header<AccountChatListApiResponse> chatInfo(String email) {

        // user
        Account account = accountRepository.findByEmail(email);
        AccountProfileApiResponse accountApiResponse = response(account);
        // chatlist
        /**
         * 남자유저일 경우 챗리스트 출력
         */
        if(account.getGender()==GenderEnum.남) {
            List<Chatroom> chatroomList = account.getManList_chat();

            List<ChatroomApiResponse> chatroomApiResponseList = chatroomList.stream()
                    .map(chatroom -> {
                        return chatroomApiLogicService.response(chatroom);
                    })
                    .map(response -> (response).getValue())
                    .collect(Collectors.toList());
            accountApiResponse.setChatroomApiResponseList(chatroomApiResponseList);
        }
        /**
         * 여자유저일 경우 챗리스트 출력
         */
        else {
            List<Chatroom> chatroomList = account.getWomanList_chat();

            List<ChatroomApiResponse> chatroomApiResponseList = chatroomList.stream()
                    .map(chatroom -> {
                        return chatroomApiLogicService.response(chatroom);
                    })
                    .map(response -> (response).getValue())
                    .collect(Collectors.toList());
            accountApiResponse.setChatroomApiResponseList(chatroomApiResponseList);
        }


        AccountChatListApiResponse accountChatListApiResponse = AccountChatListApiResponse.builder()
                .accountProfileApiResponse(accountApiResponse)
                .build();
        return Header.OK(accountChatListApiResponse);
    }

    public Header<List<ChatMessageApiResponse>> search(Long id,Pageable pageable) {
        Page<ChatMessage> chatMessages = chatMessageRepository.findByChatMessage(id, pageable);

        List<ChatMessageApiResponse> chatMessageApiResponseList = chatMessages.stream()
                .map(chatMessage -> res(chatMessage))
                .collect(Collectors.toList());

        Pagination pagination = Pagination.builder()
                .totalPages(chatMessages.getTotalPages())
                .totalElements(chatMessages.getTotalElements())
                .currentPage(chatMessages.getNumber())
                .currentElements(chatMessages.getNumberOfElements())
                .build();

        return Header.OK(chatMessageApiResponseList, pagination);
    }

    private ChatMessageApiResponse res(ChatMessage chatMessage) {
        ChatMessageApiResponse body = ChatMessageApiResponse.builder()
                .id(chatMessage.getId())
                .userId(chatMessage.getUserId())
                .chatroomId(chatMessage.getChatroomId())
                .message(chatMessage.getMessage())
                .time(chatMessage.getTime())
                .type(chatMessage.getType())
                .build();
        return body;
    }

}
