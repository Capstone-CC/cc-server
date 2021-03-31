package com.cau.cc.api;

import com.cau.cc.ifs.CrudInterface;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.ChatroomApiRequest;
import com.cau.cc.model.network.response.ChatroomApiResponse;
import com.cau.cc.service.ChatroomApiLogicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatroom")
public class ChatroomApiController implements CrudInterface<ChatroomApiRequest, ChatroomApiResponse> {

    @Autowired
    private ChatroomApiLogicService chatroomApiLogicService;

    @Override
    @PostMapping("")
    public Header<ChatroomApiResponse> create(@RequestBody Header<ChatroomApiRequest> request) {
        return chatroomApiLogicService.create(request);
    }

    @Override
    @GetMapping("{id}")
    public Header<ChatroomApiResponse> read(@PathVariable Long id) {
        return chatroomApiLogicService.read(id);
    }

    @Override
    @PutMapping("")
    public Header<ChatroomApiResponse> update(@RequestBody Header<ChatroomApiRequest> request) {
        return chatroomApiLogicService.update(request);
    }

    @Override
    @DeleteMapping("{id}")
    public Header delete(@PathVariable Long id) {
        return chatroomApiLogicService.delete(id);
    }
}
