package service.auth;

import dao.rbac.RoleDao;

import java.util.Set;

public class AuthService {
    private final RoleDao roleDao;

    public AuthService(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public Set<String> getPermissionCodesOfUser(int userId) throws Exception{
        return roleDao.getPermissionCodesOfUser(userId);

    }

    public boolean hasPermission(int userId, String requiredPermissionCode) throws Exception{
       if(userId <= 0 || requiredPermissionCode == null || requiredPermissionCode.isEmpty()){
           return false;
       }

       Set<String> userPermissions = getPermissionCodesOfUser(userId);
       return userPermissions.contains(requiredPermissionCode);
    }
}
