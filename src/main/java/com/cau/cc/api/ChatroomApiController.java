package com.cau.cc.api;

import com.cau.cc.ifs.CrudInterface;
import com.cau.cc.model.entity.Account;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.ChatroomApiRequest;
import com.cau.cc.model.network.response.AccountChatListApiResponse;
import com.cau.cc.model.network.response.ChatroomApiResponse;
import com.cau.cc.service.ChatroomApiLogicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/chatroom")
public class ChatroomApiController implements CrudInterface<ChatroomApiRequest, ChatroomApiResponse> {

    @Autowired
    private ChatroomApiLogicService chatroomApiLogicService;

    @Override
    @PostMapping("")
    public Header<ChatroomApiResponse> create(@RequestBody ChatroomApiRequest request) {
        return chatroomApiLogicService.create(request);
    }

    @Override
    @GetMapping("{id}")
    public Header<ChatroomApiResponse> read(@PathVariable Long id) {
        return chatroomApiLogicService.read(id);
    }

    @Override
    @PutMapping("")
    public Header<ChatroomApiResponse> update(@RequestBody ChatroomApiRequest request) {
        return chatroomApiLogicService.update(request);
    }

    @Override
    @DeleteMapping("{id}")
    public Header delete(@PathVariable Long id) {
        return chatroomApiLogicService.delete(id);
    }

    @GetMapping("/list")
    public Header<List<ChatroomApiResponse>> search(@PageableDefault(sort = "id", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {
        return chatroomApiLogicService.search(pageable);
    }

    /**
     * 채팅방 ID에 해당하는 메세지 LIST 읽어오기
     */


}
