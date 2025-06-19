/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator;

import static java.util.function.Predicate.not;

import com.google.common.base.Strings;
import com.google.errorprone.annotations.MustBeClosed;
import github.benslabbert.vdw.codegen.annotation.Table;
import github.benslabbert.vdw.codegen.commons.jdbc.Reference;
import github.benslabbert.vertxdaggercommons.transaction.blocking.jdbc.JdbcQueryRunner;
import github.benslabbert.vertxdaggercommons.transaction.blocking.jdbc.JdbcQueryRunnerFactory;
import github.benslabbert.vertxdaggercommons.transaction.blocking.jdbc.JdbcUtils;
import github.benslabbert.vertxdaggercommons.transaction.blocking.jdbc.JdbcUtilsFactory;
import jakarta.annotation.Generated;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.apache.commons.dbutils.StatementConfiguration;

public class TableGenerator extends ProcessorBase {

  private static final String LIST_CANONICAL_NAME = "java.util.List";
  private static final String STREAM_CANONICAL_NAME = "java.util.stream.Stream";
  private static final String ITERABLE_CANONICAL_NAME = "java.lang.Iterable";
  private static final String CONSUMER_CANONICAL_NAME = "java.util.function.Consumer";

  public TableGenerator() {
    super(Set.of(Table.class.getCanonicalName()));
  }

  @Override
  List<GeneratedFile> generateTempFile(Element e) {
    if (ElementKind.RECORD != e.getKind()) {
      throw new GenerationException("Table annotation must annotate a record");
    }

    String canonicalName = e.asType().toString();
    String classPackage = canonicalName.substring(0, canonicalName.lastIndexOf('.'));
    String annotatedClassName = e.getSimpleName().toString();
    AnnotatedClass annotatedClass =
        new AnnotatedClass(canonicalName, classPackage, annotatedClassName);

    Table table = e.getAnnotation(Table.class);
    List<TableQuery> tableQueries = getTableQueries(e);
    List<TableDetails> tableDetails = getTableDetails(e);

    if (1 != tableDetails.stream().filter(TableDetails::id).count()) {
      throw new GenerationException("Table must have exactly one id column");
    }
    if (1 != tableDetails.stream().filter(TableDetails::version).count()) {
      throw new GenerationException("Table must have exactly one version column");
    }

    String interfaceName = annotatedClass.name() + "Repository";
    String className = annotatedClass.name() + "RepositoryImpl";
    GeneratedFile generatedInterface =
        generateInterface(annotatedClass, tableQueries, tableDetails, interfaceName);

    GeneratedFile generatedRepository =
        generateRepository(
            annotatedClass, table, tableQueries, tableDetails, className, interfaceName);

    return List.of(generatedInterface, generatedRepository);
  }

  private GeneratedFile generateRepository(
      AnnotatedClass ac,
      Table table,
      List<TableQuery> tableQueries,
      List<TableDetails> tableDetails,
      String className,
      String interfaceName) {
    String varName = ac.name().substring(0, 1).toLowerCase();

    TableDetails idColumn =
        tableDetails.stream().filter(TableDetails::id).findFirst().orElseThrow();
    TableDetails versionColumn =
        tableDetails.stream().filter(TableDetails::version).findFirst().orElseThrow();
    List<String> mutableColumns =
        tableDetails.stream()
            .filter(not(TableDetails::id))
            .filter(not(TableDetails::version))
            .filter(not(TableDetails::insertOnly))
            .map(TableDetails::columnName)
            .toList();
    List<String> allColumnNames =
        tableDetails.stream()
            .filter(not(TableDetails::id))
            .filter(not(TableDetails::version))
            .map(TableDetails::columnName)
            .toList();
    List<TableDetails> allColumns =
        tableDetails.stream()
            .filter(not(TableDetails::id))
            .filter(not(TableDetails::version))
            .toList();

    StringWriter stringWriter = StringWriterFactory.create();

    try (PrintWriter out = new PrintWriter(stringWriter)) {
      out.printf("package %s;%n", ac.classPackage());
      out.println();
      out.printf("import %s;%n", Generated.class.getCanonicalName());
      out.printf("import %s;%n", MustBeClosed.class.getCanonicalName());
      out.printf("import %s;%n", Inject.class.getCanonicalName());
      out.printf("import %s;%n", Singleton.class.getCanonicalName());
      out.printf("import %s;%n", JdbcQueryRunner.class.getCanonicalName());
      out.printf("import %s;%n", JdbcQueryRunnerFactory.class.getCanonicalName());
      out.printf("import %s;%n", JdbcUtils.class.getCanonicalName());
      out.printf("import %s;%n", JdbcUtilsFactory.class.getCanonicalName());
      out.printf("import %s;%n", ResultSet.class.getCanonicalName());
      out.printf("import %s;%n", SQLException.class.getCanonicalName());
      out.printf("import %s;%n", Duration.class.getCanonicalName());
      out.printf("import %s;%n", LinkedList.class.getCanonicalName());
      out.printf("import %s;%n", StatementConfiguration.class.getCanonicalName());
      out.printf("import %s;%n", Predicate.class.getCanonicalName());
      out.printf("import %s;%n", Optional.class.getCanonicalName());
      out.printf("import %s;%n", Collection.class.getCanonicalName());
      out.printf("import %s;%n", Stream.class.getCanonicalName());
      out.printf("import %s;%n", List.class.getCanonicalName());
      out.printf("import %s;%n", ac.canonicalName());
      out.printf("import %s;%n", Reference.class.getCanonicalName());
      out.printf("import %s;%n", Consumer.class.getCanonicalName());
      out.printf("import static %s.not;%n", Predicate.class.getCanonicalName());
      out.println();

      out.printf(
          "@Generated(value = \"%s\", date = \"%s\")%n",
          getClass().getCanonicalName(),
          LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
      out.printf("public class %s implements %s {%n", className, interfaceName);
      out.println();

      String setColumns =
          mutableColumns.stream().map(s -> s + " = ?").collect(Collectors.joining(", "));
      String versionSetClause =
          "%s = %s + 1".formatted(versionColumn.columnName(), versionColumn.columnName());
      String setClause =
          setColumns.isEmpty() ? versionSetClause : setColumns + ", " + versionSetClause;

      out.printf(
          """
    private static final String UPDATE_SQL =
      \"""
        update %s
        set %s
        where %s = ? and %s = ?
        returning *
        \""";
""",
          table.value(), setClause, idColumn.columnName(), versionColumn.columnName());
      out.println();

      List<String> insertColumns = new ArrayList<>();
      insertColumns.add(idColumn.columnName());
      insertColumns.addAll(allColumnNames);
      insertColumns.add(versionColumn.columnName());
      String repeat = Strings.repeat(", ?", allColumnNames.size());

      out.printf(
          """
    private static final String INSERT_SQL =
      \"""
        insert into %s (%s)
        values (nextval('%s')%s, 0)
        returning *
        \""";
""",
          table.value(), String.join(", ", insertColumns), idColumn.sequenceName(), repeat);
      out.println();

      out.printf(
          """
    private static final String DELETE_SQL =
      \"""
        DELETE FROM %s WHERE %s = ? and %s = ?
        \""";
""",
          table.value(), idColumn.columnName(), versionColumn.columnName());
      out.println();

      out.printf(
          """
    private final JdbcQueryRunnerFactory jdbcQueryRunnerFactory;
    private final JdbcUtilsFactory jdbcUtilsFactory;
    private final JdbcQueryRunner jdbcQueryRunner;
    private final JdbcUtils jdbcUtils;

    @Inject
    %s(JdbcQueryRunnerFactory jdbcQueryRunnerFactory, JdbcUtilsFactory jdbcUtilsFactory) {
        StatementConfiguration cfg = getConfigBuilder().build();
        this.jdbcQueryRunnerFactory = jdbcQueryRunnerFactory;
        this.jdbcUtilsFactory = jdbcUtilsFactory;
        this.jdbcQueryRunner = jdbcQueryRunnerFactory.create(cfg);
        this.jdbcUtils = jdbcUtilsFactory.create(cfg);
    }
""",
          className);
      out.println();

      for (TableQuery tq : tableQueries) {
        boolean defaultFetchSize = Table.DEFAULT_FETCH_SIZE == tq.fetchSize();
        String methodName = tq.name();
        List<String> paramNames = tq.paramNames();
        String methodArgs =
            tq.paramNames().stream().map(s -> "Object " + s).collect(Collectors.joining(", "));
        String args = String.join(", ", paramNames);

        // replace all :var_names with ?
        String sanitizedSql = tq.sql().replaceAll(":\\w+", "?");

        switch (tq.returnType()) {
          case LIST_CANONICAL_NAME -> {
            if (defaultFetchSize) {
              out.printf(
                  """
    @Override
    public List<%s> %s(%s) {
        String sql = "%s";
        Object[] args = {%s};
        return jdbcQueryRunner.query(sql, this::mapToList, args);
    }
""",
                  ac.name(), methodName, methodArgs, sanitizedSql, args);
              out.println();
            } else {
              out.printf(
                  """
    @Override
    public List<%s> %s(%s) {
        String sql = "%s";
        Object[] args = {%s};
        StatementConfiguration cfg = getConfigBuilder().fetchSize(%d).build();
        return jdbcQueryRunnerFactory.create(cfg).query(sql, this::mapToList, args);
    }
""",
                  ac.name(), methodName, methodArgs, sanitizedSql, args, tq.fetchSize());
              out.println();
            }
          }
          case STREAM_CANONICAL_NAME -> {
            if (defaultFetchSize) {
              out.printf(
                  """
    @Override
    @MustBeClosed
    public Stream<%s> %s(%s) {
        String sql = "%s";
        Object[] args = {%s};
        return jdbcUtils.stream(sql, this::map, args);
    }
""",
                  ac.name(), methodName, methodArgs, sanitizedSql, args);
            } else {
              out.printf(
                  """
    @Override
    @MustBeClosed
    public Stream<%s> %s(%s) {
        String sql = "%s";
        Object[] args = {%s};
        StatementConfiguration cfg = getConfigBuilder().fetchSize(%d).build();
        return jdbcUtilsFactory.create(cfg).stream(sql, this::map, args);
    }
""",
                  ac.name(), methodName, methodArgs, sanitizedSql, args, tq.fetchSize());
            }
            out.println();
          }
          case ITERABLE_CANONICAL_NAME -> {
            if (defaultFetchSize) {
              out.printf(
                  """
    @Override
    public Iterable<%s> %s(%s) {
        String sql = "%s";
        Object[] args = {%s};
        return jdbcQueryRunner.query(sql, this::mapToList, args);
    }
""",
                  ac.name(), methodName, methodArgs, sanitizedSql, args);
              out.println();
            } else {
              out.printf(
                  """
    @Override
    public Iterable<%s> %s(%s) {
        String sql = "%s";
        Object[] args = {%s};
        StatementConfiguration cfg = getConfigBuilder().fetchSize(%d).build();
        return jdbcQueryRunnerFactory.create(cfg).query(sql, this::mapToList, args);
    }
""",
                  ac.name(), methodName, methodArgs, sanitizedSql, args, tq.fetchSize());
              out.println();
            }
          }
          case CONSUMER_CANONICAL_NAME -> {
            String p;
            if (methodArgs.isBlank()) {
              p = "Consumer<%s> consumer".formatted(ac.name);
            } else {
              p = String.join(", ", methodArgs, "Consumer<%s> consumer".formatted(ac.name));
            }
            if (defaultFetchSize) {
              out.printf(
                  """
    public void %s(%s) {
        String sql = "%s";
        Object[] args = {%s};
        jdbcQueryRunner.query(
            sql,
            rs -> {
            %s %s = map(rs);
            consumer.accept(%s);
            return null;
            },
            args);
    }
""",
                  methodName, p, sanitizedSql, methodArgs, ac.name(), varName, varName);
              out.println();
            } else {
              out.printf(
                  """
    public void %s(%s) {
        String sql = "%s";
        Object[] args = {%s};
        StatementConfiguration cfg = getConfigBuilder().fetchSize(%d).build();
        jdbcQueryRunnerFactory.create(cfg).query(
            sql,
            rs -> {
            %s %s = map(rs);
            consumer.accept(%s);
            return null;
            },
            args);
    }
""",
                  methodName,
                  p,
                  sanitizedSql,
                  methodArgs,
                  tq.fetchSize(),
                  ac.name(),
                  varName,
                  varName);
              out.println();
            }
          }
          default -> unsupportedReturnType(tq.returnType());
        }
      }

      for (TableDetails td : tableDetails) {
        TableDetails.FindByColumn fbc = td.findByColumn();
        if (null == fbc) {
          continue;
        }
        boolean defaultFetchSize = Table.DEFAULT_FETCH_SIZE == fbc.fetchSize();

        switch (fbc.returnType()) {
          case LIST_CANONICAL_NAME -> {
            if (defaultFetchSize) {
              out.printf(
                  """
    @Override
    public List<%s> %s(Object %s) {
        String sql = "SELECT * FROM %s WHERE %s = ?";
        Object[] args = {%s};
        return jdbcQueryRunner.query(sql, this::mapToList, args);
    }
""",
                  ac.name(),
                  td.columnName(),
                  td.columnName(),
                  table.value(),
                  td.columnName(),
                  td.columnName());
            } else {
              out.printf(
                  """
    @Override
    public List<%s> %s(Object %s) {
        String sql = "SELECT * FROM %s WHERE %s = ?";
        Object[] args = {%s};
        StatementConfiguration cfg = getConfigBuilder().fetchSize(%d).build();
        return jdbcQueryRunnerFactory.create(cfg).query(sql, this::mapToList, args);
    }
""",
                  ac.name(),
                  td.columnName(),
                  td.columnName(),
                  table.value(),
                  td.columnName(),
                  td.columnName(),
                  fbc.fetchSize());
            }
          }
          case STREAM_CANONICAL_NAME -> {
            if (defaultFetchSize) {
              out.printf(
                  """
    @Override
    @MustBeClosed
    public Stream<%s> %s(Object %s) {
        String sql = "SELECT * FROM %s WHERE %s = ?";
        Object[] args = {%s};
        return jdbcUtils.stream(sql, this::map, args);
    }
""",
                  ac.name(),
                  td.columnName(),
                  td.columnName(),
                  table.value(),
                  td.columnName(),
                  td.columnName());
            } else {
              out.printf(
                  """
    @Override
    @MustBeClosed
    public Stream<%s> %s(Object %s) {
        String sql = "SELECT * FROM %s WHERE %s = ?";
        Object[] args = {%s};
        StatementConfiguration cfg = getConfigBuilder().fetchSize(%d).build();
        return jdbcUtilsFactory.create(cfg).stream(sql, this::map, args);
    }
""",
                  ac.name(),
                  td.columnName(),
                  td.columnName(),
                  table.value(),
                  td.columnName(),
                  td.columnName(),
                  fbc.fetchSize());
            }
            out.println();
          }
          case ITERABLE_CANONICAL_NAME -> {
            if (defaultFetchSize) {
              out.printf(
                  """
    @Override
    public Iterable<%s> %s(Object %s) {
        String sql = "SELECT * FROM %s WHERE %s = ?";
        Object[] args = {%s};
        return jdbcQueryRunner.query(sql, this::mapToList, args);
    }
""",
                  ac.name(),
                  td.columnName(),
                  td.columnName(),
                  table.value(),
                  td.columnName(),
                  td.columnName());
            } else {
              out.printf(
                  """
    @Override
    public Iterable<%s> %s(Object %s) {
        String sql = "SELECT * FROM %s WHERE %s = ?";
        Object[] args = {%s};
        StatementConfiguration cfg = getConfigBuilder().fetchSize(%d).build();
        return jdbcQueryRunnerFactory.create(cfg).query(sql, this::mapToList, args);
    }
""",
                  ac.name(),
                  td.columnName(),
                  td.columnName(),
                  table.value(),
                  td.columnName(),
                  td.columnName(),
                  fbc.fetchSize());
            }
          }
          case CONSUMER_CANONICAL_NAME -> {
            if (defaultFetchSize) {
              out.printf(
                  """
    public void %s(Object %s, Consumer<%s> consumer) {
        String sql = "SELECT * FROM %s WHERE %s = ?";
        Object[] args = {%s};
        jdbcQueryRunner.query(
            sql,
            rs -> {
            %s %s = map(rs);
            consumer.accept(%s);
            return null;
            },
            args);
    }
""",
                  td.columnName(),
                  td.columnName(),
                  ac.name(),
                  table.value(),
                  td.columnName(),
                  td.columnName(),
                  ac.name(),
                  varName,
                  varName);
              out.println();
            } else {
              out.printf(
                  """
    public void %s(Object %s, Consumer<%s> consumer) {
        String sql = "SELECT * FROM %s WHERE %s = ?";
        Object[] args = {%s};
        StatementConfiguration cfg = getConfigBuilder().fetchSize(%d).build();
        jdbcQueryRunnerFactory.create(cfg).query(
            sql,
            rs -> {
            %s %s = map(rs);
            consumer.accept(%s);
            return null;
            },
            args);
    }
""",
                  td.columnName(),
                  td.columnName(),
                  ac.name(),
                  table.value(),
                  td.columnName(),
                  td.columnName(),
                  fbc.fetchSize(),
                  ac.name(),
                  varName,
                  varName);
              out.println();
            }
          }
          default -> unsupportedReturnType(fbc.returnType());
        }
      }

      for (TableDetails td : tableDetails) {
        if (!td.isFindOneByColumn()) {
          continue;
        }

        out.printf(
            """
    @Override
    public Optional<%s> %s(Object %s) {
        String sql = "SELECT * FROM %s WHERE %s = ? limit 1";
        Object[] args = {%s};
        return jdbcQueryRunner.query(sql, this::mapOptional, args);
    }
""",
            ac.name(), td.columnName(), varName, table.value(), td.columnName(), varName);
      }

      // common queries
      out.printf(
          """
    @Override
    @MustBeClosed
    public Stream<%s> all() {
        String sql = "SELECT * FROM %s order by %s";
        return jdbcUtils.stream(sql, this::map);
    }
""",
          ac.name(), table.value(), idColumn.columnName());
      out.println();

      out.printf(
          """
    @Override
    public Optional<%s> id(long id) {
        String sql = "SELECT * FROM %s WHERE %s = ? limit 1";
        Object[] args = {id};
        return jdbcQueryRunner.query(sql, this::mapOptional, args);
    }
""",
          ac.name(), table.value(), idColumn.columnName());
      out.println();

      out.printf(
          """
    @Override
    public Optional<%s> idAndVersion(long id, int version) {
        String sql = "SELECT * FROM %s WHERE %s = ? and %s = ? limit 1";
        Object[] args = {id, version};
        return jdbcQueryRunner.query(sql, this::mapOptional, args);
    }
""",
          ac.name(), table.value(), idColumn.columnName(), versionColumn.columnName());
      out.println();

      String insertArgs =
          allColumns.stream()
              .map(
                  s ->
                      s.isReference()
                          ? varName + "." + s.fieldName() + "().id()"
                          : varName + "." + s.fieldName() + "()")
              .collect(Collectors.joining(", "));
      String updateArgs =
          Stream.concat(allColumns.stream(), Stream.of(idColumn, versionColumn))
              .filter(not(TableDetails::insertOnly))
              .map(
                  s ->
                      s.isReference()
                          ? varName + "." + s.fieldName() + "().id()"
                          : varName + "." + s.fieldName() + "()")
              .collect(Collectors.joining(", "));

      out.printf(
          """
    @Override
    public %s save(%s %s) {
        if (%s.isNew()) {
          Object[] args = {%s};
          return jdbcQueryRunner.insert(INSERT_SQL, this::mapSingle, args);
        }

        Object[] args = {%s};
        return jdbcQueryRunner.execute(UPDATE_SQL, this::mapSingle, args).getFirst();
    }
""",
          ac.name(), ac.name(), varName, varName, insertArgs, updateArgs);
      out.println();

      out.printf(
          """
    @Override
    public Collection<%s> insertAll(Collection<%s> all) {
        if (all.isEmpty()) {
          throw new IllegalArgumentException("cannot save empty collection");
        }
        if (all.stream().anyMatch(not(%s::isNew))) {
          throw new IllegalArgumentException("cannot insert existing objects");
        }

        Object[][] args = new Object[all.size()][%d];
        int i = 0;
        for (%s %s : all) {
          args[i] = new Object[] {%s};
          i++;
        }

        return jdbcQueryRunner.insertBatch(INSERT_SQL, this::mapToList, args);
    }
""",
          ac.name(), ac.name(), ac.name(), allColumns.size(), ac.name(), varName, insertArgs);
      out.println();

      long updateParamsCount =
          Stream.concat(allColumns.stream(), Stream.of(idColumn, versionColumn))
              .filter(not(TableDetails::insertOnly))
              .count();

      out.printf(
          """
    @Override
    public Collection<%s> updateAll(Collection<%s> all) {
        if (all.isEmpty()) {
          throw new IllegalArgumentException("cannot update empty collection");
        }
        if (all.stream().anyMatch(%s::isNew)) {
          throw new IllegalArgumentException("cannot update new existing objects");
        }

        Object[][] args = new Object[all.size()][%d];
        int i = 0;
        for (%s %s : all) {
          args[i] = new Object[] {%s};
          i++;
        }

        return jdbcQueryRunner.insertBatch(UPDATE_SQL, this::mapToList, args);
    }
""",
          ac.name(), ac.name(), ac.name(), updateParamsCount, ac.name(), varName, updateArgs);
      out.println();

      out.printf(
          """
  @Override
  public int delete(%s %s) {
    if (%s.isNew()) {
      throw new IllegalArgumentException("cannot delete new object");
    }

    Object[] args = {%s.%s(), %s.%s()};
    return jdbcQueryRunner.update(DELETE_SQL, args);
  }
""",
          ac.name(),
          varName,
          varName,
          varName,
          idColumn.fieldName(),
          varName,
          versionColumn.fieldName());
      out.println();

      out.printf(
          """
    @Override
    public int[] deleteAll(Collection<%s> all) {
        if (all.isEmpty()) {
          throw new IllegalArgumentException("cannot delete empty collection");
        }
        if (all.stream().anyMatch(%s::isNew)) {
          throw new IllegalArgumentException("cannot delete new and existing objects");
        }

        Object[][] args = new Object[all.size()][2];
        int i = 0;
        for (%s %s : all) {
          args[i][0] = %s.%s();
          args[i][1] = %s.%s();
          i++;
        }

        return jdbcQueryRunner.batch(DELETE_SQL, args);
    }
""",
          ac.name(),
          ac.name(),
          ac.name(),
          varName,
          varName,
          idColumn.fieldName(),
          varName,
          versionColumn.fieldName());
      out.println();

      out.printf(
          """
    private StatementConfiguration.Builder getConfigBuilder() {
        return new StatementConfiguration.Builder().fetchSize(%d).queryTimeout(Duration.ofSeconds(5));
    }
""",
          Table.DEFAULT_FETCH_SIZE);
      out.println();
      out.printf(
          """
    private %s mapSingle(ResultSet rs) throws SQLException {
        if (!rs.next()) {
          throw new SQLException("expecting at least one result");
        }
        return map(rs);
    }
""",
          ac.name());
      out.println();
      out.printf(
          """
    private Optional<%s> mapOptional(ResultSet rs) throws SQLException {
        if (rs.next()) {
          return Optional.of(map(rs));
        }
        return Optional.empty();
    }
""",
          ac.name());
      out.println();
      out.printf(
          """
    private List<%s> mapToList(ResultSet rs) throws SQLException {
        List<%s> objects = new LinkedList<>();
        while (rs.next()) {
          objects.add(map(rs));
        }
        return objects;
    }
""",
          ac.name(), ac.name());
      out.println();

      out.printf("\tprivate %s map(ResultSet rs) throws SQLException {%n", ac.name());
      out.printf("\t\treturn %s.builder()%n", ac.name());
      for (TableDetails td : tableDetails) {
        if (td.isReference()) {
          out.printf(
              "\t\t.%s(Reference.of(rs.%s(\"%s\")))%n",
              td.fieldName(), getResultSetGetter(td), td.columnName());
        } else {
          out.printf(
              "\t\t.%s(rs.%s(\"%s\"))%n", td.fieldName(), getResultSetGetter(td), td.columnName());
        }
      }
      out.println("\t\t.build();");
      out.println("\t}");

      out.println();

      // end of class
      out.println("}");
    }

    return new GeneratedFile(stringWriter, ac.classPackage() + "." + className);
  }

  private static String getResultSetGetter(TableDetails td) {
    if (td.isReference()) {
      return "getLong";
    }
    TypeMirror type = td.element().asType();
    return switch (type.getKind()) {
      case INT -> "getInt";
      case LONG -> "getLong";
      case BOOLEAN -> "getBoolean";
      case DECLARED ->
          switch (getCanonicalName((DeclaredType) type)) {
            case "java.lang.String" -> "getString";
            case "java.sql.Date" -> "getDate";
            case "java.sql.Timestamp" -> "getTimestamp";
            default ->
                throw new GenerationException(
                    "unexpected type: %s for ResultSet getter".formatted(type));
          };
      default ->
          throw new GenerationException("unexpected type: %s for ResultSet getter".formatted(type));
    };
  }

  private static void unsupportedReturnType(String returnType) {
    throw new GenerationException(
        "unexpected return type: %s only List, Stream, Consumer and Iterable are supported"
            .formatted(returnType));
  }

  private GeneratedFile generateInterface(
      AnnotatedClass ac,
      List<TableQuery> tableQueries,
      List<TableDetails> tableDetails,
      String interfaceName) {
    String varName = ac.name().substring(0, 1).toLowerCase();

    StringWriter stringWriter = StringWriterFactory.create();

    try (PrintWriter out = new PrintWriter(stringWriter)) {
      out.printf("package %s;%n", ac.classPackage());
      out.println();
      out.printf("import %s;%n", Generated.class.getCanonicalName());
      out.printf("import %s;%n", MustBeClosed.class.getCanonicalName());
      out.printf("import %s;%n", Optional.class.getCanonicalName());
      out.printf("import %s;%n", Collection.class.getCanonicalName());
      out.printf("import %s;%n", Stream.class.getCanonicalName());
      out.printf("import %s;%n", List.class.getCanonicalName());
      out.printf("import %s;%n", Consumer.class.getCanonicalName());
      out.printf("import %s;%n", ac.canonicalName());
      out.println();

      out.printf(
          "@Generated(value = \"%s\", date = \"%s\")%n",
          getClass().getCanonicalName(),
          LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
      out.printf("public interface %s {%n", interfaceName);

      for (TableQuery tq : tableQueries) {
        String params =
            tq.paramNames().stream().map(s -> "Object " + s).collect(Collectors.joining(", "));
        switch (tq.returnType()) {
          case LIST_CANONICAL_NAME ->
              out.printf("\tList<%s> %s(%s);%n", ac.name(), tq.name(), params);
          case STREAM_CANONICAL_NAME -> {
            out.println("\t@MustBeClosed");
            out.printf("\tStream<%s> %s(%s);%n", ac.name(), tq.name(), params);
          }
          case ITERABLE_CANONICAL_NAME ->
              out.printf("\tIterable<%s> %s(%s);%n", ac.name(), tq.name(), params);
          case CONSUMER_CANONICAL_NAME -> {
            String p;
            if (params.isBlank()) {
              p = "Consumer<%s> consumer".formatted(ac.name);
            } else {
              p = String.join(", ", params, "Consumer<%s> consumer".formatted(ac.name));
            }
            out.printf("\tvoid %s(%s);%n", tq.name(), p);
          }
          default -> unsupportedReturnType(tq.returnType());
        }
      }

      for (TableDetails td : tableDetails) {
        TableDetails.FindByColumn fbc = td.findByColumn();
        if (null == fbc) {
          continue;
        }

        switch (fbc.returnType()) {
          case LIST_CANONICAL_NAME ->
              out.printf(
                  "\tList<%s> %s(Object %s);%n", ac.name(), td.columnName(), td.columnName());
          case STREAM_CANONICAL_NAME -> {
            out.println("\t@MustBeClosed");
            out.printf(
                "\tStream<%s> %s(Object %s);%n", ac.name(), td.columnName(), td.columnName());
          }
          case ITERABLE_CANONICAL_NAME ->
              out.printf(
                  "\tIterable<%s> %s(Object %s);%n", ac.name(), td.columnName(), td.columnName());
          case CONSUMER_CANONICAL_NAME ->
              out.printf(
                  "\tvoid %s(%s);%n",
                  td.columnName(),
                  String.join(
                      ", ",
                      "Object %s".formatted(td.columnName()),
                      "Consumer<%s> consumer".formatted(ac.name())));
          default -> unsupportedReturnType(fbc.returnType());
        }
      }

      for (TableDetails td : tableDetails) {
        if (!td.isFindOneByColumn()) {
          continue;
        }

        out.printf("\tOptional<%s> %s(Object %s);%n", ac.name(), td.columnName(), td.columnName());
      }

      // common queries
      out.println("\t@MustBeClosed");
      out.printf("\tStream<%s> all();%n", ac.name());
      out.printf("\tOptional<%s> id(long id);%n", ac.name());
      out.printf("\tdefault %s requireId(long id) {%n", ac.name());
      out.println(
          "\t\treturn id(id).orElseThrow(() -> new IllegalArgumentException(\"not found id"
              + " %d\".formatted(id)));");
      out.println("\t}");
      out.printf("\tOptional<%s> idAndVersion(long id, int version);%n", ac.name());
      out.printf("\tdefault %s requireIdAndVersion(long id, int version) {%n", ac.name());
      out.println(
          "\t\treturn idAndVersion(id, version).orElseThrow(() -> new"
              + " IllegalArgumentException(\"not found id %d and version %d\".formatted(id,"
              + " version)));");
      out.println("\t}");
      out.printf("\t%s save(%s %s);%n", ac.name(), ac.name(), varName);
      out.printf("\tCollection<%s> insertAll(Collection<%s> all);%n", ac.name(), ac.name());
      out.printf("\tCollection<%s> updateAll(Collection<%s> all);%n", ac.name(), ac.name());
      out.printf("\tint delete(%s %s);%n", ac.name(), varName);
      out.printf("\tint[] deleteAll(Collection<%s> all);%n", ac.name());
      out.println("}");
    }

    return new GeneratedFile(stringWriter, ac.classPackage() + "." + interfaceName);
  }

  private static List<TableQuery> getTableQueries(Element e) {
    Table.Queries queries = e.getAnnotation(Table.Queries.class);
    if (null == queries) {
      return List.of();
    }
    return Arrays.stream(queries.value())
        .map(
            query -> {
              String sql = query.sql();
              int paramIndex = sql.indexOf(':');
              if (paramIndex == -1) {
                return new TableQuery(
                    sql,
                    query.name(),
                    query.fetchSize(),
                    getReturnType(query::returnType),
                    List.of());
              }

              List<String> paramNames = new ArrayList<>();
              String sqlSlice = sql;
              while (paramIndex != -1) {
                sqlSlice = sqlSlice.substring(paramIndex + 1);
                int end = sqlSlice.indexOf(' ');
                if (-1 == end) {
                  paramNames.add(sqlSlice);
                } else {
                  paramNames.add(sqlSlice.substring(0, end));
                  sqlSlice = sqlSlice.substring(end);
                }
                paramIndex = sqlSlice.indexOf(':');
              }

              return new TableQuery(
                  sql,
                  query.name(),
                  query.fetchSize(),
                  getReturnType(query::returnType),
                  paramNames);
            })
        .toList();
  }

  private static String getCanonicalName(DeclaredType declaredType) {
    TypeElement typeElement = (TypeElement) declaredType.asElement();
    return typeElement.getQualifiedName().toString();
  }

  private static List<TableDetails> getTableDetails(Element e) {
    return e.getEnclosedElements().stream()
        .filter(ee -> ee.getKind() == ElementKind.RECORD_COMPONENT)
        .map(ee -> (RecordComponentElement) ee)
        .map(
            ee -> {
              Table.Id id = ee.getAnnotation(Table.Id.class);
              Table.Column column = ee.getAnnotation(Table.Column.class);
              if (null == column) {
                throw new GenerationException("record component must have a column annotation");
              }
              Table.InsertOnly insertOnly = ee.getAnnotation(Table.InsertOnly.class);
              Table.Version version = ee.getAnnotation(Table.Version.class);
              Table.FindByColumn findByColumn = ee.getAnnotation(Table.FindByColumn.class);
              Table.FindOneByColumn findOneByColumn = ee.getAnnotation(Table.FindOneByColumn.class);

              if (null != findByColumn && null != findOneByColumn) {
                throw new GenerationException("cannot have both findByColumn and findOneByColumn");
              }

              String fieldName = ee.getSimpleName().toString();
              TypeMirror type = ee.asType();

              boolean isReference = false;

              if (TypeKind.DECLARED == type.getKind()) {
                String qualifiedName = getCanonicalName((DeclaredType) type);
                isReference = qualifiedName.equals(Reference.class.getCanonicalName());
              }

              TableDetails.FindByColumn fbc = null;
              if (null != findByColumn && findByColumn.value().isBlank()) {
                fbc =
                    new TableDetails.FindByColumn(
                        findByColumn.fetchSize(), getReturnType(findByColumn::returnType));
              }

              return new TableDetails(
                  ee,
                  column.value(),
                  fieldName,
                  null == id ? null : id.value(),
                  id != null,
                  insertOnly != null,
                  version != null,
                  isReference,
                  null != findOneByColumn,
                  fbc);
            })
        .toList();
  }

  private static String getReturnType(Supplier<Class<?>> supplier) {
    // class in the annotation is a mirrored type
    try {
      var ignore = supplier.get(); // NOSONAR this method invocation thrown
      throw new GenerationException("expected MirroredTypeException");
    } catch (MirroredTypeException e) {
      TypeMirror typeMirror = e.getTypeMirror();
      DeclaredType declaredType = (DeclaredType) typeMirror;
      TypeElement typeElement = (TypeElement) declaredType.asElement();
      return typeElement.getQualifiedName().toString();
    }
  }

  private record TableQuery(
      String sql, String name, int fetchSize, String returnType, List<String> paramNames) {}

  private record TableDetails(
      RecordComponentElement element,
      String columnName,
      String fieldName,
      String sequenceName,
      boolean id,
      boolean insertOnly,
      boolean version,
      boolean isReference,
      boolean isFindOneByColumn,
      FindByColumn findByColumn) {

    private record FindByColumn(int fetchSize, String returnType) {}
  }

  private record AnnotatedClass(String canonicalName, String classPackage, String name) {}
}
