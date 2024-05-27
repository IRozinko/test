package fintech.crm

import fintech.crm.bankaccount.AddClientBankAccountCommand
import fintech.crm.bankaccount.ClientBankAccountService
import fintech.crm.bankaccount.DuplicateBankAccountException
import fintech.crm.bankaccount.db.ClientBankAccountEntity
import fintech.crm.bankaccount.db.ClientBankAccountRepository
import fintech.crm.client.ClientService
import fintech.crm.client.db.ClientRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class ClientBankAccountServiceTest extends BaseSpecification {

    Long clientId

    Long secondClientId

    Long thirdClientId

    @Autowired
    ClientBankAccountService clientBankAccountService

    @Autowired
    ClientBankAccountRepository clientBankAccountRepository

    @Autowired
    ClientRepository clientRepository

    def setup() {
        clientId = createClient()
        secondClientId = createClient()
        thirdClientId = createClient()
    }

    def "Add client bank account"() {

        when:
        def bankAccountId = clientBankAccountService.addBankAccount(new AddClientBankAccountCommand(clientId: clientId, bankName: "Test bank", accountNumber: "LV21HABA12312", accountOwnerName: "John", numberOfTransactions: 11L, balance: -900.00))

        then:
        bankAccountId != null

        def clientBankAccount = clientBankAccountService.get(bankAccountId)

        with(clientBankAccount) {
            clientId == clientId
            bankName == "Test bank"
            accountNumber == "LV21HABA12312"
            accountOwnerName == "John"
            numberOfTransactions == 11L
            balance == -900.00g
        }

        and:
        clientBankAccountService.findPrimaryByClientId(clientId) == Optional.empty()

    }

    def "Make primary"() {
        given:
        def bankAccountId = clientBankAccountService.addBankAccount(new AddClientBankAccountCommand(clientId: clientId, bankName: "Test bank", accountNumber: "LV21HABA12312"))

        when:
        clientBankAccountService.makePrimary(bankAccountId)

        then:
        def primaryBankAccount = clientBankAccountService.findPrimaryByClientId(clientId)
        primaryBankAccount.isPresent()
        with(primaryBankAccount.get()) {
            bankName == "Test bank"
            accountNumber == "LV21HABA12312"
        }

        when:
        clientBankAccountService.deactivatePrimaryAccount(clientId)

        then:
        !clientBankAccountService.findPrimaryByClientId(clientId).isPresent()
    }

    def "There can't be same primary bank accounts"() {
        given:
        def id1 = clientBankAccountService.addBankAccount(new AddClientBankAccountCommand(clientId: clientId, bankName: "Test bank", accountNumber: "LV21HABA12312"));
        clientBankAccountService.makePrimary(id1)
        def id2 = clientBankAccountService.addBankAccount(new AddClientBankAccountCommand(clientId: secondClientId, bankName: "Test bank", accountNumber: "LV21HABA12312"));

        when:
        clientBankAccountService.makePrimary(id2)

        then:
        thrown(DuplicateBankAccountException.class)
    }

    def "Don't update existing bank account for user"() {
        when:
        clientBankAccountService.addBankAccount(new AddClientBankAccountCommand(clientId: clientId, bankName: "Test bank", accountNumber: "LV21HABA12312"))

        and:
        def existingBankAccountId = clientBankAccountService.addBankAccount(new AddClientBankAccountCommand(clientId: clientId, bankName: "Test bank", accountNumber: "LV21HABA12312"))

        then:
        def bankAccountEntity = clientBankAccountRepository.findOne(existingBankAccountId)
        assert bankAccountEntity.id == existingBankAccountId
        assert bankAccountEntity.bankName == "Test bank"
        assert bankAccountEntity.accountNumber == "LV21HABA12312"
        assert bankAccountEntity.client.id == clientId

    }

    def "Find bank account by accountNumber"() {
        given:
        def id1 = clientBankAccountService.addBankAccount(new AddClientBankAccountCommand(clientId: clientId, bankName: "Swedbank", accountNumber: "LV21HABA12345678"))
        def id2 = clientBankAccountService.addBankAccount(new AddClientBankAccountCommand(clientId: secondClientId, bankName: "Swedbank", accountNumber: "LV21HABA12345678"))

        expect:
        clientBankAccountService.findByAccountNumber(clientId, "LV21HABA12345678").get().id == id1
        clientBankAccountService.findByAccountNumber(secondClientId, "LV21HABA12345678").get().id == id2
        !clientBankAccountService.findByAccountNumber(secondClientId, "UNKOWN").isPresent()
    }

    @Transactional
    def "Add already existed account when client has multiplied accounts with the same number (issue with migrated clients)"() {
        given:
        def client = clientRepository.findOne(clientId)
        def bankAccounts = [
            new ClientBankAccountEntity(client: client, primary: true, bankName: 'Caixa', accountNumber: 'LV21HABA12312', accountOwnerName: 'Name'),
            new ClientBankAccountEntity(client: client, primary: false, bankName: 'Sabadell', accountNumber: 'LV21HABA12345678', accountOwnerName: 'Name'),
            new ClientBankAccountEntity(client: client, primary: false, bankName: 'Sabadell', accountNumber: 'LV21HABA12345678', accountOwnerName: 'Name')
        ]
        clientBankAccountRepository.save(bankAccounts)

        when:
        clientBankAccountService.addBankAccount(new AddClientBankAccountCommand(clientId: clientId, bankName: 'Sabadell', accountNumber: "LV21HABA12345678", primaryAccount: true))

        then:
        def primaryBankAccount = clientBankAccountService.findPrimaryByClientId(clientId)
        primaryBankAccount.isPresent()
        with(primaryBankAccount.get()) {
            bankName == "Sabadell"
            accountNumber == "LV21HABA12345678"
        }
        bankAccounts.any { it.id == primaryBankAccount.get().id }
    }


}
