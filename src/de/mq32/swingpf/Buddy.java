package de.mq32.swingpf;

/**
 * Created by Felix on 25.06.2014.
 */
public class Buddy {
    private String name;
    private String publicKey;
    private String profileImage;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Buddy[Name="+name+",PublicKey="+publicKey+",ProfileImage="+profileImage+",Age="+age+"]";
    }
}
