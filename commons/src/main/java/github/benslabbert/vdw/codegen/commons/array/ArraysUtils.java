/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.commons.array;

public final class ArraysUtils {

  private ArraysUtils() {}

  public static Object[] merge(Object[] a1, Object[] a2) {
    if (0 == a1.length && 0 == a2.length) {
      return a1;
    }
    if (0 == a1.length) {
      return a2;
    }
    if (0 == a2.length) {
      return a1;
    }

    Object[] merged = new Object[a1.length + a2.length];
    System.arraycopy(a1, 0, merged, 0, a1.length);
    System.arraycopy(a2, 0, merged, a1.length, a2.length);

    return merged;
  }
}
