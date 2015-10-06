package com.elasticbeanstalk.ebaysearch;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;


public class ResultActivity extends ActionBarActivity {
    private static String jsonStr;
    private static final String LOG_TAG = "ResultActivity";
    public static final String EXTRA_MESSAGE = "ebaysearch.ToDetailsActivity";
    public static JSONObject obj;
    public static String priceStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.d(LOG_TAG, "onCreate");
        setContentView(R.layout.activity_result);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        Intent intent = getIntent();
        jsonStr = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        ((TextView) findViewById(R.id.result_keyword)).setText(
                "Result(s) for '" + MainActivity.keywordStr + "'");
        try {
            obj = new JSONObject(jsonStr);
//            Log.d(LOG_TAG, "item number: " + (obj.length()-4));
            int num = obj.length()-4;
            for(int i = 0; i < num; i++){
                String itemKey = "item" + i;
                //Set results list images
                ImageView curImage = (ImageView)findViewById(getResources().getIdentifier(itemKey + "_Image", "id", getPackageName()));
                if(!obj.getJSONObject(itemKey).getJSONObject("basicInfo").getString("galleryURL").isEmpty()){
                    new DownloadImageTask(curImage).execute(obj.getJSONObject(itemKey).getJSONObject("basicInfo").getString("galleryURL"));
                }
                else if (!obj.getJSONObject(itemKey).getJSONObject("basicInfo").getString("pictureURLSuperSize").isEmpty()){
                    new DownloadImageTask(curImage).execute(obj.getJSONObject(itemKey).getJSONObject("basicInfo").getString("pictureURLSuperSize"));
                }
                else{
                    TextView curImageNA = (TextView) findViewById(getResources().getIdentifier(itemKey + "_Image_NA", "id", getPackageName()));
                    curImageNA.setText("N/A");
                }

                //set results list titles
                TextView curTitle = (TextView)findViewById(getResources().getIdentifier(itemKey+"_Title", "id", getPackageName()));
                if(!obj.getJSONObject(itemKey).getJSONObject("basicInfo").getString("title").isEmpty()){
                    curTitle.setText(obj.getJSONObject(itemKey).getJSONObject("basicInfo").getString("title"));
                }
                else curTitle.setText("N/A");

                //Set results list prices
                TextView curPrice = (TextView)findViewById(getResources().getIdentifier(itemKey+"_Price", "id", getPackageName()));
                if(!obj.getJSONObject(itemKey).getJSONObject("basicInfo").getString("convertedCurrentPrice").isEmpty()){
                    String price = obj.getJSONObject(itemKey).getJSONObject("basicInfo").getString("convertedCurrentPrice");
                    String shipping = obj.getJSONObject(itemKey).getJSONObject("basicInfo").getString("shippingServiceCost");
                    if(shipping.isEmpty()){
                        priceStr = "Price: $" + price + " (Shipping N/A)";
                    }
                    else if(shipping.equals("0.0")){
                        priceStr = "Price: $" + price + " (FREE Shipping)";
                    }
                    else{
                        priceStr = "Price: $" + price + " (+ $" + shipping + " Shipping)";
                    }
                    curPrice.setText(priceStr);
                }
                else curPrice.setText("N/A");
            }
            for (int i = num; i < 5; i++){
                String item = "item" + i + "_View";
                String hr = "result_hr" + i;
                ((LinearLayout) findViewById(getResources().getIdentifier(item, "id", getPackageName()))).setVisibility(View.GONE);
                ((View) findViewById(getResources().getIdentifier(hr, "id", getPackageName()))).setVisibility(View.GONE);
            }


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.d(LOG_TAG, "onResume");
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
        if(id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void imageOnClick(View v) throws JSONException {
        String item = v.getTag().toString();
        String itemURL = obj.getJSONObject(item).getJSONObject("basicInfo").getString("viewItemURL");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(itemURL));
        startActivity(intent);
    }

    public void titleOnClick(View view) {
        Intent intent = new Intent(view.getContext(), DetailsActivity.class);
        String viewTag = view.getTag().toString();
        //GET TAG may get nothing, cause error here if viewTag definition not separated
        intent.putExtra(EXTRA_MESSAGE, viewTag);
        startActivity(intent);
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
    }
}

