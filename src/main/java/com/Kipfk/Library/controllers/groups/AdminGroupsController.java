package com.Kipfk.Library.controllers.groups;

import com.Kipfk.Library.appuser.AppUser;
import com.Kipfk.Library.appuser.AppUserRepository;
import com.Kipfk.Library.appuser.Groups;
import com.Kipfk.Library.appuser.GroupsRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.transaction.Transactional;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Controller
public class AdminGroupsController {
    private final AppUserRepository appUserRepository;
    private final GroupsRepository groupsRepository;

    //GROUPS
    @GetMapping("/admin/groups")
    public String showGroupsForm(Model model) {
        List<Groups> groups = groupsRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        model.addAttribute("groups", groups);
        model.addAttribute("group", new Groups());
        return "groups";
    }

    @PostMapping("/admin/groups/save")
    public String saveNewGroup(Groups groups) {
        groupsRepository.save(groups);
        return "redirect:/admin/groups?success";
    }
    @Transactional
    @PostMapping("/admin/groups/{groupid}/delete")
    public String deleteGroup(@PathVariable Long groupid) {
        List<AppUser> users = appUserRepository.findAllByGroups_IdOrderByLastName(groupid);
        for (AppUser user : users){
            user.setGroups(null);
        }
        appUserRepository.saveAll(users);
        groupsRepository.deleteById(groupid);
        return "redirect:/admin/groups?deleted";
    }
    @PostMapping("/admin/groups/{groupId}/{groupName}/update")
    public String updateGroupName(@PathVariable Long groupId,@PathVariable String groupName) {
        Groups group = groupsRepository.findAllById(groupId);
        group.setName(groupName);
        groupsRepository.save(group);
        return "redirect:/admin/groups?saved";
    }
}
