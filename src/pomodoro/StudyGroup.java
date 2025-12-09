package src.pomodoro;

import java.util.ArrayList;

public class StudyGroup {
    private String groupName;
    private User creator;
    private ArrayList<User> members;
    private boolean isGroupStudyReady = false;
    private ArrayList<User> readyMembers; 

    public StudyGroup(String groupName, User creator) {
        this.groupName = groupName;
        this.creator = creator;
        this.members = new ArrayList<>();
        this.members.add(creator);
        this.readyMembers = new ArrayList<>();        
    }

    public void addMember(User newMember) {
        if (!members.contains(newMember)) {
            members.add(newMember);
        }
    }

    public void removeMember(User memberToRemove) {
        if (members.contains(memberToRemove)) {
            members.remove(memberToRemove);
            readyMembers.remove(memberToRemove); 
            checkIsAllReady();
        }
    }
    
    public void setMemberReady(User user, boolean ready) {
        if (!members.contains(user)) {
            return; 
        }
        if (ready && !readyMembers.contains(user)) {
            readyMembers.add(user);
        } 
        else if (!ready && readyMembers.contains(user)) {
            readyMembers.remove(user);
        }
        checkIsAllReady(); 
    }
    
    public boolean checkIsAllReady() {
        if (members.size() > 0 && readyMembers.size() == members.size()) {
            this.isGroupStudyReady = true;
            return true;
        } 
        else {
            this.isGroupStudyReady = false;
            return false;
        }
    }
    
    public boolean isAllReady() {
        return isGroupStudyReady;
    }

    public ArrayList<User> getMembers() {
        return members;
    }

    public String getStudyGroupName(){
        return groupName;
    }

    public User getCreator(){
        return creator;
    }
    
    public ArrayList<User> getReadyMembers() {
        return readyMembers;
    }
}
