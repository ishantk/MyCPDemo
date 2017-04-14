package com.auribises.mycpdemo;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    @InjectView(R.id.editTextName)
    EditText eTxtName;

    @InjectView(R.id.editTextPhone)
    EditText eTxtPhone;

    @InjectView(R.id.editTextEmail)
    EditText eTxtEmail;

    @InjectView(R.id.radioButtonMale)
    RadioButton rbMale;

    @InjectView(R.id.radioButtonFemale)
    RadioButton rbFemale;

    @InjectView(R.id.spinnerCity)
    Spinner spCity;
    ArrayAdapter<String> adapter;

    @InjectView(R.id.buttonSubmit)
    Button btnSubmit;

    Student student,rcvStudent;

    ContentResolver resolver;

    boolean updateMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        student = new Student();

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item);
        adapter.add("--Select City--");
        adapter.add("Ludhina");
        adapter.add("Chandigarh");
        adapter.add("Delhi");
        adapter.add("Bengaluru");
        adapter.add("California");

        spCity.setAdapter(adapter);

        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != 0){
                    student.setCity(adapter.getItem(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        rbMale.setOnCheckedChangeListener(this);
        rbFemale.setOnCheckedChangeListener(this);

        resolver = getContentResolver();

        Intent rcv = getIntent();
        updateMode = rcv.hasExtra("keyStudent");

        if(updateMode){
            rcvStudent = (Student)rcv.getSerializableExtra("keyStudent");
            eTxtName.setText(rcvStudent.getName());
            eTxtPhone.setText(rcvStudent.getPhone());
            eTxtEmail.setText(rcvStudent.getEmail());


            if(rcvStudent.getGender().equals("Male")){
                rbMale.setChecked(true);
            }else{
                rbFemale.setChecked(true);
            }

            int p = 0;
            for(int i=0;i<adapter.getCount();i++){
                if(adapter.getItem(i).equals(rcvStudent.getCity())){
                    p = i;
                    break;
                }
            }

            spCity.setSelection(p);

            btnSubmit.setText("Update");
        }
    }

    public void clickHandler(View view){
        if(view.getId() == R.id.buttonSubmit){
            //String name = eTxtName.getText().toString().trim();
            //student.setName(name);
            student.setName(eTxtName.getText().toString().trim());
            student.setPhone(eTxtPhone.getText().toString().trim());
            student.setEmail(eTxtEmail.getText().toString().trim());


            insertIntoDB();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int id = compoundButton.getId();

        if(b) {
            if (id == R.id.radioButtonMale) {
                student.setGender("Male");
            } else {
                student.setGender("Female");
            }
        }
    }

    void insertIntoDB(){

        ContentValues values = new ContentValues();

        values.put(Util.COL_NAME,student.getName());
        values.put(Util.COL_PHONE,student.getPhone());
        values.put(Util.COL_EMAIL,student.getEmail());
        values.put(Util.COL_GENDER,student.getGender());
        values.put(Util.COL_CITY,student.getCity());

        if(!updateMode){
            Uri dummy = resolver.insert(Util.STUDENT_URI,values);
            Toast.makeText(this,student.getName()+ " Registered Successfully "+dummy.getLastPathSegment(),Toast.LENGTH_LONG).show();

            Log.i("Insert",student.toString());

            clearFields();
        }else{
            String where = Util.COL_ID + " = "+rcvStudent.getId();
            int i = resolver.update(Util.STUDENT_URI,values,where,null);
            if(i>0){
                Toast.makeText(this,"Updation Successful",Toast.LENGTH_LONG).show();
                finish();
            }
        }


    }

    void clearFields(){
        eTxtName.setText("");
        eTxtEmail.setText("");
        eTxtPhone.setText("");
        spCity.setSelection(0);
        rbMale.setChecked(false);
        rbFemale.setChecked(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(0,101,0,"All Students");


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == 101){
            Intent i = new Intent(MainActivity.this,AllStudentsActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}
