package fintech.security.user.db;

public class Entities {

    public static final String SCHEMA = "security";

    public static QUserEntity user = QUserEntity.userEntity;
    public static QPermissionEntity permission = QPermissionEntity.permissionEntity;
    public static QRoleEntity role = QRoleEntity.roleEntity;
}
