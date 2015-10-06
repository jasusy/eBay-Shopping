package com.elasticbeanstalk.ebaysearch;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.MessageDialog;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;


public class DetailsActivity extends ActionBarActivity {
    private JSONObject curItem;
    private final static String LOG_TAG = "DetailsActivity";
    private CallbackManager callbackManager;
    ShareLinkContent content;
    ShareDialog shareDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Intent intent = getIntent();
        String item = intent.getStringExtra(ResultActivity.EXTRA_MESSAGE);

        //get json of target item
        try {
            curItem = ResultActivity.obj.getJSONObject(item);
            //Writing details from json
            buildDetails();

            //set up button listeners
//        setupButton();

            //facebook share
            callbackManager = CallbackManager.Factory.create();
            ((ImageButton) findViewById(R.id.bt_details_facebook)).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
//                    Log.d("facebook share", "share button clicked");
                    try {
                        String itemTitle = curItem.getJSONObject("basicInfo").getString("title");
                        String itemLink = null;
                        itemLink = curItem.getJSONObject("basicInfo").getString("viewItemURL");

                        String itemDes = ResultActivity.priceStr + ", " + curItem.getJSONObject("basicInfo").getString("location");
                        String imageLink = curItem.getJSONObject("basicInfo").getString("galleryURL");
                        content = new ShareLinkContent.Builder()
                                .setContentTitle(itemTitle)
                                .setContentUrl(Uri.parse(itemLink))
                                .setContentDescription(itemDes)
                                .setImageUrl(Uri.parse(imageLink))
                                .build();
                        shareDialog = new ShareDialog(DetailsActivity.this);
                        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {

                            @Override
                            public void onSuccess(Sharer.Result result) {
                                Log.d("facebook", "success ");
                                Toast.makeText(getApplicationContext(), "Posted Successfully", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(FacebookException error) {
                                Log.d("facebook", "error");
                                Toast.makeText(getApplicationContext(), "Not Posted: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancel() {
                                Log.d("facebook", "cancel");
                                Toast.makeText(getApplicationContext(), "Post Cancelled", Toast.LENGTH_SHORT).show();                            }

                        });
                        shareDialog.show(content);

                    } catch (JSONException e) {
                        Log.e("facebook share", e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
            //other buttons
            final Button bt_buy = (Button) findViewById(R.id.bt_details_BuyNow);
            bt_buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final String url = curItem.getJSONObject("basicInfo").getString("viewItemURL");
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, e.getMessage());
                        e.printStackTrace();
                    }
                }
            });

            //Details tabs processing
            final Button bt_basic = (Button) findViewById(R.id.bt_details_BasicInfo);
            final Button bt_seller = (Button) findViewById(R.id.bt_details_Seller);
            final Button bt_shipping = (Button) findViewById(R.id.bt_details_Shipping);
            final LinearLayout pad_basic = (LinearLayout) findViewById(R.id.basic_pad);
            final LinearLayout pad_seller = (LinearLayout) findViewById(R.id.seller_pad);
            final LinearLayout pad_shipping = (LinearLayout) findViewById(R.id.shipping_pad);

            //Details button listener
            bt_basic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bt_basic.setBackground(getResources().getDrawable(R.drawable.mybutton1));
                    bt_seller.setBackgroundResource(R.drawable.mybutton2);
                    bt_shipping.setBackgroundResource(R.drawable.mybutton2);
                    pad_basic.setVisibility(View.VISIBLE);
                    pad_seller.setVisibility(View.GONE);
                    pad_shipping.setVisibility(View.GONE);
                }
            });
            bt_seller.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bt_basic.setBackground(getResources().getDrawable(R.drawable.mybutton2));
                    bt_seller.setBackgroundResource(R.drawable.mybutton1);
                    bt_shipping.setBackgroundResource(R.drawable.mybutton2);
                    pad_basic.setVisibility(View.GONE);
                    pad_seller.setVisibility(View.VISIBLE);
                    pad_shipping.setVisibility(View.GONE);
                }
            });
            bt_shipping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bt_basic.setBackground(getResources().getDrawable(R.drawable.mybutton2));
                    bt_seller.setBackgroundResource(R.drawable.mybutton2);
                    bt_shipping.setBackgroundResource(R.drawable.mybutton1);
                    pad_basic.setVisibility(View.GONE);
                    pad_seller.setVisibility(View.GONE);
                    pad_shipping.setVisibility(View.VISIBLE);
                }
            });

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }


    }


//    private void setupButton() {
//        //Buy Now button listener
//        final Button bt_buy = (Button) findViewById(R.id.bt_details_BuyNow);
//        bt_buy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    final String url = curItem.getJSONObject("basicInfo").getString("viewItemURL");
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    startActivity(intent);
//                } catch (JSONException e) {
//                    Log.e(LOG_TAG, e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        //Details tabs processing
//        final Button bt_basic = (Button) findViewById(R.id.bt_details_BasicInfo);
//        final Button bt_seller = (Button) findViewById(R.id.bt_details_Seller);
//        final Button bt_shipping = (Button) findViewById(R.id.bt_details_Shipping);
//        final LinearLayout pad_basic = (LinearLayout) findViewById(R.id.basic_pad);
//        final LinearLayout pad_seller = (LinearLayout) findViewById(R.id.seller_pad);
//        final LinearLayout pad_shipping = (LinearLayout) findViewById(R.id.shipping_pad);
//
//        //Details button listener
//        bt_basic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bt_basic.setBackground(getResources().getDrawable(R.drawable.mybutton1));
//                bt_seller.setBackgroundResource(R.drawable.mybutton2);
//                bt_shipping.setBackgroundResource(R.drawable.mybutton2);
//                pad_basic.setVisibility(View.VISIBLE);
//                pad_seller.setVisibility(View.GONE);
//                pad_shipping.setVisibility(View.GONE);
//            }
//        });
//        bt_seller.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bt_basic.setBackground(getResources().getDrawable(R.drawable.mybutton2));
//                bt_seller.setBackgroundResource(R.drawable.mybutton1);
//                bt_shipping.setBackgroundResource(R.drawable.mybutton2);
//                pad_basic.setVisibility(View.GONE);
//                pad_seller.setVisibility(View.VISIBLE);
//                pad_shipping.setVisibility(View.GONE);
//            }
//        });
//        bt_shipping.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bt_basic.setBackground(getResources().getDrawable(R.drawable.mybutton2));
//                bt_seller.setBackgroundResource(R.drawable.mybutton2);
//                bt_shipping.setBackgroundResource(R.drawable.mybutton1);
//                pad_basic.setVisibility(View.GONE);
//                pad_seller.setVisibility(View.GONE);
//                pad_shipping.setVisibility(View.VISIBLE);
//            }
//        });
//
//    }

    /**
     * Draw details activity layout, update content with json search result
     */
    private void buildDetails() {
        //Start filling picture and info. details in details layout page
        final ImageView superview = (ImageView) findViewById(R.id.superPicture);
        try {
            if(!curItem.getJSONObject("basicInfo").getString("pictureURLSuperSize").isEmpty()){
                new DownloadImageTask(superview).execute(curItem.getJSONObject("basicInfo").getString("pictureURLSuperSize"));
            }
            else if (!curItem.getJSONObject("basicInfo").getString("galleryURL").isEmpty()){
                new DownloadImageTask(superview).execute(curItem.getJSONObject("basicInfo").getString("galleryURL"));
            }
            else{
                superview.setImageResource(R.drawable.na);
            }

            //title
            String temp = curItem.getJSONObject("basicInfo").getString("title");
            if("".equals(temp)) temp = "N/A";
            ((TextView) findViewById(R.id.title_TextView)).setText(temp);
            //price
            ((TextView) findViewById(R.id.price_TextView)).setText(ResultActivity.priceStr);
            //location
            temp = curItem.getJSONObject("basicInfo").getString("location");
            if("".equals(temp)) temp = "N/A";
            ((TextView) findViewById(R.id.location_TextView)).setText(temp);
            //top rated icon
            temp = curItem.getJSONObject("basicInfo").getString("topRatedListing");
            if("true".equals(temp)) {
                Log.d(LOG_TAG, "toprated: " + temp);
                ((ImageView) findViewById(R.id.details_TopRated)).setVisibility(View.VISIBLE);
            }
            else ((ImageView) findViewById(R.id.details_TopRated)).setVisibility(View.INVISIBLE);


            //Start 3 tabs info. filling
            //basic info. tab
            temp = curItem.getJSONObject("basicInfo").getString("categoryName");
            if("".equals(temp)) temp = "N/A";
            ((TextView) findViewById(R.id.basic_pad_cat)).setText(temp);
            temp = curItem.getJSONObject("basicInfo").getString("conditionDisplayName");
            if("".equals(temp)) temp = "N/A";
            ((TextView) findViewById(R.id.basic_pad_cond)).setText(temp);
            temp = curItem.getJSONObject("basicInfo").getString("listingType");
            if("".equals(temp)) temp = "N/A";
            else if("FixedPrice".equals(temp)) temp = "Buy It Now";
            else if("Auction".equals(temp)) temp = "Auction";
            else if("Classified Ad".equals(temp)) temp = "Classified";
            ((TextView) findViewById(R.id.basic_pad_buyf)).setText(temp);
            //seller info. tab
            temp = curItem.getJSONObject("sellerInfo").getString("sellerUserName");
            if("".equals(temp)) temp = "N/A";
            ((TextView) findViewById(R.id.seller_pad_user)).setText(temp);

            temp = curItem.getJSONObject("sellerInfo").getString("feedbackScore");
            if("".equals(temp)) temp = "N/A";
            ((TextView) findViewById(R.id.seller_pad_score)).setText(temp);

            temp = curItem.getJSONObject("sellerInfo").getString("positiveFeedbackPercent");
            if("".equals(temp)) temp = "N/A";
            ((TextView) findViewById(R.id.seller_pad_pos)).setText(temp+"%");

            temp = curItem.getJSONObject("sellerInfo").getString("feedbackRatingStar");
            if("".equals(temp)) temp = "N/A";
            ((TextView) findViewById(R.id.seller_pad_rating)).setText(temp);

            temp = curItem.getJSONObject("sellerInfo").getString("topRatedSeller");
            if("true".equals(temp))
                ((ImageView) findViewById(R.id.seller_pad_topRated)).setImageResource(R.drawable.ok);
            else if("false".equals(temp))
                ((ImageView) findViewById(R.id.seller_pad_topRated)).setImageResource(R.drawable.remove);
            else ((ImageView) findViewById(R.id.seller_pad_topRated)).setImageResource(R.drawable.na);

            temp = curItem.getJSONObject("sellerInfo").getString("sellerStoreName");
            if("".equals(temp)) temp = "N/A";
            ((TextView) findViewById(R.id.seller_pad_store)).setText(temp);

            //Shipping tab
            temp = curItem.getJSONObject("shippingInfo").getString("shippingType"); //add comma
            if("".equals(temp)) temp = "N/A";
            else temp = temp.replaceAll("([a-z])([A-Z])", "$1 $2");
            ((TextView) findViewById(R.id.shipping_pad_type)).setText(temp);
            temp = curItem.getJSONObject("shippingInfo").getString("handlingTime");
            if("".equals(temp)) temp = "N/A";
            ((TextView) findViewById(R.id.shipping_pad_handle)).setText(temp);
            temp = curItem.getJSONObject("shippingInfo").getString("shipToLocations");
            if("".equals(temp)) temp = "N/A";
            ((TextView) findViewById(R.id.shipping_pad_location)).setText(temp);

            temp = curItem.getJSONObject("shippingInfo").getString("expeditedShipping");
            if("true".equals(temp))
                ((ImageView) findViewById(R.id.shipping_pad_exped)).setImageResource(R.drawable.ok);
            else if("false".equals(temp))
                ((ImageView) findViewById(R.id.shipping_pad_exped)).setImageResource(R.drawable.remove);
            else ((ImageView) findViewById(R.id.shipping_pad_exped)).setImageResource(R.drawable.na);

            temp = curItem.getJSONObject("shippingInfo").getString("oneDayShippingAvailable");
            if("true".equals(temp))
                ((ImageView) findViewById(R.id.shipping_pad_oneday)).setImageResource(R.drawable.ok);
            else if("false".equals(temp))
                ((ImageView) findViewById(R.id.shipping_pad_oneday)).setImageResource(R.drawable.remove);
            else ((ImageView) findViewById(R.id.shipping_pad_oneday)).setImageResource(R.drawable.na);

            temp = curItem.getJSONObject("shippingInfo").getString("returnsAccepted");
            if("true".equals(temp))
                ((ImageView) findViewById(R.id.shipping_pad_return)).setImageResource(R.drawable.ok);
            else if("false".equals(temp))
                ((ImageView) findViewById(R.id.shipping_pad_return)).setImageResource(R.drawable.remove);
            else ((ImageView) findViewById(R.id.shipping_pad_return)).setImageResource(R.drawable.na);

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
//        MessageDialog.show(DetailsActivity.this, content); //facebook message dialog.
//        Log.d("facebook", data + ", " + requestCode + ", " + resultCode);
        //facebook response could write here as well, reqestcode = -1 for sucess, 0 for cancelled or error

    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.d(LOG_TAG, "onResume");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
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
        if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
