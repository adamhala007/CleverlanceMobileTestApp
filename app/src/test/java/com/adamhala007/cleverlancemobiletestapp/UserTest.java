package com.adamhala007.cleverlancemobiletestapp;

import com.adamhala007.cleverlancemobiletestapp.models.User;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

public class UserTest {

    @Test
    public void constructorTest(){
        User user = new User(null,null);
        assertNull(user.getUsername());
        assertNull(user.getPassword());

        user = new User("",null);
        assertEquals("", user.getUsername());
        assertNull(user.getPassword());

        user = new User(null,"");
        assertNull(user.getUsername());
        assertEquals("", user.getPassword());

        user = new User("","");
        assertEquals("", user.getUsername());
        assertEquals("", user.getPassword());

        user = new User("mitosinka","milan");
        assertEquals("mitosinka", user.getUsername());
        assertEquals("milan", user.getPassword());

    }

    @Test
    public void setterTest(){
        User user = new User(null,null);

        user.setUsername("mitosinka");

        assertEquals("mitosinka", user.getUsername());
        assertNotEquals("mitosinka", user.getPassword());

        user.setPassword("milan");

        assertEquals("milan", user.getPassword());
        assertNotEquals("milan", user.getUsername());
    }


}
