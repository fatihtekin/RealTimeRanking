package com.socialpoint.ranking.service;

/**
 * setters are not created so that object becomes immutable
 * if there was be more parameters then Builder pattern could have been used not to have too many constructors but not necessary currently
 */
public class User{

    private final long userID;
    private final long rank;

    public User(final long userID) {
        this.userID = userID;
        rank = 0;
    }

    public User(long userID, long rank) {
        this.userID = userID;
        this.rank = rank;
    }
    
    public long getRank() {
        return rank;
    }

    public long getUserID() {
        return userID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        return userID == user.userID;
    }

    @Override
    public int hashCode() {
        return (int) (userID ^ (userID >>> 32));
    }

}