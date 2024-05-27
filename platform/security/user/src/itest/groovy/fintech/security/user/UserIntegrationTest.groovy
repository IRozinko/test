package fintech.security.user

import org.springframework.beans.factory.annotation.Autowired

class UserIntegrationTest extends BaseSpecification {

    @Autowired
    UserService userService


    def "Add user"() {
        expect:
        !userService.findUserByEmail("test@mail.com").isPresent()

        when:
        def id = userService.addUser(new AddUserCommand(email: "test@mail.com", password: "test"))
        userService.addUser(new AddUserCommand(email: "test2@mail.com", password: "test"))

        then:
        userService.findUserByEmail("test@mail.com").get().id == id

        and: "Find by non-normalized email"
        userService.findUserByEmail(" Test@mail.com ").get().id == id
    }

    def "Remove user"() {
        given:
        userService.addUser(new AddUserCommand(email: "user@mail.com", password: "test"))

        when:
        userService.removeUser(new RemoveUserCommand(email: "user@mail.com"))

        then:
        !userService.findUserByEmail("user@mail.com").isPresent()
    }

    def "Add user with roles and permissions"() {
        given:
        userService.saveRole(new SaveRoleCommand(name: "ROLE_BASIC", permissions: ["READ"]))
        userService.saveRole(new SaveRoleCommand(name: "ROLE_ADMIN", permissions: ["READ", "WRITE"]))
        userService.saveRole(new SaveRoleCommand(name: "ROLE_ADMIN", permissions: ["READ", "WRITE", "ADMIN"]))

        when:
        userService.addUser(new AddUserCommand(email: "user@mail.com", password: "test"))
        userService.updateUser(new UpdateUserCommand(email: "user@mail.com", roles: ["ROLE_BASIC"]))

        then:
        with(userService.findUserByEmail("user@mail.com").get()) {
            roles == ["ROLE_BASIC"] as Set
            permissions == ["READ"] as Set
        }

        when:
        userService.updateUser(new UpdateUserCommand(email: "user@mail.com", roles: ["ROLE_BASIC", "ROLE_ADMIN"]))

        then:
        with(userService.findUserByEmail("user@mail.com").get()) {
            roles == ["ROLE_BASIC", "ROLE_ADMIN"] as Set
            permissions == ["READ", "WRITE", "ADMIN"] as Set
        }
    }

    def "Delete role"() {
        when:
        userService.saveRole(new SaveRoleCommand(name: "ROLE_BASIC", permissions: ["READ"]))
        userService.addUser(new AddUserCommand(email: "user@mail.com", roles: ["ROLE_BASIC"], password: "test"))

        then:
        userService.findUserByEmail("user@mail.com").get().roles == ["ROLE_BASIC"] as Set

        when:
        userService.deleteRole("ROLE_BASIC")

        then:
        userService.findUserByEmail("user@mail.com").get().roles == [] as Set
    }
}
