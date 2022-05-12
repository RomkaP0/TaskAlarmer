package com.kradyk.taskalarmer;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class ItemAdapter  extends RecyclerView.Adapter<ItemAdapter.ViewHolder>{
    DBHelper dbHelper;

    private final LayoutInflater inflater;
    private final List<Item> items;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private String[] notif ;
    private String[] repeate ;


    ItemAdapter(Context context, List<Item> items, String[] notif, String[] repeate) {
        this.items = items;
        this.inflater = LayoutInflater.from(context);
        this.notif = notif;
        this.repeate = repeate;
    }
    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.cardview, parent, false);
        return new ViewHolder(view, new MyClickListener(){
            @Override
            public void onEdit(int p){
                Log.d("adLog",Integer.toString(p));
                Item it= items.get(p);
                Intent i = new Intent("com.example.CreateEventActivity");
                Bundle extras = new Bundle();
                extras.putString("date", it.getDate());
                extras.putString("from", "rv");
                extras.putString("id", it.getId());
                extras.putString("title", it.getTitle());
                extras.putString("timeb", it.getTimeb());
                extras.putString("timee", it.getTimee());
                extras.putString("desc", it.getDesc());
                extras.putString("notify", it.getTimenotif());
                extras.putString("repeat", it.getRepeat());
                extras.putString("paral", it.getParal());
                i.putExtras(extras);
                startActivity(parent.getContext(), i, null);
            }
            @SuppressLint("UnspecifiedImmutableFlag")
            @Override
            public void onDelete(int p){
                Log.d("adLog",Integer.toString(p));
                Item it= items.get(p);
                dbHelper = new DBHelper(parent.getContext(), "String",1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete("events","_id = ?", new String[]{it.getId()});
                Intent intent = new Intent(parent.getContext(), AlarmReceiver.class);
                pendingIntent =PendingIntent.getBroadcast(parent.getContext(), Integer.parseInt(it.getId()),intent, 0);
                if(alarmManager == null){
                    alarmManager = (AlarmManager) parent.getContext().getSystemService(Context.ALARM_SERVICE);
                }
                alarmManager.cancel(pendingIntent);
            }

            @Override
            public void onShare(int p) {
                Item it = items.get(p);
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, it.getTitle()+" "+parent.getResources().getString(R.string.schedule) +"\n"+ it.getDate()+"    "+it.getTimeb()+"-"+it.getTimee()+"\n"+ it.getDesc());
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(parent.getContext(), shareIntent,Bundle.EMPTY);
            }
        });
    }

    @Override
    public void onBindViewHolder(ItemAdapter.ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.titleView.setText(item.getTitle());
        String str = (item.getTimeb()+"-"+item.getTimee());
        if (item.getCat()==2)
            holder.imgline.setColorFilter(ContextCompat.getColor(holder.imageView.getContext(), R.color.teal_200));
        if (item.getCat()==1)
            holder.imgline.setColorFilter(ContextCompat.getColor(holder.imageView.getContext(), R.color.purple_500));
        if (item.getCat()==0)
            holder.imgline.setColorFilter(ContextCompat.getColor(holder.imageView.getContext(), R.color.purple_700));
        holder.notifyat.setText(R.string.timenotif);
        if (item.getTimenotif().equals("At moment")||item.getTimenotif().equals("В момент"))
            holder.notifyat.append(" "+notif[0]);
        else if(item.getTimenotif().contains("5"))
                holder.notifyat.append(" "+notif[1]);
            else if(item.getTimenotif().contains("1")){
                if(item.getTimenotif().contains("10"))
                    holder.notifyat.append(" "+notif[2]);
                else if (item.getTimenotif().contains("12"))
                    holder.notifyat.append(" "+notif[6]);
                else
                    holder.notifyat.append(" "+notif[4]);
            }
            else if(item.getTimenotif().contains("3"))
                holder.notifyat.append(" "+notif[3]);
            else if(item.getTimenotif().contains("6"))
                holder.notifyat.append(" "+notif[5]);
            else if(item.getTimenotif().contains("2"))
                holder.notifyat.append(" "+notif[7]);
            else
                holder.notifyat.append(" "+notif[8]);

        if (item.getRepeat().equals("Нет")||item.getRepeat().equals("No")){
            holder.imageView.setVisibility(View.GONE);
            holder.dateView.setText(item.getDate());}
        else {
            holder.dateView.setText(R.string.repeate);
            if (item.getRepeat().equals("Daily")||item.getRepeat().equals("Ежедневно"))
                holder.dateView.append(" "+repeate[1]);
            else if (item.getRepeat().equals("Weekly")||item.getRepeat().equals("Еженедельно"))
                holder.dateView.append(" "+repeate[2]);
            else if (item.getRepeat().equals("Monthly")||item.getRepeat().equals("Ежемесячно"))
                holder.dateView.append(" "+repeate[3]);
            else
                holder.dateView.append(" "+repeate[4]);
        }
        holder.timeView.setText(str);
        if (item.getParal().equals("1"))
            holder.parall.setText(R.string.paral);
        else
            holder.parall.setVisibility(View.GONE);
        holder.descView.setText(item.getDesc());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        MyClickListener listener;
        final TextView titleView, timeView,  dateView,descView, notifyat, parall;
        final ImageButton edit, delete , share;
        ImageView imgline;
        ImageView imageView;
        ConstraintLayout constraintLayout;
        public ViewHolder(View view, MyClickListener listener){
            super(view);
            imgline = view.findViewById(R.id.linecrd);
            titleView = view.findViewById(R.id.title1);
            timeView = view.findViewById(R.id.time1);
            dateView=view.findViewById(R.id.date1);
            descView=view.findViewById(R.id.desc);
            edit = view.findViewById(R.id.edit);
            imageView = view.findViewById(R.id.repeatingIC);
            notifyat = view.findViewById(R.id.notify);
            delete = view.findViewById(R.id.delete);
            parall = view.findViewById(R.id.parall);
            share = view.findViewById(R.id.share);
            constraintLayout = view.findViewById(R.id.layoutdis);


            this.listener = listener;

            edit.setOnClickListener(this);
            delete.setOnClickListener(this);
            share.setOnClickListener(this);
        }
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.edit:
                    listener.onEdit(this.getAdapterPosition());
                    break;
                case R.id.delete:
                    @SuppressLint("PrivateResource") MaterialAlertDialogBuilder builderdlg= new MaterialAlertDialogBuilder(view.getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered)
                            .setTitle(R.string.deleteev)
                            .setMessage("\n"+ view.getResources().getString( R.string.deleteevbody)+"\n")
                            .setPositiveButton("OK", (dialogInterface, i) -> {
                                listener.onDelete(getAdapterPosition());
                                constraintLayout.setVisibility(View.INVISIBLE);
                                constraintLayout.setMaxHeight(1);
                            })
                            .setNegativeButton(R.string.cancel, null);
                    builderdlg.show();


                    break;
                case R.id.share:
                    listener.onShare(getAdapterPosition());
                    break;
                default:
                    break;
            }
        }
    }

    public interface MyClickListener {
        void onEdit(int p);
        void onDelete(int p);
        void onShare(int p);
    }
    }


