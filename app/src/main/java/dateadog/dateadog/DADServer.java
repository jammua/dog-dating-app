/****************************************
 * Copyright (c) 2016 Date-A-Dog.       *
 * All rights reserved.                 *
 ***************************************/

package dateadog.dateadog;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DADServer communicates with the DAD server to retrieve and update data about dog profiles and users.
 */
public class DADServer {

    /** Holds the sole instance of this class. */
    private static DADServer instance;
    /** Used to identify this class in logging messages. */
    private static String TAG = DADServer.class.getName();
    /** URLs for server endpoints. */
    private static String DAD_SERVER_URL_BASE = "http://ec2-35-160-226-75.us-west-2.compute.amazonaws.com/api/";
    private static String GET_NEXT_DOGS_URL = DAD_SERVER_URL_BASE + "getNextDogs";
    private static String GET_LIKED_DOGS_URL = DAD_SERVER_URL_BASE + "getLikedDogs";
    private static String JUDGE_DOG_URL = DAD_SERVER_URL_BASE + "judgeDog";
    private static String REQUEST_DATE_URL = DAD_SERVER_URL_BASE + "requestDate";
    private static String LOGIN_URL = DAD_SERVER_URL_BASE + "login";
    private static String UPDATE_USER_URL = DAD_SERVER_URL_BASE + "updateUser";
    private static String GET_DATE_REQUESTS_STATUS_URL = DAD_SERVER_URL_BASE + "getDateRequestsStatus";
    /** Number of dogs to request each time a request is made. */
    private static int NUM_DOGS_REQUESTED = 20;
    private static String DEFAULT_ZIP = "98105";
    /** Parameter names for backend. */
    private static String REASON_PARAMETER = "reason";
    private static String ID_PARAMETER = "id";
    private static String TIME_PARAMETER = "epoch";
    private static String LIKED_PARAMETER = "liked";
    private static String COUNT_PARAMETER = "count";
    private static String ZIP_PARAMETER = "zip";
    /** JSON object names for backend. */
    private static String DOG_OBJECT = "dog";
    private static String FEEDBACK_OBJECT = "feedback";
    private static String REQUEST_OBJECT = "request";
    private static String STATUS_OBJECT = "status";
    private static String ID_OBJECT = "id";
    private static String TIME_OBJECT = "epoch";

    private Context context;

    /**
     * Constructs an instance of the DADServer class with the given context.
     *
     * @param context the application context in which this class will be used
     */
    public DADServer(Context context) {
        this.context = context;
    }

    /**
     * Returns an instance of the {@code DADServer} class.
     *
     * @param context the application context in which this class will be used
     * @return an instance of the {@code DADServer} class
     */
    public static DADServer getInstance(Context context) {
        if (instance == null) {
            instance = new DADServer(context);
        }
        return instance;
    }

    /**
     * Makes a POST request (with authentication) to the given URL with the given {@code jsonBody}
     * as the body of the POST request. Returns the JSON response via the given
     * {@code responseListener}.
     *
     * @param url the URL to make a POST request to
     * @param jsonBody the JSON body of the POST request
     * @param responseListener a response listener that will receive a callback with the response
     *                         data, or null to ignore response data
     */
    public void makeRequest(final String url, final JSONObject jsonBody, Response.Listener<String> responseListener) {
        if (responseListener == null) {
            // The caller would like not to receive response data. This can be done by setting an
            // empty response listener.
            responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {}
            };
        }

        RequestQueue queue = VolleySingleton.getInstance(context).getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, url, responseListener,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.e(TAG, "Request to DAD server failed.\nURL: " + url + "\nBody: " + jsonBody.toString());
                        Toast.makeText(context, R.string.server_connection_error_message, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("access_token", AccessToken.getCurrentAccessToken().getToken());
                headers.put("Content-Type", "application/json");
                return headers;
            }
            @Override
            public byte[] getBody() {
                return jsonBody.toString().getBytes();
            }
        };
        queue.add(request);
    }

    /**
     * Clients implement this interface to receive dogs from DADServer requests. An object
     * that implements this interface is passed by the client to methods that return dogs.
     * The client uses the {@code onGotDogs} method to receive the dogs from the DADServer request.
     */
    public interface DogsDataListener {
        /**
         * Called when the requested dogs have been retrieved.
         *
         * @param dogs the requested dogs
         */
        public void onGotDogs(List<Dog> dogs);
    }

    /**
     * Clients implement this interface to receive user profiles from DADServer requests. An object
     * that implements this interface is passed by the client to methods that return a user profile.
     * The client uses the {@code onGotUserProfile} method to receive the user profile from the
     * DADServer request.
     */
    public interface UserProfileDataListener {
        /**
         * Called when the requested user profile has been retrieved.
         *
         * @param userProfile the requested user profile
         */
        public void onGotUserProfile(UserProfile userProfile);
    }

    /**
     * Clients implement this interface to receive date requests from DADServer requests. An object
     * that implements this interface is passed by the client to methods that return date requests.
     * The clients uses the {@code onGotDateRequests} method to receive the date requests from
     * the DADServer request.
     */
    public interface DateRequestsDataListener {
        /**
         * Called when the date requests have been retrieved.
         *
         * @param dateRequests the date requests
         */
        public void onGotDateRequests(Set<DateRequest> dateRequests);
    }

    /**
     * Makes a DAD server request at the given URL, parses the response as a JSON
     * array of dogs and returns a set containing these dogs via the given callback listener.
     *
     * @param url a DAD endpoint that returns a JSON array of dogs
     * @param dataListener a data listener that will receive a callback with the dogs
     * @param numDogs the number of dogs to request
     * @param zip the zip of the location in which to search for dogs
     */
    private void getDogsAtUrl(String url, final DogsDataListener dataListener, int numDogs, String zip) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put(COUNT_PARAMETER, numDogs);
            parameters.put(ZIP_PARAMETER, zip);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        makeRequest(url, parameters, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    List<Dog> result = new ArrayList<>();
                    JSONArray dogsArray = (JSONArray) new JSONTokener(response).nextValue();
                    for (int i = 0; i < dogsArray.length(); i++) {
                        result.add(new Dog(dogsArray.getJSONObject(i)));
                    }
                    dataListener.onGotDogs(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Makes a DAD server request at the given URL, parses the response as a JSON
     * array of dogs and returns a set containing these dogs via the given callback listener.
     *
     * @param url a DAD endpoint that returns a JSON array of dogs
     * @param dataListener a data listener that will receive a callback with the dogs
     */
    private void getDogsAtUrl(String url, final DogsDataListener dataListener) {
        getDogsAtUrl(url, dataListener, NUM_DOGS_REQUESTED, DEFAULT_ZIP);
    }

    /**
     * Retrieves the user's profile from the DAD server.
     *
     * @param dataListener a data listener that will receive a callback with the user profile
     */
    public void getUser(final UserProfileDataListener dataListener) {
        JSONObject parameters = new JSONObject();
        makeRequest(LOGIN_URL, parameters, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonForm = (JSONObject) new JSONTokener(response).nextValue();
                    dataListener.onGotUserProfile(new UserProfile(jsonForm));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Updates the user's profile information using the given {@code UserProfile}.
     *
     * @param userProfile contains the user profile information to send to the DAD server
     */
    public void updateUser(UserProfile userProfile) {
        makeRequest(UPDATE_USER_URL, userProfile.asJSONObject(), null);
    }


    /**
     * Retrieves a set of dogs that the user has not yet judged.
     *
     * @param dataListener a data listener that will receive a callback with the dogs
     */
    public void getNextDogs(DogsDataListener dataListener) {
        getDogsAtUrl(GET_NEXT_DOGS_URL, dataListener);
    }

    /**
     * Retrieves a set of date requests (approved, rejected and pending) for the user.
     *
     * @param dataListener a data listener that will receive a callback with the date requests.
     */
    public void getDateRequests(final DateRequestsDataListener dataListener) {
        JSONObject parameters = new JSONObject();
        // Request status codes from backend.
        final String APPROVED_STATUS_CODE = "A";
        final String REJECTED_STATUS_CODE = "D";
        final String PENDING_STATUS_CODE = "P";
        makeRequest(GET_DATE_REQUESTS_STATUS_URL, parameters, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Set<DateRequest> dateRequests = new HashSet<>();
                    JSONArray jsonArray = (JSONArray) new JSONTokener(response).nextValue();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        long dogId = jsonObject.getJSONObject(DOG_OBJECT).getLong(ID_OBJECT);
                        long requestId = jsonObject.getJSONObject(REQUEST_OBJECT).getLong(ID_OBJECT);
                        String feedback = jsonObject.getJSONObject(REQUEST_OBJECT).getString(FEEDBACK_OBJECT);
                        DateRequest.Status status;
                        switch (jsonObject.getJSONObject(REQUEST_OBJECT).getString(STATUS_OBJECT)) {
                            case APPROVED_STATUS_CODE:
                                status = DateRequest.Status.APPROVED;
                                break;
                            case REJECTED_STATUS_CODE:
                                status = DateRequest.Status.REJECTED;
                                break;
                            case PENDING_STATUS_CODE:
                                status = DateRequest.Status.PENDING;
                                break;
                            default:
                                status = null;
                        }
                        Date date = new Date(jsonObject.getJSONObject(REQUEST_OBJECT).getLong(TIME_OBJECT));

                        dateRequests.add(new DateRequest(requestId, date, dogId, status, feedback));
                    }
                    dataListener.onGotDateRequests(dateRequests);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Retrieves a set of dogs that the user has liked.
     *
     * @param dataListener a data listener that will receive a callback with the dogs
     */
    public void getLikedDogs(DogsDataListener dataListener) {
        getDogsAtUrl(GET_LIKED_DOGS_URL, dataListener);
    }

    /**
     * Sends the DAD server a request to schedule a date with the dog that has the
     * given {@code dogId} at the time given by {@code epoch}.
     *
     * @param dogId the id of the dog to schedule the date with
     * @param time the time at which to schedule the date, expressed as milliseconds after epoch
     * @param reason a string describing the reason for the date
     */
    public void requestDate(long dogId, long time, String reason) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put(ID_PARAMETER, dogId);
            parameters.put(TIME_PARAMETER, time);
            parameters.put(REASON_PARAMETER, reason);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        makeRequest(REQUEST_DATE_URL, parameters, null);
    }

    /**
     * Marks the dog with the given {@code dogId} as liked or disliked.
     *
     * @param dogId the id of the dog to judge
     * @param like true if the dog should be marked as liked, false otherwise
     */
    protected void judgeDog(long dogId, boolean like) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put(ID_PARAMETER, dogId);
            parameters.put(LIKED_PARAMETER, like);
            parameters.put(TIME_PARAMETER, System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        makeRequest(JUDGE_DOG_URL, parameters, null);
    }

    /**
     * Marks the dog with the given {@code dogId} as liked.
     *
     * @param dogId the id of the dog to like
     */
    public void likeDog(long dogId) {
        judgeDog(dogId, true);
    }

    /**
     * Marks the dog with the given {@code dogId} as disliked.
     *
     * @param dogId the id of the dog to dislike
     */
    public void dislikeDog(long dogId) {
        judgeDog(dogId, false);
    }

}