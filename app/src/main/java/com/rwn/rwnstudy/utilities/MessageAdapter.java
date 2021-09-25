package com.rwn.rwnstudy.utilities;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rwn.rwnstudy.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {


    private List <MessageGetterSetter> userMessageList;

    private  FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    String messageRecieverId;


    public MessageAdapter(List<MessageGetterSetter> userMessageList,String messageRecieverId) {

        this.userMessageList = userMessageList;
        this.messageRecieverId= messageRecieverId;
    }




    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_layout, parent, false);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Messages").child(mAuth.getCurrentUser().getUid()).child(messageRecieverId);
        return new MessageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {


        holder.setIsRecyclable(false);

        String messageSenderId= mAuth.getCurrentUser().getUid();

        final MessageGetterSetter messageGetterSetter= userMessageList.get(position);


        String  fromUserID= messageGetterSetter.getFrom();
        String type = messageGetterSetter.getType();

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                CharSequence options[] = new CharSequence[]{
                        "Delete"};
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.imageViewReciever.getContext());

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {


                            databaseReference.child(messageGetterSetter.getMsgId()).removeValue();


                        }

                    }

                });

                builder.show();
                Log.d("flople", "onLongClick: "+holder.getAdapterPosition());
                Log.d("flople", "onLongClick: "+messageGetterSetter.getMsgId());

                return true;
            }
        });






        if (position == userMessageList.size()-1 && messageGetterSetter.getFrom().equals(mAuth.getCurrentUser().getUid())){

            holder.textViewSeen.setVisibility(View.VISIBLE);
            if(messageGetterSetter.getIsSeen().equals("false")) {
                holder.textViewSeen.setText("delivered");
            }
            else{
                holder.textViewSeen.setText("Seen");
            }
        }
        else {
            holder.textViewSeen.setVisibility(View.GONE);

        }



        if(type.equals("text")){
            {
                if(fromUserID.equals(messageSenderId)){

                    messageSenderIdDataSet(holder,messageGetterSetter);
                }
                else{
                    messageRecieverIdDataSet(holder,messageGetterSetter);

                }
            }

            holder.imageViewSender.setImageDrawable(null);
            holder.imageViewReciever.setImageDrawable(null);

        }else {
            if(fromUserID.equals(messageSenderId)){

                holder.imageViewReciever.setImageDrawable(null);
                loadImageForSender(holder,messageGetterSetter);
            }
            else{

                holder.imageViewSender.setImageDrawable(null);
                loadImageForReciever(holder,messageGetterSetter);
            }
        }



    }

    private void loadImageForReciever(MessageViewHolder holder, MessageGetterSetter messageGetterSetter) {

        holder.frameLayoutrecieverImage.setVisibility(View.VISIBLE);
        Picasso.get().load(messageGetterSetter.getImage()).resize(300,300).noFade().into(holder.imageViewReciever);
        holder.textViewRecieverImageTime.setText(messageGetterSetter.getTime());
    }

    private void loadImageForSender(MessageViewHolder holder, MessageGetterSetter messageGetterSetter) {



        holder.frameLayoutsenderImage.setVisibility(View.VISIBLE);
        Picasso.get().load(messageGetterSetter.getImage()).resize(300,300).noFade().into(holder.imageViewSender);

        holder.textViewtimesenderImageTime.setText(messageGetterSetter.getTime());
    }



    private void messageRecieverIdDataSet(MessageViewHolder holder, MessageGetterSetter messageGetterSetter) {

        holder.frameLayoutRecieverMessage.setVisibility(View.VISIBLE);

        holder.textViewRecieverMessage.setBackgroundResource(R.drawable.reciever_message_background);
        holder.textViewRecieverMessage.setText(messageGetterSetter.getMessage());
        holder.textViewrecieverMessageTime.setText(messageGetterSetter.getTime());
    }

    private void messageSenderIdDataSet(MessageViewHolder holder, MessageGetterSetter messageGetterSetter) {


        holder.frameLayoutsenderMessage.setVisibility(View.VISIBLE);
        holder.textViewSenderMessage.setBackgroundResource(R.drawable.sender_message_text_background);
        holder.textViewSenderMessage.setText(messageGetterSetter.getMessage());
        holder.textViewtimeSendermesageTime.setText(messageGetterSetter.getTime());
    }


    @Override
    public int getItemCount() {
        return userMessageList.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSenderMessage,textViewRecieverMessage,textViewSeen,textViewtimeSendermesageTime,textViewtimesenderImageTime,textViewRecieverImageTime,textViewrecieverMessageTime;
        ImageView imageViewSender,imageViewReciever;
        FrameLayout frameLayoutsenderMessage,frameLayoutsenderImage,frameLayoutRecieverMessage,frameLayoutrecieverImage;



        public MessageViewHolder(View itemView) {
            super(itemView);
            textViewSenderMessage = itemView.findViewById(R.id.textView_chat_senderMessage);
            textViewRecieverMessage= itemView.findViewById(R.id.textView_chat_RecieverMessage);
            imageViewReciever = itemView.findViewById(R.id.imageView_imageReciever);
            imageViewSender= itemView.findViewById(R.id.imageView_imageSender);
            frameLayoutsenderMessage= itemView.findViewById(R.id.frame_layout_sender_message);
            frameLayoutsenderImage= itemView.findViewById(R.id.frame_layout_sender_image);
            frameLayoutRecieverMessage= itemView.findViewById(R.id.frame_layout_reciever_message);
            frameLayoutrecieverImage= itemView.findViewById(R.id.frame_layout_reciever_image);
            textViewRecieverImageTime= itemView.findViewById(R.id.textview_time_reciver);
            textViewrecieverMessageTime= itemView.findViewById(R.id.textview_time_reciever_text);
            textViewtimesenderImageTime= itemView.findViewById(R.id.textview_time_sender);
            textViewtimeSendermesageTime= itemView.findViewById(R.id.textview_time_sender_text);
            textViewSeen= itemView.findViewById(R.id.textviewSeen_sender);







        }
    }

}
