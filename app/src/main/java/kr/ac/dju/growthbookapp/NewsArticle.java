package kr.ac.dju.growthbookapp;

/**
 * Created by geonyounglim on 2017. 6. 2..
 */
public class NewsArticle {
    int _id;
    String _articleTitle;
    String _articleDetailURL;
    String _articleAuthor;
    String _articleDate;


    public NewsArticle (int id, String title, String detailURL, String author, String date){
        _id = id;
        _articleTitle = title;
        _articleDetailURL = detailURL;
        _articleAuthor = author;
        _articleDate = date;

    }

    int getId() {
        return _id;
    }

    String getTitle() {
        return _articleTitle;
    }

    String getDetailURL () {
        return _articleDetailURL;
    }

    String getAuthor() {
        return _articleAuthor;
    }

    String getDate() {
        return _articleDate;
    }
}
