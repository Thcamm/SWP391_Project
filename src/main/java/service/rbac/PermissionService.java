package service.rbac;

import common.utils.NameValidator;
import dao.employee.admin.rbac.PermissionDao;
import model.employee.admin.rbac.Permission;

import java.sql.SQLException;
import java.util.Set;

public class PermissionService {
    private final PermissionDao dao;

    public PermissionService(PermissionDao dao) {
        this.dao = dao;
    }

    public Permission createPermission(String rawCode,String rawName, String category, String description) throws Exception {
        var codeResult = NameValidator.validatePermCode(
                rawCode,
                cd -> {
                    try{
                        return dao.exitsByCode(cd);
                    }catch (Exception e){
                        return true;
                    }
                }
        );

        if(!codeResult.valid){
            throw new IllegalArgumentException(String.join("; ", codeResult.errors));

        }

        var nameResult = NameValidator.validateDisplayName(
                rawName,
                nm ->{
                    try{
                        return dao.existsByNameIgnorecase(nm);
                    }catch (Exception e){
                        return true;
                    }
                },
                null
        );

        if(!nameResult.valid){
            throw new IllegalArgumentException(String.join("; ", nameResult.errors));

        }

        Permission p = new Permission();
        p.code = codeResult.normalizedValue;
        p.name = nameResult.normalizedValue;
        p.category = category != null ? category.trim().toUpperCase() : null;
        p.description = description != null ? description.trim() : null;
//        p.active = active;

        int id = dao.insert(p);
        p.permId = id;
        return p;
    }

    public Permission updatePermission(int id,
                                       String rawCode,
                                       String rawName,
                                       String category,
                                       String description
                                       ) throws Exception {
        if( id <= 0){
            throw new IllegalArgumentException("Invalid permission ID");

        }

        Permission existing = dao.findById(id);
        if( existing == null){
        throw new IllegalArgumentException("Permission ID not found" + id);
        }

        final Set<String> PROTECTED = Set.of(
                "role_permission_manage",
                "role-permission-manage"
        );
        String existingCodeNorm = (existing.code == null) ? null : existing.code.trim().toLowerCase();

        var codeResult = NameValidator.validatePermCode(
                rawCode,
                cd -> {
                    try{
                        return dao.existsByCodeExceptId(cd, id);
                    }catch (Exception e){
                        return true;
                    }
                }
        );

        if(!codeResult.valid){
            throw new IllegalArgumentException(String.join("; ", codeResult.errors));

        }

        if(PROTECTED.contains(existingCodeNorm)){
            if(!existingCodeNorm.equals(codeResult.normalizedValue)){
                throw new IllegalArgumentException("This permission is protected and its code cannot be changed: " + existing.code);
            }

//            if(!active){
//                throw new IllegalArgumentException("This permission is protected and cannot be deactivated: " + existing.code);
//            }
        }

        var nameResult = NameValidator.validateDisplayName(
                rawName,
                nm ->{
                    try {
                        return dao.existsByNameIgnorecaseExceptId(nm, id);
                    }catch (Exception e){
                        return true;
                    }
                },
                id
        );

        if(!nameResult.valid){
            throw new IllegalArgumentException(String.join("; ", nameResult.errors));

        }

        existing.code = codeResult.normalizedValue;
        existing.name = nameResult.normalizedValue;
        existing.category = category != null ? category.trim().toUpperCase() : null;
        existing.description = description != null ? description.trim() : null;
//        existing.active = active;

        dao.update(existing);
        return existing;

    }

    public Permission getById(int id) throws SQLException {
        if(id <= 0){
            throw new IllegalArgumentException("Invalid permission ID");
        }
        return dao.findById(id);
    }

    public boolean deletePermission(int id) throws SQLException {
        if(id <= 0){
            throw new IllegalArgumentException("Invalid permission ID");
        }

        Permission p = dao.findById(id);
        if(p == null){
            return false;


        }

        final Set<String> PROTECTED = Set.of(
                "role_permission_manage",
                "role-permission-manage"
        );

        String code = (p.code == null) ? null : p.code.trim().toLowerCase();

        if(PROTECTED.contains(code)){
            throw new IllegalArgumentException("This permission is protected and cannot be deleted: " + p.code);

        }


        return dao.delete(id);
    }

//    public PaginationResponse<Permission> getPermissionPaginated(int page, int size, String kw, String category) throws SQLException {
//        if(page < 1) page = 1;
//        if(size < 1) size = 10;
//        int
//    }
}
