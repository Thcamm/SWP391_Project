package service;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;

public class GeminiService {
    private static final String PROJECT_ID = "swp391project-474416";
    private static final String LOCATION = "asia-southeast1";
    private static final String MODEL_NAME = "gemini-1.5-flash";

    private static final String SYSTEM_INSTRUCTION =
            "Bạn là trợ lý ảo của website quản lý kho linh kiện xe máy. " +
                    "Bạn chỉ được trả lời các câu hỏi liên quan đến: " +
                    "- Quản lý kho hàng (inventory management) " +
                    "- Linh kiện xe máy (motorcycle parts) " +
                    "- Yêu cầu nhập/xuất kho " +
                    "- Thông tin sản phẩm, giá cả, số lượng tồn kho " +
                    "- Hướng dẫn sử dụng website " +
                    "Nếu câu hỏi không liên quan đến những chủ đề trên, hãy lịch sự từ chối và " +
                    "hướng dẫn người dùng hỏi về các chủ đề phù hợp.";

    public String generateResponse(String userMessage) throws Exception {
        try (VertexAI vertexAI = new VertexAI(PROJECT_ID, LOCATION)) {
            GenerativeModel model = new GenerativeModel(MODEL_NAME, vertexAI);

            // Tạo prompt với context
            String fullPrompt = SYSTEM_INSTRUCTION + "\n\nCâu hỏi: " + userMessage;

            GenerateContentResponse response = model.generateContent(fullPrompt);
            return ResponseHandler.getText(response);
        }
    }
}
