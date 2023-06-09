package com.tapioca.quote.manager.service;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import com.tapioca.quote.manager.model.Quote;

@Transactional
@ApplicationScoped
public class QuoteService {

	@PersistenceContext
	private EntityManager entityManager;

	public Optional<Quote> findByName(final String name) {
		return entityManager.createQuery("SELECT q FROM Quote q WHERE q.name = :name", Quote.class)
				.setParameter("name", name)
				.getResultStream()
				.findFirst();
	}

	public Optional<Quote> findById(final long id) {
		return Optional.ofNullable(entityManager.find(Quote.class, id));
	}

	public long countAll() {
		return entityManager.createQuery("SELECT COUNT(q) FROM Quote q", Number.class)
				.getSingleResult()
				.longValue();
	}

	public Stream<Quote> findAll(final int from, final int to) {
		return entityManager.createQuery("SELECT q FROM Quote q", Quote.class)
				.setFirstResult(from)
				.setMaxResults(to - from)
				.getResultStream();
	}

	public Quote create(final Quote newQuote) {
		entityManager.persist(newQuote);
		entityManager.flush();
		return newQuote;
	}

	public Quote delete(final Quote quote) {
		entityManager.remove(quote);
		entityManager.flush();
		return quote;
	}

	public <T> T mutate(final String symbol, final Function<Optional<Quote>, T> callback) {
		return callback.apply(entityManager.createQuery("select q from Quote q where q.name = :name", Quote.class)
				.setParameter("name", symbol)
				.getResultStream()
				.findFirst());
	}
}
