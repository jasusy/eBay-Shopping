package com.elasticbeanstalk.ebaysearch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;


public class MainActivity extends ActionBarActivity {
    public static final String EXTRA_MESSAGE = "ebaysearch.ToResultActivity";
    private static final String URL = "http://steveebaysearch.elasticbeanstalk.com/urlresult.php";
    private final static String LOG_TAG = "MainActivity";
    public static String keywordStr;
//    ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.d(LOG_TAG, "onCreate");
        setContentView(R.layout.activity_main);
        Spinner spinner = (Spinner) findViewById(R.id.sort_by);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_by_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        FacebookSdk.sdkInitialize(getApplicationContext());

        //Clear button listener, clear forms
        final Button buttonClear = (Button) findViewById(R.id.bt_clear);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                oneToast("clear button clicked");
                ((EditText) findViewById(R.id.keywordEditText)).setText("");
                ((EditText) findViewById(R.id.priceFromEditText)).setText("");
                ((EditText) findViewById(R.id.priceToEditText)).setText("");
                ((TextView) findViewById(R.id.errorTextView)).setText("");
            }
        });

        //Search button listener
        final Button buttonSearch = (Button) findViewById(R.id.bt_search);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputErrorHandle()){
                    ((TextView) findViewById(R.id.errorTextView)).setText("");
                    try {
                        String jsonStr = phpRequest();
                        if(jsonStr.length() < 50){//error message for "No results found"
                            final TextView errorView = (TextView) findViewById(R.id.errorTextView);
                            errorView.setText("No Results Found");
                        }
                        else{//going to result display activity ResultActivity
                            Intent intent = new Intent(v.getContext(), ResultActivity.class);
                            intent.putExtra(EXTRA_MESSAGE, jsonStr);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        Log.e(LOG_TAG, e.getMessage());
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private boolean inputErrorHandle() {
        Log.d(LOG_TAG, "test point error handle");
        final TextView errorView = (TextView) findViewById(R.id.errorTextView);
        String priceFrom, priceTo;
        if (((EditText) findViewById(R.id.keywordEditText)).getText().toString().matches("^\\s*$")){
            errorView.setText(R.string.error_keyword_empty);
            return false;
        }
        else{ //NO / close regex in java
            priceFrom = ((EditText) findViewById(R.id.priceFromEditText)).getText().toString();
            if (!priceFrom.matches("^\\s*$") && !priceFrom.matches("^\\s*\\d+(\\.\\d+)?\\s*$")) {
                errorView.setText(R.string.error_price_from);
                return false;
            }
            else {
                priceTo = ((EditText) findViewById(R.id.priceToEditText)).getText().toString();
                if (!priceTo.matches("^\\s*$") && !priceTo.matches("^\\s*\\d+(\\.\\d+)?\\s*$")) {
                    errorView.setText(R.string.error_price_to);
                    return false;
                }
                else {
                    Log.d(LOG_TAG, "test point 2");
                    if(!priceFrom.matches("^\\s*$") && !priceTo.matches("^\\s*$") && (Float.parseFloat(priceFrom) >= Float.parseFloat(priceTo))) {
                        Log.d(LOG_TAG, "test point 3");
                        errorView.setText(R.string.error_price_range);
                        return false;
                    }
                }
            }
        }
        return true;
    }


    @Override
    protected void onResume(){
        super.onResume();
//        Log.d(LOG_TAG, "onResume");

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);

    }


    /**
     * Http request from php on AWS, get json search result
     * asnyc task called
     * @return search result json String
     * @throws ExecutionException, InterruptedException
     */
    protected String phpRequest() throws ExecutionException, InterruptedException {
        Log.d(LOG_TAG, "test point error handle");
        //get search inputs
        int sortWay = ((Spinner)findViewById(R.id.sort_by)).getSelectedItemPosition();
        keywordStr = ((EditText)findViewById(R.id.keywordEditText)).getText().toString();
        String price1Str = ((EditText)findViewById(R.id.priceFromEditText)).getText().toString();
        String price2Str = ((EditText)findViewById(R.id.priceToEditText)).getText().toString();

        //generate request url
        String[] sorts = {"BestMatch", "CurrentPriceHighest", "PricePlusShippingHighest", "PricePlusShippingLowest"};
        String keyStr = URLEncoder.encode(keywordStr);
        String params = "resnum=5&keyword=" + keyStr + "&sort=" + sorts[sortWay];
        if(!price1Str.matches("^\\s*$")) params += "&minprice=" + Float.parseFloat(price1Str);
        if(!price2Str.matches("^\\s*$")) params += "&maxprice=" + Float.parseFloat(price2Str);

        //send httprequest
        RequestPHPTask aReq = new RequestPHPTask();
        Log.d(LOG_TAG, URL + "?" + params);
        aReq.execute(URL + "?" + params);

        //return result from async process
        return aReq.get();
    }

//    protected String jsonGen() throws JSONException {
//        final Spinner sortWay = (Spinner) findViewById(R.id.sort_by);
//        String keyStr = ((EditText)findViewById(R.id.keywordEditText)).getText().toString();
//        String price1Str = ((EditText)findViewById(R.id.priceFromEditText)).getText().toString();
//        String price2Str = ((EditText)findViewById(R.id.priceToEditText)).getText().toString();
//        JSONObject jsonObj = new JSONObject();
//        jsonObj.put("keyword", keyStr);
//        if(!price1Str.matches("\\s*")) jsonObj.put("minprice", price1Str);
//        if(!price2Str.matches("\\s*")) jsonObj.put("maxprice", price2Str);
//        jsonObj.put("sort", sortWay.getSelectedItem().toString());
//        return jsonObj.toString();
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void oneToast(String msg){
        Context context = getApplicationContext();
        CharSequence text = msg;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Log.d(LOG_TAG, "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.d(LOG_TAG, "onPause");

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }


}
