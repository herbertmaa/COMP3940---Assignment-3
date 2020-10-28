
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * This utility class provides an abstraction layer for sending multipart HTTP
 * POST requests to a web server.
 */
public class Main
{
    private String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;

    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     *
     * @param requestURL
     * @param charset
     * @throws IOException
     */
    public Main(String requestURL, String charset) throws IOException
    {
        this.charset = charset;
        // creates a unique boundary based on time stamp
        boundary = "===" + Long.toHexString(System.currentTimeMillis());
        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
    }

    public static void main(String[] args)
    {
        String charset = "UTF-8";
        String requestURL = "http://localhost:8081/a3/upload";
        String file_path = "C:\\tomcat\\webapps\\a3\\WEB-INF\\classes\\12.png";

        //path of file to uploaded
        File uploadFile = new File(file_path);

        //caption for the image
        String caption = "herbert are  you watching";

        //date format yyyy-mm-dd
        String date = "2020-11-21";

        try
        {
            Main multipart = new Main(requestURL, charset);

            multipart.addFilePart("fileName", uploadFile);
            multipart.addFormField("caption", caption);
            multipart.addFormField("date", date);

            multipart.writer.append("--" + multipart.boundary + "--").append(LINE_FEED).flush();
            multipart.writer.close();

            int responseCode = multipart.httpConn.getResponseCode();
            System.out.println("Response code: " + responseCode);

        } catch (IOException ex)
        {
            System.err.println(ex);
        }
    }

    /**
     * Adds a form field to the request
     *
     * @param name  field name
     * @param value field value
     */
    public void addFormField(String name, String value)
    {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + charset).append(LINE_FEED);
        writer.append(LINE_FEED).append(value).append(LINE_FEED).flush();
    }

    /**
     * Adds a upload file section to the request
     *
     * @param fieldName  name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile) throws IOException
    {
        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
        writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED).flush();
        outputStream.flush();
        writer.append(LINE_FEED).flush();
    }

}
