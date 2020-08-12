package leadinguniversity.bookapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class FindBookAdapter extends ArrayAdapter<FindBook> {

    TextView bookTitle, bookAuthor, bookDescription;
    ImageView bookImage;
    RatingBar bookRating;

    public FindBookAdapter(Context context, List<FindBook> findBooks){
        super(context, 0, findBooks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem==null)
            listItem = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);

        FindBook findBook = getItem(position);

        bookImage = listItem.findViewById(R.id.book_img);
        Picasso.get().load(findBook.getmImageUrl()).fit().into(bookImage);

        bookTitle = listItem.findViewById(R.id.book_title);
        bookTitle.setText(findBook.getmTitle());

        bookAuthor = listItem.findViewById(R.id.book_author);
        bookAuthor.setText(findBook.getmAuthor());

        bookRating = listItem.findViewById(R.id.book_rating);
        bookRating.setRating((float) findBook.getmRating());

        bookDescription = listItem.findViewById(R.id.book_disc);
        bookDescription.setText(findBook.getmDescription());

        return listItem;
    }
}