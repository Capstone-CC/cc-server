package com.cau.cc.service;

import com.cau.cc.ifs.CrudInterface;
import com.cau.cc.model.entity.Chatroom;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.ChatroomApiRequest;
import com.cau.cc.model.network.response.ChatroomApiResponse;
import com.cau.cc.model.repository.ChatRoomRepository;
import com.cau.cc.model.repository.MatchingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatroomApiLogicService implements CrudInterface<ChatroomApiRequest, ChatroomApiResponse> {

    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private MatchingRepository matchingRepository;

    @Override
    public Header<ChatroomApiResponse> create(ChatroomApiRequest request) {

        ChatroomApiRequest body = request;

        Chatroom chatroom = Chatroom.builder()
                .name(body.getName())
                .matchingId(matchingRepository.getOne(body.getMatchingId()))
                .build();

        Chatroom newChatroom = chatRoomRepository.save(chatroom);

        return response(newChatroom);

    }


    @Override
    public Header<ChatroomApiResponse> read(Long id) {
        return chatRoomRepository.findById(id)
                .map(this::response)
                .orElseGet(()->Header.ERROR("데이터 없음"));
    }

    @Override
    public Header<ChatroomApiResponse> update(ChatroomApiRequest request) {
        ChatroomApiRequest body = request;

        return chatRoomRepository.findById(body.getId())
                .map(chatroom -> {
                    chatroom.setName(body.getName());
                    chatroom.setMatchingId(matchingRepository.getOne(body.getMatchingId()));
                    return chatroom;
                })
                .map(newChatroom -> chatRoomRepository.save(newChatroom))
                .map(this::response)
                .orElseGet(()->Header.ERROR("데이터 없음"));
    }

    @Override
    public Header delete(Long id) {
        return chatRoomRepository.findById(id)
                .map(chatroom -> {
                    chatRoomRepository.delete(chatroom);
                    return Header.OK();
                })
                .orElseGet(()->Header.ERROR("데이터 없음"));
    }

    private Header<ChatroomApiResponse> response(Chatroom chatroom) {
        ChatroomApiResponse body = ChatroomApiResponse.builder()
                .id(chatroom.getId())
                .name(chatroom.getName())
                .matchingId(chatroom.getMatchingId().getId())
                .build();
        return Header.OK(body);
    }

}
