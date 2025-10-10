package service.carservice;

import model.servicetype.Service;
import dao.carservice.CarServiceDAO;
public class CarService {
    private CarServiceDAO carServiceDAO;

    public CarService() {
        this.carServiceDAO = new CarServiceDAO();
    }

    public void getAllServices() {
        carServiceDAO.getAllServices();
    }

    public void getAllServicesByCategory(String category) {
        carServiceDAO.getAllServicesByCategory(category);
    }

    public void getAllCategories() {
        carServiceDAO.getAllCategories();
    }
}
