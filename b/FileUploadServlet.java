import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.StringBuilder;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.time.LocalDate;

/**
 * Servlet that uploads images to the oracle database.
 *
 * @author Jonny, Arka
 */
@MultipartConfig
public class FileUploadServlet extends HttpServlet {

    private static int ORACLE_PORT_NUMBER = 1521;
    private String USER = "TOMCAT";
    private String PASS = "oracle";

    private static final long serialVersionUID = 3289324784888498005L;

    /**
     * Creates the initial html page and checks if the user is logged in.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.append("<!DOCTYPE html>\r\n")
                .append("<html>\r\n")
                .append("    <head>\r\n")
                .append("        <title>File Upload Form</title>\r\n")
                .append("    </head>\r\n")
                .append("    <body>\r\n");

        writer.append("<h1>Upload file</h1>\r\n");
        writer.append("<form method=\"POST\" action=\"upload\" ")
                .append("enctype=\"multipart/form-data\">\r\n");
        writer.append("<input type=\"file\" name=\"fileName\"/><br/><br/>\r\n");
        writer.append("Caption: <input type=\"text\" name=\"caption\"<br/><br/>\r\n");
        writer.append("<br />\n");
        writer.append("Date: <input type=\"date\" name=\"date\"<br/><br/>\r\n");
        writer.append("<br />\n");
        writer.append("<input type=\"submit\" value=\"Submit\"/>\r\n");
        writer.append("</form>\r\n");
        writer.append("<form action=\"logout\" method=\"GET\">\n");//Adds logout button
        writer.append("<input type=\"submit\" value=\"LOGOUT\" />\n");
        writer.append("<div style=\"position: absolute; top: 0; right: 0; width: 100px; text-align:right;\">\n");
        writer.append(""+ "1");
        writer.append("</div>\n");
        writer.append("</body>\r\n").append("</html>\r\n");
    }

    /**
     * Gets the form data and inserts it into the database.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        Part filePart = request.getPart("fileName");
        String captionName = request.getParameter("caption");
        String formDate = request.getParameter("date");
        String fileName = filePart.getSubmittedFileName();
        filePart.write(System.getProperty("catalina.base") + "/webapps/ROOT/images/" + fileName);
        String username = "user1";
        try {
            insertToDB(out, username, fileName, captionName, formDate);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    /**
     * Uses the parameters to insert values into the oracle database.
     *
     * @param out
     * @param username
     * @param fileName
     * @param captionName
     * @param formDate
     * @throws FileNotFoundException
     */
    private void insertToDB(PrintWriter out, String username, String fileName, String captionName, String formDate) throws FileNotFoundException {
        try {
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:" + ORACLE_PORT_NUMBER + ":XE", USER, PASS);
            File image = new File(System.getProperty("catalina.base") + "/webapps/ROOT/images/" + fileName);
            PreparedStatement ps = con.prepareStatement("INSERT INTO PHOTOS (PHOTO_ID, USER_ID, FILE_NAME, CAPTION_NAME, DATE_TAKEN, FILE_CONTENTS) VALUES (?, ?, ?, ?, ?, ?)");

            int primaryKey = getNextPhotoPrimaryKey();

            InputStream is = new FileInputStream(
                    System.getProperty("catalina.base") + "/webapps/ROOT/images/" + fileName);

            ps.setInt(1, primaryKey);
            ps.setInt(2, 26); // need to use an existing user id inside YOUR "USERS" table
            ps.setString(3, fileName);
            ps.setString(4, captionName);
            ps.setDate(5, getDate(formDate));
            ps.setBinaryStream(6, is, (int) (image.length()));

            ps.executeUpdate();
            con.close();
            String title = fileName + " img added successfully";
            out.println("<h1 align=\"center\">" + title + "</h1>");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds and returns the user's ID in the database.
     *
     * @param out
     * @param username
     * @return
     */
    private String getUserID(PrintWriter out, String username) {

        String result = "";
        Connection con;
        try {
            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:" + ORACLE_PORT_NUMBER + ":XE", USER, PASS);
            Statement query = con.createStatement();
            ResultSet rs = query.executeQuery("SELECT * FROM USERS");
            while (rs.next()) {
                String db_username = rs.getString("USER_ID");
                if (db_username.equals(username)) {
                    result = rs.getString("ID");
                    break;
                }
            }
            rs.close();

        } catch (SQLException e) {
            out.println("exception");
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Gets the primary key needed to insert the photo in the database.
     *
     * @return
     */
    private int getNextPhotoPrimaryKey() {
        int res = Integer.MAX_VALUE - 1;
        try {
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:" + ORACLE_PORT_NUMBER + ":XE",
                    USER, PASS);

            String sequenceString = "select PHOTO_SEQUENCE.NEXTVAL from dual";
            PreparedStatement pst = con.prepareStatement(sequenceString);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                res = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Converts the date into SQL Date.
     *
     * @param date
     * @return
     */
    private Date getDate(String date) {

        Date sqlDate = null;

        LocalDate ourDate = LocalDate.parse(date);
        sqlDate = java.sql.Date.valueOf(ourDate);

        return sqlDate;
    }
}


