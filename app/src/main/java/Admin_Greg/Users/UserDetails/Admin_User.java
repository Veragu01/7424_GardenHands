package Admin_Greg.Users.UserDetails;

public class Admin_User {

    String userId;
    private String name;
    private String email;
    private String user_group;
    private String phone;
    private String authorised;

    public Admin_User() {
        // Required for Firebase
    }

    public String getName() {
        return name;
    }

    public String getEmail(){

        return email;
    }

    public String getUser_group() {

        return user_group;
    }

    public String getPhone() {

        return phone;
    }

    public String getAuthorised() {

        return authorised;
    }

    public String getUserId(){

        return userId;
    }

    public void setUserId(String userId){

        this.userId = userId;
    }

}