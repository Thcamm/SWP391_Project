package controller.customerservice;

import dao.customer.CustomerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.customer.Customer;

import java.io.IOException;
import java.util.List;

@WebServlet("/customerservice/home")
public class CSHome extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/view/customerservice/customerservice_home.jsp")
                .forward(request, response);


    }
}
