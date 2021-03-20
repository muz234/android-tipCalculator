package com.example.abrahamlaragranados.tipcalculator2;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity implements TextWatcher, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener {

    private EditText amountInput;

    private TextView percentageView, tipAmountView, totalAmountView, pptAmountView;

    private SeekBar seekBar;

    private Spinner spinner;

    private int splitTotal;

    private double percent = .15;
    private double billAmount = 0, tip = 0, total = 0, ppt = 0;

    private boolean roundTip, roundTotal = false;

    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    private static final NumberFormat percentFormat = NumberFormat.getPercentInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        amountInput     = (EditText) findViewById(R.id.amountTextview);
        percentageView  = (TextView) findViewById(R.id.percentTextView);
        tipAmountView   = (TextView) findViewById(R.id.tipAmountView);
        totalAmountView = (TextView) findViewById(R.id.totalAmountView);
        seekBar         = (SeekBar) findViewById(R.id.percentSeekbar);
        spinner         = (Spinner) findViewById(R.id.spinnerSplit);
        pptAmountView   = (TextView) findViewById(R.id.ppTotalView);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        amountInput.addTextChangedListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        spinner.setOnItemSelectedListener(this);
    }

//    OptionsMenu Methods
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
        if (id == R.id.action_share) {

            sendSMS(String.format("Bill: %.2f\nTip: %.2f\nTotal: %.2f\nPer Person Amount: %.2f\n", billAmount, tip, total, ppt));
            return true;}


        return super.onOptionsItemSelected(item);
    }

//    My Methods
    private void sendSMS(String message) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, true);
        intent.setType("text/plain");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void calculate() {
        percentageView.setText(percentFormat.format(percent));

        if (amountInput.getText().toString().isEmpty()) {
            setTexts(0,0, 0);
            return;
        }

        billAmount = Double.parseDouble(amountInput.getText().toString());

        tip   =  billAmount * percent;
        if (roundTip) tip = Math.round(tip);

        total =  billAmount + tip;
        if (roundTotal) total = Math.round(total);

         ppt  = total / splitTotal;

        setTexts(tip, total, ppt);
    }

    private void setTexts(double tip, double total, double ppt) {
        tipAmountView.setText(currencyFormat.format(tip));
        totalAmountView.setText(currencyFormat.format(total));
        pptAmountView.setText(currencyFormat.format(ppt));
    }

    public void onRadioButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.tipButton:
                roundTip = true;
                roundTotal = false;
                break;
            case R.id.totalButton:
                roundTip = false;
                roundTotal = true;
                break;
            default:
                roundTotal = roundTip = false;
                break;
        }
        calculate();
    }

//    TextWatcher Methods
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        calculate();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

//    OnSeekBarChangeListener Methods
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        percent = (double) progress/100;
        calculate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

//    OnItemSelectedListener Methods
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        splitTotal = position + 1;
        ppt = total / splitTotal;
        pptAmountView.setText(currencyFormat.format(ppt));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
