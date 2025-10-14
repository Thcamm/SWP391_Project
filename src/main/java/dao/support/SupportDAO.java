package dao.support;

import common.DbContext;
import model.support.SupportFAQ;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SupportDAO extends DbContext {

    public List<SupportFAQ> getAllSupportFAQ() {
        String sql = "select * from supportFAQ";
        List<SupportFAQ> list = new ArrayList<>();
        try(PreparedStatement st = getConnection().prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                SupportFAQ supportFAQ = new SupportFAQ();
                supportFAQ.setFAQID(rs.getInt(""));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }


}
