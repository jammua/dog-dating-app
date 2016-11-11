package dateadog.dateadog;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * {@code DADAPI} interfaces with the Date-A-Dog server and the Petfinder API to retrieve and
 * update information about the user and dogs available to them.
 */
public class DADAPI {

    private static final String TAG = DADAPI.class.getName();

    private static String PETFINDER_API_KEY = "d025e514d458e4366c42ea3006fd31b3";
    private static String PETFINDER_URL_BASE = "http://api.petfinder.com/pet.find&format=json&animal=dog&output=full&count=100?key=" + PETFINDER_API_KEY;
    private static String DAD_SERVER_URL_BASE = "http://localhost:3000/";

    private User user;
    private int zipCode;
    private int lastOffset;
    private URL petfinderURL;

    public DADAPI(User user, int zipCode) {
        this.user = user;
        updateLocation(zipCode);
    }

    public void seenDog(Dog dog) {
        JSONObject obj = new JSONObject();
        // execute method and handle any error responses.
    }

    private Set<Dog> filterSeenDogs(Set<Dog> result) {
        // backend.getSeenDogs(I) | filter result
        return null;
    }


    public void updateLocation(int zipCode) {
        this.zipCode = zipCode;
        this.lastOffset = 0;

        try {
            petfinderURL = new URL(PETFINDER_URL_BASE + "&location=" + zipCode + "&offset=" + lastOffset);
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public Set<Dog> getNextDogs(int zipCode) {
        Set<Dog> result = new LinkedHashSet<>();

        /*
        try {
            // JSONObject petfinder = new JSONObject(IOUtils.toString(petfinderURL, Charset.forName("UTF-8")))
                    .getJSONObject("petfinder");
            // lastOffset = petfinder.getJSONObject("lastOffset").getInt("$t");
            JSONArray dogs = petfinder.getJSONArray("pets");

            for (int i = 0; i < dogs.length(); i++) {
                JSONObject dog = dogs.getJSONObject(i);
                // result.add(new Dog(dog));
            }

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            // TODO: handle network error.
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            // TODO: handle error with Petfinder's returned JSON.
        }
        */

        return filterSeenDogs(result);
    }

    private void judgeDog(Dog dog, boolean like) {

    }

    public void likeDog(Dog dog) {

    }

    public void dislikeDog(Dog dog) {

    }

    public Set<DateRequest> getRequests() {
        return null;
    }

    public void requestDate(Dog dog, Form form) {

    }

}