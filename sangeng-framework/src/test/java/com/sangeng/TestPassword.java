package com.sangeng;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest(classes = BCryptPasswordEncoder.class)
public class TestPassword {

    @Test
    void testPasswordEncoder(){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("1234");
        System.out.println(encode);//$2a$10$qd/bFDIZIbut74hm58YZT.Mce39..irSAV.Ysp.5f/Za49YJ4kJ.u

//        System.out.println(passwordEncoder.matches("1234",
//                "$2a$10$5iGUq2PwHjtUKms2THLv.etbXp92g7ekBDsb/agCh4Tj8sqUJnzSC"));

    }
}
