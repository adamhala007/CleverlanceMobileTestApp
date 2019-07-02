package com.adamhala007.cleverlancemobiletestapp;

import com.adamhala007.cleverlancemobiletestapp.utils.SHA1;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;

public class SHA1Test {

    @Test
    public void encryptionTest() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        assertEquals("0e18f44c1fec03ec4083422cb58ba6a09ac4fb2a", SHA1.encrypt("adam"));
        assertEquals("c5983e484db0b621516387b3e50af84020b214c0", SHA1.encrypt("milan"));
        assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", SHA1.encrypt(""));
        assertEquals("", SHA1.encrypt(null));
    }

}
