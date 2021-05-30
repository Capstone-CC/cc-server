package com.cau.cc.service;

import com.cau.cc.chat.websocket.chatmessage.ChatMessageDto;
import com.cau.cc.chat.websocket.controller.ChatMessageController;
import com.cau.cc.model.entity.*;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.AccountProfileApiRequest;
import com.cau.cc.model.network.response.*;
import com.cau.cc.model.repository.AccountRepository;
import com.cau.cc.model.repository.ChatMessageRepository;
import com.cau.cc.model.repository.ChatRoomRepository;
import com.cau.cc.page.Pagination;
import com.cau.cc.webrtc.model.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
    @Autowired
    private ChatRoomRepository chatRoomRepository;

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

    private AccountProfileChatApiResponse ChatResponse(Account account) {
        AccountProfileChatApiResponse accountProfileChatApiResponse = AccountProfileChatApiResponse.builder()
                .id(account.getId())
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

        return accountProfileChatApiResponse;
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
    private final ChatMessageController chatMessageController;

    public Header delete(String email, Long id) {

        Account account = accountRepository.findByEmail(email);
        Optional<Chatroom> chatroom = chatRoomRepository.findById(id);
        Chatroom newChatroom = chatroom.get();
        ChatMessageDto message = ChatMessageDto.builder()
                .type(MessageType.LEAVE)
                .chatroomId(id)
                .userId(account.getId())
                .build();

        if(account.getGender() == GenderEnum.남) {
            newChatroom.setManStatus(1);
            Chatroom chat = chatRoomRepository.save(newChatroom);
            chatMessageController.message(message);
            if (chat.getManStatus()==1 && chat.getWomanStatus()==1) {
                //chatMessageRepository.deleteAllByChatroomId(id);
                chatRoomRepository.delete(chat);
            }
        }

        else if (account.getGender() == GenderEnum.여) {
            newChatroom.setWomanStatus(1);
            Chatroom chat = chatRoomRepository.save(newChatroom);
            chatMessageController.message(message);
            if (chat.getManStatus()==1 && chat.getWomanStatus()==1) {
                //chatMessageRepository.deleteAllByChatroomId(id);
                chatRoomRepository.delete(chat);
            }
        }

        return Header.OK();

    }

    public Header<AccountChatListApiResponse> chatInfo(String email) {

        // user
        Account account = accountRepository.findByEmail(email);
        AccountProfileChatApiResponse accountApiResponse = ChatResponse(account);
        // chatlist
        /**
         * 남자유저일 경우 챗리스트 출력
         */
        if(account.getGender()==GenderEnum.남) {
            List<Chatroom> chatroomList = account.getManList_chat();

            List<ChatroomImageResponse> chatroomApiResponseList = chatroomList.stream()
                    .map(chatroom -> {
                        return chatroomApiLogicService.manResponse(chatroom);
                    })
                    .filter(response -> (response).getValue().getManStatus() == 0)
                    .map(response -> (response).getValue())
                    .collect(Collectors.toList());
            accountApiResponse.setChatroomApiResponseList(chatroomApiResponseList);
        }
        /**
         * 여자유저일 경우 챗리스트 출력
         */
        else {
            List<Chatroom> chatroomList = account.getWomanList_chat();

            List<ChatroomImageResponse> chatroomApiResponseList = chatroomList.stream()
                    .map(chatroom -> {
                        return chatroomApiLogicService.womanResponse(chatroom);
                    })
                    .filter(response -> (response).getValue().getWomanStatus() == 0)
                    .map(response -> (response).getValue())
                    .collect(Collectors.toList());
            accountApiResponse.setChatroomApiResponseList(chatroomApiResponseList);
        }


        AccountChatListApiResponse accountChatListApiResponse = AccountChatListApiResponse.builder()
                .accountProfileApiResponse(accountApiResponse)
                .build();
        return Header.OK(accountChatListApiResponse);
    }

    public Header<List<ChatMessageApiResponse>> search(Long id, Pageable pageable) {
        Page<ChatMessage> chatMessages = chatMessageRepository.findByChatMessage(id, pageable);

        List<ChatMessageApiResponse> chatMessageApiResponseList = chatMessages.stream()
                .map(chatMessage -> res(chatMessage))
                .collect(Collectors.toList());

        Pagination pagination = Pagination.builder()
                .totalPages(chatMessages.getTotalPages())
                .totalElements(chatMessages.getTotalElements())
                .currentPage(chatMessages.getNumber()+1)
                .currentElements(chatMessages.getNumberOfElements())
                .build();

        return Header.OK(chatMessageApiResponseList, pagination);
    }

    private ChatMessageApiResponse res(ChatMessage chatMessage) {
        ChatMessageApiResponse body = ChatMessageApiResponse.builder()
                .id(chatMessage.getId())
                .sender(chatMessage.getUserId().getNickName())
                .userId(chatMessage.getUserId().getId())
                .message(chatMessage.getMessage())
                .time(chatMessage.getTime())
                .type(chatMessage.getType())
                .build();
        return body;
    }

    public Header<AccountOtherResponse> other(String email, Long id) {
        Account account = accountRepository.findByEmail(email);
        Optional<Chatroom> room = chatRoomRepository.findById(id);
        Chatroom chatroom = room.get();
        Long otherId=0L;
        if(chatroom.getManId().getId() == account.getId())
            otherId = chatroom.getWomanId().getId();
        else if(chatroom.getWomanId().getId() == account.getId())
            otherId = chatroom.getManId().getId();
        Optional<Account> otherAccount = accountRepository.findById(otherId);
        Account other = otherAccount.get();

        if(other!=null) {
            AccountOtherResponse accountApiResponse = otherResponse(other);
            return Header.OK(accountApiResponse);
        }
        else return Header.ERROR("데이터 없음");
    }

    private AccountOtherResponse otherResponse(Account account) {
        AccountOtherResponse accountProfileApiResponse = AccountOtherResponse.builder()
                .image(account.getImage())
                .gender(account.getGender())
                .grade(account.getGrade())
                .nickName(account.getNickName())
                .content(account.getContent())
                .build();
        return accountProfileApiResponse;
    }
}
