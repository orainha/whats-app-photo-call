package com.example.userasus.whatsappcall;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;


public class MainActivity extends AppCompatActivity {

    //phones to be contacted
    private static final String contact_lucinda = "+1-202-555-0169";
    private static final String contact_ogi = "+1-202-555-0148";
    private static final String contact_beta = "+1-202-555-0118";
    private static final String contact_ari = "+1-202-555-0155";

    //mimeString associated to whatsapp app
    //current mimeString associated to whatsapp business app (required for a device without SIM card)
    private static final String mimeString = "vnd.android.cursor.item/vnd.com.whatsapp.w4b.voip.call";
    //whatsapp for business (w4b) package used
    private static final String whatsAppPackage = "com.whatsapp.w4b";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Button-Images
        ImageButton btnBeta = findViewById(R.id.imgBtn1);
        ImageButton btnOgi = findViewById(R.id.imgBtn2);
        ImageButton btnLuc = findViewById(R.id.imgBtn3);
        ImageButton btnAri = findViewById(R.id.imgBtn4);

        btnAri.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                whatsappCall(contact_ari);
            }
        });

        btnLuc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                whatsappCall(contact_lucinda);
            }
        });

        btnOgi.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                whatsappCall(contact_ogi);
            }
        });

        btnBeta.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                whatsappCall(contact_beta);
            }

        });
    }

    public void whatsappCall(String contact)
    {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);


        //here you have to pass whatsApp contact  number  as  contact_number ..
        String name = getContactName(contact, MainActivity.this);

        long whatsappcall = getContactIdForWhatsAppCall(name, MainActivity.this);


        if (whatsappcall != 0) {
            intent.setDataAndType(Uri.parse("content://com.android.contacts/data/" + whatsappcall),
                    mimeString);
            intent.setPackage(whatsAppPackage);

            startActivity(intent);


            //Turn device speaker on (device used was a tablet)
            AudioManager audioManager = (AudioManager) MainActivity.this.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            if (!audioManager.isSpeakerphoneOn())
                audioManager.setSpeakerphoneOn(true);
            audioManager.setMode(AudioManager.MODE_NORMAL);

            /*String data = "content://com.android.contacts/data/" + whatsappcall;
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_VIEW);
            sendIntent.setDataAndType(Uri.parse(data), "vnd.android.cursor.item/vnd.com.whatsapp.voip.call");
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);*/

        }
    }

    public String getContactName(final String phoneNumber, Context context)
    {
        //going to self phone get name associated to the phone contact
        Uri uri=Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName="";
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName=cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }

    public  int getContactIdForWhatsAppCall(String name,Context context)
    {

        //"cursor" is created, it returns the ID of the whatsapp phone contact
        Cursor cursor = getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.Data._ID},
                ContactsContract.Data.DISPLAY_NAME + "=? and " + ContactsContract.Data.MIMETYPE + "=?",
                new String[]{name, mimeString},
                ContactsContract.Contacts.DISPLAY_NAME);

        if (cursor.getCount()>0)
        {
            cursor.moveToNext();
            int phoneContactID=  cursor.getInt(cursor.getColumnIndex(ContactsContract.Data._ID));
            System.out.println("Name: "+name+" id: "+phoneContactID);
            return phoneContactID;
        }
        else
        {
            System.out.println("count < 0");
            return 0;
        }
    }

}