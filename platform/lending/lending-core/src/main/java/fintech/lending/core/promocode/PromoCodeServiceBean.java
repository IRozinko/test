package fintech.lending.core.promocode;

import fintech.DateUtils;
import fintech.TimeMachine;
import fintech.crm.client.db.ClientEntity;
import fintech.crm.client.db.ClientRepository;
import fintech.lending.core.application.LoanApplicationQuery;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.loan.LoanStatusDetail;
import fintech.lending.core.loan.db.LoanRepository;
import fintech.lending.core.promocode.db.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fintech.crm.db.Entities.client;
import static fintech.lending.core.db.Entities.loan;
import static fintech.lending.core.db.Entities.promoCode;
import static org.apache.commons.lang3.Validate.exclusiveBetween;
import static org.apache.commons.lang3.Validate.isTrue;

@Slf4j
@Component
@Transactional
public class PromoCodeServiceBean implements PromoCodeService {

    @Autowired
    private PromoCodeRepository promoCodeRepository;

    @Autowired
    private PromoCodeClientRepository promoCodeClientRepository;

    @Autowired
    private PromoCodeSourceRepository promoCodeSourceRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanApplicationService loanApplicationService;

    @Override
    public Long create(CreatePromoCodeCommand command) {
        promoCodeRepository.findFirst(promoCode.code.equalsIgnoreCase(command.getCode()), promoCode.id.asc())
            .ifPresent(pc -> {
                throw new IllegalArgumentException("Promo code already exists");
            });
        isTrue(command.getEffectiveFrom().isEqual(command.getEffectiveTo())
            || command.getEffectiveFrom().isBefore(command.getEffectiveTo()), "Invalid effective date range");
        exclusiveBetween(BigDecimal.ZERO, BigDecimal.valueOf(100.001), command.getRateInPercent(), "Invalid discount rate");
        isTrue(command.getMaxTimesToApply() > 0, "Invalid maximum times to apply");
        if (DateUtils.lt(command.getEffectiveTo(), LocalDate.now())) {
            throw new IllegalArgumentException("The promo code can not be created with the 'Effective to' date to any date in the past");
        }
        boolean newClientsOnly = command.getClientNumbers() == null || command.getClientNumbers().isEmpty();

        PromoCodeEntity promoCodeEntity = new PromoCodeEntity()
            .setCode(command.getCode())
            .setDescription(command.getDescription())
            .setEffectiveFrom(command.getEffectiveFrom())
            .setEffectiveTo(command.getEffectiveTo())
            .setRateInPercent(command.getRateInPercent())
            .setMaxTimesToApply(command.getMaxTimesToApply())
            .setActive(false)
            .setNewClientsOnly(newClientsOnly);

        PromoCodeEntity promoCode = promoCodeRepository.save(promoCodeEntity);

        updateSources(promoCode.getId(), command.getSources());

        if (!newClientsOnly) {
            validateClientNumbers(command.getClientNumbers());
            updateClientNumbers(promoCode.getId(), command.getClientNumbers());
        }

        return promoCode.getId();
    }

    @Override
    public void update(UpdatePromoCodeCommand command) {
        isTrue(command.getEffectiveFrom().isEqual(command.getEffectiveTo())
            || command.getEffectiveFrom().isBefore(command.getEffectiveTo()), "Invalid effective date range");
        exclusiveBetween(BigDecimal.ZERO, BigDecimal.valueOf(100.001), command.getRateInPercent(), "Invalid discount rate");
        isTrue(command.getMaxTimesToApply() > 0, "Invalid maximum times to apply");
        if (DateUtils.lt(command.getEffectiveTo(), LocalDate.now())) {
            throw new IllegalArgumentException("The promo code can not be created with the 'Effective to' date to any date in the past");
        }
        PromoCodeEntity promoCodeEntity = promoCodeRepository.getRequired(command.getPromoCodeId());
        promoCodeEntity.setDescription(command.getDescription());
        promoCodeEntity.setEffectiveFrom(command.getEffectiveFrom());
        promoCodeEntity.setEffectiveTo(command.getEffectiveTo());
        promoCodeEntity.setRateInPercent(command.getRateInPercent());
        promoCodeEntity.setMaxTimesToApply(command.getMaxTimesToApply());

        updateSources(promoCodeEntity.getId(), command.getSources());
        if (command.getClientNumbers() != null) {
            promoCodeEntity.setNewClientsOnly(false);
            updateClients(promoCodeEntity.getId(), command.getClientNumbers());
        }
        promoCodeRepository.save(promoCodeEntity);
    }

    @Override
    public void updateClients(Long promoCodeId, Collection<String> clientNumbers) {
        PromoCodeEntity promoCodeEntity = promoCodeRepository.getRequired(promoCodeId);

        if (Boolean.TRUE.equals(promoCodeEntity.isNewClientsOnly())) {
            throw new IllegalArgumentException("Cannot update clients list - promo code is for new clients only");
        } else {
            validateClientNumbers(clientNumbers);
            updateClientNumbers(promoCodeId, clientNumbers);
        }
    }

    private void updateSources(Long promoCodeId, Collection<String> sources) {
        Long id = promoCodeRepository.getRequired(promoCodeId).getId();
        promoCodeSourceRepository.deleteAllByPromoCodeId(id);
        if (sources != null) {
            sources.forEach(s -> {
                promoCodeSourceRepository.save(
                    new PromoCodeSourceEntity()
                        .setPromoCodeId(id)
                        .setSource(s));
            });
        }
    }

    @Override
    public void activate(Long promoCodeId) {
        PromoCodeEntity promoCodeEntity = promoCodeRepository.getRequired(promoCodeId);
        if (promoCodeEntity.getEffectiveTo().isBefore(TimeMachine.today())) {
            throw new IllegalArgumentException("Cannot activate promo code: Effective to date is in past");
        }
        promoCodeEntity.setActive(true);
        promoCodeRepository.save(promoCodeEntity);
    }

    @Override
    public void deactivate(Long promoCodeId) {
        PromoCodeEntity promoCodeEntity = promoCodeRepository.getRequired(promoCodeId);
        promoCodeEntity.setActive(false);
        promoCodeRepository.save(promoCodeEntity);
    }

    private void validateClientNumbers(Collection<String> clientNumbers) {
        List<String> clients = clientRepository.findAll(client.number.in(clientNumbers)).stream()
            .map(ClientEntity::getNumber)
            .collect(Collectors.toList());

        if (clients.size() != clientNumbers.size()) {
            String missingClients = clientNumbers.stream()
                .filter(id -> !clients.contains(id))
                .collect(Collectors.joining(", "));
            throw new RuntimeException("Unknown clients: " + missingClients);
        }
    }

    private void updateClientNumbers(Long promoCodeId, Collection<String> clientNumbers) {
        promoCodeClientRepository.deleteByPromoCodeId(promoCodeId);
        promoCodeClientRepository.save(clientNumbers.stream()
            .map(client -> new PromoCodeClientEntity()
                .setClientNumber(client)
                .setPromoCodeId(promoCodeId))
            .collect(Collectors.toList()));
    }

    @Override
    public Optional<PromoCodeOffer> getPromoCodeOffer(String promoCode, Long clientId) {
        return getPromoCodeOffer(promoCode, clientId, null);
    }

    @Override
    public Optional<PromoCodeOffer> getPromoCodeOffer(String pc, Long clientId, String source) {
        if (pc == null || pc.isEmpty()) {
            return Optional.empty();
        }

        LocalDate today = TimeMachine.today();
        PromoCodeEntity pce = promoCodeRepository.findOne(promoCode.code.equalsIgnoreCase(pc)
            .and(promoCode.active.isTrue())
            .and(promoCode.effectiveFrom.loe(today))
            .and(promoCode.effectiveTo.goe(today)));

        if (pce != null && !promoCodeIsUsedUp(pce.getId(), pce.getMaxTimesToApply())) {

            if (!sourceIsValid(pce.getId(), source)) {
                return Optional.empty();
            }

            boolean repeatingClient = clientId != null && !loanApplicationService.find(LoanApplicationQuery.byClientId(clientId)).isEmpty();

            if (pce.isNewClientsOnly() && !repeatingClient) {
                return Optional.of(new PromoCodeOffer()
                    .setPromoCodeId(pce.getId())
                    .setPromoCode(pce.getCode())
                    .setDiscountInPercent(pce.getRateInPercent()));

            } else if (!pce.isNewClientsOnly() && repeatingClient) {
                String clientNumber = clientRepository.getRequired(clientId).getNumber();
                if (promoCodeClientRepository.existsByClientNumberAndPromoCodeId(clientNumber, pce.getId())) {
                    return Optional.of(new PromoCodeOffer()
                        .setPromoCodeId(pce.getId())
                        .setPromoCode(pce.getCode())
                        .setDiscountInPercent(pce.getRateInPercent()));
                }
            }
        }
        log.warn("Client with id [{}] was trying to apply invalid promo code '{}'", clientId, pc);
        return Optional.empty();
    }

    @Override
    public void delete(Long promoCodeId) {
        PromoCodeEntity promoCodeEntity = promoCodeRepository.getRequired(promoCodeId);

        if (promoCodeEntity.isActive()) {
            throw new IllegalArgumentException("Cannot delete active promo code");
        }

        if (countTimesUsed(promoCodeId) == 0) {
            deletePromoCodeClients(promoCodeId);
            promoCodeRepository.delete(promoCodeEntity);
        } else {
            throw new IllegalArgumentException("Cannot delete promo code because it is in use");
        }
    }

    private void deletePromoCodeClients(Long promoCodeId) {
        promoCodeClientRepository.deleteByPromoCodeId(promoCodeId);
    }

    private boolean sourceIsValid(Long promoCodeId, String affiliateName) {
        List<String> validSources = promoCodeSourceRepository.getAllByPromoCodeId(promoCodeId).stream()
            .map(PromoCodeSourceEntity::getSource)
            .collect(Collectors.toList());
        return validSources.isEmpty() || validSources.contains(affiliateName);
    }

    private boolean promoCodeIsUsedUp(Long promoCodeId, Long maxTimesToApply) {
        return countTimesUsed(promoCodeId) >= maxTimesToApply;
    }

    private long countTimesUsed(Long promoCodeId) {
        return loanRepository.count(loan.promoCodeId.eq(promoCodeId).and(loan.statusDetail.ne(LoanStatusDetail.VOIDED)));
    }

    public PromoCodeOffer getRequired(Long promoCodeId) {
        PromoCodeEntity entity = promoCodeRepository.getRequired(promoCodeId);
        return new PromoCodeOffer()
            .setPromoCodeId(entity.getId())
            .setPromoCode(entity.getCode())
            .setDiscountInPercent(entity.getRateInPercent());
    }

    @Override
    public PromoCode getRequiredEntity(Long promoCodeId) {
        return promoCodeRepository.getRequired(promoCodeId).toValueObject();
    }

    // Every day at 1:00
    @Scheduled(cron = "0 0 1 * * *")
    public void expirePromoCodes() {
        List<PromoCodeEntity> expiredPromoCodes = promoCodeRepository.findAll(promoCode.active.isTrue()
            .and(promoCode.effectiveTo.before(TimeMachine.today())));

        if (!expiredPromoCodes.isEmpty()) {
            expiredPromoCodes.forEach(promoCode -> deactivate(promoCode.getId()));

            log.info("Deactivated expired Promo code(s) {}", expiredPromoCodes.stream()
                .map(PromoCodeEntity::getCode)
                .collect(Collectors.joining(", ")));
        }
    }

}
