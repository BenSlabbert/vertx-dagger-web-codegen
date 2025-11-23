/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.aop.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CacheKeyBuilder {

  private static final Logger log = LoggerFactory.getLogger(CacheKeyBuilder.class);

  private static final Pattern PLACEHOLDER = Pattern.compile("#(\\d+)");
  private static final ConcurrentHashMap<String, ParsedTemplate> TEMPLATE_CACHE =
      new ConcurrentHashMap<>();

  private CacheKeyBuilder() {}

  /**
   * Build a cache key by replacing placeholders like #0, #1 ... with corresponding values. Parsing
   * of the template is cached to avoid repeated regex processing.
   */
  public static String buildKey(String template, Object... values) {
    log.debug("Building key for template '{}'", template);
    ParsedTemplate pt = TEMPLATE_CACHE.computeIfAbsent(template, CacheKeyBuilder::parseTemplate);
    return pt.build(values);
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

  private record LiteralSegment(String literal) implements Segment {

    @Override
    public void append(StringBuilder sb, Object[] values) {
      sb.append(literal);
    }
  }

  private record IndexSegment(int index) implements Segment {

    @Override
    public void append(StringBuilder sb, Object[] values) {
      if (index >= 0 && index < values.length) {
        sb.append(values[index]);
      } else {
        sb.append('#').append(index);
      }
    }
  }

  private record ParsedTemplate(List<Segment> segments) {

    String build(Object[] values) {
      StringBuilder sb = new StringBuilder();
      for (Segment s : segments) {
        s.append(sb, values);
      }
      return sb.toString();
    }
  }
}
