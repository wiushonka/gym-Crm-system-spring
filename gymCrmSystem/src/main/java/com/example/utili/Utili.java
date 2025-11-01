package com.example.utili;

import org.jetbrains.annotations.NotNull;
import java.security.SecureRandom;
import java.util.function.Predicate;

public class Utili {

    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    private Utili(){}

    public static @NotNull String generateUsername(String firstName, String lastName, @NotNull Predicate<String> exists){
        String username = firstName + "." + lastName;
        int serialNum=1;
        String tmp=username;
        while(exists.test(tmp)) {
            tmp = username + serialNum;
            ++serialNum;
        }

        return tmp;
    }

    public static @NotNull String generatePassword(){
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<10;++i){
            sb.append(CHAR_POOL.charAt(random.nextInt(CHAR_POOL.length())));
        }
        return sb.toString();
    }

    public static @NotNull String generateUniqueNameForTraining(String name,@NotNull Predicate<String> exists){
        int serialNum=1;
        String tmp=name;
        while(exists.test(tmp)) {
            tmp = name + "_" + serialNum;
            ++serialNum;
        }

        return tmp;
    }
}
