package Models;

public class SQLCommands {
    // Generic Select queries
    public String selectAllBook="select * from library.book order by name;";
    public String selectAllAuthor="select * from library.author order by first_name;";
    public String selectAllGenre="select * from library.genre order by genre_name;";
    public String selectFictionGenre="select * from library.genre where genre_type=0 order by genre_name;";
    public String selectNonFictionGenre="select * from library.genre where genre_type=1 order by genre_name;";
    public String selectAllPublisher="select * from library.publisher order by publisher_name;";
    public String selectAllSeries="select * from library.series order by series_name;";
    public String selectAllUser="select * from library.user;";
    public String selectAllLanguage="select * from library.language order by language_name;";
    // Primary queries
    public String selectTableBookInfo="select bookID,title,s.series_name,series_part,first_name,middle_name,last_name,p.publisher_name,isbn,edition,copyright,g.genre_type,g.genre_name,format,pages,l.language_name,notes from book join language l on book.languageID = l.languageID join publisher p on book.publisherID = p.publisherID join author a on book.authorID = a.authorID join series s on book.seriesID = s.seriesID join genre g on book.genreID = g.genreID;";
    public String selectNewAuthorID="select authorID from author where firstName='%s' and lastName='%s' and nation='%s';";
    public String selectSpecificBook="select bookID,title,series_name,series_part,first_name,middle_name,last_name,publisher_name,isbn,edition,copyright,genre_name,genre_type,format,pages,language_name,notes from book join language l on book.languageID=l.languageID join publisher p on book.publisherID = p.publisherID join author a on book.authorID = a.authorID join series s on book.seriesID = s.seriesID join genre g on book.genreID = g.genreID where bookID='%s';";
    // Insert statements
    public String insertIntoAuthor="insert into library.author (first_name,middle_name, last_name, nation, birth, death) values ('%s',%s,'%s','%s',%d,%d);";
    public String insertIntoPublisher="insert into library.publisher (publisher_name, publisher_location) values ('%s','%s');";
    public String insertIntoGenre="insert into library.genre (genre_name, genre_type) values ('%s',%d);";
    public String insertIntoSeries="insert into library.series (series_name) values ('%s');";
    public String insertIntoLanguage="insert into library.language (language_Name, language_suffix) values ('%s','%s');";
    public String insertIntoBook="insert into library.book (authorID, publisherID, title, copyright, isbn, edition, genreID, series_part, format, pages, languageID, seriesID, notes) VALUES (%d,%d,'%s',%d,%d,%d,%d,%d,%d,%d,%d,%d,'%s');";
    public String updateBookToFinished="update library.book set finished = 1 where bookID= '%s';";
    // Remove statements
    public String removeBookFromDatabase="Delete from library.book where bookID='%s';";
    // Update statements
    public String updateBookInformation="update library.book set authorID=%d, publisherID=%d, title='%s', copyright=%d, isbn=%d, genreID=%d, seriesID=%d, series_part=%d, format=%d, pages=%d, languageID=%d, notes='%s' where bookID=%d;";
}
