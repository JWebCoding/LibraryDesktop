package Models;

public class Book {
    int id;
    String title;
    String series;
    int seriesPart;
    String author;
    String publisher;
    String isbn;
    int copyright;
    String genre;
    String format;
    int edition;
    String language;
    String finished;

    public Book(int id,String title,String series,String firstName,String lastName,String publisher,String isbn,int copyright,String genre,int format){
        this.id=id;
        this.title=title;
        this.series=series;
        this.author=firstName+" "+lastName;
        this.publisher=publisher;
        this.isbn=isbn;
        this.copyright=copyright;
        this.genre=genre;
        this.format=determineFormat(format);
    }

    // Constructor for all parts of a book. Use if detailed information is needed
    public Book(String title,String series,int seriesPart,String firstName,String lastName,String publisher,String isbn,int copyright,String genre,int edition,String language,int format,int finished){
        this.id=id;
        this.title=title;
        this.series=series;
        this.seriesPart=seriesPart;
        this.author=firstName+" "+lastName;
        this.publisher=publisher;
        this.isbn=isbn;
        this.copyright=copyright;
        this.genre=genre;
        this.edition=edition;
        this.language=language;
        this.format=determineFormat(format);
        this.finished=determineFinished(finished);
    }
    // Empty Constructor
    public Book(){ }

    private String determineFormat(int format){
        if(format==0){ return "P"; }
        else{ return "H"; }
    }

    private String determineFinished(int finished){
        if(finished==0){ return "No"; }
        else { return "Yes"; }
    }

    public String toString(){
        return title+" "+author+" "+copyright+" "+genre+" "+isbn;
    }

    public void setTitle(String title){ this.title=title; }
    public void setAuthor(String author){ this.author=author; }
    public void setPublisher(String publisher){ this.publisher=publisher; }
    public void setCopyright(int copyright){ this.copyright=copyright; }
    public void setIsbn(String isbn){ this.isbn=isbn; }
    public void setEdition(int edition){ this.edition=edition; }
    public void setLanguage(String language){ this.language=language;}
    public void setFormat(String format){ this.format=format; }
    public void setSeries(String series){ this.series=series; }
    public void setFinished(String finished){ this.finished=finished; }

    public String getTitle(){ return title; }
    public String getAuthor(){ return author; }
    public String getPublisher(){ return publisher; }
    public int getCopyright(){ return copyright; }
    public String getIsbn(){ return isbn; }
    public int getEdition(){ return edition; }
    public String getGenre(){ return genre; }
    public String getLanguage(){ return language; }
    public String getFormat(){ return format; }
    public String getSeries(){ return series; }
    public String getFinished(){ return finished; }
}
