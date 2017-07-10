package kr.ac.dju.growthbookapp;

/**
 * Created by dodrn on 2017-06-29.
 */

public class BookListData{
    private String book_name;
    private String book_src;
    private String book_author;
    private String book_list;
    private String book_company;
    private String book_Dday;
    private String pass_point;
    private String auth_point;

    public BookListData(String name, String src, String bookauthor, String list, String company, String day, String pass, String autho)
    {
        book_name = name;
        book_src = src;
        book_author = bookauthor;
        book_list = list;
        book_company = company;
        book_Dday = day;
        pass_point = pass;
        auth_point = autho;
    }
    public String GetBookSubject()
    {
       return book_name;
    }
    public String GetBookSrc(){return book_src;}
    public String GetBookAuthor(){
        return book_author;
    }
    public String GetBookList(){
       return  book_list;
    }
    public String GetBookCompany(){return book_company;}
    public String GetBookDday(){
        return book_Dday;
    }
    public String GetPassPoint(){
        return pass_point;
    }
    public String GetAuthPoint(){
        return auth_point;
    }


}
