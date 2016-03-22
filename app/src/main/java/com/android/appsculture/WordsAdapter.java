package com.android.appsculture;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class WordsAdapter extends RecyclerView.Adapter<WordsAdapter.WordsViewHolder> {

    Cursor cursor;
    private Context mContext;

    public WordsAdapter(Context context, Cursor cursor) {
        this.cursor = cursor;
        this.mContext = context;
    }


    @Override
    public WordsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, null);

        WordsViewHolder viewHolder = new WordsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(WordsViewHolder holder, int position) {
        //Download image using picasso library
        try{
            Picasso.with(mContext).load(cursor.getString(cursor.getColumnIndex("image")))
                    .error(R.drawable.blank_image)
                    .placeholder(R.drawable.blank_image)
                    .into(holder.image);

            //Setting text view title
            holder.word.setText(cursor.getString(cursor.getColumnIndex("word")));
            holder.meaning.setText(cursor.getString(cursor.getColumnIndex("meaning")));
        } catch(IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        cursor.moveToNext();
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public class WordsViewHolder extends RecyclerView.ViewHolder {
        protected ImageView image;
        protected TextView word;
        protected TextView meaning;

        public WordsViewHolder(View view) {
            super(view);
            this.image = (ImageView) view.findViewById(R.id.images);
            this.word = (TextView) view.findViewById(R.id.word);
            this.meaning = (TextView) view.findViewById(R.id.meaning);
        }
    }
}
