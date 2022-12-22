package community.mingle.app.src.firebase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import community.mingle.app.src.domain.TableType;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FirebaseCloudMessageService {

    //***자리에 프로젝트 이름-프로젝트 ID 형태를 받아와야됨
//    private final String API_URL = "https://fcm.googleapis.com/v1/projects/mingle-348a1/messages:send"; //ios
    private final String API_URL = "https://fcm.googleapis.com/v1/projects/mingl-871e5/messages:send"; //android
    private final ObjectMapper objectMapper;

    public void sendMessageTo(String targetToken, String title, String body, TableType tableType, Long postId) throws IOException{
        String message = makeMessage(targetToken, title, body, tableType, postId);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(API_URL).post(requestBody).addHeader("AUTHORIZATION", "Bearer " + getAcccessToken())
                .addHeader("CONTENT_TYPE", "application/json; UTF-8")
                .build();
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
    }

    private String makeMessage(String targetToken, String title, String body, TableType tableType, Long postId) throws com.fasterxml.jackson.core.JsonProcessingException {

        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder().token(targetToken)
                        .notification(FcmMessage.Notification.builder().title(title).body(body).image(null).build())
                        .data(FcmMessage.Data.builder().tableId(String.valueOf(tableType)).postId(String.valueOf(postId)).build())
                        .build())
                .validate_only(false).build();
        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getAcccessToken() throws IOException {
        //이 위치에 키를 비공개 키 집어넣어야됨
        String firebaseConfigPath = "firebase/mingl-871e5-firebase-adminsdk-tuqz6-91d17a5e99.json"; //android
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}
