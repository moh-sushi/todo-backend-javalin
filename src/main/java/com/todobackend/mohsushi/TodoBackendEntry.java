package com.todobackend.mohsushi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class TodoBackendEntry {

  @Id
  @GeneratedValue
  private Long id;
  private String url;
  private String title;
  @Column(name = "\"order\"")
  private Long order;
  private Boolean completed = Boolean.FALSE;

  public TodoBackendEntry() {
  }

  TodoBackendEntry(Long id, String url, String title, Long order, Boolean completed) {
    this.id = id;
    this.url = url;
    this.title = title;
    this.order = order;
    this.completed = completed;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUrl() {
//    if (id != null) {
//      return url + "/" + id;
//    }
//    return url;
    return url.endsWith("/") ? url + id : url + "/" + id;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Long getOrder() {
    return order;
  }

  public void setOrder(Long order) {
    this.order = order;
  }

  public Boolean getCompleted() {
    return completed;
  }

  public void setCompleted(Boolean completed) {
    this.completed = completed == null ? Boolean.FALSE : completed;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TodoBackendEntry that = (TodoBackendEntry) o;
    return Objects.equals(id, that.id)
        && Objects.equals(title, that.title)
        && Objects.equals(order, that.order)
        && Objects.equals(completed, that.completed);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, order, completed);
  }
}
