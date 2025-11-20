/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.aop.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CacheKeyBuilder {

  private static final Pattern PLACEHOLDER = Pattern.compile("#(\\d+)");
  private static final ConcurrentHashMap<String, ParsedTemplate> TEMPLATE_CACHE =
      new ConcurrentHashMap<>();

  private CacheKeyBuilder() {}

  /**
   * Build a cache key by replacing placeholders like #0, #1 ... with corresponding values. Parsing
   * of the template is cached to avoid repeated regex processing.
   */
  public static String buildKey(String template, Object... values) {
    ParsedTemplate parsed =
        TEMPLATE_CACHE.computeIfAbsent(template, CacheKeyBuilder::parseTemplate);
    return parsed.build(values);
  }

  private static ParsedTemplate parseTemplate(String template) {
    Matcher m = PLACEHOLDER.matcher(template);
    List<Segment> segments = new ArrayList<>();
    int last = 0;
    while (m.find()) {
      if (m.start() > last) {
        segments.add(new LiteralSegment(template.substring(last, m.start())));
      }
      int idx = Integer.parseInt(m.group(1));
      segments.add(new IndexSegment(idx));
      last = m.end();
    }
    if (last < template.length()) {
      segments.add(new LiteralSegment(template.substring(last)));
    }
    return new ParsedTemplate(segments);
  }

  private sealed interface Segment permits LiteralSegment, IndexSegment {
    void append(StringBuilder sb, Object[] values);
  }

  private static final class LiteralSegment implements Segment {
    private final String literal;

    private LiteralSegment(String literal) {
      this.literal = literal;
    }

    @Override
    public void append(StringBuilder sb, Object[] values) {
      sb.append(literal);
    }
  }

  private static final class IndexSegment implements Segment {
    private final int index;

    private IndexSegment(int index) {
      this.index = index;
    }

    @Override
    public void append(StringBuilder sb, Object[] values) {
      if (index >= 0 && index < values.length) {
        sb.append(values[index]);
      } else {
        sb.append('#').append(index);
      }
    }
  }

  private static final class ParsedTemplate {
    private final List<Segment> segments;

    private ParsedTemplate(List<Segment> segments) {
      this.segments = segments;
    }

    String build(Object[] values) {
      StringBuilder sb = new StringBuilder();
      for (Segment s : segments) {
        s.append(sb, values);
      }
      return sb.toString();
    }
  }
}
