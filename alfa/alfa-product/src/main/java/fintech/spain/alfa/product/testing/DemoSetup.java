package fintech.spain.alfa.product.testing;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import fintech.ClasspathUtils;
import fintech.security.user.AddUserCommand;
import fintech.security.user.SaveRoleCommand;
import fintech.security.user.UserService;
import fintech.spain.alfa.product.crm.AddressCatalog;
import fintech.task.AgentService;
import fintech.task.command.AddAgentCommand;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.StringReader;

@Component
public class DemoSetup {

    private final UserService userService;
    private final AgentService agentService;
    private final AddressCatalog addressCatalog;

    @Autowired
    public DemoSetup(UserService userService, AgentService agentService, AddressCatalog addressCatalog) {
        this.userService = userService;
        this.agentService = agentService;
        this.addressCatalog = addressCatalog;
    }

    public void setUp() {
        boUsers();
        addresses();
    }

    private void boUsers() {
        if (!userService.findUserByEmail("admin").isPresent()) {
            userService.saveRole(new SaveRoleCommand().setName("ADMIN").setPermissions(ImmutableSet.of("ROLE_ADMIN")));
            userService.addUser(new AddUserCommand().setEmail("admin").setRoles(ImmutableSet.of("ADMIN")).setPassword("test").setTemporaryPassword(false));

            agentService.addAgent(new AddAgentCommand().setEmail("admin").setTaskTypes(ImmutableList.of("*")));
        }
    }

    @SneakyThrows
    private void addresses() {
        if (addressCatalog.count() > 0) {
            return;
        }
        CSVReader reader = new CSVReader(new StringReader(ClasspathUtils.resourceToString("demo/address-catalog.csv")));
        reader.readAll().forEach(a -> addressCatalog.saveAddress(a[0], a[1], a[2], a[3]));
    }
}
