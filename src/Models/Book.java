package Models;

public class Book {
	static int bookID;
	static int numOfBooks;
    int id;
    String title;
    String series;
    Integer seriesPart;
    String author;
    String publisher;
    String isbn;
    int copyright;
    String genre;
    String format;
    int edition;
    int pageCount;
    String language;
    String finished;

    public Book(int id,String title,String series,Integer seriesPart,String firstName,String lastName,String publisher,String isbn,int copyright,String genre){
        this.id=id;
        this.title=title;
        this.series=series;
        this.seriesPart=seriesPart;
        this.author=firstName+" "+lastName;
        this.publisher=publisher;
        this.isbn=isbn;
        this.copyright=copyright;
        this.genre=genre;
    }

    // Constructor for all parts of a book. Use if detailed information is needed
    public Book(int id,String title,String series,Integer seriesPart,String firstName,String lastName,String publisher,String isbn,int copyright,String genre,int edition,String language,int format,int finished,int pageCount){
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
        this.pageCount=pageCount;
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
        return id+" "+title+" "+author+" "+copyright+" "+genre+" "+isbn;
    }
    
    public static void setBookID(int id) {
    	bookID=id;
    }
    public static int getBookID() {
    	return bookID;
    }
    public static void incrementBookNum() {
    	numOfBooks++;
    }
    public static int getBookNum() {
    	return numOfBooks;
    }
    public static void resetBookNum() {
    	numOfBooks=0;
    }
    
    public void setId(int id) {this.id=id;}
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

    public int getId() { return id; }
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
    public Integer getSeriesPart() {return seriesPart;}
    public String getFinished(){ return finished; }
    public int getPageCount() { return pageCount; }
}
