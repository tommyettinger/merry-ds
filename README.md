# merry-ds
Alternative data structures to libGDX's ObjectMap, ObjectSet, and relatives

This code is intended to be part of a pull request to libGDX, but to remain available here for older versions
of libGDX to use.

The data structures here fix [a long-standing bug in libGDX that was initially underestimated
in severity](https://github.com/libgdx/libgdx/issues/2903). The issue from 5 years ago used random items
in an ObjectSet to find unintentional collisions, when the worst case (not discovered then) is when a
malicious user generates 50 or so keys with intentionally-colliding `hashCode()` results, and inserts
them through normal usage to crash any app with an `ObjectSet<String>` or `ObjectMap<String>` (among other
vulnerable key types) by throwing an un-catchable `OutOfMemoryError`. The random case may never crash, may
take 20000 random items to trigger a crash, or may get unlucky and crash early. The malicious case only
needs between 40 and 50 items, depending on how many `hashCode()`s are produced (1 or 2 possible hashCodes
will crash any application with less than 8GB of heap given to the JVM using 49 items). There's another
problematic case as well; ObjectSet and ObjectMap are designed to be memory-efficient, but large collections
without even a single full collision can still use over 60 times as much memory as the optimal case due to
unnecessary capacity doubling, which can happen if the `hashCode()` is not expertly-constructed.
Constructing optimal `hashCode()`s is hard, and is should in most cases be a waste of time because it should
be the data structure's responsibility. The issue response blames the problem in part on bad `hashCode()`s,
a claim that deserves scrutiny.

Most libGDX users are, contrary to what Nathan Sweet seems to assume in the issue response, not able to
or willing to judge what makes a "good" `hashCode()` implementation, as ObjectMap would require. This
includes libGDX's original author, Mario Zechner, who acknowledged that a `hashCode()` for Vector2 is a
"[fantastically stupid idea](https://github.com/libgdx/libgdx/commit/5899f8465d9c98c8a3a035358247f6a08ff735c1)"
and proceeded to implement it in a straightforward way, how most Java programmers would and should
implement `hashCode()`: automatically, using an IDE's generator. The problem is not simple, and most
programmers without extensive low-level knowledge should not be expected to notice it, or to understand the
following explanation, so this is mostly here to show why "good `hashCode()`s" are not something you can
just wish into existing. Vector2 has two `float` values, and a `hashCode()` returns an `int`, so you need
to call `Float.floatToIntBits()` to get a correct mapping of floating point to integer. This becomes an
issue if either `float` stores a small integer or a rational number that is cleanly represented by a
small power-of-two denominator (like `1.5`, covering many practical uses of Vector2), which would cause
`Float.floatToIntBits()` to return an `int` with mostly `0` bits in its least-significant section. This
last tidbit, that the floats often don't change the least-significant bits from 0, cascades through the
`hashCode()` implementation, and makes most results for the types of float coordinates mentioned above
have mostly 0 in this key area of the bits. `ObjectSet` and all related classes that use `hashCode()`
in libGDX will prefer using the least-significant bits of a hash code before they even look at the
more-significant ones. This is a common implementation detail that restricts hash codes to refer only to
indices that fit in the capacity, and can be understood as masking the bits of the hash code that aren't used,
but not changing those bits so they can be used later if the capacity increases. When the least-significant
bits of hash codes are all or mostly the same, collisions in the used area of the hash code are much more
frequent, and this manifests in much higher memory usage as the capacity expands to see more of each hash code,
or in extreme cases, the application crashes with an `OutOfMemoryError`. It takes a relatively small grid of
`Vector2` values storing integer positions to crash `ObjectSet`; a 24x24 grid roughly centered on `0,0` will
run out of heap quickly. It is possible to write a good implementation of `hashCode()` for `Vector2`. I have
done it. It required far more effort than a user of libGDX should ever have to expend on a function such as
this, is slower than the current implementation (when not used in an `ObjectSet`), and once a version was
finally found that worked, the best algorithm turned out to be essentially an inferior version of Fibonacci
hashing (code below, and Fibonacci hashing's better version comes up again). The data structures can do better.

Merry is an alternative to the hash-based data structures in libGDX, consisting of:
  * ObjectMap, for Object keys mapped to Object values
  * ObjectSet, for Objects items that are unique
  * OrderedMap, for Object keys mapped to Object values that keep their insertion order
  * OrderedSet, for Object items that are unique and keep their insertion order
  * IdentityMap, for Object keys that are compared by reference equality mapped to Object values
  * ObjectFloatMap, for Object keys mapped to primitive float values
  * ObjectIntMap, for Object keys mapped to primitive int values
  * IntMap, for primitive int keys mapped to Object values
  * IntSet, for primitive int items that are unique
  * LongMap, for primitive long keys mapped to Object values
  * IntFloatMap, for primitive int keys mapped to primitive float values
  * IntIntMap, for primitive int keys mapped to primitive int values
 
All of these have the same API as in libGDX, with the exception of OrderedMap and OrderedSet, which add
the useful alter() and alterIndex() methods to change a key without changing its value or ordering, and some
protected methods across several classes that can be overridden by user-written child classes. The protected
methods and fields have JavaDocs, but typically would only be overridden in very specific use cases or by
this library (that's how `IdentityMap` is implemented).

The important change here is that these move away from libGDX's vulnerable internal algorithm, cuckoo hashing
with a stash, and change to a much older, well-studied algorithm, linear probing. The one novel difference is
that Merry also uses Fibonacci hashing to improve "bad `hashCode()`s", which can be an issue with linear probing.
[This blog post on Fibonacci hashing](https://probablydance.com/2018/06/16/fibonacci-hashing-the-optimization-that-the-world-forgot-or-a-better-alternative-to-integer-modulo/)
covers it in extensive detail; some of the fastest C++ implementations of hash tables use it. While Fibonacci
hashing helps improve the distribution of hash codes, linear probing ensures that even if all keys collide, the
map or set will still function, just somewhat more slowly. The lines of code are fewer than before, not counting
comments, and there are fewer methods because there's no stash to complicate matters. The performance results
are mixed, and Merry's performance is generally close to libGDX, but behind it in its best case. There is a
key outlier with removal, where libGDX ObjectSet struggles and Merry runs at twice the speed. When libGDX is
getting less-than-optimal `hashCode()`s, Merry quickly takes over in both better memory usage and speed.
[There's a broad comparison of various libraries' time costs per operation here](https://tommyettinger.github.io/assorted-benchmarks/index.html).
This is raw data and not formatted as nicely, but [memory comparisons are here](https://github.com/tommyettinger/assorted-benchmarks/blob/master/jmh/memory_results_raw.txt);
it's clear especially near the bottom of the file that the numbers for memory used are **much bigger** for
libGDX than they should be for a low-memory-usage class. It is also clear that Merry is consistently within a
few bytes of the best position, when it isn't already the best; its only competitor past 10 items is Koloboke,
which sometimes needs twice the capacity compared to Merry.

Usage
-----

See Maven Central, which despite the name, has Gradle instructions as well as Maven.
The first stable release should be [up here shortly](https://search.maven.org/search?q=com.github.tommyettinger).
You may need to change their recommended `implementation` keyword to `api`.

Footnote
-------

I mentioned an improved `hashCode()` for `Vector2`, so here it is:
```java
public int hashCode() {
    return (int)((NumberUtils.floatToIntBits(x) * 0xC13FA9A902A6328FL
                + NumberUtils.floatToIntBits(y) * 0x91E10DA5C79E7B1DL)
            >>> 32);
}
```

This is very similar to the Fibonacci hashing method that all of Merry's classes have as the
protected method `place(Object)`, just with two different multipliers and (this is key) the
`hashCode()` always shifts right by 32, while `place()` adapts how much it shifts over based on
the capacity of the data structure. The rest of this is extremely mathematical, and I'm not
exactly a math professor, so I'll explain fast and loose and hope nobody actually reads this
far. The choice of multiplier probably has a lot of wiggle room, but `place()` uses 2 to the
64 divided by the golden ratio, which appears to be optimal for this usage, and the improved
`Vector2.hashCode()` uses the 2 to the 64 divided by the "plastic constant," which is an
irrational number roughly equal to `1.32471795724474602596090885447809...` that is similar
to the golden ratio, and for the second multiplier divides again by the plastic constant,
rounding up to the nearest odd number. The problem with this method is that for more than a few
variables that all need independent contribution to a `hashCode()`, you get a lot of constants
very quickly, and though they can be precalculated if you know how many there are (
[I did this here](https://github.com/SquidPony/SquidLib/blob/master/squidlib-util/src/main/java/squidpony/squidmath/MummyNoise.java#L27-L313)),
even with 3 variables the quality starts to suffer. Using only one multiplier, the golden
ratio, works well because it can be called the "most irrational number," or the furthest from
a concise rational approximation, and so it is the most likely to separate the inputs it is
given across the capacity. The plastic constant is very close to holding that title itself.
Fibonacci hashing depends on the adaptive shift mentioned earlier to be its best, because it
takes a 32-bit `hashCode()` and multiplies it by a 64-bit constant (discarding overflow), which
makes the lowest bit of the hash code affect potentially all bits of the 64-bit result, makes
the highest bit affect the highest 32 bits of the result, and makes all other bits affect some
range in between. Shifting over by 32 to get a 32-bit int is OK, but the lowest bits of that int
won't be as well-distributed as they could be if a smaller number is all that is desired.
Shifting the highest bit into the place of the highest *used* bit of the capacity ensures that
the best qualities of the golden ratio are preserved. Shifting over to meet the exact needs of
the capacity also avoids a masking step. 
