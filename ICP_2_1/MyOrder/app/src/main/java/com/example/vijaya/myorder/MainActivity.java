package com.example.vijaya.myorder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {
    private static final String MAIN_ACTIVITY_TAG = "MainActivity";
    final double PIZZA_PRICE = 8;
    final double BASIC_TOPPING_PRICE = 0.5;
    final double HAWAIIAN_PRICE = 1.5;
    final double PERSONAL_PRICE = 0;
    final double MEDIUM_PRICE = 3;
    final double LARGE_PRICE = 4.5;
    final double XTRA_PRICE = 5.75;
    int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    /**
     * This method is called when the order button is clicked.
     */
    public void submitOrder(View view) {

        String order = gatherOrder(view);

        Uri uri = Uri.parse("mailto:" + "dpwh24@umkc.edu")
                .buildUpon()
                .appendQueryParameter("to", "dpwh24@umkc.edu")
                .appendQueryParameter("subject", "Your pizza order")
                .appendQueryParameter("body", order)
                .build();

        sendEmail(uri, "Select Email");
    }


    /**
     *
     * Helper function to gather order details into a string
     */

    public String gatherOrder(View view) {

        // get user input
        EditText userInputNameView = (EditText) findViewById(R.id.user_input);
        String userInputName = userInputNameView.getText().toString();

        // check if whipped cream is selected
        CheckBox extraCheese = (CheckBox) findViewById(R.id.extra_cheese_checked);
        boolean hasExtraCheese = extraCheese.isChecked();

        // check if pepperoni is selected
        CheckBox pepperoni = (CheckBox) findViewById(R.id.pepperoni_checked);
        boolean hasPepperoni = pepperoni.isChecked();

        // check if pepperoni is selected
        CheckBox hawaiian = (CheckBox) findViewById(R.id.hawaiian_checked);
        boolean hasHawaiin = hawaiian.isChecked();

        Spinner crust = (Spinner) findViewById(R.id.crust_spinner);
        String crustType = crust.getSelectedItem().toString();

        Spinner size = (Spinner) findViewById(R.id.size_spinner);
        String sizeVal = size.getSelectedItem().toString();

        // calculate and store the total price
        double totalPrice = calculatePrice(hasExtraCheese, hasPepperoni, hasHawaiin, sizeVal);

        // create and store the order summary
        String orderSummaryMessage = createOrderSummary(userInputName, hasExtraCheese, hasPepperoni,
                hasHawaiin, crustType, sizeVal, totalPrice);

        // Write the relevant code for making the buttons work(i.e implement the implicit and explicit intents
        return orderSummaryMessage;
    }

    /**
     *
     * On click destination for the summary button
     */

    public void orderSummaryPage(View v){

        String order = gatherOrder(v);

        Intent redirect = new Intent(this, SummaryActivity.class);
        redirect.putExtra(EXTRA_MESSAGE,order);
        startActivity(redirect);
    }

    /**
     *
     * Helper function to start the email application
     */
    public void sendEmail(Uri uri, String chooserTitle) {

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
        startActivity(Intent.createChooser(emailIntent, chooserTitle));
    }

    private String boolToString(boolean bool) {
        return bool ? (getString(R.string.yes)) : (getString(R.string.no));
    }

    private String createOrderSummary(String userInputName, boolean hasExtraCheese,
                                      boolean hasPepperoni, boolean hasHawaiian, String crust,
                                      String size, double price) {
        String orderSummaryMessage = getString(R.string.order_summary_name, userInputName) + "\n" +
                getString(R.string.order_summary_extra_cheese, boolToString(hasExtraCheese)) + "\n" +
                getString(R.string.order_summary_pepperoni, boolToString(hasPepperoni)) + "\n" +
                getString(R.string.order_summary_hawaiian, boolToString(hasHawaiian)) + "\n" +
                getString(R.string.order_summary_size, size) + "\n" +
                getString(R.string.order_summary_crust, crust) + "\n" +
                getString(R.string.order_summary_quantity, quantity) + "\n" +
                getString(R.string.order_summary_total_price, Math.round(price*100.0)/100.0 )+ "\n" +
                getString(R.string.thank_you);
        return orderSummaryMessage;
    }

    /**
     * Method to calculate the total price
     *
     * @return total Price
     */
    private double calculatePrice(boolean hasExtraCheese, boolean hasPepperoni, boolean hasHawaiian, String size) {
        double basePrice = PIZZA_PRICE;
        if (hasExtraCheese) {
            basePrice += BASIC_TOPPING_PRICE;
        }
        if (hasPepperoni) {
            basePrice += BASIC_TOPPING_PRICE;
        }
        if (hasHawaiian) {
            basePrice += HAWAIIAN_PRICE;
        }

        if (size.equals(getString(R.string.personal))){
            basePrice += PERSONAL_PRICE;
        }else if (size.equals(getString(R.string.medium))){
            basePrice += MEDIUM_PRICE;
        }else if (size.equals(getString(R.string.large))){
            basePrice += LARGE_PRICE;
        }else if (size.equals(getString(R.string.xtra))){
            basePrice += XTRA_PRICE;
        }
        return quantity * basePrice;
    }

    /**
     * This method displays the given quantity value on the screen.
     */
    private void display(int number) {
        TextView quantityTextView = (TextView) findViewById(R.id.quantity_text_view);
        quantityTextView.setText("" + number);
    }

    /**
     * This method increments the quantity of coffee cups by one
     *
     * @param view on passes the view that we are working with to the method
     */

    public void increment(View view) {
        if (quantity < 100) {
            quantity = quantity + 1;
            display(quantity);
        } else {
            Log.i("MainActivity", "Please select less than one hundred cups of coffee");
            Context context = getApplicationContext();
            String lowerLimitToast = getString(R.string.too_much_pizza);
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, lowerLimitToast, duration);
            toast.show();
            return;
        }
    }

    /**
     * This method decrements the quantity of coffee cups by one
     *
     * @param view passes on the view that we are working with to the method
     */
    public void decrement(View view) {
        if (quantity > 1) {
            quantity = quantity - 1;
            display(quantity);
        } else {
            Log.i("MainActivity", "Please select atleast one cup of coffee");
            Context context = getApplicationContext();
            String upperLimitToast = getString(R.string.too_little_pizza);
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, upperLimitToast, duration);
            toast.show();
            return;
        }
    }
}