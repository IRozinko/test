package fintech.db.impl;


import com.google.common.base.Preconditions;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import fintech.db.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.QueryDslJpaRepository;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Transactional(propagation = Propagation.REQUIRED)
public class BaseRepositoryImpl<T, ID extends Serializable> extends QueryDslJpaRepository<T, ID> implements BaseRepository<T, ID> {


    private final JpaEntityInformation<T, ID> entityInformation;

    public BaseRepositoryImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
    }

    public BaseRepositoryImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager, EntityPathResolver resolver) {
        super(entityInformation, entityManager, resolver);
        this.entityInformation = entityInformation;
    }

    @Override
    public T getRequired(ID id) {
        Preconditions.checkNotNull(id, "Null id");
        T entity = findOne(id);
        if (entity == null) {
            throw new EntityNotFoundException(String.format("Entity %s not found by id %s", entityInformation.getEntityName(), id));
        }
        return entity;
    }


    @Override
    public T lock(ID id) {
        return this.getRequired(id);
    }

    @Override
    public Optional<T> getOptional(ID id) {
        Preconditions.checkNotNull(id, "Null id");
        T entity = findOne(id);
        return Optional.ofNullable(entity);
    }

    @Override
    public Optional<T> getOptional(Predicate predicate) {
        T entity = findOne(predicate);
        return Optional.ofNullable(entity);
    }

    @Override
    public Optional<ID> getOptionalId(Predicate predicate) {
        return getOptional(predicate).map(entityInformation::getId);
    }

    @Override
    public Optional<T> findFirst(Predicate predicate, OrderSpecifier<?>... orders) {
        return findAll(predicate, new QPageRequest(0, 1, orders)).getContent()
            .stream()
            .findFirst();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void delete(ID id) {
        super.delete(id);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void delete(T entity) {
        super.delete(entity);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void delete(Iterable<? extends T> entities) {
        super.delete(entities);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void deleteInBatch(Iterable<T> entities) {
        super.deleteInBatch(entities);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void deleteAll() {
        super.deleteAll();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void deleteAllInBatch() {
        super.deleteAllInBatch();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public <S extends T> S save(S entity) {
        return super.save(entity);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public <S extends T> S saveAndFlush(S entity) {
        return super.saveAndFlush(entity);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public <S extends T> List<S> save(Iterable<S> entities) {
        return super.save(entities);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void flush() {
        super.flush();
    }

    @Override
    public T findOneOrNull(Predicate predicate) {
        return findOne(predicate);
    }
}
