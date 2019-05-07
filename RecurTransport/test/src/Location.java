
import java.util.Objects;


public class Location {
    private String locationName;
    private String locationID;

    public Location(String locationName, String locationID) {
        this.locationName = locationName;
        this.locationID = locationID;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }

    @Override
    public String toString() {
        return locationName + "("+locationID+")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Location other = (Location) obj;
        return this.locationID.equals(other.locationID) ;
    }
    
    
    
}
