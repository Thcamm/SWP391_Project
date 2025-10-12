document.addEventListener("DOMContentLoaded", () => {
    const chatToggle = document.getElementById("chat-toggle-btn");
    const chatBox = document.getElementById("chatbox");
    const sendBtn = document.getElementById("send-btn");
    const input = document.getElementById("user-input");
    const messages = document.getElementById("chat-messages");

    // Kiểm tra elements tồn tại
    if (!chatToggle || !chatBox) {
        console.error("Không tìm thấy chatbox elements!");
        return;
    }

    // Toggle chatbox khi click nút
    chatToggle.addEventListener("click", () => {
        console.log("Chat toggle clicked"); // Debug
        chatBox.classList.toggle("show");
        chatToggle.classList.toggle("active");
    });

    // Gửi tin nhắn
    sendBtn.addEventListener("click", sendMessage);

    input.addEventListener("keydown", (e) => {
        if (e.key === "Enter") {
            e.preventDefault();
            sendMessage();
        }
    });

    async function sendMessage() {
        const text = input.value.trim();
        if (!text) return;

        // Hiển thị tin nhắn user
        addMessage(text, "user");
        input.value = "";

        try {
            const res = await fetch("chat", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    contents: [
                        {
                            role: "user",
                            parts: [{
                                text: `Bạn là trợ lý chatbot cho một trang web sửa chữa ô tô. Nếu người dùng muốn đặt lịch hoặc xem dịch vụ, hãy hướng dẫn họ đăng nhập: <a href='http://localhost:8090/ExaminationSystem/login.jsp'>Đăng nhập</a>`
                            }]
                        },
                        {
                            role: "user",
                            parts: [{ text: text }]
                        }
                    ]
                })
            });

            const data = await res.json();
            const reply = data.candidates?.[0]?.content?.parts?.[0]?.text?.trim() || "Không có phản hồi.";

            addMessage(reply, "bot");
        } catch (err) {
            console.error("Lỗi:", err);
            addMessage("Lỗi kết nối server.", "bot");
        }
    }

    function addMessage(text, sender) {
        const messageDiv = document.createElement("div");
        messageDiv.className = `message ${sender}`; // Sửa ở đây!

        const textDiv = document.createElement("div");
        textDiv.className = "text";

        if (sender === "bot") {
            textDiv.innerHTML = text; // Cho phép HTML từ bot
        } else {
            textDiv.textContent = text;
        }

        messageDiv.appendChild(textDiv);
        messages.appendChild(messageDiv);
        messages.scrollTop = messages.scrollHeight;
    }
});
