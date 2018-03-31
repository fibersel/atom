package matchmaker;

import okhttp3.*;
import org.junit.Assert;


public class TestClient implements Runnable{

    private static String PROTOCOL = "http://";
    private static String HOST = "localhost";
    private static String PORT = "8080";
    private OkHttpClient client = new OkHttpClient();

    /*
    *   curl -X POST -i http://localhost:8080/matchmaker/join -d "name=test"
    * */

    @Override
    public void run()  {
        String name = StringGenerator.generateString();
        System.out.println(name);
        Response response;
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        Request request = new Request.Builder()
                .post(RequestBody.create(mediaType, "name=" + name))
                .url(PROTOCOL + HOST + PORT + "/matchmaker/join")
                .build();
        try {
            response = client.newCall(request).execute();
            Assert.assertTrue(response.code() == 200);
            System.out.println(name);
            System.out.println(response.body().toString());
        } catch (Exception e){

        }
    }
}
