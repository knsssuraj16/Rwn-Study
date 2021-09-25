package com.rwn.rwnstudy.utilities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.internal.GoogleApiManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rwn.rwnstudy.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapterForListView extends ArrayAdapter <MessageGetterSetter> {


    DatabaseReference databaseReferenceUserRef, databaseReferenceMessage;
    FirebaseAuth mAuth;
    TextView textViewReciverMessage, textViewSenderMessage,textViewisSeen;
    CircleImageView circleImageViewProfile;
    ImageView imageViewSender, imageViewReciever;

    String fromUserId;
    List list;
    String messageSenderId;

    public MessagesAdapterForListView(Context context, int resource, List <MessageGetterSetter> objects) {
        super(context, resource, objects);
        list= objects;
    }

    @Override
    public boolean hasStableIds() {
        return super.hasStableIds();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

            mAuth= FirebaseAuth.getInstance();
            messageSenderId = mAuth.getCurrentUser().getUid();
            MessageGetterSetter messages = getItem(position);

            fromUserId = messages.getFrom();

            if (fromUserId.equals(messageSenderId)) {
                Log.d("ADAPTERSURAJR", "getView: true "+position);

                convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.right_message_layout, parent, false);

            } else {

                Log.d("ADAPTERSURAJL", "getView: false "+position);
                convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.left_message_layout, parent, false);

            }
        }
//
//        textViewReciverMessage = convertView.findViewById(R.id.textView_chat_RecieverMessage);
        textViewSenderMessage = convertView.findViewById(R.id.textView_chat_senderMessage);
        textViewisSeen = convertView .findViewById(R.id.textviewSeen);
//        circleImageViewProfile = convertView.findViewById(R.id.circularView_chat_message_profile);
        //imageViewSender = convertView.findViewById(R.id.imageView_Sender);
//        imageViewReciever = convertView.findViewById(R.id.imageView_recieverMessage);
//
//        mAuth = FirebaseAuth.getInstance();


        MessageGetterSetter messages = getItem(position);
        String fromMessageType = messages.getType();

//        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        databaseReferenceUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);
//        databaseReferenceMessage = FirebaseDatabase.getInstance().getReference().child("Messages").child(uid).child(to);


        boolean isPhoto = messages.getImage() != null;

        String isSeenCheck= messages.getIsSeen();
        if (fromMessageType.equals("text")) {
//           textViewReciverMessage.setVisibility(View.INVISIBLE);
//           circleImageViewProfile.setVisibility(View.INVISIBLE);
//            if (fromUserId.equals(messageSenderId)) {
//
//                textViewSenderMessage.setText(messages.getMessage());
////                textViewSenderMessage.setBackgroundResource(R.drawable.sender_message_text_background);
////                textViewSenderMessage.setTextColor(Color.BLACK);
////                textViewSenderMessage.setGravity(Gravity.LEFT);
////                textViewSenderMessage.setText(messages.getMessage());
////                textViewSenderMessage.requestFocus();
//            } else {

            textViewSenderMessage.setText(messages.getMessage());

            if(position == list.size()- 1){
                textViewisSeen.setVisibility(View.VISIBLE);
                if(isSeenCheck.equals("true")){
                    textViewisSeen.setText("Seen");
                }
                else{
                    textViewisSeen.setText("delivered");
                }
            }
            else {
                textViewisSeen.setVisibility(View.GONE);
            }
//                textViewReciverMessage.setVisibility(View.VISIBLE);
//                textViewSenderMessage.setVisibility(View.INVISIBLE);
//                circleImageViewProfile.setVisibility(View.VISIBLE);
//                textViewReciverMessage.setBackgroundResource(R.drawable.reciever_message_background);
//                textViewReciverMessage.setTextColor(Color.BLACK);
//                textViewReciverMessage.setGravity(Gravity.RIGHT);
//                textViewReciverMessage.setText(messages.getMessage());
//                textViewReciverMessage.requestFocus();

//            }

        } else {

//            textViewReciverMessage.setVisibility(View.INVISIBLE);
//            circleImageViewProfile.setVisibility(View.INVISIBLE);
//            if (fromUserId.equals(messageSenderId)) {
//                textViewSenderMessage.setVisibility(View.GONE);
//                imageViewSender.setVisibility(View.VISIBLE);
//                Picasso.get().load(messages.getPicture()).into(imageViewSender);
//                imageViewSender.requestFocus();
//            } else {
//
//                textViewReciverMessage.setVisibility(View.VISIBLE);
//                circleImageViewProfile.setVisibility(View.VISIBLE);
//                textViewReciverMessage.setVisibility(View.GONE);
//                imageViewReciever.setVisibility(View.VISIBLE);
//                textViewSenderMessage.setVisibility(View.GONE);
//                Picasso.get().load(messages.getPicture()).into(imageViewReciever);
//                imageViewReciever.requestFocus();
//                imageViewSender.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//
//                        return true;
//                    }
//                });


        }


//        if (isPhoto) {
//            messageTextView.setVisibility(View.GONE);
//            photoImageView.setVisibility(View.VISIBLE);
//            Glide.with(photoImageView.getContext())
//                    .load(message.getPhotoUrl())
//                    .into(photoImageView);
//        } else {

        //  }


        return convertView;
    }

}
