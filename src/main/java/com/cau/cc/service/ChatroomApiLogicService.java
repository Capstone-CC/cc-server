package com.cau.cc.service;

import com.cau.cc.model.entity.Account;
import com.cau.cc.model.entity.ChatMessage;
import com.cau.cc.model.entity.Chatroom;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.ChatroomApiRequest;
import com.cau.cc.model.network.response.AccountApiResponse;
import com.cau.cc.model.network.response.ChatroomApiResponse;
import com.cau.cc.model.network.response.ChatroomImageResponse;
import com.cau.cc.model.repository.AccountRepository;
import com.cau.cc.model.repository.ChatMessageRepository;
import com.cau.cc.model.repository.ChatRoomRepository;
import com.cau.cc.page.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatroomApiLogicService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public Header<ChatroomApiResponse> create(ChatroomApiRequest request) {

        ChatroomApiRequest body = request;

        Chatroom chatroom = Chatroom.builder()
                .name(body.getName())
                .time(request.getTime())
                .manId(accountRepository.getOne(body.getManId()))
                .womanId(accountRepository.getOne(body.getWomanId()))
                .manStatus(0)
                .womanStatus(0)
                .build();

        Chatroom newChatroom = chatRoomRepository.save(chatroom);

        return response(newChatroom);

    }

    public Header<List<ChatroomApiResponse>> search(Pageable pageable) {
        Page<Chatroom> chatrooms = chatRoomRepository.findAll(pageable);

        List<ChatroomApiResponse> chatroomApiResponseList = chatrooms.stream()
                .map(chatroom -> res(chatroom))
                .collect(Collectors.toList());

        Pagination pagination = Pagination.builder()
                .totalPages(chatrooms.getTotalPages())
                .totalElements(chatrooms.getTotalElements())
                .currentPage(chatrooms.getNumber())
                .currentElements(chatrooms.getNumberOfElements())
                .build();

        return Header.OK(chatroomApiResponseList, pagination);
    }
    private ChatroomApiResponse res(Chatroom chatroom) {
        ChatroomApiResponse body = ChatroomApiResponse.builder()
                .id(chatroom.getId())
                .name(chatroom.getName())
                .manId(chatroom.getManId().getId())
                .womanId(chatroom.getWomanId().getId())
                .build();
        return body;
    }

//    public Header<ChatroomApiResponse> read(Long id) {
//        return chatRoomRepository.findById(id)
//                .map(this::response)
//                .orElseGet(()->Header.ERROR("데이터 없음"));
//    }

    private AccountApiResponse resp(Account user) {
        // user -> userApiResponse

        AccountApiResponse userApiResponse = AccountApiResponse.builder()
                .email(user.getEmail())
                .gender(user.getGender())
                .grade(user.getGrade())
                .nickName("푸앙이")
                .majorName(user.getMajorName())
                .build();

        // Header + data return
        return userApiResponse;
    }

//    public Header<ChatroomApiResponse> update(ChatroomApiRequest request) {
//        ChatroomApiRequest body = request;
//
//        return chatRoomRepository.findById(body.getId())
//                .map(chatroom -> {
//                    chatroom.setName(body.getName());
//                    return chatroom;
//                })
//                .map(newChatroom -> chatRoomRepository.save(newChatroom))
//                .map(this::response)
//                .orElseGet(()->Header.ERROR("데이터 없음"));
//    }

    public Header delete(Long id) {
        return chatRoomRepository.findById(id)
                .map(chatroom -> {
                    chatRoomRepository.delete(chatroom);
                    return Header.OK();
                })
                .orElseGet(()->Header.ERROR("데이터 없음"));
    }

    public Header<ChatroomApiResponse> response(Chatroom chatroom) {
        ChatroomApiResponse body = ChatroomApiResponse.builder()
                .id(chatroom.getId())
                .name(chatroom.getName())
                .manId(chatroom.getManId().getId())
                .womanId(chatroom.getWomanId().getId())
                .build();
        return Header.OK(body);
    }

    public Header<ChatroomImageResponse> womanResponse(Chatroom chatroom) {
        Account man = accountRepository.findById(chatroom.getManId().getId())
                .orElseGet(null);
        ChatMessage chatMessage = chatMessageRepository.findLastMessage(chatroom.getId());
        String lastMessage = null;
        if (chatMessage == null) {
            lastMessage = " ";
        }
        else {
            lastMessage = chatMessage.getMessage();
        }
        ChatroomImageResponse body = ChatroomImageResponse.builder()
                .id(chatroom.getId())
                .name(chatroom.getManId().getNickName())
                .manId(chatroom.getManId().getId())
                .womanId(chatroom.getWomanId().getId())
                .manStatus(chatroom.getManStatus())
                .womanStatus(chatroom.getWomanStatus())
                .otherImg(man.getImage())
                .lastMessage(lastMessage)
                .build();
        return Header.OK(body);

    }

    public Header<ChatroomImageResponse> manResponse(Chatroom chatroom) {
        Account woman = accountRepository.findById(chatroom.getWomanId().getId())
                .orElseGet(null);
        ChatMessage chatMessage = chatMessageRepository.findLastMessage(chatroom.getId());
        String lastMessage = null;
        if (chatMessage == null) {
            lastMessage = " ";
        }
        else {
            lastMessage = chatMessage.getMessage();
        }

        ChatroomImageResponse body = null;

        body = ChatroomImageResponse.builder()
                .id(chatroom.getId())
                .name(chatroom.getWomanId().getNickName())
                .manId(chatroom.getManId().getId())
                .womanId(chatroom.getWomanId().getId())
                .manStatus(chatroom.getManStatus())
                .womanStatus(chatroom.getWomanStatus())
                .otherImg(woman.getImage())
                .lastMessage(lastMessage)
                .build();
        return Header.OK(body);

    }



}
