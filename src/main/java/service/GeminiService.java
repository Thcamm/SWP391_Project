package service;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;

public class GeminiService {
    private static final String PROJECT_ID = "your-project-id";
    private static final String LOCATION = "us-central1";
    private static final String MODEL_NAME = "gemini-1.5-flash";

    public String generateResponse(String userMessage) throws Exception {
        try (VertexAI vertexAI = new VertexAI(PROJECT_ID, LOCATION)) {
            GenerativeModel model = new GenerativeModel(MODEL_NAME, vertexAI);
            GenerateContentResponse response = model.generateContent(userMessage);
            return ResponseHandler.getText(response);
        }
    }
}
