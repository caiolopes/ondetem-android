package com.icmc.ic.bixomaps.models;

/**
 * @author caiolopes
 */

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "message")
public class MessageRequest {
    @Element(name="recommend", required = false)
    Recommend recommend;

    public Recommend getRecommend() { return this.recommend; }
    public void setRecommend(Recommend _value) { this.recommend = _value; }

    public static class Recommend {
        @Element(name="user", required = false)
        User user;

        @Element(name="place", required = false)
        Place place;

        public User getUser() { return this.user; }
        public void setUser(User _value) { this.user = _value; }

        public Place getPlace() { return this.place; }
        public void setPlace(Place _value) { this.place = _value; }
    }

    public static class User {
        @ElementList(name = "profile", entry = "preference", required = false)
        List<String> profile;

        @Attribute(name="id", required = false)
        String id;

        @Attribute(name="date", required = false)
        String date;

        @Attribute(name="lat", required = false)
        String lat;

        @Attribute(name="long", required = false)
        String lng;

        public List<String> getProfile() { return this.profile; }
        public void setProfile(List<String> _value) { this.profile = _value; }

        public String getId() { return this.id; }
        public void setId(String _value) { this.id = _value; }

        public String getDate() { return this.date; }
        public void setDate(String _value) { this.date = _value; }

        public String getLat() { return this.lat; }
        public void setLat(String _value) { this.lat = _value; }

        public String getLong() { return this.lng; }
        public void setLong(String _value) { this.lng = _value; }
    }

    public static class Place {
        @Attribute(name="category", required = false)
        String category;

        public String getCategory() { return this.category; }
        public void setCategory(String _value) { this.category = _value; }
    }
}
