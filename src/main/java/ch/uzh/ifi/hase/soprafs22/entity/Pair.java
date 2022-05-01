package ch.uzh.ifi.hase.soprafs22.entity;

import java.util.Objects;

/**
 * Generic Class which holds any two objects. Once constructed, Pair is immutable in the sense that one can not replace
 * the objects that it is holding. However, the objects themselves are not immutable, which is what we need.
 * @param <S>: Generic Object: E.g.: A User
 * @param <T>: Generic Object: E.g.: A User
 */
public class Pair<S, T> {

  private final S obj1;
  private final T obj2;

  public Pair(S obj1, T obj2){
    this.obj1 = obj1;
    this.obj2 = obj2;
  }

  public S getObj1() {
    return obj1;
  }

  public T getObj2() {
    return obj2;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Pair<?, ?> pair = (Pair<?, ?>) o;
    return obj1.equals(pair.obj1) && obj2.equals(pair.obj2) || obj2.equals(pair.obj1) && obj1.equals(pair.obj2);
  }

  @Override
  public int hashCode() {
    return Objects.hash(obj1, obj2);
  }

  @Override
  public String toString() {
    return "Pair{" +
            "obj1=" + obj1 +
            ", obj2=" + obj2 +
            '}';
  }

}
