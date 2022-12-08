package com.collegemeet.android.revafest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.HashMap;

public class PayActivity extends AppCompatActivity implements PaymentResultListener {

    TextView head;
    Button pay;
    FirebaseFirestore firestore;
    String event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        Checkout.preload(this);

        head = findViewById(R.id.pay_price_head);
        pay = findViewById(R.id.pay_button);
        firestore = FirebaseFirestore.getInstance();
        if (getIntent() != null) {

            event = getIntent().getStringExtra("event_name");
            head.setText(getIntent().getStringExtra("event_name"));

            pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    paymentNow("1");
                }
            });


        }
    }

    private void paymentNow(String s) {

        final Activity activity = this;
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_0M4fmPyANhh9M0");

        double finalAmt = Float.parseFloat(s) * 100;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            options.put("name", "Horizon");
            options.put("description", "Reference No. #123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg");
//            options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", finalAmt + "");//pass amount in currency subunits
            options.put("prefill.email", "daksh211000@gmail.com");
            options.put("prefill.contact", "9412729195");
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(activity, options);

        } catch (Exception e) {
            Log.e("naaah", "Error in starting Razorpay Checkout", e);
        }
    }


    @Override
    public void onPaymentSuccess(String s) {

        HashMap<Object, String> map = new HashMap<>();
        map.put("requested_user", FirebaseAuth.getInstance().getCurrentUser().getUid());
        map.put("eventName", event);

        firestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("paids").document(event).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Toast.makeText(PayActivity.this, "Payment success", Toast.LENGTH_SHORT).show();
                finish();

            }
        });

    }

    @Override
    public void onPaymentError(int i, String s) {

        Toast.makeText(PayActivity.this, "Payment error", Toast.LENGTH_SHORT).show();
    }
}