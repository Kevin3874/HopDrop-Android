package com.example.HopDrop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OrderDetailsActivity extends AppCompatActivity {

    private Order mOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        // Get the Order object passed from the previous activity
        mOrder = (Order) getIntent().getSerializableExtra("order");

        // Update the UI with the Order details
        TextView customerNameTextView = findViewById(R.id.customer_name);
        customerNameTextView.setText(mOrder.getCustomer_name());

        TextView srcTextView = findViewById(R.id.src);
        srcTextView.setText(mOrder.getSrc());

        TextView destTextView = findViewById(R.id.dest);
        destTextView.setText(mOrder.getDest());

        TextView feeTextView = findViewById(R.id.fee);
        feeTextView.setText(String.valueOf(mOrder.getFee()));

        TextView notesTextView = findViewById(R.id.notes);
        notesTextView.setText(String.valueOf(mOrder.getNotes()));

        Button acceptButton = findViewById(R.id.accept_button);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Define what should happen when the button is clicked
                Intent intent = new Intent(OrderDetailsActivity.this, ConfirmOrderActivity.class);
                startActivity(intent);
                intent.putExtra("order", mOrder);
                startActivity(intent);
            }
        });
    }
}
