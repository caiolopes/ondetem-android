package com.icmc.ic.bixomaps.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

/**
 *
 * Created by caiolopes on 5/23/16.
 */
@Root(name = "message")
public class EventRequest {
    @Element(name="notify", required = false)
    Notify notify;

    public Notify getNotify() { return this.notify; }
    public void setNotify(Notify _value) { this.notify = _value; }

    public static class Notify {
        @Element(name="event", required = false)
        Event event;

        @Element(name="user", required = false)
        User user;

        @Element(name="place", required = false)
        Place place;

        public Event getEvent() { return this.event; }
        public void setEvent(Event _value) { this.event = _value; }

        public User getUser() { return this.user; }
        public void setUser(User _value) { this.user = _value; }

        public Place getPlace() { return this.place; }
        public void setPlace(Place _value) { this.place = _value; }
    }

    public static class Event {
        public static String CHECK_IN = "check-in";
        public static String REVIEW = "review";
        public static String CLICK = "click";
        public static String DIRECTION = "direction";

        @Attribute(name="type", required = false)
        String type;

        public String getType() { return this.type; }
        public void setType(String _value) { this.type = _value; }
    }

    public static class User {
        @Attribute(name="id", required = false)
        String id;

        @Attribute(name="date", required = false)
        String date;

        @Attribute(name="lat", required = false)
        String lat;

        @Attribute(name="long", required = false)
        String lng;

        public String getId() { return this.id; }
        public void setId(String _value) { this.id = _value; }

        public String getDate() { return this.date; }
        public void setDate(String _value) { this.date = _value; }

        public String getLat() { return this.lat; }
        public void setLat(String _value) { this.lat = _value; }

        public String setLng() { return this.lng; }
        public void setLng(String _value) { this.lng = _value; }
    }

    public static class Place {

        @Element(name="review", required = false)
        Review review;

        @Attribute(name="id", required = false)
        String id;

        @Attribute(name="category", required = false)
        String category;

        public Review getReview() { return this.review; }
        public void setReview(Review _value) { this.review = _value; }

        public String getId() { return this.id; }
        public void setId(String _value) { this.id = _value; }

        public String getCategory() { return this.category; }
        public void setCategory(String _value) { this.category = _value; }
    }

    public static class Review {
        @Attribute(name="language", required = false)
        String language;

        @Attribute(name="rating", required = false)
        String rating;

        @Text(required = false)
        String review;

        public String getLanguage() { return this.language; }
        public void setLanguage(String _value) { this.language = _value; }

        public String getRating() { return this.rating; }
        public void setRating(String _value) { this.rating = _value; }

        public String getText() { return this.review; }
        public void setText(String _value) { this.review = _value; }
    }
}
