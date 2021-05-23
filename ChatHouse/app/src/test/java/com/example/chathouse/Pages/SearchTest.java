package com.example.chathouse.Pages;

import com.example.chathouse.ViewModels.Search.InputRoomSearchViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class SearchTest {

    private SearchRoom testRoom;
    private ArrayList<ArrayList<Integer>> interest = new ArrayList<>();

    @Before
    public void setup(){
        testRoom = new SearchRoom(20,"test room 1", "description 1", new Date(), interest, 10);
    }

    @Test
    public void test1() {
        assertEquals(new Date(), testRoom.getStartDate());
        assertEquals("test room 1", testRoom.getName());
        assertEquals("description 1", testRoom.getDescription());
        assertEquals(interest, testRoom.getInterests());
        assertEquals(20, testRoom.getId());
        assertEquals(10, testRoom.getMembersCount());

    }

}