<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty faq ? 'Add New FAQ' : 'Edit FAQ'}</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        .container {
            max-width: 800px;
            margin: 0 auto;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            padding: 40px;
        }

        h1 {
            color: #333;
            margin-bottom: 10px;
            font-size: 28px;
        }

        .subtitle {
            color: #666;
            margin-bottom: 30px;
            font-size: 14px;
        }

        .form-group {
            margin-bottom: 25px;
        }

        label {
            display: block;
            margin-bottom: 8px;
            color: #333;
            font-weight: 500;
            font-size: 14px;
        }

        .required {
            color: #dc3545;
        }

        input[type="text"],
        textarea {
            width: 100%;
            padding: 12px 15px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
            font-family: inherit;
            transition: border-color 0.3s;
        }

        input[type="text"]:focus,
        textarea:focus {
            outline: none;
            border-color: #007bff;
        }

        textarea {
            resize: vertical;
            min-height: 150px;
            line-height: 1.5;
        }

        .char-count {
            text-align: right;
            font-size: 12px;
            color: #999;
            margin-top: 5px;
        }

        .alert {
            padding: 12px 20px;
            border-radius: 4px;
            margin-bottom: 20px;
            font-size: 14px;
        }

        .alert-error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }

        .form-actions {
            display: flex;
            gap: 15px;
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #eee;
        }

        .btn {
            padding: 12px 30px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            text-decoration: none;
            display: inline-block;
            transition: all 0.3s;
            font-weight: 500;
        }

        .btn-primary {
            background: #007bff;
            color: white;
        }

        .btn-primary:hover {
            background: #0056b3;
        }

        .btn-secondary {
            background: #6c757d;
            color: white;
        }

        .btn-secondary:hover {
            background: #5a6268;
        }

        .back-link {
            display: inline-flex;
            align-items: center;
            color: #007bff;
            text-decoration: none;
            margin-bottom: 20px;
            font-size: 14px;
        }

        .back-link:hover {
            text-decoration: underline;
        }

        @media (max-width: 768px) {
            .container {
                padding: 20px;
            }

            .form-actions {
                flex-direction: column;
            }

            .btn {
                width: 100%;
                text-align: center;
            }
        }
    </style>
</head>
<body>
<jsp:include page="header.jsp"/>

<div class="container-fluid p-0">
    <div class="row g-0">
        <div class="col-auto" style="width: 280px;">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <!-- Main Content Column -->
        <div class="col">
            <main class="main" style="padding: 1.25rem; padding-bottom: 0;">
                <div class="content-card"
                     style="background: white;
                      border: 1px solid #e5e7eb;
                       border-radius: 12px;
                        padding: 2.5rem;
                         min-height: calc(100vh - 64px - 1.25rem);
                          display: flex; flex-direction: column;
                           align-items: center; ">
                    <!-- Nội dung trang home của bạn ở đây -->
                    <div class="container">

                        <h1>${empty faq ? ' Add New FAQ' : ' Edit FAQ'}</h1>
                        <p class="subtitle">
                            ${empty faq ? 'Create a new frequently asked question' : 'Update the FAQ details'}
                        </p>

                        <c:if test="${param.error == 'empty'}">
                            <div class="alert alert-error">✗ All fields are required. Please fill in both question and answer.</div>
                        </c:if>

                        <form method="post" action="${pageContext.request.contextPath}/customerservice/faq"
                              onsubmit="return validateForm()">

                            <input type="hidden" name="action" value="${empty faq ? 'add' : 'update'}">
                            <c:if test="${not empty faq}">
                                <input type="hidden" name="id" value="${faq.FAQId}">
                            </c:if>

                            <div class="form-group">
                                <label for="question">
                                    Question <span class="required">*</span>
                                </label>
                                <input type="text"
                                       id="question"
                                       name="question"
                                       value="${faq.question}"
                                       maxlength="255"
                                       placeholder="Enter the FAQ question..."
                                       required>
                                <div class="char-count">
                                    <span id="questionCount">0</span>/255 characters
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="answer">
                                    Answer <span class="required">*</span>
                                </label>
                                <textarea id="answer"
                                          name="answer"
                                          placeholder="Enter the detailed answer..."
                                          required>${faq.answer}</textarea>
                                <div class="char-count">
                                    <span id="answerCount">0</span> characters
                                </div>
                            </div>

                            <c:if test="${not empty faq}">
                                <div class="form-group">
                                    <label style="display: flex; align-items: center; cursor: pointer; user-select: none;">
                                        <input type="checkbox"
                                               id="isActive"
                                               name="isActive"
                                               value="1"
                                            ${faq.active ? 'checked' : ''}
                                               style="width: auto; margin-right: 8px; cursor: pointer;">
                                        <span>Active (Display this FAQ to customers)</span>
                                    </label>
                                </div>
                            </c:if>

                            <div class="form-actions">
                                <button type="submit" class="btn btn-primary">
                                    ${empty faq ? 'Save FAQ' : ' Update FAQ'}
                                </button>
                                <a href="${pageContext.request.contextPath}/customerservice/faq"
                                   class="btn btn-secondary">✕ Cancel</a>
                            </div>
                        </form>
                    </div>
                </div>
            </main>
        </div>
    </div>
</div>
<jsp:include page="footer.jsp"/>

<script>
    // Character counter for question
    const questionInput = document.getElementById('question');
    const questionCount = document.getElementById('questionCount');

    function updateQuestionCount() {
        questionCount.textContent = questionInput.value.length;
    }

    questionInput.addEventListener('input', updateQuestionCount);
    updateQuestionCount();

    // Character counter for answer
    const answerInput = document.getElementById('answer');
    const answerCount = document.getElementById('answerCount');

    function updateAnswerCount() {
        answerCount.textContent = answerInput.value.length;
    }

    answerInput.addEventListener('input', updateAnswerCount);
    updateAnswerCount();

    // Form validation
    function validateForm() {
        const question = questionInput.value.trim();
        const answer = answerInput.value.trim();

        if (question === '' || answer === '') {
            alert('Please fill in both question and answer fields.');
            return false;
        }

        return true;
    }
</script>
</body>
</html>