/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator.json;

import static github.benslabbert.vdw.codegen.generator.json.Util.simpleName;

import jakarta.annotation.Nullable;

record ObjectSchemaGenerator(@Nullable String fieldName, boolean required, String objectClassName)
    implements SchemaGenerator {

  @Override
  public String print() {
    String s = simpleName(objectClassName);
    if (null == fieldName) {
      return "%sJson.schemaBuilder()".formatted(s);
    }

    String schema = required ? ".requiredProperty(" : ".property(";
    return schema + "\"%s\", %sJson.schemaBuilder())".formatted(fieldName, s);
  }
}
