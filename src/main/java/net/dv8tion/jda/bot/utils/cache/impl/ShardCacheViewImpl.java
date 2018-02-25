/*
 *     Copyright 2015-2018 Austin Keener & Michael Ritter & Florian Spieß
 *     Copyright 2018-2018 "Princess" Lana Samson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.dv8tion.jda.bot.utils.cache.impl;

import gnu.trove.impl.sync.TSynchronizedIntObjectMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.dv8tion.jda.bot.utils.cache.ShardCacheView;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.utils.Checks;
import net.dv8tion.jda.core.utils.cache.CacheView;

public class ShardCacheViewImpl implements ShardCacheView {
  protected final TIntObjectMap<JDA> elements;

  public ShardCacheViewImpl() {
    this.elements = new TSynchronizedIntObjectMap<>(new TIntObjectHashMap<JDA>(), new Object());
  }

  public ShardCacheViewImpl(int initialCapacity) {
    this.elements =
        new TSynchronizedIntObjectMap<>(new TIntObjectHashMap<JDA>(initialCapacity), new Object());
  }

  public void clear() {
    elements.clear();
  }

  public TIntObjectMap<JDA> getMap() {
    return elements;
  }

  @Override
  public List<JDA> asList() {
    return Collections.unmodifiableList(new ArrayList<>(elements.valueCollection()));
  }

  @Override
  public Set<JDA> asSet() {
    return Collections.unmodifiableSet(new HashSet<>(elements.valueCollection()));
  }

  @Override
  public long size() {
    return elements.size();
  }

  @Override
  public boolean isEmpty() {
    return elements.isEmpty();
  }

  @Override
  public List<JDA> getElementsByName(String name, boolean ignoreCase) {
    Checks.notEmpty(name, "Name");
    if (elements.isEmpty()) return Collections.emptyList();

    List<JDA> list = new LinkedList<>();
    for (JDA elem : elements.valueCollection()) {
      String elementName = elem.getShardInfo().getShardString();
      if (elementName != null) {
        if (ignoreCase) {
          if (elementName.equalsIgnoreCase(name)) list.add(elem);
        } else {
          if (elementName.equals(name)) list.add(elem);
        }
      }
    }

    return list;
  }

  @Override
  public Stream<JDA> stream() {
    return elements.valueCollection().stream();
  }

  @Override
  public Stream<JDA> parallelStream() {
    return elements.valueCollection().parallelStream();
  }

  @Override
  public Iterator<JDA> iterator() {
    return asList().iterator();
  }

  @Override
  public JDA getElementById(int id) {
    return this.elements.get(id);
  }

  public static class UnifiedShardCacheViewImpl implements ShardCacheView {
    protected final Supplier<Stream<ShardCacheView>> generator;

    public UnifiedShardCacheViewImpl(Supplier<Stream<ShardCacheView>> generator) {
      this.generator = generator;
    }

    @Override
    public long size() {
      return generator.get().distinct().mapToLong(CacheView::size).sum();
    }

    @Override
    public boolean isEmpty() {
      return generator.get().allMatch(CacheView::isEmpty);
    }

    @Override
    public List<JDA> asList() {
      List<JDA> list = new ArrayList<>();
      stream().forEach(list::add);
      return Collections.unmodifiableList(list);
    }

    @Override
    public Set<JDA> asSet() {
      Set<JDA> set = new HashSet<>();
      generator.get().forEach(view -> view.forEach(set::add));
      return Collections.unmodifiableSet(set);
    }

    @Override
    public List<JDA> getElementsByName(String name, boolean ignoreCase) {
      return Collections.unmodifiableList(
          generator
              .get()
              .distinct()
              .flatMap(view -> view.getElementsByName(name, ignoreCase).stream())
              .collect(Collectors.toList()));
    }

    @Override
    public JDA getElementById(int id) {
      return generator
          .get()
          .map(view -> view.getElementById(id))
          .filter(Objects::nonNull)
          .findFirst()
          .orElse(null);
    }

    @Override
    public Stream<JDA> stream() {
      return generator.get().flatMap(CacheView::stream).distinct();
    }

    @Override
    public Stream<JDA> parallelStream() {
      return generator.get().flatMap(CacheView::parallelStream).distinct();
    }

    @Override
    public Iterator<JDA> iterator() {
      return asList().iterator();
    }
  }
}
