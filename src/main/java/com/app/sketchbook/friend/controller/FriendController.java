package com.app.sketchbook.friend.controller;

import com.app.sketchbook.friend.entity.Friend;
import com.app.sketchbook.friend.entity.FriendStatus;
import com.app.sketchbook.friend.service.FriendService;
import com.app.sketchbook.post.entity.Post;
import com.app.sketchbook.user.entity.SketchUser;
import com.app.sketchbook.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
@Controller
@RequestMapping("/friend")
public class FriendController {
    //무한 스크롤 개발 담당 : 김범철
    //친구 관리 : 한수민
    private final FriendService friendService;
    private final UserService userService;

    //친구 목록 보여주기
    @GetMapping("/list")
    public String friendList(Model model) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        //List<Friend> friends = friendService.getFriends(user);
        return "friend_list";
    }

    @GetMapping("/list/{pageNumber}")
    public String list_user(Model model, @PathVariable int pageNumber) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        List<List<SketchUser>> friends = friendService.testgetFriends(user);
        List<SketchUser>userlist = friends.get(pageNumber);
        if (pageNumber<userlist.size()) {
            log.info("enter");
            model.addAttribute("nextPageNumber", pageNumber + 1);
        }
        model.addAttribute("userList", userlist);


        return "next-friend-list";
    }
    @GetMapping("/request/list")
    public String friendRequestList(Model model){
        return "request_list";
    }
    //친구 요청한 목록
    @GetMapping("/request/list/{pageNumber}")
    public String friendRequestList(Model model, @PathVariable int pageNumber) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        List<List<SketchUser>> friends = friendService.getRequestFriend(user);
        List<SketchUser>userlist = friends.get(pageNumber);
        if (pageNumber<userlist.size()) {
            log.info("enter");
            model.addAttribute("nextPageNumber", pageNumber + 1);
        }
        model.addAttribute("requestList", userlist);
        return "next-request-list";
    }

    //친구 요청받은 목록
    @GetMapping("/requested/list")
    public String friendRequestedList(Model model) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        return "requested_list";
    }

    @GetMapping("/requested/list/{pageNumber}")
    public String friendRequestedList(Model model, @PathVariable int pageNumber ) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        List<List<SketchUser>> friends = friendService.getRequestedFriend(user);
        List<SketchUser>userlist = friends.get(pageNumber);
        if (pageNumber<userlist.size()) {
            log.info("enter");
            model.addAttribute("nextPageNumber", pageNumber + 1);
        }
        model.addAttribute("requestedList", userlist);
        return "next-requested-list";
    }

    //사용자 차단 목록
    @GetMapping("/blacklist")
    public String blackList(Model model){
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        return "blacklist";
    }
    @GetMapping("/blacklist/{pageNumber}")
    public String blackList(Model model, @PathVariable int pageNumber){
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        List<List<SketchUser>> friends = friendService.getBlacklist(user);
        List<SketchUser>userlist = friends.get(pageNumber);
        if (pageNumber<userlist.size()) {
            log.info("enter");
            model.addAttribute("nextPageNumber", pageNumber + 1);
        }
        model.addAttribute("blackList", userlist);
        return "next-black-list";
    }

    //친구 찾기
    @GetMapping("/search")
    public String friendSearch(@RequestParam("keyword") String keyword, Model model) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        List<SketchUser> friends = friendService.searchFriends(user, keyword);
        model.addAttribute("friends", friends);
        return "friend_search";
    }

    //사용자 검색
    @GetMapping("/usersearch")
    public String userSearch(@RequestParam("keyword") String keyword, Model model) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
       // Map<SketchUser, FriendStatus> users = friendService.searchAllUsers(user, keyword);
        model.addAttribute("query", keyword);
        return "user_search";
    }

    //사용자 검색후 무한 스크롤 표현
    @GetMapping("/user_search/{query}/{pageNumber}")
    public String search_userlist(@PathVariable String query,Model model, @PathVariable int pageNumber) {
        Slice<SketchUser> users = friendService.fetchUsersByPage(query,pageNumber);
        List<SketchUser>userlist = users.getContent();
        if (users.hasNext()) {
            log.info("enter");
            model.addAttribute("nextPageNumber", pageNumber + 1);
            model.addAttribute("query", query);
        }
        model.addAttribute("userList", userlist);


        return "search-user-result";
    }

    //친구 요청
    @PostMapping("/request")
    public String friendRequest(@RequestParam Long friendId, Model model) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        String message = friendService.requestFriend(user, friendId);
        model.addAttribute("message", message);
        return "redirect:/friend/request/list";
    }

    //친구 요청 취소
    @PostMapping("/cancel")
    public String friendRequestCancel(@RequestParam Long friendId, Model model) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        String message = friendService.cancelFriendRequest(user, friendId);
        model.addAttribute("message", message);
        return "redirect:/friend/request/list";
    }

    //친구 수락
    @PostMapping("/accept")
    public String friendRequestAccept(@RequestParam Long friendId, Model model) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        String message = friendService.acceptFriendRequest(user, friendId);
        model.addAttribute("message", message);
        return "redirect:/friend/list";
    }

    //친구 거절
    @PostMapping("/reject")
    public String friendRequestReject(@RequestParam Long friendId, Model model) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        String message = friendService.rejectFriendRequest(user, friendId);
        model.addAttribute("message", message);
        return "redirect:/friend/request/list";
    }

    //친구 삭제
    @PostMapping("/delete")
    public String friendDelete(@RequestParam Long friendId, Model model) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        String message = friendService.deleteFriend(user, friendId);
        model.addAttribute("message", message);
        return "redirect:/friend/list";
    }

    //사용자 차단
    @PostMapping("/block")
    public String blockUser(@RequestParam Long blockId, Model model) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        String message = friendService.blockUser(user, blockId);
        model.addAttribute("message", message);
        return "redirect:/friend/list";
    }

    //사용자 차단 해제
    @PostMapping("/unblock")
    public String unblockUser(@RequestParam Long blockId, Model model) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        String message = friendService.unblockUser(user, blockId);
        model.addAttribute("message", message);
        return "redirect:/friend/list";
    }
}