package com.todobackend.mohsushi;

import io.javalin.http.Context;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class TodoBackendRepositoryHibernateImpl implements TodoBackendRepository {

  private final Session session;

  public TodoBackendRepositoryHibernateImpl(final Context context) {
    this.session = HibernateUtil.session(context);
  }

  @Override
  public List<TodoBackendEntry> all() {
    CriteriaBuilder cb = session.getCriteriaBuilder();
    CriteriaQuery<TodoBackendEntry> cq = cb.createQuery(TodoBackendEntry.class);
    Root<TodoBackendEntry> rootEntry = cq.from(TodoBackendEntry.class);
    CriteriaQuery<TodoBackendEntry> all = cq.select(rootEntry);

    TypedQuery<TodoBackendEntry> allQuery = session.createQuery(all);
    return allQuery.getResultList();
  }

  @Override
  public void deleteAll() {
    session.createQuery(String.format("delete from %s", TodoBackendEntry.class.getSimpleName())).executeUpdate();
  }

  @Override
  public TodoBackendEntry create(TodoBackendEntry entry, String url) {
    entry.setUrl(url);
    session.save(entry);
    boolean x = true;
    if (x) throw new RuntimeException();
    return entry;
  }

  @Override
  public TodoBackendEntry get(long id) {
    return session.get(TodoBackendEntry.class, id);
  }

  @Override
  public TodoBackendEntry delete(long id) {
    final TodoBackendEntry entry = get(id);
    if (entry == null) return null;
    session.remove(entry);
    return entry;
  }

  @Override
  public TodoBackendEntry update(long id, TodoBackendEntry entry) {
    final TodoBackendEntry entryFromDb = get(id);
    if (entryFromDb == null) return null;
    // updates can be executed
    if (entry.getTitle() != null) {
      entryFromDb.setTitle(entry.getTitle());
    }
    if (entry.getCompleted() != null) {
      entryFromDb.setCompleted(entry.getCompleted());
    }
    if (entry.getOrder() != null) {
      entryFromDb.setOrder(entry.getOrder());
    }
    session.update(entryFromDb);

    return entryFromDb;
  }
}
