package service.role;

import common.utils.NameValidator;
import common.utils.PaginationUtils;
import dao.employee.admin.rbac.RoleDao;
import model.pagination.PaginationResponse;
import model.employee.admin.rbac.Role;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class RoleService {
    private final RoleDao roleDao;
//    private final int actorUserId;

    public RoleService(RoleDao roleDao) {
        this.roleDao = roleDao;
//        this.actorUserId = actorUserId;
    }

    private static final Set<String> SYSTEM_ROLES = Set.of("ADMINISTRATOR", "SUPER_ADMIN", "ADMIN");

    public Role createRole(String rawName, String description) throws SQLException {
        String name = NameValidator.normalizeDisplayName(rawName);

        var vr = NameValidator.validateDisplayName(
                name,
                nm -> {
                    try {
                        return roleDao.existsByNameIgnorecase(nm);
                    } catch (SQLException e) {
                        return true;
                    }
                },
                null
        );

        if (!vr.valid) throw new IllegalArgumentException(String.join("; ", vr.errors));

        int id = roleDao.insert(vr.normalizedValue, description);
        Role r = new Role();
        r.setRoleId(id);
        r.setRoleName(vr.normalizedValue);
        r.setDescription(description);
        return r;

    }

    public Role renameRole(int roleId, String newName, String newDescription) throws SQLException {
        if (roleId <= 0) throw new IllegalArgumentException("Invalid role ID");

        Role current = roleDao.findById(roleId);
        if (current == null) throw new IllegalArgumentException("Invalid role ID");


        String normalized = NameValidator.normalizeDisplayName(newName);


        NameValidator.ValidationResult vr = NameValidator.validateDisplayName(
                normalized,
                nm -> {
                    try {
                        return roleDao.existsByNameIgnorecaseExceptId(nm, roleId);
                    } catch (SQLException e) {
                        return true;
                    }
                },
                current.getRoleId()
        );
        if (!vr.valid) throw new IllegalArgumentException(String.join("; ", vr.errors));

        boolean nameChanged = (current.getRoleName() == null)
                ? (vr.normalizedValue != null)
                : !current.getRoleName().equals(vr.normalizedValue);

        boolean descChanged = (current.getDescription() == null)
                ? (newDescription != null && !newDescription.isEmpty())
                : !current.getDescription().equals(newDescription);


        if (nameChanged || descChanged) {
            current.setRoleName(vr.normalizedValue);
            current.setDescription(newDescription);
            roleDao.update(current);
        }
        return current;
    }

    public Role getRoleById(int roleId) throws SQLException {
        if(roleId <= 0){
            throw new IllegalArgumentException("Invalid role ID");
        }

        return roleDao.findById(roleId);
    }

    public boolean deleteRole(int roleId) throws SQLException {
        Role target = roleDao.findById(roleId);
        if(target == null){
            return false;

        }

        String nameU = target.getRoleName() == null ? "" : target.getRoleName().trim().toUpperCase();
        if(SYSTEM_ROLES.contains(nameU)){
            throw new IllegalArgumentException("This is a procted system role and cannot be deleted");
        }

        int inUse = roleDao.countUsersByRole(roleId);
        if(inUse > 0){
            throw new IllegalArgumentException(
                    "Cannot delete role as it is assigned to " + inUse + " user(s). Remove/reassign users first.");

        }

        return roleDao.delete(roleId);
    }

    //page la trang mong muon bat dau tu 1
    // size la so luong item tren moi trang

    public PaginationResponse<Role> getRolePaginated(int page, int size, String keyword) throws Exception {
        if(page < 1) page = 1;
        if(size < 1) size = 10;

        int toltalItems;
        List<Role> roles;

        if(keyword != null && !keyword.trim().isEmpty()){
            keyword = keyword.trim();
            toltalItems = roleDao.countByName(keyword);
            PaginationUtils.PaginationCalculation params = PaginationUtils.calculateParams(toltalItems, page, size);
            roles = roleDao.findByName(keyword, params.getOffset(), size);

        }else {
            toltalItems = roleDao.countAll();
            PaginationUtils.PaginationCalculation params = PaginationUtils.calculateParams(toltalItems, page, size);
            roles = roleDao.findPaginated(params.getOffset(), size);
        }



        for (Role role : roles) {
            int count = roleDao.countUsersByRole(role.getRoleId());
            role.setUserCount(count);
        }
        PaginationResponse<Role> response = new PaginationResponse<>();
        response.setData(roles);
        response.setCurrentPage(page);
        response.setTotalPages((int) Math.ceil((double) toltalItems/ (double) size));
        response.setTotalItems(toltalItems);
        response.setItemsPerPage(size);

        return response;

    }



}
