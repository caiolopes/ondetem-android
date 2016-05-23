package com.icmc.ic.bixomaps.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.util.List;

/**
 * @author caiolopes
 */

@Root(name = "message")
public class MessageResponse {
    @Element(name="reply", required = false)
    Reply reply;

    public Reply getReply() { return this.reply; }
    public void setReply(Reply _value) { this.reply = _value; }

    public static class Reply {
        @ElementList(name = "recommendations", entry = "place", required = false)
        List<Place> recommendations;

        public List<Place> getRecommendations() { return this.recommendations; }
        public void setRecommendations(List<Place> _value) { this.recommendations = _value; }
    }

    public static class Place {
        @Attribute(name="address", required = false)
        String address;

        @Attribute(name="category", required = false)
        String category;

        @Attribute(name="icon", required = false)
        String icon;

        @Attribute(name="id", required = false)
        String id;

        @Attribute(name="lat", required = false)
        String lat;

        @Attribute(name="long", required = false)
        String lng;

        @Attribute(name="name", required = false)
        String name;

        @Attribute(name="phone", required = false)
        String phone;

        @Attribute(name="rating", required = false)
        String rating;

        @Attribute(name="url", required = false)
        String url;

        @Attribute(name="website", required = false)
        String website;

        @ElementList(name = "reviews", entry = "review", required = false)
        List<Reviews> reviews;

        public String getAddress() { return this.address; }
        public void setAddress(String _value) { this.address = _value; }

        public String getCategory() { return this.category; }
        public void setCategory(String _value) { this.category = _value; }

        public String getIcon() { return this.icon; }
        public void setIcon(String _value) { this.icon = _value; }

        public String getId() { return this.id; }
        public void setId(String _value) { this.id = _value; }

        public String getLat() { return this.lat; }
        public void setLat(String _value) { this.lat = _value; }

        public String getLong() { return this.lng; }
        public void setLong(String _value) { this.lng = _value; }

        public String getName() { return this.name; }
        public void setName(String _value) { this.name = _value; }

        public String getPhone() { return this.phone; }
        public void setPhone(String _value) { this.phone = _value; }

        public Float getRating() {
		    /*  Place rating should consider the average of the ratings in the
		     *  reviews, and consider the overall rating when there are no reviews */
            float counter = 0.0f;
            int divBy = 0;
            if (reviews != null) {
                for (Reviews r : reviews) {
                    counter += Float.parseFloat(r.overall_rating);
                    divBy++;
                }
            }
            if (divBy != 0)
                return counter/divBy;
            else
                return Float.parseFloat(rating);
        }
        public void setRating(String _value) { this.rating = _value; }

        public String getUrl() { return this.url; }
        public void setUrl(String _value) { this.url = _value; }

        public String getWebsite() { return this.website; }
        public void setWebsite(String _value) { this.website = _value; }

        public List<Reviews> getReviews() { return this.reviews; }
        public void setReviews(List<Reviews> _value) { this.reviews = _value; }
    }

    public static class Reviews {
        @Attribute(name="id", required = false)
        String id;

        @Attribute(name="language", required = false)
        String language;

        @Attribute(name="overall_rating", required = false)
        String overall_rating;

        @Attribute(name="time", required = false)
        String time;

        @Text(required = false)
        String review;

        public String getId() { return this.id; }
        public void setId(String _value) { this.id = _value; }

        public String getLanguage() { return this.language; }
        public void setLanguage(String _value) { this.language = _value; }

        public String getOverall_rating() { return this.overall_rating; }
        public void setOverall_rating(String _value) { this.overall_rating = _value; }

        public String getTime() { return this.time; }
        public void setTime(String _value) { this.time = _value; }

        public String getText() { return this.review; }
        public void setText(String _value) { this.review = _value; }
    }
}