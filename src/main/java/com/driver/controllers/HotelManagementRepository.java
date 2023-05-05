package com.driver.controllers;

import com.driver.model.Booking;
import com.driver.model.Facility;
import com.driver.model.Hotel;
import com.driver.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

@Repository
public class HotelManagementRepository {

    TreeMap<String, Hotel> hotelDb = new TreeMap<>();
    HashMap<Integer, User> userDb = new HashMap<>();

    HashMap<String, Booking> bookingDb = new HashMap<>();


    public String addHotel(Hotel hotel){
        if(hotelDb.containsKey(hotel.getHotelName())){
            return "FAILURE";
        }
        if(hotel.getHotelName() == null || hotel == null){
            return "FAILURE";
        }
        hotelDb.put(hotel.getHotelName(), hotel);
        return "SUCCESS";
    }

    public Integer addUser(@RequestBody User user){
        userDb.put(user.getaadharCardNo(), user);
        return user.getaadharCardNo();
    }

    public String getHotelWithMostFacilities(){
        int noOfFacility = 0;
        String ans = "";
        for(String hotelName : hotelDb.keySet()){
            Hotel hotel = hotelDb.get(hotelName);
            if(hotel.getFacilities().size() > noOfFacility){
                ans = hotelName;
            }
        }
        return ans;
    }

    public int bookARoom(Booking booking){
        //The booking object coming from postman will have all the attributes except bookingId and amountToBePaid;
        //Have bookingId as a random UUID generated String
        //save the booking Entity and keep the bookingId as a primary key
        //Calculate the total amount paid by the person based on no. of rooms booked and price of the room per night.
        //If there arent enough rooms available in the hotel that we are trying to book return -1
        //in other case return total amount paid

        UUID uuid = UUID.randomUUID();
        String bookingId = uuid.toString();
        booking.setBookingId(bookingId);

        bookingDb.put(bookingId, booking);

        String hotelName = booking.getHotelName();
        Hotel hotel = hotelDb.get(hotelName);
        int pricePerNight = hotel.getPricePerNight();
        int noOfRooms = booking.getNoOfRooms();
        int availableRooms = hotel.getAvailableRooms();
//        ***** revisit in case to update available rooms. *****
        if(noOfRooms > availableRooms){
            return -1;
        }
        int amountPaid = noOfRooms * pricePerNight;
        return amountPaid;
    }

    public int getBookings(Integer aadharCard) {
        int ans = 0;
        for(String bookingId : bookingDb.keySet()){
            Booking booking = bookingDb.get(bookingId);
            if(booking.getBookingAadharCard() == aadharCard){
                ans++;
            }
        }
        return ans;
    }

    public Hotel updateFacilities(List<Facility> newFacilities, String hotelName){
        //We are having a new facilites that a hotel is planning to bring.
        //If the hotel is already having that facility ignore that facility otherwise add that facility in the hotelDb
        //return the final updated List of facilities and also update that in your hotelDb
        //Note that newFacilities can also have duplicate facilities possible

        Hotel hotel = hotelDb.get(hotelName);
        List<Facility> currentFacilities = hotel.getFacilities();

        for(Facility facility : newFacilities){
            if(currentFacilities.contains(facility)){
                continue;
            }
            else{
                currentFacilities.add(facility);
            }
        }
        hotel.setFacilities(currentFacilities);
        hotelDb.put(hotelName, hotel);

        return hotel;
    }
}
