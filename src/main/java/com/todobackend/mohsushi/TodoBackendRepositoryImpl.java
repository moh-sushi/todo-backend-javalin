package com.todobackend.mohsushi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

class TodoBackendRepositoryImpl implements TodoBackendRepository {
  private final Map<Long, TodoBackendEntry> inMemoryDb = new HashMap<>();
  private final AtomicLong counter = new AtomicLong(0);

  @Override
  public List<TodoBackendEntry> all() {
    return new ArrayList<>(inMemoryDb.values());
  }

  @Override
  public void deleteAll() {
    inMemoryDb.clear();
  }

  @Override
  public TodoBackendEntry create(TodoBackendEntry entry, String url) {
    entry.setId(counter.incrementAndGet());
    entry.setUrl(url);
    inMemoryDb.put(entry.getId(), entry);
    return entry;
  }

  @Override
  public TodoBackendEntry get(final long id) {
    return inMemoryDb.get(id);
  }

  @Override
  public TodoBackendEntry delete(final long id) {
    final TodoBackendEntry entry = get(id);
    if (entry != null) inMemoryDb.remove(id);
    return entry;
  }

  @Override
  public TodoBackendEntry update(final long id, final TodoBackendEntry entry) {
    final TodoBackendEntry entryFromDb = inMemoryDb.get(id);
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
    // update entry in db
    inMemoryDb.put(id, entryFromDb);

    return entryFromDb;
  }
}
