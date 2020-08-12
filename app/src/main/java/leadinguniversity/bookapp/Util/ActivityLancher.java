package leadinguniversity.bookapp.Util;

import android.content.Context;
import android.content.Intent;

import leadinguniversity.bookapp.Book;
import leadinguniversity.bookapp.Ui.BookDetail;
import leadinguniversity.bookapp.Ui.PDFViewerActivity;



public final class ActivityLancher {
    public static final String BOOK_KEY = "book";
    public static final String publisher_KEY = "publisher";





    public static void openPDFViewerActivity(Context context, Book book){
        Intent i = new Intent(context, PDFViewerActivity.class);
        i.putExtra(BOOK_KEY, book);
        context.startActivity(i);
    }
    public static void openBookDetails(Context context, Book book){
        Intent i = new Intent(context, BookDetail.class);
        i.putExtra(BOOK_KEY, book);
        context.startActivity(i);
    }


}
