package it.damore.lucene.example;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GenericPermutations<T> {

  @SuppressWarnings("unchecked")
  public T[] createArray(T[] a, int size)
  {
    return (T[]) Array.newInstance(a.getClass()
                                    .getComponentType(),
                                   size);
  }

  public T[] concatenate(T[] a, T[] b)
  {
    int aLen = a.length;
    int bLen = b.length;

    T[] c = createArray(a, aLen + bLen);
    System.arraycopy(a, 0, c, 0, aLen);
    System.arraycopy(b, 0, c, aLen, bLen);

    return c;
  }

  // print N! permutation of the characters of the string s (in order)
  public List<T[]> perm1(T[] s)
  {
    List<T[]> list = new ArrayList<>();
    T[] prefix = createArray(s, 0);
    perm1(list, prefix, s);
    return list;
  }

  private void perm1(List<T[]> list, T[] prefix, T[] s)
  {
    int N = s.length;
    if (N == 0) {
      // System.out.println(Arrays.asList(prefix).stream().map(String::valueOf).collect(Collectors.joining()));
      list.add(prefix);
    } else {
      for (int i = 0; i < N; i++) {
        T[] element = createArray(prefix, 1);
        element[0] = s[i];
        T[] ss = createArray(s, i);
        System.arraycopy(s, 0, ss, 0, i);

        T[] ll = createArray(s, N - (i + 1));
        System.arraycopy(s, i + 1, ll, 0, N - (i + 1));
        perm1(list, concatenate(element, prefix), concatenate(ss, ll));
      }
    }

  }

  // print N! permutation of the elements of array a (not in order)
  public List<T[]> perm2(T[] s)
  {
    List<T[]> list = new ArrayList<>();
    perm2(list, s, s.length);
    return list;
  }

  private void perm2(List<T[]> list, T[] a, int n)
  {
    if (n == 1) {
      list.add(a.clone());
      return;
    }
    for (int i = 0; i < n; i++) {
      swap(a, i, n - 1);
      perm2(list, a, n - 1);
      swap(a, i, n - 1);
    }
  }

  // swap the objects at indices i and j
  private void swap(T[] a, int i, int j)
  {
    T c = a[i];
    a[i] = a[j];
    a[j] = c;
  }


  public static void main(String[] args)
  {
    int n = 5;
    if (args.length > 0) {
      n = Integer.parseInt(args[0]);
    }
    GenericPermutations<String> p = new GenericPermutations<>();
    String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    List<String> collect = alphabet.chars()
                                   .limit(n)
                                   .mapToObj(c -> Character.toString((char) c))
                                   .collect(Collectors.toList());
    String[] elements = collect.toArray(new String[0]);
    List<String[]> list = p.perm1(elements);
    System.out.println(list.size());
    list.stream()
        .map(Arrays::asList)
        .forEach(System.out::println);

    System.out.println("====");
    List<String[]> list2 = p.perm2(elements);
    System.out.println(list2.size());
    list2.stream()
         .map(Arrays::asList)
         .forEach(System.out::println);
  }
}
