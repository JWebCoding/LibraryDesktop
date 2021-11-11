package Models;

public class SQLCommands {
    // Generic Select queries
    public String selectAllBook="select * from library.book order by name;";
    public String selectAllAuthor="select * from library.author order by firstname;";
    public String selectAllGenre="select * from library.genre order by genre_name;";
    public String selectAllPublisher="select * from library.publisher order by publisher_name;";
    public String selectAllSeries="select * from library.series order by series_name;";
    public String selectAllUser="select * from library.user;";
    public String selectAllLanguage="select * from library.language order by language_name;";
    // Primary queries
    public String selectTableBookInfo="select bookID,title,series_name,series_part,firstName,middleName,lastName,publisher_name,isbn,edition,copyright,genre_name,format,pages,language_name from book join language l on book.languageID=l.languageID join publisher p on book.publisherID = p.publisherID join author a on book.authorID = a.authorID join series s on book.seriesID = s.seriesID join genre g on book.genreID = g.genreID;";
    
    public String selectNewAuthorID="select authorID from author where firstName='%s' and lastName='%s' and nation='%s';";
    public String selectSpecificBook="select bookID,title,series_name,series_part,firstName,middlename,lastName,publisher_name,isbn,edition,copyright,genre_name,genre_type,format,pages,language_name from book join language l on book.languageID=l.languageID join publisher p on book.publisherID = p.publisherID join author a on book.authorID = a.authorID join series s on book.seriesID = s.seriesID join genre g on book.genreID = g.genreID where bookID='%s';";
    // Insert statements
    public String insertIntoAuthor="insert into library.author (firstName,middleName, lastName, nation, birth, death) values ('%s',%s,'%s','%s',%d,%d);";
    public String insertIntoPublisher="insert into library.publisher (publisher_name, publisher_location) values ('%s','%s');";
    public String insertIntoGenre="insert into library.genre (genre_name, genre_type) values ('%s',%d);";
    public String insertIntoSeries="insert into library.obvListSeries (series_name) values ('%s'); ";
    public String insertIntoLanguage="insert into library.language (language_Name, language_suffix) values ('%s','%s');";
    public String insertIntoBook="insert into library.book (authorID, publisherID, title, copyright, isbn, edition, genreID, series_part, format, pages, languageID, seriesID) VALUES (%d,%d,'%s',%d,%d,%d,%d,%d,%d,%d,%d,%d);";
    public String updateBookToFinished="update library.book set finished = 1 where bookID= '%s';";
    // Remove statements
    public String removeBookFromDatabase="Delete from library.book where bookID='%s';";
    // Update statements
    public String updateBookInformation="update library.book set authorID='%d', publisherID='%d', title='%s', copyright='%d', isbn='%d', genreID='%d',seriesID='%d',series_part=%d,format='%d',pages='%d',languageID='%d' where bookID='%d';";
}
