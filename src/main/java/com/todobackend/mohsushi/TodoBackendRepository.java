package com.todobackend.mohsushi;

import java.util.List;

public interface TodoBackendRepository {
  List<TodoBackendEntry> all();

  void deleteAll();

  TodoBackendEntry create(TodoBackendEntry entry, String url);

  TodoBackendEntry get(long id);

  TodoBackendEntry delete(long id);

  TodoBackendEntry update(long id, TodoBackendEntry entry);
}
