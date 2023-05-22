package com.Kipfk.Library.controllers.user;

import com.Kipfk.Library.appbook.*;
import com.Kipfk.Library.appuser.*;
import com.Kipfk.Library.registration.token.ConfirmationToken;
import com.Kipfk.Library.registration.token.ConfirmationTokenRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Getter
@Setter
@AllArgsConstructor
@Controller

public class AdminUserController {
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final AppUserService appUserService;
    private final AppBookService appBookService;
    private final AppUserRepository appUserRepository;
    private final TakenBooksRepository takenBooksRepository;
    private final LikedBooksRepository likedBooksRepository;
    private final GroupsRepository groupsRepository;
    private final BookOrdersRepository bookOrdersRepository;

    @GetMapping("/admin/adduser")
    public String showAddUserForm(Model model) {
        List<Groups> groups = groupsRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        model.addAttribute("groups", groups);
        model.addAttribute("user", new AppUser());
        return "adduser";
    }

    @PostMapping("/admin/processuseradd")
    public String signUpByAdd(AppUser user, @RequestParam String groupid, @RequestParam String role) {
        boolean UserExists = appUserRepository.findByEmail(user.getEmail()).isPresent();
        if(UserExists){
            return "redirect:/admin/adduser?emailpresent";
        }
        if (groupid.equals("")){
            user.setGroups(null);
        }else {
            user.setGroups(groupsRepository.findAllById(Long.valueOf(groupid)));
        }
        if (role.equals("ADMIN")){
            user.setAppUserRole(AppUserRole.ADMIN);
        }
        if (role.equals("USER")){
            user.setAppUserRole(AppUserRole.USER);
        }
        if (role.equals("TEACHER")){
            user.setAppUserRole(AppUserRole.TEACHER);
        }
        appUserService.signUpUser(user);
        appUserService.enableAppUser(user.getEmail());
        return "redirect:/admin/adduser?useradded";
    }

    @GetMapping("/admin/allusers")
    public String listUsers(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size, @RequestParam("category") Optional<String> category) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(12);
        String categor = "";
        Page<AppUserRepository.UserNoPhoto> userPage;
        if (category.isPresent()){
            categor = category.get();
        }
        if (categor.equals("students")) {
            userPage = appUserRepository.findAllByAppUserRoleAndEnabledIsTrue(PageRequest.of(currentPage - 1, pageSize), AppUserRole.USER);
        } else if (categor.equals("teachers")){
            userPage = appUserRepository.findAllByAppUserRoleAndEnabledIsTrue(PageRequest.of(currentPage - 1, pageSize), AppUserRole.TEACHER);
        }else{
            userPage = appUserRepository.findAllByEnabledIsTrue(PageRequest.of(currentPage - 1, pageSize));
        }
        model.addAttribute("Users",userPage);
        model.addAttribute("body", appBookService.bodyArrayForPages(userPage));
        model.addAttribute("category", categor);
        return "allusers";
    }

    @GetMapping("/admin/allusers/{id}/edit")
    public String AdminUserEdit(@PathVariable(value = "id") long id, Model model){
        if (!appUserRepository.existsById(id)){
            return "redirect:/allusers";
        }
        List<Groups> groups = groupsRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        model.addAttribute("groups", groups);
        Optional <AppUser> user = appUserRepository.findById(id);
        ArrayList<AppUser> ruser = new ArrayList<>();
        user.ifPresent(ruser::add);
        model.addAttribute("userd", ruser);
        return "useradminedit";
    }
    @PostMapping("/admin/allusers/{id}/edit")
    public String AdminUserUpdate(@PathVariable(value = "id") long id,@RequestParam String firstname, @RequestParam String lastname, @RequestParam String phonenum, @RequestParam String password, @RequestParam String email, @RequestParam String groupid,@RequestParam String role) {
        AppUser user = appUserRepository.findById(id).orElseThrow();
        user.setFirstName(firstname);
        user.setLastName(lastname);
        user.setEmail(email);
        user.setPhonenum(phonenum);
        user.setPassword(password);
        if (groupid.equals("")){
            user.setGroups(null);
        }else {
            user.setGroups(groupsRepository.findAllById(Long.valueOf(groupid)));
        }
        if (role.equals("ADMIN")){
            user.setAppUserRole(AppUserRole.ADMIN);
        }
        if (role.equals("USER")){
            user.setAppUserRole(AppUserRole.USER);
        }
        if (role.equals("TEACHER")){
            user.setAppUserRole(AppUserRole.TEACHER);
        }
        appUserRepository.save(user);
        return "redirect:/admin/allusers?changessaved";
    }
    @PostMapping("/admin/allusers/{id}/remove")
    public String AdminUserDelete(@PathVariable(value = "id") long id) {
        AppUser user = appUserRepository.findById(id).orElseThrow();
        boolean tokenpresent = confirmationTokenRepository.findByAppUser(user).isPresent();
        boolean takenBookspresent = takenBooksRepository.findByUser(user).isEmpty();
        if (!takenBookspresent){
            return "redirect:/admin/usertakenadmin/"+id+"?notreturn";
        }else {
            List<LikedBooks> likedBooks = likedBooksRepository.findAllByUser(user);
            if (!likedBooks.isEmpty()){
                likedBooksRepository.deleteAll(likedBooks);
            }
            if (tokenpresent){
                ConfirmationToken token = confirmationTokenRepository.findByAppUser(user).get();
                confirmationTokenRepository.delete(token);
            }
            List<BookOrders> bookOrders = bookOrdersRepository.findAllByUser(user);
            if (!bookOrders.isEmpty()){
                bookOrdersRepository.deleteAll(bookOrders);
            }
            appUserRepository.delete(user);
            return "redirect:/admin/allusers?success";
        }
    }

//USERS BY GROUP
    @GetMapping("/admin/usersbygroup/{groupid}")
    public String showUsersByGroup(Model model, @PathVariable Long groupid) {
        Groups group = groupsRepository.findAllById(groupid);
        List<AppUser> users = appUserRepository.findAllByGroups_Id(groupid);
        model.addAttribute("users", users);
        model.addAttribute("group", group);
        return "usersByGroup";
    }

    @PostMapping("/admin/usersbygroup/{groupid}/{userid}")
    public String deleteUserFromGroup(@PathVariable Long groupid,@PathVariable Long userid) {
        AppUser user = appUserRepository.findAllById(userid);
        user.setGroups(null);
        appUserRepository.save(user);
        return "redirect:/admin/usersbygroup/"+groupid+"?userdeletedfromgroup";
    }

}
