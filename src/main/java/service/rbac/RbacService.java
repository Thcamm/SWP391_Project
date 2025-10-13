package service.rbac;

import common.utils.PaginationUtils;
import dao.employee.admin.rbac.MenuDao;
import dao.employee.admin.rbac.PermissionDao;
import dao.employee.admin.rbac.RoleDao;
import model.pagination.PaginationResponse;
import model.employee.admin.rbac.MenuItem;
import model.employee.admin.rbac.Permission;
import model.employee.admin.rbac.Role;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class RbacService {
    private final RoleDao roleDao;
    private final PermissionDao permissionDao;
    private final MenuDao menuDao;


    public RbacService(RoleDao roleDao, PermissionDao permissionDao, MenuDao menuDao) {
        this.roleDao = roleDao;
        this.permissionDao = permissionDao;
        this.menuDao = menuDao;
    }

    public List<Role> getAllRoles() throws Exception {
        return roleDao.findAll();
    }

    public List<Permission> getAllPermissions(String kw, String cate) throws SQLException {
        return permissionDao.findAll(kw,cate);
    }

    public Set<Integer> getPermissionIdsOfRole(int roleId) throws SQLException{
        return permissionDao.getPermissionIdsOfRole(roleId);
    }

    public void assignPermissions(int roleId, List<Integer> permIds, int actorUserId) throws SQLException{
        permissionDao.replacePermissions(roleId, permIds, actorUserId);

    }

    public List<MenuItem> menuForRole(int roleId) throws SQLException{
        var pids = permissionDao.getPermissionIdsOfRole(roleId);
        return menuDao.byPerms(pids);
    }

    public void createRole(Role role) throws SQLException{
        Role existing = roleDao.findByName(role.getRoleName());
        if(existing != null){
            throw new IllegalArgumentException("Role name already exists");
        }

        roleDao.insert(role.getRoleName(), role.getDescription());
    }

    public void renameRole(int roleId, String newName) throws SQLException{
        if(newName == null || newName.isBlank()){
            throw new IllegalArgumentException("New name cannot be null or blank");
        }

        Role existing = roleDao.findByName(newName);
        if(existing != null){
            throw new IllegalArgumentException("Role name already exists");

        }

        Role roleChange = roleDao.findById(roleId);
        if(roleChange == null){
            throw new IllegalArgumentException("Role ID not found");
        }

        roleDao.update(roleChange);

    }

    public PaginationResponse<Permission> getPermissionsPaged(int page, int size, String kw, String cate) throws Exception {
        int totalItems = permissionDao.countAll(kw,cate);
        PaginationUtils.PaginationCalculation params = PaginationUtils.calculateParams(totalItems, page, size);

        List<Permission> perms = permissionDao.findPaginated(kw, cate, params.getOffset(), size);

        PaginationResponse<Permission> response = new PaginationResponse<>();
        response.setData(perms);
        response.setCurrentPage(params.getSafePage());
        response.setItemsPerPage(size);
        response.setTotalPages(params.getTotalPages());

        response.setTotalItems(totalItems);

        return response;
    }

}
