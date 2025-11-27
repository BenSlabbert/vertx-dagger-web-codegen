/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.annotation.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Creates a new class `{ORIGINAL_CLASS_NAME}Json` with toJson, fromJson and schema methods for
/// validation.
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface JsonWriter {}
